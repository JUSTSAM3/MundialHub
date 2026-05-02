package co.edu.unbosque.mundialhubbackend.dto;

import java.time.LocalDateTime;

import co.edu.unbosque.mundialhubbackend.model.Match.MatchStatus;

public class MatchDTO {

	private Long id;
	private TeamDTO homeTeam;
	private TeamDTO awayTeam;
	private StadiumDTO stadium;
	private LocalDateTime matchDate;
	private MatchStatus status;
	private Integer homeScore;
	private Integer awayScore;

	public MatchDTO() {
	}

	// Getters y Setters

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public TeamDTO getHomeTeam() {
		return homeTeam;
	}

	public void setHomeTeam(TeamDTO homeTeam) {
		this.homeTeam = homeTeam;
	}

	public TeamDTO getAwayTeam() {
		return awayTeam;
	}

	public void setAwayTeam(TeamDTO awayTeam) {
		this.awayTeam = awayTeam;
	}

	public StadiumDTO getStadium() {
		return stadium;
	}

	public void setStadium(StadiumDTO stadium) {
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