package com.estivate.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Index {
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Repeatable(TableCompositeIndex.class)
	public @interface CompositeIndex {
		String name() default "";
		ColumnIndex[] columns() default {};	
	}
	
	public static @interface ColumnIndex{
		String value();
		int length() default 0;
	}
	
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TableCompositeIndex {
		CompositeIndex[] value();
	}

}
