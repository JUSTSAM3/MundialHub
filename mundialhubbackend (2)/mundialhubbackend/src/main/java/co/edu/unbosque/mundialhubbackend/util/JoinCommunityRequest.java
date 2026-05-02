package co.edu.unbosque.mundialhubbackend.util;

public class JoinCommunityRequest {

	/**
	 * Token UUID que viene en el enlace de invitación. El frontend lo extrae de la
	 * URL y lo envía aquí en el body.
	 */
	private String token;

	public JoinCommunityRequest() {
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}
}