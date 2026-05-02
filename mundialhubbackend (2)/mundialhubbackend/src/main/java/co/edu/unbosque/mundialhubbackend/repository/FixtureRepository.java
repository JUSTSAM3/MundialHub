package co.edu.unbosque.mundialhubbackend.repository;

import co.edu.unbosque.mundialhubbackend.model.Fixture;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface FixtureRepository extends JpaRepository<Fixture, Long> {

	// Todos los partidos de una liga y temporada
	List<Fixture> findByLeagueIdAndSeasonOrderByMatchDateAsc(Long leagueId, Integer season);

	// Partidos de un equipo en una temporada
	@Query("SELECT f FROM Fixture f WHERE (f.homeTeamId = :teamId OR f.awayTeamId = :teamId) AND f.season = :season ORDER BY f.matchDate ASC")
	List<Fixture> findByTeamAndSeason(@Param("teamId") Long teamId, @Param("season") Integer season);

	// Partidos en vivo (cualquier estado activo)
	@Query("SELECT f FROM Fixture f WHERE f.status IN ('1H','HT','2H','ET','BT','P','LIVE')")
	List<Fixture> findLiveFixtures();

	// Partidos de un rango de fechas
	List<Fixture> findByMatchDateBetweenOrderByMatchDateAsc(LocalDateTime from, LocalDateTime to);

	// Agenda personal: partidos de los equipos favoritos del usuario
	@Query("SELECT f FROM Fixture f WHERE (f.homeTeamId IN :teamIds OR f.awayTeamId IN :teamIds) AND f.matchDate >= :from ORDER BY f.matchDate ASC")
	List<Fixture> findAgendaForTeams(@Param("teamIds") List<Long> teamIds, @Param("from") LocalDateTime from);

	// Partidos cuya caché ya venció (para refresco en background)
	@Query("SELECT f FROM Fixture f WHERE f.status IN ('1H','HT','2H','ET','BT','P','LIVE') AND f.cachedAt < :threshold")
	List<Fixture> findLiveFixturesWithExpiredCache(@Param("threshold") LocalDateTime threshold);

	// Partidos futuros con caché vencida (TTL 24h)
	@Query("SELECT f FROM Fixture f WHERE f.status = 'NS' AND f.cachedAt < :threshold")
	List<Fixture> findUpcomingWithExpiredCache(@Param("threshold") LocalDateTime threshold);
}