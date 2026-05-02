package co.edu.unbosque.mundialhubbackend.controller;

import co.edu.unbosque.mundialhubbackend.dto.CommunityDTO;
import co.edu.unbosque.mundialhubbackend.dto.CommunityInviteDTO;
import co.edu.unbosque.mundialhubbackend.security.JwtUtil;
import co.edu.unbosque.mundialhubbackend.service.CommunityService;
import co.edu.unbosque.mundialhubbackend.util.CreateCommunityRequest;
import co.edu.unbosque.mundialhubbackend.util.GenerateInviteRequest;
import co.edu.unbosque.mundialhubbackend.util.JoinCommunityRequest;
import co.edu.unbosque.mundialhubbackend.util.RemoveMemberRequest;
import co.edu.unbosque.mundialhubbackend.util.RevokeInviteRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/communities")
@CrossOrigin(origins = { "*" })
@SecurityRequirement(name = "bearerAuth")
public class CommunityController {

	@Autowired
	private CommunityService communityService;
	@Autowired
	private JwtUtil jwtUtil;

	private String extractUsername(HttpServletRequest req) {
		String header = req.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer "))
			return jwtUtil.extractUsername(header.substring(7));
		return null;
	}

	// ─── POST /communities/create ─────────────────────────────────────────────
	/**
	 * Crea una nueva comunidad. El creador queda registrado automáticamente como
	 * OWNER.
	 *
	 * Body: { "name": "Los del trabajo", "description": "Polla Mundial 2026" }
	 */
	@PostMapping("/create")
	public ResponseEntity<String> createCommunity(HttpServletRequest request,
			@RequestBody CreateCommunityRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getName() == null || body.getName().isBlank())
			return new ResponseEntity<>("El nombre de la comunidad es obligatorio", HttpStatus.BAD_REQUEST);

		int result = communityService.createCommunity(username, body.getName().trim(),
				body.getDescription() != null ? body.getDescription().trim() : "");

