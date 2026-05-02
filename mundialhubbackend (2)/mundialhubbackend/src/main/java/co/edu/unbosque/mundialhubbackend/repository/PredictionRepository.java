package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.Prediction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PredictionRepository extends JpaRepository<Prediction, Long> {

	Optional<Prediction> findByPollMemberIdAndFixtureId(Long pollMemberId, Long fixtureId);

	// Todos los pronósticos de un miembro
	List<Prediction> findByPollMemberIdOrderByFixtureMatchDateAsc(Long pollMemberId);

	// Todos los pronósticos de un partido dentro de una polla (HU-19 — ver tabla)
	@Query("""
			    SELECT pr FROM Prediction pr
			    WHERE pr.fixture.id = :fixtureId
			      AND pr.pollMember.poll.id = :pollId
			""")
	List<Prediction> findByPollAndFixture(@Param("pollId") Long pollId, @Param("fixtureId") Long fixtureId);

	// Pronósticos pendientes de calcular puntaje para un partido ya terminado
	@Query("""
			    SELECT pr FROM Prediction pr
			    WHERE pr.fixture.id = :fixtureId
			      AND pr.pointsEarned IS NULL
			      AND pr.closed = true
			""")
	List<Prediction> findUnscoredByFixture(@Param("fixtureId") Long fixtureId);

	// Pronósticos abiertos de partidos que ya iniciaron (para cerrar en batch)
	@Query("""
			    SELECT pr FROM Prediction pr
			    WHERE pr.closed = false
			      AND pr.fixture.status IN ('1H','HT','2H','ET','BT','P','FT','AET','PEN')
			""")
	List<Prediction> findOpenPredictionsForStartedFixtures();
}