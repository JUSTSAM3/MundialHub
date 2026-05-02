package co.edu.unbosque.mundialhubbackend.dto;

import co.edu.unbosque.mundialhubbackend.model.CommunityMember.CommunityRole;

import java.time.LocalDateTime;

public class CommunityMemberDTO {

	private Long id;
	private Long userId;
	private String username;
	private String name;
	private CommunityRole role;
	private LocalDateTime joinedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
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