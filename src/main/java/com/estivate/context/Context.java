package com.estivate.context;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
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

import com.estivate.Entity.InsertDate;
import com.estivate.Entity.UpdateDate;
import com.estivate.Estivate;
import com.estivate.NameMapper;
import com.estivate.NameMapper.DefaultNameMapper;
import com.estivate.Statement;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.index.IndexDiff;
import com.estivate.query.Query;
import com.estivate.query.SelectQuery;
import com.estivate.result.IMapper;
import com.estivate.result.Result;
import com.estivate.result.IMapper.BooleanMapper;
import com.estivate.result.IMapper.DateMapper;
import com.estivate.result.IMapper.DoubleMapper;
import com.estivate.result.EntityMapper;
import com.estivate.result.IMapper.FloatMapper;
import com.estivate.result.IMapper.IntegerMapper;
import com.estivate.result.IMapper.LongMapper;
import com.estivate.result.IMapper.OrdinalEnumMapper;
import com.estivate.result.IMapper.ResultMapper;
import com.estivate.result.IMapper.ShortMapper;
import com.estivate.result.IMapper.StringEnumMapper;
import com.estivate.result.IMapper.StringMapper;
import com.estivate.util.CachedEntity;
import com.estivate.util.FieldUtils;
import com.estivate.util.StringPipe;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Context {
	
	public final DataSource datasource;
	public boolean tracePerformances = false;
	@Getter public NameMapper nameMapper = new DefaultNameMapper();

	public Consumer<Query<?,?>> fetchQueryPreProcessor = null;
	
		
	public Context(DataSource datasource) {
		this.datasource = datasource;
	}
	
	// ==================== HELPER METHODS ====================
	

	
	@SneakyThrows
	private List<String> createColumnNamesSet(ResultSetMetaData metadata) {
		
		List<String> map = new ArrayList<>();
		for(int i = 1; i <= metadata.getColumnCount(); i++) {
			map.add(metadata.getColumnLabel(i));
		}
		return map;
	}
	
	@SneakyThrows
	private String[] createColumnsArray(ResultSetMetaData metadata) {
		
		String[] columns = new String[metadata.getColumnCount()];
		for(int i = 0; i < metadata.getColumnCount(); i++) {
			columns[i] = metadata.getColumnLabel(i+1);
		}
		return columns;
		
	}
	
	/**
	 * Extracts all values from a ResultSet row as String array
	 */
	private String[] extractRowValues(ResultSet resultSet) throws SQLException {
		String[] values = new String[resultSet.getMetaData().getColumnCount()];
		for(int i = 0; i < resultSet.getMetaData().getColumnCount(); i++) { 
			values[i] = resultSet.getString(i+1);
		}
		return values;
	}
	
	
	
	/**
	 * Pre-processes a query before execution
	 */
	private Query<?,?> preExecute(Query<?,?> query) {
		if(fetchQueryPreProcessor == null) {
			return query;  
		}
		Query<?,?> clonedQuery = query.clone();
		fetchQueryPreProcessor.accept(clonedQuery);
		return clonedQuery;
	}



	// ==================== EXECUTE METHODS ====================

	@SneakyThrows
	public Boolean execute(Query<?,?> query) {
		try(Connection connection = datasource.getConnection();
			Statement statement = Statement.toStatement(this, connection, preExecute(query))) {
			return statement.executeForValidation();
		}
	}
	
	// ==================== FETCH METHODS ====================
	@SneakyThrows
	public <T> T fetchSingleWithMapper(SelectQuery<?> query, IMapper<T> mapper) {
		try(Connection connection = datasource.getConnection();
			Statement statement = Statement.toStatement(this, connection, preExecute(query));
			ResultSet resultSet = statement.executeForResultSet()) {
			
			if(!resultSet.next()) {
				return null;
			}
			mapper.setNameMapper(nameMapper);
			mapper.setColumnNames(createColumnsArray(resultSet.getMetaData()));

			return mapper.map(extractRowValues(resultSet));
		}
	}
	
	@SneakyThrows
	public <T> List<T> fetchListWithMapper(SelectQuery<?> query, IMapper<T> mapper) {
			
		try(Connection connection = datasource.getConnection();
			Statement statement = Statement.toStatement(this, connection, preExecute(query));
			ResultSet resultSet = statement.executeForResultSet()) {
			
			mapper.setNameMapper(nameMapper);
			mapper.setColumnNames(createColumnsArray(resultSet.getMetaData()));
	        
	        List<String[]> rows = new ArrayList<>();
	        
	        while(resultSet.next()) {
	        	String[] values = extractRowValues(resultSet);
	        	rows.add(values);
	        }
	        
			List<T> output = new ArrayList<>();
			if(tracePerformances) {
				for(String[] row : rows) {
					output.add(mapper.map(row));
				}
			}
			else {
				output = rows.stream().parallel().map(mapper::map).collect(Collectors.toList());
			}
			
			if(tracePerformances && mapper instanceof EntityMapper) {
				log.info(((EntityMapper) mapper).getStats());
			}
		
			return output;
		}
	}
	
	public <T> T 	fetchSingle(SelectQuery<T> query)						{ return fetchSingleAs(query, query.getEntity().entity); }
	public <U> U 	fetchSingleAs(SelectQuery<?> query, Class<U> clazz) 	{ return fetchSingleWithMapper(query, new EntityMapper<U>(clazz)); }
	public Result 	fetchSingleAsResult(SelectQuery<?> query) 			{ return fetchSingleWithMapper(query, new ResultMapper()); }
	public String 	fetchSingleAsString(SelectQuery<?> query)				{ return fetchSingleWithMapper(query, new StringMapper()); }
	public Short	fetchSingleAsShort(SelectQuery<?> query)				{ return fetchSingleWithMapper(query, new ShortMapper()); }
	public Integer	fetchSingleAsInteger(SelectQuery<?> query)			{ return fetchSingleWithMapper(query, new IntegerMapper()); }
	public Long		fetchSingleAsLong(SelectQuery<?> query)				{ return fetchSingleWithMapper(query, new LongMapper()); }
	public Float	fetchSingleAsFloat(SelectQuery<?> query)				{ return fetchSingleWithMapper(query, new FloatMapper()); }
	public Double	fetchSingleAsDouble(SelectQuery<?> query)				{ return fetchSingleWithMapper(query, new DoubleMapper()); }
	public Date		fetchSingleAsDate(SelectQuery<?> query)				{ return fetchSingleWithMapper(query, new DateMapper()); }
	public Boolean	fetchSingleAsBoolean(SelectQuery<?> query)			{ return fetchSingleWithMapper(query, new BooleanMapper()); }
	public <U extends Enum<U>> U 	fetchSingleAsStringEnum(SelectQuery<?> query, Class<U> enumClass) { return fetchSingleWithMapper(query, new StringEnumMapper<U>(enumClass)); }
	public <U extends Enum<U>> U 	fetchSingleAsOrdinalEnum(SelectQuery<?> query, Class<U> enumClass) { return fetchSingleWithMapper(query, new OrdinalEnumMapper<U>(enumClass)); }


	public <T> Optional<T> 		fetchSingleOptional(SelectQuery<T> query)						{ return Optional.ofNullable(fetchSingle(query)); }
	public <U> Optional<U> 		fetchSingleAsOptional(SelectQuery<?> query, Class<U> clazz) 	{ return Optional.ofNullable(fetchSingleAs(query, clazz)); }
	public Optional<Result> 	fetchSingleAsResultOptional(SelectQuery<?> query)				{ return Optional.ofNullable(fetchSingleAsResult(query)); }
	public Optional<String>		fetchSingleAsStringOptional(SelectQuery<?> query)				{ return Optional.ofNullable(fetchSingleAsString(query)); }
	public Optional<Short> 		fetchSingleAsShortOptional(SelectQuery<?> query)				{ return Optional.ofNullable(fetchSingleAsShort(query)); }
	public Optional<Integer> 	fetchSingleAsIntegerOptional(SelectQuery<?> query)			{ return Optional.ofNullable(fetchSingleAsInteger(query)); }
	public Optional<Long> 		fetchSingleAsLongOptional(SelectQuery<?> query)				{ return Optional.ofNullable(fetchSingleAsLong(query)); }
	public Optional<Float> 		fetchSingleAsFloatOptional(SelectQuery<?> query)				{ return Optional.ofNullable(fetchSingleAsFloat(query)); }
	public Optional<Double> 	fetchSingleAsDoubleOptional(SelectQuery<?> query)				{ return Optional.ofNullable(fetchSingleAsDouble(query)); }
	public Optional<Date> 		fetchSingleAsDateOptional(SelectQuery<?> query)				{ return Optional.ofNullable(fetchSingleAsDate(query)); }
	public Optional<Boolean> 	fetchSingleAsBooleanOptional(SelectQuery<?> query)			{ return Optional.ofNullable(fetchSingleAsBoolean(query)); }
	public <U extends Enum<U>> Optional<U> 		fetchSingleAsStringEnumOptional(SelectQuery<?> query, Class<U> enumClass) { return Optional.ofNullable(fetchSingleAsStringEnum(query, enumClass)); }
	public <U extends Enum<U>> Optional<U> 		fetchSingleAsOrdinalEnumOptional(SelectQuery<?> query, Class<U> enumClass) { return Optional.ofNullable(fetchSingleAsOrdinalEnum(query, enumClass)); }

	
		
	public <T> List<T> 		fetchList(SelectQuery<T> query)					{ return fetchListAs(query, query.getEntity().entity); }
	public <U> List<U> 		fetchListAs(SelectQuery<?> query, Class<U> clazz) { return fetchListWithMapper(query, new EntityMapper<U>(clazz)); }
	public List<Result> 	fetchListAsResults(SelectQuery<?> query) 			{ return fetchListWithMapper(query, new ResultMapper()); }
	public List<String>		fetchListAsString(SelectQuery<?> query)			{ return fetchListWithMapper(query, new StringMapper()); }
	public List<Short>		fetchListAsShort(SelectQuery<?> query)			{ return fetchListWithMapper(query, new ShortMapper()); }
	public List<Integer>	fetchListAsInteger(SelectQuery<?> query)			{ return fetchListWithMapper(query, new IntegerMapper()); }
	public List<Long>		fetchListAsLong(SelectQuery<?> query)				{ return fetchListWithMapper(query, new LongMapper()); }
	public List<Float>		fetchListAsFloat(SelectQuery<?> query)			{ return fetchListWithMapper(query, new FloatMapper()); }
	public List<Double>		fetchListAsDouble(SelectQuery<?> query)			{ return fetchListWithMapper(query, new DoubleMapper()); }
	public List<Date>		fetchListAsDate(SelectQuery<?> query)				{ return fetchListWithMapper(query, new DateMapper()); }
	public List<Boolean>	fetchListAsBoolean(SelectQuery<?> query)			{ return fetchListWithMapper(query, new BooleanMapper()); }
	public <U extends Enum<U>> List<U> 		fetchListAsStringEnum(SelectQuery<?> query, Class<U> enumClass) { return fetchListWithMapper(query, new StringEnumMapper<U>(enumClass)); }
	public <U extends Enum<U>> List<U> 		fetchListAsOrdinalEnum(SelectQuery<?> query, Class<U> enumClass) { return fetchListWithMapper(query, new OrdinalEnumMapper<U>(enumClass)); }

	public Long fetchCount(SelectQuery<?> query) {
		return fetchSingleAsLong(query.clone()
			.clearSelects()
			.clearGroupBys()
			.clearOrders()
			.selectCountAs("count"));
	}
	
		
	protected List<Result> fetchListAsResults(Statement statement) throws SQLException{
		try(ResultSet resultSet = statement.executeForResultSet()) {
	        ResultSetMetaData metadata = resultSet.getMetaData();
	        
	        String[] columnNames = createColumnsArray(metadata);
	        
	        List<Result> results = new ArrayList<>();
	        
	        while(resultSet.next()) {
	        	
	        	
	        	Result result = new Result(extractRowValues(resultSet), columnNames, statement.getContext().getNameMapper());
	        	results.add(result);
	        
	        }
	        
	        return results;
		}
	}

	// ==================== AGGREGATION METHODS ====================
	
	public <U, V> Map<U, V> aggregateToMap(SelectQuery<?> query, Function<Result,U> uType, Function<Result,V> vType){
		List<Result> results = fetchListAsResults(query);
		Map<U, V> map = new LinkedHashMap<>();
		
		for(Result result : results){
			map.put(uType.apply(result), vType.apply(result));
		}

		return map;
	}

	public <U, V> Map<U, List<V>> aggregateToMapList(SelectQuery<?> query, Function<Result,U> uType, Function<Result,V> vType){
		List<Result> results = fetchListAsResults(query);
		Map<U, List<V>> map = new LinkedHashMap<>();
		
		for(Result result : results){
			map.computeIfAbsent(uType.apply(result), k -> new ArrayList<>()).add(vType.apply(result));
		}

		return map;
	}
	
	// ==================== PERSISTENCE METHODS ====================
		
	@SneakyThrows
	public <U> U insert(U object) {
//		try(Statement statement = createStatement()){
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){		
			FieldUtils.invokeLifecycleMethods(object, PrePersist.class);

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
					statement.appendObjectAsValue(object.getClass(), field.getName(), field.get(object));
					
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
					Field field = FieldUtils.getIdField(object.getClass());
					field.setAccessible(true);
					field.setLong(object, rs.getLong(1));
				}
				else {
					return null;
				}
				
				FieldUtils.invokeLifecycleMethods(object, PostPersist.class);
				return object;
			}
		}
	}

	// Tries to find entity with same id, and if not found, tries to find entity with same unicity constraints
	// Returns true if entity was merged to existing entity
	@SneakyThrows
	public <U> boolean merge(U entity){
		// First try to find entity with same id
		Field idField = FieldUtils.getIdField(entity.getClass());

		if(idField != null){
			idField.setAccessible(true);
			if(idField.getLong(entity) != 0L) {
				SelectQuery<U> query = Estivate.selectQuery((Class<U>) entity.getClass());
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

			SelectQuery<U> query = Estivate.selectQuery((Class<U>) entity.getClass());
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
	public <U> U updateOrInsert(U object) {
		Field idField = FieldUtils.getIdField(object.getClass());
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
	public <U> void update(U entity) {
		update(Arrays.asList(entity));
	}

	@SneakyThrows
	public <U> void update(List<U> entities) {
		if(entities == null) {
			return;
		}
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){
					
			for(Object entity : entities) {
				if(entity == null) {
					continue;
				}
				FieldUtils.invokeLifecycleMethods(entity, PreUpdate.class);
				
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
					statement.appendObjectAsValue(entity.getClass(), field.getName(), field.get(entity));
				}
				
				
				statement.appendQuery(" WHERE "+nameMapper.mapDatabaseField(idField.getName())+" = ?;");
				statement.appendObjectAsValue(entity.getClass(), idField.getName(), idField.getLong(entity));
			}
			
			boolean check = statement.executeForValidation();
			
			for(Object entity : entities) {
				FieldUtils.invokeLifecycleMethods(entity, PostUpdate.class);
			}
		}
	}

	
	// ==================== TABLE MANAGEMENT ====================
	
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
	public <U> boolean createTable(Class<U> entityClass) {
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
	public boolean truncateTable(Class<?> entity) {
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){
			statement.appendQuery("TRUNCATE TABLE ").appendQuery(nameMapper.mapDatabaseClass(entity));
			return statement.executeForValidation();
		}
	}
		
	// ==================== INDEX MANAGEMENT ====================
	
	// find field from columnName
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
	
	
	
	public abstract List<TableIndex> listIndexes(Class<?> c);
	
	// ==================== MISC ====================

	@SneakyThrows
	public String queryAsString(Query<?,?> query) {
		try(Connection connection = datasource.getConnection();
			Statement statement = Statement.toStatement(this, connection, preExecute(query));) {
			return statement.query();
		}
		
	}
	
}
