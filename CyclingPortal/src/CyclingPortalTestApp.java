import static org.junit.jupiter.api.Assertions.*;

import cycling.CyclingPortal;
import cycling.DuplicatedResultException;
import cycling.IDNotRecognisedException;
import cycling.IllegalNameException;
import cycling.InvalidCheckpointsException;
import cycling.InvalidLengthException;
import cycling.InvalidLocationException;
import cycling.InvalidNameException;
import cycling.InvalidStageStateException;
import cycling.InvalidStageTypeException;
import cycling.NameNotRecognisedException;
import cycling.SegmentType;
import cycling.StageType;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Arrays;
// !! DO NOT SUBMIT: Doesn't start with java.
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.ValueSource;

class CyclingPortalTestApp {
  CyclingPortal portal;

  @BeforeEach
  public void initEach() {
    portal = new CyclingPortal();
  }

  @Nested
  class UpdateTests {
    @ParameterizedTest
    @ValueSource(strings = {"contains whitespace", "EscapChars\t\n\r\f"})
    public void stagesNowNoWhiteSpace(String invalidName) {
      assertThrows(
          InvalidNameException.class,
          () -> {
            int race = portal.createRace("race1", "racer race");
            portal.addStageToRace(
                race, invalidName, "desc", 2.0, LocalDateTime.now(), StageType.FLAT);
          });
    }

