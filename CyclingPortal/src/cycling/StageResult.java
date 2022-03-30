package cycling;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;

/** This represents a given recorded result in a stage. */
public class StageResult implements Serializable {
  private final LocalTime[] checkpoints;
  private final Duration elapsedTime;
  private Duration adjustedElapsedTime;
  private int position;
  private int sprintersPoints;
  private int mountainPoints;

  // A comparator which sorts StageResults based on Elapsed Time in ascending order. The
  // result with the shortest time will come first.
  protected static final Comparator<StageResult> sortByElapsedTime =
      Comparator.comparing(StageResult::getElapsedTime);

  /**
   * Constructor for a given results in a stage.
   *
   * @param checkpoints The array of LocalTimes at which each checkpoint was crossed/
   */
  public StageResult(LocalTime[] checkpoints) {
    this.checkpoints = checkpoints;
    this.elapsedTime = Duration.between(checkpoints[0], checkpoints[checkpoints.length - 1]);
  }

  /**
   * A method to get the times at which each checkpoint was crossed.
   *
   * @return The array of LocalTimes at which each checkpoint was crossed.
   */
  public LocalTime[] getCheckpoints() {
    return this.checkpoints;
  }

  /**
   * A method to get the elapsed time since the start of the stage.
   *
   * @return The duration of time since the stage started.
   */
  public Duration getElapsedTime() {
    return elapsedTime;
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
   * A method to set the adjusted elapsed time in a stage.
   *
   * @param adjustedElapsedTime the adjusted elapsed time in the stage.
   */
  public void setAdjustedElapsedTime(Duration adjustedElapsedTime) {
    this.adjustedElapsedTime = adjustedElapsedTime;
  }

  /**
   * A method to get the adjusted elapsed time in a stage as a duration.
   *
   * @return the adjusted elapsed time as a duration.
   */
  public Duration getAdjustedElapsedTime() {
    return adjustedElapsedTime;
  }

  /**
   * A method to get the adjusted elapsed time in a stage as a duration.
   *
   * @return the adjusted elapsed time as a duration.
   */
  public LocalTime getAdjustedElapsedLocalTime() {
    return checkpoints[0].plus(adjustedElapsedTime);
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
    return mountainPoints;
  }

  /**
   * A method to get the sprinters points in a stage.
   *
   * @return the sprinters points received in the stage.
   */
  public int getSprintersPoints() {
    return sprintersPoints;
  }
}
