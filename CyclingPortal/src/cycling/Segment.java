package cycling;

public class Segment {
	
	private static int count = 0;
	private Stage stage;
	private int stageId;
	private int id;
	private SegmentType type;
	
	public Segment(int stageId, Stage stage, SegmentType type, double location) throws InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		if (location > stage.getLength()) {
			throw new InvalidLocationException("The location is out of bounds of the stage length.");
		}
		// TODO: Fix this with results
		if (stage.isWaitingForResults()) {
			throw new InvalidStageStateException("The stage is waiting for results.");
		}
		if (stage.getType().equals(StageType.TT)) {
			throw new InvalidStageTypeException("Time-trial stages cannot contain any segments.");
		}
		this.stageId = stageId;
		this.stage = stage;
		this.id = this.count++;
		this.type = type;
	}
	
	public int getId() {
		return id;
	}
	
	public Stage getStage() {
		return stage;
	}

}
