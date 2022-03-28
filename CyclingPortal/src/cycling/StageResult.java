package cycling;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;

public class StageResult{
	private final LocalTime[] checkpoints;
	private Duration elapsedTime;
	private Duration adjustedElapsedTime;
	private int position;
	private int sprintersPoints;
	private int mountainPoints;

	protected static final Comparator<StageResult> sortByElapsedTime = (StageResult result1, StageResult result2) -> result1.getElapsedTime().compareTo(result2.getElapsedTime());
	
	public StageResult(LocalTime[] checkpoints) {
		this.checkpoints = checkpoints;
		this.elapsedTime = Duration.between(checkpoints[0], checkpoints[checkpoints.length - 1]);
	}

	public LocalTime[] getCheckpoints(){
		return this.checkpoints;
	}

	public Duration getElapsedTime() {
		return elapsedTime;
	}
	
	public void setPosition(int position){
		this.position = position;
	}
	
	public void setAdjustedElapsedTime(Duration adjustedElapsedTime) {
		this.adjustedElapsedTime = adjustedElapsedTime;
	}
	
	public int getPosition() {
		return position;
	}
	
	public Duration getAdjustedElapsedTime() {
		return adjustedElapsedTime;
	}
	
	public LocalTime getAdjustedElapsedLocalTime() {
		return checkpoints[0].plus(adjustedElapsedTime);
	}

	public void setMountainPoints(int points){
		this.mountainPoints = points;
	}

	public void setSprintersPoints(int points){
		this.sprintersPoints = points;
	}

	public int getMountainPoints(){
		return mountainPoints;
	}

	public int getSprintersPoints(){
		return sprintersPoints;
	}

	public void add(StageResult res){
		this.elapsedTime = this.elapsedTime.plus(res.getElapsedTime());
		this.adjustedElapsedTime = this.adjustedElapsedTime.plus(res.getAdjustedElapsedTime());
		this.sprintersPoints += res.getSprintersPoints();
		this.mountainPoints += res.getMountainPoints();
	}
}
