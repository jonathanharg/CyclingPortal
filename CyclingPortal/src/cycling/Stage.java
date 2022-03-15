package cycling;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Hashtable;

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
	private Hashtable<Rider, LocalTime[]> results = new Hashtable<Rider, LocalTime[]>();
	private boolean waitingForResults = false;
	
	public Stage(int raceId, Race race,String name, String description, double length, LocalDateTime startTime, StageType type) throws InvalidNameException, InvalidLengthException {
		if (name == null || name.isEmpty() || name.length() > 30) {
			throw new InvalidNameException("Race name cannot be null, empty or have more than 30 characters.");
		}
		if (length < 5) {
			throw new InvalidLengthException("Length is invalid, cannot be less than 5km.");
		}
		this.name = name;
		this.description = description;
		this.raceId  = raceId;
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
	
	public ArrayList<Segment> getSegments(){
		return segments;
	}
	
	public void addSegment(Segment segment) {
		segments.add(segment);
	}
	
	public void removeSegment(Segment segment) {
		segments.remove(segment);
	}
	
	public void registerResult(Rider rider, LocalTime[] checkpoints) throws InvalidStageStateException, DuplicatedResultException, InvalidCheckpointsException {
		if (waitingForResults == false) {
			throw new InvalidStageStateException("Results can only be added to a stage while it is waiting for results.");
		}
		if (results.containsKey(rider)) {
			throw new DuplicatedResultException("Each rider can only have one result per Stage.");
		}
		if (checkpoints.length != segments.size() + 2) {
			throw new InvalidCheckpointsException("The length of the checkpoint must equal number of Segments in the Stage + 2.");
		}
		results.put(rider,checkpoints);
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
	
}
