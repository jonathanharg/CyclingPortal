package cycling;

import java.time.Duration;
import java.time.LocalTime;

public class StageResult {
	private LocalTime[] checkpointTimes;
	private Duration elapsedTime;
	private Duration adjustedElapsedTime;
	private LocalTime adjustedElapsedLocalTime;
	private int position;
	private int stageSprintersPoints;
	private int stageMountainPoints = 0;
	
//	private ArrayList<SegmentResult> segmentResults = new ArrayList<SegmentResult>();

	public StageResult(LocalTime[] checkpoints) {
		this.checkpointTimes = checkpoints;
		this.elapsedTime = Duration.between(checkpoints[0], checkpoints[checkpoints.length - 1]);
		
//		for (int i = 0; i < checkpoints.length - 2; i++) {
//			SegmentResult segmentResult = new SegmentResult(checkpoints[i + 1]);
//			segmentResults.add(segmentResult);
//		}
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
	
	public void setStageSprintersPoints(int points) {
		this.stageSprintersPoints = points;
	}
	
	public int getStageSprintersPoints() {
		return stageSprintersPoints;
	}
	
	public void setStageMountainPoints(int points) {
		this.stageMountainPoints = points;
	}
	
	public int getStageMountainPoints() {
		return stageMountainPoints;
	}
}
