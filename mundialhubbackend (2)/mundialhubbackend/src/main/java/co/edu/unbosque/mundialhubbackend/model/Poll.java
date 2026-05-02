package co.edu.unbosque.mundialhubbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "poll")
public class Poll {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	private String name;

	/**
	 * La polla siempre pertenece a una comunidad. Solo los miembros de esa
	 * comunidad pueden unirse.
	 */
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id", nullable = false)
	private Community community;

	private String inviteCode;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "creator_id", nullable = false)
	private User creator;

	@Enumerated(EnumType.STRING)
	private PollStatus status;

	private LocalDateTime createdAt;
	private LocalDateTime finishedAt;

	@OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PollMember> members;

	public enum PollStatus {
		ACTIVE, FINISHED
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public User getCreator() {
		return creator;
	}

	public void setCreator(User creator) {
		this.creator = creator;
	}

	public PollStatus getStatus() {
		return status;
	}

	public void setStatus(PollStatus status) {
		this.status = status;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getFinishedAt() {
		return finishedAt;
	}

	public void setFinishedAt(LocalDateTime finishedAt) {
		this.finishedAt = finishedAt;
	}

	public List<PollMember> getMembers() {
		return members;
	}

	public void setMembers(List<PollMember> members) {
		this.members = members;
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

}