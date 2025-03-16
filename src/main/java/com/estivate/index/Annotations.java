package com.estivate.index;

import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class Annotations {
	
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TableIndexes {
		
		TableIndex[] value();
	}
	
	@Repeatable(TableIndexes.class)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TableIndex {
		String name() default "";
		IndexType type() default IndexType.DEFAULT;
		IndexColumn[] columns() default {};	
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface IndexColumn{
		String value();
		int length() default 0;
	}

	public static enum IndexType{ 
		DEFAULT,
		UNIQUE,
		PRIMARY
	}	
	

}
