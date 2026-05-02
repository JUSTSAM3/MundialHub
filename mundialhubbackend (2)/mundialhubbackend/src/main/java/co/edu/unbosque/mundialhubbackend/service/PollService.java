package co.edu.unbosque.mundialhubbackend.service;

import co.edu.unbosque.mundialhubbackend.dto.PollDTO;
import co.edu.unbosque.mundialhubbackend.dto.PollMemberDTO;
import co.edu.unbosque.mundialhubbackend.dto.PredictionDTO;
import co.edu.unbosque.mundialhubbackend.model.*;
import co.edu.unbosque.mundialhubbackend.model.Poll.PollStatus;
import co.edu.unbosque.mundialhubbackend.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

@Service
@Transactional
public class PollService {

	// Caracteres para el código de invitación — sin ambiguos (0, O, I, l)
	private static final String CODE_CHARS = "ABCDEFGHJKLMNPQRSTUVWXYZ23456789";
	private static final int CODE_LENGTH = 8;

	@Autowired
	private PollRepository pollRepository;
	@Autowired
	private PollMemberRepository pollMemberRepository;
	@Autowired
	private PredictionRepository predictionRepository;
	@Autowired
	private UserRepository userRepository;
	@Autowired
	private FixtureRepository fixtureRepository;
	// Inyectar estos dos adicionales en PollService
	@Autowired
	private CommunityRepository communityRepository;
	@Autowired
	private CommunityService communityService;
	// ─── HU-16 Crear polla ───────────────────────────────────────────────────

	/**
	 * Crea una nueva polla futbolera y agrega al creador como primer miembro.
	 * Retorna: 0 = OK | 1 = usuario no encontrado
	 */
	// ─── HU-16 Crear polla (actualizado) ────────────────────────────────────────
	/**
	 * La polla siempre se crea dentro de una comunidad. Solo los miembros de esa
	 * comunidad podrán unirse.
	 *
	 * Retorna: 0 = OK | 1 = usuario no encontrado | 2 = comunidad no encontrada 3 =
	 * el usuario no es miembro de la comunidad
	 */
	public int createPoll(String username, String pollName, Long communityId) {
		User creator = userRepository.findByUsername(username).orElse(null);
		if (creator == null)
			return 1;

		Community community = communityRepository.findById(communityId).orElse(null);
		if (community == null)
			return 2;

		if (!communityService.isMember(communityId, username))
			return 3;

		Poll poll = new Poll();
		poll.setName(pollName);
		poll.setCommunity(community);
		poll.setCreator(creator);
		poll.setStatus(Poll.PollStatus.ACTIVE);
		poll.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
		pollRepository.save(poll);

		PollMember member = new PollMember();
		member.setPoll(poll);
		member.setUser(creator);
		member.setTotalPoints(0);
		member.setRankingPosition(1);
		member.setJoinedAt(LocalDateTime.now(ZoneOffset.UTC));
		pollMemberRepository.save(member);

		return 0;
	}

	// ─── HU-17 Unirse a una polla (actualizado) ──────────────────────────────────
	/**
	 * Para unirse a una polla el usuario ya debe ser miembro de la comunidad. El
	 * inviteCode identifica la polla dentro de esa comunidad.
	 *
	 * Retorna: 0 = OK | 1 = usuario no encontrado | 2 = polla no encontrada 3 =
	 * polla cerrada | 4 = ya es miembro | 5 = no es miembro de la comunidad
	 */
	public int joinPoll(String username, String inviteCode) {
		User user = userRepository.findByUsername(username).orElse(null);
		if (user == null)
			return 1;

		Poll poll = pollRepository.findByInviteCode(inviteCode.toUpperCase()).orElse(null);
		if (poll == null)
			return 2;

		if (poll.getStatus() == Poll.PollStatus.FINISHED)
			return 3;

		if (pollMemberRepository.existsByPollIdAndUserUsername(poll.getId(), username))
			return 4;

		// Verificar membresía en la comunidad de la polla
		if (!communityService.isMember(poll.getCommunity().getId(), username))
			return 5;

		PollMember member = new PollMember();
		member.setPoll(poll);
		member.setUser(user);
		member.setTotalPoints(0);
		member.setJoinedAt(LocalDateTime.now(ZoneOffset.UTC));
		pollMemberRepository.save(member);

		recalculateRanking(poll.getId());
		return 0;
	}

