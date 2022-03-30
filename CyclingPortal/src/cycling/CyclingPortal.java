package cycling;

import java.io.*;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

// TODO:
//		- Asserts !!!!
//		- Code Formatting
//		- Documentation/Comments
//		- Testing
//		- each function public/private/protected/default
// 		- Optimise results?

public class CyclingPortal implements CyclingPortalInterface {
  // ArrayLists for all of a cycling portal instances teams, riders, races, stages and segments.
  // Although HashMaps could have been used here to get riders by int ID, it would be slower in the
  // long run as we would need to constantly convert it back to arrays to output results.
  private ArrayList<Team> teams = new ArrayList<>();
  private ArrayList<Rider> riders = new ArrayList<>();
  private ArrayList<Race> races = new ArrayList<>();
  private ArrayList<Stage> stages = new ArrayList<>();
  private ArrayList<Segment> segments = new ArrayList<>();

  /**
   * Determine if a string contains any illegal whitespace characters.
   *
   * @param string The input string to be tested for whitespace.
   * @return A boolean, true if the input string contains whitespace, false if not.
   */
  public static boolean containsWhitespace(String string) {
    for (int i = 0; i < string.length(); ++i) {
      if (Character.isWhitespace(string.charAt(i))) {
        return true;
      }
    }
    return false;
  }

  /**
   * Get a Team object by a Team ID.
   *
   * @param ID The int ID of the Team to be looked up.
   * @return The Team object of the team, if one is found.
   * @throws IDNotRecognisedException Thrown if no team is found with the given Team ID.
   */
  public Team getTeamById(int ID) throws IDNotRecognisedException {
    for (Team team : teams) {
      if (team.getId() == ID) {
        return team;
      }
    }
    throw new IDNotRecognisedException("Team ID not found.");
  }

  /**
   * Get a Rider object by a Rider ID.
   *
   * @param ID The int ID of the Rider to be looked up.
   * @return The Rider object of the Rider, if one is found.
   * @throws IDNotRecognisedException Thrown if no rider is found with the given Rider ID.
   */
  public Rider getRiderById(int ID) throws IDNotRecognisedException {
    for (Rider rider : riders) {
      if (rider.getId() == ID) {
        return rider;
      }
    }
    throw new IDNotRecognisedException("Rider ID not found.");
  }

  /**
   * Get a Race object by a Race ID.
   *
   * @param ID The int ID of the Race to be looked up.
   * @return The Race object of the race, if one is found.
   * @throws IDNotRecognisedException Thrown if no race is found with the given Race ID.
   */
  public Race getRaceById(int ID) throws IDNotRecognisedException {
    for (Race race : races) {
      if (race.getId() == ID) {
        return race;
      }
    }
    throw new IDNotRecognisedException("Race ID not found.");
  }

  /**
   * Get a Stage object by a Stage ID.
   *
   * @param ID The int ID of the Stage to be looked up.
   * @return The Stage object of the stage, if one is found.
   * @throws IDNotRecognisedException Thrown if no stage is found with the given Stage ID.
   */
  public Stage getStageById(int ID) throws IDNotRecognisedException {
    for (Stage stage : stages) {
      if (stage.getId() == ID) {
        return stage;
      }
    }
    throw new IDNotRecognisedException("Stage ID not found.");
  }

  /**
   * Get a Segment object by a Segment ID.
   *
   * @param ID The int ID of the Segment to be looked up.
   * @return The Segment object of the segment, if one is found.
   * @throws IDNotRecognisedException Thrown if no segment is found with the given Segment ID.
   */
  public Segment getSegmentById(int ID) throws IDNotRecognisedException {
    for (Segment segment : segments) {
      if (segment.getId() == ID) {
        return segment;
      }
    }
    throw new IDNotRecognisedException("Segment ID not found.");
  }

  /**
   * Loops over all races, stages and segments to remove all of a given riders results.
   *
   * @param rider The Rider object whose results will be removed from the Cycling Portal.
   */
  public void removeRiderResults(Rider rider) {
    for (Race race : races) {
      race.removeRiderResults(rider);
    }
    for (Stage stage : stages) {
      stage.removeRiderResults(rider);
    }
    for (Segment segment : segments) {
      segment.removeRiderResults(rider);
    }
  }

