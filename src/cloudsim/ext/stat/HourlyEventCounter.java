package cloudsim.ext.stat;

import cloudsim.ext.UserBase;

/**
 * A utitlity class to keep a list of events occuring during the day grouped by the 
 * hour of the day.
 * 
 * @author Bhathiya Wickremasinghe
 *
 */
public class HourlyEventCounter {

	private String name;
	private long[] hourlyCount = new long[24];
	static int use = 0;
	private UserBase getCurrentTime;
	private long[] hourlyOccur = new long[24];
	public HourlyEventCounter(String name) throws Exception {
		this.name = name;
		for (int i = 0; i < 24; i++){
			hourlyCount[i] = 0;
			hourlyOccur[i] = 0;
		}
	}
	
	
	public void addEvent(double timeInMS, int countAs){
		use++;
		//System.out.println("Time Original(HourlyEventCounter.java) "+timeInMS+" count AS "+countAs+ " use "+use);
		int timeInHrs = (int) Math.floor( (timeInMS / (1000 * 60 * 60)) % 24);

		timeInHrs = getCurrentTime.CurrentTime();

		//System.out.println("Current Updated Time is "+timeInHrs);
		if (timeInHrs < 24){
			hourlyCount[timeInHrs] += countAs;
			hourlyOccur[timeInHrs]++;
		}
		//printHourlyCounts();
	}
	
	public void printHourlyCounts(){
		System.out.println("*********** " + name + " *************");
		for (int i = 0; i < 24; i++){
			System.out.println((i+1) + "-" + hourlyCount[i]);
		}
		System.out.println("for each hour of "+name);
		for (int i = 0; i < 24; i++){
			System.out.println((i+1) + "-" + hourlyOccur[i]);
		}
	}
	
	public long getMax(){
		long max = -1;
		for (long i : hourlyCount){
			if (i > max){
				max = i;
			}
		}
		
		return max;
	}

	/**
	 * @return the hourlyCount
	 */
	public long[] getHourlyCount() {
		return hourlyCount;
	}

}
