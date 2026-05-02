package co.edu.unbosque.mundialhubbackend.service;

import co.edu.unbosque.mundialhubbackend.dto.FixtureDTO;
import co.edu.unbosque.mundialhubbackend.dto.FixtureEventDTO;
import co.edu.unbosque.mundialhubbackend.model.Fixture;
import co.edu.unbosque.mundialhubbackend.model.FixtureEvent;
import co.edu.unbosque.mundialhubbackend.model.User;
import co.edu.unbosque.mundialhubbackend.repository.FixtureEventRepository;
import co.edu.unbosque.mundialhubbackend.repository.FixtureRepository;
import co.edu.unbosque.mundialhubbackend.repository.UserRepository;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
public class FixtureService {

	// ── TTLs según RNF-08 ────────────────────────────────────────────────────
	private static final long LIVE_TTL_SECONDS = 30;
	private static final long FIXTURE_TTL_SECONDS = 24 * 60 * 60;

	// Estados que se consideran "en vivo"
	private static final Set<String> LIVE_STATUSES = Set.of("1H", "HT", "2H", "ET", "BT", "P", "LIVE");

	@Autowired
	private FixtureRepository fixtureRepository;
	@Autowired
	private FixtureEventRepository fixtureEventRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private ApiFootballService apiFootballService;
	@Autowired
	private ModelMapper modelMapper;

	// ─── Calendario completo (HU-04) ─────────────────────────────────────────

	/**
	 * Devuelve todos los partidos del Mundial 2026. Si la caché está vigente sirve
	 * desde BD; si no, sincroniza con la API. Retorna 0 si todo OK, 1 si hubo un
	 * error de sincronización.
	 */
	public List<FixtureDTO> getAllFixtures() {
		List<Fixture> cached = fixtureRepository.findByLeagueIdAndSeasonOrderByMatchDateAsc(
				(long) ApiFootballService.WORLD_CUP_LEAGUE_ID, ApiFootballService.WORLD_CUP_SEASON);

		// Si no hay nada en caché, sincronizar
		if (cached.isEmpty()) {
			int sync = syncAllFixtures();
			if (sync == 1) {
				// La API falló: devolver lista vacía con flag para el controller
				return new ArrayList<>();
			}
			cached = fixtureRepository.findByLeagueIdAndSeasonOrderByMatchDateAsc(
					(long) ApiFootballService.WORLD_CUP_LEAGUE_ID, ApiFootballService.WORLD_CUP_SEASON);
		}

		return cached.stream().map(f -> toDTO(f, false)).collect(Collectors.toList());
	}

	/**
	 * Sincroniza todos los fixtures del Mundial con la API-Football. Retorna 0 =
	 * OK, 1 = error de API.
	 */
	public int syncAllFixtures() {
		List<JsonObject> raw = apiFootballService.fetchAllFixtures();
		if (raw.isEmpty())
			return 1;
		raw.forEach(this::upsertFixture);
		return 0;
	}

	// ─── Partidos en vivo (HU-05) ────────────────────────────────────────────

	/**
	 * Devuelve los partidos actualmente en vivo. Respeta el TTL de 30 s: si la
	 * caché está fresca no llama la API.
	 */
	public List<FixtureDTO> getLiveFixtures() {
		LocalDateTime threshold = LocalDateTime.now(ZoneOffset.UTC).minusSeconds(LIVE_TTL_SECONDS);

		// Fixtures en vivo con caché vencida → refrescar
		List<Fixture> stale = fixtureRepository.findLiveFixturesWithExpiredCache(threshold);
		if (!stale.isEmpty()) {
			List<JsonObject> raw = apiFootballService.fetchLiveFixtures();
			raw.forEach(this::upsertFixtureWithEvents);
		}

		return fixtureRepository.findLiveFixtures().stream().map(f -> toDTO(f, false)).collect(Collectors.toList());
	}

	// ─── Detalle de un partido ────────────────────────────────────────────────

