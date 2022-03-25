package cycling;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Stage {
	private Race race;
	private String name;
	private String description;
	private double length;
	private LocalDateTime startTime;
	private StageType type;
	private int id;
	private static int count = 0;
	private boolean waitingForResults = false;
	private ArrayList<Segment> segments = new ArrayList<>();

	private HashMap<Rider, StageResult> results = new HashMap<Rider, StageResult>();

	private static final int[] FLAT_POINTS = { 50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2 };
	private static final int[] MEDIUM_POINTS = { 30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2 };
	private static final int[] HIGH_POINTS = { 20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
	private static final int[] TT_POINTS = { 20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };

	public Stage(Race race, String name, String description, double length, LocalDateTime startTime, StageType type)
			throws InvalidNameException, InvalidLengthException {
		// super(EventType.STAGE);
		// TODO: Delete EventType
		if (name == null || name.isEmpty() || name.length() > 30 || CyclingPortal.containsWhitespace(name)) {
			throw new InvalidNameException(
					"Stage name cannot be null, empty, have more than 30 characters or have white spaces.");
		}
		if (length < 5) {
			throw new InvalidLengthException("Length is invalid, cannot be less than 5km.");
		}
		this.name = name;
		this.description = description;
		this.race = race;
		this.length = length;
		this.startTime = startTime;
		this.type = type;
		this.id = Stage.count++;
		// this.race.invalidateResults();
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public double getLength() {
		return length;
	}

	public Race getRace() {
		return race;
	}

	public StageType getType() {
		return type;
	}

	public ArrayList<Segment> getSegments() {
		return segments;
	}

	public LocalDateTime getStartTime() {
		return startTime;
	}

	// void invalidateResults() {
	// calculatedPoints = false;
	// calculatedPositions = false;
	// this.race.invalidateResults();
	// }

	public void addSegment(Segment segment) {
		for (int i = 0; i < segments.size(); i++) {
			if (segment.getLocation() < segments.get(i).getLocation()) {
				segments.add(i, segment);
				return;
			}
		}
		segments.add(segment);
		// this.invalidateResults();
	}

	public void removeSegment(Segment segment) throws InvalidStageStateException {
		if (waitingForResults == true) {
			throw new InvalidStageStateException("The stage cannot be removed as it is waiting for results.");
		}
		segments.remove(segment);
		// invalidateResults();
	}

	public void registerResult(Rider rider, LocalTime[] checkpoints)
			throws InvalidStageStateException, DuplicatedResultException, InvalidCheckpointsException {
		if (waitingForResults == false) {
			throw new InvalidStageStateException(
					"Results can only be added to a stage while it is waiting for results.");
		}
		if (results.containsKey(rider)) {
			throw new DuplicatedResultException("Each rider can only have one result per Stage.");
		}
		if (checkpoints.length != segments.size() + 2) {
			throw new InvalidCheckpointsException(
					"The length of the checkpoint must equal number of Segments in the Stage + 2.");
		}

		StageResult result = new StageResult(checkpoints);
		// Save Riders result for the Stage
		results.put(rider, result);

		// Propogate all of the Riders results for each segment
		for (int i = 0; i < segments.size(); i++) {
			segments.get(i).registerResults(rider, checkpoints[i + 1]);
		}

		// invalidateResults();
	}

	public void deleteRiderResults(Rider rider) {
		results.remove(rider);
		// calculatedPositions = false;
		// calculatedPoints = false;
		// ridersByElapsedTime = null;
	}

	public void concludePreparation() throws InvalidStageStateException {
		if (waitingForResults) {
			throw new InvalidStageStateException("Stage is already waiting for results.");
		}
		waitingForResults = true;
	}

	public boolean isWaitingForResults() {
		return waitingForResults;
	}

	public StageResult getRiderResult(Rider rider) {
		calculateResults();
		return results.get(rider);
	}

	public List<Rider> getRidersByElapsedTime() {
		calculateResults();
		return sortRiderResults();
	}

	private List<Rider> sortRiderResults() {
		// if (ridersByElapsedTime == null) {
		List<Rider> ridersByElapsedTime = results.entrySet()
				.stream()
				.sorted(Comparator.comparing(Map.Entry::getValue, StageResult.sortByElapsedTime))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
		// }
		return ridersByElapsedTime;
	}

	private void calculateResults() {
		List<Rider> riders = sortRiderResults();

		for (int i = 0; i < results.size(); i++) {
			Rider rider = riders.get(i);
			StageResult result = results.get(rider);
			int position = i + 1;

			// Position Calculation
			result.setPosition(position);

			// Adjusted Elapsed Time Calculations
			if (i == 0) {
				result.setAdjustedElapsedTime(result.getElapsedTime());
			} else {
				Rider prevRider = riders.get(i - 1);
				Duration prevTime = results.get(prevRider).getElapsedTime();
				Duration time = results.get(rider).getElapsedTime();

				int timeDiff = time.minus(prevTime).compareTo(Duration.ofSeconds(1));
				if (timeDiff <= 0) {
					// Close Finish Condition
					Duration prevAdjustedTime = results.get(prevRider).getAdjustedElapsedTime();
					result.setAdjustedElapsedTime(prevAdjustedTime);
				} else {
					// Far Finish Condition
					result.setAdjustedElapsedTime(time);
				}
			}

			// Points Calculation
			int sprintersPoints = 0;
			int mountainPoints = 0;
			for (Segment segment: segments){
				SegmentResult segmentResult = segment.getRiderResult(rider);
				sprintersPoints += segmentResult.getSprintersPoints();
				mountainPoints += segmentResult.getMountainPoints();
			}
			int[] pointsDistribution = getPointDistribution();
			if (position <= pointsDistribution.length) {
				sprintersPoints += pointsDistribution[i];
			}
			result.setSprintersPoints(sprintersPoints);
			result.setMountainPoints(mountainPoints);
		}
		// calculatedPositions = true;
	}

	private int[] getPointDistribution() {
		switch (type) {
			case FLAT:
				return FLAT_POINTS;
			case MEDIUM_MOUNTAIN:
				return MEDIUM_POINTS;
			case HIGH_MOUNTAIN:
				return HIGH_POINTS;
			case TT:
				return TT_POINTS;
			default:
				return new int[]{};
		}

	}
}
