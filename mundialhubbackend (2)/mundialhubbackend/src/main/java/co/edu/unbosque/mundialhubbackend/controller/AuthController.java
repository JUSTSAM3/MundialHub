package co.edu.unbosque.mundialhubbackend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import co.edu.unbosque.mundialhubbackend.model.User;
import co.edu.unbosque.mundialhubbackend.model.User.Role;
import co.edu.unbosque.mundialhubbackend.security.JwtUtil;
import co.edu.unbosque.mundialhubbackend.service.UserService;
import co.edu.unbosque.mundialhubbackend.util.AESUtil;
import co.edu.unbosque.mundialhubbackend.util.ConfirmEmailRequest;
import co.edu.unbosque.mundialhubbackend.util.LoginRequest;
import co.edu.unbosque.mundialhubbackend.util.RegisterRequest;
import co.edu.unbosque.mundialhubbackend.util.ParameterEmailRequest;
import jakarta.mail.MessagingException;

/**
 * Controlador REST para la autenticación de usuarios. Maneja las operaciones de
 * inicio de sesión y registro de usuarios. Proporciona endpoints para login,
 * registro, confirmación de cuenta y reenvío de código de confirmación.
 * 
 * @since 1.0
 */
@RestController
@RequestMapping("/auth")
public class AuthController {

	/**
	 * Gestor de autenticación para validar credenciales de usuario.
	 */
	private final AuthenticationManager authenticationManager;

	/**
	 * Utilidad para operaciones con tokens JWT.
	 */
	private final JwtUtil jwtUtil;

	/**
	 * Servicio para operaciones relacionadas con usuarios.
	 */
	private final UserService userService;

