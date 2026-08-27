package com.estivate.context;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Consumer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.destridon.iter8.Iter8;
import com.estivate.Entity;
import com.estivate.Entity.UpdateDate;
import com.estivate.Estivate;
import com.estivate.NameMapper;
import com.estivate.NameMapper.DefaultNameMapper;
import com.estivate.Statement;
import com.estivate.index.Annotations.ColumnDefaultValue;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.query.AlterQuery;
import com.estivate.query.Attribute;
import com.estivate.query.CreateQuery;
import com.estivate.query.CreateQuery.Index;
import com.estivate.query.DeleteQuery;
import com.estivate.query.InsertQuery;
import com.estivate.query.Join;
import com.estivate.query.Query;
import com.estivate.query.SelectQuery;
import com.estivate.query.UpdateQuery;
import com.estivate.reconciliation.ColumnModel;
import com.estivate.reconciliation.ColumnModel.EntityColumn;
import com.estivate.reconciliation.EntityModel;
import com.estivate.reconciliation.TableField;
import com.estivate.result.ResultRow;
import com.estivate.result.ResultTable;
import com.estivate.util.CachedEntity;
import com.estivate.util.Chronometer;
import com.estivate.util.FieldUtils;
import com.estivate.util.FieldUtils.AttributeGetter;
import com.estivate.util.Pair;

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
	public Consumer<InsertQuery<?>> insertInterceptor = null;
	public Consumer<AlterQuery<?>> alterInterceptor = null;
	
	// public Consumer<List<?>> updatePostConsumer = null;
	// public Consumer<List<?>> insertPostConsumer = null;
		
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

	private AlterQuery<?> preExecute(AlterQuery<?> query) {
		AlterQuery<?> clonedQuery = query.clone();
		if(alterInterceptor != null){
			alterInterceptor.accept(clonedQuery);
		}
		return clonedQuery;
	}

	
	/**
	 * Pre-processes an object before insert
	 */
	private <U> InsertQuery<U> preInsert(InsertQuery<U> query) {
		InsertQuery<U> clonedQuery = query.clone();
		if(insertInterceptor != null) {
			insertInterceptor.accept(clonedQuery);
		}
		return clonedQuery;
	}



	// ==================== EXECUTE METHODS ====================

	@SneakyThrows
	public Boolean execute(Query<?,?> query, Chronometer chronometer) {
		chronometer.step("execute::start");
		try(Connection connection = datasource.getConnection();
			Statement statement = queryAsStatement(preExecute(query), connection)) {
			chronometer.step("execute::getconnectionAndStatement");
			Boolean result = statement.executeForValidation();
			chronometer.step("execute::result");
			return result;

		}
	}

	public Boolean execute(Query<?,?> query) {
		return execute(query, new Chronometer("execute"));
	}

	@SneakyThrows
	public Boolean execute(AlterQuery<?> query) {

		if(query.getOperations() == null || query.getOperations().isEmpty()) {
			throw new RuntimeException("ALTER TABLE query must have at least one operation");
		}

		try(Connection connection = datasource.getConnection();
			Statement statement = queryAsStatement(query, connection)){
			return statement.executeForValidation();
		}

	}

	@SneakyThrows
	public Boolean execute(CreateQuery<?> query) {
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, datasource.getConnection())) {

			statement.appendQuery("CREATE TABLE");
			if(query.isIfNotExists()) {
				statement.appendQuery("IF NOT EXISTS");
			}
			statement.appendQuery(nameMapper.toTableName(query.getEntity()));
			
			statement.appendQuery(" (");
			for(Iter8<EntityColumn> entityColumns : Iter8.from(query.getColumns())) {
			
				EntityColumn entityColumn = entityColumns.getValue();
			
				TableField tableField = getTableField(entityColumn);
				statement.appendQuery(tableField.getName());
				statement.appendQuery(tableField.getType());

				if(tableField.getLength() != null) {
					statement.appendQuery("(" + tableField.getLength() + ")");
				}

				//statement.appendQuery(columnDefinition.getFullColumnType(this));
				// if (tableField.getCharset() != null) {
				// 	statement.appendQuery("CHARACTER SET");
				// 	statement.appendQuery(tableField.getCharset());
				// }
				// if (tableField.getCollation() != null) {
				// 	statement.appendQuery("COLLATE");
				// 	statement.appendQuery(tableField.getCollation());
				// }
				if (Boolean.FALSE.equals(entityColumn.isNullable())) {
					statement.appendQuery("NOT NULL");
				}
				if (entityColumn.getDefaultValue() != null) {
					statement.appendQuery("DEFAULT");
					if(entityColumn.getDefaultValue().startsWith("'") && entityColumn.getDefaultValue().endsWith("'")) {
						statement.appendQuery(entityColumn.getDefaultValue());
					}
					else {
						statement.appendQuery("'" + entityColumn.getDefaultValue() + "'");
					}
				}
				if (Boolean.TRUE.equals(entityColumn.isAutoIncrement())) {
					statement.appendQuery("AUTO_INCREMENT");
				}
				if (Boolean.TRUE.equals(entityColumn.isPrimaryKey())) {
					statement.appendQuery("PRIMARY KEY");
				}
				// if (entityColumn.getComment() != null) {
				// 	statement.appendQuery("COMMENT");
				// 	statement.appendQuery("'" + entityColumn.getComment().replace("'", "''") + "'");
				// }
				if(!entityColumns.isLast()){
					statement.appendQuery(",");
				}
			}
			
			for(Index index : query.getIndexes()) {
				
				statement.appendQuery(",");

				switch(index.getType() != null ? index.getType() : IndexType.DEFAULT) {
					case PRIMARY:
						statement.appendQuery("PRIMARY KEY");
						break;
					case UNIQUE:
						statement.appendQuery("UNIQUE INDEX");
						statement.appendQuery(nameMapper.mapIndex(index.getName(), index.getType(), index.getColumns()));
						break;
					case FULLTEXT:
						statement.appendQuery("FULLTEXT INDEX");
						statement.appendQuery(nameMapper.mapIndex(index.getName(), index.getType(), index.getColumns()));
						break;
					case DEFAULT:
					default:
						statement.appendQuery("INDEX");
						statement.appendQuery(nameMapper.mapIndex(index.getName(), index.getType(), index.getColumns()));
						break;
				}

				statement.appendQuery("(");
				statement.appendQuery(index.getColumns().stream()
						.map(col -> nameMapper.mapDatabaseField(col.getColumnName()) + (col.getLength() != null && col.getLength() != 0 ? "("+col.getLength()+")" : ""))
						.collect(Collectors.joining(", ")));

				statement.appendQuery(")");

			}
			statement.appendQuery(")");
			return statement.executeForValidation();
		}

		
	}

	@SneakyThrows
	public <T> void execute(InsertQuery<T> query, Chronometer chronometer) {

		if(query.getValues() == null || query.getValues().isEmpty()) {
			return;
		}
		
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){
	
			chronometer.step("executeInsert::start");
			query = preInsert(query);
			chronometer.step("executeInsert::preInsert");
			statement.appendQuery("INSERT INTO ", nameMapper.toTableName(query.getEntity()));
			chronometer.step("executeInsert::appendQuery");
			query.getFields().stream().forEach(x -> x.setAccessible(true));
			String columnsString = query.getFields().stream().map(x -> nameMapper.mapDatabaseField(x.getName())).collect(Collectors.joining(", "));
			String valuesString = query.getFields().stream().map(x -> "?").collect(Collectors.joining(", "));
			statement.appendQuery("(", columnsString, ") VALUES ");
			chronometer.step("executeInsert::appendQuery::values");
			for(T value : query.getValues()) {
				FieldUtils.invokeLifecycleMethods(value, javax.persistence.PrePersist.class);
				FieldUtils.invokeLifecycleMethods(value, jakarta.persistence.PrePersist.class);
			}
			chronometer.step("executeInsert::invokeLifecycleMethods");

			for(Iter8<T> valueIterator : Iter8.from(query.getValues())) {
				statement.appendQuery("(", valuesString, ")");
				for(Field field : query.getFields()) {
					statement.appendObjectAsValue(valueIterator.getValue().getClass(), field.getName(), field.get(valueIterator.getValue()));
				}
				if(!valueIterator.isLast()) {
					statement.appendQuery(",");
				}
			}

			chronometer.step("executeInsert::appendValues");
			
			try(ResultSet resultSet = statement.executeForGeneratedKeys()){
				// TODO : handle other types of generated keys

				chronometer.step("executeInsert::executeForGeneratedKeys");
				List<Long> generatedKeys = new ArrayList<>();
				while(resultSet.next()) {
					generatedKeys.add(resultSet.getLong(1));
				}
				chronometer.step("executeInsert::resultSetToGeneratedKeys");
				if(generatedKeys.size() == query.getValues().size() && query.getIdField() != null) {
					for(int i = 0; i < query.getValues().size(); i++) {
						if(query.getIdField().getType() == long.class) {
							query.getIdField().setLong(query.getValues().get(i), generatedKeys.get(i));
						} 
						else if(query.getIdField().getType() == Long.class) {
							query.getIdField().set(query.getValues().get(i), generatedKeys.get(i));
						} 
						else if(query.getIdField().getType() == int.class) {
							query.getIdField().setInt(query.getValues().get(i), generatedKeys.get(i).intValue());
						}
						else if(query.getIdField().getType() == Integer.class) {
							query.getIdField().set(query.getValues().get(i), generatedKeys.get(i).intValue());
						}
						else if(query.getIdField().getType() == short.class) {
							query.getIdField().setShort(query.getValues().get(i), generatedKeys.get(i).shortValue());
						}
						else if(query.getIdField().getType() == Short.class) {
							query.getIdField().set(query.getValues().get(i), generatedKeys.get(i).shortValue());
						}
						else {
							throw new IllegalStateException("ID field must be of type long or Long, but found: " + query.getIdField().getType());
						}
					}
				}
				chronometer.step("executeInsert::applyKey");

			}

			// Post Persist
			for(T value : query.getValues()) {
				FieldUtils.invokeLifecycleMethods(value, javax.persistence.PostPersist.class);
				FieldUtils.invokeLifecycleMethods(value, jakarta.persistence.PostPersist.class);
			}

			chronometer.step("executeInsert::postPersist");

		}
		
	}

	public <T> void execute(InsertQuery<T> query) {
		execute(query, new Chronometer("executeInsert"));
	}

	// ==================== PERSISTENCE METHODS ====================
		
	@SneakyThrows
	public <U> void insert(U object, Chronometer chronometer) {

		InsertQuery<U> insertQuery = Estivate.insertQuery((Class<U>) object.getClass());
		insertQuery.value(object);
		execute(insertQuery, chronometer);
			
	}

	public <U> void insert(U object) {
		insert(object, new Chronometer("insert"));
	}


	@SneakyThrows
	public <T> void insert(Collection<T> entities) {
		if(entities != null) {
			for(T entity : entities) {
				insert(entity);
			}
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
	
	public <E> E 			fetchSingle		(SelectQuery<E> query) { return fetchAsSingle(query, query.getEntity()); }
	public <E> Optional<E> 	fetchOptional	(SelectQuery<E> query) { return Optional.ofNullable(fetchSingle(query)); }
	public <E> List<E> 		fetchList		(SelectQuery<E> query) { return fetchAsList(query, query.getEntity()); }
	
	

	/*
	 * Clones the query, clears selects, and imports selects from result mapping, and returns a single value
	 */
	public <T> T fetchAsSingle(SelectQuery<?> query, Class<T> entity) 	{ 
		SelectQuery<?> newQuery = query.clone().clearSelects().selectAll(entity).limit(1);
		return fetch(newQuery).asSingle(entity); 
	}
	public <T> T fetchAsSingle(SelectQuery<?> query, Entity<T> entity) { 
		SelectQuery<?> newQuery = query.clone().clearSelects().selectAll(entity).limit(1);
		return fetch(newQuery).asSingle(entity); 
	}
	

	public <T> Optional<T> fetchAsOptional(SelectQuery<?> query, Class<T> entity) { return Optional.ofNullable(fetchAsSingle(query, entity)); }
	public <T> Optional<T> fetchAsOptional(SelectQuery<?> query, Entity<T> entity) { return Optional.ofNullable(fetchAsSingle(query, entity)); }
	
	public <T> List<T> fetchAsList(SelectQuery<?> query, Entity<T> entity) {
		SelectQuery<?> newQuery = query.clone().clearSelects().selectAll(entity);
		return fetch(newQuery).asList(entity);
	}
	public <T> List<T> fetchAsList(SelectQuery<?> query, Class<T> entity) 	{ 
		SelectQuery<?> newQuery = query.clone().clearSelects().selectAll(entity);
		return fetch(newQuery).asList(entity); 
	}

	
		
	

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
		SelectQuery<?> newQuery = query.clone().clearSelects().select(attribute).limit(1);
		return fetch(newQuery).asSingle(attribute);
	}
	public Object 	fetchAsSingle(SelectQuery<?> query, Class<?> entity, String attributeName) { return fetchAsSingle(query, Estivate.attribute(entity, attributeName)); }
	public Object 	fetchAsSingle(SelectQuery<?> query, Entity<?> entity, String attributeName) { return fetchAsSingle(query, Estivate.attribute(entity, attributeName)); }
	public <T, P> P fetchAsSingle(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (P) fetchAsSingle(query, Estivate.attribute(attributeGetter)); }
	public Object 	fetchAsSingle(SelectQuery<?> query, Class<?> entity, String attributeName, Attribute.Function function) { return fetchAsSingle(query, Estivate.attribute(entity, attributeName, function)); }
	public Object 	fetchAsSingle(SelectQuery<?> query, Entity<?> entity, String attributeName, Attribute.Function function) { return fetchAsSingle(query, Estivate.attribute(entity, attributeName, function)); }
	public <T, P> P fetchAsSingle(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return (P) fetchAsSingle(query, Estivate.attribute(attributeGetter, function)); }

	/*
	 * Clones the query, selects only the attribute, and returns a single optional value
	 */
	public 			Optional<?> fetchAsOptional(SelectQuery<?> query, Attribute attribute) { return Optional.ofNullable(fetchAsSingle(query, attribute)); }
	public 			Optional<?> fetchAsOptional(SelectQuery<?> query, Class<?> entity, String attributeName) { return fetchAsOptional(query, Estivate.attribute(entity, attributeName)); }
	public 			Optional<?> fetchAsOptional(SelectQuery<?> query, Entity<?> entity, String attributeName) { return fetchAsOptional(query, Estivate.attribute(entity, attributeName)); }
	public <T, P> 	Optional<P> fetchAsOptional(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (Optional<P>) fetchAsOptional(query, Estivate.attribute(attributeGetter)); }
	public 			Optional<?> fetchAsOptional(SelectQuery<?> query, Class<?> entity, String attributeName, Attribute.Function function) { return fetchAsOptional(query, Estivate.attribute(entity, attributeName, function)); }
	public 			Optional<?> fetchAsOptional(SelectQuery<?> query, Entity<?> entity, String attributeName, Attribute.Function function) { return fetchAsOptional(query, Estivate.attribute(entity, attributeName, function)); }
	public <T, P> 	Optional<P> fetchAsOptional(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return (Optional<P>) fetchAsOptional(query, Estivate.attribute(attributeGetter, function)); }
	
	/* 
	 * Clones the query, selects only the attribute, and returns a list of the values
	 */
	public List<?> fetchAsList(SelectQuery<?> query, Attribute attribute) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(attribute);
		return fetch(newQuery).asList(attribute);
	}
	public 			List<?> fetchAsList(SelectQuery<?> query, Class<?> entity, String attributeName) { return fetchAsList(query, Estivate.attribute(entity, attributeName)); }
	public 			List<?> fetchAsList(SelectQuery<?> query, Entity<?> entity, String attributeName) { return fetchAsList(query, Estivate.attribute(entity, attributeName)); }
	public <T, P> 	List<P> fetchAsList(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (List<P>) fetchAsList(query, Estivate.attribute(attributeGetter)); }
	public 			List<?> fetchAsList(SelectQuery<?> query, Class<?> entity, String attributeName, Attribute.Function function) { return fetchAsList(query, Estivate.attribute(entity, attributeName, function)); }
	public 			List<?> fetchAsList(SelectQuery<?> query, Entity<?> entity, String attributeName, Attribute.Function function) { return fetchAsList(query, Estivate.attribute(entity, attributeName, function)); }
	public <T, P> 	List<P> fetchAsList(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return (List<P>) fetchAsList(query, Estivate.attribute(attributeGetter, function)); }
	

	public List<?> fetchAsListDistinct(SelectQuery<?> query, Attribute attribute) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(attribute).distinct();
		return fetch(newQuery).asList(attribute);
	}
	public 			List<?> fetchAsListDistinct(SelectQuery<?> query, Class<?> entity, String attributeName) { return fetchAsListDistinct(query, Estivate.attribute(entity, attributeName)); }
	public 			List<?> fetchAsListDistinct(SelectQuery<?> query, Entity<?> entity, String attributeName) { return fetchAsListDistinct(query, Estivate.attribute(entity, attributeName)); }
	public <T, P> 	List<P> fetchAsListDistinct(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (List<P>) fetchAsListDistinct(query, Estivate.attribute(attributeGetter)); }
	public 			List<?> fetchAsListDistinct(SelectQuery<?> query, Class<?> entity, String attributeName, Attribute.Function function) { return fetchAsListDistinct(query, Estivate.attribute(entity, attributeName, function)); }
	public 			List<?> fetchAsListDistinct(SelectQuery<?> query, Entity<?> entity, String attributeName, Attribute.Function function) { return fetchAsListDistinct(query, Estivate.attribute(entity, attributeName, function)); }
	public <T, P> 	List<P> fetchAsListDistinct(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return (List<P>) fetchAsListDistinct(query, Estivate.attribute(attributeGetter, function)); }

	/*
	 * Clones the query, selects only the attribute with distinct option, and returns a set of the values
	 */
	public Set<?> fetchAsSet(SelectQuery<?> query, Attribute attribute) {
		SelectQuery<?> newQuery = query.clone().clearSelects().select(attribute).distinct();
		return fetch(newQuery).asSetAttribute(attribute);
	}
	public 			Set<?> fetchAsSet(SelectQuery<?> query, Class<?> entity, String attributeName) { return fetchAsSet(query, Estivate.attribute(entity, attributeName)); }
	public 			Set<?> fetchAsSet(SelectQuery<?> query, Entity<?> entity, String attributeName) { return fetchAsSet(query, Estivate.attribute(entity, attributeName)); }
	public <T, P> 	Set<P> fetchAsSet(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) { return (Set<P>) fetchAsSet(query, Estivate.attribute(attributeGetter)); }
	public 			Set<?> fetchAsSet(SelectQuery<?> query, Class<?> entity, String attributeName, Attribute.Function function) { return fetchAsSet(query, Estivate.attribute(entity, attributeName, function)); }
	public 			Set<?> fetchAsSet(SelectQuery<?> query, Entity<?> entity, String attributeName, Attribute.Function function) { return fetchAsSet(query, Estivate.attribute(entity, attributeName, function)); }
	public <T, P> 	Set<P> fetchAsSet(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter, Attribute.Function function) { return (Set<P>) fetchAsSet(query, Estivate.attribute(attributeGetter, function)); }
	
	
	/*
	 * Clones the query, clears group bys, orders, and selects only the count, and returns a single value
	 */
	public Long fetchCountAll(SelectQuery<?> query) {
		return fetch(query.clone()
			.clearSelects()
			.clearGroupBys()
			.clearOrderBys()
			.limit(null)
			.offset(null)
			.selectCountAll("count")).asSingleLong();
	}

	/*
	 * Clones the query, clears group bys, orders, and selects COUNT(DISTINCT attribute), and returns a single value
	 */
	public Long fetchCountDistinct(SelectQuery<?> query, Attribute attribute) {
		return fetch(query.clone()
			.clearSelects()
			.clearGroupBys()
			.clearOrderBys()
			.limit(null)
			.offset(null)
			.selectCountDistinct(attribute, "count")).asSingleLong();
	}

	public Long fetchCountDistinct(SelectQuery<?> query, Class<?> entity, String attributeName) {
		return fetchCountDistinct(query, Estivate.attribute(entity, attributeName));
	}

	public Long fetchCountDistinct(SelectQuery<?> query, Entity<?> entity, String attributeName) {
		return fetchCountDistinct(query, Estivate.attribute(entity, attributeName));
	}

	public <T, P> Long fetchCountDistinct(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) {
		return fetchCountDistinct(query, Estivate.attribute(attributeGetter));
	}

	/*
	 * Clones the query, clears group bys, orders, and selects only the count, and returns a single optional value
	 */
	public Optional<Long> fetchOptionalCountAll(SelectQuery<?> query) {
		return Optional.ofNullable(fetchCountAll(query));
	}

	public Optional<Long> fetchOptionalCountDistinct(SelectQuery<?> query, Attribute attribute) {
		return Optional.ofNullable(fetchCountDistinct(query, attribute));
	}

	public Optional<Long> fetchOptionalCountDistinct(SelectQuery<?> query, Class<?> entity, String attributeName) {
		return Optional.ofNullable(fetchCountDistinct(query, entity, attributeName));
	}

	public Optional<Long> fetchOptionalCountDistinct(SelectQuery<?> query, Entity<?> entity, String attributeName) {
		return Optional.ofNullable(fetchCountDistinct(query, entity, attributeName));
	}

	public <T, P> Optional<Long> fetchOptionalCountDistinct(SelectQuery<?> query, AttributeGetter<T, P> attributeGetter) {
		return Optional.ofNullable(fetchCountDistinct(query, attributeGetter));
	}

	
	
	// ==================== AGGREGATION METHODS ====================
	

	public <T> Map<Object, Object> fetchAsMap(SelectQuery<T> query, Attribute keyAttribute, Attribute valueAttribute){
		query.clone().clearSelects().select(keyAttribute).select(valueAttribute);
		return fetch(query).asMap(keyAttribute, valueAttribute);
	}

	public <T, A1E, A1T, A2E, A2T> Map<A1T, A2T> fetchAsMap(SelectQuery<T> query, AttributeGetter<A1E, A1T> attributeGetter, AttributeGetter<A2E, A2T> valueGetter){
		query.clone().clearSelects().select(attributeGetter).select(valueGetter);
		return fetch(query).asMap(attributeGetter, valueGetter);
	}

	public <T, AE, AT, C> Map<AT, C> fetchAsMap(SelectQuery<T> query, AttributeGetter<AE, AT> attributeGetter, Class<C> vClass){
		query.clone().clearSelects().select(attributeGetter).selectAll(vClass);
		return fetch(query).asMap(attributeGetter, vClass);
	}

	public <T, C1, C2> Map<C1, C2> fetchAsMap(SelectQuery<T> query, Class<C1> uClass, Class<C2> vClass){
		query.clone().clearSelects().selectAll(uClass).selectAll(vClass);
		return fetch(query).asMap(uClass, vClass);
	}

	public <T, C, AE, AT> Map<C, AT> fetchAsMap(SelectQuery<T> query, Class<C> uClass, AttributeGetter<AE, AT> valueGetter){
		query.clone().clearSelects().selectAll(uClass).select(valueGetter);
		return fetch(query).asMap(uClass, valueGetter);
	}

	public <T, A1E, A1T, A2E, A2T> Map<A1T, List<A2T>> fetchAsMapList(SelectQuery<T> query, AttributeGetter<A1E, A1T> attributeGetter, AttributeGetter<A2E, A2T> valueGetter){
		query.clone().clearSelects().select(attributeGetter).select(valueGetter);
		return fetch(query).asMapList(attributeGetter, valueGetter);
	}
	public <T, AE, AT, C> Map<AT, List<C>> fetchAsMapList(SelectQuery<T> query, AttributeGetter<AE, AT> attributeGetter, Class<C> vClass){
		query.clone().clearSelects().select(attributeGetter).selectAll(vClass);
		return fetch(query).asMapList(attributeGetter, vClass);
	}
	public <T, C, AE, AT> Map<C, List<AT>> fetchAsMapList(SelectQuery<T> query, Class<C> uClass, AttributeGetter<AE, AT> valueGetter){
		query.clone().clearSelects().selectAll(uClass).select(valueGetter);
		return fetch(query).asMapList(uClass, valueGetter);
	}
	public <T, C1, C2> Map<C1, List<C2>> fetchAsMapList(SelectQuery<T> query, Class<C1> uClass, Class<C2> vClass){
		query.clone().clearSelects().selectAll(uClass).selectAll(vClass);
		return fetch(query).asMapList(uClass, vClass);
	}

	public <T, A1E, A1T, A2E, A2T> Map<A1T, Set<A2T>> fetchAsMapSet(SelectQuery<T> query, AttributeGetter<A1E, A1T> attributeGetter, AttributeGetter<A2E, A2T> valueGetter){
		query.clone().clearSelects().select(attributeGetter).select(valueGetter);
		return fetch(query).asMapSet(attributeGetter, valueGetter);
	}
	public <T, AE, AT, C> Map<AT, Set<C>> fetchAsMapSet(SelectQuery<T> query, AttributeGetter<AE, AT> attributeGetter, Class<C> vClass){
		query.clone().clearSelects().select(attributeGetter).selectAll(vClass);
		return fetch(query).asMapSet(attributeGetter, vClass);
	}
	public <T, C, AE, AT> Map<C, Set<AT>> fetchAsMapSet(SelectQuery<T> query, Class<C> uClass, AttributeGetter<AE, AT> valueGetter){
		query.clone().clearSelects().selectAll(uClass).select(valueGetter);
		return fetch(query).asMapSet(uClass, valueGetter);
	}
	public <T, C1, C2> Map<C1, Set<C2>> fetchAsMapSet(SelectQuery<T> query, Class<C1> uClass, Class<C2> vClass){
		query.clone().clearSelects().selectAll(uClass).selectAll(vClass);
		return fetch(query).asMapSet(uClass, vClass);
	}

	


	
	

	

