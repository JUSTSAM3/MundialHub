package co.edu.unbosque.mundialhubbackend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unbosque.mundialhubbackend.model.Team;

public interface TeamRepository extends JpaRepository<Team, Long> {
	Optional<Team> findByName(String name);

	void deleteByName(String name);

	boolean existsByName(String name);
}