	// ─── HU-18 Guardar pronóstico ────────────────────────────────────────────

	/**
	 * Registra o actualiza el pronóstico del usuario para un partido dentro de su
	 * polla. Si el partido ya inició, el pronóstico está cerrado y no puede
	 * modificarse. Retorna: 0 = OK | 1 = miembro no encontrado | 2 = partido no
	 * encontrado 3 = pronóstico cerrado (partido ya inició) | 4 = polla finalizada
	 */
	public int savePrediction(String username, Long pollId, Long fixtureId, int homeGoals, int awayGoals) {

		PollMember member = pollMemberRepository.findByPollIdAndUserUsername(pollId, username).orElse(null);
		if (member == null)
			return 1;

		if (member.getPoll().getStatus() == PollStatus.FINISHED)
			return 4;

		Fixture fixture = fixtureRepository.findById(fixtureId).orElse(null);
		if (fixture == null)
			return 2;

		// El partido ya comenzó → no se puede apostar
		if (isMatchStarted(fixture.getStatus()))
			return 3;

		Prediction prediction = predictionRepository.findByPollMemberIdAndFixtureId(member.getId(), fixtureId)
				.orElse(new Prediction());

		prediction.setPollMember(member);
		prediction.setFixture(fixture);
		prediction.setPredictedHomeGoals(homeGoals);
		prediction.setPredictedAwayGoals(awayGoals);
		prediction.setClosed(false);
		prediction.setUpdatedAt(LocalDateTime.now(ZoneOffset.UTC));

		if (prediction.getCreatedAt() == null) {
			prediction.setCreatedAt(LocalDateTime.now(ZoneOffset.UTC));
		}

		predictionRepository.save(prediction);
		return 0;
	}

	// ─── HU-19 Ranking de la polla ───────────────────────────────────────────

	/**
	 * Devuelve el ranking actualizado de una polla. Retorna lista vacía si la polla
	 * no existe.
	 */
	public List<PollMemberDTO> getRanking(Long pollId) {
		List<PollMember> members = pollMemberRepository.findByPollIdOrderByTotalPointsDescJoinedAtAsc(pollId);
		return members.stream().map(this::toPollMemberDTO).collect(Collectors.toList());
	}

	// ─── Consulta de pollas del usuario ──────────────────────────────────────

	public List<PollDTO> getPollsForUser(String username) {
		return pollRepository.findPollsByParticipant(username).stream().map(this::toPollDTO)
				.collect(Collectors.toList());
	}

	public PollDTO getPollById(Long pollId) {
		Poll poll = pollRepository.findById(pollId).orElse(null);
		if (poll == null)
			return null;
		return toPollDTOWithMembers(poll);
	}

	// ─── Pronósticos de un partido en una polla ───────────────────────────────

	public List<PredictionDTO> getPredictionsForFixture(Long pollId, Long fixtureId) {
		return predictionRepository.findByPollAndFixture(pollId, fixtureId).stream().map(this::toPredictionDTO)
				.collect(Collectors.toList());
	}

	public List<PredictionDTO> getMyPredictions(String username, Long pollId) {
		PollMember member = pollMemberRepository.findByPollIdAndUserUsername(pollId, username).orElse(null);
		if (member == null)
			return new ArrayList<>();
		return predictionRepository.findByPollMemberIdOrderByFixtureMatchDateAsc(member.getId()).stream()
				.map(this::toPredictionDTO).collect(Collectors.toList());
	}

