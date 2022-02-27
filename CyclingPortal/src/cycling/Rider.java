package cycling;

public class Rider {
	private int teamID;
	private String name;
	private int yearOfBirth;
	
	private static int count = 0;
	private int id;
	
	//TODO: check the teamid is valid, name is valid, YOB is valid, pointer to team?
	// let team know theres new rider in the team
	// is having same team and rider id's a good idea?
	
	public Rider(int teamID, String name, int yearOfBirth) throws IDNotRecognisedException{
		if (verifyTeamID(teamID) == false) {
			throw new IDNotRecognisedException("Team ID does not exist on the system.");
		}
		if (name == null) {
			throw new java.lang.IllegalArgumentException("The rider's name cannot be null.");
		}
		if (yearOfBirth < 1900) {
			throw new java.lang.IllegalArgumentException("The rider's birth year is invalid, must be greater than 1900.");
		}
		this.teamID = teamID;
		this.name = name;
		this.yearOfBirth = yearOfBirth;
		this.id = this.count++;
	}
	
	private boolean verifyTeamID(int teamID) {
		//TODO: check that teamID matches preexisting ID
		return true;
	}
	
	public int getID() {
		return id;
	}
	
}