//	// Tries to find entity with same id, and if not found, tries to find entity with same unicity constraints
//	// Returns true if entity was merged to existing entity
//	@SneakyThrows
//	public <U> boolean merge(U entity){
//		// First try to find entity with same id
//		Field idField = FieldUtils.getIdField(entity.getClass());
//
//		if(idField != null){
//			idField.setAccessible(true);
//			if(idField.getLong(entity) != 0L) {
//				SelectQuery<U> query = Estivate.selectQuery((Class<U>) entity.getClass());
//				query.eq(entity.getClass(), idField.getName(), idField.getLong(entity));
//				U duplicatedEntity = fetchAsSingle(query, (Class<U>) entity.getClass());
//				if(duplicatedEntity != null) {
//					// Copy fields from result into object
//					for(Field field : FieldUtils.getEntityFields(entity.getClass())) {
//						field.setAccessible(true);
//						field.set(entity, field.get(duplicatedEntity));
//					}
//					return true;
//				}
//			}
//		}
//	
//		// Tries to merge with entity having same unicity constraints
//		IndexDiff indexDiff = new IndexDiff(this, entity.getClass());
//		for(TableIndex entityIndex : indexDiff.getEntityIndexes()){
//			if(entityIndex.type() != IndexType.UNIQUE) {
//				continue;
//			}
//
//			SelectQuery<U> query = Estivate.selectQuery((Class<U>) entity.getClass());
//			for(IndexColumn columnIndex : entityIndex.columns()) {
//				Field field = entity.getClass().getDeclaredField(columnIndex.value());
//				field.setAccessible(true);
//				Object value = field.get(entity);
//
//				query.eq(entity.getClass(), columnIndex.value(), value);
//			}
//
//			U duplicatedEntity = fetchAsSingle(query, (Class<U>) entity.getClass());
//			
//			if(duplicatedEntity != null) {
//				// Copy fields from result into object
//				for(Field field : FieldUtils.getEntityFields(entity.getClass())) {
//					field.setAccessible(true);
//					field.set(entity, field.get(duplicatedEntity));
//				}
//				return true;
//			}
//		}
//
//		return false;
//	}

