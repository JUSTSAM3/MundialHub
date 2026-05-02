package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.Community;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface CommunityRepository extends JpaRepository<Community, Long> {

	// Todas las comunidades donde el usuario es dueño o miembro
	@Query("""
			    SELECT DISTINCT c FROM Community c
			    LEFT JOIN c.members m
			    WHERE c.owner.username = :username
			       OR m.user.username  = :username
			    ORDER BY c.createdAt DESC
			""")
	List<Community> findCommunitiesByParticipant(@Param("username") String username);

	// Verifica que el usuario sea miembro de una comunidad
	@Query("""
			    SELECT COUNT(m) > 0 FROM CommunityMember m
			    WHERE m.community.id = :communityId
			      AND m.user.username = :username
			""")
	boolean isUserMember(@Param("communityId") Long communityId, @Param("username") String username);
}