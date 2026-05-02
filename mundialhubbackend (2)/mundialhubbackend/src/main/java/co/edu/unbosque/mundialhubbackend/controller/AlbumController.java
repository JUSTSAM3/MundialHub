package co.edu.unbosque.mundialhubbackend.controller;

import co.edu.unbosque.mundialhubbackend.dto.StickerExchangeDTO;
import co.edu.unbosque.mundialhubbackend.dto.StickerPackageDTO;
import co.edu.unbosque.mundialhubbackend.dto.UserStickerDTO;
import co.edu.unbosque.mundialhubbackend.model.StickerPackage.PackageSource;
import co.edu.unbosque.mundialhubbackend.security.JwtUtil;
import co.edu.unbosque.mundialhubbackend.service.AlbumService;
import co.edu.unbosque.mundialhubbackend.util.AlbumDetailRequest;
import co.edu.unbosque.mundialhubbackend.util.OpenPackageRequest;
import co.edu.unbosque.mundialhubbackend.util.ProposeExchangeRequest;
import co.edu.unbosque.mundialhubbackend.util.RedeemPromoRequest;
import co.edu.unbosque.mundialhubbackend.util.RespondExchangeRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/album")
@CrossOrigin(origins = { "*" })
@SecurityRequirement(name = "bearerAuth")
public class AlbumController {

	@Autowired
	private AlbumService albumService;
	@Autowired
	private JwtUtil jwtUtil;

	private String extractUsername(HttpServletRequest req) {
		String header = req.getHeader("Authorization");
		if (header != null && header.startsWith("Bearer "))
			return jwtUtil.extractUsername(header.substring(7));
		return null;
	}

	// ─── POST /album/view — Ver álbum (HU-21) ─────────────────────────────────
	/**
	 * Devuelve el álbum del usuario organizado por secciones. Si section viene null
	 * → álbum completo.
	 *
	 * Body: { "section": "Argentina" } ← opcional Body: {} ← devuelve todo
	 */
	@PostMapping("/view")
	public ResponseEntity<List<UserStickerDTO>> viewAlbum(HttpServletRequest request,
			@RequestBody AlbumDetailRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		List<UserStickerDTO> result = albumService.getAlbum(username, body.getSection());
		if (result.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(result);
	}

	// ─── GET /album/sections — Resumen por secciones (HU-21) ─────────────────
	/**
	 * Devuelve un resumen de las secciones del álbum con el porcentaje de
	 * completitud.
	 */
	@GetMapping("/sections")
	public ResponseEntity<Map<String, Object>> getAlbumSections(HttpServletRequest request) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		return ResponseEntity.ok(albumService.getAlbumSections(username));
	}

	// ─── GET /album/duplicates — Ver repetidas (HU-24) ────────────────────────
	/**
	 * Devuelve todas las láminas que el usuario tiene en cantidad > 1.
	 */
	@GetMapping("/duplicates")
	public ResponseEntity<List<UserStickerDTO>> getDuplicates(HttpServletRequest request) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		List<UserStickerDTO> result = albumService.getDuplicates(username);
		if (result.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(result);
	}

	// ─── GET /album/packages/pending — Paquetes por abrir (HU-23) ────────────
	/**
	 * Devuelve los paquetes de láminas que el usuario aún no ha abierto.
	 */
	@GetMapping("/packages/pending")
	public ResponseEntity<List<StickerPackageDTO>> getPendingPackages(HttpServletRequest request) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		List<StickerPackageDTO> result = albumService.getPendingPackages(username);
		if (result.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(result);
	}

	// ─── POST /album/packages/open — Abrir paquete (HU-23) ───────────────────
	/**
	 * Abre un paquete pendiente y revela sus láminas. Devuelve las láminas
	 * obtenidas para la animación de apertura.
	 *
	 * Body: { "packageId": 7 }
	 */
	@PostMapping("/packages/open")
	public ResponseEntity<?> openPackage(HttpServletRequest request, @RequestBody OpenPackageRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getPackageId() == null)
			return new ResponseEntity<>("El packageId es obligatorio", HttpStatus.BAD_REQUEST);

