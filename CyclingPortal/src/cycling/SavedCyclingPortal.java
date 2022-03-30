package cycling;

import java.io.Serializable;
import java.util.ArrayList;

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
