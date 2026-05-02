package co.edu.unbosque.mundialhubbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "poll_member",
		// Un usuario solo puede estar una vez en cada polla
		uniqueConstraints = @UniqueConstraint(columnNames = { "poll_id", "user_id" }))
public class PollMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "poll_id", nullable = false)
	private Poll poll;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	// Puntos acumulados a través de todos los partidos de la polla
	private Integer totalPoints;

	// Posición en el ranking (se recalcula al actualizar puntos)
	private Integer rankingPosition;

	private LocalDateTime joinedAt;

	@OneToMany(mappedBy = "pollMember", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Prediction> predictions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Poll getPoll() {
		return poll;
	}

	public void setPoll(Poll poll) {
		this.poll = poll;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public List<Prediction> getPredictions() {
		return predictions;
	}

	public void setPredictions(List<Prediction> predictions) {
		this.predictions = predictions;
	}

}