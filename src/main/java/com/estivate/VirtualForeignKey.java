package com.estivate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;



@Target( ElementType.FIELD )
@Retention( RetentionPolicy.RUNTIME )
public @interface VirtualForeignKey {
	
	public Class entity();
	public String attribute() default "";

}