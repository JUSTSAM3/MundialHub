package co.edu.unbosque.mundialhubbackend.controller;

import co.edu.unbosque.mundialhubbackend.dto.StadiumDTO;
import co.edu.unbosque.mundialhubbackend.dto.TeamDTO;
import co.edu.unbosque.mundialhubbackend.security.JwtUtil;
import co.edu.unbosque.mundialhubbackend.service.PreferenceService;
import co.edu.unbosque.mundialhubbackend.util.PreferenceRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/preference")
@CrossOrigin(origins = { "*" })
@SecurityRequirement(name = "bearerAuth")
public class PreferenceController {

	@Autowired
	private PreferenceService preferenceService;

	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * Extrae el username desde el token JWT del header Authorization. El subject
	 * del JWT corresponde al campo username del User.
	 */
	private String extractUsername(HttpServletRequest request) {
		String authHeader = request.getHeader("Authorization");
		if (authHeader != null && authHeader.startsWith("Bearer ")) {
			return jwtUtil.extractUsername(authHeader.substring(7));
		}
		return null;
	}

	/**
	 * GET /preferences/team/get Devuelve todos los equipos favoritos del usuario
	 * autenticado.
	 */
	@GetMapping("/team/get")
	public ResponseEntity<List<TeamDTO>> getFavoriteTeams(HttpServletRequest request) {
		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		return ResponseEntity.ok(preferenceService.getFavoriteTeams(username));
	}

	/**
	 * PUT /preferences/team/update Reemplaza la lista completa de equipos favoritos
	 * con los IDs recibidos. Body: { "ids": [1, 2, 3] } Usado en el asistente de
	 * configuración inicial (HU-03) donde el usuario selecciona varios equipos de
	 * una sola vez.
	 */
	@PutMapping("/team/update")
	public ResponseEntity<String> updateFavoriteTeams(HttpServletRequest request,
			@RequestBody PreferenceRequest prefRequest) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		int result = preferenceService.updateFavoriteTeams(username, prefRequest.getId());
		switch (result) {
		case 0:
			return ResponseEntity.ok("Equipos favoritos actualizados correctamente");
		case 1:
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<>("Error al actualizar equipos", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * POST /preferences/team/add Agrega uno o varios equipos a los favoritos sin
	 * afectar los ya existentes. Body: { "ids": [4, 5] }
	 */
	@PostMapping("/team/add")
	public ResponseEntity<String> addFavoriteTeams(HttpServletRequest request,
			@RequestBody PreferenceRequest prefRequest) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		int result = preferenceService.addTeamsToFavorites(username, prefRequest.getId());
		switch (result) {
		case 0:
			return ResponseEntity.ok("Equipos añadidos a favoritos");
		case 1:
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<>("Error al añadir equipos", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * DELETE /preferences/team/remove Elimina uno o varios equipos de los
	 * favoritos. Body: { "ids": [2, 3] }
	 */
	@DeleteMapping("/team/remove")
	public ResponseEntity<String> removeFavoriteTeams(HttpServletRequest request,
			@RequestBody PreferenceRequest prefRequest) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		int result = preferenceService.removeTeamsFromFavorites(username, prefRequest.getId());
		switch (result) {
		case 0:
			return ResponseEntity.ok("Equipos eliminados de favoritos");
		case 1:
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<>("Error al eliminar equipos", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * GET /preferences/stadium/get Devuelve todos los estadios favoritos del
	 * usuario autenticado.
	 */
	@GetMapping("/stadium/get")
	public ResponseEntity<List<StadiumDTO>> getFavoriteStadiums(HttpServletRequest request) {
		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>(HttpStatus.UNAUTHORIZED);
		return ResponseEntity.ok(preferenceService.getFavoriteStadiums(username));
	}

	/**
	 * PUT /preferences/stadium/update Reemplaza la lista completa de estadios
	 * favoritos con los IDs recibidos. Body: { "ids": [1, 2, 3] }
	 */
	@PutMapping("/stadium/update")
	public ResponseEntity<String> updateFavoriteStadiums(HttpServletRequest request,
			@RequestBody PreferenceRequest prefRequest) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		int result = preferenceService.updateFavoriteStadiums(username, prefRequest.getId());
		switch (result) {
		case 0:
			return ResponseEntity.ok("Estadios favoritos actualizados correctamente");
		case 1:
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<>("Error al actualizar estadios", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * POST /preferences/stadium/add Agrega uno o varios estadios a los favoritos
	 * sin afectar los ya existentes. Body: { "ids": [4, 5] }
	 */
	@PostMapping("/stadium/add")
	public ResponseEntity<String> addFavoriteStadiums(HttpServletRequest request,
			@RequestBody PreferenceRequest prefRequest) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		int result = preferenceService.addStadiumsToFavorites(username, prefRequest.getId());
		switch (result) {
		case 0:
			return ResponseEntity.ok("Estadios añadidos a favoritos");
		case 1:
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<>("Error al añadir estadios", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}

	/**
	 * DELETE /preferences/stadium/remove Elimina uno o varios estadios de los
	 * favoritos. Body: { "ids": [2, 3] }
	 */
	@DeleteMapping("/stadium/remove")
	public ResponseEntity<String> removeFavoriteStadiums(HttpServletRequest request,
			@RequestBody PreferenceRequest prefRequest) {

		String username = extractUsername(request);
		if (username == null)
			return new ResponseEntity<>("Token inválido", HttpStatus.UNAUTHORIZED);

		int result = preferenceService.removeStadiumsFromFavorites(username, prefRequest.getId());
		switch (result) {
		case 0:
			return ResponseEntity.ok("Estadios eliminados de favoritos");
		case 1:
			return new ResponseEntity<>("Usuario no encontrado", HttpStatus.NOT_FOUND);
		default:
			return new ResponseEntity<>("Error al eliminar estadios", HttpStatus.INTERNAL_SERVER_ERROR);
		}
	}
}