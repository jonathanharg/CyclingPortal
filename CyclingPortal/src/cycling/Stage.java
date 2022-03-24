package cycling;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Stage extends CompetitiveEvent {
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

	private static final int[] FLAT_POINTS = { 50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2 };
	private static final int[] MEDIUM_POINTS = { 30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2 };
	private static final int[] HIGH_POINTS = { 20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };
	private static final int[] TT_POINTS = { 20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1 };

	public Stage(Race race, String name, String description, double length, LocalDateTime startTime, StageType type)
			throws InvalidNameException, InvalidLengthException {
		super(EventType.STAGE);
		if (name == null || name.isEmpty() || name.length() > 30 || CyclingPortal.containsWhitespace(name)) {
			throw new InvalidNameException("Stage name cannot be null, empty, have more than 30 characters or have white spaces.");
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
		this.race.invalidateResults();
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

	void invalidateResults() {
		calculatedPoints = false;
		calculatedPositions = false;
		this.race.invalidateResults();
	}

	public void addSegment(Segment segment) {
		for (int i = 0; i < segments.size(); i++) {
			if (segment.getLocation() < segments.get(i).getLocation()) {
				segments.add(i, segment);
				return;
			}
		}
		segments.add(segment);
		this.invalidateResults();
	}

	public void removeSegment(Segment segment) throws InvalidStageStateException {
		if (waitingForResults == true){
			throw new InvalidStageStateException("The stage cannot be removed as it is waiting for results.");
		}
		segments.remove(segment);
		invalidateResults();
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

		Result result = new Result(checkpoints);
		// Save Riders result for the Stage
		results.put(rider, result);

		// Propogate all of the Riders results for each segment
		for (int i = 0; i < segments.size(); i++) {
			segments.get(i).registerResults(rider, checkpoints[i + 1]);
		}

		invalidateResults();
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

	public Result getRiderPointsResult(Rider rider){
		getRiderPoints(PointType.MOUNTAIN, rider);
		getRiderPoints(PointType.SPRINTERS, rider);
		return getRiderPointsResult(rider);
	}

	// TODO: Clean up
	public int getRiderPoints(PointType pointType, Rider rider){
		// ** Three stages of results: 1. checkpoints only 2. positions calculated 3. points calculated
		Result stageResult = this.getRiderPositionResults(rider);
		int stagePoints = 0;
		for(Segment segment:segments){
			Result segmentResult = segment.getRiderPositionResults(rider);
			int segmentPoints = segment.pointsByPosition(pointType, segmentResult.getPosition());
			segmentResult.setPoints(pointType, segmentPoints);
			stagePoints += segmentPoints;
		}
		if(pointType.equals(PointType.SPRINTERS)){
			int extraPoints = this.pointsByPosition(stageResult.getPosition());
			stagePoints += extraPoints;
		}
		stageResult.setPoints(pointType, stagePoints);
		return stagePoints;
	}

	private int pointsByPosition(int position) {
		int[] pointsDistribution = {};
		int points = 0;
		switch (type) {
			case FLAT:
				pointsDistribution = FLAT_POINTS;
				break;
			case MEDIUM_MOUNTAIN:
				pointsDistribution = MEDIUM_POINTS;
				break;
			case HIGH_MOUNTAIN:
				pointsDistribution = HIGH_POINTS;
				break;
			case TT:
				pointsDistribution = TT_POINTS;
				break;
		}
		if (position <= pointsDistribution.length) {
			points = pointsDistribution[position - 1];
		}
		return points;
	}
}