    @Test
    public void getRaceStagesIsEmptyArray() {
      try {
        int race = portal.createRace("race1", "toboggan race");
        int[] stages = portal.getRaceStages(race);
        assertEquals(stages.length, 0);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @ParameterizedTest
    @ValueSource(strings = {"contains whitespace", "EscapChars\t\n\r\f"})
    public void teamsNowNoWhiteSpace(String invalidName) {
      assertThrows(
          InvalidNameException.class,
          () -> {
            portal.createTeam(invalidName, "why diogo");
          });
    }

    @Test
    public void getRiderAETreturnNull() {
      int race, stage = -1, team, rider = -1;
      try {
        race = portal.createRace("race1", "toboggan race");
        stage =
            portal.addStageToRace(
                race, "stage", "stage desc", 7.0, LocalDateTime.now(), StageType.FLAT);
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
    @ValueSource(strings = {"Steve", "aLongButLegalNameAAAAAAAAAAAAA"})
    public void legalNames(String legalName) {
      try {
        portal.createTeam(legalName, "A Description");
      } catch (IllegalNameException | InvalidNameException e) {
        e.printStackTrace();
      }
    }

    @Test
    public void nullName() {
      assertThrows(
          InvalidNameException.class,
          () -> {
            portal.createTeam(null, null);
          });
    }

    @ParameterizedTest
    @ValueSource(
        strings = {
          "",
          "aVeryLongNameAAAAAAAAAAAAAAAAAA",
          "contains whitespace",
          "EscapChars\t\n\r\f"
        })
    public void invalidNames(String invalidName) {
      assertThrows(
          InvalidNameException.class,
          () -> {
            portal.createTeam(invalidName, null);
          });
    }

    @Test
    public void existingName() {
      assertThrows(
          IllegalNameException.class,
          () -> {
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
      assertThrows(
          IllegalArgumentException.class,
          () -> {
            int id = portal.createTeam("Team0", "Test team");
            portal.createRider(id, null, 2000);
          });
    }

    @ParameterizedTest
    @ValueSource(ints = {2000, 1900, 1950})
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
    @ValueSource(ints = {0, 1899, -200})
    public void invalidYOB(int invalidYOBs) {
      assertThrows(
          IllegalArgumentException.class,
          () -> {
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
        int[] riderIds = {r1, r2, r3};
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
      assertThrows(
          IllegalNameException.class,
          () -> {
            portal.createRace("RaceA", "Description");
            System.out.println(portal.getRaceIds().length);
            portal.createRace("RaceA", "Repeated Race Descripton");
            System.out.println(portal.getRaceIds().length);
          });
    }

    @ParameterizedTest
    @ValueSource(strings = {"Steve", "aLongButLegalNameAAAAAAAAAAAAA"})
    public void legalNames(String legalName) {
      try {
        portal.createRace(legalName, "A Description");
      } catch (IllegalNameException | InvalidNameException e) {
        e.printStackTrace();
      }
    }

    @Test
    public void nullName() {
      assertThrows(
          InvalidNameException.class,
          () -> {
            portal.createRace(null, null);
          });
    }

    @ParameterizedTest
    @ValueSource(
        strings = {"", "aVeryLongNameAAAAAAAAAAAAAAAAAA", "Space Name", "EscapChars\t\n\r\f"})
    public void invalidNames(String invalidName) {
      assertThrows(
          InvalidNameException.class,
          () -> {
            portal.createRace(invalidName, null);
          });
    }
  }

  @Nested
  class JTests {
    int raceId,
        stageA,
        stageB,
        stageC,
        stageD,
        stageE,
        segmentA1,
        segmentA2,
        segmentA3,
        segmentA4,
        segmentA5,
        segmentA6,
        segmentEmnt,
        segmentEsprint,
        teamId,
        rider1Id,
        rider2Id,
        rider3Id,
        rider4Id,
        rider5Id;

    @BeforeEach
    public void createRacesStagesSegments() {
      try {
        raceId = portal.createRace("RacerRacer", "racerRacingRace");
        teamId = portal.createTeam("BlueTeam", null);
        rider1Id = portal.createRider(teamId, "Andrew", 1999);
        rider2Id = portal.createRider(teamId, "Bart", 1999);
        rider3Id = portal.createRider(teamId, "Charlie", 1999);
        rider4Id = portal.createRider(teamId, "Doug", 1999);
        rider5Id = portal.createRider(teamId, "Earnie", 1999);
        stageB =
            portal.addStageToRace(
                raceId, "stage2", "tt", 50.0, LocalDateTime.now().plusHours(1), StageType.TT);
        stageC =
            portal.addStageToRace(
                raceId,
                "stage3",
                "hm",
                50.0,
                LocalDateTime.now().plusHours(2),
                StageType.HIGH_MOUNTAIN);
        stageD =
            portal.addStageToRace(
                raceId,
                "stage4",
                "mm",
                50.0,
                LocalDateTime.now().plusHours(3),
                StageType.MEDIUM_MOUNTAIN);
        stageA =
            portal.addStageToRace(
                raceId, "stage1", "ft", 50.0, LocalDateTime.now(), StageType.FLAT);

        stageE =
            portal.addStageToRace(
                raceId, "BasicStage", null, 10, LocalDateTime.now().plusHours(4), StageType.FLAT);

        segmentA6 = portal.addIntermediateSprintToStage(stageA, 22.0);

        segmentA4 = portal.addCategorizedClimbToStage(stageA, 17.0, SegmentType.C4, 0.2, 1.0);
        segmentA3 = portal.addCategorizedClimbToStage(stageA, 12.0, SegmentType.C3, 0.1, 3.0);
        segmentA1 = portal.addCategorizedClimbToStage(stageA, 4.0, SegmentType.SPRINT, 12.0, 6.0);
        segmentA2 = portal.addCategorizedClimbToStage(stageA, 9.0, SegmentType.C2, 8.3, 2.0);
        segmentA5 = portal.addCategorizedClimbToStage(stageA, 19.0, SegmentType.HC, 6.9, 1.0);
        segmentEmnt = portal.addCategorizedClimbToStage(stageE, 7.0, SegmentType.HC, 5.2, 2.0);
        segmentEsprint = portal.addIntermediateSprintToStage(stageE, 2.0);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Test
    public void stagesAreOrdered() {
      try {
        int[] result = portal.getRaceStages(raceId);
        int[] ans = new int[] {stageA, stageB, stageC, stageD, stageE};
        assertArrayEquals(result, ans);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Test
    public void segmentsAreOrdered() {
      try {
        int[] result = portal.getStageSegments(stageA);
        int[] ans = new int[] {segmentA1, segmentA2, segmentA3, segmentA4, segmentA5, segmentA6};

        assertArrayEquals(result, ans);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Test
    public void registerStageEresults() {
      try {
        portal.concludeStagePreparation(stageE);
        portal.registerRiderResultsInStage(
            stageE,
            rider1Id,
            LocalTime.of(0, 10, 00, 00),
            LocalTime.of(0, 10, 11, 00),
            LocalTime.of(0, 10, 17, 00),
            LocalTime.of(0, 10, 54, 00));
        portal.registerRiderResultsInStage(
            stageE,
            rider2Id,
            LocalTime.of(0, 10, 00, 00),
            LocalTime.of(0, 10, 13, 00),
            LocalTime.of(0, 10, 18, 00),
            LocalTime.of(0, 10, 52, 00));
        portal.registerRiderResultsInStage(
            stageE,
            rider3Id,
            LocalTime.of(0, 10, 00, 00),
            LocalTime.of(0, 10, 14, 00),
            LocalTime.of(0, 10, 20, 00),
            LocalTime.of(0, 10, 51, 00));
        portal.registerRiderResultsInStage(
            stageE,
            rider4Id,
            LocalTime.of(0, 10, 00, 00),
            LocalTime.of(0, 10, 10, 00),
            LocalTime.of(0, 10, 15, 00),
            LocalTime.of(0, 10, 55, 00));
        portal.registerRiderResultsInStage(
            stageE,
            rider5Id,
            LocalTime.of(0, 10, 00, 00),
            LocalTime.of(0, 10, 17, 00),
            LocalTime.of(0, 10, 21, 00),
            LocalTime.of(0, 10, 50, 00));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Test
    public void getAETinStage() {
      registerStageEresults();
      try {
        assertEquals(
            portal.getRiderAdjustedElapsedTimeInStage(stageE, rider2Id),
            LocalTime.of(0, 10, 50, 0));
      } catch (IDNotRecognisedException e) {
        e.printStackTrace();
      }
    }

    @Test
    public void getRankInStageE() {
      registerStageEresults();
      try {
        assertArrayEquals(
            portal.getRidersRankInStage(stageE),
            new int[] {rider5Id, rider3Id, rider2Id, rider1Id, rider4Id});
      } catch (IDNotRecognisedException e) {
        e.printStackTrace();
      }
    }

    @Test
    public void getRankedAETInStageE() {
      registerStageEresults();
      try {
        assertArrayEquals(
            portal.getRankedAdjustedElapsedTimesInStage(stageE),
            new LocalTime[] {
              LocalTime.of(0, 10, 50, 00),
              LocalTime.of(0, 10, 50, 00),
              LocalTime.of(0, 10, 50, 00),
              LocalTime.of(0, 10, 54, 00),
              LocalTime.of(0, 10, 54, 00)
            });
      } catch (IDNotRecognisedException e) {
        e.printStackTrace();
      }
    }

    @Test
    public void getMountainPointsInStageE() {
      registerStageEresults();
      try {
        assertArrayEquals(
            portal.getRidersMountainPointsInStage(stageE), new int[] {8, 10, 12, 15, 20});
      } catch (IDNotRecognisedException e) {
        e.printStackTrace();
      }
    }

    @Test
    public void getSprintersPointsInStageE() {
      registerStageEresults();
      try {
        assertArrayEquals(portal.getRidersPointsInStage(stageE), new int[] {61, 43, 35, 35, 36});
      } catch (IDNotRecognisedException e) {
        e.printStackTrace();
      }
    }

    @Test
    public void registerRiderDuplicateThrows() {
      registerStageEresults();
      assertThrows(
          DuplicatedResultException.class,
          () -> {
            portal.registerRiderResultsInStage(
                stageE,
                rider1Id,
                LocalTime.of(0, 10, 00, 00),
                LocalTime.of(0, 10, 14, 00),
                LocalTime.of(0, 10, 20, 00),
                LocalTime.of(0, 10, 51, 00));
          });
    }

    @Test
    public void erasePortal() {
      registerStageEresults();
      portal.eraseCyclingPortal();
      assertEquals(0, portal.getTeams().length);
      assertEquals(0, portal.getRaceIds().length);
      try {
        assertEquals(0, portal.createRace("race", "description"));
        assertEquals(0, portal.createTeam("name", "description"));
        assertEquals(0, portal.createRider(0, "name", 1999));
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Test
    public void savePortal() {
      registerStageEresults();
      try {
        portal.saveCyclingPortal("testSavePortal");
      } catch (IOException e) {
        e.printStackTrace();
      }
    }

    @Test
    public void saveLoadPortal() {
      registerStageEresults();
      int teamslen = portal.getTeams().length;
      int racelen = portal.getRaceIds().length;
      try {
        portal.saveCyclingPortal("testSaveLoadPortal");
      } catch (IOException e) {
        e.printStackTrace();
      }
      portal.eraseCyclingPortal();
      try {
        portal.loadCyclingPortal("testSaveLoadPortal");
      } catch (ClassNotFoundException | IOException e) {
        e.printStackTrace();
      }
      assertEquals(teamslen, portal.getTeams().length);
      assertEquals(racelen, portal.getRaceIds().length);
    }

    @Test
    public void viewRaceDetails() {
      try {
        String details = portal.viewRaceDetails(raceId);
        assertTrue(details.contains(String.valueOf(raceId)));
        assertTrue(details.contains("RacerRacer"));
        assertTrue(details.contains("racerRacingRace"));
        assertTrue(details.contains("5"));
        assertTrue(details.contains("210"));
      } catch (IDNotRecognisedException e) {
        e.printStackTrace();
      }
    }

    @Test
    public void testRiderResultsInStage() {
      registerStageEresults();
      LocalTime[] actual = null;
      LocalTime[] ans =
          new LocalTime[] {
            LocalTime.of(0, 10, 14, 00), LocalTime.of(0, 10, 20, 00), LocalTime.of(0, 0, 51, 0)
          };
      try {
        // assertEquals(portal.getStageSegments(stageE).length + 1,
        // portal.getRiderResultsInStage(stageE, rider3Id).length);
        actual = portal.getRiderResultsInStage(stageE, rider3Id);
      } catch (Exception e) {
        e.printStackTrace();
      }
      assertArrayEquals(ans, actual);
    }

    @Test
    public void testRemoveRaceByName() {
      try {
        portal.removeRaceByName("RacerRacer");
        assertEquals(0, portal.getRaceIds().length);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Test
    public void testRemoveRaceByNameException() {
      assertThrows(
          NameNotRecognisedException.class,
          () -> {
            portal.removeRaceByName("AUniqueNameFSDljfsdljfasd");
          });
    }
  }

  @Nested
  class MTests {

    int teamId,
        rider1Id,
        race1Id,
        race2Id,
        stage1Id,
        stage2Id,
        intspr1,
        intspr2,
        race3Id,
        stage3Id,
        seg1,
        seg2,
        seg3;
    int teamIdRESULTS;
    int rider1IdR;
    int rider2IdR;
    int rider3IdR;
    int rider4IdR;
    int rider5IdR;
    int raceIdR;
    int stageIdR;
    int mountIdR;
    int sprintIdR;

    @BeforeEach
    void b4() {

      try {
        teamId = portal.createTeam("BlueTeam", null);
        race1Id = portal.createRace("BasicRace", null);
        race2Id = portal.createRace("Penis", "poop");
        stage1Id =
            portal.addStageToRace(
                race1Id, "Penguin", null, 10, LocalDateTime.now(), StageType.FLAT);
        stage2Id =
            portal.addStageToRace(
                race1Id, "Giraffe", null, 10, LocalDateTime.now(), StageType.FLAT);
        intspr1 = portal.addIntermediateSprintToStage(stage2Id, 7.0);
        intspr2 = portal.addIntermediateSprintToStage(stage2Id, 7.2);

        teamIdRESULTS = portal.createTeam("RTEAM", null);
        rider1IdR = portal.createRider(teamId, "Andrew", 1999);
        rider2IdR = portal.createRider(teamId, "Bart", 1999);
        rider3IdR = portal.createRider(teamId, "Charlie", 1999);
        rider4IdR = portal.createRider(teamId, "Doug", 1999);
        rider5IdR = portal.createRider(teamId, "Earnie", 1999);

        raceIdR = portal.createRace("BasicRaceTTT", null);
        stageIdR =
            portal.addStageToRace(
                raceIdR, "BasicStage", null, 10, LocalDateTime.now(), StageType.FLAT);
        mountIdR = portal.addCategorizedClimbToStage(stageIdR, 7.0, SegmentType.HC, 5.2, 2.0);
        sprintIdR = portal.addIntermediateSprintToStage(stageIdR, 2.0);
        portal.concludeStagePreparation(stageIdR);
        portal.registerRiderResultsInStage(
            stageIdR,
            rider1IdR,
            LocalTime.of(0, 10, 00, 00),
            LocalTime.of(0, 10, 11, 00),
            LocalTime.of(0, 10, 17, 00),
            LocalTime.of(0, 10, 54, 00));
        portal.registerRiderResultsInStage(
            stageIdR,
            rider2IdR,
            LocalTime.of(0, 10, 00, 00),
            LocalTime.of(0, 10, 13, 00),
            LocalTime.of(0, 10, 18, 00),
            LocalTime.of(0, 10, 52, 00));
        portal.registerRiderResultsInStage(
            stageIdR,
            rider3IdR,
            LocalTime.of(0, 10, 00, 00),
            LocalTime.of(0, 10, 14, 00),
            LocalTime.of(0, 10, 20, 00),
            LocalTime.of(0, 10, 51, 00));
        portal.registerRiderResultsInStage(
            stageIdR,
            rider4IdR,
            LocalTime.of(0, 10, 00, 00),
            LocalTime.of(0, 10, 10, 00),
            LocalTime.of(0, 10, 15, 00),
            LocalTime.of(0, 10, 55, 00));
        portal.registerRiderResultsInStage(
            stageIdR,
            rider5IdR,
            LocalTime.of(0, 10, 00, 00),
            LocalTime.of(0, 10, 17, 00),
            LocalTime.of(0, 10, 21, 00),
            LocalTime.of(0, 10, 50, 00));

      } catch (Exception e) {

      }
    }

    // viewRaceDetails

    @Test
    public void viewRaceDetailsID() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.viewRaceDetails(33333);
          });
    }

    // removeRaceById

    @Test
    public void removeRaceByIdworks() {
      int raceRemovez = -1;
      try {
        raceRemovez = portal.createRace("iamgonnadie", "ded");
      } catch (Exception e) {
      }
      int len = portal.getRaceIds().length;
      try {
        portal.removeRaceById(raceRemovez);
      } catch (Exception e) {
      }
      assertEquals(portal.getRaceIds().length, len - 1);
    }

    @Test
    public void removeRaceByIdthrowId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.removeRaceById(33333);
          });
    }

    // getNumberOfStages

    @Test
    public void getNumberOfStagesThrowId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.getNumberOfStages(33333);
          });
    }

    @Test
    public void getNumberOfStagesReturns1() {
      try {
        assertEquals(portal.getNumberOfStages(race1Id), 2);
      } catch (Exception e) {
      }
    }

    @Test
    public void getNumberOfStagesReturns2() {
      try {
        assertEquals(portal.getNumberOfStages(race2Id), 0);
      } catch (Exception e) {
      }
    }

    // addStageToRace

    @Test
    public void addStageToRaceThrowsID() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            int id =
                portal.addStageToRace(1000, "Yes", null, 10, LocalDateTime.now(), StageType.FLAT);
          });
    }

    @Test
    public void addStageToRaceThrowsSameName1() {
      assertThrows(
          IllegalNameException.class,
          () -> {
            int id =
                portal.addStageToRace(
                    race1Id, "Penguin", null, 10, LocalDateTime.now(), StageType.FLAT);
          });
    }

    @Test
    public void addStageToRaceThrowsSameName2() {
      assertThrows(
          IllegalNameException.class,
          () -> {
            int id =
                portal.addStageToRace(
                    race2Id, "Penguin", null, 10, LocalDateTime.now(), StageType.FLAT);
          });
    }

    @Test
    public void addStageToRaceThrowsNullName() {
      assertThrows(
          InvalidNameException.class,
          () -> {
            int id =
                portal.addStageToRace(race1Id, null, null, 10, LocalDateTime.now(), StageType.FLAT);
          });
    }

    @Test
    public void addStageToRaceThrowsEmptyName() {
      assertThrows(
          InvalidNameException.class,
          () -> {
            int id =
                portal.addStageToRace(race1Id, "", null, 10, LocalDateTime.now(), StageType.FLAT);
          });
    }

    @Test
    public void addStageToRaceThrowsLongName() {
      assertThrows(
          InvalidNameException.class,
          () -> {
            int id =
                portal.addStageToRace(
                    race1Id,
                    "this is a str that has many many " + "many characters, hopefully more than 30",
                    null,
                    10,
                    LocalDateTime.now(),
                    StageType.FLAT);
          });
    }

    @Test
    public void addStageToRaceThrowsShortLength() {
      assertThrows(
          InvalidLengthException.class,
          () -> {
            int id =
                portal.addStageToRace(
                    race1Id, "Penis", null, 4.9, LocalDateTime.now(), StageType.FLAT);
          });
    }

    // getRaceStages

    @Test
    public void getRaceStagesThrowsTd() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            int[] stageids = portal.getRaceStages(30000);
          });
    }

    @Test
    public void getRaceStagesReturns1() {
      try {
        int[] stageids = portal.getRaceStages(race1Id);
        assertEquals(stageids.length, portal.getNumberOfStages(race1Id));
      } catch (Exception e) {
      }
    }

    @Test
    public void getRaceStagesReturns2() {
      try {
        int[] stageids = portal.getRaceStages(race1Id);
        assertEquals(stageids[0], stage1Id);
      } catch (Exception e) {
      }
    }

    @Test
    public void getRaceStagesReturns3() {
      try {
        int[] stageids = portal.getRaceStages(race1Id);
        assertEquals(stageids[1], stage2Id);
      } catch (Exception e) {
      }
    }

    // getStageLength

    @Test
    public void getStageLengthThrowsId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            double leng = portal.getStageLength(30000);
          });
    }

    @Test
    public void getStageLengthReturns() {
      try {
        assertEquals(portal.getStageLength(stage1Id), 10);
      } catch (Exception e) {
      }
    }

    // removeStageById

    @Test
    public void removeStageByIdThrowsId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.removeStageById(33333);
          });
    }

