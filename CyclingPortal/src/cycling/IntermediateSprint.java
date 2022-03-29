package cycling;

public class IntermediateSprint extends Segment {
  private final double location;

  public IntermediateSprint(Stage stage, double location)
      throws InvalidLocationException, InvalidStageTypeException, InvalidStageStateException {
    super(stage, SegmentType.SPRINT, location);
    this.location = location;
  }
}
