package co.edu.unbosque.mundialhubbackend.dto;

public class TeamDTO {

	private Long id;
	private String name;
	private String flagUrl;
	private String groupName;

	public TeamDTO() {
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

	public String getFlagUrl() {
		return flagUrl;
	}

	public void setFlagUrl(String flagUrl) {
		this.flagUrl = flagUrl;
	}

	public String getGroupName() {
		return groupName;
	}

	public void setGroupName(String groupName) {
		this.groupName = groupName;
	}
}