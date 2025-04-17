package com.estivate;

import java.util.Arrays;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.estivate.index.Annotations.TableIndex;
import com.estivate.query.Query.Entity;

public abstract class NameMapper {
	
	// database mapping : maps names in query to find table
	public abstract String mapDatabaseClass(Class<?> c);
	public String mapDatabaseClass(Entity<?> e) {
		return e.alias != null ? e.alias : mapDatabaseClass(e.entity);
	}
	
	public abstract String mapDatabaseField(String field);

	public String mapDatabase(Class<?> c, String field) 	{ 
		// Case attribute alias
		if(c == null) {
			return field;
		}
		return mapDatabaseClass(c)+"."+mapDatabaseField(field); 
	}
	public String mapDatabase(Entity<?> e, String field) 	{
		// Case attribute alias
		if(e == null) {
			return field;
		}
		return mapDatabaseClass(e)+"."+mapDatabaseField(field); 
	}


	// entity mapping : maps names in query to aliases
	public abstract String mapEntityClass(Class<?> c);
	public String mapEntityClass(Entity<?> e) {
		return e.alias != null ? e.alias : mapEntityClass(e.entity);
	}
	
	public abstract String mapEntityField(String field);

	public String mapEntity(Class<?> c, String field) 	{ return mapEntityClass(c) + "." + mapEntityField(field); }
	public String mapEntity(Entity<?> e, String field) { return mapEntityClass(e) + "." + mapEntityField(field); }

	
	
	public String mapIndex(TableIndex compositeIndex) {
		if(StringUtils.isNotBlank(compositeIndex.name())) {
			return compositeIndex.name();
		}
		return Arrays.asList(compositeIndex.columns()).stream().map(x -> x.value()).collect(Collectors.joining("_"));
	}

	
	public static class DefaultNameMapper extends NameMapper{
		public String mapEntityClass(Class<?> c) { return c.getSimpleName(); }
		public String mapEntityField(String field) { return field; }
		public String mapDatabaseClass(Class<?> c) { return c.getSimpleName(); }
		public String mapDatabaseField(String field) { return field; }
	}
	
	public static class UppercaseNameMapper extends NameMapper{
		public String mapEntityClass(Class<?> c) { return c.getSimpleName().toUpperCase(); }
		public String mapEntityField(String field) { return field.toUpperCase(); }
		public String mapDatabaseClass(Class<?> c) { return c.getSimpleName().toUpperCase(); }
		public String mapDatabaseField(String field) { return field.toUpperCase(); }
	}
	
	
	

}
