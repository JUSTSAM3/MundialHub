package co.edu.unbosque.mundialhubbackend.util;

public class JoinPollRequest {

	/**
	 * Código de 8 caracteres que identifica la polla dentro de la comunidad.
	 * Distinto al token de invitación de la comunidad.
	 */
	private String inviteCode;

	public JoinPollRequest() {
	}

	public String getInviteCode() {
		return inviteCode;
	}

	public void setInviteCode(String inviteCode) {
		this.inviteCode = inviteCode;
	}
}