	// ─── Cálculo de puntos al terminar un partido ─────────────────────────────

	/**
	 * Calcula los puntos de todos los pronósticos del partido que acaba de
	 * terminar. Se llama desde FixtureService cuando detecta que un fixture cambió
	 * a FT/AET/PEN.
	 *
	 * Reglas de puntuación: 3 pts → marcador exacto 1 pt → ganador o empate
	 * correcto, pero marcador incorrecto 0 pts → fallo total
	 */
	public void calculateScoresForFixture(Long fixtureId) {
		Fixture fixture = fixtureRepository.findById(fixtureId).orElse(null);
		if (fixture == null || fixture.getHomeGoals() == null || fixture.getAwayGoals() == null)
			return;

		List<Prediction> pending = predictionRepository.findUnscoredByFixture(fixtureId);

		for (Prediction p : pending) {
			int pts = score(p.getPredictedHomeGoals(), p.getPredictedAwayGoals(), fixture.getHomeGoals(),
					fixture.getAwayGoals());
			p.setPointsEarned(pts);
			predictionRepository.save(p);

			// Sumar al total del miembro
			PollMember member = p.getPollMember();
			member.setTotalPoints(member.getTotalPoints() + pts);
			pollMemberRepository.save(member);
		}

		// Recalcular ranking de todas las pollas que tengan predicciones de este
		// fixture
		pending.stream().map(p -> p.getPollMember().getPoll().getId()).distinct().forEach(this::recalculateRanking);
	}

	// ─── HU-20 Premio al ganador ──────────────────────────────────────────────

	/**
	 * Cierra una polla, identifica al ganador y le entrega el premio. Llama a
	 * AlbumService.awardPackage() — implementado en el módulo de álbum. Retorna: 0
	 * = OK | 1 = polla no encontrada | 2 = ya estaba cerrada 3 = sin miembros para
	 * determinar ganador
	 */
	public int finishPoll(Long pollId) {
		Poll poll = pollRepository.findById(pollId).orElse(null);
		if (poll == null)
			return 1;
		if (poll.getStatus() == PollStatus.FINISHED)
			return 2;

		List<PollMember> ranking = pollMemberRepository.findByPollIdOrderByTotalPointsDescJoinedAtAsc(pollId);
		if (ranking.isEmpty())
			return 3;

		poll.setStatus(PollStatus.FINISHED);
		poll.setFinishedAt(LocalDateTime.now(ZoneOffset.UTC));
		pollRepository.save(poll);

		// El ganador es el primer lugar del ranking
		PollMember winner = ranking.get(0);

		// TODO: llamar AlbumService.awardWinnerPackage(winner.getUser().getUsername())
		// cuando el módulo de álbum esté implementado
		System.out.println("[PollService] Premio pendiente de entrega para usuario: " + winner.getUser().getUsername());

		return 0;
	}

	// ─── Job: cerrar pronósticos de partidos que ya iniciaron ─────────────────

	/**
	 * Se ejecuta cada minuto. Cierra automáticamente los pronósticos de partidos
	 * que ya comenzaron para garantizar equidad (HU-18).
	 */
	@Scheduled(fixedDelay = 60_000)
	public void closeOpenPredictionsForStartedMatches() {
		List<Prediction> open = predictionRepository.findOpenPredictionsForStartedFixtures();
		open.forEach(p -> {
			p.setClosed(true);
			predictionRepository.save(p);
		});
		if (!open.isEmpty()) {
			System.out.println("[PollService] Pronósticos cerrados: " + open.size());
		}
	}

	// ─── Helpers privados ─────────────────────────────────────────────────────

	private int score(int predHome, int predAway, int realHome, int realAway) {
		// Marcador exacto
		if (predHome == realHome && predAway == realAway)
			return 3;
		// Ganador / empate correcto
		int predResult = Integer.compare(predHome, predAway);
		int realResult = Integer.compare(realHome, realAway);
		if (predResult == realResult)
			return 1;
		return 0;
	}

