package cycling;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Race Class. This represents a Race that holds a Race's Stages and Riders, and also contains
 * methods that deal with these.
 */
public class Race implements Serializable {

  private final String name;
  private final String description;

  private final ArrayList<Stage> stages = new ArrayList<>();

  private final HashMap<Rider, RaceResult> results = new HashMap<>();

  private static int count = 0;
  private final int id;

  /**
   * Constructor method that sets up Rider with a name and a description.
   *
   * @param name: Cannot be empty, null, have a length greater than 30 or contain any whitespace.
   * @param description: A description of the race.
   * @throws InvalidNameException Thrown if the Race name is does not meet name requirements stated
   *     above.
   */
  public Race(String name, String description) throws InvalidNameException {
    if (name == null
        || name.isEmpty()
        || name.length() > 30
        || CyclingPortal.containsWhitespace(name)) {
      throw new InvalidNameException(
          "The name cannot be null, empty, have more than 30 characters, or have white spaces.");
    }
    this.name = name;
    this.description = description;
    // ID counter represents the highest known ID at the current time to ensure there
    // are no ID collisions.
    this.id = Race.count++;
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
   * Method that sets the static ID counter to an inputted value.
   *
   * @param newCount: new value of the static ID counter.
   */
  static void setIdCounter(int newCount) {
    count = newCount;
  }

  /**
   * Method to get the ID of the Race object.
   *
   * @return int id: the Race's unique ID value.
   */
  public int getId() {
    return id;
  }

  /**
   * Method to get the name of the Race.
   *
   * @return String name: the given name of the Race.
   */
  public String getName() {
    return name;
  }

  /**
   * Method that adds a Stage to the Race object's ordered list of Stages. It is added to the
   * correct position based on its start time.
   *
   * @param stage: The stage to be added to the Race.
   */
  public void addStage(Stage stage) {
    for (int i = 0; i < stages.size(); i++) {
      // Retrieves the start time of each Stage in the Race's current Stages one by one.
      // These are already ordered by their start times.
      LocalDateTime iStartTime = stages.get(i).getStartTime();
      // Adds the new Stage to the list of stages in the correct position based on
      // its start time.
      if (stage.getStartTime().isBefore(iStartTime)) {
        stages.add(i, stage);
        return;
      }
    }
    stages.add(stage);
  }

  /**
   * Method to get the list of Stages in the Race ordered by their start times.
   *
   * @return Arraylist<Stages> stages: The ordered list of Stages.
   */
  public ArrayList<Stage> getStages() {
    return stages;
  }

  /**
   * Method that removes a given Stage from the list of Stages.
   *
   * @param stage: the Stage to be deleted.
   */
  public void removeStage(Stage stage) {
    stages.remove(stage);
  }

  /**
   * Method to get then details of a Race including Race ID, name, description number of stages and
   * total length.
   *
   * @return String: concatenated paragraph of details.
   */
  public String getDetails() {
    double currentLength = 0;
    for (final Stage stage : stages) {
      currentLength = currentLength + stage.getLength();
    }
    return ("Race ID: "
        + id
        + System.lineSeparator()
        + "Name: "
        + name
        + System.lineSeparator()
        + "Description: "
        + description
        + System.lineSeparator()
        + "Number of Stages: "
        + stages.size()
        + System.lineSeparator()
        + "Total length: "
        + currentLength);
  }

  /**
   * Method to get a list of Riders in the Race, sorted by their Adjusted Elapsed Time.
   *
   * @return List<Rider>: correctly sorted Riders.
   */
  public List<Rider> getRidersByAdjustedElapsedTime() {
    calculateResults();
    return sortRiderResultsBy(RaceResult.sortByAdjustedElapsedTime);
  }

  /**
   * Method to get a list of Riders in the Race, sorted by their Sprinters Points.
   *
   * @return List<Rider>: correctly sorted Riders.
   */
  public List<Rider> getRidersBySprintersPoints() {
    calculateResults();
    return sortRiderResultsBy(RaceResult.sortBySprintersPoints);
  }

  /**
   * Method to get a list of Riders in the Race, sorted by their Mountain Points.
   *
   * @return List<Rider>: correctly sorted Riders.
   */
  public List<Rider> getRidersByMountainPoints() {
    calculateResults();
    return sortRiderResultsBy(RaceResult.sortByMountainPoints);
  }

  /**
   * Method to get the results of a given Rider.
   *
   * @param rider: Rider to get the results of.
   * @return RaceResult: Result of the Rider.
   */
  public RaceResult getRiderResults(Rider rider) {
    calculateResults();
    return results.get(rider);
  }

  /**
   * Method to remove the Results of a given Rider.
   *
   * @param rider: Rider whose Results will be removed.
   */
  public void removeRiderResults(Rider rider) {
    results.remove(rider);
  }

  /**
   * Method to get a list of Riders sorted by a given comparator of their Results.
   *
   * @param comparison: a comparator on the Riders' Results to sort the Riders by.
   * @return List<Rider>: List of Riders sorted by the comparator on the Results.
   */
  private List<Rider> sortRiderResultsBy(Comparator<RaceResult> comparison) {
    return results.entrySet().stream()
        .sorted(Map.Entry.comparingByValue(comparison))
        .map(Map.Entry::getKey)
        .collect(Collectors.toList());
  }

  /**
   * Method to register the Rider's Result to the Stage.
   *
   * @param rider: Rider whose Result needs to be registered.
   * @param stageResult: Stage that the Result will be added to.
   */
  private void registerRiderResults(Rider rider, StageResult stageResult) {
    if (results.containsKey(rider)) {
      // When the hashmap of Results already contains the Results for the given Rider,
      // results are not re-added.
      results.get(rider).addStageResult(stageResult);
    } else {
      // If the hashmap of Results does not contain the Results for the given Rider,
      // they then are added now.
      RaceResult raceResult = new RaceResult();
      raceResult.addStageResult(stageResult);
      results.put(rider, raceResult);
    }
  }

  /** Method that calculates the results for each Rider. */
  private void calculateResults() {
    for (Stage stage : stages) {
      HashMap<Rider, StageResult> stageResults = stage.getStageResults();
      for (Rider rider : stageResults.keySet()) {
        registerRiderResults(rider, stageResults.get(rider));
      }
    }
  }
}
