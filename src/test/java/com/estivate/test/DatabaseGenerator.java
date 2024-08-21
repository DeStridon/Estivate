package com.estivate.test;

import java.sql.DriverManager;
import java.util.stream.Collectors;

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
			Query.nameMapper = new TestNameMapper();
			
			context.create(TaskEntity.class);
			context.create(SegmentEntity.class);
			context.create(FragmentEntity.class);
			context.create(TaskHistoryEntity.class);	
			
			System.out.println(context.showTables().stream().collect(Collectors.joining(", ")));
			System.out.println();
			
		}
		
		
		return context;
				
	}
	
	public static class TestNameMapper extends NameMapper{
		public String mapEntityClass(Class c) { return c.getSimpleName().toUpperCase()+"_E";}
		public String mapEntityField(String field) { return field.toUpperCase()+"_E";  }
		public String mapDatabaseClass(Class c) { return c.getSimpleName().toUpperCase()+"_D"; }
		public String mapDatabaseField(String field) { return field.toUpperCase()+"_D"; }
	}
	
}
