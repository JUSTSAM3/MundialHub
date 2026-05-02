package co.edu.unbosque.mundialhubbackend.service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Optional;
import java.util.Random;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import co.edu.unbosque.mundialhubbackend.dto.UserDTO;
import co.edu.unbosque.mundialhubbackend.model.User;
import co.edu.unbosque.mundialhubbackend.model.User.UserStatus;
import co.edu.unbosque.mundialhubbackend.repository.UserRepository;
import co.edu.unbosque.mundialhubbackend.util.AESUtil;
import co.edu.unbosque.mundialhubbackend.util.RegisterRequest;
import co.edu.unbosque.mundialhubbackend.util.ParameterEmailRequest;
import jakarta.mail.MessagingException;

/**
 * Servicio para la gestión de usuarios. Proporciona métodos para crear,
 * actualizar, eliminar usuarios, así como manejo de verificación de cuentas y
 * envío de correos.
 * 
 * @since 1.0
 */
@Service
public class UserService {

	@Autowired
	private UserRepository userRepo;

	@Autowired
	private EmailService emailService;

	@Autowired
	private ModelMapper modelMapper;

	@Autowired
	private PasswordEncoder passwordEncoder;

	/**
	 * Crea un nuevo usuario con la información proporcionada. Se asegura de
	 * desencriptar datos, validar, encriptar contraseña, generar código de
	 * verificación y enviar correo.
	 * 
	 * @param data DTO con datos del usuario
	 * @return Código de resultado de la operación: 0 = éxito, -1 = error genérico,
	 *         -2 = email duplicado, -3 = username duplicado, -4 = datos inválidos
	 * @throws MessagingException si falla el envío de correo
	 */
	public int create(RegisterRequest data) throws MessagingException {

		System.out.println("----------ENTRO-----------");
		System.out.println(data.toString());
		User entity = modelMapper.map(data, User.class);
//		User entity = decrypt(data);
		entity.setPassword(AESUtil.hashingToSHA256(entity.getPassword()));

		if (entity.getName() == null || entity.getUsername() == null || entity.getEmail() == null
				|| entity.getPassword() == null) {
			return -4;
		}

		entity.setPassword(passwordEncoder.encode(entity.getPassword()));

		Optional<User> foundEmail = userRepo.findByEmail(entity.getEmail());
		Optional<User> foundUsername = userRepo.findByEmail(entity.getUsername());

		if (foundEmail.isPresent())
			return -2;
		if (foundUsername.isPresent())
			return -3;

		Random random = new Random();

		int code = random.nextInt(999999);
		String codeado = Integer.toString(code);

		entity.setVerificationCode(codeado);
		entity.setStatus(UserStatus.PENDING_VERIFICATION);
		entity.setCreatedAt(LocalDateTime.now());

		System.out.println("------BEFORE SAVE-------");
		UserDTO a = modelMapper.map(entity, UserDTO.class);
		System.out.println(a.toString());

		try {
			entity.setEnabled(false);
			userRepo.save(entity);
			emailService.sendRegisterEmail(entity.getEmail(), codeado, entity.getUsername());
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	public int createForAdmin(RegisterRequest data) throws MessagingException {

		System.out.println("----------ENTRO-----------");
		System.out.println(data.toString());
		User entity = modelMapper.map(data, User.class);
//		User entity = decrypt(data);
		entity.setPassword(AESUtil.hashingToSHA256(entity.getPassword()));

		if (entity.getName() == null || entity.getUsername() == null || entity.getEmail() == null
				|| entity.getPassword() == null) {
			return -4;
		}

		entity.setPassword(passwordEncoder.encode(entity.getPassword()));

		Optional<User> foundEmail = userRepo.findByEmail(entity.getEmail());
		Optional<User> foundUsername = userRepo.findByEmail(entity.getUsername());

		if (foundEmail.isPresent())
			return -2;
		if (foundUsername.isPresent())
			return -3;

//		Random random = new Random();
//		
//		int code = random.nextInt(999999);
//		String codeado = Integer.toString(code);

//		entity.setVerificationCode(codeado);
		entity.setStatus(UserStatus.ACTIVE);
		entity.setCreatedAt(LocalDateTime.now());

		System.out.println("------BEFORE SAVE-------");
		UserDTO a = modelMapper.map(entity, UserDTO.class);
		System.out.println(a.toString());

		try {
//			entity.setEnabled(false);
			userRepo.save(entity);
//			emailService.sendRegisterEmail(entity.getEmail(), codeado, entity.getUsername());
			return 0;
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}
	}

	/**
	 * Verifica el código de confirmación para activar la cuenta.
	 * 
	 * @param email Email del usuario
	 * @param code  Código de verificación recibido
	 * @return Código de resultado: 0 = éxito, -1 = cuenta no existe, -2 = cuenta ya
	 *         confirmada, -3 = código incorrecto
	 */
	public int confirmRegistration(String email, String code) {
		User entity = userRepo.findByEmail(email).orElse(null);
		if (entity == null) {
			return -1;
		}
		if (entity.isEnabled()) {
			return -2;
		}
		if (!entity.getVerificationCode().equals(code)) {
			return -3;
		}

		entity.setEnabled(true);
		entity.setStatus(UserStatus.ACTIVE);
		entity.setVerificationCode(null);

		UserDTO tempo = modelMapper.map(entity, UserDTO.class);
		try {
			userRepo.save(entity);
			emailService.sendWelcomeEmail(tempo.getEmail(), tempo.getUsername());
		} catch (Exception e) {
			e.printStackTrace();
			return -1;
		}

		return 0;
	}

	/**
	 * Reenvía el correo de confirmación con un nuevo código.
	 * 
	 * @param data con el email del usuario
	 * @return Código de resultado: 0 = éxito, 1 = cuenta ya confirmada, 2 = cuenta
	 *         no encontrada
	 * @throws MessagingException si falla el envío
	 */
	public int resendConfirmRegistration(ParameterEmailRequest data) throws MessagingException {

		Optional<User> opt = userRepo.findByEmail(data.getEmail());
		if (opt.isEmpty()) {
			return 2;
		}

		User account = opt.get();
		if (account.isEnabled()) {
			return 1;
		}

		Random random = new Random();

		int code = random.nextInt(999999);
		String newCode = Integer.toString(code);

		account.setVerificationCode(newCode);

		userRepo.save(account);

		emailService.sendRegisterEmail(account.getEmail(), newCode, account.getUsername());

		return 0;
	}

	/**
	 * Obtiene la lista de todos los usuarios como DTOs.
	 * 
	 * @return Lista de usuarios en formato DTO.
	 */
	public ArrayList<UserDTO> findAll() {
		ArrayList<User> entityList = (ArrayList<User>) userRepo.findAll();
		ArrayList<UserDTO> dtoList = new ArrayList<>();

		entityList.forEach((entity) -> {
//			UserDTO dto = encrypt(entity);
			UserDTO dto = modelMapper.map(entity, UserDTO.class);
			dtoList.add(dto);
		});

		return dtoList;
	}

	/**
	 * Elimina un usuario por su ID.
	 * 
	 * @param id ID del usuario a eliminar
	 * @return 0 si se eliminó, 1 si no se encontró
	 */
	public int deleteById(Long id) {
		Optional<User> found = userRepo.findById(id);
		if (found.isPresent()) {
			userRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * Elimina un usuario por email (recibido en DTO).
	 * 
	 * @param dto DTO con el email
	 * @return 0 si se eliminó, 1 si no se encontró
	 */
	public int deleteByEmail(ParameterEmailRequest dto) {

		User entity = modelMapper.map(dto, User.class);

//		entity.setEmail(AESUtil.decrypt(entity.getEmail()));

		Optional<User> found = userRepo.findByEmail(entity.getEmail());
		if (found.isPresent()) {
			userRepo.delete(found.get());
			return 0;
		} else {
			return 1;
		}
	}

	/**
	 * Actualiza un usuario identificado por email, con nuevos datos.
	 * 
	 * @param old        DTO con datos antiguos
	 * @param newAccount DTO con datos nuevos
	 * @return Código de resultado: 0 = éxito, 1 = username duplicado, 2 = email
	 *         duplicado, 3 = usuario no encontrado, 4 = error desconocido
	 */
	public int updateByEmail(ParameterEmailRequest old, RegisterRequest newAccount) {
		String email = old.getEmail();
		User newEntity = modelMapper.map(newAccount, User.class);

		// 1. Verificar si el usuario a editar existe
		Optional<User> userToUpdateOpt = userRepo.findByEmail(email);
		if (!userToUpdateOpt.isPresent()) {
			return 3; // Usuario no encontrado
		}
		User userToUpdate = userToUpdateOpt.get();

		// 2. Validar disponibilidad de nuevo Username (si cambió)
		if (!userToUpdate.getUsername().equals(newAccount.getUsername())) {
			if (userRepo.findByUsername(newAccount.getUsername()).isPresent()) {
				return 1; // Username ya en uso por otro
			}
		}

		// 3. Validar disponibilidad de nuevo Email (si cambió)
		if (!email.equals(newAccount.getEmail())) {
			if (userRepo.findByEmail(newAccount.getEmail()).isPresent()) {
				return 2; // Email ya en uso por otro
			}
		}

		userToUpdate.setName(newEntity.getName());
		userToUpdate.setUsername(newEntity.getUsername());
		userToUpdate.setEmail(newEntity.getEmail());
		userToUpdate.setRole(newEntity.getRole());
		userToUpdate.setPassword(AESUtil.hashingToSHA256(userToUpdate.getPassword()));

		userRepo.save(userToUpdate);
		return 0;

	}

	/**
	 * Obtiene un usuario por email y lo convierte a DTO.
	 * 
	 * @param in DTO con email de búsqueda
	 * @return DTO del usuario encontrado, o null si no existe
	 */
	public UserDTO getAccountByEmail(ParameterEmailRequest in) {

		Optional<User> found = userRepo.findByEmail(in.getEmail());

		UserDTO out = modelMapper.map(found.orElse(null), UserDTO.class);

		return out;

	}

	/**
	 * Verifica si un username ya está tomado.
	 * 
	 * @param username Nombre de usuario a verificar
	 * @return true si está tomado, false si no
	 */
	public boolean findUsernameAlreadyTaken(String username) {
		Optional<User> found = userRepo.findByUsername(username);
		return found.isPresent();
	}

	/**
	 * Desencripta datos del DTO a entidad User.
	 * 
	 * @param dto DTO con datos encriptados
	 * @return Entidad User con datos desencriptados
	 */
	private User decrypt(UserDTO dto) {
		User entity = modelMapper.map(dto, User.class);

		entity.setName(AESUtil.decrypt(entity.getName()));
		entity.setUsername(AESUtil.decrypt(entity.getUsername()));
		entity.setEmail(AESUtil.decrypt(entity.getEmail()));

		return entity;
	}

	/**
	 * Encripta datos de entidad User a DTO.
	 * 
	 * @param e Entidad User
	 * @return DTO con datos encriptados
	 */
	private UserDTO encrypt(User e) {

		UserDTO dto = modelMapper.map(e, UserDTO.class);

		dto.setName(AESUtil.encrypt(dto.getName()));
		dto.setUsername(AESUtil.encrypt(dto.getUsername()));
		dto.setEmail(AESUtil.encrypt(dto.getEmail()));

		return dto;

	}

}
