package cycling;

import java.io.Serializable;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Stage Class.
 */
public class Stage  implements Serializable {
  private final Race race;
  private final String name;
  private final String description;
  private final double length;
  private final LocalDateTime startTime;
  private final StageType type;
  private final int id;
  private static int count = 0;
  private boolean waitingForResults = false;
  private final ArrayList<Segment> segments = new ArrayList<>();

  private final HashMap<Rider, StageResult> results = new HashMap<>();

  private static final int[] FLAT_POINTS = {50, 30, 20, 18, 16, 14, 12, 10, 8, 7, 6, 5, 4, 3, 2};
  private static final int[] MEDIUM_POINTS = {30, 25, 22, 19, 17, 15, 13, 11, 9, 7, 6, 5, 4, 3, 2};
  private static final int[] HIGH_POINTS = {20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};
  private static final int[] TT_POINTS = {20, 17, 15, 13, 11, 10, 9, 8, 7, 6, 5, 4, 3, 2, 1};

  /**
   * Constructor method that sets a Stage up with a race, name, description, length
   * startTime and type.
   *
   * @param race: Race that the Stage is in.
   * @param name: name of the Stage.
   * @param description: description of the Stage.
   * @param length: length of the Stage.
   * @param startTime: start time of the Stage.
   * @param type: the type of Stage.
   * @throws InvalidNameException Thrown if the name is empty, null, longer than 30
   * characters or contains whitespace.
   * @throws InvalidLengthException Thrown if the length is less than 5km.
   */
  public Stage(
      Race race,
      String name,
      String description,
      double length,
      LocalDateTime startTime,
      StageType type)
      throws InvalidNameException, InvalidLengthException {
    if (name == null
        || name.isEmpty()
        || name.length() > 30
        || CyclingPortal.containsWhitespace(name)) {
      throw new InvalidNameException(
          "Stage name cannot be null, empty, have more than 30 characters or have white spaces.");
    }
    if (length < 5) {
      throw new InvalidLengthException("Length is invalid, cannot be less than 5km.");
    }
    this.name = name;
    this.description = description;
    this.race = race;
    this.length = length;
    this.startTime = startTime;
    this.type = type;
    // ID counter represents the highest known ID at the current time to ensure there
    // are no ID collisions.
    this.id = Stage.count++;
  }

  /** Method that resets the static ID counter of the Race. Used for erasing and loading. */
  static void resetIdCounter() {
    count = 0;
  }

  /**
   * Method to get the current state of the static ID counter.
   *
   * @return the highest race ID stored currently.
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
   * Method to get the ID of the Race object.
   *
   * @return id: the Race's unique ID value.
   */
  public int getId() {
    return id;
  }

  /**
   * Method to get the name of the Stage.
   *
   * @return name: the given name of the Stage.
   */
  public String getName() {
    return name;
  }

  /**
   * Method to get the length of the Stage.
   * 
   * @return length: the given length of the Stage.
   */
  public double getLength() {
    return length;
  }

  /**
   * Method to get the Stage's Race.
   * 
   * @return race: the given Race that the Stage is in.
   */
  public Race getRace() {
    return race;
  }

  /**
   * Method to get the Stage's type.
   * 
   * @return type: the given type of the Stage
   */
  public StageType getType() {
    return type;
  }

  /**
   * Method to get the Segments in the Stage.
   * 
   * @return segments: a list of Segments in the Stage.
   */
  public ArrayList<Segment> getSegments() {
    return segments;
  }

  /**
   * Method to get the start time of the Stage.
   * 
   * @return startTime: the given start time of the Stage.
   */
  public LocalDateTime getStartTime() {
    return startTime;
  }

  /**
   * Method that adds a Segment to the Stage. It is added to the list of Segments
   * based on its location in the Stage.
   * 
   * @param segment: Segment that will be added to the Stage.
   */
  public void addSegment(Segment segment) {
    // Loops through the ordered list of segments to find the correct place for the new
    // Segment to be added.
    for (int i = 0; i < segments.size(); i++) {
      // Compares the Segments based on their locations.
      // The new Segment is inserted if its location is less than the location of the 
      // current Segment it is being compared to.
      if (segment.getLocation() < segments.get(i).getLocation()) {
        segments.add(i, segment);
        return;
      }
    }
    segments.add(segment);
  }

  /**
   * Method that removes a given Segment from the Stage's Segments.
   * 
   * @param segment: the Segment intended to be removed.
   * @throws InvalidStageStateException Thrown if the Stage is waiting for results.
   */
  public void removeSegment(Segment segment) throws InvalidStageStateException {
    if (waitingForResults) {
      throw new InvalidStageStateException(
          "The segment cannot be removed as it is waiting for results.");
    }
    segments.remove(segment);
  }

