package co.edu.unbosque.mundialhubbackend.configuration;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import co.edu.unbosque.mundialhubbackend.model.User;
import co.edu.unbosque.mundialhubbackend.model.User.Role;
import co.edu.unbosque.mundialhubbackend.model.User.UserStatus;
import co.edu.unbosque.mundialhubbackend.repository.UserRepository;
import co.edu.unbosque.mundialhubbackend.util.AESUtil;

/**
 * Clase de configuración que carga datos iniciales en la base de datos al
 * iniciar la aplicación. Se utiliza para insertar datos por defecto para
 * pruebas o inicialización.
 * 
 * @since 1.0
 */
@Configuration
public class LoadDatabase {

	private static final Logger log = LoggerFactory.getLogger(LoadDatabase.class);

	/**
	 * Bean CommandLineRunner que ejecuta la carga de datos al inicio. Verifica si
	 * existen usuarios administrador y usuario normal y los crea si no existen.
	 * 
	 * @param userRepo        Repositorio para gestionar usuarios.
	 * @param passwordEncoder Codificador de contraseñas para almacenar de forma
	 *                        segura.
	 * @return Runner que inicializa los datos en la base.
	 */
	@Bean
	CommandLineRunner initDatabase(UserRepository userRepo, PasswordEncoder passwordEncoder) {

		return args -> {
			Optional<User> found = userRepo.findByUsername("admin");
			if (found.isPresent()) {
				log.info("El administrador ya existe, omitiendo la creación del administrador...");
			} else {
				String password = AESUtil.hashingToSHA256(AESUtil.hashingToSHA256("123"));
//				User adminUser = new User("admin", "admin", "example2@gmail.com", passwordEncoder.encode(password),
//						Role.ADMIN);
				User adminUser = new User("admin", "admin", "admin@gmail.com", passwordEncoder.encode(password), Role.ADMIN, UserStatus.ACTIVE);
				adminUser.setEnabled(true);
				adminUser.setAccountNonExpired(true);
				adminUser.setAccountNonLocked(true);
				adminUser.setCredentialsNonExpired(true);
				userRepo.save(adminUser);
				
				log.info("Precargando usuario administrador");
			}
			Optional<User> found2 = userRepo.findByUsername("normal");
			if (found2.isPresent()) {
				log.info("El usuario normal ya existe, omitiendo la creación del usuario normal...");
			} else {
				String password = AESUtil.hashingToSHA256(AESUtil.hashingToSHA256("123"));
				User normalUser = new User("normal", "normal", "example@gmail.com", passwordEncoder.encode(password),
						Role.USER, UserStatus.ACTIVE);
				normalUser.setEnabled(true);
				normalUser.setAccountNonExpired(true);
				normalUser.setAccountNonLocked(true);
				normalUser.setCredentialsNonExpired(true);
				userRepo.save(normalUser);
				log.info("Precargando usuario normal");
			}

		};
	}

}
