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
import cycling.InvalidNameException;
import cycling.SegmentType;
import cycling.StageType;
import cycling.IDNotRecognisedException;
import java.lang.IllegalArgumentException;
import java.time.LocalDateTime;
import java.time.LocalTime;
// !! DO NOT SUBMIT: Doesn't start with java.

class CyclingPortalTestApp {
	CyclingPortal portal;

	@BeforeEach
	public void initEach() {
		portal = new CyclingPortal();
	}

	@Nested
	class TeamTests {

		@Test
		public void returnsID() {
			// TODO: assert returned int = team.getID
			try {
				int team = portal.createTeam("Team A", "Description...");
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
		@ValueSource(strings = { "", "aVeryLongNameAAAAAAAAAAAAAAAAAA" })
		public void invalidNames(String invalidName) {
			assertThrows(InvalidNameException.class, () -> {
				portal.createTeam(invalidName, null);
			});
		}

		@Test
		public void existingName() {
			assertThrows(IllegalNameException.class, () -> {
				portal.createTeam("Team A", "Description");
				portal.createTeam("Team A", "Repeated Team Name");
			});
		}
	}

	// TODO: Verify TeamID test
	@Nested
	class RiderTests {
		@Test
		public void returnsID() {
			try {
				int id = portal.createTeam("Team 0", "Test team");
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
				int id = portal.createTeam("Team 0", "Test team");
				portal.createRider(id, null, 2000);
			});
		}

		@ParameterizedTest
		@ValueSource(ints = { 2000, 1900, 1950 })
		public void validYOB(int validYOBs) {
			try {
				int id = portal.createTeam("Team 0", "Test team");
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
				int id = portal.createTeam("Team 0", "Test team");
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
				int teamId = portal.createTeam("Test Team", "Team");
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
				int id = portal.createRace("Race 0", "Test race");
			} catch (IllegalNameException | InvalidNameException e1) {
				e1.printStackTrace();
			}
		}

		@Test
		public void existingName() {
			assertThrows(IllegalNameException.class, () -> {
				portal.createRace("Race A", "Description");
				portal.createRace("Race A", "Repeated Race Descripton");
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
		public void basicResultsTest() {
			try {
				int teamId = portal.createTeam("Blue Team", null);
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
				portal.registerRiderResultsInStage(stageId, rider5Id, LocalTime.of(0, 10, 00, 00), LocalTime.of(0, 10, 17, 00),
						LocalTime.of(0, 10, 21, 00), LocalTime.of(0, 10, 50, 00));
				portal.registerRiderResultsInStage(stageId, rider3Id, LocalTime.of(0, 10, 00, 00), LocalTime.of(0, 10, 14, 00),
						LocalTime.of(0, 10, 20, 00), LocalTime.of(0, 10, 51, 00));
				portal.registerRiderResultsInStage(stageId, rider2Id, LocalTime.of(0, 10, 00, 00), LocalTime.of(0, 10, 13, 00),
						LocalTime.of(0, 10, 18, 00), LocalTime.of(0, 10, 52, 00));
				portal.registerRiderResultsInStage(stageId, rider1Id, LocalTime.of(0, 10, 00, 00), LocalTime.of(0, 10, 11, 00),
						LocalTime.of(0, 10, 17, 00), LocalTime.of(0, 10, 54, 00));
				portal.registerRiderResultsInStage(stageId, rider4Id, LocalTime.of(0, 10, 00, 00), LocalTime.of(0, 10, 10, 00),
						LocalTime.of(0, 10, 15, 00), LocalTime.of(0, 10, 55, 00));
				
				portal.testResults(stageId);
				
				assertEquals(portal.getRiderAdjustedElapsedTimeInStage(stageId, rider2Id), LocalTime.of(0, 10,50,0));
				assertArrayEquals(portal.getRidersRankInStage(stageId), new int[] {rider5Id,rider3Id,rider2Id,rider1Id,rider4Id});
				assertArrayEquals(portal.getRankedAdjustedElapsedTimesInStage(stageId), new LocalTime[] { LocalTime.of(0, 10, 50, 00), LocalTime.of(0, 10, 50, 00), LocalTime.of(0, 10, 50, 00), LocalTime.of(0, 10, 54, 00), LocalTime.of(0, 10, 54, 00)});
				assertArrayEquals(portal.getRidersPointsInStage(stageId), new int[] {
						11,
						13,
						15,
						17,
						20,
				});
				System.out.println("Stop");
				portal.eraseCyclingPortal();

				
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	@Nested
	class MTests{
		@Test
		public void basic() {
			try {
				int teamId = portal.createTeam("Blue Team", null);
				int rider1Id = portal.createRider(teamId, "Andrew", 1999);
				int rider2Id = portal.createRider(teamId, "Bart", 1999);
				int rider3Id = portal.createRider(teamId, "Charlie", 1999);
				int rider4Id = portal.createRider(teamId, "Doug", 1999);
				int rider5Id = portal.createRider(teamId, "Earnie", 1999);
	
				int race1Id = portal.createRace("BasicRace", null);
				int race2Id = portal.createRace("Penis", "poop");
				int stage1Id = portal.addStageToRace(race1Id, "Penguin", null, 10, LocalDateTime.now(),
						StageType.FLAT);
				int stage2Id = portal.addStageToRace(race1Id, "Giraffe", null, 10, LocalDateTime.now(),
						StageType.FLAT);
				
				
				//viewRaceDetails 
				System.out.println(portal.viewRaceDetails(race2Id));
				assertThrows(IDNotRecognisedException.class, () -> {
					portal.viewRaceDetails(33333);
				});
				
				
				//removeRaceById
				int raceRemovez = portal.createRace("i am gonna die", "ded");
				assertEquals(portal.getRaceIds().length,3);
				portal.removeRaceById(raceRemovez);
				assertEquals(portal.getRaceIds().length,2);
				assertThrows(IDNotRecognisedException.class, () -> {
					portal.removeRaceById(33333);
				});
				
				
				//getNumberOfStages
				assertThrows(IDNotRecognisedException.class, () -> {
					portal.getNumberOfStages(33333);
				});
				assertEquals(portal.getNumberOfStages(race1Id),2);
				assertEquals(portal.getNumberOfStages(race2Id),0);
				
				
				//addStageToRace
				assertThrows(IDNotRecognisedException.class, () -> {
					int id = portal.addStageToRace(1000, "Yes", null, 10, LocalDateTime.now(),
							StageType.FLAT);
				});
				assertThrows(IllegalNameException.class, () -> {
					int id = portal.addStageToRace(race1Id, "Penguin", null, 10, LocalDateTime.now(),
							StageType.FLAT);
				});
				assertThrows(IllegalNameException.class, () -> {
					int id = portal.addStageToRace(race2Id, "Penguin", null, 10, LocalDateTime.now(),
							StageType.FLAT);
				});
				assertThrows(InvalidNameException.class, () -> {
					int id = portal.addStageToRace(race1Id, null, null, 10, LocalDateTime.now(),
							StageType.FLAT);
				});
				assertThrows(InvalidNameException.class, () -> {
					int id = portal.addStageToRace(race1Id, "", null, 10, LocalDateTime.now(),
							StageType.FLAT);
				});
				assertThrows(InvalidNameException.class, () -> {
					int id = portal.addStageToRace(race1Id, "this is a str that has many many "
							+ "many characters, hopefully more than 30", null, 10, LocalDateTime.now(),
							StageType.FLAT);
				});
				assertThrows(InvalidLengthException.class, () -> {
					int id = portal.addStageToRace(race1Id, "Penis", null, 4.9, LocalDateTime.now(),
							StageType.FLAT);
				});
				assertThrows(InvalidLengthException.class, () -> {
					int id = portal.addStageToRace(race1Id, "Penis", null, -100, LocalDateTime.now(),
							StageType.FLAT);
				});
				

				//getRaceStages
				assertThrows(IDNotRecognisedException.class, () -> {
					int[] stageids = portal.getRaceStages(30000);
				});
				int[] stageids = portal.getRaceStages(race1Id);
				assertEquals(stageids.length, portal.getNumberOfStages(race1Id));
				assertEquals(stageids[0],stage1Id);
				assertEquals(stageids[1],stage2Id);
				
				
				//getStageLength
				assertThrows(IDNotRecognisedException.class, () -> {
					double leng = portal.getStageLength(30000);
				});
				assertEquals(portal.getStageLength(stage1Id), 10);
				
				
				//removeStageById
				assertThrows(IDNotRecognisedException.class, () -> {
					portal.removeStageById(33333);
				});
				int stageDel = portal.addStageToRace(race1Id, "Delete me", null, 10, LocalDateTime.now(),
						StageType.FLAT);
				assertEquals(portal.getRaceStages(race1Id).length, 3);
				portal.removeStageById(stageDel);
				assertEquals(portal.getRaceStages(race1Id).length, 2);
				
				
				//addCategorizedClimbToStage
				
				
				
				
				
				
			} catch (Exception e) {
				
				
				
				
			}
			
			
			
			
			
			
		}
	}
	
	
}
