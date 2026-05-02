package co.edu.unbosque.mundialhubbackend.controller;

import co.edu.unbosque.mundialhubbackend.dto.FixtureDTO;
import co.edu.unbosque.mundialhubbackend.security.JwtUtil;
import co.edu.unbosque.mundialhubbackend.service.FixtureService;
import co.edu.unbosque.mundialhubbackend.util.GetFixturesRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/fixtures")
@CrossOrigin(origins = { "*" })
@SecurityRequirement(name = "bearerAuth")
public class FixtureController {

	@Autowired
	private FixtureService fixtureService;
	@Autowired
	private JwtUtil jwtUtil;

	private String extractUsername(HttpServletRequest req) {
		String header = req.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer "))
			return jwtUtil.extractUsername(header.substring(7));
		return null;
	}

	// ─── GET /fixtures/all — Calendario completo (HU-04) ─────────────────────
	/**
	 * Devuelve todos los partidos del Mundial 2026. Sirve desde caché si el TTL
	 * está vigente.
	 */
	@GetMapping("/all")
	public ResponseEntity<List<FixtureDTO>> getAllFixtures(HttpServletRequest request) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		List<FixtureDTO> result = fixtureService.getAllFixtures();
		if (result.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(result);
	}

	// ─── POST /fixtures/bydate — Partidos por fecha (HU-04 filtro) ───────────
	/**
	 * Devuelve los partidos de una fecha específica.
	 *
	 * Body: { "date": "2026-06-15" }
	 */
	@PostMapping("/bydate")
	public ResponseEntity<List<FixtureDTO>> getFixturesByDate(HttpServletRequest request,
			@RequestBody GetFixturesRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		if (body.getDate() == null || body.getDate().isBlank())
			return new ResponseEntity<>( HttpStatus.BAD_REQUEST);

		List<FixtureDTO> result = fixtureService.getFixturesByDate(body.getDate().trim());
		if (result.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(result);
	}

	// ─── GET /fixtures/live — Partidos en vivo (HU-05) ───────────────────────
	/**
	 * Devuelve los partidos actualmente en curso. Caché TTL 30 s — si no hay
	 * ninguno en vivo retorna 204.
	 */
	@GetMapping("/live")
	public ResponseEntity<List<FixtureDTO>> getLiveFixtures(HttpServletRequest request) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		List<FixtureDTO> result = fixtureService.getLiveFixtures();
		if (result.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(result);
	}

	// ─── POST /fixtures/detail — Detalle de un partido (HU-05) ──────────────
	/**
	 * Devuelve el detalle completo de un partido con todos sus eventos. Si el
	 * proveedor externo falló, devuelve el último dato en caché con el campo
	 * pendingUpdate=true (RNF-04).
	 *
	 * Body: { "fixtureId": 215662 }
	 */
	@PostMapping("/detail")
	public ResponseEntity<FixtureDTO> getFixtureDetail(HttpServletRequest request,
			@RequestBody GetFixturesRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		if (body.getFixtureId() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		FixtureDTO dto = fixtureService.getFixtureById(body.getFixtureId());
		if (dto == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return ResponseEntity.ok(dto);
	}

	// ─── GET /fixtures/agenda — Agenda personal (HU-06) ──────────────────────
	/**
	 * Devuelve los próximos partidos de los equipos favoritos del usuario
	 * autenticado. Los equipos favoritos se obtienen de sus preferencias guardadas
	 * en BD.
	 */
	@GetMapping("/agenda")
	public ResponseEntity<List<FixtureDTO>> getAgenda(HttpServletRequest request) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		List<FixtureDTO> result = fixtureService.getAgendaForUser(username);
		if (result.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(result);
	}

	// ─── POST /fixtures/sync — Sincronización manual ──────────────────────────
	/**
	 * Fuerza la sincronización del calendario con la API-Football. Solo ADMIN y
	 * CONTENT pueden ejecutarlo. Se usa después de que el rol Contenido publica un
	 * cambio en el calendario (HU-27).
	 *
	 * Body vacío — no requiere parámetros.
	 */
	@PostMapping("/sync")
	public ResponseEntity<String> syncFixtures(HttpServletRequest request) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		int result = fixtureService.syncAllFixtures();
		switch (result) {
		case 0:
			return ResponseEntity.ok("Calendario sincronizado correctamente");
		default:
			return new ResponseEntity<>(
					"El proveedor externo no respondió. " + "Se mantiene el último calendario disponible.",
					HttpStatus.SERVICE_UNAVAILABLE);
		}
	}
}