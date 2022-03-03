package cycling;
import java.util.ArrayList;

public class Race {
	
	private String name;
	private String description;
	
	private ArrayList<Stage> stages = new ArrayList<>();
	
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
	
	public void addStage(Stage stage) {
		stages.add(stage);
	}
	
	public ArrayList<Stage> getStages(){
		return stages;
	}
	
	public void removeStage(Stage stage) {
		stages.remove(stage);
	}
	
	public String getDetails() {
		double currentLength = 0;
		for (final Stage stage: stages) {
			currentLength = currentLength + stage.getLength();
		}
		String details = ("Race ID: " + id + System.lineSeparator() 
		+ "Name: " +name + System.lineSeparator() 
		+ "Description: " + description  + System.lineSeparator() 
		+ "Number of Stages: " + stages.size() + System.lineSeparator() 
		+ "Total length: " + currentLength);
		return details;
	}

}
