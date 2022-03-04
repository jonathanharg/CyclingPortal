package cycling;

import java.util.ArrayList;

public class Team {
	private String name;
	private String description;

	private ArrayList<Rider> riders = new ArrayList<Rider>();
	private static int count = 0;
	private int id;

	public Team(String name, String description) throws InvalidNameException {
		if (name == null || name.isEmpty() || name.length() > 30) {
			throw new InvalidNameException("Team name cannot be null, empty or have more than 30 characters.");
		}
		this.name = name;
		this.description = description;
		this.id = this.count++;
	}

	public String getName() {
		return name;
	}

	public int getId() {
		return id;
	}

	public Rider getRiderByID(int ID) throws IDNotRecognisedException {
		for (Rider rider : riders) {
			if (rider.getId() == ID) {
				return rider;
			}
		}
		throw new IDNotRecognisedException("Rider ID not found.");
	}

	public void removeRider(Rider rider) {
		riders.remove(rider);
	}

	public ArrayList<Rider> getRiders() {
		return riders;
	}

	public void addRider(Rider rider) {
		riders.add(rider);
	}
}
