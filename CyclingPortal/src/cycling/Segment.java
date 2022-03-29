package cycling;

import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Segment {
  private static int count = 0;
  private final Stage stage;
  private final int id;
  private final SegmentType type;
  private final double location;

  private final HashMap<Rider, SegmentResult> results = new HashMap<>();

  private static final int[] SPRINT_POINTS = {20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
  private static final int[] HC_POINTS = {20, 15, 12, 10, 8, 6, 4, 2};
  private static final int[] C1_POINTS = {10, 8, 6, 4, 2, 1};
  private static final int[] C2_POINTS = {5, 3, 2, 1};
  private static final int[] C3_POINTS = {2, 1};
  private static final int[] C4_POINTS = {1};

  public Segment(Stage stage, SegmentType type, double location)
      throws InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
    if (location > stage.getLength()) {
      throw new InvalidLocationException("The location is out of bounds of the stage length.");
    }
    if (stage.isWaitingForResults()) {
      throw new InvalidStageStateException("The stage is waiting for results.");
    }
    if (stage.getType().equals(StageType.TT)) {
      throw new InvalidStageTypeException("Time-trial stages cannot contain any segments.");
    }
    this.stage = stage;
    this.id = Segment.count++;
    this.type = type;
    this.location = location;
  }

  static void resetIdCounter() {
    count = 0;
  }

  static int getIdCounter() {
    return count;
  }

  static void setIdCounter(int newCount) {
    count = newCount;
  }

  public SegmentType getType() {
    return type;
  }

  public int getId() {
    return id;
  }

  public Stage getStage() {
    return stage;
  }

  public double getLocation() {
    return location;
  }

  public void registerResults(Rider rider, LocalTime finishTime) {
    SegmentResult result = new SegmentResult(finishTime);
    results.put(rider, result);
  }

  public SegmentResult getRiderResult(Rider rider) {
    calculateResults();
    return results.get(rider);
  }

  public void removeRiderResults(Rider rider) {
    results.remove(rider);
  }

  private List<Rider> sortRiderResults() {
    return results.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(SegmentResult.sortByFinishTime))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  private void calculateResults() {
    List<Rider> riders = sortRiderResults();

    for (int i = 0; i < results.size(); i++) {
      Rider rider = riders.get(i);
      SegmentResult result = results.get(rider);
      int position = i + 1;
      // Position Calculation
      result.setPosition(position);

      // Points Calculation
      int[] pointsDistribution = getPointsDistribution();
      if (position <= pointsDistribution.length) {
        int points = pointsDistribution[i];
        if (this.type.equals(SegmentType.SPRINT)) {
          result.setSprintersPoints(points);
          result.setMountainPoints(0);
        } else {
          result.setSprintersPoints(0);
          result.setMountainPoints(points);
        }
      } else {
        result.setMountainPoints(0);
        result.setSprintersPoints(0);
      }
    }
  }

  private int[] getPointsDistribution() {
    return switch (type) {
      case HC -> HC_POINTS;
      case C1 -> C1_POINTS;
      case C2 -> C2_POINTS;
      case C3 -> C3_POINTS;
      case C4 -> C4_POINTS;
      case SPRINT -> SPRINT_POINTS;
    };
  }
}
