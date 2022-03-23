package cycling;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
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
		if (name == null || name.isEmpty() || name.length() > 30) {
			throw new InvalidNameException("Stage name cannot be null, empty or have more than 30 characters.");
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

	public void addSegment(Segment segment) {
		// TODO: TEST!!!!
		for (int i = 0; i < segments.size(); i++) {
			if (segment.getLocation() < segments.get(i).getLocation()) {
				segments.add(i, segment);
				return;
			}
		}
		segments.add(segment);

		// !!!
		// latestResultsCalculated = false;
	}

	public void removeSegment(Segment segment) {
		segments.remove(segment);

		// !!!
		// latestResultsCalculated = false;
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
		results.put(rider, result);

		for (int i = 0; i < segments.size(); i++) {
			segments.get(i).registerResults(rider, checkpoints[i + 1]);
		}

		// !!!
		// ridersByElapsedTime = null;
		// latestResultsCalculated = false;
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

	public int getRiderPoints(PointType pointType, Rider rider){
		Result stageResult = getRiderResults(rider);
		int stagePoints = 0;
		for(Segment segment:segments){
			Result segmentResult = segment.getRiderResults(rider);
			int segmentPoints = segment.calculatePoints(pointType, segmentResult.getPosition());
			segmentResult.setPoints(pointType, segmentPoints);
			stagePoints += segmentPoints;
		}
		if(pointType.equals(PointType.SPRINTERS)){
			stagePoints += this.calculatePoints(stageResult.getPosition());
		}
		stageResult.setPoints(pointType, stagePoints);
		return stagePoints;
	}

	private int calculatePoints(int position) {
		int[] pointsDistribution = {};
		int points = 0;
		switch (type) {
			case FLAT:
				pointsDistribution = FLAT_POINTS;
			case MEDIUM_MOUNTAIN:
				pointsDistribution = MEDIUM_POINTS;
			case HIGH_MOUNTAIN:
				pointsDistribution = HIGH_POINTS;
			case TT:
				pointsDistribution = TT_POINTS;
		}
		if (position <= pointsDistribution.length) {
			points = pointsDistribution[position - 1];
		}
		return points;
	}
}
