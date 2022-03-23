package cycling;

import java.lang.Double;

public class CategorizedClimb extends Segment {
	private Double averageGradient;

	public CategorizedClimb(Stage stage, Double location, SegmentType type, Double averageGradient, Double length) throws InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		super(stage, type, location);
		this.averageGradient = averageGradient;
	}

}
