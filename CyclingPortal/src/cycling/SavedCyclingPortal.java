package cycling;

import java.io.Serializable;
import java.util.ArrayList;

/**
 * Class which is used to save the state of the CyclingPortal. Teams, Riders, Races, Stages, and
 * Segments are all saved along with their respective ID counters.
 */
public class SavedCyclingPortal implements Serializable {
  final ArrayList<Team> teams;
  final ArrayList<Rider> riders;
  final ArrayList<Race> races;
  final ArrayList<Stage> stages;
  final ArrayList<Segment> segments;
  final int teamIdCount;
  final int riderIdCount;
  final int raceIdCount;
  final int stageIdCount;
  final int segmentIdCount;

  /**
   * Constructor for a SavedCyclingPortal which is used in saving and loading.
   *
   * @param teams the teams to be saved.
   * @param riders the riders to be saved.
   * @param races the races to be saved.
   * @param stages the stages to be saved.
   * @param segments the segments to be saved.
   * @param teamIdCount the highest known team ID, saved in order to avoid ID clashes.
   * @param riderIdCount the highest known rider ID, saved in order to avoid ID clashes.
   * @param raceIdCount the highest known race ID, saved in order to avoid ID clashes.
   * @param stageIdCount the highest known stage ID, saved in order to avoid ID clashes.
   * @param segmentIdCount the highest known segment ID, saved in order to avoid ID clashes.
   */
  public SavedCyclingPortal(
      ArrayList<Team> teams,
      ArrayList<Rider> riders,
      ArrayList<Race> races,
      ArrayList<Stage> stages,
      ArrayList<Segment> segments,
      int teamIdCount,
      int riderIdCount,
      int raceIdCount,
      int stageIdCount,
      int segmentIdCount) {
    this.teams = teams;
    this.riders = riders;
    this.races = races;
    this.stages = stages;
    this.segments = segments;
    this.teamIdCount = teamIdCount;
    this.riderIdCount = riderIdCount;
    this.raceIdCount = raceIdCount;
    this.stageIdCount = stageIdCount;
    this.segmentIdCount = segmentIdCount;
  }
}
