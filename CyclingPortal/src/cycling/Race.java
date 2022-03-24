package cycling;

import java.util.ArrayList;
import java.time.LocalDateTime;

public class Race extends CompetitiveEvent{

	private String name;
	private String description;

	private ArrayList<Stage> stages = new ArrayList<>();

	private static int count = 0;
	private int id;

	public Race(String name, String description) throws InvalidNameException {
		super(EventType.RACE);
		if (name == null || name.isEmpty() || name.length() > 30 || CyclingPortal.containsWhitespace(name)) {
			throw new InvalidNameException(
					"The name cannot be null, empty, have more than 30 characters, or have white spaces.");
		}
		this.name = name;
		this.description = description;
		this.id = Race.count++;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	void invalidateResults(){
		calculatedPoints = false;
		calculatedPositions = false;
	}

	public void addStage(Stage stage) {
		for (int i = 0; i < stages.size(); i++) {
			LocalDateTime iStartTime = stages.get(i).getStartTime();
			if (stage.getStartTime().isBefore(iStartTime)) {
				stages.add(i, stage);
				return;
			}
		}
		stages.add(stage);
		invalidateResults();
	}

	public ArrayList<Stage> getStages() {
		return stages;
	}

	public void removeStage(Stage stage) {
		stages.remove(stage);
		invalidateResults();
	}

	public String getDetails() {
		double currentLength = 0;
		for (final Stage stage : stages) {
			currentLength = currentLength + stage.getLength();
		}
		String details = ("Race ID: " + id + System.lineSeparator() + "Name: " + name + System.lineSeparator()
				+ "Description: " + description + System.lineSeparator() + "Number of Stages: " + stages.size()
				+ System.lineSeparator() + "Total length: " + currentLength);
		return details;
	}

	// TODO: Up next
	// TODO: SPLIT RESULTS INTO RACERESULT STAGERESULT SEGRESULT, & ADD COMPETITIVE EVENTS TO RACE, STAGE, SEGMENT??? - IN BRANCH?
	// public Result getCumulativeResults(Rider rider){
	// 	Result raceResult = new Result(null);
	// 	for(Stage stage:stages){
	// 		Result stageResult = stage.getRiderPointsResult(rider);
	// 		raceResult.add(stageResult);
	// 	}
	// 	results.put(rider, raceResult);
	// 	return raceResult;
	// }
}
