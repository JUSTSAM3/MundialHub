package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.StickerPackage;
import co.edu.unbosque.mundialhubbackend.model.StickerPackage.PackageSource;
import co.edu.unbosque.mundialhubbackend.model.StickerPackage.PackageStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StickerPackageRepository extends JpaRepository<StickerPackage, Long> {

	// Paquetes pendientes de apertura del usuario (HU-23)
	List<StickerPackage> findByUserUsernameAndStatusOrderByAwardedAtDesc(String username, PackageStatus status);

	// Todos los paquetes del usuario (historial)
	List<StickerPackage> findByUserUsernameOrderByAwardedAtDesc(String username);

	// Cuántos paquetes de un tipo recibió el usuario hoy (límite RNF-16)
	@Query("""
			    SELECT COUNT(sp) FROM StickerPackage sp
			    WHERE sp.user.username = :username
			      AND sp.source        = :source
			      AND sp.awardedAt    >= :startOfDay
			""")
	long countByUserAndSourceToday(@Param("username") String username, @Param("source") PackageSource source,
			@Param("startOfDay") LocalDateTime startOfDay);
}