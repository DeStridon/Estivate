package com.estivate;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.index.IndexColumn;
import com.estivate.query.CreateQuery.Index;

public abstract class NameMapper {

	// Custom database class name
	// database mapping : maps names in query to find table
	public abstract String mapDatabaseClass(Class<?> c);
	public abstract String mapDatabaseField(String field);
	
	// entity mapping : maps names in query to aliases
	public abstract String mapEntityClass(Class<?> c);
	public abstract String mapEntityField(String field);

	
	
	public String toTableName(Class<?> c) {
		javax.persistence.Table javaxPersistenceTableAnnotation = c.getDeclaredAnnotation(javax.persistence.Table.class);
		jakarta.persistence.Table jakartaPersistenceTableAnnotation = c.getDeclaredAnnotation(jakarta.persistence.Table.class);
		if(javaxPersistenceTableAnnotation != null && StringUtils.isNotBlank(javaxPersistenceTableAnnotation.name())) {
			return javaxPersistenceTableAnnotation.name();
		}
		else if(jakartaPersistenceTableAnnotation != null && StringUtils.isNotBlank(jakartaPersistenceTableAnnotation.name())) {
			return jakartaPersistenceTableAnnotation.name();
		}
		return mapDatabaseClass(c);
	}
	
	public String toTableName(Entity<?> e) {
		return e.alias != null ? e.alias : toTableName(e.entity);
	}
	

	public String toTableNameAttribute(Class<?> c, String field) 	{ 
		// Case attribute alias
		if(c == null) {
			return field;
		}
		return mapDatabaseClass(c)+"."+mapDatabaseField(field); 
	}
	
	public String toTableNameAttribute(Entity<?> e, String field) 	{
		// Case attribute alias
		if(e == null || e.entity == null) {
			return field;
		}
		return toTableName(e)+"."+mapDatabaseField(field); 
	}


	
	public String toEntityName(Entity<?> e) {
		return e.alias != null ? e.alias : mapEntityClass(e.entity);
	}
	

	public String toEntityNameAttribute(Class<?> c, String field) 	{ return mapEntityClass(c) + "." + mapEntityField(field); }
	public String toEntityNameAttribute(Entity<?> e, String field) { return toEntityName(e) + "." + mapEntityField(field); }

	
	
//	public String mapIndex(TableIndex compositeIndex) {
//		if(StringUtils.isNotBlank(compositeIndex.name())) {
//			return compositeIndex.name();
//		}
//		return Arrays.asList(compositeIndex.columns()).stream().map(x -> x.value()).collect(Collectors.joining("_"));
//	}
//	
//	public String mapIndex(Index index) {
//		if(StringUtils.isNotBlank(index.getName())) {
//			return index.getName();
//		}
//		return index.getColumns().stream().map(x -> x.getColumnName()).collect(Collectors.joining("_"));
//	}

	public String mapIndex(String indexName, IndexType type, List<IndexColumn> columns) {
		if(StringUtils.isNotBlank(indexName)) {
			return indexName;
		}
		return type.name().toLowerCase() + "_" + columns.stream().map(x -> x.getColumnName()).collect(Collectors.joining("_"));
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
