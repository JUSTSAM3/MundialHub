package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.CommunityMember;
import co.edu.unbosque.mundialhubbackend.model.CommunityMember.CommunityRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CommunityMemberRepository extends JpaRepository<CommunityMember, Long> {

	Optional<CommunityMember> findByCommunityIdAndUserUsername(Long communityId, String username);

	boolean existsByCommunityIdAndUserUsername(Long communityId, String username);

	List<CommunityMember> findByCommunityIdOrderByJoinedAtAsc(Long communityId);

	// Para verificar si el usuario es dueño antes de acciones privilegiadas
	boolean existsByCommunityIdAndUserUsernameAndRole(Long communityId, String username, CommunityRole role);

	void deleteByCommunityIdAndUserUsername(Long communityId, String username);
}