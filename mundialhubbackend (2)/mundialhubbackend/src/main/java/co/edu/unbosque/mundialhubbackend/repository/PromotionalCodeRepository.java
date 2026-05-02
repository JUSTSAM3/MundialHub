package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.PromoRedemption;
import co.edu.unbosque.mundialhubbackend.model.PromotionalCode;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface PromotionalCodeRepository extends JpaRepository<PromotionalCode, Long> {

	Optional<PromotionalCode> findByCodeAndActiveTrue(String code);

	List<PromotionalCode> findByCreatedByUsernameOrderByCreatedAtDesc(String username);

	// Verifica si el usuario ya canjeó este código
	@Query("""
			    SELECT COUNT(r) > 0 FROM PromoRedemption r
			    WHERE r.code.id = :codeId
			      AND r.user.username = :username
			""")
	boolean hasUserRedeemed(@Param("codeId") Long codeId, @Param("username") String username);

	// Necesario porque PromoRedemption no tiene su propio Repository
	@Modifying
	@Query("SELECT r FROM PromoRedemption r WHERE r.code.id = :codeId")
	List<PromoRedemption> findRedemptionsByCode(@Param("codeId") Long codeId);

	default void saveRedemption(PromoRedemption redemption) {
		// Se gestiona a través del cascade de PromotionalCode
		// Implementación delegada al EntityManager via @Modifying en el service
	}
}