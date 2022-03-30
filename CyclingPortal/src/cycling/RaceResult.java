package cycling;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;

/**
 * This represents a given riders results in a race. The riders adjusted elapsed time, sprinters points and mountain points over all stages and segments are recorded here.
 */
public class RaceResult implements Serializable {
  private Duration cumulativeAdjustedElapsedTime = Duration.ZERO;
  private int cumulativeSprintersPoints = 0;
  private int cumulativeMountainPoints = 0;

  // A comparator which sorts RaceResults based on Adjusted Elapsed Time in ascending order. The result with the shortest time will come first.
  protected static final Comparator<RaceResult> sortByAdjustedElapsedTime =
      Comparator.comparing(RaceResult::getCumulativeAdjustedElapsedTime);

  // A comparator which sorts RaceResults based on Sprinters Points in descending order. The result with the most points will come first.
  protected static final Comparator<RaceResult> sortBySprintersPoints =
      (RaceResult result1, RaceResult result2) ->
          Integer.compare(
              result2.getCumulativeSprintersPoints(), result1.getCumulativeSprintersPoints());

  // A comparator which sorts RaceResults based on Mountain Points in descending order. The result with the most points will come first.
  protected static final Comparator<RaceResult> sortByMountainPoints =
      (RaceResult result1, RaceResult result2) ->
          Integer.compare(
              result2.getCumulativeMountainPoints(), result1.getCumulativeMountainPoints());

  /**
   * A method to get the recorded Adjusted Elapsed Time over all stages.
   * @return The cumulative adjusted elapsed time as a duration.
   */
  public Duration getCumulativeAdjustedElapsedTime() {
    return this.cumulativeAdjustedElapsedTime;
  }

  /**
   * A method to get the recorded Adjusted Elapsed Time over all stages as a LocalTime.
   * @return The cumulative adjusted elapsed time as a Local Time
   */
  public LocalTime getCumulativeAdjustedElapsedLocalTime() {
    // Calculated the AET as a Local time by adding the duration to midnight: 0:00 + Duration
    return LocalTime.MIDNIGHT.plus(this.cumulativeAdjustedElapsedTime);
  }

  /**
   * A method to get the recorded Mountain Points over all stages and segments.
   * @return The cumulative mountain points.
   */
  public int getCumulativeMountainPoints() {
    return this.cumulativeMountainPoints;
  }

  /**
   * A method to get the recorded Sprinters Points over all stages and segments.
   * @return The cumulative sprinters points.
   */
  public int getCumulativeSprintersPoints() {
    return this.cumulativeSprintersPoints;
  }

  /**
   * A method to add a stage result to the race result. This is useful as a riders results in a race is just a sum of their results in all a races stages. E.g. RaceResults = Stage1Result + Stage2Result + Stage3Result + ... 
   * @param stageResult the stage results which should be added to a race result.
   */
  public void addStageResult(StageResult stageResult) {
    this.cumulativeAdjustedElapsedTime =
        this.cumulativeAdjustedElapsedTime.plus(stageResult.getAdjustedElapsedTime());
    this.cumulativeSprintersPoints += stageResult.getSprintersPoints();
    this.cumulativeMountainPoints += stageResult.getMountainPoints();
  }
}
