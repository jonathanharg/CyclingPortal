package cycling;

public class Rider {
	private int teamID;
	private Team team;
	private String name;
	private int yearOfBirth;

	private static int count = 0;
	private int id;

	public Rider(int teamID, Team team, String name, int yearOfBirth) throws IllegalArgumentException {
		if (name == null) {
			throw new java.lang.IllegalArgumentException("The rider's name cannot be null.");
		}
		if (yearOfBirth < 1900) {
			throw new java.lang.IllegalArgumentException(
					"The rider's birth year is invalid, must be greater than 1900.");
		}

		this.teamID = teamID;
		this.team = team;
		this.name = name;
		this.yearOfBirth = yearOfBirth;
		this.id = Rider.count++;
	}

	public int getId() {
		return id;
	}

	public int getTeamId() {
		return teamID;
	}

	public Team getTeam() {
		return team;
	}

	public String getName() {
		return name;
	}
}
