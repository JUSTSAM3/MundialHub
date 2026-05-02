package co.edu.unbosque.mundialhubbackend.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import co.edu.unbosque.mundialhubbackend.dto.StadiumDTO;
import co.edu.unbosque.mundialhubbackend.service.StadiumService;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;

@RestController
@RequestMapping("/stadium")
@CrossOrigin(origins = {"*"})
@SecurityRequirement(name = "bearerAuth")
public class StadiumController {

    @Autowired
    private StadiumService stadiumService;

    @PostMapping("/create")
    public ResponseEntity<String> create(@RequestBody StadiumDTO dto) {
        int res = stadiumService.create(dto);
        if (res == 0) return new ResponseEntity<>("Estadio creado", HttpStatus.CREATED);
        if (res == 2) return new ResponseEntity<>("Nombre ya existe", HttpStatus.CONFLICT);
        return new ResponseEntity<>("Error interno", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @GetMapping("/getall")
    public ResponseEntity<List<StadiumDTO>> getAll() {
        return ResponseEntity.ok(stadiumService.getAll());
    }

    @GetMapping("/getbyname/{name}")
    public ResponseEntity<StadiumDTO> getByName(@PathVariable String name) {
        StadiumDTO dto = stadiumService.getByName(name);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @PutMapping("/updatebyname/{name}")
    public ResponseEntity<String> update(@PathVariable String name, @RequestBody StadiumDTO dto) {
        int res = stadiumService.updateByName(name, dto);
        return res == 0 ? ResponseEntity.accepted().body("Estadio actualizado") : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/deletebyname/{name}")
    public ResponseEntity<String> delete(@PathVariable String name) {
        int res = stadiumService.deleteByName(name);
        return res == 0 ? ResponseEntity.ok("Estadio eliminado") : ResponseEntity.notFound().build();
    }
}