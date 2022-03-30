package cycling;

import java.io.Serializable;
import java.util.ArrayList;

/** Team class. This represents a team of riders. */
public class Team implements Serializable {
  private final String name;
  private final String description;

  private final ArrayList<Rider> riders = new ArrayList<>();
  private static int count = 0;
  private final int id;

  /**
   * Constructor method that sets up the Team with a name and a description.
   *
   * @param name of the team.
   * @param description of the team.
   * @throws InvalidNameException Thrown if the team name is null, empty, has more than 30
   *     characters or contains any whitespace.
   */
  public Team(String name, String description) throws InvalidNameException {
    if (name == null
        || name.isEmpty()
        || name.length() > 30
        || CyclingPortal.containsWhitespace(name)) {
      throw new InvalidNameException(
          "Team name cannot be null, empty, have more than 30 characters or have white spaces.");
    }
    this.name = name;
    this.description = description;
    this.id = Team.count++;
  }

  /** Method to reset the static ID counter. */
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
   * Method that gets the name of the Team.
   *
   * @return name of the Team.
   */
  public String getName() {
    return name;
  }

  /**
   * Method that gets the ID of the Team.
   *
   * @return ID of the Team.
   */
  public int getId() {
    return id;
  }

  /**
   * Method that removes a Rider from the Team.
   *
   * @param rider to be removed.
   */
  public void removeRider(Rider rider) {
    riders.remove(rider);
  }

  /**
   * Method to get the Riders in the Team.
   *
   * @return A list of Riders in the Team.
   */
  public ArrayList<Rider> getRiders() {
    return riders;
  }

  /**
   * Method that adds a Rider to the Team.
   *
   * @param rider to be added to the Team.
   */
  public void addRider(Rider rider) {
    riders.add(rider);
  }
}
