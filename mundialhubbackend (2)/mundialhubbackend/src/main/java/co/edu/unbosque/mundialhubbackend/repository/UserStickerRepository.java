package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.UserSticker;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UserStickerRepository extends JpaRepository<UserSticker, Long> {

	Optional<UserSticker> findByUserUsernameAndStickerId(String username, Long stickerId);

	List<UserSticker> findByUserUsernameOrderByStickerAlbumNumberAsc(String username);

	// Álbum por sección (HU-21)
	@Query("""
			    SELECT us FROM UserSticker us
			    WHERE us.user.username = :username
			      AND us.sticker.section = :section
			    ORDER BY us.sticker.albumNumber ASC
			""")
	List<UserSticker> findByUserAndSection(@Param("username") String username, @Param("section") String section);

	// Repetidas disponibles para intercambio (HU-24)
	@Query("""
			    SELECT us FROM UserSticker us
			    WHERE us.user.username = :username
			      AND us.quantity > 1
			    ORDER BY us.sticker.albumNumber ASC
			""")
	List<UserSticker> findDuplicatesByUser(@Param("username") String username);

	// Repetidas marcadas como disponibles para intercambio
	@Query("""
			    SELECT us FROM UserSticker us
			    WHERE us.user.username = :username
			      AND us.offeredForExchange > 0
			    ORDER BY us.sticker.albumNumber ASC
			""")
	List<UserSticker> findOfferedByUser(@Param("username") String username);

	// Total de láminas únicas que tiene el usuario
	@Query("SELECT COUNT(us) FROM UserSticker us WHERE us.user.username = :username")
	long countUniqueByUser(@Param("username") String username);

	boolean existsByUserUsernameAndStickerId(String username, Long stickerId);
}