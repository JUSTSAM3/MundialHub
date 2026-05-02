package co.edu.unbosque.mundialhubbackend.dto;

public class FixtureEventDTO {

	private Long fixtureId;
	private Integer elapsed;
	private Integer elapsedExtra;
	private Long teamId;
	private String teamName;
	private String playerName;
	private String assistName;
	private String type;
	private String detail;
	private String comments;

	public Long getFixtureId() {
		return fixtureId;
	}

	public void setFixtureId(Long fixtureId) {
		this.fixtureId = fixtureId;
	}

	public Integer getElapsed() {
		return elapsed;
	}

	public void setElapsed(Integer elapsed) {
		this.elapsed = elapsed;
	}

	public Integer getElapsedExtra() {
		return elapsedExtra;
	}

	public void setElapsedExtra(Integer elapsedExtra) {
		this.elapsedExtra = elapsedExtra;
	}

	public Long getTeamId() {
		return teamId;
	}

	public void setTeamId(Long teamId) {
		this.teamId = teamId;
	}

	public String getTeamName() {
		return teamName;
	}

	public void setTeamName(String teamName) {
		this.teamName = teamName;
	}

	public String getPlayerName() {
		return playerName;
	}

	public void setPlayerName(String playerName) {
		this.playerName = playerName;
	}

	public String getAssistName() {
		return assistName;
	}

	public void setAssistName(String assistName) {
		this.assistName = assistName;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getDetail() {
		return detail;
	}

	public void setDetail(String detail) {
		this.detail = detail;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}

}