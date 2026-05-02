package co.edu.unbosque.mundialhubbackend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class FixtureDTO {

	private Long id;

	// Liga
	private Long leagueId;
	private String leagueName;
	private String leagueLogo;
	private String leagueRound;
	private Integer season;

	// Equipos
	private Long homeTeamId;
	private String homeTeamName;
	private String homeTeamLogo;

	private Long awayTeamId;
	private String awayTeamName;
	private String awayTeamLogo;

	// Resultado
	private Integer homeGoals;
	private Integer awayGoals;

	// Info
	private LocalDateTime matchDate;
	private String venue;
	private String city;
	private String referee;
	private String status;
	private Integer elapsed;

	// Etiqueta que indica si el dato está desactualizado (RNF-04)
	private boolean pendingUpdate;

	private List<FixtureEventDTO> events;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getLeagueId() {
		return leagueId;
	}

	public void setLeagueId(Long leagueId) {
		this.leagueId = leagueId;
	}

	public String getLeagueName() {
		return leagueName;
	}

	public void setLeagueName(String leagueName) {
		this.leagueName = leagueName;
	}

	public String getLeagueLogo() {
		return leagueLogo;
	}

	public void setLeagueLogo(String leagueLogo) {
		this.leagueLogo = leagueLogo;
	}

	public String getLeagueRound() {
		return leagueRound;
	}

	public void setLeagueRound(String leagueRound) {
		this.leagueRound = leagueRound;
	}

	public Integer getSeason() {
		return season;
	}

	public void setSeason(Integer season) {
		this.season = season;
	}

	public Long getHomeTeamId() {
		return homeTeamId;
	}

	public void setHomeTeamId(Long homeTeamId) {
		this.homeTeamId = homeTeamId;
	}

	public String getHomeTeamName() {
		return homeTeamName;
	}

	public void setHomeTeamName(String homeTeamName) {
		this.homeTeamName = homeTeamName;
	}

	public String getHomeTeamLogo() {
		return homeTeamLogo;
	}

	public void setHomeTeamLogo(String homeTeamLogo) {
		this.homeTeamLogo = homeTeamLogo;
	}

	public Long getAwayTeamId() {
		return awayTeamId;
	}

	public void setAwayTeamId(Long awayTeamId) {
		this.awayTeamId = awayTeamId;
	}

	public String getAwayTeamName() {
		return awayTeamName;
	}

	public void setAwayTeamName(String awayTeamName) {
		this.awayTeamName = awayTeamName;
	}

	public String getAwayTeamLogo() {
		return awayTeamLogo;
	}

	public void setAwayTeamLogo(String awayTeamLogo) {
		this.awayTeamLogo = awayTeamLogo;
	}

	public Integer getHomeGoals() {
		return homeGoals;
	}

	public void setHomeGoals(Integer homeGoals) {
		this.homeGoals = homeGoals;
	}

	public Integer getAwayGoals() {
		return awayGoals;
	}

	public void setAwayGoals(Integer awayGoals) {
		this.awayGoals = awayGoals;
	}

	public LocalDateTime getMatchDate() {
		return matchDate;
	}

	public void setMatchDate(LocalDateTime matchDate) {
		this.matchDate = matchDate;
	}

	public String getVenue() {
		return venue;
	}

	public void setVenue(String venue) {
		this.venue = venue;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getReferee() {
		return referee;
	}

	public void setReferee(String referee) {
		this.referee = referee;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public Integer getElapsed() {
		return elapsed;
	}

	public void setElapsed(Integer elapsed) {
		this.elapsed = elapsed;
	}

	public boolean isPendingUpdate() {
		return pendingUpdate;
	}

	public void setPendingUpdate(boolean pendingUpdate) {
		this.pendingUpdate = pendingUpdate;
	}

	public List<FixtureEventDTO> getEvents() {
		return events;
	}

	public void setEvents(List<FixtureEventDTO> events) {
		this.events = events;
	}

}