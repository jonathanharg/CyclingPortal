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
        stageF,
        segmentA1,
        segmentA2,
        segmentA3,
        segmentA4,
        segmentA5,
        segmentA6,
        segmentEmnt,
        segmentEsprint,
        segmentF,
        teamId,
        rider1Id,
        rider2Id,
        rider3Id,
        rider4Id,
        rider5Id,
        rider6Id,
        rider7Id,
        rider8Id,
        rider9Id,
        rider10Id,
        rider11Id,
        rider12Id,
        rider13Id,
        rider14Id,
        rider15Id,
        rider16Id,
        rider17Id,
        rider18Id,
        rider19Id,
        rider20Id;

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
        rider6Id = portal.createRider(teamId, "Frank", 1999);
        rider7Id = portal.createRider(teamId, "Greg", 1999);
        rider8Id = portal.createRider(teamId, "Hugh", 1999);
        rider9Id = portal.createRider(teamId, "Issac", 1999);
        rider10Id = portal.createRider(teamId, "Jack", 1999);
        rider11Id = portal.createRider(teamId, "Kyle", 1999);
        rider12Id = portal.createRider(teamId, "Lenny", 1999);
        rider13Id = portal.createRider(teamId, "Max", 1999);
        rider14Id = portal.createRider(teamId, "Ned", 1999);
        rider15Id = portal.createRider(teamId, "Oliver", 1999);
        rider16Id = portal.createRider(teamId, "Patrick", 1999);
        rider17Id = portal.createRider(teamId, "Quentin", 1999);
        rider18Id = portal.createRider(teamId, "Richard", 1999);
        rider19Id = portal.createRider(teamId, "Sam", 1999);
        rider20Id = portal.createRider(teamId, "Trevor", 1999);
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
        stageF =
            portal.addStageToRace(
                raceId, "stage6", "flat", 8.0, LocalDateTime.now().plusHours(5), StageType.FLAT);

        segmentA6 = portal.addIntermediateSprintToStage(stageA, 22.0);
        segmentA4 = portal.addCategorizedClimbToStage(stageA, 17.0, SegmentType.C4, 0.2, 1.0);
        segmentA3 = portal.addCategorizedClimbToStage(stageA, 12.0, SegmentType.C3, 0.1, 3.0);
        segmentA1 = portal.addCategorizedClimbToStage(stageA, 4.0, SegmentType.SPRINT, 12.0, 6.0);
        segmentA2 = portal.addCategorizedClimbToStage(stageA, 9.0, SegmentType.C2, 8.3, 2.0);
        segmentA5 = portal.addCategorizedClimbToStage(stageA, 19.0, SegmentType.HC, 6.9, 1.0);
        segmentEmnt = portal.addCategorizedClimbToStage(stageE, 7.0, SegmentType.HC, 5.2, 2.0);
        segmentEsprint = portal.addIntermediateSprintToStage(stageE, 2.0);
        segmentF = portal.addCategorizedClimbToStage(stageF, 2.0, SegmentType.C1, 6.9, 3.0);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Test
    public void stagesAreOrdered() {
      try {
        int[] result = portal.getRaceStages(raceId);
        int[] ans = new int[] {stageA, stageB, stageC, stageD, stageE, stageF};
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
    public void pointsTestResults() {
      try {
        portal.concludeStagePreparation(stageA);
        portal.concludeStagePreparation(stageB);
        portal.concludeStagePreparation(stageC);
        portal.concludeStagePreparation(stageD);
        portal.concludeStagePreparation(stageE);
        portal.concludeStagePreparation(stageF);

        // !INSERT HERE

        portal.registerRiderResultsInStage(
            stageA,
            rider1Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 47),
            LocalTime.of(2, 19),
            LocalTime.of(3, 0),
            LocalTime.of(3, 28),
            LocalTime.of(3, 33),
            LocalTime.of(4, 3),
            LocalTime.of(4, 44));
        portal.registerRiderResultsInStage(
            stageB, rider1Id, LocalTime.of(9, 0), LocalTime.of(9, 13));
        portal.registerRiderResultsInStage(
            stageC, rider1Id, LocalTime.of(10, 0), LocalTime.of(10, 25));
        portal.registerRiderResultsInStage(
            stageD, rider1Id, LocalTime.of(11, 0), LocalTime.of(11, 40));
        portal.registerRiderResultsInStage(
            stageE,
            rider1Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 49),
            LocalTime.of(13, 10),
            LocalTime.of(13, 13));
        portal.registerRiderResultsInStage(
            stageF, rider1Id, LocalTime.of(13, 0), LocalTime.of(13, 33), LocalTime.of(14, 20));
        portal.registerRiderResultsInStage(
            stageA,
            rider2Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 7),
            LocalTime.of(1, 15),
            LocalTime.of(1, 26),
            LocalTime.of(2, 21),
            LocalTime.of(2, 54),
            LocalTime.of(3, 7),
            LocalTime.of(3, 11));
        portal.registerRiderResultsInStage(
            stageB, rider2Id, LocalTime.of(9, 0), LocalTime.of(9, 53));
        portal.registerRiderResultsInStage(
            stageC, rider2Id, LocalTime.of(10, 0), LocalTime.of(10, 24));
        portal.registerRiderResultsInStage(
            stageD, rider2Id, LocalTime.of(11, 0), LocalTime.of(11, 36));
        portal.registerRiderResultsInStage(
            stageE,
            rider2Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 25),
            LocalTime.of(12, 30),
            LocalTime.of(13, 6));
        portal.registerRiderResultsInStage(
            stageF, rider2Id, LocalTime.of(13, 0), LocalTime.of(13, 18), LocalTime.of(13, 56));
        portal.registerRiderResultsInStage(
            stageA,
            rider3Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 37),
            LocalTime.of(1, 51),
            LocalTime.of(2, 22),
            LocalTime.of(2, 46),
            LocalTime.of(3, 31),
            LocalTime.of(4, 10),
            LocalTime.of(4, 35));
        portal.registerRiderResultsInStage(
            stageB, rider3Id, LocalTime.of(9, 0), LocalTime.of(9, 17));
        portal.registerRiderResultsInStage(
            stageC, rider3Id, LocalTime.of(10, 0), LocalTime.of(10, 28));
        portal.registerRiderResultsInStage(
            stageD, rider3Id, LocalTime.of(11, 0), LocalTime.of(11, 38));
        portal.registerRiderResultsInStage(
            stageE,
            rider3Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 33),
            LocalTime.of(12, 52),
            LocalTime.of(13, 45));
        portal.registerRiderResultsInStage(
            stageF, rider3Id, LocalTime.of(13, 0), LocalTime.of(13, 42), LocalTime.of(14, 22));
        portal.registerRiderResultsInStage(
            stageA,
            rider4Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 13),
            LocalTime.of(1, 47),
            LocalTime.of(1, 51),
            LocalTime.of(2, 20),
            LocalTime.of(2, 32),
            LocalTime.of(3, 13),
            LocalTime.of(3, 49));
        portal.registerRiderResultsInStage(
            stageB, rider4Id, LocalTime.of(9, 0), LocalTime.of(9, 24));
        portal.registerRiderResultsInStage(
            stageC, rider4Id, LocalTime.of(10, 0), LocalTime.of(10, 16));
        portal.registerRiderResultsInStage(
            stageD, rider4Id, LocalTime.of(11, 0), LocalTime.of(11, 46));
        portal.registerRiderResultsInStage(
            stageE,
            rider4Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 19),
            LocalTime.of(12, 29),
            LocalTime.of(13, 12));
        portal.registerRiderResultsInStage(
            stageF, rider4Id, LocalTime.of(13, 0), LocalTime.of(13, 41), LocalTime.of(13, 43));
        portal.registerRiderResultsInStage(
            stageA,
            rider5Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 6),
            LocalTime.of(2, 0),
            LocalTime.of(2, 27),
            LocalTime.of(3, 22),
            LocalTime.of(3, 46),
            LocalTime.of(4, 33),
            LocalTime.of(5, 20));
        portal.registerRiderResultsInStage(
            stageB, rider5Id, LocalTime.of(9, 0), LocalTime.of(9, 38));
        portal.registerRiderResultsInStage(
            stageC, rider5Id, LocalTime.of(10, 0), LocalTime.of(10, 54));
        portal.registerRiderResultsInStage(
            stageD, rider5Id, LocalTime.of(11, 0), LocalTime.of(11, 42));
        portal.registerRiderResultsInStage(
            stageE,
            rider5Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 32),
            LocalTime.of(12, 37),
            LocalTime.of(13, 3));
        portal.registerRiderResultsInStage(
            stageF, rider5Id, LocalTime.of(13, 0), LocalTime.of(13, 4), LocalTime.of(13, 51));
        portal.registerRiderResultsInStage(
            stageA,
            rider6Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 25),
            LocalTime.of(1, 45),
            LocalTime.of(2, 0),
            LocalTime.of(2, 10),
            LocalTime.of(2, 58),
            LocalTime.of(3, 42),
            LocalTime.of(3, 53));
        portal.registerRiderResultsInStage(
            stageB, rider6Id, LocalTime.of(9, 0), LocalTime.of(9, 10));
        portal.registerRiderResultsInStage(
            stageC, rider6Id, LocalTime.of(10, 0), LocalTime.of(10, 18));
        portal.registerRiderResultsInStage(
            stageD, rider6Id, LocalTime.of(11, 0), LocalTime.of(11, 54));
        portal.registerRiderResultsInStage(
            stageE,
            rider6Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 12),
            LocalTime.of(13, 7),
            LocalTime.of(13, 25));
        portal.registerRiderResultsInStage(
            stageF, rider6Id, LocalTime.of(13, 0), LocalTime.of(13, 47), LocalTime.of(14, 16));
        portal.registerRiderResultsInStage(
            stageA,
            rider7Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 3),
            LocalTime.of(1, 16),
            LocalTime.of(1, 46),
            LocalTime.of(2, 22),
            LocalTime.of(3, 17),
            LocalTime.of(3, 27),
            LocalTime.of(4, 10));
        portal.registerRiderResultsInStage(
            stageB, rider7Id, LocalTime.of(9, 0), LocalTime.of(9, 46));
        portal.registerRiderResultsInStage(
            stageC, rider7Id, LocalTime.of(10, 0), LocalTime.of(10, 23));
        portal.registerRiderResultsInStage(
            stageD, rider7Id, LocalTime.of(11, 0), LocalTime.of(11, 34));
        portal.registerRiderResultsInStage(
            stageE,
            rider7Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 36),
            LocalTime.of(12, 59),
            LocalTime.of(13, 30));
        portal.registerRiderResultsInStage(
            stageF, rider7Id, LocalTime.of(13, 0), LocalTime.of(13, 29), LocalTime.of(13, 34));
        portal.registerRiderResultsInStage(
            stageA,
            rider8Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 55),
            LocalTime.of(2, 7),
            LocalTime.of(2, 58),
            LocalTime.of(3, 14),
            LocalTime.of(4, 9),
            LocalTime.of(4, 32),
            LocalTime.of(5, 25));
        portal.registerRiderResultsInStage(
            stageB, rider8Id, LocalTime.of(9, 0), LocalTime.of(9, 40));
        portal.registerRiderResultsInStage(
            stageC, rider8Id, LocalTime.of(10, 0), LocalTime.of(10, 34));
        portal.registerRiderResultsInStage(
            stageD, rider8Id, LocalTime.of(11, 0), LocalTime.of(11, 55));
        portal.registerRiderResultsInStage(
            stageE,
            rider8Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 24),
            LocalTime.of(13, 17),
            LocalTime.of(13, 26));
        portal.registerRiderResultsInStage(
            stageF, rider8Id, LocalTime.of(13, 0), LocalTime.of(13, 16), LocalTime.of(13, 27));
        portal.registerRiderResultsInStage(
            stageA,
            rider9Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 32),
            LocalTime.of(1, 34),
            LocalTime.of(1, 43),
            LocalTime.of(2, 5),
            LocalTime.of(2, 14),
            LocalTime.of(2, 36),
            LocalTime.of(3, 19));
        portal.registerRiderResultsInStage(
            stageB, rider9Id, LocalTime.of(9, 0), LocalTime.of(9, 35));
        portal.registerRiderResultsInStage(
            stageC, rider9Id, LocalTime.of(10, 0), LocalTime.of(10, 44));
        portal.registerRiderResultsInStage(
            stageD, rider9Id, LocalTime.of(11, 0), LocalTime.of(11, 16));
        portal.registerRiderResultsInStage(
            stageE,
            rider9Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 52),
            LocalTime.of(12, 58),
            LocalTime.of(13, 14));
        portal.registerRiderResultsInStage(
            stageF, rider9Id, LocalTime.of(13, 0), LocalTime.of(13, 24), LocalTime.of(13, 46));
        portal.registerRiderResultsInStage(
            stageA,
            rider10Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 27),
            LocalTime.of(1, 39),
            LocalTime.of(2, 24),
            LocalTime.of(2, 48),
            LocalTime.of(3, 26),
            LocalTime.of(3, 48),
            LocalTime.of(4, 30));
        portal.registerRiderResultsInStage(
            stageB, rider10Id, LocalTime.of(9, 0), LocalTime.of(9, 28));
        portal.registerRiderResultsInStage(
            stageC, rider10Id, LocalTime.of(10, 0), LocalTime.of(10, 13));
        portal.registerRiderResultsInStage(
            stageD, rider10Id, LocalTime.of(11, 0), LocalTime.of(11, 17));
        portal.registerRiderResultsInStage(
            stageE,
            rider10Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 11),
            LocalTime.of(13, 5),
            LocalTime.of(13, 54));
        portal.registerRiderResultsInStage(
            stageF, rider10Id, LocalTime.of(13, 0), LocalTime.of(13, 2), LocalTime.of(13, 10));
        portal.registerRiderResultsInStage(
            stageA,
            rider11Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 31),
            LocalTime.of(2, 14),
            LocalTime.of(2, 46),
            LocalTime.of(2, 59),
            LocalTime.of(3, 48),
            LocalTime.of(4, 11),
            LocalTime.of(4, 18));
        portal.registerRiderResultsInStage(
            stageB, rider11Id, LocalTime.of(9, 0), LocalTime.of(9, 32));
        portal.registerRiderResultsInStage(
            stageC, rider11Id, LocalTime.of(10, 0), LocalTime.of(10, 9));
        portal.registerRiderResultsInStage(
            stageD, rider11Id, LocalTime.of(11, 0), LocalTime.of(11, 27));
        portal.registerRiderResultsInStage(
            stageE,
            rider11Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 40),
            LocalTime.of(13, 2),
            LocalTime.of(13, 36));
        portal.registerRiderResultsInStage(
            stageF, rider11Id, LocalTime.of(13, 0), LocalTime.of(13, 13), LocalTime.of(13, 48));
        portal.registerRiderResultsInStage(
            stageA,
            rider12Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 12),
            LocalTime.of(1, 49),
            LocalTime.of(2, 14),
            LocalTime.of(2, 18),
            LocalTime.of(2, 47),
            LocalTime.of(3, 5),
            LocalTime.of(3, 29));
        portal.registerRiderResultsInStage(
            stageB, rider12Id, LocalTime.of(9, 0), LocalTime.of(9, 30));
        portal.registerRiderResultsInStage(
            stageC, rider12Id, LocalTime.of(10, 0), LocalTime.of(10, 7));
        portal.registerRiderResultsInStage(
            stageD, rider12Id, LocalTime.of(11, 0), LocalTime.of(11, 47));
        portal.registerRiderResultsInStage(
            stageE,
            rider12Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 34),
            LocalTime.of(12, 57),
            LocalTime.of(13, 44));
        portal.registerRiderResultsInStage(
            stageF, rider12Id, LocalTime.of(13, 0), LocalTime.of(13, 45), LocalTime.of(13, 52));
        portal.registerRiderResultsInStage(
            stageA,
            rider13Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 35),
            LocalTime.of(2, 2),
            LocalTime.of(2, 28),
            LocalTime.of(3, 5),
            LocalTime.of(3, 10),
            LocalTime.of(3, 31),
            LocalTime.of(4, 1));
        portal.registerRiderResultsInStage(
            stageB, rider13Id, LocalTime.of(9, 0), LocalTime.of(9, 11));
        portal.registerRiderResultsInStage(
            stageC, rider13Id, LocalTime.of(10, 0), LocalTime.of(10, 39));
        portal.registerRiderResultsInStage(
            stageD, rider13Id, LocalTime.of(11, 0), LocalTime.of(11, 41));
        portal.registerRiderResultsInStage(
            stageE,
            rider13Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 46),
            LocalTime.of(13, 31),
            LocalTime.of(14, 26));
        portal.registerRiderResultsInStage(
            stageF, rider13Id, LocalTime.of(13, 0), LocalTime.of(13, 43), LocalTime.of(13, 58));
        portal.registerRiderResultsInStage(
            stageA,
            rider14Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 28),
            LocalTime.of(1, 42),
            LocalTime.of(1, 50),
            LocalTime.of(2, 2),
            LocalTime.of(2, 33),
            LocalTime.of(2, 53),
            LocalTime.of(3, 38));
        portal.registerRiderResultsInStage(
            stageB, rider14Id, LocalTime.of(9, 0), LocalTime.of(9, 31));
        portal.registerRiderResultsInStage(
            stageC, rider14Id, LocalTime.of(10, 0), LocalTime.of(10, 55));
        portal.registerRiderResultsInStage(
            stageD, rider14Id, LocalTime.of(11, 0), LocalTime.of(11, 2));
        portal.registerRiderResultsInStage(
            stageE,
            rider14Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 7),
            LocalTime.of(12, 19),
            LocalTime.of(12, 52));
        portal.registerRiderResultsInStage(
            stageF, rider14Id, LocalTime.of(13, 0), LocalTime.of(13, 59), LocalTime.of(14, 23));
        portal.registerRiderResultsInStage(
            stageA,
            rider15Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 4),
            LocalTime.of(1, 9),
            LocalTime.of(1, 36),
            LocalTime.of(2, 16),
            LocalTime.of(2, 25),
            LocalTime.of(3, 0),
            LocalTime.of(3, 12));
        portal.registerRiderResultsInStage(
            stageB, rider15Id, LocalTime.of(9, 0), LocalTime.of(9, 15));
        portal.registerRiderResultsInStage(
            stageC, rider15Id, LocalTime.of(10, 0), LocalTime.of(10, 48));
        portal.registerRiderResultsInStage(
            stageD, rider15Id, LocalTime.of(11, 0), LocalTime.of(11, 52));
        portal.registerRiderResultsInStage(
            stageE,
            rider15Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 3),
            LocalTime.of(12, 32),
            LocalTime.of(12, 47));
        portal.registerRiderResultsInStage(
            stageF, rider15Id, LocalTime.of(13, 0), LocalTime.of(13, 37), LocalTime.of(14, 37));
        portal.registerRiderResultsInStage(
            stageA,
            rider16Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 44),
            LocalTime.of(2, 1),
            LocalTime.of(2, 9),
            LocalTime.of(2, 57),
            LocalTime.of(3, 1),
            LocalTime.of(3, 11),
            LocalTime.of(3, 56));
        portal.registerRiderResultsInStage(
            stageB, rider16Id, LocalTime.of(9, 0), LocalTime.of(9, 7));
        portal.registerRiderResultsInStage(
            stageC, rider16Id, LocalTime.of(10, 0), LocalTime.of(10, 45));
        portal.registerRiderResultsInStage(
            stageD, rider16Id, LocalTime.of(11, 0), LocalTime.of(11, 48));
        portal.registerRiderResultsInStage(
            stageE,
            rider16Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 13),
            LocalTime.of(12, 34),
            LocalTime.of(13, 4));
        portal.registerRiderResultsInStage(
            stageF, rider16Id, LocalTime.of(13, 0), LocalTime.of(13, 55), LocalTime.of(14, 19));
        portal.registerRiderResultsInStage(
            stageA,
            rider17Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 19),
            LocalTime.of(2, 6),
            LocalTime.of(2, 51),
            LocalTime.of(2, 56),
            LocalTime.of(3, 8),
            LocalTime.of(3, 19),
            LocalTime.of(3, 26));
        portal.registerRiderResultsInStage(
            stageB, rider17Id, LocalTime.of(9, 0), LocalTime.of(9, 6));
        portal.registerRiderResultsInStage(
            stageC, rider17Id, LocalTime.of(10, 0), LocalTime.of(10, 33));
        portal.registerRiderResultsInStage(
            stageD, rider17Id, LocalTime.of(11, 0), LocalTime.of(11, 37));
        portal.registerRiderResultsInStage(
            stageE,
            rider17Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 9),
            LocalTime.of(12, 56),
            LocalTime.of(13, 33));
        portal.registerRiderResultsInStage(
            stageF, rider17Id, LocalTime.of(13, 0), LocalTime.of(13, 3), LocalTime.of(13, 6));
        portal.registerRiderResultsInStage(
            stageA,
            rider18Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 34),
            LocalTime.of(1, 53),
            LocalTime.of(2, 35),
            LocalTime.of(3, 6),
            LocalTime.of(3, 13),
            LocalTime.of(3, 30),
            LocalTime.of(3, 54));
        portal.registerRiderResultsInStage(
            stageB, rider18Id, LocalTime.of(9, 0), LocalTime.of(9, 49));
        portal.registerRiderResultsInStage(
            stageC, rider18Id, LocalTime.of(10, 0), LocalTime.of(10, 35));
        portal.registerRiderResultsInStage(
            stageD, rider18Id, LocalTime.of(11, 0), LocalTime.of(11, 9));
        portal.registerRiderResultsInStage(
            stageE,
            rider18Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 42),
            LocalTime.of(12, 47),
            LocalTime.of(13, 15));
        portal.registerRiderResultsInStage(
            stageF, rider18Id, LocalTime.of(13, 0), LocalTime.of(13, 46), LocalTime.of(13, 57));
        portal.registerRiderResultsInStage(
            stageA,
            rider19Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 30),
            LocalTime.of(2, 11),
            LocalTime.of(2, 34),
            LocalTime.of(3, 8),
            LocalTime.of(4, 2),
            LocalTime.of(4, 55),
            LocalTime.of(5, 6));
        portal.registerRiderResultsInStage(
            stageB, rider19Id, LocalTime.of(9, 0), LocalTime.of(9, 39));
        portal.registerRiderResultsInStage(
            stageC, rider19Id, LocalTime.of(10, 0), LocalTime.of(10, 10));
        portal.registerRiderResultsInStage(
            stageD, rider19Id, LocalTime.of(11, 0), LocalTime.of(11, 4));
        portal.registerRiderResultsInStage(
            stageE,
            rider19Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 41),
            LocalTime.of(13, 6),
            LocalTime.of(13, 32));
        portal.registerRiderResultsInStage(
            stageF, rider19Id, LocalTime.of(13, 0), LocalTime.of(13, 5), LocalTime.of(13, 15));
        portal.registerRiderResultsInStage(
            stageA,
            rider20Id,
            LocalTime.of(1, 0),
            LocalTime.of(1, 5),
            LocalTime.of(1, 35),
            LocalTime.of(1, 39),
            LocalTime.of(2, 16),
            LocalTime.of(2, 35),
            LocalTime.of(3, 15),
            LocalTime.of(3, 33));
        portal.registerRiderResultsInStage(
            stageB, rider20Id, LocalTime.of(9, 0), LocalTime.of(9, 27));
        portal.registerRiderResultsInStage(
            stageC, rider20Id, LocalTime.of(10, 0), LocalTime.of(10, 38));
        portal.registerRiderResultsInStage(
            stageD, rider20Id, LocalTime.of(11, 0), LocalTime.of(11, 51));
        portal.registerRiderResultsInStage(
            stageE,
            rider20Id,
            LocalTime.of(12, 0),
            LocalTime.of(12, 44),
            LocalTime.of(12, 50),
            LocalTime.of(13, 19));
        portal.registerRiderResultsInStage(
            stageF, rider20Id, LocalTime.of(13, 0), LocalTime.of(13, 40), LocalTime.of(14, 10));

        // !END OF INSERT
      } catch (IDNotRecognisedException
          | DuplicatedResultException
          | InvalidCheckpointsException
          | InvalidStageStateException e) {
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
        assertTrue(details.contains("6"));
        assertTrue(details.contains("218"));
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

    @Test
    public void testRemoveSegment() {
      try {
        portal.removeSegment(segmentA3);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Test
    public void testRemoveRaceById() {
      try {
        portal.removeRaceById(raceId);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Test
    public void testRemoveStageById() {
      try {
        portal.removeStageById(stageA);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Test
    public void testRemoveTeam() {
      try {
        portal.removeTeam(teamId);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Test
    public void testRemoveRider() {
      try {
        portal.removeRider(rider4Id);
      } catch (Exception e) {
        e.printStackTrace();
      }
    }

    @Nested
    class RaceResultsTests {
      @Test
      void result_stage_test() {
        pointsTestResults();
        int[] ranks = null;
        try {
          ranks = portal.getRidersGeneralClassificationRank(raceId);
        } catch (IDNotRecognisedException e) {
          e.printStackTrace();
        }
      }

      @Test
      public void RESULT_GC() {
        pointsTestResults();
        int[] ranks = new int[] {};
        int[] ans =
            new int[] {
              rider17Id, rider9Id, rider2Id, rider4Id, rider14Id, rider12Id, rider15Id, rider10Id,
              rider18Id, rider19Id, rider11Id, rider6Id, rider7Id, rider20Id, rider16Id, rider1Id,
              rider13Id, rider3Id, rider8Id, rider5Id
            };
        try {
          ranks = portal.getRidersGeneralClassificationRank(raceId);
        } catch (IDNotRecognisedException e) {
          e.printStackTrace();
        }
        assertArrayEquals(ans, ranks);
        // ! this does not test for adjusted elapsed times.
      }

      @Test
      public void RESULT_GCTimes() {
        pointsTestResults();
        LocalTime[] times = new LocalTime[] {};
        LocalTime[] ans =
            new LocalTime[] {
              LocalTime.of(5, 21),
              LocalTime.of(5, 54),
              LocalTime.of(6, 6),
              LocalTime.of(6, 10),
              LocalTime.of(6, 21),
              LocalTime.of(6, 29),
              LocalTime.of(6, 31),
              LocalTime.of(6, 32),
              LocalTime.of(6, 39),
              LocalTime.of(6, 46),
              LocalTime.of(6, 50),
              LocalTime.of(6, 56),
              LocalTime.of(6, 57),
              LocalTime.of(6, 58),
              LocalTime.of(6, 59),
              LocalTime.of(7, 35),
              LocalTime.of(7, 56),
              LocalTime.of(8, 5),
              LocalTime.of(8, 27),
              LocalTime.of(8, 28)
            };
        try {
          times = portal.getGeneralClassificationTimesInRace(raceId);
        } catch (IDNotRecognisedException e) {
          e.printStackTrace();
        }
        assertArrayEquals(ans, times);
        // ! this does not test for adjusted elapsed times.
      }

      @Test
      public void RESULT_GCPoint() {
        pointsTestResults();
        int[] points = new int[] {};
        int[] ans =
            new int[] {
              134, 85, 120, 87, 115, 77, 142, 89, 51, 68, 53, 62, 75, 56, 61, 37, 32, 27, 35, 52
            };
        try {
          points = portal.getRidersPointsInRace(raceId);
        } catch (IDNotRecognisedException e) {
          e.printStackTrace();
        }
        assertArrayEquals(ans, points);
        // ! this does not test for adjusted elapsed times.
      }

      @Test
      public void RESULT_GCMount() {
        pointsTestResults();
        int[] points = new int[] {};
        int[] ans = new int[] {8, 21, 21, 27, 31, 6, 31, 10, 4, 4, 2, 2, 2, 10, 8, 0, 0, 0, 1, 12};
        try {
          points = portal.getRidersMountainPointsInRace(raceId);
        } catch (IDNotRecognisedException e) {
          e.printStackTrace();
        }
        assertArrayEquals(ans, points);
        // ! this does not test for adjusted elapsed times.
      }

      @Test
      public void RESULT_PointRank() {
        pointsTestResults();
        int[] points = new int[] {};
        int[] ans =
            new int[] {
              rider15Id, rider17Id, rider2Id, rider14Id, rider10Id, rider4Id, rider9Id, rider12Id,
              rider7Id, rider19Id, rider6Id, rider16Id, rider20Id, rider11Id, rider5Id, rider18Id,
              rider1Id, rider8Id, rider13Id, rider3Id
            };
        try {
          points = portal.getRidersPointClassificationRank(raceId);
        } catch (IDNotRecognisedException e) {
          e.printStackTrace();
        }
        assertArrayEquals(ans, points);
        // ! this does not test for adjusted elapsed times.
      }

      @Test
      public void RESULT_MountRank() {
        pointsTestResults();
        int[] points = new int[] {};
        int[] ans =
            new int[] {
              rider14Id, rider15Id, rider4Id, rider2Id, rider9Id, rider5Id, rider10Id, rider20Id,
              rider16Id, rider17Id, rider12Id, rider18Id, rider19Id, rider6Id, rider7Id, rider11Id,
              rider8Id, rider1Id, rider3Id, rider13Id
            };
        try {
          points = portal.getRidersMountainPointClassificationRank(raceId);
        } catch (IDNotRecognisedException e) {
          e.printStackTrace();
        }
        assertEquals(rider4Id, points[2]);
        assertEquals(rider5Id, points[5]);
        assertEquals(rider12Id, points[10]);
        assertEquals(rider8Id, points[16]);

        // ! this does not test for adjusted elapsed times.
      }
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
        // System.out.println(Arrays.toString(ans));
        // System.out.println(Arrays.toString(segments));
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

    // @Test
    // public void registerRiderResultsInStageThrowsDup() {
    // assertThrows(DuplicatedResultException.class, () -> {
    // portal.concludeStagePreparation(stage1Id);
    // portal.registerRiderResultsInStage(stage1Id, rider1IdR, LocalTime.of(0, 10,
    // 00, 00),
    // LocalTime.of(0, 10, 11, 00),
    // LocalTime.of(0, 10, 17, 00), LocalTime.of(0, 10, 54, 00));
    // portal.registerRiderResultsInStage(stage1Id, rider1IdR, LocalTime.of(0, 10,
    // 00, 00),
    // LocalTime.of(0, 10, 11, 00),
    // LocalTime.of(0, 10, 17, 00), LocalTime.of(0, 10, 54, 00));
    // });
    // }

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