  @Override
  public int[] getRaceIds() {
    int[] raceIDs = new int[races.size()];
    for (int i = 0; i < races.size(); i++) {
      Race race = races.get(i);
      raceIDs[i] = race.getId();
    }
    return raceIDs;
  }

  @Override
  public int createRace(String name, String description)
      throws IllegalNameException, InvalidNameException {
    // Check a race with this name does not already exist in the system.
    for (Race race : races) {
      if (race.getName().equals(name)) {
        throw new IllegalNameException("A Race with the name " + name + " already exists.");
      }
    }
    Race race = new Race(name, description);
    races.add(race);
    return race.getId();
  }

  @Override
  public String viewRaceDetails(int raceId) throws IDNotRecognisedException {
    Race race = getRaceById(raceId);
    return race.getDetails();
  }

  @Override
  public void removeRaceById(int raceId) throws IDNotRecognisedException {
    Race race = getRaceById(raceId);
    // Remove all the races stages from the CyclingPortal.
    for (final Stage stage : race.getStages()) {
      stages.remove(stage);
    }
    races.remove(race);
  }

  @Override
  public int getNumberOfStages(int raceId) throws IDNotRecognisedException {
    Race race = getRaceById(raceId);
    return race.getStages().size();
  }

  @Override
  public int addStageToRace(
      int raceId,
      String stageName,
      String description,
      double length,
      LocalDateTime startTime,
      StageType type)
      throws IDNotRecognisedException, IllegalNameException, InvalidNameException,
          InvalidLengthException {
    Race race = getRaceById(raceId);
    // Check a stage with this name does not already exist in the system.
    for (final Stage stage : stages) {
      if (stage.getName().equals(stageName)) {
        throw new IllegalNameException("A stage with the name " + stageName + " already exists.");
      }
    }
    Stage stage = new Stage(race, stageName, description, length, startTime, type);
    stages.add(stage);
    race.addStage(stage);
    return stage.getId();
  }

  @Override
  public int[] getRaceStages(int raceId) throws IDNotRecognisedException {
    Race race = getRaceById(raceId);
    ArrayList<Stage> raceStages = race.getStages();
    int[] raceStagesId = new int[raceStages.size()];
    // Gathers the Stage ID's of the Stages in the Race.
    for (int i = 0; i < raceStages.size(); i++) {
      Stage stage = race.getStages().get(i);
      raceStagesId[i] = stage.getId();
    }
    return raceStagesId;
  }

  @Override
  public double getStageLength(int stageId) throws IDNotRecognisedException {
    Stage stage = getStageById(stageId);
    return stage.getLength();
  }

  @Override
  public void removeStageById(int stageId) throws IDNotRecognisedException {
    Stage stage = getStageById(stageId);
    Race race = stage.getRace();
    // Removes stage from both the Races and Stages.
    race.removeStage(stage);
    stages.remove(stage);
  }

  @Override
  public int addCategorizedClimbToStage(
      int stageId, Double location, SegmentType type, Double averageGradient, Double length)
      throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
          InvalidStageTypeException {
    Stage stage = getStageById(stageId);
    CategorizedClimb climb = new CategorizedClimb(stage, location, type, averageGradient, length);
    // Adds Categorized Climb to both the list of Segments and the Stage.
    segments.add(climb);
    stage.addSegment(climb);
    return climb.getId();
  }

  @Override
  public int addIntermediateSprintToStage(int stageId, double location)
      throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
          InvalidStageTypeException {
    Stage stage = getStageById(stageId);
    IntermediateSprint sprint = new IntermediateSprint(stage, location);
    // Adds Intermediate Sprint to both the list of Segments and the Stage.
    segments.add(sprint);
    stage.addSegment(sprint);
    return sprint.getId();
  }

  @Override
  public void removeSegment(int segmentId)
      throws IDNotRecognisedException, InvalidStageStateException {
    Segment segment = getSegmentById(segmentId);
    Stage stage = segment.getStage();
    // Removes Segment from both the Stage and list of Segments.
    stage.removeSegment(segment);
    segments.remove(segment);
  }

  @Override
  public void concludeStagePreparation(int stageId)
      throws IDNotRecognisedException, InvalidStageStateException {
    Stage stage = getStageById(stageId);
    stage.concludePreparation();
  }

