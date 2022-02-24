package cycling;

public class Rider {
	private int teamID;
	private String name;
	private int yearOfBirth;
	
	//TODO: check the teamid is valid, name is valid, YOB is valid, pointer to team?
	// let team know theres new rider in the team
	
	public Rider(int teamID, String name, int yearOfBirth) {
		this.teamID = teamID;
		this.name = name;
		this.yearOfBirth = yearOfBirth;
	}

}
