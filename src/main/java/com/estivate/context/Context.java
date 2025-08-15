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
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.LinkedHashSet;

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

import com.estivate.Entity.InsertDate;
import com.estivate.Entity.UpdateDate;
import com.estivate.Estivate;
import com.estivate.IMapper;
import com.estivate.IMapper.EntityMapper;
import com.estivate.IMapper.IntegerMapper;
import com.estivate.IMapper.ShortMapper;
import com.estivate.IMapper.LongMapper;
import com.estivate.IMapper.FloatMapper;
import com.estivate.IMapper.DoubleMapper;
import com.estivate.IMapper.StringMapper;
import com.estivate.IMapper.BooleanMapper;
import com.estivate.IMapper.DateMapper;

import com.estivate.NameMapper;
import com.estivate.NameMapper.DefaultNameMapper;
import com.estivate.Result;
import com.estivate.Statement;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.index.IndexDiff;
import com.estivate.query.Query;
import com.estivate.util.CachedEntity;
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
	
	// ==================== HELPER METHODS ====================
	
	/**
	 * Creates a database connection and statement for the given query
	 */
//	private <T> Statement createStatement(Query<T> query) throws SQLException {
//		Connection connection = datasource.getConnection();
//		return Statement.toStatement(this, connection, preExecute(query));
//	}
	
	/**
	 * Creates a database connection and statement for general operations
	 */
//	private Statement createStatement() throws SQLException {
//		Connection connection = datasource.getConnection();
//		return new Statement(this, connection);
//	}
	
	/**
	 * Creates a column name to index mapping from ResultSet metadata
	 */
