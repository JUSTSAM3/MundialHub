package co.edu.unbosque.mundialhubbackend.dto;

import java.time.LocalDateTime;

public class PredictionDTO {

	private Long id;
	private Long pollMemberId;
	private Long fixtureId;
	private String homeTeamName;
	private String awayTeamName;

	private Integer predictedHomeGoals;
	private Integer predictedAwayGoals;

	// Resultado real (null si el partido no terminó)
	private Integer actualHomeGoals;
	private Integer actualAwayGoals;

	private Integer pointsEarned;
	private boolean closed;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getPollMemberId() {
		return pollMemberId;
	}

	public void setPollMemberId(Long pollMemberId) {
		this.pollMemberId = pollMemberId;
	}

	public Long getFixtureId() {
		return fixtureId;
	}

	public void setFixtureId(Long fixtureId) {
		this.fixtureId = fixtureId;
	}

	public String getHomeTeamName() {
		return homeTeamName;
	}

	public void setHomeTeamName(String homeTeamName) {
		this.homeTeamName = homeTeamName;
	}

	public String getAwayTeamName() {
		return awayTeamName;
	}

	public void setAwayTeamName(String awayTeamName) {
		this.awayTeamName = awayTeamName;
	}

	public Integer getPredictedHomeGoals() {
		return predictedHomeGoals;
	}

	public void setPredictedHomeGoals(Integer predictedHomeGoals) {
		this.predictedHomeGoals = predictedHomeGoals;
	}

	public Integer getPredictedAwayGoals() {
		return predictedAwayGoals;
	}

	public void setPredictedAwayGoals(Integer predictedAwayGoals) {
		this.predictedAwayGoals = predictedAwayGoals;
	}

	public Integer getActualHomeGoals() {
		return actualHomeGoals;
	}

	public void setActualHomeGoals(Integer actualHomeGoals) {
		this.actualHomeGoals = actualHomeGoals;
	}

	public Integer getActualAwayGoals() {
		return actualAwayGoals;
	}

	public void setActualAwayGoals(Integer actualAwayGoals) {
		this.actualAwayGoals = actualAwayGoals;
	}

	public Integer getPointsEarned() {
		return pointsEarned;
	}

	public void setPointsEarned(Integer pointsEarned) {
		this.pointsEarned = pointsEarned;
	}

	public boolean isClosed() {
		return closed;
	}

	public void setClosed(boolean closed) {
		this.closed = closed;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getUpdatedAt() {
		return updatedAt;
	}

	public void setUpdatedAt(LocalDateTime updatedAt) {
		this.updatedAt = updatedAt;
	}

}