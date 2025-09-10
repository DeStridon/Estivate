package com.estivate.query;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


/*
 * Criterions not to be included : between
 * Input object : filtering
 * Output object : Mapping object
 */

public class QueryBuilder {
	
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Eq{
		public Class<?> entity() default void.class;
		public static final String attribute = null;
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface EqIfNotNull{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface EqNullable{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface NotEq{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface NotEqIfNotNull{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface NotEqNullable{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Lt{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface LtIfNotNull{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Lte{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface LteIfNotNull{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Gt{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface GtIfNotNull{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}

	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Gte{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface GteIfNotNull{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface Like{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface LikeIfNotNull{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface LikeContains{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface LikeContainsIfNotNull{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface LikeStartsWith{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface LikeStartsWithIfNotNull{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface In{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface InIfNotEmpty{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface InOrFalseIfEmpty{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface InOrNull{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface NotIn{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface NotInIfNotEmpty{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface NotInOrTrueIfEmpty{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface NotInOrNull{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface LikeInContains{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}

	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface LikeInStartsWith{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface LikeInEndsWith{
		public Class<?> entity() default void.class;
		public String attribute() default "";
	}


}
