package cycling;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;

public class Result{
	private LocalTime[] checkpoints;
	private Duration elapsedTime;
	private Duration adjustedElapsedTime;
	private int position;
	private int sprintersPoints;
	private int mountainPoints;

	protected static final Comparator<Result> sortByElapsedTime = (Result result1, Result result2) -> result1.getLastCheckpoint().compareTo(result2.getLastCheckpoint());
	
	public Result(LocalTime[] checkpoints) {
		if (checkpoints == null){
			this.checkpoints = null;
			this.elapsedTime = null;
		} else {
			this.checkpoints = checkpoints;
			this.elapsedTime = Duration.between(checkpoints[0], checkpoints[checkpoints.length - 1]);
		}
	}

	public LocalTime[] getCheckpoints(){
		return this.checkpoints;
	}

	public LocalTime getLastCheckpoint(){
		return checkpoints[checkpoints.length - 1];
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

	public void add(Result res){
		this.elapsedTime = this.elapsedTime.plus(res.getElapsedTime());
		this.adjustedElapsedTime = this.adjustedElapsedTime.plus(res.getAdjustedElapsedTime());
		this.sprintersPoints += res.sprintersPoints;
		this.mountainPoints += res.mountainPoints;
	}
}
