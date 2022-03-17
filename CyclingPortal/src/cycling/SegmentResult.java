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

}
