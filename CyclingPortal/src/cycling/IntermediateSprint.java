package cycling;

public class IntermediateSprint extends Segment {
	private double location;

	public IntermediateSprint(int stageId, Stage stage, double location) throws InvalidLocationException, InvalidStageTypeException, InvalidStageStateException {
		super(stageId, stage, SegmentType.SPRINT, location);
		this.location = location;
	}
}
