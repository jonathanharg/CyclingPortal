package cycling;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.Comparator;

public class SegmentResult implements Serializable {
  private final LocalTime finishTime;
  private int position;
  private int sprintersPoints;
  private int mountainPoints;

  protected static final Comparator<SegmentResult> sortByFinishTime =
      Comparator.comparing(SegmentResult::getFinishTime);

  public SegmentResult(LocalTime finishTime) {
    this.finishTime = finishTime;
  }

  public LocalTime getFinishTime() {
    return finishTime;
  }

  public void setPosition(int position) {
    this.position = position;
  }

  public int getPosition() {
    return position;
  }

  public void setMountainPoints(int points) {
    this.mountainPoints = points;
  }

  public void setSprintersPoints(int points) {
    this.sprintersPoints = points;
  }

  public int getMountainPoints() {
    return this.mountainPoints;
  }

  public int getSprintersPoints() {
    return this.sprintersPoints;
  }
}
