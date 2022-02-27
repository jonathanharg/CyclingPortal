import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.*;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Nested;

import cycling.CyclingPortal;
import cycling.IllegalNameException;
import cycling.InvalidNameException;
import cycling.IDNotRecognisedException;
import java.lang.IllegalArgumentException;
// !! DO NOT SUBMIT: Doesn't start with java.

class CyclingPortalTestApp {
	CyclingPortal portal = new CyclingPortal();
	
	@Nested
	class TeamTests {
		
		@Test
		public void returnsID() {
			//TODO: assert returned int = team.getID
			try {
				int team = portal.createTeam("Team A", "Description...");
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
			assertThrows(InvalidNameException.class, () -> {
				portal.createTeam(null, null);
			});
		}

		@ParameterizedTest
		@ValueSource(strings = {"", "aVeryLongNameAAAAAAAAAAAAAAAAAA"})
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

	@Nested
	class RiderTests {
		// TODO: Verify TeamID test
		
		@Test
		public void returnsID() {
			//TODO: assert returned int = rider.getID
			try {
				int rider = portal.createRider(0, "John Snow", 2000);
			} catch (IDNotRecognisedException | IllegalArgumentException e){
				e.printStackTrace();
			}
		}
		
		@Test
		public void nullName() {
			assertThrows(IllegalArgumentException.class,() -> {
				portal.createRider(0,null,2000);
			});
		}
		
		@ParameterizedTest
		@ValueSource(ints = {2000, 1900, 1950})
		public void validYOB(int validYOBs) {
			try  {
				portal.createRider(0, "Maddie", validYOBs);
			} catch (IDNotRecognisedException | IllegalArgumentException e) {
				e.printStackTrace();
			}
		}
		
		@ParameterizedTest
		@ValueSource(ints = {0,1899,-200})
		public void invalidYOB(int invalidYOBs) {
			assertThrows(IllegalArgumentException.class, () -> {
				portal.createRider(0, "Jonathan", invalidYOBs);
			});
		}
	}
}
