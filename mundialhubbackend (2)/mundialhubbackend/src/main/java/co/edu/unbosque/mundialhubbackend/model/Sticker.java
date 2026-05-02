package co.edu.unbosque.mundialhubbackend.model;

import jakarta.persistence.*;

@Entity
@Table(name = "sticker")
public class Sticker {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false)
	private String name;

	/**
	 * Sección del álbum donde aparece esta lámina. Ejemplos: "Argentina", "Estadio
	 * Azteca", "Figuras del torneo"
	 */
	private String section;

	/**
	 * COMMON → 60 % de probabilidad en un paquete RARE → 30 % EPIC → 9 % LEGEND → 1
	 * %
	 */
	@Enumerated(EnumType.STRING)
	private StickerRarity rarity;

	/**
	 * Categoría de la lámina. PLAYER → jugador de una selección STADIUM → estadio
	 * sede del torneo TROPHY → copa y objetos del torneo BADGE → escudo de
	 * selección
	 */
	@Enumerated(EnumType.STRING)
	private StickerCategory category;

	/**
	 * URL del avatar/ilustración. Nunca fotografías de personas reales (RNF-15).
	 */
	private String imageUrl;

	// Descripción corta visible en el álbum
	private String description;

	// Número de lámina dentro del álbum (ej: 042)
	private Integer albumNumber;

	private boolean active;

	public enum StickerRarity {
		COMMON, RARE, EPIC, LEGEND
	}

	public enum StickerCategory {
		PLAYER, STADIUM, TROPHY, BADGE
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}

	public StickerRarity getRarity() {
		return rarity;
	}

	public void setRarity(StickerRarity rarity) {
		this.rarity = rarity;
	}

	public StickerCategory getCategory() {
		return category;
	}

	public void setCategory(StickerCategory category) {
		this.category = category;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getAlbumNumber() {
		return albumNumber;
	}

	public void setAlbumNumber(Integer albumNumber) {
		this.albumNumber = albumNumber;
	}

	public boolean isActive() {
		return active;
	}

	public void setActive(boolean active) {
		this.active = active;
	}

}