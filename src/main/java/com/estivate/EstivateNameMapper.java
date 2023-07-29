package com.estivate;

public interface EstivateNameMapper {
	
	public String mapEntity(Class c);
	public String mapAttribute(String attributeName);
	public String mapEntityAttribute(Class entity, String attributeName);
	
	
	public static class DefaultNameMapper implements EstivateNameMapper{
		public String mapEntity(Class c) {
			return c.getSimpleName();
		}
		
		public String mapAttribute(String attributeName) {
			return attributeName;
		}
	
		public String mapEntityAttribute(Class entity, String attributeName) {
			return mapEntity(entity)+"."+attributeName;
		}
	}
	
	public static class UppercaseNameMapper implements EstivateNameMapper{
		public String mapEntity(Class c) {
			return c.getSimpleName().toUpperCase();
		}
		
		public String mapAttribute(String attributeName) {
			return attributeName.toUpperCase();
		}
	
		public String mapEntityAttribute(Class entity, String attributeName) {
			return mapEntity(entity)+"."+attributeName.toUpperCase();
		}
		
	}


	
	

}