	private boolean isMatchStarted(String status) {
		return status != null
				&& List.of("1H", "HT", "2H", "ET", "BT", "P", "FT", "AET", "PEN", "SUSP", "INT", "ABD", "AWD", "WO")
						.contains(status);
	}

	/**
	 * Recalcula la posición en el ranking de todos los miembros de una polla. Se
	 * llama cada vez que cambian los puntos de algún miembro.
	 */
	private void recalculateRanking(Long pollId) {
		List<PollMember> members = pollMemberRepository.findByPollIdOrderByTotalPointsDescJoinedAtAsc(pollId);
		for (int i = 0; i < members.size(); i++) {
			members.get(i).setRankingPosition(i + 1);
			pollMemberRepository.save(members.get(i));
		}
	}

	private String generateUniqueInviteCode() {
		Random rnd = new Random();
		String code;
		do {
			StringBuilder sb = new StringBuilder(CODE_LENGTH);
			for (int i = 0; i < CODE_LENGTH; i++) {
				sb.append(CODE_CHARS.charAt(rnd.nextInt(CODE_CHARS.length())));
			}
			code = sb.toString();
		} while (pollRepository.existsByInviteCode(code));
		return code;
	}

	// ─── Conversión Entidad → DTO ─────────────────────────────────────────────

	private PollDTO toPollDTO(Poll p) {
		PollDTO dto = new PollDTO();
		dto.setId(p.getId());
		dto.setName(p.getName());
		dto.setInviteCode(p.getInviteCode());
		dto.setCreatorUsername(p.getCreator().getUsername());
		dto.setStatus(p.getStatus());
		dto.setCreatedAt(p.getCreatedAt());
		dto.setFinishedAt(p.getFinishedAt());
		dto.setMemberCount(p.getMembers() != null ? p.getMembers().size() : 0);
		return dto;
	}

	private PollDTO toPollDTOWithMembers(Poll p) {
		PollDTO dto = toPollDTO(p);
		List<PollMember> members = pollMemberRepository.findByPollIdOrderByTotalPointsDescJoinedAtAsc(p.getId());
		dto.setMembers(members.stream().map(this::toPollMemberDTO).collect(Collectors.toList()));
		return dto;
	}

	private PollMemberDTO toPollMemberDTO(PollMember m) {
		PollMemberDTO dto = new PollMemberDTO();
		dto.setPollMemberId(m.getId());
		dto.setUserId(m.getUser().getId());
		dto.setUsername(m.getUser().getUsername());
		dto.setName(m.getUser().getName());
		dto.setTotalPoints(m.getTotalPoints());
		dto.setRankingPosition(m.getRankingPosition());
		dto.setJoinedAt(m.getJoinedAt());
		return dto;
	}

	private PredictionDTO toPredictionDTO(Prediction p) {
		PredictionDTO dto = new PredictionDTO();
		dto.setId(p.getId());
		dto.setPollMemberId(p.getPollMember().getId());
		dto.setFixtureId(p.getFixture().getId());
		dto.setHomeTeamName(p.getFixture().getHomeTeamName());
		dto.setAwayTeamName(p.getFixture().getAwayTeamName());
		dto.setPredictedHomeGoals(p.getPredictedHomeGoals());
		dto.setPredictedAwayGoals(p.getPredictedAwayGoals());
		dto.setActualHomeGoals(p.getFixture().getHomeGoals());
		dto.setActualAwayGoals(p.getFixture().getAwayGoals());
		dto.setPointsEarned(p.getPointsEarned());
		dto.setClosed(p.isClosed());
		dto.setCreatedAt(p.getCreatedAt());
		dto.setUpdatedAt(p.getUpdatedAt());
		return dto;
	}
}