		switch (result) {
		case 0:
			return new ResponseEntity<>("Comunidad creada correctamente", HttpStatus.CREATED);
		case 1:
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<>("Error al crear la comunidad", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── GET /communities/mine ────────────────────────────────────────────────
	/**
	 * Devuelve todas las comunidades en las que el usuario autenticado participa,
	 * ya sea como dueño o como miembro.
	 */
	@GetMapping("/mine")
	public ResponseEntity<List<CommunityDTO>> getMyCommunities(HttpServletRequest request) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		List<CommunityDTO> list = communityService.getCommunitiesForUser(username);
		if (list.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(list);
	}

	// ─── POST /communities/detail ─────────────────────────────────────────────
	/**
	 * Devuelve el detalle de una comunidad con su lista de miembros.
	 *
	 * Body: { "communityId": 1 }
	 */
	@PostMapping("/detail")
	public ResponseEntity<CommunityDTO> getCommunityDetail(HttpServletRequest request,
			@RequestBody GenerateInviteRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		if (body.getCommunityId() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		CommunityDTO dto = communityService.getCommunityById(body.getCommunityId());
		if (dto == null)
			return new ResponseEntity<>(HttpStatus.NOT_FOUND);

		return ResponseEntity.ok(dto);
	}

	// ─── POST /communities/invites/generate ───────────────────────────────────
	/**
	 * Genera un enlace de invitación para una comunidad. Solo el OWNER puede
	 * generarlo.
	 *
	 * Body: { "communityId": 1, "maxUses": 10, "expireDays": 3 } maxUses y
	 * expireDays son opcionales. Si no se envían: ilimitado y 7 días.
	 */
	@PostMapping("/invites/generate")
	public ResponseEntity<?> generateInvite(HttpServletRequest request, @RequestBody GenerateInviteRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getCommunityId() == null)
			return new ResponseEntity<>("El communityId es obligatorio", HttpStatus.BAD_REQUEST);

		Object[] result = communityService.generateInvite(username, body.getCommunityId(), body.getMaxUses(),
				body.getExpireDays());

		int code = (int) result[0];
		switch (code) {
		case 0:
			return ResponseEntity.ok((CommunityInviteDTO) result[1]);
		case 1:
			return new ResponseEntity<>("Comunidad no encontrada", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>("Solo el dueño puede generar invitaciones", HttpStatus.FORBIDDEN);
		default:
			return new ResponseEntity<>("Error al generar invitación", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── POST /communities/invites/active ─────────────────────────────────────
	/**
	 * Devuelve todos los enlaces de invitación activos de una comunidad. Solo
	 * accesible para el OWNER.
	 *
	 * Body: { "communityId": 1 }
	 */
	@PostMapping("/invites/active")
	public ResponseEntity<List<CommunityInviteDTO>> getActiveInvites(HttpServletRequest request,
			@RequestBody GenerateInviteRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		if (body.getCommunityId() == null)
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);

		List<CommunityInviteDTO> invites = communityService.getActiveInvites(username, body.getCommunityId());

		if (invites.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(invites);
	}

	// ─── POST /communities/invites/revoke ─────────────────────────────────────
	/**
	 * Revoca (desactiva) un enlace de invitación existente. Solo el OWNER puede
	 * revocarlo.
	 *
	 * Body: { "communityId": 1, "maxUses": null } — se reutiliza inviteId via
	 * communityId
	 *
	 * Nota: se usa GenerateInviteRequest pero aquí communityId funciona como
	 * inviteId para no crear una clase extra. Si se prefiere claridad, crear
	 * RevokeInviteRequest.
	 */
	@PostMapping("/invites/revoke")
	public ResponseEntity<String> revokeInvite(HttpServletRequest request, @RequestBody RevokeInviteRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getInviteId() == null)
			return new ResponseEntity<>("El inviteId es obligatorio", HttpStatus.BAD_REQUEST);

		int result = communityService.revokeInvite(username, body.getInviteId());
		switch (result) {
		case 0:
			return ResponseEntity.ok("Enlace de invitación revocado");
		case 1:
			return new ResponseEntity<>("Invitación no encontrada", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>("Solo el dueño puede revocar invitaciones", HttpStatus.FORBIDDEN);
		default:
			return new ResponseEntity<>("Error al revocar invitación", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── POST /communities/join ───────────────────────────────────────────────
	/**
	 * Procesa el token de un enlace de invitación y agrega al usuario autenticado
	 * como miembro de la comunidad correspondiente.
	 *
	 * El frontend extrae el token de la URL compartida y lo envía aquí en el body.
	 * Ejemplo de URL compartida:
	 * https://mundialhub.app/communities/join/550e8400-e29b...
	 *
	 * Body: { "token": "550e8400-e29b-41d4-a716-446655440000" }
	 */
	@PostMapping("/join")
	public ResponseEntity<String> joinByToken(HttpServletRequest request, @RequestBody JoinCommunityRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Debes iniciar sesión para unirte a la comunidad", HttpStatus.UNAUTHORIZED);

		if (body.getToken() == null || body.getToken().isBlank())
			return new ResponseEntity<>("El token de invitación es obligatorio", HttpStatus.BAD_REQUEST);

		int result = communityService.joinByToken(username, body.getToken().trim());
		switch (result) {
		case 0:
			return ResponseEntity.ok("Te uniste a la comunidad correctamente");
		case 1:
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>("El enlace de invitación no es válido", HttpStatus.NOT_FOUND);
		case 3:
			return new ResponseEntity<>("El enlace de invitación ya venció", HttpStatus.GONE);
		case 4:
			return new ResponseEntity<>("El enlace de invitación ya agotó sus usos", HttpStatus.GONE);
		case 5:
			return new ResponseEntity<>("Ya eres miembro de esta comunidad", HttpStatus.CONFLICT);
		default:
			return new ResponseEntity<>("Error al procesar la invitación", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── POST /communities/members/remove ─────────────────────────────────────
	/**
	 * El OWNER expulsa a un miembro de su comunidad.
	 *
	 * Body: { "communityId": 1, "targetUsername": "diego_bonza" }
	 */
	@PostMapping("/members/remove")
	public ResponseEntity<String> removeMember(HttpServletRequest request, @RequestBody RemoveMemberRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getCommunityId() == null || body.getTargetUsername() == null || body.getTargetUsername().isBlank())
			return new ResponseEntity<>("communityId y targetUsername son obligatorios", HttpStatus.BAD_REQUEST);

		int result = communityService.removeMember(username, body.getCommunityId(), body.getTargetUsername().trim());

		switch (result) {
		case 0:
			return ResponseEntity.ok("Miembro eliminado de la comunidad");
		case 1:
			return new ResponseEntity<>("Comunidad no encontrada", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>("Solo el dueño puede expulsar miembros", HttpStatus.FORBIDDEN);
		case 3:
			return new ResponseEntity<>("El usuario no es miembro de esta comunidad", HttpStatus.NOT_FOUND);
		case 4:
			return new ResponseEntity<>("El dueño no puede expulsarse a sí mismo", HttpStatus.BAD_REQUEST);
		default:
			return new ResponseEntity<>("Error al expulsar miembro", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── POST /communities/members/leave ──────────────────────────────────────
	/**
	 * El usuario autenticado abandona voluntariamente una comunidad. El OWNER no
	 * puede abandonarla directamente.
	 *
	 * Body: { "communityId": 1 }
	 */
	@PostMapping("/members/leave")
	public ResponseEntity<String> leaveCommunity(HttpServletRequest request, @RequestBody GenerateInviteRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getCommunityId() == null)
			return new ResponseEntity<>("El communityId es obligatorio", HttpStatus.BAD_REQUEST);

		int result = communityService.leaveCommunity(username, body.getCommunityId());
		switch (result) {
		case 0:
			return ResponseEntity.ok("Abandonaste la comunidad");
		case 1:
			return new ResponseEntity<>("No eres miembro de esta comunidad", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>(
					"El dueño no puede abandonar la comunidad. " + "Elimínala o transfiere la titularidad primero.",
					HttpStatus.BAD_REQUEST);
		default:
			return new ResponseEntity<>("Error al abandonar la comunidad", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}