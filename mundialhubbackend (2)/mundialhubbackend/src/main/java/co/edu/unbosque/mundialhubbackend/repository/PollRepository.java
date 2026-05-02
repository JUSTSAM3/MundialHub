package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.Poll;
import co.edu.unbosque.mundialhubbackend.model.Poll.PollStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PollRepository extends JpaRepository<Poll, Long> {

	Optional<Poll> findByInviteCode(String inviteCode);

	// Todas las pollas donde participa un usuario (como creador o miembro)
	@Query("""
			    SELECT DISTINCT p FROM Poll p
			    LEFT JOIN p.members m
			    WHERE p.creator.username = :username OR m.user.username = :username
			    ORDER BY p.createdAt DESC
			""")
	List<Poll> findPollsByParticipant(@Param("username") String username);

	// Pollas activas que aún no tienen a este usuario
	@Query("""
			    SELECT p FROM Poll p
			    WHERE p.status = :status
			    AND NOT EXISTS (
			        SELECT m FROM PollMember m
			        WHERE m.poll = p AND m.user.username = :username
			    )
			""")
	List<Poll> findActiveNotJoinedByUser(@Param("username") String username, @Param("status") PollStatus status);

	boolean existsByInviteCode(String inviteCode);
}