package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.Sticker;
import co.edu.unbosque.mundialhubbackend.model.Sticker.StickerRarity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface StickerRepository extends JpaRepository<Sticker, Long> {

	List<Sticker> findByActiveTrueOrderByAlbumNumberAsc();

	List<Sticker> findBySectionAndActiveTrue(String section);

	// Todos los stickers de una rareza para el sistema de sorteo
	List<Sticker> findByRarityAndActiveTrue(StickerRarity rarity);

	// Secciones únicas del álbum (para construir la vista por secciones HU-21)
	@Query("SELECT DISTINCT s.section FROM Sticker s WHERE s.active = true ORDER BY s.section")
	List<String> findDistinctSections();

	// Stickers que el usuario NO tiene todavía (para mostrar los huecos del álbum)
	@Query("""
			    SELECT s FROM Sticker s
			    WHERE s.active = true
			      AND s.id NOT IN (
			          SELECT us.sticker.id FROM UserSticker us WHERE us.user.username = :username
			      )
			    ORDER BY s.albumNumber ASC
			""")
	List<Sticker> findMissingStickersForUser(@Param("username") String username);
}