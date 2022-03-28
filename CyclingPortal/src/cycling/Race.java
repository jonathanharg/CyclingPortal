package cycling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class Race {

	private final String name;
	private final String description;

	private final ArrayList<Stage> stages = new ArrayList<>();

	private final HashMap<Rider, RaceResult> results = new HashMap<>();

	private static int count = 0;
	private final int id;

	public Race(String name, String description) throws InvalidNameException {
		if (name == null || name.isEmpty() || name.length() > 30 || CyclingPortal.containsWhitespace(name)) {
			throw new InvalidNameException(
					"The name cannot be null, empty, have more than 30 characters, or have white spaces.");
		}
		this.name = name;
		this.description = description;
		this.id = Race.count++;
	}

	static void resetIdCounter() {
		count = 0;
	}

	static int getIdCounter() {
		return count;
	}

	static void setIdCounter(int newCount){
		count = newCount;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
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
	}

	public ArrayList<Stage> getStages() {
		return stages;
	}

	public void removeStage(Stage stage) {
		stages.remove(stage);
	}

	public String getDetails() {
		double currentLength = 0;
		for (final Stage stage : stages) {
			currentLength = currentLength + stage.getLength();
		}
		return ("Race ID: " + id + System.lineSeparator() + "Name: " + name + System.lineSeparator()
				+ "Description: " + description + System.lineSeparator() + "Number of Stages: " + stages.size()
				+ System.lineSeparator() + "Total length: " + currentLength);
	}

	public List<Rider> getRidersByAdjustedElapsedTime() {
		calculateResults();
		return sortRiderResultsBy(RaceResult.sortByAdjustedElapsedTime);
	}

	public List<Rider> getRidersBySprintersPoints() {
		calculateResults();
		return sortRiderResultsBy(RaceResult.sortBySprintersPoints);
	}

	public List<Rider> getRidersByMountainPoints() {
		calculateResults();
		return sortRiderResultsBy(RaceResult.sortByMountainPoints);
	}

	public RaceResult getRiderResults(Rider rider) {
		calculateResults();
		return results.get(rider);
	}

	public void removeRiderResults(Rider rider) {
		results.remove(rider);
	}

	private List<Rider> sortRiderResultsBy(Comparator<RaceResult> comparison) {
		return results.entrySet()
				.stream()
				.sorted(Map.Entry.comparingByValue(comparison))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
	}

	private void registerRiderResults(Rider rider, StageResult stageResult) {
		if (results.containsKey(rider)) {
			results.get(rider).addStageResult(stageResult);
		} else {
			RaceResult raceResult = new RaceResult();
			raceResult.addStageResult(stageResult);
			results.put(rider, raceResult);
		}
	}

	private void calculateResults() {
		for (Stage stage : stages) {
			HashMap<Rider, StageResult> stageResults = stage.getStageResults();
			for (Rider rider : stageResults.keySet()) {
				registerRiderResults(rider, stageResults.get(rider));
			}
		}
	}
}