  @Override
  public int[] getStageSegments(int stageId) throws IDNotRecognisedException {
    Stage stage = getStageById(stageId);
    ArrayList<Segment> stageSegments = stage.getSegments();
    int[] stageSegmentsId = new int[stageSegments.size()];
    // Gathers Segment ID's from the Segments in the Stage.
    for (int i = 0; i < stageSegments.size(); i++) {
      Segment segment = stageSegments.get(i);
      stageSegmentsId[i] = segment.getId();
    }
    return stageSegmentsId;
  }

  @Override
  public int createTeam(String name, String description)
      throws IllegalNameException, InvalidNameException {
    // Checks if the Team name already exists on the system.
    for (final Team team : teams) {
      if (team.getName().equals(name)) {
        throw new IllegalNameException("A Team with the name " + name + " already exists.");
      }
    }
    Team team = new Team(name, description);
    teams.add(team);
    return team.getId();
  }

  @Override
  public void removeTeam(int teamId) throws IDNotRecognisedException {
    Team team = getTeamById(teamId);
    // Loops through and removes Team Riders and Team Rider Results.
    for (final Rider rider : team.getRiders()) {
      removeRiderResults(rider);
      riders.remove(rider);
    }
    teams.remove(team);
  }

  @Override
  public int[] getTeams() {
    int[] teamIDs = new int[teams.size()];
    for (int i = 0; i < teams.size(); i++) {
      Team team = teams.get(i);
      teamIDs[i] = team.getId();
    }
    return teamIDs;
  }

  @Override
  public int[] getTeamRiders(int teamId) throws IDNotRecognisedException {
    Team team = getTeamById(teamId);
    ArrayList<Rider> teamRiders = team.getRiders();
    int[] teamRiderIds = new int[teamRiders.size()];
    // Gathers ID's of Riders in the Team.
    for (int i = 0; i < teamRiderIds.length; i++) {
      teamRiderIds[i] = teamRiders.get(i).getId();
    }
    return teamRiderIds;
  }

  @Override
  public int createRider(int teamID, String name, int yearOfBirth)
      throws IDNotRecognisedException, IllegalArgumentException {
    Team team = getTeamById(teamID);
    Rider rider = new Rider(team, name, yearOfBirth);
    // Adds Rider to both the Team and the list of Riders.
    team.addRider(rider);
    riders.add(rider);
    return rider.getId();
  }

  @Override
  public void removeRider(int riderId) throws IDNotRecognisedException {
    Rider rider = getRiderById(riderId);
    removeRiderResults(rider);
    // Removes Rider from both the Team and the list of Riders.
    rider.getTeam().removeRider(rider);
    riders.remove(rider);
  }

  @Override
  public void registerRiderResultsInStage(int stageId, int riderId, LocalTime... checkpoints)
      throws IDNotRecognisedException, DuplicatedResultException, InvalidCheckpointsException,
          InvalidStageStateException {
    Stage stage = getStageById(stageId);
    Rider rider = getRiderById(riderId);
    stage.registerResult(rider, checkpoints);
  }

  @Override
  public LocalTime[] getRiderResultsInStage(int stageId, int riderId)
      throws IDNotRecognisedException {
    Stage stage = getStageById(stageId);
    Rider rider = getRiderById(riderId);
    StageResult result = stage.getRiderResult(rider);

    if (result == null) {
      // Returns an empty array if the Result is null.
      return new LocalTime[] {};
    } else {
      LocalTime[] checkpoints = result.getCheckpoints();
      // Rider Results will always be 1 shorter than the checkpoint length because
      // the finish time checkpoint will be replaced with the Elapsed Time and the start time
      // checkpoint will be ignored.
      LocalTime[] resultsInStage = new LocalTime[checkpoints.length - 1];
      LocalTime elapsedTime = LocalTime.MIDNIGHT.plus(result.getElapsedTime());
      for (int i = 0; i < resultsInStage.length; i++) {
        if (i == resultsInStage.length - 1) {
          // Adds the Elapsed Time to the end of the array of Results.
          resultsInStage[i] = elapsedTime;
        } else {
          // Adds each checkpoint to the array of Results until all have been added, skipping the
          // Start time checkpoint.
          resultsInStage[i] = checkpoints[i + 1];
        }
      }
      return resultsInStage;
    }
  }

