package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.StickerExchange;
import co.edu.unbosque.mundialhubbackend.model.StickerExchange.ExchangeStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface StickerExchangeRepository extends JpaRepository<StickerExchange, Long> {

	// Propuestas que el usuario envió
	List<StickerExchange> findByProposerUsernameOrderByProposedAtDesc(String username);

	// Propuestas que el usuario recibió y están pendientes
	List<StickerExchange> findByReceiverUsernameAndStatusOrderByProposedAtDesc(String username, ExchangeStatus status);

	// Intercambios del usuario (enviados + recibidos) para el historial
	@Query("""
			    SELECT e FROM StickerExchange e
			    WHERE e.proposer.username = :username
			       OR e.receiver.username = :username
			    ORDER BY e.proposedAt DESC
			""")
	List<StickerExchange> findAllByParticipant(@Param("username") String username);

	// Cuántos intercambios completó el usuario hoy (límite 10 por día RNF-16)
	@Query("""
			    SELECT COUNT(e) FROM StickerExchange e
			    WHERE (e.proposer.username = :username OR e.receiver.username = :username)
			      AND e.status      = 'ACCEPTED'
			      AND e.respondedAt >= :startOfDay
			""")
	long countCompletedExchangesToday(@Param("username") String username,
			@Param("startOfDay") LocalDateTime startOfDay);

	// Intercambios pendientes que ya vencieron (para el job de limpieza)
	List<StickerExchange> findByStatusAndExpiresAtBefore(ExchangeStatus status, LocalDateTime now);
}