	/**
	 * Devuelve el detalle completo de un partido, incluyendo eventos. Si el partido
	 * está en vivo respeta el TTL de 30 s. Si el proveedor falla devuelve el último
	 * dato en caché con pendingUpdate=true (RNF-04).
	 */
	public FixtureDTO getFixtureById(Long id) {
		Fixture cached = fixtureRepository.findById(id).orElse(null);

		boolean needsRefresh = cached == null || isCacheExpired(cached);

		if (needsRefresh) {
			JsonObject raw = apiFootballService.fetchFixtureById(id);
			if (raw != null) {
				cached = upsertFixtureWithEvents(raw);
			} else if (cached != null) {
				// API falló pero tenemos datos en caché: marcar como pendiente
				return toDTO(cached, true);
			} else {
				return null; // No hay datos en ningún lado
			}
		}

		return toDTO(cached, false);
	}

	// ─── Agenda personal (HU-06) ─────────────────────────────────────────────

	/**
	 * Devuelve los próximos partidos de los equipos favoritos del usuario. Los
	 * equipos favoritos se obtienen desde su perfil en BD.
	 */
	public List<FixtureDTO> getAgendaForUser(String username) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null || user.getFavoriteTeams() == null || user.getFavoriteTeams().isEmpty()) {
			return new ArrayList<>();
		}

		List<Long> teamIds = user.getFavoriteTeams().stream().map(t -> t.getId()).collect(Collectors.toList());

		List<Fixture> fixtures = fixtureRepository.findAgendaForTeams(teamIds, LocalDateTime.now(ZoneOffset.UTC));

		return fixtures.stream().map(f -> toDTO(f, false)).collect(Collectors.toList());
	}

	// ─── Partidos por fecha (HU-04 filtro) ───────────────────────────────────

	public List<FixtureDTO> getFixturesByDate(String date) {
		// date formato: YYYY-MM-DD
		LocalDateTime from = LocalDateTime.parse(date + "T00:00:00");
		LocalDateTime to = LocalDateTime.parse(date + "T23:59:59");

		List<Fixture> cached = fixtureRepository.findByMatchDateBetweenOrderByMatchDateAsc(from, to);

		if (cached.isEmpty()) {
			List<JsonObject> raw = apiFootballService.fetchFixturesByDate(date);
			raw.forEach(this::upsertFixture);
			cached = fixtureRepository.findByMatchDateBetweenOrderByMatchDateAsc(from, to);
		}

		return cached.stream().map(f -> toDTO(f, false)).collect(Collectors.toList());
	}

	// ─── Helpers de caché ────────────────────────────────────────────────────

	private boolean isCacheExpired(Fixture f) {
		if (f.getCachedAt() == null)
			return true;
		long ttl = LIVE_STATUSES.contains(f.getStatus()) ? LIVE_TTL_SECONDS : FIXTURE_TTL_SECONDS;
		return f.getCachedAt().plusSeconds(ttl).isBefore(LocalDateTime.now(ZoneOffset.UTC));
	}

	// ─── Mapeo desde JSON de API-Football → Entidad ───────────────────────────

	/**
	 * Inserta o actualiza un Fixture en BD a partir del JSON de la API. No
	 * sincroniza eventos (más rápido, para listas grandes).
	 */
	private void upsertFixture(JsonObject raw) {
		JsonObject fixtureJson = raw.getAsJsonObject("fixture");
		JsonObject leagueJson = raw.getAsJsonObject("league");
		JsonObject teamsJson = raw.getAsJsonObject("teams");
		JsonObject goalsJson = raw.getAsJsonObject("goals");
		JsonObject statusJson = fixtureJson.getAsJsonObject("status");
		JsonObject venueJson = fixtureJson.getAsJsonObject("venue");

		Long id = fixtureJson.get("id").getAsLong();

		Fixture f = fixtureRepository.findById(id).orElse(new Fixture());
		f.setId(id);

		// Liga
		f.setLeagueId(leagueJson.get("id").getAsLong());
		f.setLeagueName(leagueJson.get("name").getAsString());
		f.setLeagueLogo(nullableString(leagueJson, "logo"));
		f.setLeagueRound(nullableString(leagueJson, "round"));
		f.setSeason(leagueJson.get("season").getAsInt());

		// Equipos
		JsonObject home = teamsJson.getAsJsonObject("home");
		JsonObject away = teamsJson.getAsJsonObject("away");
		f.setHomeTeamId(home.get("id").getAsLong());
		f.setHomeTeamName(home.get("name").getAsString());
		f.setHomeTeamLogo(nullableString(home, "logo"));
		f.setAwayTeamId(away.get("id").getAsLong());
		f.setAwayTeamName(away.get("name").getAsString());
		f.setAwayTeamLogo(nullableString(away, "logo"));

		// Resultado
		f.setHomeGoals(nullableInt(goalsJson, "home"));
		f.setAwayGoals(nullableInt(goalsJson, "away"));

		// Fecha (ISO 8601 → LocalDateTime UTC)
		String rawDate = fixtureJson.get("date").getAsString();
		f.setMatchDate(LocalDateTime.parse(rawDate, DateTimeFormatter.ISO_OFFSET_DATE_TIME).toInstant(ZoneOffset.UTC)
				.atZone(ZoneOffset.UTC).toLocalDateTime());

		// Estadio
		if (venueJson != null && !venueJson.isJsonNull()) {
			f.setVenue(nullableString(venueJson, "name"));
			f.setCity(nullableString(venueJson, "city"));
		}
		f.setReferee(nullableString(fixtureJson, "referee"));

		// Estado
		f.setStatus(statusJson.get("short").getAsString());
		f.setElapsed(nullableInt(statusJson, "elapsed"));

		// Marca de caché
		f.setCachedAt(LocalDateTime.now(ZoneOffset.UTC));

		fixtureRepository.save(f);
	}

	/**
	 * Igual que upsertFixture pero también sincroniza los eventos del partido. Se
	 * usa para vista en vivo y detalle de partido.
	 */
	private Fixture upsertFixtureWithEvents(JsonObject raw) {
		upsertFixture(raw);

		JsonObject fixtureJson = raw.getAsJsonObject("fixture");
		Long id = fixtureJson.get("id").getAsLong();

		Fixture f = fixtureRepository.findById(id).orElse(null);
		if (f == null)
			return null;

		// Sincronizar eventos si vienen en el JSON
		JsonArray eventsJson = raw.getAsJsonArray("events");
		if (eventsJson != null && !eventsJson.isEmpty()) {
			fixtureEventRepository.deleteByFixtureId(id);
			for (var el : eventsJson) {
				JsonObject ev = el.getAsJsonObject();
				FixtureEvent event = new FixtureEvent();
				event.setFixture(f);

				JsonObject time = ev.getAsJsonObject("time");
				event.setElapsed(nullableInt(time, "elapsed"));
				event.setElapsedExtra(nullableInt(time, "extra"));

				JsonObject team = ev.getAsJsonObject("team");
				event.setTeamId(team.get("id").getAsLong());
				event.setTeamName(team.get("name").getAsString());

				JsonObject player = ev.getAsJsonObject("player");
				event.setPlayerName(nullableString(player, "name"));

				JsonObject assist = ev.getAsJsonObject("assist");
				event.setAssistName(assist != null ? nullableString(assist, "name") : null);

				event.setType(nullableString(ev, "type"));
				event.setDetail(nullableString(ev, "detail"));
				event.setComments(nullableString(ev, "comments"));

				fixtureEventRepository.save(event);
			}
		}
		return f;
	}

	// ─── Conversión Entidad → DTO ─────────────────────────────────────────────

	private FixtureDTO toDTO(Fixture f, boolean pendingUpdate) {
		FixtureDTO dto = modelMapper.map(f, FixtureDTO.class);
		dto.setPendingUpdate(pendingUpdate);

		List<FixtureEvent> events = fixtureEventRepository.findByFixtureIdOrderByElapsedAsc(f.getId());
		dto.setEvents(events.stream().map(e -> modelMapper.map(e, FixtureEventDTO.class)).collect(Collectors.toList()));

		return dto;
	}

	// ─── Utilidades de parsing JSON seguro ────────────────────────────────────

	private String nullableString(JsonObject obj, String key) {
		if (obj == null || !obj.has(key) || obj.get(key).isJsonNull())
			return null;
		return obj.get(key).getAsString();
	}

	private Integer nullableInt(JsonObject obj, String key) {
		if (obj == null || !obj.has(key) || obj.get(key).isJsonNull())
			return null;
		return obj.get(key).getAsInt();
	}
}