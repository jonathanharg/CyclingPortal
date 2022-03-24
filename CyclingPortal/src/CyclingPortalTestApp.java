import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;

import cycling.CyclingPortal;
import cycling.IllegalNameException;
import cycling.InvalidLengthException;
import cycling.InvalidLocationException;
import cycling.InvalidNameException;
import cycling.InvalidStageStateException;
import cycling.InvalidStageTypeException;
import cycling.SegmentType;
import cycling.StageType;
import cycling.IDNotRecognisedException;

import java.beans.Transient;
import java.lang.IllegalArgumentException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
// !! DO NOT SUBMIT: Doesn't start with java.

class CyclingPortalTestApp {
	CyclingPortal portal;

	@BeforeEach
	public void initEach() {
		portal = new CyclingPortal();
	}

	@Nested
	class UpdateTests {
		@ParameterizedTest
		@ValueSource(strings = {"contains whitespace","EscapChars\t\n\r\f" })
		public void stagesNowNoWhiteSpace(String invalidName) {
			assertThrows(InvalidNameException.class, () -> {
				int race = portal.createRace("race1", "racer race");
				portal.addStageToRace(race, invalidName, "desc", 2.0, LocalDateTime.now(), StageType.FLAT);
			});
		}
		
		@Test
		public void getRaceStagesIsEmptyArray() {
			try {
				int race = portal.createRace("race1", "toboggan race");
				int[] stages = portal.getRaceStages(race);
				assertEquals(stages.length,0);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		@ParameterizedTest
		@ValueSource(strings = {"contains whitespace","EscapChars\t\n\r\f" })
		public void teamsNowNoWhiteSpace(String invalidName) {
			assertThrows(InvalidNameException.class, () -> {
				portal.createTeam(invalidName, "why diogo");
			});
		}
		
		@Test
		public void getRiderAETreturnNull() {
			int race, stage = -1, team, rider = -1;
			try {
				race = portal.createRace("race1", "toboggan race");
				stage = portal.addStageToRace(race, "stage", "stage desc", 7.0, LocalDateTime.now(), StageType.FLAT);
				team = portal.createTeam("teame", "teamteam");
				rider = portal.createRider(team, "john smith", 1999);
			} catch (Exception e) {
				e.printStackTrace();
			}
			try {
				assertNull(portal.getRiderAdjustedElapsedTimeInStage(stage, rider));
			} catch (IDNotRecognisedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	@Nested
	class TeamTests {

		@Test
		public void returnsID() {
			// TODO: assert returned int = team.getID
			try {
				int team = portal.createTeam("TeamA", "Description...");
			} catch (IllegalNameException | InvalidNameException e) {
				e.printStackTrace();
			}
		}

		@ParameterizedTest
		@ValueSource(strings = { "Steve", "aLongButLegalNameAAAAAAAAAAAAA" })
		public void legalNames(String legalName) {
			try {
				portal.createTeam(legalName, "A Description");
			} catch (IllegalNameException | InvalidNameException e) {
				e.printStackTrace();
			}
		}

		@Test
		public void nullName() {
			assertThrows(InvalidNameException.class, () -> {
				portal.createTeam(null, null);
			});
		}

		@ParameterizedTest
		@ValueSource(strings = { "", "aVeryLongNameAAAAAAAAAAAAAAAAAA", "contains whitespace","EscapChars\t\n\r\f" })
		public void invalidNames(String invalidName) {
			assertThrows(InvalidNameException.class, () -> {
				portal.createTeam(invalidName, null);
			});
		}

		@Test
		public void existingName() {
			assertThrows(IllegalNameException.class, () -> {
				portal.createTeam("TeamA", "Description");
				portal.createTeam("TeamA", "Repeated Team Name");
			});
		}
	}

	// TODO: Verify TeamID test
	@Nested
	class RiderTests {
		@Test
		public void returnsID() {
			try {
				int id = portal.createTeam("Team0", "Test team");
				int rider = portal.createRider(id, "John Snow", 2000);
			} catch (IllegalNameException | InvalidNameException e1) {
				e1.printStackTrace();
			} catch (IDNotRecognisedException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}

		@Test
		public void nullName() {
			assertThrows(IllegalArgumentException.class, () -> {
				int id = portal.createTeam("Team0", "Test team");
				portal.createRider(id, null, 2000);
			});
		}

		@ParameterizedTest
		@ValueSource(ints = { 2000, 1900, 1950 })
		public void validYOB(int validYOBs) {
			try {
				int id = portal.createTeam("Team0", "Test team");
				portal.createRider(id, "Maddie", validYOBs);
			} catch (IDNotRecognisedException | IllegalArgumentException e) {
				e.printStackTrace();
			} catch (IllegalNameException | InvalidNameException e) {
				e.printStackTrace();
			}
		}

		@ParameterizedTest
		@ValueSource(ints = { 0, 1899, -200 })
		public void invalidYOB(int invalidYOBs) {
			assertThrows(IllegalArgumentException.class, () -> {
				int id = portal.createTeam("Team0", "Test team");
				portal.createRider(id, "Jonathan", invalidYOBs);
			});
		}
	}

	@Nested
	class CyclingPortalTests {
		@Test
		public void testGetTeams() {
			assertEquals(portal.getTeams().length, 0);
			try {
				portal.createTeam("Steve", "Just your average guy.");
				assertEquals(portal.getTeams().length, 1);
				portal.createTeam("Bobo", "Hey, I'm using Whatsapp!");
				portal.createTeam("Philip", "Single");
				assertEquals(portal.getTeams().length, 3);
				// TODO: !!! test with removing team too
			} catch (IllegalNameException | InvalidNameException e) {
				e.printStackTrace();
			}
		}

		@Test
		public void testGetTeamRiders() {
			try {
				int teamId = portal.createTeam("TestTeam", "Team");
				int r1 = portal.createRider(teamId, "rider 1", 1999);
				int r2 = portal.createRider(teamId, "rider 1", 1999);
				int r3 = portal.createRider(teamId, "rider 1", 1999);
				int[] riderIds = { r1, r2, r3 };
				assertArrayEquals(portal.getTeamRiders(teamId), riderIds);
			} catch (Exception e) {

			}
		}
	}

	@Nested
	class RaceTests {
		@Test
		public void testReturnsId() {
			try {
				int id = portal.createRace("Race0", "Test race");
			} catch (IllegalNameException | InvalidNameException e1) {
				e1.printStackTrace();
			}
		}

		@Test
		public void existingName() {
			assertThrows(IllegalNameException.class, () -> {
				portal.createRace("RaceA", "Description");
				System.out.println(portal.getRaceIds().length);
				portal.createRace("RaceA", "Repeated Race Descripton");
				System.out.println(portal.getRaceIds().length);
			});
		}

		@ParameterizedTest
		@ValueSource(strings = { "Steve", "aLongButLegalNameAAAAAAAAAAAAA" })
		public void legalNames(String legalName) {
			try {
				portal.createRace(legalName, "A Description");
			} catch (IllegalNameException | InvalidNameException e) {
				e.printStackTrace();
			}
		}

		@Test
		public void nullName() {
			assertThrows(InvalidNameException.class, () -> {
				portal.createRace(null, null);
			});
		}

		@ParameterizedTest
		@ValueSource(strings = { "", "aVeryLongNameAAAAAAAAAAAAAAAAAA", "Space Name", "EscapChars\t\n\r\f"})
		public void invalidNames(String invalidName) {
			assertThrows(InvalidNameException.class, () -> {
				portal.createRace(invalidName, null);
			});
		}

	}
	
	@Nested
	class ResultsTests {
		@Test
		public void stagesAreOrdered() {
			try {
				int raceId = portal.createRace("RacerRacer", "racerRacingRace");
				int stage2 = portal.addStageToRace(raceId, "stage2", "tt", 50.0, LocalDateTime.now().plusHours(1), StageType.TT);
				int stage3 = portal.addStageToRace(raceId, "stage3", "hm", 50.0, LocalDateTime.now().plusHours(2), StageType.HIGH_MOUNTAIN);
				int stage4 = portal.addStageToRace(raceId, "stage4", "mm", 50.0, LocalDateTime.now().plusHours(3), StageType.MEDIUM_MOUNTAIN);
				int stage1 = portal.addStageToRace(raceId, "stage1", "ft", 50.0, LocalDateTime.now(), StageType.FLAT);

				int segment6 = portal.addIntermediateSprintToStage(stage1, 22.0);
				int segment4 = portal.addCategorizedClimbToStage(stage1, 17.0, SegmentType.C4, 0.2, 1.0);
				int segment3 = portal.addCategorizedClimbToStage(stage1, 12.0, SegmentType.C3, 0.1, 3.0);
				int segment1 = portal.addCategorizedClimbToStage(stage1, 4.0, SegmentType.SPRINT, 12.0, 6.0);
				int segment2 = portal.addCategorizedClimbToStage(stage1, 9.0, SegmentType.C2, 8.3, 2.0);
				int segment5 = portal.addCategorizedClimbToStage(stage1, 19.0, SegmentType.HC, 6.9, 1.0);
				
				int[] result = portal.getRaceStages(raceId);
				int[] ans = new int[] {stage1, stage2, stage3, stage4};

				assertArrayEquals(result, ans);

			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Test
		public void segmentsAreOrdered() {
			try {
				int raceId = portal.createRace("RacerRacer", "racerRacingRace");
				int stage2 = portal.addStageToRace(raceId, "stage2", "tt", 50.0, LocalDateTime.now().plusHours(1), StageType.TT);
				int stage3 = portal.addStageToRace(raceId, "stage3", "hm", 50.0, LocalDateTime.now().plusHours(2), StageType.HIGH_MOUNTAIN);
				int stage4 = portal.addStageToRace(raceId, "stage4", "mm", 50.0, LocalDateTime.now().plusHours(3), StageType.MEDIUM_MOUNTAIN);
				int stage1 = portal.addStageToRace(raceId, "stage1", "ft", 50.0, LocalDateTime.now(), StageType.FLAT);

				int segment6 = portal.addIntermediateSprintToStage(stage1, 22.0);
				int segment4 = portal.addCategorizedClimbToStage(stage1, 17.0, SegmentType.C4, 0.2, 1.0);
				int segment3 = portal.addCategorizedClimbToStage(stage1, 12.0, SegmentType.C3, 0.1, 3.0);
				int segment1 = portal.addCategorizedClimbToStage(stage1, 4.0, SegmentType.SPRINT, 12.0, 6.0);
				int segment2 = portal.addCategorizedClimbToStage(stage1, 9.0, SegmentType.C2, 8.3, 2.0);
				int segment5 = portal.addCategorizedClimbToStage(stage1, 19.0, SegmentType.HC, 6.9, 1.0);

				int[] result = portal.getStageSegments(stage1);
				int[] ans = new int[] {segment1, segment2, segment3, segment4, segment5, segment6};
				
				assertArrayEquals(result, ans);
				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		
		
		@Test
		public void basicResultsTest() {
			try {
				int teamId = portal.createTeam("BlueTeam", null);
				int rider1Id = portal.createRider(teamId, "Andrew", 1999);
				int rider2Id = portal.createRider(teamId, "Bart", 1999);
				int rider3Id = portal.createRider(teamId, "Charlie", 1999);
				int rider4Id = portal.createRider(teamId, "Doug", 1999);
				int rider5Id = portal.createRider(teamId, "Earnie", 1999);

				int raceId = portal.createRace("BasicRace", null);
				int stageId = portal.addStageToRace(raceId, "BasicStage", null, 10, LocalDateTime.now(),
						StageType.FLAT);
				int mountId = portal.addCategorizedClimbToStage(stageId, 7.0, SegmentType.HC, 5.2, 2.0);
				int sprintId = portal.addIntermediateSprintToStage(stageId, 2.0);
				portal.concludeStagePreparation(stageId);
				portal.registerRiderResultsInStage(stageId, rider1Id, LocalTime.of(0, 10, 00, 00), LocalTime.of(0, 10, 11, 00),
						LocalTime.of(0, 10, 17, 00), LocalTime.of(0, 10, 54, 00));
				portal.registerRiderResultsInStage(stageId, rider2Id, LocalTime.of(0, 10, 00, 00), LocalTime.of(0, 10, 13, 00),
						LocalTime.of(0, 10, 18, 00), LocalTime.of(0, 10, 52, 00));
				portal.registerRiderResultsInStage(stageId, rider3Id, LocalTime.of(0, 10, 00, 00), LocalTime.of(0, 10, 14, 00),
						LocalTime.of(0, 10, 20, 00), LocalTime.of(0, 10, 51, 00));
				portal.registerRiderResultsInStage(stageId, rider4Id, LocalTime.of(0, 10, 00, 00), LocalTime.of(0, 10, 10, 00),
						LocalTime.of(0, 10, 15, 00), LocalTime.of(0, 10, 55, 00));
				portal.registerRiderResultsInStage(stageId, rider5Id, LocalTime.of(0, 10, 00, 00), LocalTime.of(0, 10, 17, 00),
						LocalTime.of(0, 10, 21, 00), LocalTime.of(0, 10, 50, 00));

				
				
//				portal.testResults(stageId);
				
				assertEquals(portal.getRiderAdjustedElapsedTimeInStage(stageId, rider2Id), LocalTime.of(0, 10,50,0));
				assertArrayEquals(portal.getRidersRankInStage(stageId), new int[] {rider5Id,rider3Id,rider2Id,rider1Id,rider4Id});
				assertArrayEquals(portal.getRankedAdjustedElapsedTimesInStage(stageId), new LocalTime[] { LocalTime.of(0, 10, 50, 00), LocalTime.of(0, 10, 50, 00), LocalTime.of(0, 10, 50, 00), LocalTime.of(0, 10, 54, 00), LocalTime.of(0, 10, 54, 00)});
				
				assertArrayEquals(portal.getRidersMountainPointsInStage(stageId), new int[] {8,10,12,15,20});
				assertArrayEquals(portal.getRidersPointsInStage(stageId), new int[] {61,43,35,35,36});
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Nested
	class MTests{
		
		int teamId, rider1Id, race1Id, race2Id, stage1Id, stage2Id, intspr1, intspr2, race3Id, stage3Id, seg1, seg2, seg3;
		
		@BeforeEach
		void b4() {
			
			try {
				teamId = portal.createTeam("BlueTeam", null);
				race1Id = portal.createRace("BasicRace", null);
				race2Id = portal.createRace("Penis", "poop");
				stage1Id = portal.addStageToRace(race1Id, "Penguin", null, 10, LocalDateTime.now(),
						StageType.FLAT);
				stage2Id = portal.addStageToRace(race1Id, "Giraffe", null, 10, LocalDateTime.now(),
						StageType.FLAT);
				intspr1 = portal.addIntermediateSprintToStage(stage2Id, 7.0);
				intspr2 = portal.addIntermediateSprintToStage(stage2Id,7.2);

				
				
			} catch (Exception e ) {
				
			}
	
		}
	
		
		//viewRaceDetails 
		
		@Test
		public void viewRaceDetailsID() {
			assertThrows(IDNotRecognisedException.class, () -> {
				portal.viewRaceDetails(33333);
			});
		}
		
		
		//removeRaceById
		
		@Test
		public void removeRaceByIdworks() {
			int raceRemovez = 333;
			try {
				raceRemovez = portal.createRace("iamgonnadie", "ded");
			}catch (Exception e) {}
			assertEquals(portal.getRaceIds().length,3);
			try {
				portal.removeRaceById(raceRemovez);
			}catch (Exception e) {}
			assertEquals(portal.getRaceIds().length,2);
		}
		
		@Test
		public void removeRaceByIdthrowId() {
			assertThrows(IDNotRecognisedException.class, () -> {
				portal.removeRaceById(33333);
			});
		}
		
		//getNumberOfStages
		
		@Test
		public void getNumberOfStagesThrowId() {
			assertThrows(IDNotRecognisedException.class, () -> {
				portal.getNumberOfStages(33333);
			});
		}
		
		@Test
		public void getNumberOfStagesReturns1() {
			try {
				assertEquals(portal.getNumberOfStages(race1Id),2);
			}catch (Exception e){}
		}
		
		@Test
		public void getNumberOfStagesReturns2() {
			try {
				assertEquals(portal.getNumberOfStages(race2Id),0);
			}catch (Exception e){}
		}
		
		//addStageToRace
		
		@Test
		public void addStageToRaceThrowsID() {
			assertThrows(IDNotRecognisedException.class, () -> {
				int id = portal.addStageToRace(1000, "Yes", null, 10, LocalDateTime.now(),
						StageType.FLAT);
			});
		}
		
		@Test
		public void addStageToRaceThrowsSameName1() {
			assertThrows(IllegalNameException.class, () -> {
				int id = portal.addStageToRace(race1Id, "Penguin", null, 10, LocalDateTime.now(),
						StageType.FLAT);
			});
		}
		
		@Test
		public void addStageToRaceThrowsSameName2() {
			assertThrows(IllegalNameException.class, () -> {
				int id = portal.addStageToRace(race2Id, "Penguin", null, 10, LocalDateTime.now(),
						StageType.FLAT);
			});
		}

		@Test
		public void addStageToRaceThrowsNullName() {
			assertThrows(InvalidNameException.class, () -> {
				int id = portal.addStageToRace(race1Id, null, null, 10, LocalDateTime.now(),
						StageType.FLAT);
			});
		}

		@Test
		public void addStageToRaceThrowsEmptyName() {
			assertThrows(InvalidNameException.class, () -> {
				int id = portal.addStageToRace(race1Id, "", null, 10, LocalDateTime.now(),
						StageType.FLAT);
			});
		}

		@Test
		public void addStageToRaceThrowsLongName() {
			assertThrows(InvalidNameException.class, () -> {
				int id = portal.addStageToRace(race1Id, "this is a str that has many many "
						+ "many characters, hopefully more than 30", null, 10, LocalDateTime.now(),
						StageType.FLAT);
			});
		}
		
		@Test
		public void addStageToRaceThrowsShortLength() {
			assertThrows(InvalidLengthException.class, () -> {
				int id = portal.addStageToRace(race1Id, "Penis", null, 4.9, LocalDateTime.now(),
						StageType.FLAT);
			});
		}
		
		
		//getRaceStages
		
		@Test
		public void getRaceStagesThrowsTd() {
			assertThrows(IDNotRecognisedException.class, () -> {
				int[] stageids = portal.getRaceStages(30000);
			});	
		}

		@Test
		public void getRaceStagesReturns1() {
			try {
				int[] stageids = portal.getRaceStages(race1Id);
				assertEquals(stageids.length, portal.getNumberOfStages(race1Id));
			}catch(Exception e) {}
		}
		
		@Test
		public void getRaceStagesReturns2() {
			try {
				int[] stageids = portal.getRaceStages(race1Id);
				assertEquals(stageids[0],stage1Id);
			}catch(Exception e) {}
		}
		
		@Test
		public void getRaceStagesReturns3() {
			try {
				int[] stageids = portal.getRaceStages(race1Id);
				assertEquals(stageids[1],stage2Id);
			}catch(Exception e) {}
		}
		
		
		//getStageLength
		
		@Test
		public void getStageLengthThrowsId() {
			assertThrows(IDNotRecognisedException.class, () -> {
				double leng = portal.getStageLength(30000);
			});	
		}

		@Test
		public void getStageLengthReturns() {
			try {
				assertEquals(portal.getStageLength(stage1Id), 10);
			}catch(Exception e) {}
		}

		
		//removeStageById
		
		@Test
		public void removeStageByIdThrowsId() {
			assertThrows(IDNotRecognisedException.class, () -> {
				portal.removeStageById(33333);
			});
		}

		@Test
		public void removeStageByIdWorks() {
			try {
				int stageDel = portal.addStageToRace(race1Id, "Delete me", null, 10, LocalDateTime.now(),
						StageType.FLAT);
				assertEquals(portal.getRaceStages(race1Id).length, 3);
				portal.removeStageById(stageDel);
				assertEquals(portal.getRaceStages(race1Id).length, 2);
			} catch (Exception e) {}
		}

		
		//addCategorizedClimbToStage
		
		@Test
		public void addCategorizedClimbToStageThrowsId() {
			assertThrows(IDNotRecognisedException.class, () -> {
				portal.addCategorizedClimbToStage(3000, 7.0, SegmentType.HC, 5.2, 2.0);
			});
		}

		@Test
		public void addCategorizedClimbToStageThrowsLongLocation() {
			//??
			assertThrows(InvalidLocationException.class, () -> {
				portal.addCategorizedClimbToStage(stage1Id, 100.0, SegmentType.HC, 5.2, 2.0);
			});
		}

		@Test
		public void addCategorizedClimbToStageThrowsConcludedPrep() {
			assertThrows(InvalidStageStateException.class, () -> {
				portal.concludeStagePreparation(stage2Id);
				portal.addCategorizedClimbToStage(stage2Id, 7.0, SegmentType.HC, 5.2, 2.0);
			});
		}
		
		@Test
		public void addCategorizedClimbToStageThrowsTT() {
			assertThrows(InvalidStageTypeException.class, () -> {
				int stage3Id = portal.addStageToRace(race1Id, "TimeTrial", null, 10, LocalDateTime.now(),
						StageType.TT);
				portal.addCategorizedClimbToStage(stage3Id, 7.0, SegmentType.HC, 5.2, 2.0);
			});
		}
		
		
		//addIntermediateSprintToStage
		
		@Test
		public void addIntermediateSprintToStageThrowsId() {
			assertThrows(IDNotRecognisedException.class, () -> {
				portal.addIntermediateSprintToStage(3000, 7.0);
			});
		}

		@Test
		public void addIntermediateSprintToStageThrowsLongLocation() {
			assertThrows(InvalidLocationException.class, () -> {
				portal.addIntermediateSprintToStage(stage1Id, 100.0);
			});
		}

		@Test
		public void addIntermediateSprintToStageThrowsConcPrep() {
			assertThrows(InvalidStageStateException.class, () -> {
				portal.concludeStagePreparation(stage2Id);
				portal.addIntermediateSprintToStage(stage2Id, 7.0);
			});
		}

		@Test
		public void addIntermediateSprintToStageThrowsTT() {
			assertThrows(InvalidStageTypeException.class, () -> {
				int stage3Id = portal.addStageToRace(race1Id, "TimeTrial", null, 10, LocalDateTime.now(),
						StageType.TT);
				portal.addIntermediateSprintToStage(stage3Id, 7.0);
			});
		}

		
		//removeSegment
		
		@Test
		public void removeSegmentThrowsId() {
			assertThrows(IDNotRecognisedException.class, () -> {
				portal.removeSegment(3000);
			});
		}

		@Test
		public void removeSegmentThrowsConcPrep() {
			assertThrows(InvalidStageStateException.class, () -> {
				portal.concludeStagePreparation(stage2Id);
				portal.removeSegment(intspr1);
			});
		}
		
		//concludeStagePreparation
		
		@Test
		public void concludeStagePreparationThrowsId() {
			assertThrows(IDNotRecognisedException.class, () -> {
				portal.concludeStagePreparation(3000);
			});
		}

		@Test
		public void concludeStagePreparationThrowsConcPrep() {
			assertThrows(InvalidStageStateException.class, () -> {
				portal.concludeStagePreparation(stage2Id);
				portal.concludeStagePreparation(stage2Id);
			});
		}

		//getStageSegments
		
		@Test
		public void getStageSegmentsThrowsId() {
			assertThrows(IDNotRecognisedException.class, () -> {
				portal.getStageSegments(3000);
			});
		}
		
		@Test
		public void getStageSegmentsOrdered() {
			try {
				race3Id = portal.createRace("ARace", null);
				stage3Id = portal.addStageToRace(race3Id,"Stage", null, 10, LocalDateTime.now(),
						StageType.FLAT);
				seg1 = portal.addIntermediateSprintToStage(stage3Id, 5.0);
				seg2 = portal.addIntermediateSprintToStage(stage3Id, 9.0);
				seg3 = portal.addIntermediateSprintToStage(stage3Id, 7.0);
				

				int[] segments = portal.getStageSegments(stage3Id);
				int[] ans = new int[] {seg1, seg3, seg2};
				System.out.println(Arrays.toString(ans));
				System.out.println(Arrays.toString(segments));
				assertArrayEquals(segments, ans);
//				assertEquals(segments[0], seg2);
//				assertEquals(segments[1], seg3);
//				assertEquals(segments[2], seg1);

			}catch (Exception e) {}
		}
		
		
		// createTeam
		
		@Test
		public void createTeamThrowsSameName() {
			assertThrows(IllegalNameException.class, () -> {
				portal.createTeam("BlueTeam", "Hello");
			});
		}
		
		@Test
		public void createTeamThrowsInvalidName() {
			assertThrows(InvalidNameException.class, () -> {
				portal.createTeam(null, "Hello");
			});
			assertThrows(InvalidNameException.class, () -> {
				portal.createTeam("", "Hello");
			});
			assertThrows(InvalidNameException.class, () -> {
				portal.createTeam("Hi i have more than 30 characters hopefully once i have wirrtten enoof wordz", "Hello");
			});
		}
		
		@Test
		public void createTeamReturnsId() {
			
		}
		

	}
	
	
}
