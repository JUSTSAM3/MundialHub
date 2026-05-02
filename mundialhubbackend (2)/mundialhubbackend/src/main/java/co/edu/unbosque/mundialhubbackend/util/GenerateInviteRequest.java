package co.edu.unbosque.mundialhubbackend.util;

public class GenerateInviteRequest {

	private Long communityId;

	/**
	 * Cantidad máxima de personas que pueden usar este enlace. null = ilimitado.
	 */
	private Integer maxUses;

	/**
	 * Días hasta que el enlace vence. null = 7 días por defecto.
	 */
	private Integer expireDays;

	public GenerateInviteRequest() {
	}

	public Long getCommunityId() {
		return communityId;
	}

	public void setCommunityId(Long communityId) {
		this.communityId = communityId;
	}

	public Integer getMaxUses() {
		return maxUses;
	}

	public void setMaxUses(Integer maxUses) {
		this.maxUses = maxUses;
	}

	public Integer getExpireDays() {
		return expireDays;
	}

	public void setExpireDays(Integer expireDays) {
		this.expireDays = expireDays;
	}
}