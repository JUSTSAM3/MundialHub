package co.edu.unbosque.mundialhubbackend.dto;

import java.time.LocalDateTime;
import java.util.List;

public class CommunityDTO {

	private Long id;
	private String name;
	private String description;
	private String ownerUsername;

	private int memberCount;
	private int pollCount;

	private LocalDateTime createdAt;

	// Solo se incluye en la vista de detalle
	private List<CommunityMemberDTO> members;

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

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getOwnerUsername() {
		return ownerUsername;
	}

	public void setOwnerUsername(String ownerUsername) {
		this.ownerUsername = ownerUsername;
	}

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	public int getPollCount() {
		return pollCount;
	}

	public void setPollCount(int pollCount) {
		this.pollCount = pollCount;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public List<CommunityMemberDTO> getMembers() {
		return members;
	}

	public void setMembers(List<CommunityMemberDTO> members) {
		this.members = members;
	}

}