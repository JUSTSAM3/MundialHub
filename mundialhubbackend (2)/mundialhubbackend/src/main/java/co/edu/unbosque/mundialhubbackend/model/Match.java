package co.edu.unbosque.mundialhubbackend.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "matches") 
public class Match {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne
	@JoinColumn(name = "home_team_id", nullable = false)
	private Team homeTeam;

	@ManyToOne
	@JoinColumn(name = "away_team_id", nullable = false)
	private Team awayTeam;

	@ManyToOne
	@JoinColumn(name = "stadium_id", nullable = false)
	private Stadium stadium;

	@Column(nullable = false)
	private LocalDateTime matchDate;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false)
	private MatchStatus status;

	private Integer homeScore;

	private Integer awayScore;

	public Match() {
		this.status = MatchStatus.SCHEDULED;
	}

	public enum MatchStatus {
		SCHEDULED, IN_PROGRESS, FINISHED
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Team getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(Team homeTeam) {
		this.homeTeam = homeTeam;
	}

	public Team getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(Team awayTeam) {
		this.awayTeam = awayTeam;
	}

	public Stadium getStadium() {
		return stadium;
	}

	public void setStadium(Stadium stadium) {
		this.stadium = stadium;
	}

	public LocalDateTime getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(LocalDateTime matchDate) {
		this.matchDate = matchDate;
	}

	public MatchStatus getStatus() {
		return status;
	}

	public void setStatus(MatchStatus status) {
		this.status = status;
	}

	public Integer getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(Integer homeScore) {
		this.homeScore = homeScore;
	}

	public Integer getAwayScore() {
		return awayScore;
	}

	public void setAwayScore(Integer awayScore) {
		this.awayScore = awayScore;
	}
}