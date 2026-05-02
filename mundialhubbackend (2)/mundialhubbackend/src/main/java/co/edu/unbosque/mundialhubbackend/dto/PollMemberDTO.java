package co.edu.unbosque.mundialhubbackend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class PollMemberDTO {

	private Long pollMemberId;
	private Long userId;
	private String username;
	private String name;

	private Integer totalPoints;
	private Integer rankingPosition;

	private LocalDateTime joinedAt;

	// Solo se incluye cuando se pide el detalle de pronósticos de un partido
	private List<PredictionDTO> predictions;

	public Long getPollMemberId() {
		return pollMemberId;
	}

	public void setPollMemberId(Long pollMemberId) {
		this.pollMemberId = pollMemberId;
	}

	public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Integer getTotalPoints() {
		return totalPoints;
	}

	public void setTotalPoints(Integer totalPoints) {
		this.totalPoints = totalPoints;
	}

	public Integer getRankingPosition() {
		return rankingPosition;
	}

	public void setRankingPosition(Integer rankingPosition) {
		this.rankingPosition = rankingPosition;
	}

	public LocalDateTime getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(LocalDateTime joinedAt) {
		this.joinedAt = joinedAt;
	}

	public List<PredictionDTO> getPredictions() {
		return predictions;
	}

	public void setPredictions(List<PredictionDTO> predictions) {
		this.predictions = predictions;
	}

}