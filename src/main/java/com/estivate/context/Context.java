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
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
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

import org.apache.commons.lang3.StringUtils;

import com.estivate.Mapper;
import com.estivate.NameMapper;
import com.estivate.NameMapper.DefaultNameMapper;
import com.estivate.Result;
import com.estivate.Statement;
import com.estivate.entity.CachedEntity;
import com.estivate.entity.InsertDate;
import com.estivate.entity.UpdateDate;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.index.IndexDiff;
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

	public Consumer<Query<?>> fetchQueryPreProcessor = null;
	
	
	public Context(DataSource datasource) {
		this.datasource = datasource;
	}
	
	
	public <T> T fetchSingle(Query<T> query){
		return fetchSingleAs(query, (Class<T>) query.getEntity().entity);
	}
	
	public <T> Optional<T> fetchSingleOptional(Query<T> query){
		return Optional.ofNullable(fetchSingle(query));
	}

	@SneakyThrows
	public Result fetchSingleAsResult(Query<?> query) {
		
		try(Connection connection = datasource.getConnection();
			Statement statement = Statement.toStatement(this, connection, preExecute(query));
			ResultSet resultSet = statement.executeForResultSet()) {
			
	    	ResultSetMetaData metadata = resultSet.getMetaData();
	        
	        if(resultSet.next()) {
	
	        	Map<String, String> map = new HashMap<>();
	        	for(int i = 1; i <= metadata.getColumnCount(); i++) {
	        		map.put(metadata.getColumnLabel(i), resultSet.getString(i));
	        	}
	
	        	Result result = new Result(statement, map);
				return result;
	        }
	        
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public Optional<Result> fetchSingleAsResultOptional(Query<?> query){
		return Optional.ofNullable(fetchSingleAsResult(query));
	}
	
	@SuppressWarnings("unchecked")
	@SneakyThrows
	public <U> U fetchSingleAs(Query<?> query, Class<U> clazz) {
		Result result = fetchSingleAsResult(query);
		if(result == null){
			return null;
		}
		else if(clazz == Integer.class){
			return (U) result.columnAsInteger(result.getColumns().keySet().iterator().next());
		}
		else if(clazz == Short.class){
			return (U) result.columnAsShort(result.getColumns().keySet().iterator().next());
		}
		else if(clazz == Long.class){
			return (U) result.columnAsLong(result.getColumns().keySet().iterator().next());
		}
		else if(clazz == Float.class){
			return (U) result.columnAsFloat(result.getColumns().keySet().iterator().next());
		}
		else if(clazz == Double.class){
			return (U) result.columnAsDouble(result.getColumns().keySet().iterator().next());
		}
		else if(clazz == String.class){
			return (U) result.columnAsString(result.getColumns().keySet().iterator().next());
		}
		else if(clazz == Boolean.class){
			return (U) result.columnAsBoolean(result.getColumns().keySet().iterator().next());
		}
		else if(clazz == java.util.Date.class){
			return (U) result.columnAsDate(result.getColumns().keySet().iterator().next());
		}
		else if(clazz.isEnum()){
			// Try to parse as ordinal first
			String value = result.columnAsString(result.getColumns().keySet().iterator().next());

			if(StringUtils.isNumeric(value)) {
				return result.columnAsOrdinalEnum(value, clazz);
			}
			else{
				return result.columnAsStringEnum(value, clazz);	
			}
		}
		else {
			U object = result.mapTo(clazz);
	    	return object;
		}
	}

	public <U> Optional<U> fetchSingleAsOptional(Query<?> query, Class<U> clazz) {
		return Optional.ofNullable(fetchSingleAs(query, clazz));
	}

	public <T> List<T> fetchList(Query<T> query){
		return fetchListAs(query, (Class<T>) query.getEntity().entity);
	}
	@SneakyThrows
	public <U> List<U> fetchListAs(Query<?> query, Class<U> clazz) {
		
		try(Connection connection = datasource.getConnection();
			Statement statement = Statement.toStatement(this, connection, preExecute(query));
			ResultSet resultSet = statement.executeForResultSet()) {
			
	        Mapper<U> mapper = new Mapper<>(clazz, this, tracePerformances);
	        
	        ResultSetMetaData metadata = resultSet.getMetaData();
	        mapper.attachMetadata(metadata);
	        
	        List<String[]> rows = new ArrayList<>();
	        
	        while(resultSet.next()) {
	        	String[] values = new String[metadata.getColumnCount()];
	        	for(int i = 0; i < metadata.getColumnCount(); i++) { 
	        		values[i] = resultSet.getString(i+1);
	        	}
	        	rows.add(values);
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
			
			if(tracePerformances) {
				System.out.println(mapper.getStats());
			}
	
			return output;
		}
		
	}
	
	
	@SneakyThrows
	public List<Result> fetchListAsResults(Query<?> query){

		try(Connection connection = datasource.getConnection();
			Statement statement = Statement.toStatement(this, connection, preExecute(query));
			ResultSet resultSet = statement.executeForResultSet()) {
			Chronometer chronometer = new Chronometer("list", tracePerformances);
			chronometer.timeThreshold(100);
	        
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

	
	
	protected List<Result> fetchListAsResults(Statement statement) throws SQLException{
		
		try(ResultSet resultSet = statement.executeForResultSet()) {
		
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
	}

	
	
	
	
	
	
	
	@SneakyThrows
	public <U> U updateOrInsert(U object) {
		
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
	public <U> U insert(U object) {
		
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){
			
			for(Method method : FieldUtils.findMethodWithAnnotation(object.getClass(), PrePersist.class)) {
				method.invoke(object);
			}

			List<String> fieldValueList = new ArrayList<>();
			
			statement.appendQuery("INSERT INTO ")
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
			
			try(ResultSet rs = statement.executeForGeneratedKeys()){
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
		
	}
	

	// Tries to find entity with same 
	public <U> void merge(U entity){
		try{
			Field idField = getIdField(entity.getClass());
			idField.setAccessible(true);
		
			if(idField != null && idField.getLong(entity) != 0L) {
				Query query = new Query(entity.getClass());
				query.eq(entity.getClass(), idField.getName(), idField.getLong(entity));
				U duplicatedEntity = fetchSingleAs(query, (Class<U>) entity.getClass());
				if(duplicatedEntity != null) {
					// Copy fields from result into object
					for(Field field : FieldUtils.getEntityFields(entity.getClass())) {
						field.setAccessible(true);
						field.set(entity, field.get(duplicatedEntity));
					}
					return;
				}
			}
		
			// Tries to merge with entity having same unicity constraints
			IndexDiff indexDiff = new IndexDiff(this, entity.getClass());
			for(TableIndex entityIndex : indexDiff.getEntityIndexes()){
				if(entityIndex.type() != IndexType.UNIQUE) {
					continue;
				}

				Query query = new Query(entity.getClass());
				for(IndexColumn columnIndex : entityIndex.columns()) {
					Field field = entity.getClass().getDeclaredField(columnIndex.value());
					field.setAccessible(true);
					Object value = field.get(entity);

					query.eq(entity.getClass(), columnIndex.value(), value);
				}

				U duplicatedEntity = fetchSingleAs(query, (Class<U>) entity.getClass());
				
				if(duplicatedEntity != null) {
					
					// Copy fields from result into object
					for(Field field : FieldUtils.getEntityFields(entity.getClass())) {
						field.setAccessible(true);
						field.set(entity, field.get(duplicatedEntity));
					}
				}

			}

		}
		catch(Exception e) {
			log.error("Error on merge", e);
		}


	}


	public <U> void mergeOrInsert(U entity){
		merge(entity);
		
		try{
			Field idField = getIdField(entity.getClass());
			idField.setAccessible(true);
		
			if(idField != null && idField.getLong(entity) != 0L) {
				insert(entity);
			}
		}
		catch(Exception e){
			log.error("Error on mergeOrInsert", e);
		}
	}
	

	
	@SneakyThrows
	public <U> boolean create(Class<U> entityClass) {
		
		List<String> fields = new ArrayList<>();
		for(Field field : FieldUtils.getEntityFields(entityClass)) {
			StringPipe fieldCreation = new StringPipe();
			fieldCreation.separator(" ");
			
			fieldCreation.append(nameMapper.mapDatabaseField(field.getName()));
			
			Class<?> returnClass = field.getType();
			
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
		
		try(Connection connection = datasource.getConnection(); 
			PreparedStatement statement = connection.prepareStatement(result);){
			return statement.execute();
		}
		
		
	}
	
	@SneakyThrows
	public String queryAsString(Query query) {
		
		try(Connection connection = datasource.getConnection();
			Statement statement = Statement.toStatement(this, connection, preExecute(query)); ){
			return statement.query();
		}
		
	}
	
	private Query preExecute(Query query) {
		if(fetchQueryPreProcessor == null) {
			return query;
		}
		Query<?> clonedQuery = query.clone();
		fetchQueryPreProcessor.accept(clonedQuery);
		return clonedQuery;
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

		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){
					
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
		try (Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection, "SHOW TABLES;");
			ResultSet resultSet = statement.executeForResultSet(); ){
			
			List<String> rows = new ArrayList<>();
	        
	        while(resultSet.next()) {
	        	rows.add(resultSet.getString(1));        	
	        }
	        
	        return rows;
		}

	}
	
	@SneakyThrows
	public boolean truncateTable(Class<?> entity) {
		try (Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){
			
			statement.appendQuery("TRUNCATE TABLE ").appendQuery(nameMapper.mapDatabaseClass(entity));
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
	public boolean addIndex(Class<?> c, String name, IndexType type, List<String> columns) {
		
		try(Connection connection = datasource.getConnection(); 
			Statement statement = new Statement(this, connection); ){
			statement
				.appendQuery("CREATE")
				.appendQuery(type == IndexType.DEFAULT ? "" : type.name().toUpperCase())
				.appendQuery("INDEX")
				.appendQuery(name)
				.appendQuery("ON");

			statement.appendQuery(nameMapper.mapDatabaseClass(c)+columns.stream().collect(Collectors.joining(", ", "(", ")")));
			
			return statement.executeForValidation();
		}
		
	}

	@SneakyThrows
	public boolean removeIndex(Class<?> c, String name){
		if(name.equals("PRIMARY")) {
			return false;
		}
		
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){
			statement
				.appendQuery("DROP INDEX")
				.appendQuery(name)
				.appendQuery("ON")
				.appendQuery(nameMapper.mapDatabaseClass(c));
			return statement.executeForValidation();
		}
		
	}
	
	public static TableIndex CompositeIndex(String name, IndexType type, List<IndexColumn> columns) {
		
		IndexColumn[] array = new IndexColumn[columns.size()];
		columns.toArray(array);
		
		TableIndex index = new TableIndex() {
			@Override
			public String name() { return name; }

			@Override
			public IndexType type() { return type; }

			@Override
			public Class<? extends Annotation> annotationType() { return null; }

			@Override
			public IndexColumn[] columns() { return array; }
		};
		
		return index;
	
	}
	
	public IndexColumn ColumnIndex(String value, Integer length) {
		
		IndexColumn index = new IndexColumn() {

			@Override
			public Class<? extends Annotation> annotationType() { return null; }

			@Override
			public String value() { return value; }

			@Override
			public int length() { return length; }
			
		};
		
		return index;
	
	}
	
	public abstract List<TableIndex> listIndexes(Class<?> c);
		
	
	

}
