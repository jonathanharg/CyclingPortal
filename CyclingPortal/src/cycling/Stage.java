package cycling;
import java.time.LocalDateTime;

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
	
}
