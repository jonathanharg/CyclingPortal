package cycling;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

//TODO:
//		- Asserts !!!!
//		- Code Formatting
//		- Documentation/Comments
//		- Testing
//		- test all removes are removing everything associated with that thing
//		- each function public/private/protected/default
// 		- Optimise results?

public class CyclingPortal implements CyclingPortalInterface {

	private ArrayList<Team> teams = new ArrayList<>();
	private ArrayList<Rider> riders = new ArrayList<>();
	private ArrayList<Race> races = new ArrayList<>();
	private ArrayList<Stage> stages = new ArrayList<>();
	private ArrayList<Segment> segments = new ArrayList<>();

	private record SavedCyclingPortal(ArrayList<Team> teams, ArrayList<Rider> riders, ArrayList<Race> races, ArrayList<Stage> stages, ArrayList<Segment> segments, int teamIdCount, int riderIdCount, int raceIdCount, int stageIdCount, int segmentIdCount) {}

	public static boolean containsWhitespace(String str) {
		for (int i = 0; i < str.length(); ++i) {
			if (Character.isWhitespace(str.charAt(i))) {
				return true;
			}
		}
		return false;
	}

	public Team getTeamById(int ID) throws IDNotRecognisedException {
		for (Team team : teams) {
			if (team.getId() == ID) {
				return team;
			}
		}
		throw new IDNotRecognisedException("Team ID not found.");
	}

	public Rider getRiderById(int ID) throws IDNotRecognisedException {
		for (Rider rider : riders) {
			if (rider.getId() == ID) {
				return rider;
			}
		}
		throw new IDNotRecognisedException("Racer ID not found.");
	}

	public Race getRaceById(int ID) throws IDNotRecognisedException {
		for (Race race : races) {
			if (race.getId() == ID) {
				return race;
			}
		}
		throw new IDNotRecognisedException("Race ID not found.");
	}

	public Stage getStageById(int ID) throws IDNotRecognisedException {
		for (Stage stage : stages) {
			if (stage.getId() == ID) {
				return stage;
			}
		}
		throw new IDNotRecognisedException("Stage ID not found.");
	}

	public Segment getSegmentById(int ID) throws IDNotRecognisedException {
		for (Segment segment : segments) {
			if (segment.getId() == ID) {
				return segment;
			}
		}
		throw new IDNotRecognisedException("Segment ID not found.");
	}

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
	public int createRace(String name, String description) throws IllegalNameException, InvalidNameException {
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
	public int addStageToRace(int raceId, String stageName, String description, double length, LocalDateTime startTime,
			StageType type)
			throws IDNotRecognisedException, IllegalNameException, InvalidNameException, InvalidLengthException {
		Race race = getRaceById(raceId);
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
		race.removeStage(stage);
		stages.remove(stage);
	}

	@Override
	public int addCategorizedClimbToStage(int stageId, Double location, SegmentType type, Double averageGradient,
			Double length) throws IDNotRecognisedException, InvalidLocationException, InvalidStageStateException,
			InvalidStageTypeException {
		Stage stage = getStageById(stageId);
		CategorizedClimb climb = new CategorizedClimb(stage, location, type, averageGradient, length);
		segments.add(climb);
		stage.addSegment(climb);
		return climb.getId();
	}

	@Override
	public int addIntermediateSprintToStage(int stageId, double location) throws IDNotRecognisedException,
			InvalidLocationException, InvalidStageStateException, InvalidStageTypeException {
		Stage stage = getStageById(stageId);
		IntermediateSprint sprint = new IntermediateSprint(stage, location);
		segments.add(sprint);
		stage.addSegment(sprint);
		return sprint.getId();
	}

	@Override
	public void removeSegment(int segmentId) throws IDNotRecognisedException, InvalidStageStateException {
		Segment segment = getSegmentById(segmentId);
		Stage stage = segment.getStage();
		stage.removeSegment(segment);
		segments.remove(segment);
	}

	@Override
	public void concludeStagePreparation(int stageId) throws IDNotRecognisedException, InvalidStageStateException {
		Stage stage = getStageById(stageId);
		stage.concludePreparation();
	}

	@Override
	public int[] getStageSegments(int stageId) throws IDNotRecognisedException {
		Stage stage = getStageById(stageId);
		ArrayList<Segment> stageSegments = stage.getSegments();
		int[] stageSegmentsId = new int[stageSegments.size()];
		for (int i = 0; i < stageSegments.size(); i++) {
			Segment segment = stageSegments.get(i);
			stageSegmentsId[i] = segment.getId();
		}
		return stageSegmentsId;
	}

	@Override
	public int createTeam(String name, String description) throws IllegalNameException, InvalidNameException {
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
		team.addRider(rider);
		riders.add(rider);
		return rider.getId();
	}

	@Override
	public void removeRider(int riderId) throws IDNotRecognisedException {
		Rider rider = getRiderById(riderId);
		removeRiderResults(rider);
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
	public LocalTime[] getRiderResultsInStage(int stageId, int riderId) throws IDNotRecognisedException {
		Stage stage = getStageById(stageId);
		Rider rider = getRiderById(riderId);
		StageResult result = stage.getRiderResult(rider);

		if (result == null) {
			return new LocalTime[] {};
		} else {
			LocalTime[] checkpoints = result.getCheckpoints();
			LocalTime[] resultsInStage = new LocalTime[checkpoints.length + 1];
			LocalTime elapsedTime = LocalTime.MIDNIGHT.plus(result.getElapsedTime());
			for (int i = 0; i <= resultsInStage.length; i++) {
				if (i == resultsInStage.length) {
					resultsInStage[i] = elapsedTime;
				} else {
					resultsInStage[i] = checkpoints[i];
				}
			}
			return resultsInStage;
		}
	}

	@Override
	public LocalTime getRiderAdjustedElapsedTimeInStage(int stageId, int riderId) throws IDNotRecognisedException {
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
		List<Rider> riders = stage.getRidersByElapsedTime();
		int[] riderIds = new int[riders.size()];
		for (int i = 0; i < riders.size(); i++) {
			riderIds[i] = riders.get(i).getId();
		}
		return riderIds;
	}

	@Override
	public LocalTime[] getRankedAdjustedElapsedTimesInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = getStageById(stageId);
		List<Rider> riders = stage.getRidersByElapsedTime();
		LocalTime[] riderAETs = new LocalTime[riders.size()];
		for (int i = 0; i < riders.size(); i++) {
			Rider rider = riders.get(i);
			riderAETs[i] = stage.getRiderResult(rider).getAdjustedElapsedLocalTime();
		}
		return riderAETs;
	}

	@Override
	public int[] getRidersPointsInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = getStageById(stageId);
		List<Rider> riders = stage.getRidersByElapsedTime();
		int[] riderSprinterPoints = new int[riders.size()];
		for (int i = 0; i < riders.size(); i++) {
			Rider rider = riders.get(i);
			riderSprinterPoints[i] = stage.getRiderResult(rider).getSprintersPoints();
		}
		return riderSprinterPoints;
	}

