package co.edu.unbosque.mundialhubbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "fixture_event")
public class FixtureEvent {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fixture_id", nullable = false)
	private Fixture fixture;

	private Integer elapsed;
	private Integer elapsedExtra;

	private Long teamId;
	private String teamName;

	private String playerName;
	private String assistName;

	/**
	 * type → "Goal" | "Card" | "subst" | "Var" detail → "Normal Goal" | "Yellow
	 * Card" | "Red Card" | "Penalty" | etc.
	 */
	private String type;
	private String detail;
	private String comments;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Fixture getFixture() {
		return fixture;
	}

	public void setFixture(Fixture fixture) {
		this.fixture = fixture;
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