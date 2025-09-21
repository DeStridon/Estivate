package com.estivate.result;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

public class ResultMapping {

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Attribute {
		
		public Class<?> entity();
		public String attribute();

	}

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Column {
		public String column();
	}
	
	//TODO : add for function
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface CountAttribute {
		
		public Class<?> entity();
		public String attribute();

	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface SumAttribute {
		
		public Class<?> entity();
		public String attribute();

	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface MinAttribute {
		
		public Class<?> entity();
		public String attribute();

	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface MaxAttribute {
		
		public Class<?> entity();
		public String attribute();

	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface AvgAttribute {
		
		public Class<?> entity();
		public String attribute();

	}
	
}
