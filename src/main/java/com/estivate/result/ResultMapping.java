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
		public String alias() default "";

	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Count {
		
		public Class<?> entity();
		public String attribute();
		public String alias();

	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Sum {
		
		public Class<?> entity();
		public String attribute();
		public String alias();

	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Min {
		
		public Class<?> entity();
		public String attribute();
		public String alias();

	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Max {
		
		public Class<?> entity();
		public String attribute();
		public String alias();

	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Avg {
		
		public Class<?> entity();
		public String attribute();
		public String alias();

	}

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Function {
		
		public Class<?> entity();
		public String attribute();
		public String alias();
		public String functionPrefix();
		public String functionSuffix();
	}
	
	
}
