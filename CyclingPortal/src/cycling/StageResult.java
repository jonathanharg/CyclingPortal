package cycling;

import java.time.Duration;
import java.time.LocalTime;

public class StageResult {
	private LocalTime[] checkpointTimes;
	private Duration elapsedTime;
	private Duration adjustedElapsedTime;
	private int position;
	private int stageSprintersPoints;
	private int stageMountainPoints;

	public StageResult(LocalTime[] checkpoints) {
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
	}
	
	public int getPosition() {
		return position;
	}
	
	public Duration getAdjustedElapsedTime() {
		return adjustedElapsedTime;
	}
	
	public void setStageSprintersPoints(int points) {
		this.stageSprintersPoints = points;
	}
	
	public int getStageSprintersPoints() {
		return stageSprintersPoints;
	}
}
