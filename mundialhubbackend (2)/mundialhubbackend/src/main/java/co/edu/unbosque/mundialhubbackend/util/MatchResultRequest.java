package co.edu.unbosque.mundialhubbackend.util;

public class MatchResultRequest {

	private long id;
	private Integer homeScore;
	private Integer awayScore;

	public MatchResultRequest() {
	}

	public Integer getHomeScore() {
		return homeScore;
	}

	public void setHomeScore(Integer homeScore) {
		this.homeScore = homeScore;
	}

	public Integer getAwayScore() {
		return awayScore;
	}

	public void setAwayScore(Integer awayScore) {
		this.awayScore = awayScore;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

}