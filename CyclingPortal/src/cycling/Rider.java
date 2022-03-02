package cycling;

public class Rider {
	private int teamID;
	private String name;
	private int yearOfBirth;
	
	private static int count = 0;
	private int id;
	
	public Rider(int teamID, String name, int yearOfBirth) throws IllegalArgumentException{
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
	
	public int getID() {
		return id;
	}
	
}
