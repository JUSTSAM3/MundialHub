package co.edu.unbosque.mundialhubbackend.controller;

import co.edu.unbosque.mundialhubbackend.dto.PollDTO;
import co.edu.unbosque.mundialhubbackend.dto.PollMemberDTO;
import co.edu.unbosque.mundialhubbackend.dto.PredictionDTO;
import co.edu.unbosque.mundialhubbackend.security.JwtUtil;
import co.edu.unbosque.mundialhubbackend.service.PollService;
import co.edu.unbosque.mundialhubbackend.util.CreatePollRequest;
import co.edu.unbosque.mundialhubbackend.util.FinishPollRequest;
import co.edu.unbosque.mundialhubbackend.util.GetFixturesRequest;
import co.edu.unbosque.mundialhubbackend.util.JoinPollRequest;
import co.edu.unbosque.mundialhubbackend.util.SavePredictionRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/polls")
@CrossOrigin(origins = { "*" })
@SecurityRequirement(name = "bearerAuth")
public class PollController {

	@Autowired
	private PollService pollService;
	@Autowired
	private JwtUtil jwtUtil;

	private String extractUsername(HttpServletRequest req) {
		String header = req.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer "))
			return jwtUtil.extractUsername(header.substring(7));
		return null;
	}

	// ─── POST /polls/create — Crear polla (HU-16) ─────────────────────────────
	/**
	 * Crea una polla dentro de una comunidad existente. Solo los miembros de esa
	 * comunidad pueden crear pollas en ella.
	 *
	 * Body: { "name": "Polla del trabajo", "communityId": 1 }
	 */
	@PostMapping("/create")
	public ResponseEntity<String> createPoll(HttpServletRequest request, @RequestBody CreatePollRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getName() == null || body.getName().isBlank())
			return new ResponseEntity<>("El nombre de la polla es obligatorio", HttpStatus.BAD_REQUEST);

		if (body.getCommunityId() == null)
			return new ResponseEntity<>("El communityId es obligatorio", HttpStatus.BAD_REQUEST);

		int result = pollService.createPoll(username, body.getName().trim(), body.getCommunityId());

