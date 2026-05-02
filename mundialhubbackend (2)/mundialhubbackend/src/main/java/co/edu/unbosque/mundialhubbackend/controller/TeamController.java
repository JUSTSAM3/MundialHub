package co.edu.unbosque.mundialhubbackend.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundialhubbackend.dto.TeamDTO;
import co.edu.unbosque.mundialhubbackend.service.TeamService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.transaction.Transactional;

@RestController
@RequestMapping("/team")
@CrossOrigin(origins = { "*" })
@Transactional
@SecurityRequirement(name = "bearerAuth")
public class TeamController {

	@Autowired
	private TeamService teamService;

	@PostMapping("/create")
	public ResponseEntity<String> create(@RequestBody TeamDTO dto) {
		int res = teamService.create(dto);
		if (res == 0)
			return new ResponseEntity<>("Creado", HttpStatus.CREATED);
		return new ResponseEntity<>("Error o Duplicado", HttpStatus.BAD_REQUEST);
	}

	@GetMapping("/getall")
	public ResponseEntity<List<TeamDTO>> getAll() {
		return ResponseEntity.ok(teamService.getAll());
	}

	@GetMapping("/getbyname/{name}")
	public ResponseEntity<TeamDTO> getByName(@PathVariable String name) {
		TeamDTO dto = teamService.getByName(name);
		return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
	}

	@PutMapping("/updatebyname/{name}")
	public ResponseEntity<String> update(@PathVariable String name, @RequestBody TeamDTO dto) {
		int res = teamService.updateByName(name, dto);
		return res == 0 ? ResponseEntity.accepted().body("Actualizado") : ResponseEntity.notFound().build();
	}

	@DeleteMapping("/deletebyname/{name}")
	public ResponseEntity<String> delete(@PathVariable String name) {
		int res = teamService.deleteByName(name);
		return res == 0 ? ResponseEntity.ok("Eliminado") : ResponseEntity.notFound().build();
	}
}