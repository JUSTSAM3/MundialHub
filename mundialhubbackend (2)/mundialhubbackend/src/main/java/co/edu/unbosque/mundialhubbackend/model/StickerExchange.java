package co.edu.unbosque.mundialhubbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "sticker_exchange")
public class StickerExchange {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	// Quien propone el intercambio
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "proposer_id", nullable = false)
	private User proposer;

	// Quien recibe la propuesta
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "receiver_id", nullable = false)
	private User receiver;

	// Lámina que el proponente ofrece
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "offered_sticker_id", nullable = false)
	private Sticker offeredSticker;

	// Lámina que el proponente quiere recibir a cambio
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "requested_sticker_id", nullable = false)
	private Sticker requestedSticker;

	/**
	 * PENDING → propuesta enviada, esperando respuesta ACCEPTED → el receptor
	 * aceptó, láminas intercambiadas REJECTED → el receptor rechazó CANCELLED→ el
	 * proponente canceló antes de respuesta EXPIRED → no respondida dentro del
	 * tiempo límite
	 */
	@Enumerated(EnumType.STRING)
	private ExchangeStatus status;

	private LocalDateTime proposedAt;
	private LocalDateTime respondedAt;

	// El intercambio expira automáticamente a las 48 h de ser propuesto
	private LocalDateTime expiresAt;

	public enum ExchangeStatus {
		PENDING, ACCEPTED, REJECTED, CANCELLED, EXPIRED
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getProposer() {
		return proposer;
	}

	public void setProposer(User proposer) {
		this.proposer = proposer;
	}

	public User getReceiver() {
		return receiver;
	}

	public void setReceiver(User receiver) {
		this.receiver = receiver;
	}

	public Sticker getOfferedSticker() {
		return offeredSticker;
	}

	public void setOfferedSticker(Sticker offeredSticker) {
		this.offeredSticker = offeredSticker;
	}

	public Sticker getRequestedSticker() {
		return requestedSticker;
	}

	public void setRequestedSticker(Sticker requestedSticker) {
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