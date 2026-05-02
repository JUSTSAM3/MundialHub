package co.edu.unbosque.mundialhubbackend.repository;

import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import co.edu.unbosque.mundialhubbackend.model.Stadium;

public interface StadiumRepository extends JpaRepository<Stadium, Long> {
	Optional<Stadium> findByName(String name);

	void deleteByName(String name);

	boolean existsByName(String name);
}