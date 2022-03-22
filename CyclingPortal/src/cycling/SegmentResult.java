package cycling;

import java.time.LocalTime;

public class SegmentResult {
	private LocalTime checkpointTime;
	private int position;
	private int segmentSprinterPoints;
	private int segmentMountainPoints;

	public SegmentResult(LocalTime checkpoint) {
		this.checkpointTime = checkpoint;
	}
	
	public LocalTime getCheckpointTime() {
		return checkpointTime;
	}
	
	public void setPosition(int position) {
		this.position = position;
	}
	
	public int getPosition(int position) {
		return position;
	}
	
	public int getSegmentSprinterPoints() {
		return segmentSprinterPoints;
	}

	public void setSegmentSprinterPoints(int segmentSprinterPoints) {
		this.segmentSprinterPoints = segmentSprinterPoints;
	}

	public int getSegmentMountainPoints() {
		return segmentMountainPoints;
	}

	public void setSegmentMountainPoints(int segmentMountainPoints) {
		this.segmentMountainPoints = segmentMountainPoints;
	}
}
