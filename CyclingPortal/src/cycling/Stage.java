package cycling;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Stage {

	private int raceId;
	private Race race;
	private String name;
	private String description;
	private double length;
	private LocalDateTime startTime;
	private StageType type;
	private int id;
	private static int count = 0;
	private ArrayList<Segment> segments = new ArrayList<>();
	private HashMap<Rider, StageResult> results = new HashMap<Rider, StageResult>();
	private boolean waitingForResults = false;
	
	private static final int[] FLAT_POINTS = {50,30,20,18,16,14,12,10,8,7,6,5,4,3,2};
	private static final int[] MEDIUM_POINTS = {30,25,22,19,17,15,13,11,9,7,6,5,4,3,2};
	private static final int[] HIGH_POINTS = {20,17,15,13,11,10,9,8,7,6,5,4,3,2,1};
	private static final int[] TT_POINTS = {20,17,15,13,11,10,9,8,7,6,5,4,3,2,1};


	private static Comparator<StageResult> sortByElapsedTime = (StageResult result1, StageResult result2) -> result1
			.getElapsedTime().compareTo(result2.getElapsedTime());

	public Stage(int raceId, Race race, String name, String description, double length, LocalDateTime startTime,
			StageType type) throws InvalidNameException, InvalidLengthException {
		if (name == null || name.isEmpty() || name.length() > 30) {
			throw new InvalidNameException("Stage name cannot be null, empty or have more than 30 characters.");
		}
		if (length < 5) {
			throw new InvalidLengthException("Length is invalid, cannot be less than 5km.");
		}
		this.name = name;
		this.description = description;
		this.raceId = raceId;
		this.race = race;
		this.length = length;
		this.startTime = startTime;
		this.type = type;
		this.id = this.count++;
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
	}

	public void removeSegment(Segment segment) {
		segments.remove(segment);
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
		results.put(rider, result);

		for (int i = 0; i < segments.size(); i++) {
			segments.get(i).registerResults(rider, checkpoints[i + 1]);
		}
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

	public LocalTime[] getRiderResults(Rider rider) {
		// return results.get(rider);
		return null;
	}

	public void deleteRiderResults(Rider rider) {
		results.remove(rider);
	}

	private List<Rider> generateElapsedTime() {
		List<Rider> ridersByElapsedTime = results.entrySet()
				.stream()
				.sorted(Comparator.comparing(Map.Entry::getValue, sortByElapsedTime))
				.map(Map.Entry::getKey)
				.collect(Collectors.toList());
		return ridersByElapsedTime;
	}

	private void generateStatistics() {
		List<Rider> riders = generateElapsedTime();
		
		for(int i = 0; i < results.size(); i++) {
			Rider rider = riders.get(i);
			StageResult result = results.get(rider);
			result.setPosition(i + 1);
			result.setStageSprintersPoints(getPoints(i));
			
			if(i == 0) {
				result.setAdjustedElapsedTime(result.getElapsedTime());
			} else {
				Rider prevRider = riders.get(i - 1);
				Duration prevTime = results.get(prevRider).getElapsedTime();
				Duration time = results.get(rider).getElapsedTime();
				
				int timeDiff = time.minus(prevTime).compareTo(Duration.ofSeconds(1));
				if(timeDiff <= 0) {
//					Close Finish Condition
					Duration prevAdjustedTime = results.get(prevRider).getAdjustedElapsedTime();
					results.get(rider).setAdjustedElapsedTime(prevAdjustedTime);
				} else {
//					Far Finish Condition
					results.get(rider).setAdjustedElapsedTime(time);
				}
			}
			
		}
	}
	
	public void debugPrintResults() {
		generateStatistics();
		generateElapsedTime().forEach(rider -> {
			StageResult riderResults = results.get(rider);
			System.out.println(rider.getName() + " finished in " + riderResults.getPosition() + "th.");
			System.out.println("ET: " + riderResults.getElapsedTime() + " AET: " + riderResults.getAdjustedElapsedTime() + " SP: " + riderResults.getStageSprintersPoints());
		});
	}
	
	private int getPoints(int index) {
		int[] points = {};
		switch(type) {
			case FLAT:
				points = FLAT_POINTS;
			case MEDIUM_MOUNTAIN:
				points = MEDIUM_POINTS;
			case HIGH_MOUNTAIN:
				points = HIGH_POINTS;
			case TT:
				points = TT_POINTS;
		}
		if ((index) > points.length) {
			return 0;
		} else {
			return points[index];
		}
	}
}
