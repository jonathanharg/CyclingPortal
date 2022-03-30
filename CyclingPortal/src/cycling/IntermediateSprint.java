package cycling;

/**
 * Intermediate Sprint class. This represents a type of Segment that has a stage and
 * a location.
 */
public class IntermediateSprint extends Segment {
  private final double location;

  /**
   * Constructor method that sets the Intermediate Sprint up with a stage and a location.
   * @param stage of the Intermediate Sprint.
   * @param location of the Intermediate Sprint
   * @throws InvalidLocationException Thrown if the location is out of bounds of the 
   * Stage length.
   * @throws InvalidStageStateException Thrown if the Stage is waiting for results.
   * @throws InvalidStageTypeException Thrown if the type is a time trial.
   */
  public IntermediateSprint(Stage stage, double location)
      throws InvalidLocationException, InvalidStageTypeException, InvalidStageStateException {
    super(stage, SegmentType.SPRINT, location);
    this.location = location;
  }
}