  /**
   * Method that registers a Rider's result and adds it to the Stage.
   * 
   * @param rider: the Rider whose results will be registered.
   * @param checkpoints: the Rider's results.
   * @throws InvalidStageStateException Thrown if the Stage is not waiting for results.
   * @throws DuplicatedResultException Thrown if the Rider already has results registered
   * in the Stage.
   * @throws InvalidCheckpointsException Thrown if the number checkpoints doesn't equal the
   * number of Segments in the Stage + 2
   */
  public void registerResult(Rider rider, LocalTime[] checkpoints)
      throws InvalidStageStateException, DuplicatedResultException, InvalidCheckpointsException {
    if (!waitingForResults) {
      throw new InvalidStageStateException(
          "Results can only be added to a stage while it is waiting for results.");
    }
    if (results.containsKey(rider)) {
      throw new DuplicatedResultException("Each rider can only have one result per Stage.");
    }
    if (checkpoints.length != segments.size() + 2) {
      throw new InvalidCheckpointsException(
          "The length of the checkpoint must equal the number of Segments in the Stage + 2.");
    }

    StageResult result = new StageResult(checkpoints);
    // Save Riders result for the Stage
    results.put(rider, result);

    // Propagate all the Riders results for each segment
    for (int i = 0; i < segments.size(); i++) {
      segments.get(i).registerResults(rider, checkpoints[i + 1]);
    }
  }

  /**
   * Method that concludes the Stage preparation and ensures that the Stage is now
   * waiting for results.
   * 
   * @throws InvalidStageStateException Thrown if the Stage is already waiting for results.
   */
  public void concludePreparation() throws InvalidStageStateException {
    if (waitingForResults) {
      throw new InvalidStageStateException("Stage is already waiting for results.");
    }
    waitingForResults = true;
  }

  /**
   * Method to identify whether the Stage is waiting for results.
   * 
   * @return A boolean, true if the Stage is waiting for results, false if it is not.
   */
  public boolean isWaitingForResults() {
    return waitingForResults;
  }

  /**
   * Method to calculate and return the results of a given Rider.
   * 
   * @param rider: Rider whose results are desired.
   * @return results of the Rider.
   */
  public StageResult getRiderResult(Rider rider) {
    calculateResults();
    return results.get(rider);
  }

  /**
   * Method to remove the results of a Rider.
   * 
   * @param rider whose results are to be removed.
   */
  public void removeRiderResults(Rider rider) {
    results.remove(rider);
  }

  /**
   * Method to 
   * @return
   */
  public List<Rider> getRidersByElapsedTime() {
    calculateResults();
    return sortRiderResults();
  }

  public HashMap<Rider, StageResult> getStageResults() {
    calculateResults();
    return results;
  }

  private List<Rider> sortRiderResults() {
    return results.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(StageResult.sortByElapsedTime))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  private void calculateResults() {
    List<Rider> riders = sortRiderResults();

    for (int i = 0; i < results.size(); i++) {
      Rider rider = riders.get(i);
      StageResult result = results.get(rider);
      int position = i + 1;

      // Position Calculation
      result.setPosition(position);

      // Adjusted Elapsed Time Calculations
      if (i == 0) {
        result.setAdjustedElapsedTime(result.getElapsedTime());
      } else {
        Rider prevRider = riders.get(i - 1);
        Duration prevTime = results.get(prevRider).getElapsedTime();
        Duration time = results.get(rider).getElapsedTime();

        int timeDiff = time.minus(prevTime).compareTo(Duration.ofSeconds(1));
        if (timeDiff <= 0) {
          // Close Finish Condition
          Duration prevAdjustedTime = results.get(prevRider).getAdjustedElapsedTime();
          result.setAdjustedElapsedTime(prevAdjustedTime);
        } else {
          // Far Finish Condition
          result.setAdjustedElapsedTime(time);
        }
      }

      // Points Calculation
      int sprintersPoints = 0;
      int mountainPoints = 0;
      for (Segment segment : segments) {
        SegmentResult segmentResult = segment.getRiderResult(rider);
        sprintersPoints += segmentResult.getSprintersPoints();
        mountainPoints += segmentResult.getMountainPoints();
      }
      int[] pointsDistribution = getPointDistribution();
      if (position <= pointsDistribution.length) {
        sprintersPoints += pointsDistribution[i];
      }
      result.setSprintersPoints(sprintersPoints);
      result.setMountainPoints(mountainPoints);
    }
  }

  private int[] getPointDistribution() {
    return switch (type) {
      case FLAT -> FLAT_POINTS;
      case MEDIUM_MOUNTAIN -> MEDIUM_POINTS;
      case HIGH_MOUNTAIN -> HIGH_POINTS;
      case TT -> TT_POINTS;
    };
  }
}
