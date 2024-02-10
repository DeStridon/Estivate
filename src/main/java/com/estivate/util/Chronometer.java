package com.estivate.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@Slf4j
public class Chronometer {
	
	public static int defaultThreshold = 5000;
	
	
	final String name;
	
	private boolean active = true;
	private Integer timeThreshold = 5000;
	
	private long begin = System.currentTimeMillis();
	private long lastStep = begin;
	
	private List<Pair<String, Float>> steps = new ArrayList<>();
	private Map<String, String> details = new LinkedHashMap<>();
	
	
	public Chronometer(String name){
		this(name, true);
	}
	
	public Chronometer(String name, boolean activated){
		this.name = name;
		this.active = activated;
		
		this.begin = System.currentTimeMillis();
		this.lastStep = begin;
		this.timeThreshold = defaultThreshold;
	}
	
	public Chronometer active(boolean active) {
		this.active = active;
		return this;
	}
	
	public Chronometer timeThreshold(Integer timeThreshold) {
		this.timeThreshold = timeThreshold;
		return this;
	}
	
	
	public Chronometer addDetail(String key, String value) {
		this.details.put(key, value);
		return this;
	}
	
	public Chronometer addDetail(String key, Object value) {
		if(value != null) {
			return addDetail(key, value.toString());
		}
		return this;
	}

	
	public Chronometer reset(){
		if(this.active){
			this.begin = System.currentTimeMillis();
		}
		return this;
	}

	
	public Chronometer step(String stepName){
		
		if(!this.active){
			return this;
		}

		long currentStep = System.currentTimeMillis();
		float spentTime = (currentStep - this.lastStep) / 1000f;
		
		if(this.timeThreshold != null && (currentStep - this.begin) > this.timeThreshold) {
			log.warn("! Chronometer [ "+name+" ] Logger time limit triggered ! Step: " + stepName);
		}
			
		this.steps.add(new Pair<String, Float>(stepName, spentTime));
		this.lastStep = currentStep;
	
		return this;
	}

	public Chronometer end(String finalStepName) {
		this.step(finalStepName);
		
		if(this.timeThreshold != null && (this.lastStep - this.begin) > this.timeThreshold) {
			System.out.println(getLog());
		}
		return this;
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
