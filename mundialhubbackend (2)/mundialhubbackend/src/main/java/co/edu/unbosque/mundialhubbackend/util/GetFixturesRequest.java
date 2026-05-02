package co.edu.unbosque.mundialhubbackend.util;

public class GetFixturesRequest {

	/**
	 * Filtro opcional por fecha en formato YYYY-MM-DD. Si viene null se devuelve el
	 * calendario completo.
	 */
	private String date;

	/**
	 * ID del partido a consultar en detalle. Se usa en el endpoint de detalle
	 * individual.
	 */
	private Long fixtureId;

	public GetFixturesRequest() {
	}

	public String getDate() {
		return date;
	}

	public void setDate(String date) {
		this.date = date;
	}

	public Long getFixtureId() {
		return fixtureId;
	}

	public void setFixtureId(Long fixtureId) {
		this.fixtureId = fixtureId;
	}
}