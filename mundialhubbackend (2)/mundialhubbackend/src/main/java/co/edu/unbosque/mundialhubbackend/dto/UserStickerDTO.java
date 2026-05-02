package co.edu.unbosque.mundialhubbackend.dto;

import java.time.LocalDateTime;

public class UserStickerDTO {

	private Long id;
	private StickerDTO sticker;

	private Integer quantity;
	private Integer offeredForExchange;

	// Calculado: quantity > 1
	private boolean hasDuplicates;

	// Calculado: quantity >= 1
	private boolean inAlbum;

	private LocalDateTime firstObtainedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public StickerDTO getSticker() {
		return sticker;
	}

	public void setSticker(StickerDTO sticker) {
		this.sticker = sticker;
	}

	public Integer getQuantity() {
		return quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Integer getOfferedForExchange() {
		return offeredForExchange;
	}

	public void setOfferedForExchange(Integer offeredForExchange) {
		this.offeredForExchange = offeredForExchange;
	}

	public boolean isHasDuplicates() {
		return hasDuplicates;
	}

	public void setHasDuplicates(boolean hasDuplicates) {
		this.hasDuplicates = hasDuplicates;
	}

	public boolean isInAlbum() {
		return inAlbum;
	}

	public void setInAlbum(boolean inAlbum) {
		this.inAlbum = inAlbum;
	}

	public LocalDateTime getFirstObtainedAt() {
		return firstObtainedAt;
	}

	public void setFirstObtainedAt(LocalDateTime firstObtainedAt) {
		this.firstObtainedAt = firstObtainedAt;
	}

}