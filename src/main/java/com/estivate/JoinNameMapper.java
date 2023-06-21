package com.estivate;

public interface JoinNameMapper {
	
	public String mapEntity(Class c);
	public String mapAttribute(String attributeName);
	public String mapEntityAttribute(Class entity, String attributeName);
	
	
	public static class DefaultNameMapper implements JoinNameMapper{
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


	
	

}
