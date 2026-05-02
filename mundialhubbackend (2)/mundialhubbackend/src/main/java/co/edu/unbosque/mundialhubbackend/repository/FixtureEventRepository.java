package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.FixtureEvent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FixtureEventRepository extends JpaRepository<FixtureEvent, Long> {

	List<FixtureEvent> findByFixtureIdOrderByElapsedAsc(Long fixtureId);

	void deleteByFixtureId(Long fixtureId);
}