package cycling;

public class Rider {
	private final Team team;
	private final String name;
	private final int yearOfBirth;

	private static int count = 0;
	private final int id;

	public Rider(Team team, String name, int yearOfBirth) throws IllegalArgumentException {
		if (name == null) {
			throw new java.lang.IllegalArgumentException("The rider's name cannot be null.");
		}
		if (yearOfBirth < 1900) {
			throw new java.lang.IllegalArgumentException(
					"The rider's birth year is invalid, must be greater than 1900.");
		}

		this.team = team;
		this.name = name;
		this.yearOfBirth = yearOfBirth;
		this.id = Rider.count++;
	}

	static void resetIdCounter() {
		count = 0;
	}

	static int getIdCounter() {
		return count;
	}

	static void setIdCounter(int newCount){
		count = newCount;
	}

	public int getId() {
		return id;
	}

	public Team getTeam() {
		return team;
	}
}
