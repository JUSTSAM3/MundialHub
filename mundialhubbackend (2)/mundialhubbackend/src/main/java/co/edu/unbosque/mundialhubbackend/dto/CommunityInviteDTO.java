package co.edu.unbosque.mundialhubbackend.dto;

import java.time.LocalDateTime;

public class CommunityInviteDTO {

	private Long id;
	private Long communityId;
	private String communityName;

	/**
	 * Solo si tenemos dominio
	 * URL completa lista para compartir por WhatsApp, Telegram, etc. Ejemplo:
	 * https://mundialhub.app/communities/join/550e8400-e29b-41d4-a716
	 */
	private String inviteUrl;

	private String token;
	private String createdByUsername;
	private Integer maxUses;
	private Integer currentUses;
	private boolean active;

	private LocalDateTime createdAt;
	private LocalDateTime expiresAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getCommunityId() {
		return communityId;
	}

	public void setCommunityId(Long communityId) {
		this.communityId = communityId;
	}

	public String getCommunityName() {
		return communityName;
	}

	public void setCommunityName(String communityName) {
		this.communityName = communityName;
	}

	public String getInviteUrl() {
		return inviteUrl;
	}

	public void setInviteUrl(String inviteUrl) {
		this.inviteUrl = inviteUrl;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getCreatedByUsername() {
		return createdByUsername;
	}

	public void setCreatedByUsername(String createdByUsername) {
		this.createdByUsername = createdByUsername;
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

}