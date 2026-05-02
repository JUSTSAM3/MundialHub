package co.edu.unbosque.mundialhubbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "user_sticker", uniqueConstraints = @UniqueConstraint(columnNames = { "user_id", "sticker_id" }))
public class UserSticker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "sticker_id", nullable = false)
	private Sticker sticker;

	/**
	 * Cantidad total que tiene el usuario de esta lámina. quantity == 1 → pegada en
	 * el álbum, no repetida. quantity > 1 → tiene (quantity - 1) repetidas
	 * disponibles para intercambio.
	 */
	private Integer quantity;

	/**
	 * Cuántas de las repetidas el usuario marcó como disponibles para intercambio.
	 * No puede superar (quantity - 1).
	 */
	private Integer offeredForExchange;

	// Fecha en que el usuario obtuvo esta lámina por primera vez
	private LocalDateTime firstObtainedAt;

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

	public Sticker getSticker() {
		return sticker;
	}

	public void setSticker(Sticker sticker) {
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

	public LocalDateTime getFirstObtainedAt() {
		return firstObtainedAt;
	}

	public void setFirstObtainedAt(LocalDateTime firstObtainedAt) {
		this.firstObtainedAt = firstObtainedAt;
	}

}