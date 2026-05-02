package co.edu.unbosque.mundialhubbackend.util;

public class RespondExchangeRequest {

	private Long exchangeId;

	/**
	 * true → acepta el intercambio false → lo rechaza
	 */
	private boolean accepted;

	public RespondExchangeRequest() {
	}

	public Long getExchangeId() {
		return exchangeId;
	}

	public void setExchangeId(Long exchangeId) {
		this.exchangeId = exchangeId;
	}

	public boolean isAccepted() {
		return accepted;
	}

	public void setAccepted(boolean accepted) {
		this.accepted = accepted;
	}
}