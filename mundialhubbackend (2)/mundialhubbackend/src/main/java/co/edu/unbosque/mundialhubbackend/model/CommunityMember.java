package co.edu.unbosque.mundialhubbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_member", uniqueConstraints = @UniqueConstraint(columnNames = { "community_id", "user_id" }))
public class CommunityMember {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id", nullable = false)
	private Community community;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/**
	 * OWNER → creador, puede generar invitaciones y cerrar la comunidad MEMBER →
	 * participante regular
	 */
	@Enumerated(EnumType.STRING)
	private CommunityRole role;

	private LocalDateTime joinedAt;

	public enum CommunityRole {
		OWNER, MEMBER
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public CommunityRole getRole() {
		return role;
	}

	public void setRole(CommunityRole role) {
		this.role = role;
	}

	public LocalDateTime getJoinedAt() {
		return joinedAt;
	}

	public void setJoinedAt(LocalDateTime joinedAt) {
		this.joinedAt = joinedAt;
	}

}