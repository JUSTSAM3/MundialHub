package co.edu.unbosque.mundialhubbackend.dto;

import co.edu.unbosque.mundialhubbackend.model.Poll.PollStatus;

import java.time.LocalDateTime;
import java.util.List;

public class PollDTO {

	private Long id;
	private String name;
	private String inviteCode;
	private String creatorUsername;
	private PollStatus status;
	private LocalDateTime createdAt;
	private LocalDateTime finishedAt;
	private int memberCount;

	// Solo se incluye cuando se pide el detalle de una polla
	private List<PollMemberDTO> members;

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

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}

	public String getCreatorUsername() {
		return creatorUsername;
	}

	public void setCreatorUsername(String creatorUsername) {
		this.creatorUsername = creatorUsername;
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

	public int getMemberCount() {
		return memberCount;
	}

	public void setMemberCount(int memberCount) {
		this.memberCount = memberCount;
	}

	public List<PollMemberDTO> getMembers() {
		return members;
	}

	public void setMembers(List<PollMemberDTO> members) {
		this.members = members;
	}

}