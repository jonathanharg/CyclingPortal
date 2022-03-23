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
	private int sprintersPoints;
	private int mountainPoints;

	protected static final Comparator<Result> sortByLastCheckpoint = (Result result1, Result result2) -> result1.getLastCheckpoint().compareTo(result2.getLastCheckpoint());

	
	public Result(LocalTime[] checkpoints) {
		this.checkpointTimes = checkpoints;
		this.elapsedTime = Duration.between(checkpoints[0], checkpoints[checkpoints.length - 1]);
	}

	public LocalTime getLastCheckpoint(){
		return checkpointTimes[checkpointTimes.length - 1];
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
	
	public void setPoints(PointType pointType, int points) {
		switch(pointType){
			case MOUNTAIN:
				this.mountainPoints = points;
				break;
			case SPRINTERS:
				this.sprintersPoints = points;
				break;
		}
	}
	
	public int getPoints(PointType pointType) {
		switch(pointType){
			case MOUNTAIN:
				return mountainPoints;
			case SPRINTERS:
				return sprintersPoints;
			default:
				return 0;
		}
	}
}
