package cycling;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;

public class Result{
	private LocalTime[] checkpointTimes;
	private Duration elapsedTime;
	private Duration adjustedElapsedTime;
	private LocalTime adjustedElapsedLocalTime;
	private int position;
	private int cumulativeSprintersPoints;
	private int cumulativeMountainPoints;
	private int sprintersPoints;

	private int mountainPoints;

	protected static final Comparator<Result> sortByElapsedTime = (Result result1, Result result2) -> result1.getElapsedTime().compareTo(result2.getElapsedTime());

	
	public Result(LocalTime[] checkpoints) {
		this.checkpointTimes = checkpoints;
		this.elapsedTime = Duration.between(checkpoints[0], checkpoints[checkpoints.length - 1]);
	}

	public Duration getElapsedTime() {
		return elapsedTime;
	}
	
	public void setPosition(int position){
		this.position = position;
	}
	
	public void setAdjustedElapsedTime(Duration adjustedElapsedTime) {
		this.adjustedElapsedTime = adjustedElapsedTime;
		this.adjustedElapsedLocalTime = checkpointTimes[0].plus(adjustedElapsedTime);
	}
	
	public int getPosition() {
		return position;
	}
	
	public Duration getAdjustedElapsedTime() {
		return adjustedElapsedTime;
	}
	
	public LocalTime getAdjustedElapsedLocalTime() {
		return adjustedElapsedLocalTime;
	}
	
	public void setSprintersPoints(int points) {
		this.sprintersPoints = points;
	}
	
	public int getSprintersPoints() {
		return sprintersPoints;
	}
	
	public void setMountainPoints(int points) {
		this.mountainPoints = points;
	}
	
	public int getMountainPoints() {
		return mountainPoints;
	}

	public int getCumulativeSprintersPoints() {
		return cumulativeSprintersPoints;
	}

	public void setCumulativeSprintersPoints(int cumulativeSprintersPoints) {
		this.cumulativeSprintersPoints = cumulativeSprintersPoints;
	}

	public int getCumulativeMountainPoints() {
		return cumulativeMountainPoints;
	}

	public void setCumulativeMountainPoints(int cumulativeMountainPoints) {
		this.cumulativeMountainPoints = cumulativeMountainPoints;
	}
}
