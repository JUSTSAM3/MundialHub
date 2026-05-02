package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.CommunityInvite;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface CommunityInviteRepository extends JpaRepository<CommunityInvite, Long> {

	Optional<CommunityInvite> findByToken(String token);

	List<CommunityInvite> findByCommunityIdAndActiveTrue(Long communityId);

	// Invitaciones activas que ya vencieron (para limpiar en background)
	List<CommunityInvite> findByActiveTrueAndExpiresAtBefore(LocalDateTime now);
}