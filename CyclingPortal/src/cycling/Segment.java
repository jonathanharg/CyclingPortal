package cycling;

import java.time.LocalTime;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Segment {
	private static int count = 0;
	private Stage stage;
	private int id;
	private SegmentType type;
	private double location;

	private HashMap<Rider, SegmentResult> results = new HashMap<Rider, SegmentResult>();

	private static final int[] SPRINT_POINTS = { 20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
	private static final int[] HC_POINTS = { 20, 15, 12, 10, 8, 6, 4, 2 };
	private static final int[] C1_POINTS = { 10, 8, 6, 4, 2, 1 };
	private static final int[] C2_POINTS = { 5, 3, 2, 1 };
	private static final int[] C3_POINTS = { 2, 1 };
	private static final int[] C4_POINTS = { 1 };

	public Segment(Stage stage, SegmentType type, double location)
			throws InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		// super(EventType.SEGMENT);
		if (location > stage.getLength()) {
			throw new InvalidLocationException("The location is out of bounds of the stage length.");
		}
		if (stage.isWaitingForResults()) {
			throw new InvalidStageStateException("The stage is waiting for results.");
		}
		if (stage.getType().equals(StageType.TT)) {
			throw new InvalidStageTypeException("Time-trial stages cannot contain any segments.");
		}
		this.stage = stage;
		this.id = Segment.count++;
		this.type = type;
		this.location = location;
		// this.stage.invalidateResults();
	}

	public SegmentType getType() {
		return type;
	}

	public int getId() {
		return id;
	}

	public Stage getStage() {
		return stage;
	}

	public double getLocation() {
		return location;
	}

	// void invalidateResults(){
	// calculatedPoints = false;
	// calculatedPositions = false;
	// this.stage.invalidateResults();
	// }

	public void registerResults(Rider rider, LocalTime finishTime) {
		SegmentResult result = new SegmentResult(finishTime);
		results.put(rider, result);
		// invalidateResults();
	}

	public void deleteRiderResults(Rider rider) {
		results.remove(rider);
		// calculatedPositions = false;
		// calculatedPoints = false;
		// ridersByElapsedTime = null;
	}

	public SegmentResult getRiderResult(Rider rider){
		// TODO: Optimise
		calculateResults();
		return results.get(rider);
	}

	private List<Rider> sortRiderResults() {
		// if (ridersByElapsedTime == null) {
		List<Rider> ridersByFinishTime = results.entrySet()
				.stream()
				.sorted(Comparator.comparing(Map.Entry::getValue, SegmentResult.sortByFinishTime))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
		// }
		return ridersByFinishTime;
	}

	private void calculateResults() {
		List<Rider> riders = sortRiderResults();

		for (int i = 0; i < results.size(); i++) {
			Rider rider = riders.get(i);
			SegmentResult result = results.get(rider);
			int position = i + 1;
			// Position Calculation
			result.setPosition(position);

			// Points Calculation
			int[] pointsDistribution = getPointsDistribution();
			if (position <= pointsDistribution.length) {
				int points = pointsDistribution[i];
				if(this.type.equals(SegmentType.SPRINT)){
					result.setSprintersPoints(points);
					result.setMountainPoints(0);
				} else {
					result.setSprintersPoints(0);
					result.setMountainPoints(points);
				}
			} else {
				result.setMountainPoints(0);
				result.setSprintersPoints(0);
			}
		}
		// calculatedPositions = true;
	}

	private int[] getPointsDistribution(){
		switch (type) {
			case HC:
				return HC_POINTS;
			case C1:
				return C1_POINTS;
			case C2:
				return C2_POINTS;
			case C3:
				return C3_POINTS;
			case C4:
				return C4_POINTS;
			case SPRINT:
				return SPRINT_POINTS;
			default:
				return new int[]{};
		}
	}
}
