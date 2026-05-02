package co.edu.unbosque.mundialhubbackend.util;

/**
 * Clase que representa la solicitud para confirmar el correo electrónico de un
 * usuario. Contiene el email y el código de confirmación.
 * 
 * @since 1.0
 */
public class ConfirmEmailRequest {

	private String email;
	private String code;

	/**
	 * Obtiene el correo electrónico asociado a la confirmación.
	 * 
	 * @return Correo electrónico
	 */
	public String getEmail() {
		return email;
	}

	/**
	 * Establece el correo electrónico asociado a la confirmación.
	 * 
	 * @param email Correo electrónico
	 */
	public void setEmail(String email) {
		this.email = email;
	}

	/**
	 * Obtiene el código de verificación enviado.
	 * 
	 * @return Código de verificación
	 */
	public String getCode() {
		return code;
	}

	/**
	 * Establece el código de verificación enviado.
	 * 
	 * @param code Código de verificación
	 */
	public void setCode(String code) {
		this.code = code;
	}

}
