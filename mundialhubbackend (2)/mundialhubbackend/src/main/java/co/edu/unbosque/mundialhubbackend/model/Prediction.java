package co.edu.unbosque.mundialhubbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "prediction",
		// Un miembro solo puede tener un pronóstico por partido dentro de una polla
		uniqueConstraints = @UniqueConstraint(columnNames = { "poll_member_id", "fixture_id" }))
public class Prediction {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "poll_member_id", nullable = false)
	private PollMember pollMember;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "fixture_id", nullable = false)
	private Fixture fixture;

	private Integer predictedHomeGoals;
	private Integer predictedAwayGoals;

	/**
	 * Puntos ganados en este pronóstico. null hasta que el partido termina. Lógica
	 * de puntuación: 3 pts → marcador exacto 1 pt → ganador / empate correcto 0 pts
	 * → fallo total
	 */
	private Integer pointsEarned;

	/**
	 * true cuando el partido ya inició. Desde ese momento el pronóstico no puede
	 * modificarse (HU-18 — cierre automático).
	 */
	private boolean closed;

	private LocalDateTime createdAt;
	private LocalDateTime updatedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PollMember getPollMember() {
		return pollMember;
	}

	public void setPollMember(PollMember pollMember) {
		this.pollMember = pollMember;
	}

	public Fixture getFixture() {
		return fixture;
	}

	public void setFixture(Fixture fixture) {
		this.fixture = fixture;
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