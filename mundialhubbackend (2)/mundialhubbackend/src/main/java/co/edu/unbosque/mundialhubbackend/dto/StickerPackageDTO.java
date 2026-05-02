package co.edu.unbosque.mundialhubbackend.dto;

import co.edu.unbosque.mundialhubbackend.model.StickerPackage.PackageSource;
import co.edu.unbosque.mundialhubbackend.model.StickerPackage.PackageStatus;

import java.time.LocalDateTime;
import java.util.List;

public class StickerPackageDTO {

	private Long id;
	private PackageSource source;
	private PackageStatus status;
	private Integer stickerCount;
	private LocalDateTime awardedAt;
	private LocalDateTime openedAt;

	// Solo se incluye cuando el paquete acaba de abrirse (HU-23)
	private List<StickerDTO> revealedStickers;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PackageSource getSource() {
		return source;
	}

	public void setSource(PackageSource source) {
		this.source = source;
	}

	public PackageStatus getStatus() {
		return status;
	}

	public void setStatus(PackageStatus status) {
		this.status = status;
	}

	public Integer getStickerCount() {
		return stickerCount;
	}

	public void setStickerCount(Integer stickerCount) {
		this.stickerCount = stickerCount;
	}

	public LocalDateTime getAwardedAt() {
		return awardedAt;
	}

	public void setAwardedAt(LocalDateTime awardedAt) {
		this.awardedAt = awardedAt;
	}

	public LocalDateTime getOpenedAt() {
		return openedAt;
	}

	public void setOpenedAt(LocalDateTime openedAt) {
		this.openedAt = openedAt;
	}

	public List<StickerDTO> getRevealedStickers() {
		return revealedStickers;
	}

	public void setRevealedStickers(List<StickerDTO> revealedStickers) {
		this.revealedStickers = revealedStickers;
	}

}