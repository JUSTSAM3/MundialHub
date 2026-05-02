package co.edu.unbosque.mundialhubbackend.dto;

import co.edu.unbosque.mundialhubbackend.model.StickerExchange.ExchangeStatus;

import java.time.LocalDateTime;

public class StickerExchangeDTO {

	private Long id;

	private String proposerUsername;
	private String receiverUsername;

	private StickerDTO offeredSticker;
	private StickerDTO requestedSticker;

	private ExchangeStatus status;

	private LocalDateTime proposedAt;
	private LocalDateTime respondedAt;
	private LocalDateTime expiresAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getProposerUsername() {
		return proposerUsername;
	}

	public void setProposerUsername(String proposerUsername) {
		this.proposerUsername = proposerUsername;
	}

	public String getReceiverUsername() {
		return receiverUsername;
	}

	public void setReceiverUsername(String receiverUsername) {
		this.receiverUsername = receiverUsername;
	}

	public StickerDTO getOfferedSticker() {
		return offeredSticker;
	}

	public void setOfferedSticker(StickerDTO offeredSticker) {
		this.offeredSticker = offeredSticker;
	}

	public StickerDTO getRequestedSticker() {
		return requestedSticker;
	}

	public void setRequestedSticker(StickerDTO requestedSticker) {
		this.requestedSticker = requestedSticker;
	}

	public ExchangeStatus getStatus() {
		return status;
	}

	public void setStatus(ExchangeStatus status) {
		this.status = status;
	}

	public LocalDateTime getProposedAt() {
		return proposedAt;
	}

	public void setProposedAt(LocalDateTime proposedAt) {
		this.proposedAt = proposedAt;
	}

	public LocalDateTime getRespondedAt() {
		return respondedAt;
	}

	public void setRespondedAt(LocalDateTime respondedAt) {
		this.respondedAt = respondedAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

}