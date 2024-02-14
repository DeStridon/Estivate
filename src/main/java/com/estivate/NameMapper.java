package com.estivate;

import com.estivate.query.Query.Entity;

public abstract class NameMapper {
	
	public abstract String mapEntityClass(Class c);
	public abstract String mapEntityField(String field);
	public abstract String mapDatabaseClass(Class c);
	public abstract String mapDatabaseField(String field);

	public String mapEntity(Class c, String field) { return mapEntityClass(c)+"."+mapEntityField(field); }
	public String mapEntity(Entity e, String field) { return e.getName() + "." + mapDatabaseField(field); }
	public String mapDatabase(Class c, String field) { return mapDatabaseClass(c)+"."+mapDatabaseField(field); }
	public String mapDatabase(Entity c, String field) { return mapDatabaseClass(c.getClass())+"."+mapDatabaseField(field); }
	

	
	public static class DefaultNameMapper extends NameMapper{
		public String mapEntityClass(Class c) { return c.getSimpleName(); }
		public String mapEntityField(String field) { return field; }
		public String mapDatabaseClass(Class c) { return c.getSimpleName(); }
		public String mapDatabaseField(String field) { return field; }
	}
	
	public static class UppercaseNameMapper extends NameMapper{
		public String mapEntityClass(Class c) { return c.getSimpleName().toUpperCase(); }
		public String mapEntityField(String field) { return field.toUpperCase(); }
		public String mapDatabaseClass(Class c) { return c.getSimpleName().toUpperCase(); }
		public String mapDatabaseField(String field) { return field.toUpperCase(); }
	}


	
	

}
