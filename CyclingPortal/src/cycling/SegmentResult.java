package cycling;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Comparator;

/** This represents a given recorded result in a segment. */
public class SegmentResult implements Serializable {
  private final LocalTime finishTime;
  private int position;
  private int sprintersPoints;
  private int mountainPoints;

  // A comparator which sorts SegmentResults based on Elapsed Time in ascending order. The
  // result with the shortest time will come first.
  protected static final Comparator<SegmentResult> sortByFinishTime =
      Comparator.comparing(SegmentResult::getFinishTime);

  /**
   * Constructor for a given result in a stage.
   *
   * @param finishTime The time at which the segment was finished.
   */
  public SegmentResult(LocalTime finishTime) {
    this.finishTime = finishTime;
  }

  /**
   * A method to get the time at which the segment was finished.
   *
   * @return The LocalTime at which the segment was finished.
   */
  public LocalTime getFinishTime() {
    return finishTime;
  }

  /**
   * A method to set the position in a stage.
   *
   * @param position the position of the rider in the stage.
   */
  public void setPosition(int position) {
    this.position = position;
  }

  /**
   * A method to set the mountain points in a stage.
   *
   * @param points the mountain points received in the stage.
   */
  public void setMountainPoints(int points) {
    this.mountainPoints = points;
  }

  /**
   * A method to set the sprinters points in a stage.
   *
   * @param points the sprinters points received in the stage.
   */
  public void setSprintersPoints(int points) {
    this.sprintersPoints = points;
  }

  /**
   * A method to get the mountain points in a stage.
   *
   * @return the mountain points received in the stage
   */
  public int getMountainPoints() {
    return this.mountainPoints;
  }

  /**
   * A method to get the sprinters points in a stage.
   *
   * @return the sprinters points received in the stage.
   */
  public int getSprintersPoints() {
    return this.sprintersPoints;
  }
}
