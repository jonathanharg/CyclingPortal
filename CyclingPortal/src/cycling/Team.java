package cycling;

import java.util.ArrayList;

public class Team {
	// TODO: ID, Check if team with same name already exists, check name & desc are valid, add to a global list of teams?,
	
	private String name;
	private String description;
	
	private ArrayList<Rider> riders = new ArrayList<Rider>();
	private static int count = 0;
	private int id;
	
	
	public Team(String name, String description) throws InvalidNameException {
		if (name == null || name.isEmpty() || name.length() > 30) {
			throw new InvalidNameException("Team name cannot be null, empty or have more than 30 characters.");
			// TODO
//		} else if (name already exists in portal) {
//			do this later
		} else {
			this.name = name;
		}
		this.description = description;
		this.id = this.count++;
	}
	
	public int getId() {
		return id;
	}
	
	public Rider[] getRiders() {
		return (Rider[]) riders.toArray();
	}

}
