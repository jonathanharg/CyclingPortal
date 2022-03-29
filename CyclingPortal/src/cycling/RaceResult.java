package cycling;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;

public class RaceResult {
  private Duration cumulativeAdjustedElapsedTime = Duration.ZERO;
  private int cumulativeSprintersPoints = 0;
  private int cumulativeMountainPoints = 0;

  // TODO: Test ordered Asc
  protected static final Comparator<RaceResult> sortByAdjustedElapsedTime =
      Comparator.comparing(RaceResult::getCumulativeAdjustedElapsedTime);

  // TODO: Test order Desc
  protected static final Comparator<RaceResult> sortBySprintersPoints =
      Comparator.comparing(RaceResult::getCumulativeSprintersPoints).reversed();
  // protected static final Comparator<RaceResult> sortBySprintersPoints = (RaceResult result1,
  // 		RaceResult result2) -> Integer.compare(result2.getCumulativeSprintersPoints(),
  // 				result1.getCumulativeSprintersPoints());
  protected static final Comparator<RaceResult> sortByMountainPoints =
      Comparator.comparing(RaceResult::getCumulativeMountainPoints).reversed();
  // protected static final Comparator<RaceResult> sortByMountainPoints = (RaceResult result1,
  // 		RaceResult result2) -> Integer.compare(result2.getCumulativeMountainPoints(),
  // 				result1.getCumulativeMountainPoints());

  public Duration getCumulativeAdjustedElapsedTime() {
    return this.cumulativeAdjustedElapsedTime;
  }

  public LocalTime getCumulativeAdjustedElapsedLocalTime() {
    return LocalTime.MIDNIGHT.plus(this.cumulativeAdjustedElapsedTime);
  }

  public int getCumulativeMountainPoints() {
    return this.cumulativeMountainPoints;
  }

  public int getCumulativeSprintersPoints() {
    return this.cumulativeSprintersPoints;
  }

  public void addStageResult(StageResult stageResult) {
    this.cumulativeAdjustedElapsedTime =
        this.cumulativeAdjustedElapsedTime.plus(stageResult.getAdjustedElapsedTime());
    this.cumulativeSprintersPoints += stageResult.getSprintersPoints();
    this.cumulativeMountainPoints += stageResult.getMountainPoints();
  }
}