		Object[] result = albumService.openPackage(username, body.getPackageId());
		int code = (int) result[0];

		switch (code) {
		case 0:
			return ResponseEntity.ok((StickerPackageDTO) result[1]);
		case 1:
			return new ResponseEntity<>("Paquete no encontrado", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>("Este paquete no te pertenece", HttpStatus.FORBIDDEN);
		case 3:
			return new ResponseEntity<>("Este paquete ya fue abierto", HttpStatus.CONFLICT);
		default:
			return new ResponseEntity<>("Error al abrir el paquete", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── POST /album/packages/award — Otorgar paquete por actividad (HU-22) ──
	/**
	 * El backend llama esto internamente cuando detecta una acción elegible. El
	 * frontend puede llamarlo al completar una acción específica.
	 *
	 * Body: { "source": "DAILY_LOGIN" } Fuentes válidas: DAILY_LOGIN |
	 * PREDICTION_COMPLETE | FOLLOW_LIVE
	 */
	@PostMapping("/packages/award")
	public ResponseEntity<String> awardActivityPackage(HttpServletRequest request,
			@RequestBody AlbumDetailRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		PackageSource source;
		try {
			source = PackageSource.valueOf(body.getSection());
		} catch (IllegalArgumentException | NullPointerException e) {
			return new ResponseEntity<>(
					"Fuente inválida. Valores aceptados: DAILY_LOGIN, PREDICTION_COMPLETE, FOLLOW_LIVE",
					HttpStatus.BAD_REQUEST);
		}

		int result = albumService.awardActivityPackage(username, source);
		switch (result) {
		case 0:
			return ResponseEntity.ok("Paquete de láminas entregado correctamente");
		case 1:
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>("Alcanzaste el límite de paquetes por este tipo de actividad hoy",
					HttpStatus.TOO_MANY_REQUESTS);
		default:
			return new ResponseEntity<>("Error al entregar el paquete", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── POST /album/promo/redeem — Canjear código promo (HU-26) ─────────────
	/**
	 * Body: { "promoCode": "MUNDIAL2026" }
	 */
	@PostMapping("/promo/redeem")
	public ResponseEntity<String> redeemPromo(HttpServletRequest request, @RequestBody RedeemPromoRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getPromoCode() == null || body.getPromoCode().isBlank())
			return new ResponseEntity<>("El código promocional es obligatorio", HttpStatus.BAD_REQUEST);

		int result = albumService.redeemPromoCode(username, body.getPromoCode());
		switch (result) {
		case 0:
			return ResponseEntity.ok("Código canjeado. Revisa tus paquetes pendientes.");
		case 1:
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>("Código promocional inválido", HttpStatus.NOT_FOUND);
		case 3:
			return new ResponseEntity<>("El código promocional ya venció", HttpStatus.GONE);
		case 4:
			return new ResponseEntity<>("El código promocional ya agotó sus usos", HttpStatus.GONE);
		case 5:
			return new ResponseEntity<>("Ya canjeaste este código anteriormente", HttpStatus.CONFLICT);
		default:
			return new ResponseEntity<>("Error al canjear el código", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── POST /album/exchange/propose — Proponer intercambio (HU-25) ──────────
	/**
	 * Body: { "receiverUsername": "diego_bonza", "offeredStickerId": 42,
	 * "requestedStickerId": 87 }
	 */
	@PostMapping("/exchange/propose")
	public ResponseEntity<String> proposeExchange(HttpServletRequest request,
			@RequestBody ProposeExchangeRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getReceiverUsername() == null || body.getOfferedStickerId() == null
				|| body.getRequestedStickerId() == null)
			return new ResponseEntity<>("receiverUsername, offeredStickerId y requestedStickerId son obligatorios",
					HttpStatus.BAD_REQUEST);

		int result = albumService.proposeExchange(username, body.getReceiverUsername().trim(),
				body.getOfferedStickerId(), body.getRequestedStickerId());

		switch (result) {
		case 0:
			return new ResponseEntity<>("Propuesta de intercambio enviada", HttpStatus.CREATED);
		case 1:
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>("El usuario receptor no existe", HttpStatus.NOT_FOUND);
		case 3:
			return new ResponseEntity<>("No tienes esa lámina como repetida para ofrecer", HttpStatus.BAD_REQUEST);
		case 4:
			return new ResponseEntity<>("El receptor no tiene esa lámina como repetida", HttpStatus.BAD_REQUEST);
		case 5:
			return new ResponseEntity<>("Alcanzaste el límite de 10 intercambios por hoy",
					HttpStatus.TOO_MANY_REQUESTS);
		case 6:
			return new ResponseEntity<>("No puedes proponer un intercambio contigo mismo", HttpStatus.BAD_REQUEST);
		default:
			return new ResponseEntity<>("Error al proponer intercambio", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── POST /album/exchange/respond — Responder propuesta (HU-25) ──────────
	/**
	 * Body: { "exchangeId": 3, "accepted": true }
	 */
	@PostMapping("/exchange/respond")
	public ResponseEntity<String> respondExchange(HttpServletRequest request,
			@RequestBody RespondExchangeRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getExchangeId() == null)
			return new ResponseEntity<>("El exchangeId es obligatorio", HttpStatus.BAD_REQUEST);

		int result = albumService.respondExchange(username, body.getExchangeId(), body.isAccepted());

		switch (result) {
		case 0:
			String msg = body.isAccepted() ? "Intercambio aceptado. Las láminas fueron transferidas."
					: "Intercambio rechazado.";
			return ResponseEntity.ok(msg);
		case 1:
			return new ResponseEntity<>("Intercambio no encontrado", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>("No eres el receptor de esta propuesta", HttpStatus.FORBIDDEN);
		case 3:
			return new ResponseEntity<>("Este intercambio ya fue respondido", HttpStatus.CONFLICT);
		case 4:
			return new ResponseEntity<>("La propuesta de intercambio ya venció", HttpStatus.GONE);
		case 5:
			return new ResponseEntity<>("Alcanzaste el límite de 10 intercambios por hoy",
					HttpStatus.TOO_MANY_REQUESTS);
		case 6:
			return new ResponseEntity<>("Las condiciones del intercambio ya no son válidas", HttpStatus.CONFLICT);
		default:
			return new ResponseEntity<>("Error al responder el intercambio", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── POST /album/exchange/cancel — Cancelar propuesta ────────────────────
	/**
	 * Body: { "exchangeId": 3 }
	 */
	@PostMapping("/exchange/cancel")
	public ResponseEntity<String> cancelExchange(HttpServletRequest request, @RequestBody RespondExchangeRequest body) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		if (body.getExchangeId() == null)
			return new ResponseEntity<>("El exchangeId es obligatorio", HttpStatus.BAD_REQUEST);

		int result = albumService.cancelExchange(username, body.getExchangeId());
		switch (result) {
		case 0:
			return ResponseEntity.ok("Propuesta cancelada correctamente");
		case 1:
			return new ResponseEntity<>("Intercambio no encontrado", HttpStatus.NOT_FOUND);
		case 2:
			return new ResponseEntity<>("No eres el proponente de esta propuesta", HttpStatus.FORBIDDEN);
		case 3:
			return new ResponseEntity<>("La propuesta ya fue respondida o cancelada", HttpStatus.CONFLICT);
		default:
			return new ResponseEntity<>("Error al cancelar la propuesta", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	// ─── GET /album/exchange/incoming — Propuestas recibidas ─────────────────
	@GetMapping("/exchange/incoming")
	public ResponseEntity<List<StickerExchangeDTO>> getIncomingExchanges(HttpServletRequest request) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		List<StickerExchangeDTO> result = albumService.getIncomingExchanges(username);
		if (result.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(result);
	}

	// ─── GET /album/exchange/history — Historial de intercambios ─────────────
	@GetMapping("/exchange/history")
	public ResponseEntity<List<StickerExchangeDTO>> getExchangeHistory(HttpServletRequest request) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);

		List<StickerExchangeDTO> result = albumService.getMyExchangeHistory(username);
		if (result.isEmpty())
			return new ResponseEntity<>(HttpStatus.NO_CONTENT);

		return ResponseEntity.ok(result);
	}
}