    @Test
    public void removeStageByIdWorks() {
      try {
        int stageDel =
            portal.addStageToRace(
                race1Id, "Delete me", null, 10, LocalDateTime.now(), StageType.FLAT);
        assertEquals(portal.getRaceStages(race1Id).length, 3);
        portal.removeStageById(stageDel);
        assertEquals(portal.getRaceStages(race1Id).length, 2);
      } catch (Exception e) {
      }
    }

    // addCategorizedClimbToStage

    @Test
    public void addCategorizedClimbToStageThrowsId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.addCategorizedClimbToStage(3000, 7.0, SegmentType.HC, 5.2, 2.0);
          });
    }

    @Test
    public void addCategorizedClimbToStageThrowsLongLocation() {
      // ??
      assertThrows(
          InvalidLocationException.class,
          () -> {
            portal.addCategorizedClimbToStage(stage1Id, 100.0, SegmentType.HC, 5.2, 2.0);
          });
    }

    @Test
    public void addCategorizedClimbToStageThrowsConcludedPrep() {
      assertThrows(
          InvalidStageStateException.class,
          () -> {
            portal.concludeStagePreparation(stage2Id);
            portal.addCategorizedClimbToStage(stage2Id, 7.0, SegmentType.HC, 5.2, 2.0);
          });
    }

    @Test
    public void addCategorizedClimbToStageThrowsTT() {
      assertThrows(
          InvalidStageTypeException.class,
          () -> {
            int stage3Id =
                portal.addStageToRace(
                    race1Id, "TimeTrial", null, 10, LocalDateTime.now(), StageType.TT);
            portal.addCategorizedClimbToStage(stage3Id, 7.0, SegmentType.HC, 5.2, 2.0);
          });
    }

    // addIntermediateSprintToStage

    @Test
    public void addIntermediateSprintToStageThrowsId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.addIntermediateSprintToStage(3000, 7.0);
          });
    }

    @Test
    public void addIntermediateSprintToStageThrowsLongLocation() {
      assertThrows(
          InvalidLocationException.class,
          () -> {
            portal.addIntermediateSprintToStage(stage1Id, 100.0);
          });
    }

    @Test
    public void addIntermediateSprintToStageThrowsConcPrep() {
      assertThrows(
          InvalidStageStateException.class,
          () -> {
            portal.concludeStagePreparation(stage2Id);
            portal.addIntermediateSprintToStage(stage2Id, 7.0);
          });
    }

    @Test
    public void addIntermediateSprintToStageThrowsTT() {
      assertThrows(
          InvalidStageTypeException.class,
          () -> {
            int stage3Id =
                portal.addStageToRace(
                    race1Id, "TimeTrial", null, 10, LocalDateTime.now(), StageType.TT);
            portal.addIntermediateSprintToStage(stage3Id, 7.0);
          });
    }

    // removeSegment

    @Test
    public void removeSegmentThrowsId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.removeSegment(3000);
          });
    }

    @Test
    public void removeSegmentThrowsConcPrep() {
      assertThrows(
          InvalidStageStateException.class,
          () -> {
            portal.concludeStagePreparation(stage2Id);
            portal.removeSegment(intspr1);
          });
    }

    // concludeStagePreparation

    @Test
    public void concludeStagePreparationThrowsId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.concludeStagePreparation(3000);
          });
    }

    @Test
    public void concludeStagePreparationThrowsConcPrep() {
      assertThrows(
          InvalidStageStateException.class,
          () -> {
            portal.concludeStagePreparation(stage2Id);
            portal.concludeStagePreparation(stage2Id);
          });
    }

    // getStageSegments

    @Test
    public void getStageSegmentsThrowsId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.getStageSegments(3000);
          });
    }

    @Test
    public void getStageSegmentsOrdered() {
      try {
        race3Id = portal.createRace("ARace", null);
        stage3Id =
            portal.addStageToRace(race3Id, "Stage", null, 10, LocalDateTime.now(), StageType.FLAT);
        seg1 = portal.addIntermediateSprintToStage(stage3Id, 5.0);
        seg2 = portal.addIntermediateSprintToStage(stage3Id, 9.0);
        seg3 = portal.addIntermediateSprintToStage(stage3Id, 7.0);

        int[] segments = portal.getStageSegments(stage3Id);
        int[] ans = new int[] {seg1, seg3, seg2};
        System.out.println(Arrays.toString(ans));
        System.out.println(Arrays.toString(segments));
        assertArrayEquals(segments, ans);
        // assertEquals(segments[0], seg2);
        // assertEquals(segments[1], seg3);
        // assertEquals(segments[2], seg1);

      } catch (Exception e) {
      }
    }

    // createTeam

    @Test
    public void createTeamThrowsSameName() {
      assertThrows(
          IllegalNameException.class,
          () -> {
            portal.createTeam("BlueTeam", "Hello");
          });
    }

    @Test
    public void createTeamThrowsInvalidName() {
      assertThrows(
          InvalidNameException.class,
          () -> {
            portal.createTeam(null, "Hello");
          });
      assertThrows(
          InvalidNameException.class,
          () -> {
            portal.createTeam("", "Hello");
          });
      assertThrows(
          InvalidNameException.class,
          () -> {
            portal.createTeam(
                "Hi i have more than 30 characters hopefully once i have wirrtten enoof wordz",
                "Hello");
          });
    }

    @Test
    public void createTeamReturnsId() {}

    // removeTeam

    @Test
    public void removeTeamThrowsId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.removeSegment(3000);
          });
    }

    @Test
    public void removeTeamDoesIt() {}

    // getTeams

    @Test
    public void getTeamsGetsTeams() {}

    // createRider

    @Test
    public void creatRiderThrowsTd() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.createRider(3000, "Penis Head", 2000);
          });
    }

    @Test
    public void createRiderThrowsIllegalArgumentException() {
      assertThrows(
          IllegalArgumentException.class,
          () -> {
            portal.createRider(teamId, null, 2000);
          });
      assertThrows(
          IllegalArgumentException.class,
          () -> {
            portal.createRider(teamId, "Penis Head", 1899);
          });
    }

    @Test
    public void createRiderCreatesRider() {}

    // removeRider

    @Test
    public void removeRiderThrowsId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.removeRider(3000);
          });
    }

    @Test
    public void removeRiderRemovesRider() {}

    // registerRiderResultsInStage

    @Test
    public void registerRiderResultsInStageSThrowsId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.registerRiderResultsInStage(
                3000,
                rider1Id,
                LocalTime.of(0, 10, 00, 00),
                LocalTime.of(0, 10, 11, 00),
                LocalTime.of(0, 10, 17, 00),
                LocalTime.of(0, 10, 54, 00));
          });
    }

    @Test
    public void registerRiderResultsInStageRThrowsId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.registerRiderResultsInStage(
                stage1Id,
                3000,
                LocalTime.of(0, 10, 00, 00),
                LocalTime.of(0, 10, 11, 00),
                LocalTime.of(0, 10, 17, 00),
                LocalTime.of(0, 10, 54, 00));
          });
    }

    //		@Test
    //		public void registerRiderResultsInStageThrowsDup() {
    //			assertThrows(DuplicatedResultException.class, () -> {
    //				portal.concludeStagePreparation(stage1Id);
    //				portal.registerRiderResultsInStage(stage1Id, rider1IdR, LocalTime.of(0, 10, 00, 00),
    //						LocalTime.of(0, 10, 11, 00),
    //						LocalTime.of(0, 10, 17, 00), LocalTime.of(0, 10, 54, 00));
    //				portal.registerRiderResultsInStage(stage1Id, rider1IdR, LocalTime.of(0, 10, 00, 00),
    //						LocalTime.of(0, 10, 11, 00),
    //						LocalTime.of(0, 10, 17, 00), LocalTime.of(0, 10, 54, 00));
    //			});
    //		}

    @Test
    public void registerRiderResultsInStageThrowsCheckpoint() {
      assertThrows(
          InvalidCheckpointsException.class,
          () -> {
            portal.concludeStagePreparation(stage1Id);
            portal.registerRiderResultsInStage(
                stage1Id,
                rider1IdR,
                LocalTime.of(0, 10, 00, 00),
                LocalTime.of(0, 10, 11, 00),
                LocalTime.of(0, 10, 11, 12));
          });
    }

    @Test
    public void registerRiderResultsInStageThrowsStageState() {
      assertThrows(
          InvalidStageStateException.class,
          () -> {
            int stage =
                portal.addStageToRace(
                    race1Id, "PolarB", null, 10, LocalDateTime.now(), StageType.FLAT);
            portal.registerRiderResultsInStage(
                stage, rider1IdR, LocalTime.of(0, 10, 00, 00), LocalTime.of(0, 10, 11, 00));
          });
    }

    ////		TODO: Why does this not work??
    //		@Test
    //		public void registerRiderResultsInStageDoesIt() {
    //			LocalTime[] times = null;
    //			LocalTime eTime = null;
    //			try {
    //				portal.registerRiderResultsInStage(stage2Id, rider1Id, LocalTime.of(0, 10, 00, 00),
    //						LocalTime.of(0, 10, 11, 00));
    //				 times = portal.getRiderResultsInStage(stage2Id, rider1Id);
    //				 eTime = portal.getRiderAdjustedElapsedTimeInStage(stage2Id,rider1Id);
    //			} catch (Exception e){}
    //			assertArrayEquals(times, new LocalTime[]{LocalTime.of(0, 10, 00, 00), LocalTime.of(0, 10,
    // 11, 00), eTime});
    //		}

    // getRiderResultsInStage

    @Test
    public void getRiderResultsInStageThrowsSId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.getRiderResultsInStage(3000, rider1Id);
          });
    }

    @Test
    public void getRiderResultsInStageThrowsRId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.getRiderResultsInStage(stage1Id, 3000);
          });
    }

    // getRiderAdjustedElapsedTimeInStage

    @Test
    public void getRiderAdjustedElapsedTimeInStageSId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.getRiderAdjustedElapsedTimeInStage(3000, rider1Id);
          });
    }

    @Test
    public void getRiderAdjustedElapsedTimeInStageRId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.getRiderAdjustedElapsedTimeInStage(stage1Id, 3000);
          });
    }

    @Test
    public void getRiderAdjustedElapsedTimeInStageReturns() throws IDNotRecognisedException {
      assertEquals(
          portal.getRiderAdjustedElapsedTimeInStage(stageIdR, rider2IdR),
          LocalTime.of(0, 10, 50, 0));
    }

    // deleteRiderResultsInStage

    @Test
    public void deleteRiderResultsInStageThrowsSId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.deleteRiderResultsInStage(3000, rider1Id);
          });
    }

    @Test
    public void deleteRiderResultsInStageThrowsRId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.deleteRiderResultsInStage(stage1Id, 3000);
          });
    }

    @Test
    public void deleteRiderResultsInStageDeletes() {
      LocalTime[] resultsEmpty = null;
      try {
        portal.deleteRiderResultsInStage(stageIdR, rider2IdR);
        resultsEmpty = portal.getRiderResultsInStage(stageIdR, rider2IdR);
      } catch (Exception e) {
      }
      assertArrayEquals(resultsEmpty, new LocalTime[] {});
    }

    // getRidersRankInStage

    @Test
    public void getRidersRankInStageThrowsId() {
      assertThrows(
          IDNotRecognisedException.class,
          () -> {
            portal.getRidersRankInStage(3000);
          });
    }

    @Test
    public void getRidersRankInStageReturns() {}

    @Test
    public void getRidersRankInStageReturnsEmpty() throws IDNotRecognisedException {
      int newStage = 0;
      try {
        newStage =
            portal.addStageToRace(race1Id, "STAGES", null, 10, LocalDateTime.now(), StageType.FLAT);
      } catch (Exception e) {
      }
      assertArrayEquals(portal.getRidersRankInStage(newStage), new int[] {});
    }

    // getRankedAdjustedElapsedTimesInStage

    // getRidersPointsInStage

    // getRidersMountainPointsInStage

    // eraseCyclingPortal

    // saveCyclingPortal

    // loadCyclingPortal

    // removeRaceByName

    // getGeneralClassificationTimesInRace

    // getRidersPointsInRace

    // getRidersMountainPointsInRace

    // getRidersGeneralClassificationRank

    // getRidersPointClassificationRank

    // getRidersMountainPointClassificationRank

  }
}