  @Override
  public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId)
      throws IDNotRecognisedException {
    Stage stage = getStageById(stageId);
    Rider rider = getRiderById(riderId);
    StageResult result = stage.getRiderResult(rider);
    if (result == null) {
      return null;
    } else {
      return result.getAdjustedElapsedLocalTime();
    }
  }

  @Override
  public void deleteRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
    Stage stage = getStageById(stageId);
    Rider rider = getRiderById(riderId);
    stage.removeRiderResults(rider);
  }

  @Override
  public int[] getRidersRankInStage(int stageId) throws IDNotRecognisedException {
    Stage stage = getStageById(stageId);
    // Gets a list of Riders from the Stage ordered by their Elapsed Times.
    List<Rider> riders = stage.getRidersByElapsedTime();
    int[] riderIds = new int[riders.size()];
    // Gathers ID's from the ordered list of Riders.
    for (int i = 0; i < riders.size(); i++) {
      riderIds[i] = riders.get(i).getId();
    }
    return riderIds;
  }

  @Override
  public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId)
      throws IDNotRecognisedException {
    Stage stage = getStageById(stageId);
    // Gets a list of Riders from the Stage ordered by their Elapsed Times.
    List<Rider> riders = stage.getRidersByElapsedTime();
    LocalTime[] riderAETs = new LocalTime[riders.size()];
    // Gathers Riders' Adjusted Elapsed Times ordered by their Elapsed Times.
    for (int i = 0; i < riders.size(); i++) {
      Rider rider = riders.get(i);
      riderAETs[i] = stage.getRiderResult(rider).getAdjustedElapsedLocalTime();
    }
    return riderAETs;
  }

  @Override
  public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
    Stage stage = getStageById(stageId);
    // Gets a list of Riders from the Stage ordered by their Elapsed Times.
    List<Rider> riders = stage.getRidersByElapsedTime();
    int[] riderSprinterPoints = new int[riders.size()];
    // Gathers Sprinters' Points ordered by their Elapsed Times.
    for (int i = 0; i < riders.size(); i++) {
      Rider rider = riders.get(i);
      riderSprinterPoints[i] = stage.getRiderResult(rider).getSprintersPoints();
    }
    return riderSprinterPoints;
  }

  @Override
  public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
    Stage stage = getStageById(stageId);
    // Gets a list of Riders from the Stage ordered by their Elapsed Times.
    List<Rider> riders = stage.getRidersByElapsedTime();
    int[] riderMountainPoints = new int[riders.size()];
    // Gathers Riders' Mountain Points ordered by their Elapsed Times.
    for (int i = 0; i < riders.size(); i++) {
      Rider rider = riders.get(i);
      riderMountainPoints[i] = stage.getRiderResult(rider).getMountainPoints();
    }
    return riderMountainPoints;
  }

  @Override
  public void eraseCyclingPortal() {
    // Replaces teams, riders, races, stages and segments with empty ArrayLists.
    teams = new ArrayList<>();
    riders = new ArrayList<>();
    races = new ArrayList<>();
    stages = new ArrayList<>();
    segments = new ArrayList<>();
    // Sets the ID counters of the Rider, Team, Race, Stage and Segment objects back
    // to 0.
    Rider.resetIdCounter();
    Team.resetIdCounter();
    Race.resetIdCounter();
    Stage.resetIdCounter();
    Segment.resetIdCounter();
  }

  @Override
  public void saveCyclingPortal(String filename) throws IOException {
    FileOutputStream file = new FileOutputStream(filename + ".ser");
    ObjectOutputStream output = new ObjectOutputStream(file);
    // Saves teams, riders, races, stages and segments ArrayLists.
    // Saves ID counters of Team, Rider, Race, Stage and Segment objects.
    SavedCyclingPortal savedCyclingPortal =
        new SavedCyclingPortal(
            teams,
            riders,
            races,
            stages,
            segments,
            Team.getIdCounter(),
            Rider.getIdCounter(),
            Race.getIdCounter(),
            Stage.getIdCounter(),
            Segment.getIdCounter());
    output.writeObject(savedCyclingPortal);
    output.close();
    file.close();
  }

  @Override
  public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
    eraseCyclingPortal();
    FileInputStream file = new FileInputStream(filename + ".ser");
    ObjectInputStream input = new ObjectInputStream(file);

    SavedCyclingPortal savedCyclingPortal = (SavedCyclingPortal) input.readObject();
    // Imports teams, riders, races, stages and segments ArrayLists from the last save.
    teams = savedCyclingPortal.teams;
    riders = savedCyclingPortal.riders;
    races = savedCyclingPortal.races;
    stages = savedCyclingPortal.stages;
    segments = savedCyclingPortal.segments;

    // Imports ID counters of Team, Rider, Race, Stage and Segment objects from the last save.
    Team.setIdCounter(savedCyclingPortal.teamIdCount);
    Rider.setIdCounter(savedCyclingPortal.riderIdCount);
    Race.setIdCounter(savedCyclingPortal.raceIdCount);
    Stage.setIdCounter(savedCyclingPortal.stageIdCount);
    Segment.setIdCounter(savedCyclingPortal.segmentIdCount);

    input.close();
    file.close();
  }

  @Override
  public void removeRaceByName(String name) throws NameNotRecognisedException {
    for (final Race race : races) {
      if (race.getName().equals(name)) {
        races.remove(race);
        return;
      }
    }
    throw new NameNotRecognisedException("Race name is not in the system.");
  }

  @Override
  public int[] getRidersGeneralClassificationRank(int raceId) throws IDNotRecognisedException {
    Race race = getRaceById(raceId);
    List<Rider> riders = race.getRidersByAdjustedElapsedTime();
    int[] riderIds = new int[riders.size()];
    // Gathers Rider ID's ordered by their Adjusted Elapsed Times.
    for (int i = 0; i < riders.size(); i++) {
      riderIds[i] = riders.get(i).getId();
    }
    return riderIds;
  }

  @Override
  public LocalTime[] getGeneralClassificationTimesInRace(int raceId)
      throws IDNotRecognisedException {
    Race race = getRaceById(raceId);
    // Gets a list of Riders from the Stage ordered by their Adjusted Elapsed Times.
    List<Rider> riders = race.getRidersByAdjustedElapsedTime();
    LocalTime[] riderTimes = new LocalTime[riders.size()];
    // Gathers Riders' Cumulative Adjusted Elapsed LocalTimes ordered by their Adjusted Elapsed
    // Times.
    for (int i = 0; i < riders.size(); i++) {
      riderTimes[i] = race.getRiderResults(riders.get(i)).getCumulativeAdjustedElapsedLocalTime();
    }
    return riderTimes;
  }

  @Override
  public int[] getRidersPointsInRace(int raceId) throws IDNotRecognisedException {
    Race race = getRaceById(raceId);
    List<Rider> riders = race.getRidersByAdjustedElapsedTime();
    int[] riderIds = new int[riders.size()];
    // Gathers Riders' Cumulative Sprinters Points ordered by their Adjusted Elapsed Times.
    for (int i = 0; i < riders.size(); i++) {
      riderIds[i] = race.getRiderResults(riders.get(i)).getCumulativeSprintersPoints();
    }
    return riderIds;
  }

  @Override
  public int[] getRidersMountainPointsInRace(int raceId) throws IDNotRecognisedException {
    Race race = getRaceById(raceId);
    List<Rider> riders = race.getRidersByAdjustedElapsedTime();
    int[] riderIds = new int[riders.size()];
    // Gathers Riders' Cumulative Mountain Points ordered by their Adjusted Elapsed Times.
    for (int i = 0; i < riders.size(); i++) {
      riderIds[i] = race.getRiderResults(riders.get(i)).getCumulativeMountainPoints();
    }
    return riderIds;
  }

  @Override
  public int[] getRidersPointClassificationRank(int raceId) throws IDNotRecognisedException {
    Race race = getRaceById(raceId);
    List<Rider> riders = race.getRidersBySprintersPoints();
    int[] riderIds = new int[riders.size()];
    // Gathers Rider ID's ordered by their Sprinters Points.
    for (int i = 0; i < riders.size(); i++) {
      riderIds[i] = riders.get(i).getId();
    }
    return riderIds;
  }

  @Override
  public int[] getRidersMountainPointClassificationRank(int raceId)
      throws IDNotRecognisedException {
    Race race = getRaceById(raceId);
    List<Rider> riders = race.getRidersByMountainPoints();
    int[] riderIds = new int[riders.size()];
    // Gathers Rider ID's ordered by their Mountain Points.
    for (int i = 0; i < riders.size(); i++) {
      riderIds[i] = riders.get(i).getId();
    }
    return riderIds;
  }
}
