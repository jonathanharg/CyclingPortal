package cycling;

import java.io.Serializable;
import java.time.LocalTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Segment Class. This represents a segment of a stage in a rice in the cycling portal. This deals
 * with details about the segment as well as well as the segments results.
 */
public class Segment implements Serializable {
  private static int count = 0;
  private final Stage stage;
  private final int id;
  private final SegmentType type;
  private final double location;

  private final HashMap<Rider, SegmentResult> results = new HashMap<>();

  // Segment sprinters/mountain points.
  private static final int[] SPRINT_POINTS = {20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
  private static final int[] HC_POINTS = {20, 15, 12, 10, 8, 6, 4, 2};
  private static final int[] C1_POINTS = {10, 8, 6, 4, 2, 1};
  private static final int[] C2_POINTS = {5, 3, 2, 1};
  private static final int[] C3_POINTS = {2, 1};
  private static final int[] C4_POINTS = {1};

  /**
   * Constructor method that creates a segment for a given stage, segment type and location.
   *
   * @param stage The stage object which this segment is in. The stage cannot be waiting for results
   *     or be a time-trial stage.
   * @param type The type of segment, can be either SPRINT, C4, C3, C2, C1, or HC.
   * @param location The location of the segment in the stage in kilometers, cannot be longer than
   *     the length of the stage.
   * @throws InvalidLocationException
   * @throws InvalidStageStateException
   * @throws InvalidStageTypeException
   */
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
    // ID counter represents the highest known ID at the current time to ensure
    // there
    // are no ID collisions.
    this.id = Segment.count++;
    this.type = type;
    this.location = location;
  }

  /** Reset the static segment ID counter. Used for erasing/loading the CyclingPortal. */
  static void resetIdCounter() {
    count = 0;
  }

  /**
   * Method to get the current state of the static ID counter.
   *
   * @return the highest segment ID stored currently.
   */
  static int getIdCounter() {
    return count;
  }

  /**
   * Method that sets the static ID counter to a given value. Used when loading to avoid ID
   * collisions.
   *
   * @param newCount: new value of the static ID counter.
   */
  static void setIdCounter(int newCount) {
    count = newCount;
  }

  /**
   * Method to get the ID of the segment object.
   *
   * @return id: the Segments's unique ID value.
   */
  public int getId() {
    return id;
  }

  /**
   * Method to get the Stage which the segment exists in.
   *
   * @return The stage object.
   */
  public Stage getStage() {
    return stage;
  }

  /**
   * Method to get the location of the segment within the stage.
   *
   * @return the location in kilometers as a double.
   */
  public double getLocation() {
    return location;
  }

  /**
   * Method to register the time which a given rider completed the segment.
   *
   * @param rider The rider which finished the segment.
   * @param finishTime The time which the rider finished the segment.
   */
  public void registerResults(Rider rider, LocalTime finishTime) {
    // Create a segment result for the rider.
    SegmentResult result = new SegmentResult(finishTime);
    // Associate the result with the rider in the result HashMap.
    results.put(rider, result);
  }

  /**
   * Method to get a given riders results in this segment.
   *
   * @param rider The rider whose results will be returned.
   * @return The results the rider received in the segment.
   */
  public SegmentResult getRiderResult(Rider rider) {
    // First calculate the segments results, such as riders position and points.
    calculateResults();
    // Then return the results for the requested rider.
    return results.get(rider);
  }

  /**
   * Method to remove a given riders results from the segment.
   *
   * @param rider The rider object whose results should be removed.
   */
  public void removeRiderResults(Rider rider) {
    results.remove(rider);
  }

  /**
   * Private function to sort all the riders who have results registered by their finish time.
   * Useful for getting each riders position.
   *
   * @return All riders who have a registered result sorted by their finish time.
   */
  private List<Rider> sortRiderResults() {
    // convert the hashmap into a set
    return results.entrySet().stream()
        // Sort the set by the finish time of the results
        .sorted(Map.Entry.comparingByValue(SegmentResult.sortByFinishTime))
        // Get the rider element of the set and ignore the results now they have been
        // sorted and convert to a list.
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  /** Private method to calculate the results for this segment. */
  private void calculateResults() {
    // First get a list of riders sorted by their finish time.
    List<Rider> riders = sortRiderResults();

    for (int i = 0; i < results.size(); i++) {
      Rider rider = riders.get(i);
      SegmentResult result = results.get(rider);
      int position = i + 1;
      // Position Calculation
      result.setPosition(position); // Set the riders position

      // Points Calculation
      int[] pointsDistribution =
          getPointsDistribution(); // Get the point distribution based on the segment type.
      if (position <= pointsDistribution.length) {
        // Get the riders points based on their position
        int points = pointsDistribution[i];
        if (this.type.equals(SegmentType.SPRINT)) {
          // If the segment is a sprint, set the riders points as sprinters points.
          result.setSprintersPoints(points);
          result.setMountainPoints(0);
        } else {
          // If the segment is not a sprint, set the riders points as mountain points.
          result.setSprintersPoints(0);
          result.setMountainPoints(points);
        }
      } else {
        // If the rider does not finish in a point-awarding position, reward 0 points.
        result.setMountainPoints(0);
        result.setSprintersPoints(0);
      }
    }
  }

  /**
   * Private method to get the point distribution of the segment based on the type of segment.
   *
   * @return an array of integers that represent the points that should be rewarded based on the
   *     segment type.
   */
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
