package com.estivate.test;

import java.util.stream.Collectors;

import org.h2.tools.Server;

import com.estivate.NameMapper;
import com.estivate.context.Context;
import com.estivate.context.H2Context;
import com.estivate.index.IndexDiff;
import com.estivate.test.entities.ChildEntity;
import com.estivate.test.entities.ParentEntity;

import lombok.SneakyThrows;

public class DatabaseGenerator {

	private static Context context = null;
	
	@SneakyThrows
	static Context getContext() {
		
		if(context == null) {
			
			//context = new Context(DriverManager.getConnection("jdbc:h2:mem:test"));
			context = new H2Context(DatasourceGenerator.datasource());
			Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8083").start();
			
			context.nameMapper = new TestNameMapper();
			
			context.create(ParentEntity.class);
			IndexDiff parentIndexDiff = new IndexDiff(context, ParentEntity.class);
			parentIndexDiff.apply();
			
			context.create(ChildEntity.class);
			IndexDiff childIndexDiff = new IndexDiff(context, ChildEntity.class);
			childIndexDiff.apply();
		
			
			System.out.println(context.showTables().stream().collect(Collectors.joining(", ")));
			System.out.println();
			
		}
		
		
		
		return context;
				
	}
	
	public static ParentEntity createRandomParent() {
		
		ParentEntity parentEntity = new ParentEntity();
		parentEntity.setHomeId((long) randomInt(1, 100));
		parentEntity.setName("Random Task "+randomInt(1, 1000));
	
		return parentEntity;

	}
	
	public static class TestNameMapper extends NameMapper{
		public String mapEntityClass(Class<?> c) { return c.getSimpleName().toUpperCase()+"_E";}
		public String mapEntityField(String field) { return field.toUpperCase()+"_E";  }
		public String mapDatabaseClass(Class<?> c) { return c.getSimpleName().toUpperCase()+"_D"; }
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
