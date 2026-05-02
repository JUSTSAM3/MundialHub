package co.edu.unbosque.mundialhubbackend.security;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import co.edu.unbosque.mundialhubbackend.repository.UserRepository;


/**
 * Implementación del servicio de detalles de usuario para Spring Security. Este
 * servicio carga los detalles del usuario a partir del nombre de usuario,
 * utilizando el repositorio UserRepository.
 * 
 * @since 1.0
 */
@Service
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository userRepository;

	/**
	 * Constructor que recibe la dependencia del repositorio de usuarios.
	 * 
	 * @param userRepository Repositorio para acceder a datos de usuario.
	 */
	public UserDetailsServiceImpl(UserRepository userRepository) {
		this.userRepository = userRepository;
	}

	/**
	 * Carga un usuario por su nombre de usuario.
	 * 
	 * @param username Nombre de usuario para buscar.
	 * @return UserDetails con la información del usuario encontrado.
	 * @throws UsernameNotFoundException Si el usuario no es encontrado.
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		return userRepository.findByUsername(username)
				.orElseThrow(() -> new UsernameNotFoundException("User not found with username: " + username));
	}
}
