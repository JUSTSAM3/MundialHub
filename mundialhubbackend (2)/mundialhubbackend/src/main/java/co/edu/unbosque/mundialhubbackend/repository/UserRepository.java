package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.User;
import co.edu.unbosque.mundialhubbackend.model.User.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByEmail(String email);
	
	Optional<User> findByUsername(String username);

	Optional<User> findByEmailAndPassword(String email, String password);

	List<User> findByRole(Role role);

	boolean existsByEmail(String email);
}