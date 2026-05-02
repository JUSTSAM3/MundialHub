package co.edu.unbosque.mundialhubbackend.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "promotional_code")
public class PromotionalCode {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String code;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "created_by_id")
	private User createdBy;

	private Integer maxUses;
	private Integer currentUses;
	private boolean active;

	private LocalDateTime createdAt;
	private LocalDateTime expiresAt;

	@OneToMany(mappedBy = "code", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<PromoRedemption> redemptions;

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}

	public User getCreatedBy() {
		return createdBy;
	}

	public void setCreatedBy(User createdBy) {
		this.createdBy = createdBy;
	}

	public Integer getMaxUses() {
		return maxUses;
	}

	public void setMaxUses(Integer maxUses) {
		this.maxUses = maxUses;
	}

	public Integer getCurrentUses() {
		return currentUses;
	}

	public void setCurrentUses(Integer currentUses) {
		this.currentUses = currentUses;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

	public LocalDateTime getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(LocalDateTime createdAt) {
		this.createdAt = createdAt;
	}

	public LocalDateTime getExpiresAt() {
		return expiresAt;
	}

	public void setExpiresAt(LocalDateTime expiresAt) {
		this.expiresAt = expiresAt;
	}

	public List<PromoRedemption> getRedemptions() {
		return redemptions;
	}

	public void setRedemptions(List<PromoRedemption> redemptions) {
		this.redemptions = redemptions;
	}

}