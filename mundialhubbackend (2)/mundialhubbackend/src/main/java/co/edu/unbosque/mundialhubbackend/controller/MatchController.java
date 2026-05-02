package co.edu.unbosque.mundialhubbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundialhubbackend.dto.MatchDTO;
import co.edu.unbosque.mundialhubbackend.service.MatchService;
import co.edu.unbosque.mundialhubbackend.util.MatchCreateRequest;
import co.edu.unbosque.mundialhubbackend.util.MatchResultRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/match")
@CrossOrigin(origins = { "*" })
@SecurityRequirement(name = "bearerAuth")
public class MatchController {

	@Autowired
	private MatchService matchService;

	// Solo ADMIN/OPERATOR (Se configura en SecurityConfig)
	@PostMapping("/create")
	public ResponseEntity<String> createMatch(@RequestBody MatchCreateRequest request) {
		int status = matchService.createMatch(request);
		if (status == 0)
			return new ResponseEntity<>("Partido programado", HttpStatus.CREATED);
		if (status == 1)
			return new ResponseEntity<>("Equipo o estadio no encontrado", HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>("Error interno", HttpStatus.INTERNAL_SERVER_ERROR);
	}

	// Todos los usuarios
	@GetMapping("/getall")
	public ResponseEntity<List<MatchDTO>> getAllMatches() {
		List<MatchDTO> list = matchService.getAllMatches();
		return list.isEmpty() ? ResponseEntity.noContent().build() : ResponseEntity.ok(list);
	}

	// Solo ADMIN/OPERATOR
	@PutMapping("/result")
	public ResponseEntity<String> updateResult(@RequestBody MatchResultRequest request) {
		int status = matchService.updateMatchResult(request);
		if (status == 0)
			return new ResponseEntity<>("Resultado registrado y partido finalizado", HttpStatus.OK);
		if (status == 1)
			return new ResponseEntity<>("Partido no encontrado", HttpStatus.NOT_FOUND);
		if (status == 3)
			return new ResponseEntity<>("Faltan los marcadores", HttpStatus.BAD_REQUEST);
		return new ResponseEntity<>("Error interno", HttpStatus.INTERNAL_SERVER_ERROR);
	}
}