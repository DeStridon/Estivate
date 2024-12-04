package com.estivate.entity;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.estivate.entity.CompositeIndex.ColumnIndex;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.TYPE)
public @interface CompositeIndex {

	String name() default "";
	
	ColumnIndex[] columns() default {};
	
	public static @interface ColumnIndex{
		String name();
		int length() default 0;
	}
	
}
