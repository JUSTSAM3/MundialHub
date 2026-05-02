package co.edu.unbosque.mundialhubbackend.util;

public class ProposeExchangeRequest {

	private String receiverUsername;
	private Long offeredStickerId;
	private Long requestedStickerId;

	public ProposeExchangeRequest() {
	}

	public String getReceiverUsername() {
		return receiverUsername;
	}

	public void setReceiverUsername(String receiverUsername) {
		this.receiverUsername = receiverUsername;
	}

	public Long getOfferedStickerId() {
		return offeredStickerId;
	}

	public void setOfferedStickerId(Long offeredStickerId) {
		this.offeredStickerId = offeredStickerId;
	}

	public Long getRequestedStickerId() {
		return requestedStickerId;
	}

	public void setRequestedStickerId(Long requestedStickerId) {
		this.requestedStickerId = requestedStickerId;
	}
}