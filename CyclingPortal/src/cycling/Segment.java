package cycling;

import java.time.LocalTime;
import java.util.List;

public class Segment extends CompetitiveEvent{
	private static int count = 0;
	private Stage stage;
	private int id;
	private SegmentType type;
	private double location;
	
	private static final int[] SPRINT_POINTS = {20,17,15,13,11,10,9,8,7,6,5,4,3,2,1};
	private static final int[] HC_POINTS = {20,15,12,10,8,6,4,2};
	private static final int[] C1_POINTS = {10,8,6,4,2,1};
	private static final int[] C2_POINTS = {5,3,2,1};
	private static final int[] C3_POINTS = {2,1};
	private static final int[] C4_POINTS = {1};


	public Segment(Stage stage, SegmentType type, double location)
			throws InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		super(EventType.SEGMENT);
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
		Result result = new Result(new LocalTime[]{checkpoint});
		results.put(rider, result);

		// !!!
		// latestResultsCalculated = false;
	}

	public void calculatePoints() {
		List<Rider> riders = getRidersByElapsedTime();
		int[] pointsDistribution = {};
		switch(type) {
			case HC:
				pointsDistribution = HC_POINTS;
			case C1:
				pointsDistribution = C1_POINTS;
			case C2:
				pointsDistribution = C2_POINTS;
			case C3:
				pointsDistribution = C3_POINTS;
			case C4:
				pointsDistribution = C4_POINTS;
			case SPRINT:
				pointsDistribution = SPRINT_POINTS;
		}
		for(Rider rider:riders){
			Result result = getRiderResults(rider);
			int points = 0;
			if ((result.getPosition()) <= pointsDistribution.length) {
				points = pointsDistribution[result.getPosition() - 1];
			}
			if (type.equals(SegmentType.SPRINT)){
				result.setSprintersPoints(points);
			} else {
				result.setMountainPoints(points);
			}
		}
	}
}
