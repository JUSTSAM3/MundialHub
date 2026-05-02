package co.edu.unbosque.mundialhubbackend.util;

public class AlbumDetailRequest {

	/**
	 * Si viene null se devuelve el álbum completo. Si viene un valor se filtra solo
	 * esa sección.
	 */
	private String section;

	public AlbumDetailRequest() {
	}

	public String getSection() {
		return section;
	}

	public void setSection(String section) {
		this.section = section;
	}
}