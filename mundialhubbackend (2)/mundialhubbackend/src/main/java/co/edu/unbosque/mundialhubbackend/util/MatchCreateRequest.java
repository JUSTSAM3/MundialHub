package co.edu.unbosque.mundialhubbackend.util;

import java.time.LocalDateTime;

public class MatchCreateRequest {
	private Long homeTeamId;
	private Long awayTeamId;
	private Long stadiumId;
	private LocalDateTime matchDate;

	public MatchCreateRequest() {
	}

	public Long getHomeTeamId() {
		return homeTeamId;
	}

	public void setHomeTeamId(Long homeTeamId) {
		this.homeTeamId = homeTeamId;
	}

	public Long getAwayTeamId() {
		return awayTeamId;
	}

	public void setAwayTeamId(Long awayTeamId) {
		this.awayTeamId = awayTeamId;
	}

	public Long getStadiumId() {
		return stadiumId;
	}

	public void setStadiumId(Long stadiumId) {
		this.stadiumId = stadiumId;
	}

	public LocalDateTime getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(LocalDateTime matchDate) {
		this.matchDate = matchDate;
	}
}