//	// Try merging if entity has a valid id, otherwise insert
//	public <U> void mergeOrInsert(U entity){
//		if(!merge(entity)){
//			insert(entity);
//		}
//	}
		
	public <U> void updateOrInsert(U object){
		updateOrInsert(object, new Chronometer("updateOrInsert"));
	}
		
	@SneakyThrows
	public <U> void updateOrInsert(U object, Chronometer chronometer) {
		chronometer.step("updateOrInsert::start");
		Field idField = FieldUtils.getIdField(object.getClass());
		chronometer.step("updateOrInsert::idField");
		if(idField != null) {
			idField.setAccessible(true);
			chronometer.step("updateOrInsert::idField::accessible");
			if(idField.getLong(object) == 0L) {
				insert(object, chronometer);
				chronometer.step("updateOrInsert::insert");
			}
			else {
				update(object, chronometer);
				chronometer.step("updateOrInsert::update");
			}
			
			if(object instanceof CachedEntity) {
				((CachedEntity) object).saveState();
				chronometer.step("updateOrInsert::cachedEntity::saveState");
			}
		}
		else{
			insert(object, chronometer);
			chronometer.step("updateOrInsert::insert");
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
	public <U> void update(U entity, Chronometer chronometer) {
		
		if(entity == null) {
			return;
		}

		chronometer.step("update::start");
		FieldUtils.invokeLifecycleMethods(entity, javax.persistence.PreUpdate.class);
		FieldUtils.invokeLifecycleMethods(entity, jakarta.persistence.PreUpdate.class);
		chronometer.step("update::invokeLifecycleMethods");
		
		Long id = null;
		Field idField = null;
		
		Set<Field> updatedFields = new LinkedHashSet<>(FieldUtils.getEntityFields(entity.getClass()));
		chronometer.step("update::updatedFields");
		
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
		chronometer.step("update::updatedFields::set");
		
		if(entity instanceof CachedEntity) {
			updatedFields = new LinkedHashSet<>(((CachedEntity) entity).updatedFields());
		}
		chronometer.step("update::updatedFields::cachedEntity");
		
		// Remove id field from update set
		if(idField != null) {
			updatedFields.remove(idField);
		}
		chronometer.step("update::updatedFields::removeIdField");
		
		// No change to entity
		if(updatedFields.isEmpty()) {
			return;
		}
		
		if(idField == null || id == null || id == 0) {
			log.error("No id with value found, no update possible");
			return;
		}

	

		
		UpdateQuery<U> query = Estivate.updateQuery((Class<U>) entity.getClass());

		chronometer.step("update::query");
		for(Field field : updatedFields) {
			query.set(field.getName(), field.get(entity));
		}
		chronometer.step("update::query::set");
		query.eq(idField.getName(), idField.get(entity));
		chronometer.step("update::query::eq");
		execute(query, chronometer);
		chronometer.step("update::execute");

		FieldUtils.invokeLifecycleMethods(entity, javax.persistence.PostUpdate.class);
		FieldUtils.invokeLifecycleMethods(entity, jakarta.persistence.PostUpdate.class);
		chronometer.step("update::invokeLifecycleMethods::postUpdate");
	}

	public <U> void update(U entity) {
		update(entity, new Chronometer("update"));
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


//	public <U> boolean createTable(Class<U> entityClass) {
//
//		CreateQuery<U> query = Estivate.createQuery(entityClass);
//		execute(query);
//
//		TableIndex[] compositeIndex = entityClass.getDeclaredAnnotationsByType(TableIndex.class);
//		
//		for(TableIndex index : compositeIndex) {
//			String name = index.name().isEmpty() ? nameMapper.mapIndex(index) : index.name();
//			List<String> columns = Arrays.asList(index.columns()).stream().map(x -> nameMapper.mapDatabaseField(x.value())+ (x.length() > 0 ? "("+x.length()+")":"")).collect(Collectors.toList());
//			addIndex(entityClass, name, index.type(), columns);
//		}
//
//		return true;
//
//	}
	
	
//	@SneakyThrows
//	public <U> boolean createTableIfNotExists(Class<U> entityClass) {
//
//		List<String> existingTables = showTables();
//		String newTableName = nameMapper.toTableName(entityClass);
//		if(showTables().contains(nameMapper.toTableName(entityClass))) {
//			return false;
//		}
//		
//		
//		CreateQuery<U> query = Estivate.createQuery(entityClass);
//		query.ifNotExists();
//		execute(query);
//
//		List<TableIndex> tableIndexes = listIndexes(entityClass);
//
//		TableIndex[] entityIndexes = entityClass.getDeclaredAnnotationsByType(TableIndex.class);
//		
//		for(TableIndex index : entityIndexes) {
//			String name = index.name().isEmpty() ? nameMapper.mapIndex(index) : index.name();
//			List<String> columns = Arrays.asList(index.columns()).stream().map(x -> nameMapper.mapDatabaseField(x.value())+ (x.length() > 0 ? "("+x.length()+")":"")).collect(Collectors.toList());
//			
//			if(tableIndexes.stream().anyMatch(x -> x.name().equals(name))) {
//				continue;
//			}
//
//			addIndex(entityClass, name, index.type(), columns);
//		}
//
//		return true;
//	}

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
	
	
	
	


	public boolean indexEquals(TableIndex left, TableIndex right) {
		if(!left.name().toUpperCase().equals(right.name().toUpperCase())) {
			return false;
		}
		if(left.type() != right.type()) {
			return false;
		}
		return indexColumnsEquals(left, right);
	}

	public boolean indexColumnsEquals(TableIndex left, TableIndex right) {
		if(left.columns().length != right.columns().length) {
			return false;
		}
		for(int i = 0; i < left.columns().length; i++) {
			IndexColumn leftColumn = left.columns()[i];
			IndexColumn rightColumn = right.columns()[i];
			
			if(!leftColumn.value().equals(rightColumn.value())) {
				return false;
			}
			if(leftColumn.length() != 0 && rightColumn.length() != 0 && leftColumn.length() != rightColumn.length()) {
				return false;
			}
		}
		return true;
	}

	public void dropColumn(Class<?> c, String fieldName) throws Exception {
		String columnName = nameMapper.mapDatabaseField(fieldName);
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){
			statement.appendQuery("ALTER TABLE ").appendQuery(nameMapper.toTableName(c)).appendQuery(" DROP COLUMN ").appendQuery(columnName);
			statement.executeForValidation();
		}
	}



	public void changeColumn(Class<?> c, String fieldName, String columnType) throws Exception {
		String columnName = nameMapper.mapDatabaseField(fieldName);
		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){
			statement.appendQuery("ALTER TABLE ").appendQuery(nameMapper.toTableName(c)).appendQuery(" CHANGE COLUMN ").appendQuery(columnName).appendQuery(" ").appendQuery(columnType);
			statement.executeForValidation();
		}
	}
	
	
	// ==================== MISC ====================

	@SneakyThrows
	public String queryAsString(Query<?,?> query) {
		try(Connection connection = datasource.getConnection();
			Statement statement = queryAsStatement(preExecute(query), connection);) {
			return statement.query();
		}
	}

	
	public String queryAsString(AlterQuery<?> query) {
		try(Connection connection = datasource.getConnection();
			Statement statement = queryAsStatement(preExecute(query), connection);) {
			return statement.query();
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}


	public Statement queryAsStatement(Query<?,?> query, Connection connection) {
		Statement statement = new Statement(this, connection);
	
		// 1. Comments
		for(String comment : query.getComments()) {
			statement.appendQuery("-- "+comment+"\n");
		}
			
			// 2. Select or update or delete or alter table
		if(query instanceof SelectQuery) {
			statement.appendQuery("SELECT");
			
			if(((SelectQuery<?>) query).isDistinct()) {
				statement.appendQuery("DISTINCT");
			}
			
			if(((SelectQuery<?>) query).getSelects().isEmpty()) {
				((SelectQuery<?>) query).selectAll(query.getEntity());
			}
			
			//List<Select> selects = ((SelectQuery<?>) query).getSelects().stream().sorted(Comparator.comparing(x -> x.function == null || !x.function.equals(Estivate.Functions.distinct))).collect(Collectors.toList());
			
			statement.appendQuery(String.join(", ", ((SelectQuery<?>) query).getSelects().stream().map(x -> statement.selectString(x)).collect(Collectors.toList()))+"\n");
			
			statement.appendQuery("FROM");

		}
		else if(query instanceof UpdateQuery) {
			statement.appendQuery("UPDATE");
		}
		else if(query instanceof DeleteQuery) {
			statement.appendQuery("DELETE");
			if(!query.getJoins().isEmpty()){
				statement.appendEntity(query.getEntity());
			}
			statement.appendQuery("FROM");
		}

		statement.appendEntity(query.getEntity());
		
		
		
		// 4. Add Hint
		if(query instanceof SelectQuery && ((SelectQuery<?>) query).getIndexHint() != null && ((SelectQuery<?>) query).getIndexNames() != null && !((SelectQuery<?>) query).getIndexNames().isEmpty()) {
			statement.appendQuery(((SelectQuery<?>) query).getIndexHint()+ " INDEX ("+((SelectQuery<?>) query).getIndexNames().stream().collect(Collectors.joining(", "))+")");
		}
		
		// 5. Add Join (not applicable for ALTER TABLE or CREATE TABLE)
		for(Join join : query.getJoins()) {
			statement.appendJoin(join);
			statement.appendQuery("\n");
		}
	

		// 6. If update query, add set
		if(query instanceof UpdateQuery) {
			statement.appendQuery("SET");
			
			LinkedHashMap<Attribute, Object> attributeMap = ((UpdateQuery) query).getUpdates();
			boolean first = true;
			for(Map.Entry<Attribute, Object> entry : attributeMap.entrySet()) {

				// if not first, add comma
				if(first) {
					first = false;
				}
				else {
					statement.appendQuery(", ");
				}

				statement.appendAttributeAsParameter(entry.getKey());
				statement.appendQuery("=");
					statement.appendQuery(statement.writeParameter(entry.getKey().entity.entity, entry.getKey().attribute, entry.getValue()));
			}  
			
		}

		
		// 7. Add Where (not applicable for ALTER TABLE or CREATE TABLE)
		if(!query.getCriterions().isEmpty()) {
			statement.appendQuery("WHERE");
			statement.appendNodeToStatement(query, true);
		}
		
		// 8. Add Group by
		if(query instanceof SelectQuery && !((SelectQuery<?>) query).getGroupBys().isEmpty()) {
			List<Attribute> groups = ((SelectQuery<?>) query).getGroupBys();
			statement.appendQuery(groups.stream().map(x -> statement.groupString(x)).collect(Collectors.joining(", ", "GROUP BY ", ""))+"\n");
		}
		
		// 9. Add Having
		// Append having (if any)
		if(query instanceof SelectQuery && ((SelectQuery<?>) query).getHaving() != null) {
			statement.appendQuery("HAVING");
			statement.appendNodeToStatement(((SelectQuery<?>) query).getHaving(), true);
		}
		
		// 10. Append order (not applicable for ALTER TABLE or CREATE TABLE)
		if(!query.getOrders().isEmpty()) {
			statement.appendQuery(query.getOrders().stream().map(x -> statement.orderString(x)).collect(Collectors.joining(", ", "ORDER BY ", ""))+"\n");
		}
		
		// 11. Append limit & offset (not applicable for ALTER TABLE or CREATE TABLE)
		if(query.getLimit() != null) {
			statement.appendQuery("LIMIT "+query.getLimit()+"\n");
		}
		if(query.getOffset() != null) {
			statement.appendQuery("OFFSET "+ query.getOffset() +"\n");
		}
		

		return statement;
	}

	public Statement queryAsStatement(AlterQuery<?> query, Connection connection) {
	
		Statement statement = new Statement(this, connection);

		statement.appendQuery("ALTER TABLE");
		statement.appendQuery(nameMapper.toTableName(query.getEntity()));
		
		for(Iter8<AlterQuery.Operation> operationIterator : Iter8.from(query.getOperations())) {

			AlterQuery.Operation operation = operationIterator.getValue();
			
			if(operation instanceof AlterQuery.AddColumn) {
				AlterQuery.AddColumn addColumnOperation = (AlterQuery.AddColumn) operation;
				TableField tableField = getTableField(addColumnOperation.getColumnDefinition());
				statement.appendQuery("ADD COLUMN");
				statement.appendQuery(nameMapper.mapDatabaseField(addColumnOperation.getColumnName()));
				statement.appendQuery(tableField.type);
				if(tableField.getLength() != null) {
					statement.appendQuery("(" + tableField.getLength() + ")");
				}
				if(!tableField.isNullable()) {
					statement.appendQuery("NOT NULL");
				}
				if(tableField.isAutoIncrement()) {
					statement.appendQuery("AUTO_INCREMENT");
				}
				if(tableField.getDefaultValue() != null) {
					statement.appendQuery("DEFAULT "+defaultValueForType(tableField.getDefaultValue(), addColumnOperation.getColumnDefinition().getType()));
				}
			}
			else if(operation instanceof AlterQuery.DropColumn) {
				AlterQuery.DropColumn dropColumnOperation = (AlterQuery.DropColumn) operation;
				statement.appendQuery("DROP COLUMN");
				statement.appendQuery(nameMapper.mapDatabaseField(dropColumnOperation.getColumnName()));
			}
			else if(operation instanceof AlterQuery.ModifyColumn) {
				AlterQuery.ModifyColumn modifyColumnOperation = (AlterQuery.ModifyColumn) operation;
				statement.appendQuery("MODIFY COLUMN");
				TableField tableField = getTableField(modifyColumnOperation.getColumnDefinition());
				statement.appendQuery(nameMapper.mapDatabaseField(modifyColumnOperation.getColumnName()));
				statement.appendQuery(tableField.type);
				if(tableField.getLength() != null) {
					statement.appendQuery("(" + tableField.getLength() + ")");
				}
				if(!tableField.isNullable()) {
					statement.appendQuery("NOT NULL");
				}
				if(tableField.isAutoIncrement()) {
					statement.appendQuery("AUTO_INCREMENT");
				}
				if(tableField.getDefaultValue() != null) {
					statement.appendQuery("DEFAULT "+defaultValueForType(tableField.getDefaultValue(), modifyColumnOperation.getColumnDefinition().getType()));
				}
			}
			else if(operation instanceof AlterQuery.RenameColumn) {
				AlterQuery.RenameColumn renameColumnOperation = (AlterQuery.RenameColumn) operation;
				statement.appendQuery("RENAME COLUMN");
				statement.appendQuery(nameMapper.mapDatabaseField(renameColumnOperation.getColumnName()));
				statement.appendQuery(nameMapper.mapDatabaseField(renameColumnOperation.getNewColumnName()));
			}
			else if(operation instanceof AlterQuery.AddIndex) {
				AlterQuery.AddIndex addIndexOperation = (AlterQuery.AddIndex) operation;
				statement.appendQuery("ADD");
				if(addIndexOperation.getType() == IndexType.DEFAULT) {
					statement.appendQuery("INDEX");
				}
				else if(addIndexOperation.getType() == IndexType.FULLTEXT) {
					statement.appendQuery("FULLTEXT");
				}
				else if(addIndexOperation.getType() == IndexType.UNIQUE) {
					statement.appendQuery("UNIQUE");
				}
				
				statement.appendQuery(nameMapper.mapIndex(addIndexOperation.getIndexName(), addIndexOperation.getType(), addIndexOperation.getColumns()));
				statement.appendQuery("(");
				statement.appendQuery(addIndexOperation.getColumns().stream().map(col -> nameMapper.mapDatabaseField(col.getColumnName())+ ((col.getLength() != null && col.getLength() != 0) ? "("+col.getLength()+")" : "")).collect(Collectors.joining(", ")));
				statement.appendQuery(")");
			}
			else if(operation instanceof AlterQuery.DropIndex) {
				statement.appendQuery("DROP INDEX");
				AlterQuery.DropIndex dropIndexOperation = (AlterQuery.DropIndex) operation;
				statement.appendQuery(dropIndexOperation.getIndexName());
			}
			else if(operation instanceof AlterQuery.RenameTable) {
				statement.appendQuery("RENAME TABLE");
				AlterQuery.RenameTable renameTableOperation = (AlterQuery.RenameTable) operation;
				statement.appendQuery(renameTableOperation.getNewTableName());
			}
			else {
				throw new RuntimeException("Unsupported operation: " + operation.getClass().getName());
			}
			if(!operationIterator.isLast()) {
				statement.appendQuery(",");
			}
		}
		
		return statement;

	}
	
	public String defaultValueForType(String defaultValue, Class type) {
		if(defaultValue == null) {
			return null;
		}
		if(type == Boolean.class || type == boolean.class) {
			Boolean defaultValueBool = FieldUtils.parseBoolean(defaultValue);
			if(defaultValueBool == null) {
				return null;
			}
			else {
				return defaultValueBool.toString().toLowerCase();
			}
		}
		if(defaultValue.startsWith("'") && defaultValue.endsWith("'")) {
			return defaultValue;
		}
		return "'"+defaultValue+"'";
		
	}

	abstract public ColumnModel.ColumnFormat getColumnFormat(ColumnModel.EntityColumn entityColumn);



	public static ColumnModel.EntityColumn getEntityColumn(Field entityField) {
		ColumnModel.EntityColumn entityColumn = new ColumnModel.EntityColumn();
		entityColumn.setName(entityField.getName());
		entityColumn.setType(entityField.getType());

		// Primary key
		if(entityField.isAnnotationPresent(javax.persistence.Id.class) || entityField.isAnnotationPresent(jakarta.persistence.Id.class)){
			entityColumn.setPrimaryKey(true);
		}

		if(FieldUtils.isAutoIncrement(entityField)) {
			entityColumn.setAutoIncrement(true);
		}

		entityColumn.setNullable(FieldUtils.isNullable(entityField));

		javax.persistence.Column javaxColumn = entityField.getDeclaredAnnotation(javax.persistence.Column.class);
		jakarta.persistence.Column jakartaColumn = entityField.getDeclaredAnnotation(jakarta.persistence.Column.class);
		if (javaxColumn != null || jakartaColumn != null) {
			String columnDef = javaxColumn != null ? javaxColumn.columnDefinition() : jakartaColumn.columnDefinition();
			Integer columnLength = javaxColumn != null ? javaxColumn.length() : jakartaColumn.length();
			entityColumn.setDesignedType(columnDef);
			entityColumn.setDesignedLength(columnLength);
		}

		if (entityField.getDeclaredAnnotation(javax.persistence.Convert.class) != null || entityField.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
			entityColumn.setType(String.class);
		}

		// Handle enums
		else if (entityField.getType().isEnum()) {
			if (FieldUtils.isEnumeratedAsString(entityField)) {
				String enumValues = Arrays.stream(entityField.getType().getEnumConstants())
						.map(c -> "'" + ((Enum<?>) c).name() + "'")
						.collect(Collectors.joining(","));
				entityColumn.setDesignedType("ENUM(" + enumValues + ")");
				entityColumn.setDesignedLength(null);
			}
			else{
				entityColumn.setDesignedType("TINYINT");
			}
		}
		
		ColumnDefaultValue columnDefaultValue = entityField.getDeclaredAnnotation(ColumnDefaultValue.class);
		if(columnDefaultValue != null) {
			entityColumn.setDefaultValue(columnDefaultValue.value());
		}

		

		return entityColumn;
	}

	public TableField getTableField(ColumnModel.EntityColumn entityColumn) {
		
		ColumnModel.ColumnFormat columnFormat = getColumnFormat(entityColumn);
		
		return TableField.builder()
			.name(nameMapper.mapDatabaseField(entityColumn.getName()))
			.type(columnFormat.getType())
			.length(columnFormat.getLength())
			.nullable(entityColumn.isNullable())
			.defaultValue(entityColumn.getDefaultValue())
			.autoIncrement(entityColumn.isAutoIncrement())
			.build();
		
	}

	public TableField getTableField(Field entityField) {
		return getTableField(getEntityColumn(entityField));
	}

    public EntityModel scanDatabaseTable(Class<?> c) {

        List<TableField> fields = listFields(nameMapper.toTableName(c));
        List<TableIndex> indexes = listIndexes(c);
        
        return EntityModel.builder()
                .tableName(nameMapper.toTableName(c))
                .fields(fields)
                .indexes(indexes)
                .build();
        
    }
    
    public abstract List<TableField> listFields(String tableName);
    public abstract List<TableIndex> listIndexes(Class<?> entity);
    
	
	/**
     * Extracts length from SQL type (e.g., VARCHAR(255) -> 255)
     */
	protected Integer extractLength(String sqlType) {
		if (sqlType == null || !sqlType.contains("(")) {
			return null;
		}
		try {
			int start = sqlType.indexOf("(") + 1;
			int end = sqlType.indexOf(")");
			if (end > start) {
				String lengthStr = sqlType.substring(start, end);
				// Handle cases like DECIMAL(10,2)
				if (lengthStr.contains(",")) {
					lengthStr = lengthStr.split(",")[0];
				}
				return Integer.parseInt(lengthStr.trim());
			}
		} catch (NumberFormatException e) {
			// Ignore parsing errors
		}
		return null;
	}
	
	protected Pair<String, Integer> parseColumnType(String columnType){
	
//		RegexBuilder regex = RegexFactory.regexBuilder();
//		regex
//			.unique(RegexFactory.sequenceGroup().setGroupType(Group.GroupType.Capturing).setName("type")
//				.some(CharacterClass.Alphabetic))
//			.any(CharacterClass.Space)
//			.optional(RegexFactory.sequenceGroup()
//				.unique("\\(")
//				.unique(RegexFactory.sequenceGroup().some(RegexFactory.classMatch(CharacterClass.Numeric)).setGroupType(Group.GroupType.Capturing).setName("length"))
//				.optional(
//					RegexFactory.sequenceGroup()
//						.unique(",")
//						.unique(RegexFactory.sequenceGroup().some(RegexFactory.classMatch(CharacterClass.Numeric)).setGroupType(Group.GroupType.Capturing))
//					)
//				.unique("\\)"));
//		
//		RegexMatcher regexMatcher = RegexFactory.regexMatcher(regex, columnType);
//		regexMatcher.find();
////		String match1 = regexMatcher.getMatch(0).group;
////		String match2 = regexMatcher.getMatch(1).group;
////		String match3 = regexMatcher.getMatch(2).group;
//		
//		String type = regexMatcher.getMatch("type").group;
//		String length = regexMatcher.getMatch("length").group;
		
		// For enum, keep all in type
		if(columnType.toUpperCase().startsWith("ENUM")) {
			return new Pair<String, Integer>(columnType, null);
		}
		Pattern reg = Pattern.compile("([a-zA-Z]+)\\s*(\\(([0-9]+)(,([0-9]+))?\\))?");
		Matcher matcher = reg.matcher(columnType);
		if(matcher.find()) {
			return new Pair<String, Integer>(matcher.group(1), matcher.group(3) == null ? null : Integer.parseInt(matcher.group(3)));
		}
		return null;
		
		
		
		
		
	}




}
