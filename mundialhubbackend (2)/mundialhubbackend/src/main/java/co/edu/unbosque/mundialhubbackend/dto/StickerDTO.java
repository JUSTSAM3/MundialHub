package co.edu.unbosque.mundialhubbackend.dto;

import co.edu.unbosque.mundialhubbackend.model.Sticker.StickerCategory;
import co.edu.unbosque.mundialhubbackend.model.Sticker.StickerRarity;

public class StickerDTO {

	private Long id;
	private String name;
	private String section;
	private StickerRarity rarity;
	private StickerCategory category;
	private String imageUrl;
	private String description;
	private Integer albumNumber;

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

}