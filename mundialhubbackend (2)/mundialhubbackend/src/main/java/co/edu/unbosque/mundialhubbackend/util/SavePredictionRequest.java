package co.edu.unbosque.mundialhubbackend.util;

public class SavePredictionRequest {

	private Long pollId;
	private Long fixtureId;
	private Integer predictedHomeGoals;
	private Integer predictedAwayGoals;

	public SavePredictionRequest() {
	}

	public Long getPollId() {
		return pollId;
	}

	public void setPollId(Long pollId) {
		this.pollId = pollId;
	}

	public Long getFixtureId() {
		return fixtureId;
	}

	public void setFixtureId(Long fixtureId) {
		this.fixtureId = fixtureId;
	}

	public Integer getPredictedHomeGoals() {
		return predictedHomeGoals;
	}

	public void setPredictedHomeGoals(Integer predictedHomeGoals) {
		this.predictedHomeGoals = predictedHomeGoals;
	}

	public Integer getPredictedAwayGoals() {
		return predictedAwayGoals;
	}

	public void setPredictedAwayGoals(Integer predictedAwayGoals) {
		this.predictedAwayGoals = predictedAwayGoals;
	}
}