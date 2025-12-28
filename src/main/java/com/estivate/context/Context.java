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
	public <T> ResultTable fetch(SelectQuery<T> query){
		SelectQuery<T> finalQuery = (SelectQuery<T>) preExecute(query);
		
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection, finalQuery);
			ResultSet resultSet = statement.executeForResultSet()) {
			
			ResultTable resultTable = new ResultTable(this, createColumnsArray(resultSet.getMetaData()), finalQuery);
			
			while(resultSet.next()) {
	        	String[] values = extractRowValues(resultSet);
	        	resultTable.addRow(values);
	        }
			
			return resultTable;
		}
	}
	
	

	/*
	 * Clones the query, clears selects, and imports selects from result mapping, and returns a single value
	 */
	public <T> T fetchAsSingle(SelectQuery<?> query, Class<T> clazz) 	{ 
		SelectQuery<?> newQuery = query.clone().clearSelects().selectAll(clazz);
		return fetch(newQuery).asSingle(clazz); 
	}
	public <T> T fetchAsSingle(SelectQuery<?> query, Entity<T> entity) { return fetchAsSingle(query, entity); }
	public <E> E fetchSingle(SelectQuery<E> query) { return fetchAsSingle(query, query.getEntity()); }


	public <E> Optional<E> fetchOptional(SelectQuery<E> query) { return Optional.ofNullable(fetchSingle(query)); }
	public <T> Optional<T> fetchAsOptional(SelectQuery<?> query, Class<T> clazz) { return Optional.ofNullable(fetchAsSingle(query, clazz)); }
	public <T> Optional<T> fetchAsOptional(SelectQuery<?> query, Entity<T> entity) { return Optional.ofNullable(fetchAsSingle(query, entity)); }
	
	public <T> List<T> fetchAsList(SelectQuery<?> query, Entity<T> entity) {
		SelectQuery<?> newQuery = query.clone().clearSelects().selectAll(entity);
		return fetch(newQuery).asList(entity.entity);
	}
	public <T> List<T> fetchAsList(SelectQuery<?> query, Class<T> clazz) 	{ 
		SelectQuery<?> newQuery = query.clone().clearSelects().selectAll(clazz);
		return fetch(newQuery).asList(clazz); 
	}

	
		
	public <E> List<E> fetchList(SelectQuery<E> query) { return fetchAsList(query, query.getEntity()); }
	

	@Deprecated
	protected List<ResultRow> fetchListAsResults(Statement statement) throws SQLException{
		try(ResultSet resultSet = statement.executeForResultSet()) {
	        ResultSetMetaData metadata = resultSet.getMetaData();
	        
	        String[] columnNames = createColumnsArray(metadata);

			ResultTable resultTable = new ResultTable(this, columnNames, null);
			
	        
	        while(resultSet.next()) {	        	
	        	resultTable.addRow(extractRowValues(resultSet));
	        }
	        
	        return resultTable.getRows();
		}
	}


	
	/*
	 * Clones the query, selects only the attribute, and returns a single value
	 */
	public Object fetchAsSingle(SelectQuery<?> query, Attribute attribute) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(attribute);
		return fetch(newQuery).asSingle(attribute.entity, attribute.attribute);
	}
	public Object fetchAsSingle(SelectQuery<?> query, Class<?> entity, String attributeName) { return fetchAsSingle(query, Estivate.attribute(entity, attributeName)); }
	public Object fetchAsSingle(SelectQuery<?> query, Entity<?> entity, String attributeName) { return fetchAsSingle(query, Estivate.attribute(entity, attributeName)); }
	public <T, P> P fetchAsSingle(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (P) fetchAsSingle(query, Estivate.attribute(attributeGetter)); }

	/*
	 * Clones the query, selects only the attribute, and returns a single optional value
	 */
	public Optional<?> fetchAsOptional(SelectQuery<?> query, Attribute attribute) { return Optional.ofNullable(fetchAsSingle(query, attribute.entity, attribute.attribute)); }
	public Optional<?> fetchAsOptional(SelectQuery<?> query, Class<?> entity, String attributeName) { return fetchAsOptional(query, Estivate.attribute(entity, attributeName)); }
	public Optional<?> fetchAsOptional(SelectQuery<?> query, Entity<?> entity, String attributeName) { return fetchAsOptional(query, Estivate.attribute(entity, attributeName)); }
	public <T, P> Optional<P> fetchAsOptional(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (Optional<P>) fetchAsOptional(query, Estivate.attribute(attributeGetter)); }

	/* 
	 * Clones the query, selects only the attribute, and returns a list of the values
	 */
	public List<?> fetchAsList(SelectQuery<?> query, Attribute attribute) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(attribute);
		return fetch(newQuery).asList(attribute);
	}
	public List<?> fetchAsList(SelectQuery<?> query, Class<?> entity, String attributeName) { return fetchAsList(query, Estivate.attribute(entity, attributeName)); }
	public List<?> fetchAsList(SelectQuery<?> query, Entity<?> entity, String attributeName) { return fetchAsList(query, Estivate.attribute(entity, attributeName)); }
	public <T, P> List<P> fetchAsList(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (List<P>) fetchAsList(query, Estivate.attribute(attributeGetter)); }

	public List<?> fetchAsListDistinct(SelectQuery<?> query, Attribute attribute) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(attribute).distinct();
		return fetch(newQuery).asList(attribute);
	}
	public List<?> fetchAsListDistinct(SelectQuery<?> query, Class<?> entity, String attributeName) { return fetchAsListDistinct(query, Estivate.attribute(entity, attributeName)); }
	public List<?> fetchAsListDistinct(SelectQuery<?> query, Entity<?> entity, String attributeName) { return fetchAsListDistinct(query, Estivate.attribute(entity, attributeName)); }
	public <T, P> List<P> fetchAsListDistinct(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (List<P>) fetchAsListDistinct(query, Estivate.attribute(attributeGetter)); }

	/*
	 * Clones the query, selects only the attribute with distinct option, and returns a set of the values
	 */
	public Set<?> fetchAsSet(SelectQuery<?> query, Attribute attribute) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(attribute).distinct();
		return fetch(newQuery).asSetAttribute(attribute);
	}
	public Set<?> fetchAsSet(SelectQuery<?> query, Class<?> entity, String attributeName) { return fetchAsSet(query, Estivate.attribute(entity, attributeName)); }
	public Set<?> fetchAsSet(SelectQuery<?> query, Entity<?> entity, String attributeName) { return fetchAsSet(query, Estivate.attribute(entity, attributeName)); }
	public <T, P> Set<P> fetchAsSet(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (Set<P>) fetchAsSet(query, Estivate.attribute(attributeGetter)); }

	
	/*
	 * Clones the query, clears group bys, orders, and selects only the count, and returns a single value
	 */
	public Long fetchAsCount(SelectQuery<?> query) {
		return fetch(query.clone()
			.clearSelects()
			.clearGroupBys()
			.clearOrderBys()
			.limit(null)
			.offset(null)
			.selectCountAs("count")).asSingleLong();
	}

	/*
	 * Clones the query, clears group bys, orders, and selects only the count, and returns a single optional value
	 */
	public Optional<Long> fetchAsCountOptional(SelectQuery<?> query) {
		return Optional.ofNullable(fetchAsCount(query));
	}

	/*
	 * Clones the query, clears group bys, orders, and selects only the count distinct, and returns a single value
	 */
	public Long fetchAsCountDistinct(SelectQuery<?> query, Class<?> entity, String attributeName) {
		return fetch(query.clone()
			.clearSelects()
			.clearGroupBys()
			.clearOrderBys()
			.selectCountDistinct(entity, attributeName, "count")).asSingleLong();
	}
	public Long fetchAsCountDistinct(SelectQuery<?> query, Entity<?> entity, String attributeName) { return fetchAsCountDistinct(query, entity.entity, attributeName); }
	public Long fetchAsCountDistinct(SelectQuery<?> query, Attribute attribute) { return fetchAsCountDistinct(query, attribute.entity, attribute.attribute); }
	public <T, P> Long fetchAsCountDistinct(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return fetchAsCountDistinct(query, Estivate.attribute(attributeGetter)); }

	/*
	 * Clones the query, clears group bys, orders, and selects only the count distinct, and returns a single optional value
	 */
	public Optional<Long> fetchAsCountDistinctOptional(SelectQuery<?> query, Class<?> entity, String attributeName) {
		return Optional.ofNullable(fetchAsCountDistinct(query, entity, attributeName));
	}
	public Optional<Long> fetchAsCountDistinctOptional(SelectQuery<?> query, Entity<?> entity, String attributeName) { return Optional.ofNullable(fetchAsCountDistinct(query, entity, attributeName)); }
	public Optional<Long> fetchAsCountDistinctOptional(SelectQuery<?> query, Attribute attribute) { return Optional.ofNullable(fetchAsCountDistinct(query, attribute.entity, attribute.attribute)); }
	public <T, P> Optional<Long> fetchAsCountDistinctOptional(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return Optional.ofNullable(fetchAsCountDistinct(query, Estivate.attribute(attributeGetter))); }

	
	// ==================== AGGREGATION METHODS ====================
	
	public <T, U, V> Map<U, V> aggregateToMap(SelectQuery<T> query, Function<ResultRow,U> uType, Function<ResultRow,V> vType){
		List<ResultRow> results = fetch(query).getRows();
		Map<U, V> map = new LinkedHashMap<>();
		
		for(ResultRow result : results){
			map.put(uType.apply(result), vType.apply(result));
		}

		return map;
	}

	public <T, U, V> Map<U, List<V>> aggregateToMapList(SelectQuery<T> query, Function<ResultRow,U> uType, Function<ResultRow,V> vType){
		List<ResultRow> results = fetch(query).getRows();
		Map<U, List<V>> map = new LinkedHashMap<>();
		
		for(ResultRow result : results){
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
				U duplicatedEntity = fetchAsSingle(query, (Class<U>) entity.getClass());
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

			U duplicatedEntity = fetchAsSingle(query, (Class<U>) entity.getClass());
			
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
