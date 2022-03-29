package cycling;

import java.time.Duration;
import java.time.LocalTime;
import java.util.Comparator;

public class StageResult {
  private final LocalTime[] checkpoints;
  private final Duration elapsedTime;
  private Duration adjustedElapsedTime;
  private int position;
  private int sprintersPoints;
  private int mountainPoints;

  protected static final Comparator<StageResult> sortByElapsedTime =
      Comparator.comparing(StageResult::getElapsedTime);

  public StageResult(LocalTime[] checkpoints) {
    this.checkpoints = checkpoints;
    this.elapsedTime = Duration.between(checkpoints[0], checkpoints[checkpoints.length - 1]);
  }

  public LocalTime[] getCheckpoints() {
    return this.checkpoints;
  }

  public Duration getElapsedTime() {
    return elapsedTime;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public void setAdjustedElapsedTime(Duration adjustedElapsedTime) {
    this.adjustedElapsedTime = adjustedElapsedTime;
  }

  public int getPosition() {
    return position;
  }

  public Duration getAdjustedElapsedTime() {
    return adjustedElapsedTime;
  }

  public LocalTime getAdjustedElapsedLocalTime() {
    return checkpoints[0].plus(adjustedElapsedTime);
  }

  public void setMountainPoints(int points) {
    this.mountainPoints = points;
  }

  public void setSprintersPoints(int points) {
    this.sprintersPoints = points;
  }

  public int getMountainPoints() {
    return mountainPoints;
  }

  public int getSprintersPoints() {
    return sprintersPoints;
  }

  // --Commented out by Inspection START (28/03/2022, 3:31 pm):
  //	public void add(StageResult res){
  //		this.elapsedTime = this.elapsedTime.plus(res.getElapsedTime());
  //		this.adjustedElapsedTime = this.adjustedElapsedTime.plus(res.getAdjustedElapsedTime());
  //		this.sprintersPoints += res.getSprintersPoints();
  //		this.mountainPoints += res.getMountainPoints();
  //	}
  // --Commented out by Inspection STOP (28/03/2022, 3:31 pm)
}
