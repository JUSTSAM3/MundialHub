package co.edu.unbosque.mundialhubbackend.util;

/**
 * Clase que encapsula la solicitud de actualización de cuenta, conteniendo un
 * objeto UserDTO con la información antigua y otro con la nueva.
 * 
 * @since 1.0
 */
public class AccountUpdateRequest {

	private ParameterEmailRequest oldOne;
	private RegisterRequest newOne;

	/**
	 * Obtiene el DTO con los datos antiguos.
	 * 
	 * @return DTO con datos antiguos
	 */
	public ParameterEmailRequest getOldOne() {
		return oldOne;
	}

	/**
	 * Establece el DTO con los datos antiguos.
	 * 
	 * @param oldOne DTO con datos antiguos
	 */
	public void setOldOne(ParameterEmailRequest oldOne) {
		this.oldOne = oldOne;
	}

	/**
	 * Obtiene el DTO con los datos nuevos.
	 * 
	 * @return DTO con datos nuevos
	 */
	public RegisterRequest getNewOne() {
		return newOne;
	}

	/**
	 * Establece el DTO con los datos nuevos.
	 * 
	 * @param newOne DTO con datos nuevos
	 */
	public void setNewOne(RegisterRequest newOne) {
		this.newOne = newOne;
	}

}
