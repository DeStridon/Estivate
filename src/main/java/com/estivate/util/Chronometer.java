package com.estivate.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

/**
 * Chronometer is class that can be instantiate to have the time between the start and the end.
 * The current time limit is 5 000 ms. So if the time take more than this limit, it will show in the console. 
 * 
 * @Autowired Chronometer chronomter and @Autowired @Qualifier(Config.CHRONO) Chronometer chrono
 * "chronomter" and "chrono" are two different different beans. 
 * - The first one call the bean "chronometer". It will be used for each HTTP request. Each time we call this
 * 	 bean, the data will be reset to null.
 * - The second one call the bean "chrono". It will be used if we want to send to performance database. To have
 *   more information, see the class : com.datawords.framework.annotation.Performance.
 *   When you call this object, it will be the same reference for one class.
 * Do not used @Autowired Chronometer chrono, it's the same as @Autowired @Qualifier(Config.CHRONO) Chronometer chrono.
 * Prefer use @Qualifier to call the exact bean and prevent errors. 
 * If you don't know which @Autowired use, do not used it in your class. It will prevent for errors.
 * 
 * Example of case if you are using Qualifier :
 * - You are using a class with a main method that will call many private/public method, like scheduler. 
 * 	 You want to have the chronometer of the main method and also, the private method.
 * 		1. Start the chrono before calling the all private method
 * 		2. Create a new Chronometer in each private method you want with Chronometer.createTimeTriggeredChronometer
 * 		3. At the end of theses new chronometers, add in the details of the chrono, the chronometer log.
 * - You are using a class that was not invoked by http. This class call a method in RestController using Chronometer.
 * 		1. In the method of the RestController, add the chronometer at the parameters to use the reference of the exact chronometer.
 * - You have multiple private method and there is no main method.
 * 		1. Do not use Autowired. Create a chronometer like before with Chronometer.createTimeTriggeredChronometer
 */
@Slf4j
public class Chronometer {
	
	long begin = System.currentTimeMillis();
	long lastStep = begin;
	protected List<Pair<String, Float>> steps = new ArrayList<>();
	boolean activated = true;
	public boolean doLog = true;
	
	Map<String, String> details = new LinkedHashMap<>();
	
	
	private Integer timeLimit = 5000;
	
	@Getter String name;
	
	public static Chronometer createTimeTriggeredChronometer(String name, int millisecondsTimeLimit) {
		Chronometer chrono = new Chronometer(name);
		chrono.doLog = false;
		chrono.timeLimit = millisecondsTimeLimit;
		return chrono;
	}
	
	public static Chronometer createTimeTriggeredChronometer(String name) {
		return createTimeTriggeredChronometer(name, 5000);
	}
	
	public Chronometer(String name){
		this.name = name;
	}
	
	public Chronometer(String name, boolean activated){
		this.name = name;
		this.activated = activated;
	}
	
	public Chronometer(String name, boolean activated, boolean doLog){
		this.name = name;
		this.activated = activated;
		this.doLog = doLog;
	}
	
	public long getBeginTime() {
		return this.begin;
	}
	
	public Chronometer setDetail(String key, String value) {
		this.details.put(key, value);
		return this;
	}
	
	public Chronometer setDetail(String key, Object value) {
		if(value != null) {
			return setDetail(key, value.toString());
		}
		return this;
	}

	
	public Chronometer start(){
		if(this.activated){
			this.begin = System.currentTimeMillis();
		}
		return this;
	}

	public Chronometer start(String name) {
		this.name = name;
		this.begin = System.currentTimeMillis();
		this.lastStep = begin;
		this.steps = new ArrayList<>();
		this.details = new HashMap<>();
		this.activated = true;
		this.doLog = false;
		return this.start();
	}
	
	public Chronometer start(String name, int millisecondsTimeLimit) {
		this.doLog = false;
		this.timeLimit = millisecondsTimeLimit;
		return this.start(name);
	}
	
	public Chronometer start(String name, boolean doLog) {
		this.name = name;
		this.begin = System.currentTimeMillis();
		this.lastStep = begin;
		this.steps = new ArrayList<>();
		this.details = new HashMap<>();
		this.activated = true;
		this.doLog = doLog;
		return this.start();
	}
	
	public Chronometer step(String stepName){
		
		if(!this.activated){
			return this;
		}

		long currentStep = System.currentTimeMillis();
		float spentTime = (currentStep - this.lastStep) / 1000f;
		
		if(this.doLog == false && this.timeLimit != null && (currentStep - this.begin) > this.timeLimit) {
			
			log.warn("! Chronometer [ "+name+" ] Logger time limit triggered ! Step: " + stepName);
			this.doLog = true;
		}
			
		this.steps.add(new Pair<String, Float>(stepName, spentTime));

		this.lastStep = currentStep;
	
		return this;
	}

	public void end(String finalStepName) {
		
		this.step(finalStepName);

		if(!this.doLog) {
			return;
		}
		
		System.out.println(getLog());
		
	}
	
	public String getLog() {
		
		LinkedHashMap<String, List<Float>> stats = new LinkedHashMap<>();
		
		for(Pair<String, Float> pair : this.steps) {
			if(stats.get(pair.x) == null) {
				stats.put(pair.x, new ArrayList<>());
			}
			stats.get(pair.x).add(pair.y);
		}
		
		double globalTime = this.totalTime();
		
		StringBuilder sb = new StringBuilder();

		sb.append("Chrono Triggered : ").append(this.name).append("\n");
		sb.append("\tTotal time: ").append((int) Math.floor(globalTime)).append("ms\n");

		if (!stats.isEmpty()){
			sb.append("\n\tSteps\n");
			
			for(Entry<String, List<Float>> entry : stats.entrySet()) {
				int totalLength = entry.getValue().stream().mapToInt(x -> (int) Math.floor(x*1000)).sum();
				int ratio = (int) Math.round(totalLength / globalTime * 100);
				sb.append("\t  ").append(ratio*stats.size() > 500 ? ">>> " : "").append(entry.getKey()).append(" : ").append(totalLength).append("ms - ").append(ratio).append("%").append(entry.getValue().size() > 1 ? " (" + entry.getValue().size() + ")" : "").append("\n");
			}
		}

		if (!this.details.isEmpty()){
			sb.append("\n\tDetails\n");
			for(Entry<String, String> entry : this.details.entrySet()) {
				sb.append("\t  ").append(entry.getKey()).append(" : ").append(entry.getValue()).append("\n");
			}
		}
		
		
		
		return sb.toString();
	}

	public double totalTime() {
		return this.steps.stream().mapToDouble(x -> x.y*1000).sum();
	}
}
