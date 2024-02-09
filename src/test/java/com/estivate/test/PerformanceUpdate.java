package com.estivate.test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.junit.jupiter.api.Test;

import com.estivate.Result;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.util.Chronometer;

public class PerformanceUpdate {
	
	@Test
	public void testPerf() {
		
		Map<String, String> map = new HashMap<>();
		map.put("SegmentEntity.projectId", "1");
		map.put("SegmentEntity.sourceFragmentId", "2");
		map.put("SegmentEntity.targetFragmentId", "45");
		map.put("SegmentEntity.taskId", "555");
		map.put("SegmentEntity.sourceContent", "blablablablablabla");
		map.put("SegmentEntity.targetContent", "pihiphiphpih");
		map.put("SegmentEntity.sourceLanguage", "en_GB");
		map.put("SegmentEntity.targetLanguage", "fr_FR");
		map.put("SegmentEntity.macroStatus", "4");
		map.put("SegmentEntity.microStatus", "2");
		
		List<Result> results = new ArrayList<>();
		for(int i = 0; i < 25000; i++) {
			results.add(new Result(null, map));
		}

		Chronometer chrono = Chronometer.createTimeTriggeredChronometer("bla", 100);
		chrono.start();
		
		List<SegmentEntity> segments1 = results.stream().map(x -> x.mapAs(SegmentEntity.class)).collect(Collectors.toList());
		
		chrono.end("end");
		
	}

}
