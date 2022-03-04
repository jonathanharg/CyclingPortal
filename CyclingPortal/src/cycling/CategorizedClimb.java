package cycling;

import java.lang.Double;

public class CategorizedClimb extends Segment {
	private Double averageGradient;

	public CategorizedClimb(int stageId, Stage stage, Double location, SegmentType type, Double averageGradient, Double length) throws InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		super(stageId, stage, type, length);
		this.averageGradient = averageGradient;
	}

}
