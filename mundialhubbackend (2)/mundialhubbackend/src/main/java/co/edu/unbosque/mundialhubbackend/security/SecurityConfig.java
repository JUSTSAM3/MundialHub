package co.edu.unbosque.mundialhubbackend.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Clase de configuración de seguridad para la aplicación. Configura la
 * autenticación y autorización basada en JWT.
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

	/**
	 * Filtro de autenticación JWT que procesa los tokens en las solicitudes.
	 */
	private final JwtAuthenticationFilter jwtAuthFilter;

	/**
	 * Servicio que carga los detalles del usuario para la autenticación.
	 */
	private final UserDetailsService userDetailsService;

	/**
	 * Constructor que inicializa los componentes necesarios para la seguridad.
	 * 
	 * @param jwtAuthFilter      Filtro para procesar tokens JWT
	 * @param userDetailsService Servicio para cargar detalles de usuarios
	 */
	public SecurityConfig(JwtAuthenticationFilter jwtAuthFilter, UserDetailsService userDetailsService) {
		this.jwtAuthFilter = jwtAuthFilter;
		this.userDetailsService = userDetailsService;
	}

	/**
	 * Configura la cadena de filtros de seguridad HTTP. Define reglas de acceso,
	 * manejo de sesiones y filtros de autenticación.
	 * 
	 * @param http Configuración de seguridad HTTP
	 * @return Cadena de filtros de seguridad configurada
	 * @throws Exception Si ocurre un error durante la configuración
	 */
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
		http.csrf(csrf -> csrf.disable()).authorizeHttpRequests(auth -> auth
				// 1. Rutas Públicas (Swagger y Auth)
				.requestMatchers("/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/api-docs/**", "/auth/**")
				.permitAll()

				// 2. Gestión de Preferencias (HU-04) - USER y ADMIN
				.requestMatchers("/preference/**").hasAnyRole("USER", "ADMIN")

				// 3. Rutas de Usuario Específicas
				.requestMatchers("/user/updatebyemailbyjson", "/user/getaccountbyemail", "/user/traerusername")
				.hasAnyRole("USER", "ADMIN")

				// 4. Administración de Usuarios - Solo ADMIN
				.requestMatchers("/user/create", "/user/getall", "/user/deletebyemailbyjson", "/user/**")
				.hasRole("ADMIN")

				// 5. Rutas de Lectura (Estadios, Equipos, Partidos) - Acceso para todos
				.requestMatchers("/stadium/getall", "/stadium/getbyname/**", "/team/getall", "/team/getbyname/**",
						"/match/getall")
				.hasAnyRole("USER", "ADMIN", "CONTENT", "SUPPORT", "COMPLIANCE")

				// 6. Rutas de Escritura (Estadios, Equipos, Partidos) - Solo ADMIN y CONTENT
				// (Semi-Administrador)
				.requestMatchers("/stadium/create", "/stadium/updatebyname/**", "/stadium/deletebyname/**",
						"/team/create", "/team/updatebyname/**", "/team/deletebyname/**", "/match/create",
						"/match/result/**")
				.hasAnyRole("ADMIN", "CONTENT")

				// 7. Rutas de Soporte y Cumplimiento
				.requestMatchers("/support/**").hasAnyRole("ADMIN", "SUPPORT").requestMatchers("/compliance/**")
				.hasAnyRole("ADMIN", "COMPLIANCE")

				// Calendario y agenda — todos los roles autenticados
				.requestMatchers("/fixtures", "/fixtures/live", "/fixtures/agenda", "/fixtures/*")
				.hasAnyRole("USER", "ADMIN", "CONTENT", "SUPPORT", "COMPLIANCE")

				// Sincronización manual — solo operadores internos
				.requestMatchers("/fixtures/sync").hasAnyRole("ADMIN", "CONTENT")

				.requestMatchers("/album/**").hasAnyRole("USER", "ADMIN")

				// Pollas — solo usuarios autenticados con rol USER o ADMIN
				.requestMatchers("/polls/**").hasAnyRole("USER", "ADMIN")

				// Comunidades — todos los roles autenticados
				.requestMatchers("/communities/**").hasAnyRole("USER", "ADMIN")
					
				// Cualquier otra ruta requiere autenticación
				.anyRequest().authenticated())
				.sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.authenticationProvider(authenticationProvider())
				.addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class);

		return http.build();
	}

	/**
	 * Configura el proveedor de autenticación. Establece el servicio de detalles de
	 * usuario y el codificador de contraseñas.
	 * 
	 * @return Proveedor de autenticación configurado
	 */
	@Bean
	public AuthenticationProvider authenticationProvider() {
		DaoAuthenticationProvider authProvider = new DaoAuthenticationProvider(userDetailsService);
		authProvider.setPasswordEncoder(passwordEncoder());
		return authProvider;
	}

	/**
	 * Configura el gestor de autenticación.
	 * 
	 * @param config Configuración de autenticación
	 * @return Gestor de autenticación
	 * @throws Exception Si ocurre un error durante la configuración
	 */
	@Bean
	public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
		return config.getAuthenticationManager();
	}

	/**
	 * Configura el codificador de contraseñas. Utiliza BCrypt para el hash de
	 * contraseñas.
	 * 
	 * @return Codificador de contraseñas BCrypt
	 */
	@Bean
	public PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
}
