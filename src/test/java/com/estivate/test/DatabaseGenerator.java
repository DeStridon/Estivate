package com.estivate.test;

import java.sql.DriverManager;

import com.estivate.Context;
import com.estivate.NameMapper;
import com.estivate.query.Query;
import com.estivate.test.entities.FragmentEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;
import com.estivate.test.entities.TaskHistoryEntity;

import lombok.SneakyThrows;

public class DatabaseGenerator {

	private static Context context = null;
	
	@SneakyThrows
	static Context getContext() {
		
		if(context == null) {
		
			context = new Context(DriverManager.getConnection("jdbc:h2:mem:test"));
			
			context.create(TaskEntity.class);
			context.create(SegmentEntity.class);
			context.create(FragmentEntity.class);
			context.create(TaskHistoryEntity.class);	
			
			Query.nameMapper = new NameMapper.UppercaseNameMapper();
		
		}
		
		
		return context;
				
	}
	
}
