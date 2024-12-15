package com.estivate.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Index {
	
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TableIndexes {
		CompositeIndex[] value();
	}
	
	@Repeatable(TableIndexes.class)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface CompositeIndex {
		String name() default "";
		ColumnIndex[] columns() default {};	
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface ColumnIndex{
		String value();
		int length() default 0;
	}
	
	

}
