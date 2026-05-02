package co.edu.unbosque.mundialhubbackend.controller;

import java.util.ArrayList;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundialhubbackend.dto.UserDTO;
import co.edu.unbosque.mundialhubbackend.security.JwtUtil;
import co.edu.unbosque.mundialhubbackend.service.UserService;
import co.edu.unbosque.mundialhubbackend.util.AccountUpdateRequest;
import co.edu.unbosque.mundialhubbackend.util.ParameterEmailRequest;
import co.edu.unbosque.mundialhubbackend.util.RegisterRequest;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import jakarta.mail.MessagingException;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Controlador REST para la gestión de usuarios. Proporciona endpoints para
 * creación, consulta, actualización y eliminación de usuarios.
 * 
 * Aplica seguridad JWT para los endpoints. Permite CORS para orígenes
 * específicos.
 * 
 * @since 1.0
 */
@RestController
@RequestMapping("/user")
@CrossOrigin(origins = { "*" })
@Transactional
@SecurityRequirement(name = "bearerAuth")
public class UserController {

	@Autowired
	private UserService accountServ;

	@Autowired
	private JwtUtil jwtUtil;

	/**
	 * Constructor vacío.
	 */
	public UserController() {
	}

	/**
	 * Crea un nuevo usuario.
	 * 
	 * @param data DTO con datos del usuario a crear.
	 * @return ResponseEntity con mensaje y código HTTP.
	 * @throws MessagingException Si hay error al enviar correo.
	 */
	@PostMapping("/create")
	public ResponseEntity<String> create(@RequestBody RegisterRequest data) throws MessagingException {
		System.out.println("CREANDO LOG -------");
		System.out.println("DATO POR CONTROLLER ");
		System.out.println(data.toString());
		System.out.println("----FIN-----");

		int status = accountServ.createForAdmin(data);

		System.out.println(status);

		if (status == 0) {
			return new ResponseEntity<>("Account created successfully!!", HttpStatus.CREATED);
		} else if (status == -2) {
			return new ResponseEntity<>("Email already registered", HttpStatus.IM_USED);
		} else if (status == -3) {
			return new ResponseEntity<>("Username already taken", HttpStatus.IM_USED);
		} else {
			return new ResponseEntity<>("Error creating account", HttpStatus.NOT_ACCEPTABLE);
		}
	}

	/**
	 * Obtiene la lista de todos los usuarios.
	 * 
	 * @return ResponseEntity con la lista de usuarios o estado no content.
	 */
	@GetMapping("/getall")
	public ResponseEntity<ArrayList<UserDTO>> showAll() {
		ArrayList<UserDTO> accounts = accountServ.findAll();
		if (accounts.isEmpty()) {
			return new ResponseEntity<ArrayList<UserDTO>>(accounts, HttpStatus.NO_CONTENT);

		} else {
			return new ResponseEntity<ArrayList<UserDTO>>(accounts, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Obtiene un usuario por email.
	 * 
	 * @param in DTO con email para consulta.
	 * @return ResponseEntity con usuario o no content.
	 */
	@PostMapping("/getaccountbyemail")
	public ResponseEntity<UserDTO> getAccount(@RequestBody ParameterEmailRequest in) {

		UserDTO out = accountServ.getAccountByEmail(in);

		if (out == null) {
			return new ResponseEntity<UserDTO>(out, HttpStatus.NO_CONTENT);
		} else {
			return new ResponseEntity<UserDTO>(out, HttpStatus.ACCEPTED);
		}
	}

	/**
	 * Elimina un usuario por ID.
	 * 
	 * @param id Identificador del usuario.
	 * @return ResponseEntity con mensaje y estado.
	 */
	@DeleteMapping("/deletebyid/{id}")
	public ResponseEntity<String> deleteById(@PathVariable Long id) {
		int status = accountServ.deleteById(id);
		if (status == 0) {
			return new ResponseEntity<String>("Account deleted", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<String>("Error while deleting account", HttpStatus.NOT_ACCEPTABLE);
		}
	}

	/**
	 * Elimina un usuario por email recibido en JSON.
	 * 
	 * @param dto DTO con email del usuario a eliminar.
	 * @return ResponseEntity con mensaje y estado.
	 */
	@DeleteMapping("/deletebyemailbyjson")
	public ResponseEntity<String> deleteByJson(@RequestBody ParameterEmailRequest dto) {

		int status = accountServ.deleteByEmail(dto);
		if (status == 0) {
			return new ResponseEntity<String>("Account deleted", HttpStatus.ACCEPTED);
		} else {
			return new ResponseEntity<String>("Error while deleting account", HttpStatus.NOT_ACCEPTABLE);
		}
	}

	/**
	 * Actualiza un usuario basado en email, con datos antiguos y nuevos.
	 * 
	 * @param update Request con usuario antiguo y nuevo.
	 * @return ResponseEntity con mensaje y estado.
	 */
	@PutMapping("/updatebyemailbyjson")
	public ResponseEntity<String> putMethodName(@RequestBody AccountUpdateRequest update) {
		ParameterEmailRequest old = update.getOldOne();
		RegisterRequest newAccount = update.getNewOne();

		int status = accountServ.updateByEmail(old, newAccount);

		if (status == 0) {
			return new ResponseEntity<String>("Account updated", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<String>("Username already taken", HttpStatus.IM_USED);
		} else if (status == 2) {
			return new ResponseEntity<String>("Email already registered", HttpStatus.IM_USED);
		} else if (status == 3) {
			return new ResponseEntity<String>("User to update not found", HttpStatus.NOT_FOUND);
		} else {
			return new ResponseEntity<String>("Error updating account", HttpStatus.NOT_ACCEPTABLE);
		}
	}

	/**
	 * Extrae el nombre de usuario del token JWT recibido.
	 * 
	 * @param param Token JWT
	 * @return ResponseEntity con el nombre de usuario o mensaje de error.
	 */
	@GetMapping("/getbyusername")
	public ResponseEntity<String> getByUsername(@RequestParam String param) {

		String username = jwtUtil.extractUsername(param);

		if (username != null) {
			return new ResponseEntity<String>(username, HttpStatus.ACCEPTED);
		} else
			return new ResponseEntity<String>("No se pudo extraer el nombre", HttpStatus.ACCEPTED);

	}

}
