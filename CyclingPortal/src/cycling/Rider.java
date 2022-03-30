package cycling;

import java.io.Serializable;

/** Rider class which represents a rider in the CyclingPortal. */
public class Rider implements Serializable {
  private final Team team;
  private final String name;
  private final int yearOfBirth;

  // Highest used ID count, used in order to avoid ID clashes.
  private static int count = 0;
  private final int id;

  /**
   * Constructor for a Rider in a CyclingPortal
   *
   * @param team the team the rider races for.
   * @param name the riders name, which cannot be null.
   * @param yearOfBirth the riders year of birth, which must be greater than 1900.
   * @throws IllegalArgumentException thrown if the riders name is null or the riders birth year is
   *     not greater than 1900.
   */
  public Rider(Team team, String name, int yearOfBirth) throws IllegalArgumentException {
    if (name == null) {
      throw new java.lang.IllegalArgumentException("The rider's name cannot be null.");
    }
    if (yearOfBirth < 1900) {
      throw new java.lang.IllegalArgumentException(
          "The rider's birth year is invalid, must be greater than 1900.");
    }

    this.team = team;
    this.name = name;
    this.yearOfBirth = yearOfBirth;
    this.id = Rider.count++;
  }

  /** Method to reset the static rider ID counter, used for loading and erasing. */
  static void resetIdCounter() {
    count = 0;
  }

  /**
   * Method to get the static rider ID counter, used for saving.
   *
   * @return the lowest known available rider ID.
   */
  static int getIdCounter() {
    return count;
  }

  /**
   * Method to set the static rider ID counter to a specific value, used for loading and erasing.
   *
   * @param newCount the number the ID counter should be set to.
   */
  static void setIdCounter(int newCount) {
    count = newCount;
  }

  /**
   * Method to get the Riders ID.
   *
   * @return the Riders ID.
   */
  public int getId() {
    return id;
  }

  /**
   * Method to get the Riders Team.
   *
   * @return the Team the rider races for.
   */
  public Team getTeam() {
    return team;
  }
}