	/**
	 * Constructor que inicializa las dependencias necesarias para el controlador.
	 * 
	 * @param authenticationManager Gestor de autenticación
	 * @param jwtUtil               Utilidad para tokens JWT
	 * @param userService           Servicio de usuarios
	 */
	public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService) {
		this.authenticationManager = authenticationManager;
		this.jwtUtil = jwtUtil;
		this.userService = userService;
	}

	/**
	 * Maneja las solicitudes de inicio de sesión. Autentica al usuario y genera un
	 * token JWT si las credenciales son válidas.
	 * 
	 * @param loginRequest DTO con las credenciales de inicio de sesión (nombre de
	 *                     usuario y contraseña)
	 * @return ResponseEntity con el token JWT y el rol del usuario si la
	 *         autenticación es exitosa, o un mensaje de error si falla
	 */
	@PostMapping("/login")
	public ResponseEntity<?> login(@RequestBody LoginRequest loginRequest) {
		System.out.println("------------LOGIN-------------------");
		System.out.println(loginRequest.getUsername());
//		loginRequest.setUsername(AESUtil.decrypt(loginRequest.getUsername()));
//		solicitud para admin y normal user, doble hash simulado del front 
		loginRequest.setPassword(AESUtil.hashingToSHA256(AESUtil.hashingToSHA256(loginRequest.getPassword())));
//		loginRequest.setPassword(AESUtil.hashingToSHA256(loginRequest.getPassword()));

		try {
			Authentication authentication = authenticationManager.authenticate(
					new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword()));

			UserDetails userDetails = (UserDetails) authentication.getPrincipal();
			String jwt = jwtUtil.generateToken(userDetails);

			// Obtener el rol de userDetails si es nuestra clase User
			String role = null;
			if (userDetails instanceof User) {
				User user = (User) userDetails;
				role = user.getRole().name();
			}

//			jwt = AESUtil.encrypt(jwt); ya cuando conecta a front se habilita esto

			return ResponseEntity.ok(new AuthResponse(jwt, role));
		} catch (AuthenticationException e) {
			e.printStackTrace();
			return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
					.body("Nombre de usuario o contraseña inválidos o usuario no encontrado");
		}
	}

	/**
	 * Maneja las solicitudes de registro de nuevos usuarios. Verifica si el nombre
	 * de usuario ya existe y crea un nuevo usuario si está disponible.
	 * 
	 * @param registerRequest DTO con la información del nuevo usuario
	 * @return ResponseEntity con un mensaje de éxito si el registro es exitoso, o
	 *         un mensaje de error si falla
	 * @throws MessagingException Si ocurre un error al enviar el correo de
	 *                            confirmación.
	 */
	@PostMapping("/register")
	public ResponseEntity<?> register(@RequestBody RegisterRequest registerRequest) throws MessagingException {

		// Verificar si el nombre de usuario ya existe
		if (userService.findUsernameAlreadyTaken(registerRequest.getUsername())) {
			return ResponseEntity.status(HttpStatus.CONFLICT).body("El nombre de usuario ya existe");
		}

		System.out.println(registerRequest.toString());
		// Crear nuevo usuario
		registerRequest.setRole(Role.USER);
		int result = userService.create(registerRequest);
		if (result == 0) {
			return ResponseEntity.status(HttpStatus.CREATED).body("Account created succesfully");
		} else if (result == -2) {
			return new ResponseEntity<>("Email already registered", HttpStatus.IM_USED);
		} else if (result == -3) {
			return new ResponseEntity<>("Username already taken", HttpStatus.IM_USED);
		} else {
			return new ResponseEntity<>("Error creating account", HttpStatus.NOT_ACCEPTABLE);
		}
	}

	/**
	 * Endpoint para confirmar la cuenta de usuario mediante código enviado por
	 * correo.
	 * 
	 * @param data Objeto que contiene el email y código de confirmación.
	 * @return ResponseEntity con resultado de la confirmación.
	 */
	@PostMapping("/confirmaccount")
	public ResponseEntity<String> confirmarCorreo(@RequestBody ConfirmEmailRequest data) {

		System.out.println("--- CONFIRMACIÓN CORRREO ---");
		System.out.println(data.getEmail() + " " + data.getCode() + "\nFIN DEL REST CONTROLLER");
//		data.setEmail(AESUtil.decrypt(data.getEmail()));
//		data.setCode(AESUtil.decrypt(data.getCode()));
		System.out.println("CODIGO OBTENIDO " + data.getCode());

		int status = userService.confirmRegistration(data.getEmail(), data.getCode());

		if (status == 0) {
			return new ResponseEntity<String>("Email confirmed", HttpStatus.ACCEPTED);
		} else if (status == -1) {
			return new ResponseEntity<String>("Account doesn't exist", HttpStatus.NOT_FOUND);
		} else if (status == -2) {
			return new ResponseEntity<String>("Account already confirmed", HttpStatus.BAD_REQUEST);
		} else {
			return new ResponseEntity<String>("WRONG VERIFICATION CODE", HttpStatus.NOT_ACCEPTABLE);
		}
	}

	/**
	 * Endpoint para reenviar el código de confirmación de cuenta.
	 * 
	 * @param data DTO con datos del usuario (email).
	 * @return ResponseEntity con resultado del reenvío.
	 * @throws MessagingException Si ocurre error al enviar el correo.
	 */
	@PostMapping("/resendcode")
	public ResponseEntity<String> reenviarConfirmacion(@RequestBody ParameterEmailRequest data)
			throws MessagingException {

//		data.setEmail(AESUtil.decrypt(data.getEmail()));

		int status = userService.resendConfirmRegistration(data);
		System.out.println("---RESEND CONFIRMATION---");
		System.out.println("to " + data.getEmail());

		if (status == 0) {
			return new ResponseEntity<String>("Mail sended", HttpStatus.ACCEPTED);
		} else if (status == 1) {
			return new ResponseEntity<String>("Account already confirmed", HttpStatus.NOT_ACCEPTABLE);
		} else {
			return new ResponseEntity<String>("Account not found", HttpStatus.NOT_FOUND);
		}
	}

	/**
	 * Clase interna para representar la respuesta de autenticación. Contiene el
	 * token JWT y el rol del usuario autenticado.
	 */
	private static class AuthResponse {
		/**
		 * Token JWT generado para el usuario autenticado.
		 */
		private final String token;

		/**
		 * Rol del usuario autenticado.
		 */
		private final String role;

		/**
		 * Constructor con solo token.
		 * 
		 * @param token Token JWT generado
		 */
		@SuppressWarnings("unused")
		public AuthResponse(String token) {
			this.token = token;
			// Extraer rol del token
			this.role = null; // Se establecerá en el constructor con el parámetro de rol
		}

		/**
		 * Constructor con token y rol.
		 * 
		 * @param token Token JWT generado
		 * @param role  Rol del usuario
		 */
		public AuthResponse(String token, String role) {
			this.token = token;
			this.role = role;
		}

		/**
		 * Obtiene el token JWT.
		 * 
		 * @return Token JWT
		 */
		@SuppressWarnings("unused")
		public String getToken() {
			return token;
		}

		/**
		 * Obtiene el rol del usuario.
		 * 
		 * @return Rol del usuario
		 */
		@SuppressWarnings("unused")
		public String getRole() {
			return role;
		}
	}
}
