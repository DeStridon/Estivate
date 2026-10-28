package com.estivate.util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import lombok.extern.slf4j.Slf4j;

@Data
@Builder
@AllArgsConstructor
@Slf4j
public class Chronometer {
	
	public static int defaultThreshold = 5000;

	/** Shared no-op instance when performance tracing is off. */
	private static final Chronometer INACTIVE = new Chronometer("inactive", false);
	
	
	final String name;
	
	private boolean active = true;
	/** Threshold in milliseconds before {@link #end(String)} logs. */
	private Integer timeThreshold = 5000;
	
	private long begin = System.nanoTime();
	private long lastStep = begin;
	
	/** Step durations in seconds (fractional; measured with {@link System#nanoTime()}). */
	private List<Pair<String, Float>> steps = new ArrayList<>();
	private Map<String, String> details = new LinkedHashMap<>();

	/**
	 * Returns a live chronometer when {@code active}, otherwise a shared no-op instance.
	 */
	public static Chronometer of(String name, boolean active) {
		return active ? new Chronometer(name, true) : INACTIVE;
	}
	
	
	public Chronometer(String name){
		this(name, true);
	}
	
	public Chronometer(String name, boolean activated){
		this.name = name;
		this.active = activated;
		
		this.begin = System.nanoTime();
		this.lastStep = begin;
		this.timeThreshold = defaultThreshold;
		if(!activated) {
			this.steps = Collections.emptyList();
			this.details = Collections.emptyMap();
		}
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
		if(!this.active) {
			return this;
		}
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
			this.begin = System.nanoTime();
			this.lastStep = this.begin;
		}
		return this;
	}

	/** Starts the next interval without recording a step (drops idle time between operations). */
	public Chronometer mark() {
		if(this.active) {
			this.lastStep = System.nanoTime();
		}
		return this;
	}

	
	public Chronometer step(String stepName){
		
		if(!this.active){
			return this;
		}

		long currentStep = System.nanoTime();
		float spentTime = (currentStep - this.lastStep) / 1_000_000_000f;
		
		if(this.timeThreshold != null && (currentStep - this.begin) / 1_000_000L > this.timeThreshold) {
			log.warn("! Chronometer [ "+name+" ] Logger time limit triggered ! Step: " + stepName);
		}
			
		this.steps.add(new Pair<String, Float>(stepName, spentTime));
		this.lastStep = currentStep;
	
		return this;
	}

	public Chronometer end(String finalStepName) {
		this.step(finalStepName);
		
		if(this.active && this.timeThreshold != null && (this.lastStep - this.begin) / 1_000_000L > this.timeThreshold) {
			log.info(getLog());
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
		
		double globalTimeMs = this.totalTime();
		boolean micros = globalTimeMs < 50;
		
		StringBuilder sb = new StringBuilder();

		sb.append("Chrono Triggered : ").append(this.name).append("\n");
		if(micros) {
			sb.append("\tTotal time: ").append(Math.round(globalTimeMs * 1000)).append("µs\n");
		}
		else {
			sb.append("\tTotal time: ").append((int) Math.floor(globalTimeMs)).append("ms\n");
		}

		if (!stats.isEmpty()){
			sb.append("\n\tSteps\n");
			
			for(Entry<String, List<Float>> entry : stats.entrySet()) {
				double stepMs = entry.getValue().stream().mapToDouble(x -> x * 1000d).sum();
				int ratio = globalTimeMs <= 0 ? 0 : (int) Math.round(stepMs / globalTimeMs * 100);
				int count = entry.getValue().size();
				sb.append("\t  ").append(ratio * stats.size() > 500 ? ">>> " : "").append(entry.getKey()).append(" : ");
				if(micros) {
					long stepMicros = Math.round(stepMs * 1000);
					sb.append(stepMicros).append("µs");
					if(count > 1) {
						sb.append(" avg ").append(stepMicros / count).append("µs");
					}
				}
				else {
					sb.append((int) Math.floor(stepMs)).append("ms");
				}
				sb.append(" - ").append(ratio).append("%");
				if(count > 1) {
					sb.append(" (").append(count).append(")");
				}
				sb.append("\n");
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

	/** Total recorded step time in milliseconds. */
	public double totalTime() {
		return this.steps.stream().mapToDouble(x -> x.y*1000).sum();
	}
}
