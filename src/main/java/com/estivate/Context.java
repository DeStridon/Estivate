package com.estivate;


import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PostPersist;
import javax.persistence.PostUpdate;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import com.estivate.entity.CachedEntity;
import com.estivate.entity.InsertDate;
import com.estivate.entity.UpdateDate;
import com.estivate.query.Query;
import com.estivate.util.Chronometer;
import com.estivate.util.FieldUtils;
import com.estivate.util.StringPipe;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Context {
	
	final public Connection connection;
	
	public boolean tracePerformances = false;
	
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
		Chronometer chronometer = new Chronometer("list");
		chronometer.timeThreshold(100);
		
		Statement statement = Statement.toStatement(connection, joinQuery);
		chronometer.step("statement creation");
		
        statement.execute();
        chronometer.step("execute");
        
        ResultSet resultSet = statement.getResultSet();
        chronometer.step("get resultset");
        
        
        ResultSetMetaData metadata = resultSet.getMetaData();
        
        List<Result> results = new ArrayList<>();
        
        
        while(resultSet.next()) {
        	chronometer.step("resultset next");
            
        	Map<String, String> map = new HashMap<>();
            
        	for(int i = 1; i <= metadata.getColumnCount(); i++) { 
        		map.put(metadata.getColumnLabel(i), resultSet.getString(i));
        	}
        	chronometer.step("insert in map");
            
        	Result result = new Result(joinQuery, map);
        	results.add(result);
        	chronometer.step("create result");
            
        }
        chronometer.end("end");
        
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
	
	public <U> List<U> listAs2(Query joinQuery, Class<U> clazz) {
		
		List<Result> results = list(joinQuery);
		Mapper<U> mapper = new Mapper<>(clazz);
		
		List<U> output = new ArrayList<>();
		for(Result result : results) {
			output.add(mapper.map(result.getColumns()));
		}
		System.out.println(mapper.getStats());
		return output;
	}
	
	@SneakyThrows
	public <U> List<U> listAsNew(Query joinQuery, Class<U> clazz) {
		
		Chronometer chronometer = new Chronometer("list");
		chronometer.timeThreshold(100);
		chronometer.active(tracePerformances);
		
		Statement statement = Statement.toStatement(connection, joinQuery);
		chronometer.step("statement creation");
		
        statement.execute();
        chronometer.step("execute");
        
        ResultSet resultSet = statement.getResultSet();
        chronometer.step("get resultset");
        
        Mapper<U> mapper = new Mapper<>(clazz);
        chronometer.step("create mapper");
        
        ResultSetMetaData metadata = resultSet.getMetaData();
        mapper.attachMetadata(metadata);
        chronometer.step("get and attach metadata");
        
        List<String[]> rows = new ArrayList<>();
        
        while(resultSet.next()) {
        	chronometer.step("resultset next");
            
        	String[] values = new String[metadata.getColumnCount()];
            
        	for(int i = 0; i < metadata.getColumnCount(); i++) { 
        		values[i] = resultSet.getString(i+1);
        	}
        	rows.add(values);
            
        	chronometer.step("create row");
        }
        
		List<U> output = new ArrayList<>();
		for(String[] row : rows) {
			output.add(mapper.map(row));
		}
		chronometer.end("map rows");
		
		if(tracePerformances) {
			System.out.println(mapper.getStats());
		}

		return output;
	}
	
	public <U> List<U> listAsParallel(Query joinQuery, Class<U> clazz) {
		List<Result> results = list(joinQuery);
		List<U> output = results.stream().parallel().map(x -> x.mapAs(clazz)).collect(Collectors.toList());
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
		
		for(Method method : FieldUtils.findMethodWithAnnotation(object.getClass(), PrePersist.class)) {
			method.invoke(object);
		}

		List<String> fieldValueList = new ArrayList<>();

		Statement statement = new Statement(connection)
				.appendQuery("INSERT INTO ")
				.appendQuery(Query.nameMapper.mapEntityClass(object.getClass()));

		
		for(Field field : FieldUtils.getEntityFields(object.getClass())) {
			field.setAccessible(true);
			// Skip Id, will be auto generated by db
			if(field.isAnnotationPresent(Id.class)) {
				continue;
			}
			else if(field.isAnnotationPresent(InsertDate.class) && (field.getType() == java.util.Date.class || field.getType() == java.sql.Date.class)) {
				field.set(object, new Date());
			}
			
			try {
				
				if(field.get(object) == null) {
					continue;
				}
				
				fieldValueList.add(Query.nameMapper.mapEntityField(field.getName()));
				statement.appendValue(object.getClass(), field.getName(), field.get(object));
				
			}
			catch(Exception e) {
				log.error("Cannot map field "+field.getName(), e);
				throw e;
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
		
		for(Method method : FieldUtils.findMethodWithAnnotation(object.getClass(), PostPersist.class)) {
			method.invoke(object);
		}
		
		return object;
	}

	
	@SneakyThrows
	public boolean create(Class<? extends Object> entityClass) {
		
		List<String> fields = new ArrayList<>();
		for(Field field : FieldUtils.getEntityFields(entityClass)) {
			StringPipe fieldCreation = new StringPipe();
			fieldCreation.separator(" ");
			
			fieldCreation.append(Query.nameMapper.mapDatabaseField(field.getName()));
			
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
				fieldCreation.append("DATETIME");
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
		
		
		String result = "CREATE TABLE "+Query.nameMapper.mapDatabaseClass(entityClass)+" ("+fields.stream().collect(Collectors.joining(", "))+")";
		
		PreparedStatement statement = connection.prepareStatement(result);
		
		return statement.execute();
		
		
	}
	
	public String queryAsString(Query query) {
		return Statement.toStatement(connection, query).query();
	}
	
	private Field getIdField(Class<? extends Object> objectClass) {
		if(objectClass == null) {
			return null;
		}
		for(Field field : FieldUtils.getEntityFields(objectClass)) {
			
			// Skip Id, will be auto generated by db
			if(field.isAnnotationPresent(Id.class)) {
				return field;
			}
		}
		return null;
	}
	
	
	
	@SneakyThrows
	public void update(Object entity) {

		
		for(Method method : FieldUtils.findMethodWithAnnotation(entity.getClass(), PreUpdate.class)) {
			method.invoke(entity);
		}
		
		Long id = null;
		Field idField = null;
		
		Set<Field> updatedFields = FieldUtils.getEntityFields(entity.getClass());

		
		for(Field field : updatedFields) {
			field.setAccessible(true);

			if(field.isAnnotationPresent(Id.class)) {
				idField = field;
				id = field.getLong(entity);
			}
			else if(field.isAnnotationPresent(UpdateDate.class) && (field.getType() == java.util.Date.class || field.getType() == java.sql.Date.class)) {
				field.set(entity, new Date());
			}
			
		}
		
		if(entity instanceof CachedEntity) {
			updatedFields = ((CachedEntity) entity).updatedFields();
		}
		
		// No change to entity
		if(updatedFields.isEmpty()) {
			return;
		}
		
		
		if(idField == null || id == null || id == 0) {
			log.error("No id with value found, no update possible");
		}
		
		// 1. Create query
		Statement statement = new Statement(connection)
				.appendQuery("UPDATE ")
				.appendQuery(Query.nameMapper.mapDatabaseClass(entity.getClass()))
				.appendQuery(" SET ");
				
				//String query = "UPDATE "+EstivateQuery.nameMapper.mapEntity(entity.getClass())+" SET ";

		// 2. List updated fields
		statement.appendQuery(updatedFields.stream().map(x-> Query.nameMapper.mapDatabaseField(x.getName()) + " = ?").collect(Collectors.joining(", ")));
		
		for(Field field : updatedFields) {
			statement.appendValue(entity.getClass(), field.getName(), field.get(entity));
		}
		
		
		statement.appendQuery(" WHERE "+Query.nameMapper.mapDatabaseField(idField.getName())+" = ?");
		statement.appendValue(entity.getClass(), idField.getName(), idField.getLong(entity));
		
		
		
		boolean check = statement.execute();
		
		for(Method method : FieldUtils.findMethodWithAnnotation(entity.getClass(), PostUpdate.class)) {
			method.invoke(entity);
		}
		
	}


}
