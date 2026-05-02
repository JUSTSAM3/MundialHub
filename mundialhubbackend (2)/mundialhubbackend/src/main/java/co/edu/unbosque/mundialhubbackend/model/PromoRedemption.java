package co.edu.unbosque.mundialhubbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "promo_redemption", uniqueConstraints = @UniqueConstraint(columnNames = { "code_id", "user_id" }))
public class PromoRedemption {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "code_id", nullable = false)
	private PromotionalCode code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "user_id", nullable = false)
	private User user;

	private LocalDateTime redeemedAt;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public PromotionalCode getCode() {
		return code;
	}

	public void setCode(PromotionalCode code) {
		this.code = code;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getRedeemedAt() {
		return redeemedAt;
	}

	public void setRedeemedAt(LocalDateTime redeemedAt) {
		this.redeemedAt = redeemedAt;
	}

}