//	private Map<String, Integer> createColumnMap(ResultSetMetaData metadata) throws SQLException {
//		Map<String, Integer> map = new HashMap<>();
//		for(int i = 1; i <= metadata.getColumnCount(); i++) {
//			map.put(metadata.getColumnLabel(i), i);
//		}
//		return map;
//	}
	
	@SneakyThrows
	private List<String> createColumnNamesSet(ResultSetMetaData metadata) {
		
		List<String> map = new ArrayList<>();
		for(int i = 1; i <= metadata.getColumnCount(); i++) {
			map.add(metadata.getColumnLabel(i));
		}
		return map;
	}
	
	/**
	 * Extracts all values from a ResultSet row as String array
	 */
	private String[] extractRowValues(ResultSet resultSet, int columnCount) throws SQLException {
		String[] values = new String[columnCount];
		for(int i = 0; i < columnCount; i++) { 
			values[i] = resultSet.getString(i+1);
		}
		return values;
	}
	
	/**
	 * Invokes methods with a specific annotation on an entity
	 */
	private void invokeLifecycleMethods(Object entity, Class<? extends Annotation> annotationClass) {
		try {
			for(Method method : FieldUtils.findMethodWithAnnotation(entity.getClass(), annotationClass)) {
				method.invoke(entity);
			}
		} catch (Exception e) {
			log.error("Error invoking lifecycle method with annotation " + annotationClass.getSimpleName(), e);
		}
	}
	

	
	/**
	 * Gets the ID field from an entity class
	 */
	private Field getIdField(Class<?> entityClass) {
		if(entityClass == null) {
			return null;
		}
		for(Field field : FieldUtils.getEntityFields(entityClass)) {
			if(field.isAnnotationPresent(Id.class)) {
				return field;
			}
		}
		return null;
	}
	
	/**
	 * Pre-processes a query before execution
	 */
	private Query<?> preExecute(Query<?> query) {
		if(fetchQueryPreProcessor == null) {
			return query;
		}
		Query<?> clonedQuery = query.clone();
		fetchQueryPreProcessor.accept(clonedQuery);
		return clonedQuery;
	}
	
	// ==================== FETCH METHODS ====================
	
	public <T> T fetchSingle(Query<T> query){
		return fetchSingleAs(query, (Class<T>) query.getEntity().entity);
	}
	
	public <T> Optional<T> fetchSingleOptional(Query<T> query){
		return Optional.ofNullable(fetchSingle(query));
	}

	@SneakyThrows
	public Result fetchSingleAsResult(Query<?> query) {
//		try(Statement statement = createStatement(query);
//		ResultSet resultSet = statement.executeForResultSet()) {
		try(Connection connection = datasource.getConnection();
			Statement statement = Statement.toStatement(this, connection, preExecute(query));
			ResultSet resultSet = statement.executeForResultSet()) {

			
	    	ResultSetMetaData metadata = resultSet.getMetaData();
	        //Map<String, Integer> map = createColumnMap(metadata);
	    	List<String> columnNames = createColumnNamesSet(metadata);
	        if(resultSet.next()) {
	        	Result result = new Result(resultSet, columnNames, statement);
				return result;
	        }
		}
		return null;
	}
	


	public Optional<Result> fetchSingleAsResultOptional(Query<?> query){
		return Optional.ofNullable(fetchSingleAsResult(query));
	}
	
	@SuppressWarnings("unchecked")
	@SneakyThrows
	public <U> U fetchSingleAs(Query<?> query, Class<U> clazz) {
//		try(Statement statement = createStatement(query);
//			ResultSet resultSet = statement.executeForResultSet()) {
		try(Connection connection = datasource.getConnection();
			Statement statement = Statement.toStatement(this, connection, preExecute(query));
			ResultSet resultSet = statement.executeForResultSet()) {
			
			ResultSetMetaData metadata = resultSet.getMetaData();
			IMapper<U> mapper = generateMapper(clazz, this, createColumnNamesSet(metadata), tracePerformances);

			if(!resultSet.next()) {
				return null;
			}

			String[] values = extractRowValues(resultSet, metadata.getColumnCount());
			return mapper.map(values);
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
//		try(Statement statement = createStatement(query);
//			ResultSet resultSet = statement.executeForResultSet()) {
		try(Connection connection = datasource.getConnection();
				Statement statement = Statement.toStatement(this, connection, preExecute(query));
				ResultSet resultSet = statement.executeForResultSet()) {
			
			ResultSetMetaData metadata = resultSet.getMetaData();
			IMapper<U> mapper = generateMapper(clazz, this, createColumnNamesSet(metadata), tracePerformances);
	        
	        List<String[]> rows = new ArrayList<>();
	        
	        while(resultSet.next()) {
	        	String[] values = extractRowValues(resultSet, metadata.getColumnCount());
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
			
			if(tracePerformances && mapper instanceof EntityMapper) {
				System.out.println(((EntityMapper) mapper).getStats());
			}
		
			return output;
		}
	}
		
	@SuppressWarnings("unchecked")
	private <U> IMapper<U> generateMapper(Class<U> clazz, Context context, List<String> columnNames, boolean tracePerformances2) {
		if(clazz == Integer.class){
			return (IMapper<U>) new IntegerMapper();
		}
		else if(clazz == Short.class){
			return (IMapper<U>) new ShortMapper();
		}
		else if(clazz == Long.class){
			return (IMapper<U>) new LongMapper();
		}
		else if(clazz == Float.class){
			return (IMapper<U>) new FloatMapper();
		}
		else if(clazz == Double.class){
			return (IMapper<U>) new DoubleMapper();
		}
		else if(clazz == String.class){
			return (IMapper<U>) new StringMapper();
		}
		else if(clazz == Boolean.class){
			return (IMapper<U>) new BooleanMapper();
		}
		else if(clazz == java.util.Date.class){
			return (IMapper<U>) new DateMapper();
		}
		else {
			return new EntityMapper<>(clazz, this, columnNames, tracePerformances2);
		}
	}

	@SneakyThrows
	public List<Result> fetchListAsResults(Query<?> query){
//		try(Statement statement = createStatement(query);
//			ResultSet resultSet = statement.executeForResultSet()) {
		try(Connection connection = datasource.getConnection();
			Statement statement = Statement.toStatement(this, connection, preExecute(query));
			ResultSet resultSet = statement.executeForResultSet()) {
			
			Chronometer chronometer = new Chronometer("list", tracePerformances);
			chronometer.timeThreshold(100);
	        
	        ResultSetMetaData metadata = resultSet.getMetaData();
//	        Map<String, Integer> map = createColumnMap(metadata);
	        List<String> columnNames = createColumnNamesSet(metadata);
	        
	        List<Result> results = new ArrayList<>();
	        
	        while(resultSet.next()) {
	        	chronometer.step("resultset next");
	            
	        	Result result = new Result(resultSet, columnNames, statement);
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
	        //Map<String, Integer> map = createColumnMap(metadata);
	        
	        List<String> columnNames = createColumnNamesSet(metadata);
	        
	        List<Result> results = new ArrayList<>();
	        
	        while(resultSet.next()) {
	        	Result result = new Result(resultSet, columnNames, statement);
	        	results.add(result);
	        }
	        
	        return results;
		}
	}

	// ==================== AGGREGATION METHODS ====================
	
	public <U, V> Map<U, V> aggregateToMap(Query<?> query, Function<Result,U> uType, Function<Result,V> vType){
		List<Result> results = fetchListAsResults(query);
		Map<U, V> map = new LinkedHashMap<>();
		
		for(Result result : results){
			map.put(uType.apply(result), vType.apply(result));
		}

		return map;
	}

	public <U, V> Map<U, List<V>> aggregateToMapList(Query<?> query, Function<Result,U> uType, Function<Result,V> vType){
		List<Result> results = fetchListAsResults(query);
		Map<U, List<V>> map = new LinkedHashMap<>();
		
		for(Result result : results){
			map.computeIfAbsent(uType.apply(result), k -> new ArrayList<>()).add(vType.apply(result));
		}

		return map;
	}
	
	// ==================== PERSISTENCE METHODS ====================
		
	@SneakyThrows
	public <U> U updateOrInsert(U object) {
		Field idField = getIdField(object.getClass());
		if(idField != null) {
			idField.setAccessible(true);
				
			if(idField.getLong(object) == 0L) {
				insert(object);
			}
			else {
				update(object);
			}
			
			if(object instanceof CachedEntity) {
				((CachedEntity) object).saveState();
			}
		}
		else{
			insert(object);
		}
			
		return object;
	}
	
	@SneakyThrows
	public <U> U insert(U object) {
//		try(Statement statement = createStatement()){
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){		
			invokeLifecycleMethods(object, PrePersist.class);

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
				
				invokeLifecycleMethods(object, PostPersist.class);
				return object;
			}
		}
	}

	// Tries to find entity with same id, and if not found, tries to find entity with same unicity constraints
	// Returns true if entity was merged to existing entity
	@SneakyThrows
	public <U> boolean merge(U entity){
		// First try to find entity with same id
		Field idField = getIdField(entity.getClass());

		if(idField != null){
			idField.setAccessible(true);
			if(idField.getLong(entity) != 0L) {
				Query<U> query = Estivate.query((Class<U>) entity.getClass());
				query.eq(entity.getClass(), idField.getName(), idField.getLong(entity));
				U duplicatedEntity = fetchSingleAs(query, (Class<U>) entity.getClass());
				if(duplicatedEntity != null) {
					// Copy fields from result into object
					for(Field field : FieldUtils.getEntityFields(entity.getClass())) {
						field.setAccessible(true);
						field.set(entity, field.get(duplicatedEntity));
					}
					return true;
				}
			}
		}
	
		// Tries to merge with entity having same unicity constraints
		IndexDiff indexDiff = new IndexDiff(this, entity.getClass());
		for(TableIndex entityIndex : indexDiff.getEntityIndexes()){
			if(entityIndex.type() != IndexType.UNIQUE) {
				continue;
			}

			Query<U> query = Estivate.query((Class<U>) entity.getClass());
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
				return true;
			}
		}

		return false;
	}

	// Try merging if entity has a valid id, otherwise insert
	public <U> void mergeOrInsert(U entity){
		if(!merge(entity)){
			insert(entity);
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
	public String queryAsString(Query<?> query) {
//		try(Statement statement = createStatement(query)){
		try(Connection connection = datasource.getConnection();
			Statement statement = Statement.toStatement(this, connection, preExecute(query));) {
			return statement.query();
		}
		
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
				invokeLifecycleMethods(entity, PreUpdate.class);
				
				Long id = null;
				Field idField = null;
				
				Set<Field> updatedFields = new LinkedHashSet<>(FieldUtils.getEntityFields(entity.getClass()));
				
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
					updatedFields = new LinkedHashSet<>(((CachedEntity) entity).updatedFields());
				}
				
				// Remove id field from update set
				if(idField != null) {
					updatedFields.remove(idField);
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
				invokeLifecycleMethods(entity, PostUpdate.class);
			}
		}
	}
	
	// ==================== UTILITY METHODS ====================
	
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
		try(Connection connection = datasource.getConnection();
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
