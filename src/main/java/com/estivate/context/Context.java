package com.estivate.context;


import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
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
import javax.sql.DataSource;

import com.estivate.Mapper;
import com.estivate.NameMapper;
import com.estivate.Result;
import com.estivate.Statement;
import com.estivate.NameMapper.DefaultNameMapper;
import com.estivate.entity.CachedEntity;
import com.estivate.entity.Index.ColumnIndex;
import com.estivate.entity.Index.CompositeIndex;
import com.estivate.entity.InsertDate;
import com.estivate.entity.UpdateDate;
import com.estivate.query.Query;
import com.estivate.util.Chronometer;
import com.estivate.util.FieldUtils;
import com.estivate.util.StringPipe;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Context {
	
	public final DataSource datasource;
	public boolean tracePerformances = false;
	public NameMapper nameMapper = new DefaultNameMapper();
	
	
	public Context(DataSource datasource) {
		this.datasource = datasource;
	}
	
	
	@SneakyThrows
	public <U> U uniqueResult(Query query, Class<U> clazz) {
		
		try(Connection connection = datasource.getConnection()){
			Statement statement = Statement.toStatement(this, connection, query);
	        ResultSet resultSet = statement.executeForResultSet();
	    	ResultSetMetaData metadata = resultSet.getMetaData();
	        
	        if(resultSet.next()) {
	
	        	Map<String, String> map = new HashMap<>();
	        	for(int i = 1; i <= metadata.getColumnCount(); i++) {
	        		map.put(metadata.getColumnLabel(i), resultSet.getString(i));
	        	}
	
	        	Result result = new Result(statement, map);
	        	U object = result.mapTo(clazz);
	        	
	        	return object;
	        }
	        return null;
		}
		
	}
	
	
	@SneakyThrows
	public List<Result> list(Query joinQuery){

		try(Connection connection = datasource.getConnection()){
			Chronometer chronometer = new Chronometer("list", tracePerformances);
			chronometer.timeThreshold(100);
	
			Statement statement = Statement.toStatement(this, connection, joinQuery);
			chronometer.step("statement creation");
			
	        ResultSet resultSet = statement.executeForResultSet();
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
	            
	        	Result result = new Result(statement, map);
	        	results.add(result);
	        	chronometer.step("create result");
	            
	        }
	        chronometer.end("end");
	        
	        return results;
		}
		
		
	}
	
	protected List<Result> list(Statement statement) throws SQLException{
		
		ResultSet resultSet = statement.executeForResultSet();
        ResultSetMetaData metadata = resultSet.getMetaData();
        
        List<Result> results = new ArrayList<>();
        
        
        while(resultSet.next()) {
            
        	Map<String, String> map = new HashMap<>();
            
        	for(int i = 1; i <= metadata.getColumnCount(); i++) { 
        		map.put(metadata.getColumnLabel(i), resultSet.getString(i));
        	}
            
        	Result result = new Result(statement, map);
        	results.add(result);
            
        }
        
        return results;
	}

	
	@SneakyThrows
	public <U> List<U> listAs(Query joinQuery, Class<U> clazz) {
		
		try(Connection connection = datasource.getConnection()){
			
			Chronometer chronometer = new Chronometer("listAs", tracePerformances);
			chronometer.timeThreshold(100);
			
			Statement statement = Statement.toStatement(this, connection, joinQuery);
			chronometer.step("statement creation");
			
	        ResultSet resultSet = statement.executeForResultSet();
	        chronometer.step("get resultset");
	        
	        Mapper<U> mapper = new Mapper<>(clazz, this);
	        chronometer.step("create mapper");
	        //mapper.chronometer.active(tracePerformances);
	        
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
			if(tracePerformances) {
				for(String[] row : rows) {
					output.add(mapper.map(row));
				}
			}
			else {
				output = rows.stream().parallel().map(mapper::map).collect(Collectors.toList());
			}
			chronometer.end("map rows");
			
			if(tracePerformances) {
				System.out.println(mapper.getStats());
			}
	
			return output;
		}
		
		
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
		
		try(Connection connection = datasource.getConnection()){
			for(Method method : FieldUtils.findMethodWithAnnotation(object.getClass(), PrePersist.class)) {
				method.invoke(object);
			}

			List<String> fieldValueList = new ArrayList<>();
			
			Statement statement = new Statement(this, connection)
					.appendQuery("INSERT INTO ")
					.appendQuery(nameMapper.mapDatabaseClass(object.getClass()));

			
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
					
					fieldValueList.add(nameMapper.mapDatabaseField(field.getName()));
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
			
			ResultSet rs = statement.executeForGeneratedKeys();
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
		
	}
	
	
	

	
	@SneakyThrows
	public <U> boolean create(Class<U> entityClass) {
		
		List<String> fields = new ArrayList<>();
		for(Field field : FieldUtils.getEntityFields(entityClass)) {
			StringPipe fieldCreation = new StringPipe();
			fieldCreation.separator(" ");
			
			fieldCreation.append(nameMapper.mapDatabaseField(field.getName()));
			
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
				throw new RuntimeException("Cannot map field "+entityClass.getSimpleName()+"."+field.getName()+" type="+field.getType());
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
		
		
		String result = "CREATE TABLE "+nameMapper.mapDatabaseClass(entityClass)+" ("+fields.stream().collect(Collectors.joining(", "))+")";
		
		try (Connection connection = datasource.getConnection()){
			PreparedStatement statement = connection.prepareStatement(result);
			return statement.execute();
		}
		
		
	}
	
	public String queryAsString(Query query) throws SQLException {
		try (Connection connection = datasource.getConnection()){
			return Statement.toStatement(this, connection, query).query();
		}
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
	public <U> void update(U entity) {
		updateAll(Arrays.asList(entity));
	}
	
	@SneakyThrows
	public <U> void updateAll(List<U> entities) {

		try(Connection connection = datasource.getConnection()){
			Statement statement = new Statement(this, connection);
					
			for(Object entity : entities) {
				
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
					continue;
				}
				
				
				if(idField == null || id == null || id == 0) {
					log.error("No id with value found, no update possible");
					continue;
				}
				
				// 1. Create query
				
				statement.appendQuery("UPDATE ")
						.appendQuery(nameMapper.mapDatabaseClass(entity.getClass()))
						.appendQuery(" SET ");
						
				// 2. List updated fields
				statement.appendQuery(updatedFields.stream().map(x-> nameMapper.mapDatabaseField(x.getName()) + " = ?").collect(Collectors.joining(", ")));
				
				for(Field field : updatedFields) {
					statement.appendValue(entity.getClass(), field.getName(), field.get(entity));
				}
				
				
				statement.appendQuery(" WHERE "+nameMapper.mapDatabaseField(idField.getName())+" = ?;");
				statement.appendValue(entity.getClass(), idField.getName(), idField.getLong(entity));
				
				
			}
			
			boolean check = statement.executeForValidation();
			
			for(Object entity : entities) {
				for(Method method : FieldUtils.findMethodWithAnnotation(entity.getClass(), PostUpdate.class)) {
					method.invoke(entity);
				}
			}
		}
	}
	
	@SneakyThrows
	public List<String> showTables(){
		try (Connection connection = datasource.getConnection()){
			Statement statement = new Statement(this, connection).appendQuery("SHOW TABLES;");
			ResultSet resultSet = statement.executeForResultSet();
			
			List<String> rows = new ArrayList<>();
	        
	        while(resultSet.next()) {
	        	rows.add(resultSet.getString(1));        	
	        }
	        
	        return rows;
		}

	}
	
	@SneakyThrows
	public boolean truncateTable(Class c) {
		try (Connection connection = datasource.getConnection()){
			Statement statement = new Statement(this, connection).appendQuery("TRUNCATE TABLE ").appendQuery(nameMapper.mapDatabaseClass(c));
			return statement.executeForValidation();
		}
	}


	
	public String findEntityName(Class<?> c, String columnName) {
		for(Field field : FieldUtils.getEntityFields(c)) {
			if(columnName.equals(nameMapper.mapDatabaseField(field.getName()))){
				return field.getName();
			}
		}
		return null;
	}

	@SneakyThrows
	public boolean addIndex(Class<?> c, String name, List<String> columns) {
		try (Connection connection = datasource.getConnection()){
		//CREATE INDEX IDXNAME ON TEST(NAME)
			Statement statement = new Statement(this, connection).appendQuery("CREATE INDEX").appendQuery(name).appendQuery("ON");
			statement.appendQuery(nameMapper.mapDatabaseClass(c)+columns.stream().collect(Collectors.joining(", ", "(", ")")));
			
			return statement.executeForValidation();
		}
		
	}
	
	public CompositeIndex CompositeIndex(String name, List<ColumnIndex> columns) {
		
		ColumnIndex[] array = new ColumnIndex[columns.size()];
		columns.toArray(array);
		
		CompositeIndex index = new CompositeIndex() {
			@Override
			public String name() { return name; }

			@Override
			public Class<? extends Annotation> annotationType() { return null; }

			@Override
			public ColumnIndex[] columns() { return array; }
		};
		
		return index;
	
	}
	
	public ColumnIndex ColumnIndex(String value, Integer length) {
		
		ColumnIndex index = new ColumnIndex() {

			@Override
			public Class<? extends Annotation> annotationType() { return null; }

			@Override
			public String value() { return value; }

			@Override
			public int length() { return length; }
			
		};
		
		return index;
	
	}
	
	public abstract List<CompositeIndex> listIndexes(Class<?> c);
		
	
	

}