	@Override
	public int[] getRidersMountainPointsInStage(int stageId) throws IDNotRecognisedException {
		Stage stage = getStageById(stageId);
		List<Rider> riders = stage.getRidersByElapsedTime();
		int[] riderMountainPoints = new int[riders.size()];
		for (int i = 0; i < riders.size(); i++) {
			Rider rider = riders.get(i);
			riderMountainPoints[i] = stage.getRiderResult(rider).getMountainPoints();
		}
		return riderMountainPoints;
	}

	@Override
	public void eraseCyclingPortal() {
		teams = new ArrayList<>();
		riders = new ArrayList<>();
		races = new ArrayList<>();
		stages = new ArrayList<>();
		segments = new ArrayList<>();
		Rider.resetIdCounter();
		Team.resetIdCounter();
		Race.resetIdCounter();
		Stage.resetIdCounter();
		Segment.resetIdCounter();
	}

	@Override
	public void saveCyclingPortal(String filename) throws IOException {
		FileOutputStream file = new FileOutputStream(filename);
		ObjectOutputStream output = new ObjectOutputStream(file);
		SavedCyclingPortal savedCyclingPortal = new SavedCyclingPortal(teams, riders, races, stages, segments, Team.getIdCounter(), Rider.getIdCounter(), Race.getIdCounter(), Stage.getIdCounter(), Segment.getIdCounter());
		output.writeObject(savedCyclingPortal);
		output.close();
		file.close();
	}

	@Override
	public void loadCyclingPortal(String filename) throws IOException, ClassNotFoundException {
		eraseCyclingPortal();
		FileInputStream file = new FileInputStream(filename);
		ObjectInputStream input = new ObjectInputStream(file);

		SavedCyclingPortal savedCyclingPortal = (SavedCyclingPortal)input.readObject();
		teams = savedCyclingPortal.teams;
		riders = savedCyclingPortal.riders;
		races = savedCyclingPortal.races;
		stages = savedCyclingPortal.stages;
		segments = savedCyclingPortal.segments;

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
		for (int i = 0; i < riders.size(); i++) {
			riderIds[i] = riders.get(i).getId();
		}
		return riderIds;
	}

	@Override
	public LocalTime[] getGeneralClassificationTimesInRace(int raceId) throws IDNotRecognisedException {
		Race race = getRaceById(raceId);
		List<Rider> riders = race.getRidersByAdjustedElapsedTime();
		LocalTime[] riderTimes = new LocalTime[riders.size()];
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
		for (int i = 0; i < riders.size(); i++) {
			riderIds[i] = riders.get(i).getId();
		}
		return riderIds;
	}

	@Override
	public int[] getRidersMountainPointClassificationRank(int raceId) throws IDNotRecognisedException {
		Race race = getRaceById(raceId);
		List<Rider> riders = race.getRidersByMountainPoints();
		int[] riderIds = new int[riders.size()];
		for (int i = 0; i < riders.size(); i++) {
			riderIds[i] = riders.get(i).getId();
		}
		return riderIds;
	}
}
