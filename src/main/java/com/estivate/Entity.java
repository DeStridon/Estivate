package com.estivate;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.estivate.query.SelectQuery;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;

@EqualsAndHashCode
@AllArgsConstructor
public class Entity<U> {
	
	public final Class<U> entity;
	public final String alias;
	
	public Entity(Class<U> entity) {
		this(entity, null);
	}
	
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("Entity[");
		if(alias != null) {
			sb.append("alias = ").append(alias).append(", ");
		}
		sb.append("entity = ").append(entity.getSimpleName()).append("]");
		
		return sb.toString();
	}
	
	
	
	public static class SubQueryEntity<T> extends Entity<T> {
		
		public SelectQuery<T> query;

		public SubQueryEntity(SelectQuery<T> query, String alias){
			super(query.getEntity().entity, alias);
			this.query = query;
		}

	}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface InsertDate {}
	
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.FIELD)
	public @interface UpdateDate {}
	
	@Target( ElementType.FIELD )
	@Retention( RetentionPolicy.RUNTIME )
	public @interface VirtualForeignKey {
		
		public Class<?> entity();
		public String attribute() default "";

	}

}