		switch (result) {
		case 0:
			return new ResponseEntity<>("Polla creada correctamente", HttpStatus.CREATED);
		case 1:
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>("Comunidad no encontrada", HttpStatus.NOT_FOUND);
		case 3:
			return new ResponseEntity<>("Debes ser miembro de la comunidad para crear una polla", HttpStatus.FORBIDDEN);
		default:
			return new ResponseEntity<>("Error al crear la polla", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── POST /polls/join — Unirse a una polla (HU-17) ────────────────────────
	/**
	 * El usuario se une a una polla usando el código de 8 caracteres. Debe ser
	 * miembro de la comunidad a la que pertenece la polla.
	 *
	 * Body: { "inviteCode": "AB3K9ZTW" }
	 */
	@PostMapping("/join")
	public ResponseEntity<String> joinPoll(HttpServletRequest request, @RequestBody JoinPollRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getInviteCode() == null || body.getInviteCode().isBlank())
			return new ResponseEntity<>("El código de invitación es obligatorio", HttpStatus.BAD_REQUEST);

		int result = pollService.joinPoll(username, body.getInviteCode().trim());
		switch (result) {
		case 0:
			return ResponseEntity.ok("Te uniste a la polla correctamente");
		case 1:
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>("Código de invitación inválido", HttpStatus.NOT_FOUND);
		case 3:
			return new ResponseEntity<>("La polla ya finalizó", HttpStatus.GONE);
		case 4:
			return new ResponseEntity<>("Ya eres miembro de esta polla", HttpStatus.CONFLICT);
		case 5:
			return new ResponseEntity<>("Debes ser miembro de la comunidad para unirte a esta polla",
					HttpStatus.FORBIDDEN);
		default:
			return new ResponseEntity<>("Error al unirse a la polla", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── GET /polls/mine — Mis pollas ──────────────────────────────────────────
	/**
	 * Devuelve todas las pollas en las que el usuario autenticado participa.
	 */
	@GetMapping("/mine")
	public ResponseEntity<List<PollDTO>> getMyPolls(HttpServletRequest request) {
		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		List<PollDTO> polls = pollService.getPollsForUser(username);
		if (polls.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(polls);
	}

	// ─── POST /polls/detail — Detalle de una polla ────────────────────────────
	/**
	 * Devuelve el detalle completo de una polla con todos sus miembros y ranking.
	 *
	 * Body: { "pollId": 1 }
	 */
	@PostMapping("/detail")
	public ResponseEntity<PollDTO> getPollDetail(HttpServletRequest request, @RequestBody FinishPollRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		if (body.getPollId() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		PollDTO dto = pollService.getPollById(body.getPollId());
		if (dto == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return ResponseEntity.ok(dto);
	}

	// ─── POST /polls/ranking — Ranking de una polla (HU-19) ───────────────────
	/**
	 * Devuelve el ranking actualizado de una polla ordenado por puntos.
	 *
	 * Body: { "pollId": 1 }
	 */
	@PostMapping("/ranking")
	public ResponseEntity<List<PollMemberDTO>> getRanking(HttpServletRequest request,
			@RequestBody FinishPollRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		if (body.getPollId() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		List<PollMemberDTO> ranking = pollService.getRanking(body.getPollId());
		if (ranking.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(ranking);
	}

	// ─── POST /polls/predictions/save — Guardar pronóstico (HU-18) ────────────
	/**
	 * Registra o actualiza el pronóstico del usuario para un partido dentro de su
	 * polla. No se permite modificar si el partido ya inició.
	 *
	 * Body: { "pollId": 1, "fixtureId": 215662, "predictedHomeGoals": 2,
	 * "predictedAwayGoals": 1 }
	 */
	@PostMapping("/predictions/save")
	public ResponseEntity<String> savePrediction(HttpServletRequest request, @RequestBody SavePredictionRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getPollId() == null || body.getFixtureId() == null || body.getPredictedHomeGoals() == null
				|| body.getPredictedAwayGoals() == null)
			return new ResponseEntity<>("pollId, fixtureId, predictedHomeGoals y predictedAwayGoals son obligatorios",
					HttpStatus.BAD_REQUEST);

		if (body.getPredictedHomeGoals() < 0 || body.getPredictedAwayGoals() < 0)
			return new ResponseEntity<>("Los goles no pueden ser negativos", HttpStatus.BAD_REQUEST);

		int result = pollService.savePrediction(username, body.getPollId(), body.getFixtureId(),
				body.getPredictedHomeGoals(), body.getPredictedAwayGoals());

		switch (result) {
		case 0:
			return ResponseEntity.ok("Pronóstico guardado correctamente");
		case 1:
			return new ResponseEntity<>("No eres miembro de esta polla", HttpStatus.FORBIDDEN);
		case 2:
			return new ResponseEntity<>("Partido no encontrado", HttpStatus.NOT_FOUND);
		case 3:
			return new ResponseEntity<>("El partido ya inició, no se pueden modificar pronósticos", HttpStatus.LOCKED);
		case 4:
			return new ResponseEntity<>("La polla ya finalizó", HttpStatus.GONE);
		default:
			return new ResponseEntity<>("Error al guardar pronóstico", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── POST /polls/predictions/mine — Mis pronósticos en una polla ──────────
	/**
	 * Devuelve todos los pronósticos del usuario autenticado dentro de una polla.
	 *
	 * Body: { "pollId": 1 }
	 */
	@PostMapping("/predictions/mine")
	public ResponseEntity<List<PredictionDTO>> getMyPredictions(HttpServletRequest request,
			@RequestBody FinishPollRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		if (body.getPollId() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		List<PredictionDTO> predictions = pollService.getMyPredictions(username, body.getPollId());

		if (predictions.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(predictions);
	}

	// ─── POST /polls/predictions/fixture — Pronósticos por partido ────────────
	/**
	 * Devuelve los pronósticos de todos los miembros de una polla para un partido
	 * específico. Se usa para mostrar la tabla de resultados (HU-19).
	 *
	 * Body: { "pollId": 1, "fixtureId": 215662 }
	 */
	@PostMapping("/predictions/fixture")
	public ResponseEntity<List<PredictionDTO>> getPredictionsForFixture(HttpServletRequest request,
			@RequestBody SavePredictionRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		if (body.getPollId() == null || body.getFixtureId() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		List<PredictionDTO> predictions = pollService.getPredictionsForFixture(body.getPollId(), body.getFixtureId());

		if (predictions.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(predictions);
	}

	// ─── POST /polls/finish — Cerrar polla y entregar premio (HU-20) ──────────
	/**
	 * Cierra la polla, calcula el ganador final y le entrega el premio digital.
	 * Solo el creador de la polla o un ADMIN pueden ejecutar esta acción.
	 *
	 * Body: { "pollId": 1 }
	 */
	@PostMapping("/finish")
	public ResponseEntity<String> finishPoll(HttpServletRequest request, @RequestBody FinishPollRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getPollId() == null)
			return new ResponseEntity<>("El pollId es obligatorio", HttpStatus.BAD_REQUEST);

		int result = pollService.finishPoll(body.getPollId());
		switch (result) {
		case 0:
			return ResponseEntity.ok("Polla finalizada. Premio pendiente de entrega al ganador.");
		case 1:
			return new ResponseEntity<>("Polla no encontrada", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>("La polla ya estaba finalizada", HttpStatus.CONFLICT);
		case 3:
			return new ResponseEntity<>("La polla no tiene miembros", HttpStatus.UNPROCESSABLE_ENTITY);
		default:
			return new ResponseEntity<>("Error al finalizar la polla", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}