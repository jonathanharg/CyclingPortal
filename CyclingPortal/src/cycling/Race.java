package cycling;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.time.LocalDateTime;

public class Race {

	private String name;
	private String description;

	private ArrayList<Stage> stages = new ArrayList<>();

	private HashMap<Rider, RaceResult> results = new HashMap<Rider, RaceResult>();

	private static int count = 0;
	private int id;

	public Race(String name, String description) throws InvalidNameException {
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

	// void invalidateResults(){
	// calculatedPoints = false;
	// calculatedPositions = false;
	// }

	public void addStage(Stage stage) {
		for (int i = 0; i < stages.size(); i++) {
			LocalDateTime iStartTime = stages.get(i).getStartTime();
			if (stage.getStartTime().isBefore(iStartTime)) {
				stages.add(i, stage);
				return;
			}
		}
		stages.add(stage);
		// invalidateResults();
	}

	public ArrayList<Stage> getStages() {
		return stages;
	}

	public void removeStage(Stage stage) {
		stages.remove(stage);
		// invalidateResults();
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

	private List<Rider> sortRiderResultsBy(Comparator<RaceResult> comparison) {
		// if (ridersByElapsedTime == null) {
		List<Rider> sortedRiders = results.entrySet()
				.stream()
				.sorted(Comparator.comparing(Map.Entry::getValue, comparison))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
		// }
		return sortedRiders;
	}

	// TODO: work out what to do for this.
	private void calculateResults() {
		return;
	}
}
