package co.edu.unbosque.mundialhubbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "community_invite")
public class CommunityInvite {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String token;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "community_id", nullable = false)
	private Community community;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_id", nullable = false)
	private User createdBy;

	private LocalDateTime createdAt;
	private LocalDateTime expiresAt;

	/**
	 * null = usos ilimitados > 0 = se desactiva cuando currentUses alcanza maxUses
	 */
	private Integer maxUses;
	private Integer currentUses;

	/**
	 * false cuando el token fue revocado manualmente o agotó sus usos
	 */
	private boolean active;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public Community getCommunity() {
		return community;
	}

	public void setCommunity(Community community) {
		this.community = community;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public Integer getMaxUses() {
		return maxUses;
	}

	public void setMaxUses(Integer maxUses) {
		this.maxUses = maxUses;
	}

	public Integer getCurrentUses() {
		return currentUses;
	}

	public void setCurrentUses(Integer currentUses) {
		this.currentUses = currentUses;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}