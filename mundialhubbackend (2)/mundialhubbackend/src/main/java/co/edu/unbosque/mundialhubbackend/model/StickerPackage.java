package co.edu.unbosque.mundialhubbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "sticker_package")
public class StickerPackage {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	/**
	 * Cómo se obtuvo este paquete: DAILY_LOGIN → inicio de sesión diario
	 * PREDICTION_COMPLETE → completó pronóstico en una polla FOLLOW_LIVE → siguió
	 * un partido en vivo PROMO_CODE → canjeó un código promocional POLL_WINNER →
	 * ganó una polla (HU-20)
	 */
	@Enumerated(EnumType.STRING)
	private PackageSource source;

	/**
	 * PENDING → el usuario aún no lo abrió OPENED → ya fue abierto y las láminas se
	 * asignaron
	 */
	@Enumerated(EnumType.STRING)
	private PackageStatus status;

	// Cantidad de láminas que contiene este paquete (default 5)
	private Integer stickerCount;

	private LocalDateTime awardedAt;
	private LocalDateTime openedAt;

	/**
	 * Láminas que se revelaron al abrir este paquete. Se guarda para mostrar el
	 * historial de apertura (HU-23).
	 */
	@ManyToMany
	@JoinTable(name = "package_revealed_stickers", joinColumns = @JoinColumn(name = "package_id"), inverseJoinColumns = @JoinColumn(name = "sticker_id"))
	private List<Sticker> revealedStickers;

	public enum PackageSource {
		DAILY_LOGIN, PREDICTION_COMPLETE, FOLLOW_LIVE, PROMO_CODE, POLL_WINNER
	}

	public enum PackageStatus {
		PENDING, OPENED
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
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

	public List<Sticker> getRevealedStickers() {
		return revealedStickers;
	}

	public void setRevealedStickers(List<Sticker> revealedStickers) {
		this.revealedStickers = revealedStickers;
	}

}