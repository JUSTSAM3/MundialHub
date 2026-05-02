package co.edu.unbosque.mundialhubbackend.util;

public class RemoveMemberRequest {

	private Long communityId;
	private String targetUsername;

	public RemoveMemberRequest() {
	}

	public Long getCommunityId() {
		return communityId;
	}

	public void setCommunityId(Long communityId) {
		this.communityId = communityId;
	}

	public String getTargetUsername() {
		return targetUsername;
	}

	public void setTargetUsername(String targetUsername) {
		this.targetUsername = targetUsername;
	}
}