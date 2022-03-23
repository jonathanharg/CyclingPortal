package cycling;

import java.time.Duration;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class CompetitiveEvent {
	protected HashMap<Rider, Result> results = new HashMap<Rider, Result>();
	private boolean latestResultsCalculated = false; //TODO:DO WE NEED THIS?? & NEED TO BE UNPROTECTED
	private List<Rider> ridersByElapsedTime = null; // TODO: NEEDS TO BE MADE PRIVATE?
	private EventType eventType;

	public CompetitiveEvent(EventType eventType) {
		this.eventType = eventType;
	}

	public Result getRiderResults(Rider rider) {
		// !!!

		return results.get(rider);
	}

	public void deleteRiderResults(Rider rider) {
		results.remove(rider);
		latestResultsCalculated = false;
		ridersByElapsedTime = null;
	}

	public List<Rider> getRidersByElapsedTime() {
		if(ridersByElapsedTime == null){
			ridersByElapsedTime = results.entrySet()
					.stream()
					.sorted(Comparator.comparing(Map.Entry::getValue, Result.sortByElapsedTime))
					.map(Map.Entry::getKey)
					.collect(Collectors.toList());
		}
		return ridersByElapsedTime;
	}

	private void calculateResults() {
		List<Rider> riders = getRidersByElapsedTime();
		
		for(int i = 0; i < results.size(); i++) {
			Rider rider = riders.get(i);
			Result result = results.get(rider);

			// Position Calculation
			result.setPosition(i + 1);
			
//			Adjusted Elapsed Time Calculations
			if(i == 0) {
				result.setAdjustedElapsedTime(result.getElapsedTime());
			} else {
				Rider prevRider = riders.get(i - 1);
				Duration prevTime = results.get(prevRider).getElapsedTime();
				Duration time = results.get(rider).getElapsedTime();
				
				int timeDiff = time.minus(prevTime).compareTo(Duration.ofSeconds(1));
				if(timeDiff <= 0) {
//					Close Finish Condition
					Duration prevAdjustedTime = results.get(prevRider).getAdjustedElapsedTime();
					results.get(rider).setAdjustedElapsedTime(prevAdjustedTime);
				} else {
//					Far Finish Condition
					results.get(rider).setAdjustedElapsedTime(time);
				}
			}
			
		}
		latestResultsCalculated = true;
	}
	
	public void debugPrintResults() {
		calculateResults();
		getRidersByElapsedTime().forEach(rider -> {
			Result riderResults = results.get(rider);
			System.out.println(rider.getName() + " finished in " + riderResults.getPosition() + "th.");
			System.out.println("ET: " + riderResults.getElapsedTime() + " AET: " + riderResults.getAdjustedElapsedTime() + " SP: " + riderResults.getSprintersPoints() + " MP: " + riderResults.getMountainPoints());
		});
	}

}
