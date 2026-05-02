package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.PollMember;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface PollMemberRepository extends JpaRepository<PollMember, Long> {

	Optional<PollMember> findByPollIdAndUserUsername(Long pollId, String username);

	boolean existsByPollIdAndUserUsername(Long pollId, String username);

	// Ranking ordenado por puntos (HU-19)
	List<PollMember> findByPollIdOrderByTotalPointsDescJoinedAtAsc(Long pollId);

	// Todos los miembros de una polla para recalcular posiciones
	List<PollMember> findByPollId(Long pollId);
}