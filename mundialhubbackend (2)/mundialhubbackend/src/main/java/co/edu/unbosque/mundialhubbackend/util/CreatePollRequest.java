package co.edu.unbosque.mundialhubbackend.util;

public class CreatePollRequest {

	private String name;
	private Long communityId;

	public CreatePollRequest() {
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public Long getCommunityId() {
		return communityId;
	}

	public void setCommunityId(Long communityId) {
		this.communityId = communityId;
	}
}