package cycling;

/**
 * Categorised Climb class. This represents a type of Segment that has a stage, location, type,
 * average gradient and a length.
 */
public class CategorizedClimb extends Segment {
  private final Double averageGradient;
  private final Double length;

  /**
   * Constructor method that sets up the Categorised Climb with a stage, location, type, average
   * gradient and length.
   *
   * @param stage that the Categorised Climb is in.
   * @param location of the Categorised Climb.
   * @param type of Categorised Climb.
   * @param averageGradient of the Categorised Climb.
   * @param length of the Categorised Climb.
   * @throws InvalidLocationException Thrown if the location is out of bounds of the Stage length.
   * @throws InvalidStageStateException Thrown if the Stage is waiting for results.
   * @throws InvalidStageTypeException Thrown if the type is a time trial.
   */
  public CategorizedClimb(
      Stage stage, Double location, SegmentType type, Double averageGradient, Double length)
      throws InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
    super(stage, type, location);
    this.averageGradient = averageGradient;
    this.length = length;
  }
}
