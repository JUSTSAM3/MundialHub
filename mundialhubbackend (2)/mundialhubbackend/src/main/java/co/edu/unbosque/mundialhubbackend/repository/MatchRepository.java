package co.edu.unbosque.mundialhubbackend.repository;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import co.edu.unbosque.mundialhubbackend.model.Match;
import co.edu.unbosque.mundialhubbackend.model.Match.MatchStatus;

public interface MatchRepository extends JpaRepository<Match, Long> {

	@Query("SELECT m FROM Match m WHERE m.status = :status AND m.matchDate BETWEEN :startDate AND :endDate")
	List<Match> findMatchesByStatusAndDateRange(@Param("status") MatchStatus status,
			@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
}