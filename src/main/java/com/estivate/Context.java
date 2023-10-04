package com.estivate;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Transient;

import com.estivate.entity.CachedEntity;
import com.estivate.query.Query;
import com.estivate.util.StringPipe;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Context {
	
	final public Connection connection;
	
	public Context(Connection connection) {
		this.connection = connection;
	}
	
	@SneakyThrows
	public <U> U uniqueResult(Query query, Class<U> clazz) {
		
		
		Statement statement = Statement.toStatement(connection, query);
		
		statement.execute();
        
        ResultSet resultSet = statement.getResultSet();
    	ResultSetMetaData metadata = resultSet.getMetaData();
        
        if(resultSet.next()) {

        	Map<String, String> map = new HashMap<>();
        	for(int i = 1; i <= metadata.getColumnCount(); i++) {
        		map.put(metadata.getColumnLabel(i), resultSet.getString(i));
        	}

        	Result result = new Result(query, map);
        	U object = result.mapAs(clazz);
        	
        	return object;
        }
        return null;
	}
	
	
	@SneakyThrows
	public List<Result> list(Query joinQuery){
		
		Statement statement = Statement.toStatement(connection, joinQuery);
		
        statement.execute();
        
        ResultSet resultSet = statement.getResultSet();
        
        
        List<Result> results = new ArrayList<>();
        
        while(resultSet.next()) {
        	Map<String, String> map = new HashMap<>();
        	ResultSetMetaData metadata = resultSet.getMetaData();

        	for(int i = 1; i <= metadata.getColumnCount(); i++) {
        		map.put(metadata.getColumnLabel(i), resultSet.getString(i));
        	}
        	Result result = new Result(joinQuery, map);
        	results.add(result);
        }
        
        return results;
		
	}
	
	
	public <U> List<U> listAs(Query joinQuery, Class<U> clazz) {
		List<Result> results = list(joinQuery);
		List<U> output = new ArrayList<>();
		for(Result result : results) {
			output.add(result.mapAs(clazz));
		}
		return output;
	}
	
	@SneakyThrows
	public <U> U saveOrUpdate(U object) {
		
		Field idField = getIdField(object.getClass());
		idField.setAccessible(true);
		if(idField != null && idField.getLong(object) == 0L) {
			insert(object);
		}
		else {
			update(object);
		}
		
		if(object instanceof CachedEntity) {
			((CachedEntity) object).saveState();
		}
		
		return object;
	}
	
	@SneakyThrows
	private <U> U insert(U object) {
		

		List<String> fieldValueList = new ArrayList<>();

		Statement statement = new Statement(connection)
				.appendQuery("INSERT INTO ")
				.appendQuery(Query.nameMapper.mapEntity(object.getClass()));

		
		for(Field field : getAllFields(object.getClass())) {
			field.setAccessible(true);
			// Skip Id, will be auto generated by db
			if(field.isAnnotationPresent(Id.class)) {
				continue;
			}
			else if(field.getType() == org.slf4j.Logger.class) {
				continue;
			}
			
			try {
				
				if(field.get(object) == null) {
					continue;
				}
				
				fieldValueList.add(field.getName());
				statement.appendValue(object.getClass(), field.getName(), field.get(object));
				//EstivateUtil.compileAttribute(entity.getClass(), field.getName(), field.get(entity)));
				
			}
			catch(Exception e) {
				log.error("Cannot map field "+field.getName(), e);
			}
			
		}
				
		statement.appendQuery("(")
				.appendQuery(fieldValueList.stream().collect(Collectors.joining(", ")))
				.appendQuery(") VALUES (")
				.appendQuery(fieldValueList.stream().map(x -> "?").collect(Collectors.joining(", ")))
				.appendQuery(")");
		
		
				
		statement.execute();
		
		ResultSet rs = statement.getGeneratedKeys();
		if (rs.next()) {
			Field field = getIdField(object.getClass());
			field.setAccessible(true);
			field.setLong(object, rs.getLong(1));
		}
		else {
			return null;
		}
		
		return object;
	}

	
	@SneakyThrows
	public boolean create(Class<? extends Object> entityClass) {
		
		List<String> fields = new ArrayList<>();
		for(Field field : getAllFields(entityClass)) {
			StringPipe fieldCreation = new StringPipe();
			fieldCreation.separator(" ");
			
			fieldCreation.append(field.getName());
			
			Class returnClass = field.getType();
			
			if(field.getDeclaredAnnotation(Convert.class) != null) {
				returnClass = String.class;
			}
			
			if(returnClass.isEnum()) {

				if(field.getDeclaredAnnotation(Enumerated.class) != null && field.getDeclaredAnnotation(Enumerated.class).value() != null && field.getDeclaredAnnotation(Enumerated.class).value() == EnumType.STRING) {
					returnClass = String.class;
				}
				else {
					returnClass = Integer.class;
				}
			}
			
			if(returnClass == org.slf4j.Logger.class || returnClass == CachedEntity.class) {
				continue;
			}
			else if(returnClass == Integer.class || returnClass == Integer.TYPE || returnClass == Long.class || returnClass == Long.TYPE) {
				fieldCreation.append("INT");
			}
			else if(returnClass == Float.class || returnClass == Float.TYPE) {
				fieldCreation.append("FLOAT");
			}
			else if(returnClass == Double.class || returnClass == Double.TYPE) {
				fieldCreation.append("DOUBLE");
			}
			else if(returnClass == String.class) {
				fieldCreation.append("VARCHAR");
			}
			else if(returnClass == Boolean.class || returnClass == boolean.class) {
				fieldCreation.append("BOOL");
			}
			else if(returnClass == java.util.Date.class || returnClass == java.sql.Date.class) {
				fieldCreation.append("DATE");
			}
			else {
				throw new RuntimeException("Cannot map type "+field.getType());
			}
			
			if(field.isAnnotationPresent(Id.class)) {
				fieldCreation.append("PRIMARY KEY");
			}
			
			if(field.getDeclaredAnnotation(GeneratedValue.class) != null) {
				GeneratedValue generatedValue = field.getDeclaredAnnotation(GeneratedValue.class);
				if(generatedValue.strategy() == GenerationType.IDENTITY) {
					fieldCreation.append("AUTO_INCREMENT");
				}
			}
			
			
			fields.add(fieldCreation.toString());
		}
		
		String result = "CREATE TABLE "+entityClass.getSimpleName()+" ("+fields.stream().collect(Collectors.joining(", "))+")";
		
		PreparedStatement statement = connection.prepareStatement(result);
		
		return statement.execute();
		
		
	}
	
	private Field getIdField(Class<? extends Object> objectClass) {
		if(objectClass == null) {
			return null;
		}
		for(Field field : getAllFields(objectClass)) {
			
			// Skip Id, will be auto generated by db
			if(field.isAnnotationPresent(Id.class)) {
				return field;
			}
		}
		return null;
	}
	
	List<Field> getAllFields(Class<? extends Object> objectClass){
		
		if(objectClass == null) {
			return new ArrayList<>();
		}
		List<Field> fields = getAllFields(objectClass.getSuperclass());
		for(Field field : objectClass.getDeclaredFields()) {
			
			if(field.isAnnotationPresent(Transient.class)) {
				continue;
			}
			fields.add(field);
			
		}
		
		return fields;
	}
	
	@SneakyThrows
	public void update(Object entity) {

		List<Field> updatedFields = getAllFields(entity.getClass());

		if(entity instanceof CachedEntity) {
			updatedFields = ((CachedEntity) entity).updatedFields();
		}
		
		// No change to entity
		if(updatedFields.isEmpty()) {
			return;
		}
		
		Field idField = getFieldWithAnnotation(entity.getClass(), Id.class);
		
		if(idField == null) {
			log.error("No @Id field on class "+entity.getClass());
			return;
		}
		
		idField.setAccessible(true);
		Long id = idField.getLong(entity);
		if(id == null || id == 0) {
			log.error("Null or 0 value id for entity, no update possible");
		}
		
		// 1. Create query
		Statement statement = new Statement(connection)
				.appendQuery("UPDATE ")
				.appendQuery(Query.nameMapper.mapEntity(entity.getClass()))
				.appendQuery(" SET ");
				
				//String query = "UPDATE "+EstivateQuery.nameMapper.mapEntity(entity.getClass())+" SET ";

		// 2. List updated fields
		statement.appendQuery(updatedFields.stream().map(x-> Query.nameMapper.mapEntityAttribute(entity.getClass(), x.getName()) + " = ?").collect(Collectors.joining(", ")));
		
		for(Field field : updatedFields) {
			statement.appendValue(entity.getClass(), field.getName(), field.get(entity));
		}
		
		
		statement.appendQuery(" WHERE "+Query.nameMapper.mapAttribute(idField.getName())+" = ?");
		statement.appendValue(entity.getClass(), idField.getName(), idField.getLong(entity));
		
		
		
		boolean check = statement.execute();
		
		System.out.println(check);
		
	}

	
	public Field getFieldWithAnnotation(Class<? extends Object> entityClass, Class<? extends Annotation> annotationClass) {
		
		for(Field field : getAllFields(entityClass)) {
			if(field.isAnnotationPresent(annotationClass)) {
				return field;
			}
		}
		
		return null;
		
	}


}
