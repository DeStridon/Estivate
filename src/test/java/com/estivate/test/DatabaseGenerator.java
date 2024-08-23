package com.estivate.test;

import java.sql.DriverManager;
import java.util.Random;
import java.util.stream.Collectors;

import com.estivate.Context;
import com.estivate.NameMapper;
import com.estivate.query.Query;
import com.estivate.test.entities.FragmentEntity;
import com.estivate.test.entities.SegmentEntity;
import com.estivate.test.entities.TaskEntity;
import com.estivate.test.entities.TaskHistoryEntity;
import com.estivate.test.entities.misc.Language;

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
	
	public static TaskEntity createRandomTask() {
		
		TaskEntity taskEntity = new TaskEntity();
		taskEntity.setProjectId((long) randomInt(1, 100));
		taskEntity.setName("Random Task "+randomInt(1, 1000));
		taskEntity.setSourceLanguage(randomEnum(Language.class));
		taskEntity.setSourceLanguage(randomEnum(Language.class));
	
		return taskEntity;

	}
	
	public static class TestNameMapper extends NameMapper{
		public String mapEntityClass(Class c) { return c.getSimpleName().toUpperCase()+"_E";}
		public String mapEntityField(String field) { return field.toUpperCase()+"_E";  }
		public String mapDatabaseClass(Class c) { return c.getSimpleName().toUpperCase()+"_D"; }
		public String mapDatabaseField(String field) { return field.toUpperCase()+"_D"; }
	}
	
	public static int randomInt(int from, int to) {
		return (int) Math.floor(Math.random()*(to-from)) + from;
	}
	
	public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = randomInt(0, clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
	
	
	
}
