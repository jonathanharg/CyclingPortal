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
	private boolean updatedStatisticsCalculated = false; //TODO:DO WE NEED THIS??
	
	private static Comparator<SegmentResult> sortByElapsedTime = (SegmentResult result1, SegmentResult result2) -> result1
			.getCheckpointTime().compareTo(result2.getCheckpointTime());
	
	private static final int[] SPRINT_POINTS = {20,17,15,13,11,10,9,8,7,6,5,4,3,2,1};
	private static final int[] HC_POINTS = {20,15,12,10,8,6,4,2};
	private static final int[] C1_POINTS = {10,8,6,4,2,1};
	private static final int[] C2_POINTS = {5,3,2,1};
	private static final int[] C3_POINTS = {2,1};
	private static final int[] C4_POINTS = {1};


	public Segment(Stage stage, SegmentType type, double location)
			throws InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
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

	public void registerResults(Rider rider, LocalTime checkpoint) {
		SegmentResult result = new SegmentResult(checkpoint);
		results.put(rider, result);
		updatedStatisticsCalculated = false;
	}
	
	public SegmentResult getRiderResult(Rider rider) {
		return results.get(rider);
	}
	
	public List<Rider> getRidersByElapsedTime() {
		List<Rider> ridersByElapsedTime = results.entrySet()
				.stream()
				.sorted(Comparator.comparing(Map.Entry::getValue, sortByElapsedTime))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
		return ridersByElapsedTime;
	}
	
	public void generateSegmentResults() {
		List<Rider> riders = getRidersByElapsedTime();
		
		for(int i = 0; i < results.size(); i++) {
			Rider rider = riders.get(i);
			SegmentResult result = results.get(rider);
			result.setPosition(i + 1);
			if (this.type.equals(SegmentType.SPRINT)) {
				result.setSegmentSprinterPoints(getPoints(i));
			} else {
				result.setSegmentMountainPoints(getPoints(i));
			}
		}
		updatedStatisticsCalculated = true;
	}
	
	private int getPoints(int position) {
		int[] points = {};
		switch(type) {
			case HC:
				points = HC_POINTS;
			case C1:
				points = C1_POINTS;
			case C2:
				points = C2_POINTS;
			case C3:
				points = C3_POINTS;
			case C4:
				points = C4_POINTS;
			case SPRINT:
				points = SPRINT_POINTS;
		}
		if ((position) > points.length) {
			return 0;
		} else {
			return points[position];
		}
	}
}
