package com.estivate.test;

import java.util.stream.Collectors;

import org.h2.tools.Server;

import com.estivate.NameMapper;
import com.estivate.context.Context;
import com.estivate.context.H2Context;
import com.estivate.index.IndexDiff;
import com.estivate.test.entities.ChildEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.OrderLineEntity;
import com.estivate.test.entities.ParentEntity;
import com.estivate.test.entities.ProductEntity;

import lombok.SneakyThrows;

public class DatabaseGenerator {

	private static Context context = null;

	
	@SneakyThrows
	static Context getContext() {
		
		if(context == null) {
			
			context = new H2Context(DatasourceGenerator.datasource());
			Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8083").start();
			
			context.nameMapper = new TestNameMapper();

			context.create(ProductEntity.class);
			IndexDiff productIndexDiff = new IndexDiff(context, ProductEntity.class);
			productIndexDiff.addUnimplemented();

			context.create(OrderLineEntity.class);
			IndexDiff orderLineIndexDiff = new IndexDiff(context, OrderLineEntity.class);
			orderLineIndexDiff.addUnimplemented();

			context.create(OrderEntity.class);
			IndexDiff orderIndexDiff = new IndexDiff(context, OrderEntity.class);
			orderIndexDiff.addUnimplemented();

			context.create(CustomerEntity.class);
			IndexDiff customerIndexDiff = new IndexDiff(context, CustomerEntity.class);
			customerIndexDiff.addUnimplemented();
			

			

			
			context.create(ParentEntity.class);
			IndexDiff parentIndexDiff = new IndexDiff(context, ParentEntity.class);
			parentIndexDiff.addUnimplemented();
			
			context.create(ChildEntity.class);
			IndexDiff childIndexDiff = new IndexDiff(context, ChildEntity.class);
			childIndexDiff.addUnimplemented();
		
			
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
