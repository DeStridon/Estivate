package com.estivate.context;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
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

import javax.sql.DataSource;

import com.estivate.Entity;
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
import com.estivate.query.Attribute;
import com.estivate.query.DeleteQuery;
import com.estivate.query.Query;
import com.estivate.query.SelectQuery;
import com.estivate.query.UpdateQuery;
import com.estivate.result.ResultRow;
import com.estivate.result.ResultTable;
import com.estivate.util.CachedEntity;
import com.estivate.util.FieldUtils;
import com.estivate.util.FieldUtils.AttributeGetter;
import com.estivate.util.StringPipe;

import lombok.Getter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public abstract class Context {
	
	public final DataSource datasource;
	public boolean tracePerformances = false;
	@Getter public NameMapper nameMapper = new DefaultNameMapper();
	
	public Consumer<SelectQuery<?>> selectInterceptor = null;
	public Consumer<UpdateQuery<?>> updateInterceptor = null;
	public Consumer<DeleteQuery<?>> deleteInterceptor = null;
	public Consumer<Object> 		insertInterceptor = null;
	
		
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
		Query<?,?> clonedQuery = query.clone();
		
		// Route to specific preprocessors based on query type
		if(query instanceof SelectQuery && selectInterceptor != null) {
			selectInterceptor.accept((SelectQuery<?>) clonedQuery);
		}
		else if(query instanceof UpdateQuery && updateInterceptor != null) {
			updateInterceptor.accept((UpdateQuery<?>) clonedQuery);
		}
		else if(query instanceof DeleteQuery && deleteInterceptor != null) {
			deleteInterceptor.accept((DeleteQuery<?>) clonedQuery);
		}
		
		return clonedQuery;
	}
	
	/**
	 * Pre-processes an object before insert
	 */
	private <U> void preInsert(U object) {
		if(insertInterceptor != null) {
			insertInterceptor.accept(object);
		}
	}



	// ==================== EXECUTE METHODS ====================

	@SneakyThrows
	public Boolean execute(Query<?,?> query) {
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection, preExecute(query))) {
			return statement.executeForValidation();
		}
	}
	
	
	@SneakyThrows
	public <T> ResultTable<T> fetch(SelectQuery<T> query){
		SelectQuery<T> finalQuery = (SelectQuery<T>) preExecute(query);
		
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection, finalQuery);
			ResultSet resultSet = statement.executeForResultSet()) {
			
			ResultTable<T> resultTable = new ResultTable<>(this, createColumnsArray(resultSet.getMetaData()), finalQuery);
			
			while(resultSet.next()) {
	        	String[] values = extractRowValues(resultSet);
	        	resultTable.addRow(values);
	        }
			
			return resultTable;
		}
	}
	
	// // ==================== FETCH METHODS ====================
	// @Deprecated
	// @SneakyThrows
	// public <T> T fetchSingleWithMapper(SelectQuery<?> query, IMapper<T> mapper) {
	// 	try(Connection connection = datasource.getConnection();
	// 		Statement statement = Statement.toStatement(this, connection, preExecute(query));
	// 		ResultSet resultSet = statement.executeForResultSet()) {
			
	// 		if(!resultSet.next()) {
	// 			return null;
	// 		}
			
	// 		return mapper.map(extractRowValues(resultSet));
	// 	}
	// }
	
	// @Deprecated
	// @SneakyThrows
	// public <T> List<T> fetchListWithMapper(SelectQuery<?> query, IMapper<T> mapper) {
			
	// 	try(Connection connection = datasource.getConnection();
	// 		Statement statement = Statement.toStatement(this, connection, preExecute(query));
	// 		ResultSet resultSet = statement.executeForResultSet()) {
			
	// 		//mapper.setNameMapper(nameMapper);
	// 		//mapper.configure(query);
	        
	//         List<String[]> rows = new ArrayList<>();
	        
	//         while(resultSet.next()) {
	//         	String[] values = extractRowValues(resultSet);
	//         	rows.add(values);
	//         }
	        
	// 		List<T> output = new ArrayList<>();
	// 		if(tracePerformances) {
	// 			for(String[] row : rows) {
	// 				output.add(mapper.map(row));
	// 			}
	// 		}
	// 		else {
	// 			output = rows.stream().parallel().map(mapper::map).collect(Collectors.toList());
	// 		}
			
	// 		if(tracePerformances && mapper instanceof EntityMapper) {
	// 			log.info(((EntityMapper) mapper).getStats());
	// 		}
		
	// 		return output;
	// 	}
	// }


	public <T> T 			fetchSingle(SelectQuery<T> query)					{ return fetch(query).mapTo(query.getEntity().entity); }
	public <U> U 			fetchSingleAs(SelectQuery<?> query, Class<U> clazz) { return fetch(query).mapTo(clazz); }
	public <T> ResultRow<T> fetchSingleAsResult(SelectQuery<T> query) 			{ return fetch(query).getRows().get(0); }
	public String 			fetchSingleAsString(SelectQuery<?> query)			{ return fetch(query).mapToString(); }
	public Short			fetchSingleAsShort(SelectQuery<?> query)			{ return fetch(query).mapToShort(); }
	public Integer			fetchSingleAsInteger(SelectQuery<?> query)			{ return fetch(query).mapToInteger(); }
	public Long				fetchSingleAsLong(SelectQuery<?> query)				{ return fetch(query).mapToLong(); }
	public Float			fetchSingleAsFloat(SelectQuery<?> query)			{ return fetch(query).mapToFloat(); }
	public Double			fetchSingleAsDouble(SelectQuery<?> query)			{ return fetch(query).mapToDouble(); }
	public Date				fetchSingleAsDate(SelectQuery<?> query)				{ return fetch(query).mapToDate(); }
	public LocalDateTime 	fetchSingleAsLocalDateTime(SelectQuery<?> query)	{ return fetch(query).mapToLocalDateTime(); }
	public Boolean			fetchSingleAsBoolean(SelectQuery<?> query)			{ return fetch(query).mapToBoolean(); }
	public <U extends Enum<U>> U 	fetchSingleAsStringEnum(SelectQuery<?> query, Class<U> enumClass) { return fetch(query).mapToStringEnum(enumClass); }
	public <U extends Enum<U>> U 	fetchSingleAsOrdinalEnum(SelectQuery<?> query, Class<U> enumClass) { return fetch(query).mapToOrdinalEnum(enumClass); }


	public <E> Optional<E> 				fetchOptional(SelectQuery<E> query)					{ return Optional.ofNullable(fetchSingle(query)); }
	public <U> Optional<U> 				fetchOptionalAs(SelectQuery<?> query, Class<U> clazz) { return Optional.ofNullable(fetchSingleAs(query, clazz)); }
	public <E> Optional<ResultRow<E>> 	fetchOptionalAsResult(SelectQuery<E> query)			{ return Optional.ofNullable(fetchSingleAsResult(query)); }
	public Optional<String>				fetchOptionalAsString(SelectQuery<?> query)			{ return Optional.ofNullable(fetchSingleAsString(query)); }
	public Optional<Short> 				fetchOptionalAsShort(SelectQuery<?> query)			{ return Optional.ofNullable(fetchSingleAsShort(query)); }
	public Optional<Integer> 			fetchOptionalAsInteger(SelectQuery<?> query)			{ return Optional.ofNullable(fetchSingleAsInteger(query)); }
	public Optional<Long> 				fetchOptionalAsLong(SelectQuery<?> query)				{ return Optional.ofNullable(fetchSingleAsLong(query)); }
	public Optional<Float> 				fetchOptionalAsFloat(SelectQuery<?> query)			{ return Optional.ofNullable(fetchSingleAsFloat(query)); }
	public Optional<Double> 			fetchOptionalAsDouble(SelectQuery<?> query)			{ return Optional.ofNullable(fetchSingleAsDouble(query)); }
	public Optional<Date> 				fetchOptionalAsDate(SelectQuery<?> query)				{ return Optional.ofNullable(fetchSingleAsDate(query)); }
	public Optional<LocalDateTime> 		fetchOptionalAsLocalDateTime(SelectQuery<?> query)	{ return Optional.ofNullable(fetchSingleAsLocalDateTime(query)); }
	public Optional<Boolean> 			fetchOptionalAsBoolean(SelectQuery<?> query)			{ return Optional.ofNullable(fetchSingleAsBoolean(query)); }
	public <U extends Enum<U>> Optional<U> 		fetchOptionalAsStringEnum(SelectQuery<?> query, Class<U> enumClass) { return Optional.ofNullable(fetchSingleAsStringEnum(query, enumClass)); }
	public <U extends Enum<U>> Optional<U> 		fetchOptionalAsOrdinalEnum(SelectQuery<?> query, Class<U> enumClass) { return Optional.ofNullable(fetchSingleAsOrdinalEnum(query, enumClass)); }
	
	
		
	public <T> List<T> 				fetchList(SelectQuery<T> query)						{ return fetch(query).mapToList(query.getEntity().entity); }
	public <U> List<U> 				fetchListAs(SelectQuery<?> query, Class<U> clazz) 	{ return fetch(query).mapToList(clazz); }
	public <T> List<ResultRow<T>> 	fetchListAsResults(SelectQuery<T> query) 	{ return fetch(query).getRows(); }
	public List<String>				fetchListAsString(SelectQuery<?> query)				{ return fetch(query).mapToListString(); }
	public List<Short>				fetchListAsShort(SelectQuery<?> query)				{ return fetch(query).mapToListShort(); }
	public List<Integer>			fetchListAsInteger(SelectQuery<?> query)			{ return fetch(query).mapToListInteger(); }
	public List<Long>				fetchListAsLong(SelectQuery<?> query)				{ return fetch(query).mapToListLong(); }
	public List<Float>				fetchListAsFloat(SelectQuery<?> query)				{ return fetch(query).mapToListFloat(); }
	public List<Double>				fetchListAsDouble(SelectQuery<?> query)				{ return fetch(query).mapToListDouble(); }
	public List<Date>				fetchListAsDate(SelectQuery<?> query)				{ return fetch(query).mapToListDate(); }
	public List<LocalDateTime>		fetchListAsLocalDateTime(SelectQuery<?> query)	{ return fetch(query).mapToListLocalDateTime(); }
	public List<Boolean>			fetchListAsBoolean(SelectQuery<?> query)			{ return fetch(query).mapToListBoolean(); }
	public <T extends Enum<T>> List<T> 		fetchListAsStringEnum(SelectQuery<?> query, Class<T> enumClass) { return fetch(query).mapToListStringEnum(enumClass); }
	public <T extends Enum<T>> List<T> 		fetchListAsOrdinalEnum(SelectQuery<?> query, Class<T> enumClass) { return fetch(query).mapToListOrdinalEnum(enumClass); }


	@Deprecated
	protected List<ResultRow<Object>> fetchListAsResults(Statement statement) throws SQLException{
		try(ResultSet resultSet = statement.executeForResultSet()) {
	        ResultSetMetaData metadata = resultSet.getMetaData();
	        
	        String[] columnNames = createColumnsArray(metadata);

			ResultTable<Object> resultTable = new ResultTable<>(this, columnNames, null);
			
	        
	        while(resultSet.next()) {	        	
	        	resultTable.addRow(extractRowValues(resultSet));
	        }
	        
	        return resultTable.getRows();
		}
	}

	// ==================== PROJECT METHODS ====================
	
	
	/*
	 * Clones the query, clears selects, and imports selects from result mapping, and returns a single value
	 */
	public <U> U projectTo(SelectQuery<?> query, Class<U> clazz) 	{ 
		SelectQuery<?> newQuery = query.clone().clearSelects().selectAll(clazz);
		return fetch(newQuery).mapTo(clazz); 
	}

	/*
	 * Clones the query, clears selects, and imports selects from result mapping, and returns a single optional value
	 */
	public <U> Optional<U> projectToOptional(SelectQuery<?> query, Class<U> clazz) 	{ 
		return Optional.ofNullable(projectTo(query, clazz)); 
	}

	/*
	 * Clones the query, clears selects, and imports selects from result mapping, and returns a list of the values
	 */
	public <U> List<U> projectToList(SelectQuery<?> query, Class<U> clazz) 	{ 
		SelectQuery<?> newQuery = query.clone().clearSelects().selectAll(clazz);
		return fetch(newQuery).mapToList(clazz); 
	}

	public <U> List<U> projectToList(SelectQuery<?> query, Entity<U> entity) {
		SelectQuery<?> newQuery = query.clone().clearSelects().selectAll(entity);
		return fetch(newQuery).mapToList(entity.entity);
	}
	
	/*
	 * Clones the query, selects only the attribute, and returns a single value
	 */

	public Object projectToAttribute(SelectQuery<?> query, Attribute attribute) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(attribute);
		return fetch(newQuery).mapToAttribute(attribute.entity, attribute.attribute);
	}
	public Object projectToAttribute(SelectQuery<?> query, Class<?> entity, String attributeName) { return projectToAttribute(query, Estivate.attribute(entity, attributeName)); }
	public Object projectToAttribute(SelectQuery<?> query, Entity<?> entity, String attributeName) { return projectToAttribute(query, Estivate.attribute(entity, attributeName)); }
	public <T, P> P projectToAttribute(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (P) projectToAttribute(query, Estivate.attribute(attributeGetter)); }
	/*
	 * Clones the query, selects only the attribute, and returns a single optional value
	 */
	public Optional<?> projectToAttributeOptional(SelectQuery<?> query, Attribute attribute) { return Optional.ofNullable(projectToAttribute(query, attribute.entity, attribute.attribute)); }
	public Optional<?>	projectToAttributeOptional(SelectQuery<?> query, Class<?> entity, String attributeName) { return projectToAttributeOptional(query, Estivate.attribute(entity, attributeName)); }
	public Optional<?>	projectToAttributeOptional(SelectQuery<?> query, Entity<?> entity, String attributeName) { return projectToAttributeOptional(query, Estivate.attribute(entity, attributeName)); }
	public <T, P> Optional<P> projectToAttributeOptional(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (Optional<P>) projectToAttributeOptional(query, Estivate.attribute(attributeGetter)); }


	/* 
	 * Clones the query, selects only the attribute, and returns a list of the values
	 */
	public List<?> projectToAttributeList(SelectQuery<?> query, Class<?> entity, String attributeName) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(entity, attributeName);
		return fetch(newQuery).mapToListAttribute(entity, attributeName);
	}
	public List<?> projectToAttributeList(SelectQuery<?> query, Entity<?> entity, String attributeName) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(entity, attributeName);
		return fetch(newQuery).mapToListAttribute(entity, attributeName);
	}

	public List<?> projectToAttributeList(SelectQuery<?> query, Attribute attribute) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(attribute);
		return fetch(newQuery).mapToListAttribute(attribute);
	}
	public <T, P> List<P> projectToAttributeList(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (List<P>) projectToAttributeList(query, Estivate.attribute(attributeGetter)); }

	/*
	 * Clones the query, selects only the attribute with distinct option, and returns a set of the values
	 */
	public Set<?> projectToAttributeSet(SelectQuery<?> query, Class<?> entity, String attributeName) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(entity, attributeName).distinct();
		return fetch(newQuery).mapToSetAttribute(entity, attributeName);
	}

	public Set<?> projectToAttributeSet(SelectQuery<?> query, Entity<?> entity, String attributeName) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(entity, attributeName).distinct();
		return fetch(newQuery).mapToSetAttribute(entity, attributeName);
	}

	public Set<?> projectToAttributeSet(SelectQuery<?> query, Attribute attribute) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(attribute).distinct();
		return fetch(newQuery).mapToSetAttribute(attribute);
	}

	public <T, P> Set<P> projectToAttributeSet(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (Set<P>) projectToAttributeSet(query, Estivate.attribute(attributeGetter)); }

	/*
	 * Clones the query, clears group bys, orders, and selects only the count, and returns a single value
	 */
	public Long projectToCount(SelectQuery<?> query) {
		return fetchSingleAsLong(query.clone()
			.clearSelects()
			.clearGroupBys()
			.clearOrderBys()
			.selectCountAs("count"));
	}

	/*
	 * Clones the query, clears group bys, orders, and selects only the count, and returns a single optionalvalue
	 */
	public Optional<Long> projectToCountOptional(SelectQuery<?> query) {
		return Optional.ofNullable(projectToCount(query));
	}

	/*
	 * Clones the query, clears group bys, orders, and selects only the count, and returns a single value
	 */
	public Long projectToCountDistinct(SelectQuery<?> query, Class<?> entity, String attributeName) {
		return fetchSingleAsLong(query.clone()
			.clearSelects()
			.clearGroupBys()
			.clearOrderBys()
			.selectCountDistinct(entity, attributeName, "count"));
	}
	public Long projectToCountDistinct(SelectQuery<?> query, Entity<?> entity, String attributeName) { return projectToCountDistinct(query, entity.entity, attributeName); }
	public Long projectToCountDistinct(SelectQuery<?> query, Attribute attribute) { return projectToCountDistinct(query, attribute.entity, attribute.attribute); }
	public <T, P> Long projectToCountDistinct(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return projectToCountDistinct(query, Estivate.attribute(attributeGetter)); }

	/*
	 * Clones the query, clears group bys, orders, and selects only the count, and returns a single optionalvalue
	 */
	public Optional<Long> projectToCountDistinctOptional(SelectQuery<?> query, Class<?> entity, String attributeName) {
		return Optional.ofNullable(projectToCountDistinct(query, entity, attributeName));
	}
	public Optional<Long> projectToCountDistinctOptional(SelectQuery<?> query, Entity<?> entity, String attributeName) { return Optional.ofNullable(projectToCountDistinct(query, entity, attributeName)); }
	public Optional<Long> projectToCountDistinctOptional(SelectQuery<?> query, Attribute attribute) { return Optional.ofNullable(projectToCountDistinct(query, attribute.entity, attribute.attribute)); }
	public <T, P> Optional<Long> projectToCountDistinctOptional(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return Optional.ofNullable(projectToCountDistinct(query, Estivate.attribute(attributeGetter))); }

	// ==================== AGGREGATION METHODS ====================
	
	public <T, U, V> Map<U, V> aggregateToMap(SelectQuery<T> query, Function<ResultRow<T>,U> uType, Function<ResultRow<T>,V> vType){
		List<ResultRow<T>> results = fetchListAsResults(query);
		Map<U, V> map = new LinkedHashMap<>();
		
		for(ResultRow<T> result : results){
			map.put(uType.apply(result), vType.apply(result));
		}

		return map;
	}

	public <T, U, V> Map<U, List<V>> aggregateToMapList(SelectQuery<T> query, Function<ResultRow<T>,U> uType, Function<ResultRow<T>,V> vType){
		List<ResultRow<T>> results = fetchListAsResults(query);
		Map<U, List<V>> map = new LinkedHashMap<>();
		
		for(ResultRow<T> result : results){
			map.computeIfAbsent(uType.apply(result), k -> new ArrayList<>()).add(vType.apply(result));
		}

		return map;
	}
	
	// ==================== PERSISTENCE METHODS ====================
		
	@SneakyThrows
	public <U> void insert(U object) {

		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){	
			
			preInsert(object);
			
			FieldUtils.invokeLifecycleMethods(object, javax.persistence.PrePersist.class);
			FieldUtils.invokeLifecycleMethods(object, jakarta.persistence.PrePersist.class);

			List<String> fieldValueList = new ArrayList<>();
			
			statement.appendQuery("INSERT INTO ")
					.appendQuery(nameMapper.toTableName(object.getClass()));
			
			
			for(Field field : FieldUtils.getEntityFields(object.getClass())) {
				field.setAccessible(true);
				// Skip Id, will be auto generated by db
				if(field.isAnnotationPresent(javax.persistence.Id.class) || field.isAnnotationPresent(jakarta.persistence.Id.class)) {
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
					long generatedId = rs.getLong(1);
					
					// Handle both primitive long and wrapper Long types
					if (field.getType() == long.class) {
						field.setLong(object, generatedId);
					} else if (field.getType() == Long.class) {
						field.set(object, generatedId);
					} else {
						throw new IllegalStateException("ID field must be of type long or Long, but found: " + field.getType());
					}
				}
				
				FieldUtils.invokeLifecycleMethods(object, javax.persistence.PostPersist.class);
				FieldUtils.invokeLifecycleMethods(object, jakarta.persistence.PostPersist.class);
				
			}
		}
	}


	@SneakyThrows
	public <T> void insert(Collection<T> entities) {
		if(entities != null) {
			for(T entity : entities) {
				insert(entity);
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
	public <U> void updateOrInsert(U object) {
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
			
	}

	@SneakyThrows
	public <U> void updateOrInsert(Collection<U> entities) {
		if(entities != null) {
			for(U entity : entities) {
				updateOrInsert(entity);
			}
		}
	}

	@SneakyThrows
	public <U> void update(U entity) {
		
		if(entity == null) {
			return;
		}

		FieldUtils.invokeLifecycleMethods(entity, javax.persistence.PreUpdate.class);
		FieldUtils.invokeLifecycleMethods(entity, jakarta.persistence.PreUpdate.class);
				
		Long id = null;
		Field idField = null;
		
		Set<Field> updatedFields = new LinkedHashSet<>(FieldUtils.getEntityFields(entity.getClass()));
		
		for(Field field : updatedFields) {
			field.setAccessible(true);
			
			if(field.isAnnotationPresent(javax.persistence.Id.class) || field.isAnnotationPresent(jakarta.persistence.Id.class)) {
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
			return;
		}
		
		if(idField == null || id == null || id == 0) {
			log.error("No id with value found, no update possible");
			return;
		}
	
		// TODO : replace by UpdateQuery
		
		UpdateQuery<U> query = Estivate.updateQuery((Class<U>) entity.getClass());
		
		for(Field field : updatedFields) {
			query.set(field.getName(), field.get(entity));
		}
		
		query.eq(idField.getName(), idField.get(entity));
		
		execute(query);
				
//		try(Connection connection = datasource.getConnection();
//			Statement statement = new Statement(this, connection); ){
//				
//				// 1. Create query
//				statement.appendQuery("UPDATE ")
//						.appendQuery(nameMapper.toTableName(entity.getClass()))
//						.appendQuery(" SET ");
//						
//				// 2. List updated fields
//				statement.appendQuery(updatedFields.stream().map(x-> nameMapper.mapDatabaseField(x.getName()) + " = ?").collect(Collectors.joining(", ")));
//				
//				for(Field field : updatedFields) {
//					statement.appendObjectAsValue(entity.getClass(), field.getName(), field.get(entity));
//				}
//				
//				
//				statement.appendQuery(" WHERE "+nameMapper.mapDatabaseField(idField.getName())+" = ?;");
//				statement.appendObjectAsValue(entity.getClass(), idField.getName(), idField.getLong(entity));
//
//				boolean check = statement.executeForValidation();
//
//		}

		FieldUtils.invokeLifecycleMethods(entity, javax.persistence.PostUpdate.class);
		FieldUtils.invokeLifecycleMethods(entity, jakarta.persistence.PostUpdate.class);
		
	}

	@SneakyThrows
	public <U> void update(Collection<U> entities) {
		if(entities == null) {
			return;
		}

		for(U entity : entities) {
			update(entity);
		}
		
	}

	
	@SneakyThrows
	public <U> void delete(U entity) {
		
		if(entity == null) {
			return;
		}

		DeleteQuery<U> query = Estivate.deleteQuery((Class<U>) entity.getClass());
		
		Field idField = FieldUtils.getIdField(entity.getClass());
		
		query.eq(idField.getName(), idField.get(entity));
		execute(query);
		
	}
	
	@SneakyThrows
	public <U> void delete(Collection<U> entities) {
		for(U entity : entities) {
			delete(entity);
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
			
			if(field.getDeclaredAnnotation(javax.persistence.Convert.class) != null) {
				returnClass = String.class;
			}
			else if(field.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
				returnClass = String.class;
			}
			
			if(returnClass.isEnum()) {
	
				if(field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null && field.getDeclaredAnnotation(javax.persistence.Enumerated.class).value() != null && field.getDeclaredAnnotation(javax.persistence.Enumerated.class).value() == javax.persistence.EnumType.STRING) {
					returnClass = String.class;
				}
				else if(field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class).value() != null && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class).value() == jakarta.persistence.EnumType.STRING) {
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
			
			if(field.isAnnotationPresent(javax.persistence.Id.class) || field.isAnnotationPresent(jakarta.persistence.Id.class)) {
				fieldCreation.append("PRIMARY KEY");
			}

			if(field.getDeclaredAnnotation(javax.persistence.GeneratedValue.class) != null) {
				javax.persistence.GeneratedValue generatedValue = field.getDeclaredAnnotation(javax.persistence.GeneratedValue.class);
				if(generatedValue.strategy() == javax.persistence.GenerationType.IDENTITY) {
					fieldCreation.append("AUTO_INCREMENT");
				}
			}
			else if(field.getDeclaredAnnotation(jakarta.persistence.GeneratedValue.class) != null) {
				jakarta.persistence.GeneratedValue generatedValue = field.getDeclaredAnnotation(jakarta.persistence.GeneratedValue.class);
				if(generatedValue.strategy() == jakarta.persistence.GenerationType.IDENTITY) {
					fieldCreation.append("AUTO_INCREMENT");
				}
			}
			
			fields.add(fieldCreation.toString());
		}
		
		String result = "CREATE TABLE "+nameMapper.toTableName(entityClass)+" ("+fields.stream().collect(Collectors.joining(", "))+")";
		
		try(Connection connection = datasource.getConnection(); 
			PreparedStatement statement = connection.prepareStatement(result);){
			return statement.execute();
		}
	}

	@SneakyThrows
	public boolean truncateTable(Class<?> entity) {
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){
			statement.appendQuery("TRUNCATE TABLE ").appendQuery(nameMapper.toTableName(entity));
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

			statement.appendQuery(nameMapper.toTableName(c)+columns.stream().collect(Collectors.joining(", ", "(", ")")));
			
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
				.appendQuery(nameMapper.toTableName(c));
			return statement.executeForValidation();
		}
	}
	
	
	
	public abstract List<TableIndex> listIndexes(Class<?> c);
	
	// ==================== MISC ====================

	@SneakyThrows
	public String queryAsString(Query<?,?> query) {
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection, preExecute(query));) {
			return statement.query();
		}
		
	}
	
}
