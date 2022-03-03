package cycling;

public class Race {
	
	private String name;
	private String description;
	
	private static int count = 0;
	private int id;
	
	public Race(String name, String description) throws InvalidNameException {
		if (name == null || name.isEmpty() || name.length() > 30) {
			throw new InvalidNameException("Race name cannot be null, empty or have more than 30 characters.");
		}
		this.name = name;
		this.description = description;
		this.id = this.count++;
	}

	public int getId() {
		return id;
	}

	public String getName() {
		return name;
	}

}
