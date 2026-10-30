package com.estivate;

import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Entity.SubQueryEntity;
import com.estivate.context.Context;
import com.estivate.query.Aggregator;
import com.estivate.query.Attribute;
import com.estivate.query.Criterion;
import com.estivate.query.AlterQuery;
import com.estivate.query.CreateQuery;
import com.estivate.query.DeleteQuery;
import com.estivate.query.EstivateNode;
import com.estivate.query.Join;
import com.estivate.query.Keyword;
import com.estivate.query.Query;
import com.estivate.query.Query.Order;
import com.estivate.query.SelectQuery;
import com.estivate.query.UpdateQuery;
import com.estivate.util.Chronometer;
import com.estivate.util.ClassMeta;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


/*
 * Wrapper for java.sql.PreparedStatement
 * doesn't hold any connection, it just helps using statement
 */
@ToString
@Slf4j
public class Statement implements AutoCloseable{

	@Getter final Context context;
	final Connection connection;
	
	StringBuilder query = new StringBuilder();
	List<Object> parameters = new ArrayList<>();
	
	PreparedStatement statement = null;
	/** When true, {@link #close()} must not close {@link #statement} — Context owns the cache. */
	private boolean cachedPreparedStatement = false;
	
	public Statement(Context context, Connection connection){
		this.context = context;
		this.connection = connection;
	}
	
	public Statement(Context context, Connection connection, String query) {
		this(context, connection);
		this.query = new StringBuilder(query);
	}


	public Statement(Context context, Connection connection, Query<?,?> query) {

		this.context = context;
		this.connection = connection;

		if(query instanceof UpdateQuery
				&& query.getComments().isEmpty()
				&& query.getJoins().isEmpty()
				&& query.getOrders().isEmpty()
				&& query.getLimit() == null
				&& query.getOffset() == null) {
			appendSimpleUpdate((UpdateQuery<?>) query);
			return;
		}

		// 1. Comments (often a stack trace for slow-query diagnosis)
		for(String comment : query.getComments()) {
			appendQuery("-- "+comment+"\n");
		}
		
		// 2. Select or update or delete or alter table
		if(query instanceof SelectQuery) {
			appendQuery("SELECT");
			
			if(((SelectQuery<?>) query).isDistinct()) {
				appendQuery("DISTINCT");
			}
			
			if(((SelectQuery<?>) query).getSelects().isEmpty()) {
				((SelectQuery<?>) query).selectAll(query.getEntity());
			}
			
			//List<Select> selects = ((SelectQuery<?>) query).getSelects().stream().sorted(Comparator.comparing(x -> x.function == null || !x.function.equals(Estivate.Functions.distinct))).collect(Collectors.toList());
			
			appendQuery(String.join(", ", ((SelectQuery<?>) query).getSelects().stream().map(this::selectString).collect(Collectors.toList()))+"\n");
			
			appendQuery("FROM");

		}
		else if(query instanceof UpdateQuery) {
			appendQuery("UPDATE");
		}
		else if(query instanceof DeleteQuery) {
			appendQuery("DELETE");
			if(!query.getJoins().isEmpty()){
				appendEntity(query.getEntity());
			}
			appendQuery("FROM");
		}

		appendEntity(query.getEntity());
		
		
		
		// 4. Add Hint
		if(query instanceof SelectQuery && ((SelectQuery<?>) query).getIndexHint() != null && ((SelectQuery<?>) query).getIndexNames() != null && !((SelectQuery<?>) query).getIndexNames().isEmpty()) {
			appendQuery(((SelectQuery<?>) query).getIndexHint()+ " INDEX ("+((SelectQuery<?>) query).getIndexNames().stream().collect(Collectors.joining(", "))+")");
		}
		
		// 5. Add Join (not applicable for ALTER TABLE or CREATE TABLE)
		for(Join join : query.getJoins()) {
			appendJoin(join);
			appendQuery("\n");
		}
	

		// 6. If update query, add set
		if(query instanceof UpdateQuery) {
			appendQuery("SET");
			
			LinkedHashMap<Attribute, Object> attributeMap = ((UpdateQuery) query).getUpdates();
			boolean first = true;
			for(Map.Entry<Attribute, Object> entry : attributeMap.entrySet()) {

				// if not first, add comma
				if(first) {
					first = false;
				}
				else {
					appendQuery(", ");
				}

				appendQuery(assignmentTarget(entry.getKey()));
				appendQuery("=");
	  			appendQuery(writeParameter(entry.getKey(), entry.getValue()));
			}  
			
		}
		

		
		// 7. Add Where (not applicable for ALTER TABLE or CREATE TABLE)
		if(!query.getCriterions().isEmpty()) {
        	appendQuery("WHERE");
        	appendNodeToStatement(query, true);
        }
		
		// 8. Add Group by
		if(query instanceof SelectQuery && !((SelectQuery<?>) query).getGroupBys().isEmpty()) {
			List<Attribute> groups = ((SelectQuery<?>) query).getGroupBys();
			appendQuery(groups.stream().map(x -> groupString(x)).collect(Collectors.joining(", ", "GROUP BY ", ""))+"\n");
		}
		
		// 9. Add Having
		// Append having (if any)
		if(query instanceof SelectQuery && ((SelectQuery<?>) query).getHaving() != null) {
			appendQuery("HAVING");
			appendNodeToStatement(((SelectQuery<?>) query).getHaving(), true);
		}
		
		// 10. Append order (not applicable for ALTER TABLE or CREATE TABLE)
		if(!query.getOrders().isEmpty()) {
			appendQuery(query.getOrders().stream().map(x -> orderString(x)).collect(Collectors.joining(", ", "ORDER BY ", ""))+"\n");
		}
		
		// 11. Append limit & offset (not applicable for ALTER TABLE or CREATE TABLE)
		if(query.getLimit() != null) {
			appendQuery("LIMIT "+query.getLimit()+"\n");
		}
		if(query.getOffset() != null) {
			appendQuery("OFFSET "+ query.getOffset() +"\n");
		}
	}

	/**
	 * Hot path for entity updates: no joins/comments/limit. Builds SQL from the same
	 * {@link UpdateQuery} model as the general renderer, with fewer {@link #appendQuery} calls.
	 */
	private void appendSimpleUpdate(UpdateQuery<?> updateQuery) {
		Entity<?> entity = updateQuery.getEntity();
		this.query.append("UPDATE ").append(context.mappedTableName(entity.entity)).append(" SET ");
		boolean first = true;
		for(Map.Entry<Attribute, Object> entry : updateQuery.getUpdates().entrySet()) {
			if(!first) {
				this.query.append(" , ");
			}
			first = false;
			Attribute attribute = entry.getKey();
			this.query.append(assignmentTarget(attribute)).append(" = ");
			// writeParameter handles literals, Attribute RHS, and SelectQuery subqueries
			this.query.append(writeParameter(attribute, entry.getValue()));
		}
		if(!updateQuery.getCriterions().isEmpty()) {
			this.query.append(" WHERE ");
			appendSimpleWhere(updateQuery);
		}
	}

	private String assignmentTarget(Attribute attribute) {
		if(context.dialect.qualifyUpdateTarget()) {
			return context.mappedTableAttribute(attribute.entity, attribute.attribute);
		}
		return context.mappedDatabaseField(attribute.attribute);
	}

	private void appendSimpleWhere(Query<?,?> query) {
		List<EstivateNode> criterions = query.getCriterions();
		for(int i = 0; i < criterions.size(); i++) {
			if(i > 0) {
				this.query.append(' ').append(query.getGroupType()).append(' ');
			}
			appendSimpleCriterion(criterions.get(i));
		}
	}

	private void appendSimpleCriterion(EstivateNode node) {
		if(node instanceof Criterion.Operator) {
			Criterion.Operator operator = (Criterion.Operator) node;
			this.query.append(context.mappedTableAttribute(operator.attribute.entity, operator.attribute.attribute));
			this.query.append(' ').append(operator.type.symbol).append(' ');
			this.query.append(writeParameter(operator.attribute, operator.value));
			return;
		}
		appendNodeToStatement(node, false);
	}
	
	public Statement appendQuery(String queryContent) {
		int length = query.length();
		if(length > 0 && !queryContent.equals(")")) {
			char last = query.charAt(length - 1);
			if(last != ' ' && last != '(') {
				query.append(' ');
			}
		}
		query.append(queryContent);
		return this;
	}

	public Statement appendQuery(String... queryContents) {
		for(String queryContent : queryContents) {
			appendQuery(queryContent);
		}
		return this;
	}

	public Statement appendAttributeAsParameter(Attribute attribute){
		
		//TODO : handle alias ?

		if(attribute.function != null && attribute.function.equals(Estivate.Functions.count)
				&& (attribute.entity == null || attribute.entity.entity == null)
				&& attribute.attribute == null) {
			appendQuery("COUNT(*)");
		}
		else if(attribute.function != null) {
			appendQuery(attribute.function.render(context.mappedTableAttribute(attribute.entity, attribute.attribute)));
		}
		else {
			appendQuery(context.mappedTableAttribute(attribute.entity, attribute.attribute));
		}

		return this;
	}

	
	public String query() {
		return query.toString();
	}

	
	public Statement ensureParameterCapacity(int capacity) {
		if(parameters instanceof ArrayList) {
			((ArrayList<Object>) parameters).ensureCapacity(capacity);
		}
		return this;
	}

	public Statement appendObjectAsValue(Class<?> entity, String fieldName, Object parameter) {
		parameters.add(compileObject(entity, fieldName, parameter));
		return this;
	}

	public Statement appendObjectAsValue(Field field, Object parameter) {
		parameters.add(compileObject(field, parameter));
		return this;
	}
	
	// private Statement appendParameterAsValue(Class<?> entity, String attribute, Object parameter) {
	// 	appendQuery(writeParameter(entity, attribute, parameter));		
	// 	return this;
	// }

	private Statement appendParameterAsValue(Attribute attribute, Object parameter) {
		appendQuery(writeParameter(attribute, parameter));
		return this;
	}

	public String writeParameter(Attribute attribute, Object parameter) {
		if(parameter instanceof Attribute) {
			Attribute valueAttribute = (Attribute) parameter;
			String attributeField = valueAttribute.entity == null
				? valueAttribute.attribute
				: context.mappedTableAttribute(valueAttribute.entity, valueAttribute.attribute);
			if(valueAttribute.function != null) {
				return valueAttribute.function.render(attributeField);
			}
			return attributeField;
		}
		if(parameter instanceof SelectQuery) {
			SelectQuery<?> selectQuery = (SelectQuery<?>) parameter;
			Statement subStatement = new Statement(context, connection, selectQuery);
			parameters.addAll(subStatement.parameters);
			return "(" + subStatement.query() + ")";
		}
		if(attribute != null && attribute.field != null) {
			appendObjectAsValue(attribute.field, parameter);
			return "?";
		}
		Class<?> entityClass = attribute != null && attribute.entity != null ? attribute.entity.entity : null;
		String fieldName = attribute != null ? attribute.attribute : null;
		appendObjectAsValue(entityClass, fieldName, parameter);
		return "?";
	}
	
	public String writeParameter(Class<?> entity, String field, Object parameter) {
		
		if(parameter instanceof Attribute) {
			return writeParameter((Attribute) null, parameter);
		}
		else if(parameter instanceof SelectQuery){
			return writeParameter((Attribute) null, parameter);
		}
		else {
			appendObjectAsValue(entity, field, parameter);
			return "?";
		}
			
	}
	

	public boolean executeForValidation() throws SQLException{
		return execute(connection, Chronometer.of("execute", context.tracePerformances), false);
	}

	public boolean executeForValidation(Chronometer chronometer) throws SQLException{
		return execute(connection, chronometer, false);
	}


	public ResultSet executeForGeneratedKeys(Chronometer chronometer) throws SQLException{
		if(statement == null) {
			execute(connection, chronometer, true);
		}
		return statement.getGeneratedKeys();
	}

	public ResultSet executeForGeneratedKeys() throws SQLException{
		return executeForGeneratedKeys(Chronometer.of("executeForGeneratedKeys", context.tracePerformances));
	}
	
	public ResultSet executeForResultSet() throws SQLException {
		return executeForResultSet(Chronometer.of("execute", context.tracePerformances));
	}

	public ResultSet executeForResultSet(Chronometer chronometer) throws SQLException {
		if(statement == null) {
			prepareAndBind(connection, chronometer, false);
			ResultSet resultSet = statement.executeQuery();
			chronometer.step("jdbc::executeQuery");
			return resultSet;
		}
		return statement.getResultSet();
	}
	


	private boolean execute(Connection connection, Chronometer chronometer, boolean returnGeneratedKeys) throws SQLException {
		prepareAndBind(connection, chronometer, returnGeneratedKeys);
		try {
			statement.executeUpdate();
			chronometer.step("jdbc::execute");
			return true;
		}
		catch(Exception e) {
			log.error("Error executing statement : "+query, e);
			throw e;
		}
	}

	private void prepareAndBind(Connection connection, Chronometer chronometer, boolean returnGeneratedKeys) throws SQLException {
		if(query.length() == 0) {
			return;
		}
		String queryString = query.toString();
		chronometer.step("jdbc::toString");

		try {
			cachedPreparedStatement = false;
			PreparedStatement cached = context.prepareCachedStatement(connection, queryString, returnGeneratedKeys);
			if(cached != null) {
				statement = cached;
				cachedPreparedStatement = true;
			}
			if(statement == null) {
				statement = returnGeneratedKeys
					? connection.prepareStatement(queryString, java.sql.Statement.RETURN_GENERATED_KEYS)
					: connection.prepareStatement(queryString);
			}
			chronometer.step("jdbc::prepare");

			for(int i = 0; i < parameters.size(); i++) {
				Object object = parameters.get(i);
				int index = i + 1;

				if(object == null) {
					statement.setObject(index, null);
				}
				else if(object instanceof String) {
					statement.setString(index, (String) object);
				}
				else if(object instanceof Integer) {
					statement.setInt(index, (Integer) object);
				}
				else if(object instanceof Long) {
					statement.setLong(index, (Long) object);
				}
				else if(object instanceof Boolean) {
					statement.setBoolean(index, (Boolean) object);
				}
				else if(object instanceof Float) {
					statement.setFloat(index, (Float) object);
				}
				else if(object instanceof Double) {
					statement.setDouble(index, (Double) object);
				}
				else if(object instanceof BigDecimal) {
					statement.setBigDecimal(index, (BigDecimal) object);
				}
				else if(object instanceof java.util.Date) {
					statement.setTimestamp(index, new Timestamp(((java.util.Date) object).getTime()));
				}
				else if(object instanceof java.sql.Date) {
					statement.setTimestamp(index, new Timestamp(((java.sql.Date) object).getTime()));
				}
				else if(object instanceof java.sql.Time) {
					statement.setTimestamp(index, new Timestamp(((java.sql.Time) object).getTime()));
				}
				else if(object instanceof java.sql.Timestamp) {
					statement.setTimestamp(index, (java.sql.Timestamp) object);
				}
				else if(object instanceof LocalDateTime) {
					statement.setTimestamp(index, Timestamp.valueOf((LocalDateTime) object));
				}
				else if(object instanceof Instant) {
					statement.setTimestamp(index, Timestamp.from((Instant) object));
				}
				else if(object instanceof java.time.LocalDate) {
					statement.setTimestamp(index, Timestamp.valueOf(((java.time.LocalDate) object).atStartOfDay()));
				}
				else if(object instanceof java.time.LocalTime) {
					statement.setTimestamp(index, Timestamp.valueOf(((java.time.LocalTime) object).atDate(java.time.LocalDate.now())));
				}
				else {
					log.error("Cannot map object of type "+object.getClass());
				}
			}
			chronometer.step("jdbc::bind");
		}
		catch(Exception e) {
			log.error("Error preparing statement : "+query, e);
			throw e;
		}
	}

	public void appendJoin(Join join) {

		appendQuery(join.joinType.toString());
		appendQuery("JOIN");

		if(join.rightEntity instanceof SubQueryEntity<?>) {
			appendQuery("(");
			Statement subStatement = new Statement(context, connection, ((SubQueryEntity<?>) join.rightEntity).query);
			appendQuery(subStatement.query());
			appendQuery(") AS ");
			appendQuery(join.rightEntity.alias);
			parameters.addAll(subStatement.parameters);
			
		}
		else{
			appendQuery(context.mappedTableName(join.rightEntity.entity));
			if(join.rightEntity.alias != null) { appendQuery(join.rightEntity.alias);}
			if(join.indexHint != null && join.indexNames != null && !join.indexNames.isEmpty()) {
				appendQuery(join.indexHint.toString()+ " INDEX ("+join.indexNames.stream().collect(Collectors.joining(", "))+")");
			}
		}
		appendQuery("ON");
		appendNodeToStatement(join.joiningCriterion, false);		
		
	}

	public void appendEntity(Entity<?> entity) {

		if(entity instanceof SubQueryEntity<?>){

			appendQuery("(");
			Statement subStatement = new Statement(context, connection, ((SubQueryEntity<?>) entity).query);
			appendQuery(subStatement.query());
			appendQuery(") AS ");
			parameters.addAll(subStatement.parameters);
			if(entity.alias != null) {
				appendQuery(entity.alias);
			}

		}
		else{
			appendQuery(context.mappedTableName(entity.entity));
			if(entity.alias != null) {
				appendQuery(entity.alias);
			}
		}
	}
	
	public String orderString(Order order) {

		StringBuilder sb = new StringBuilder();
	
		if(order.function != null) {
			sb.append(order.function.render(context.mappedTableAttribute(order.entity, order.attribute)));
		}
		else {
			sb.append(context.mappedTableAttribute(order.entity, order.attribute));
		}
		sb.append(order.direction != null ? " " + order.direction.toString().toUpperCase() : "");
	
		return sb.toString();
		
	}
		
	
	public String groupString(Attribute attribute) {
		// TODO : handle attribute function level
		return context.mappedTableAttribute(attribute.entity, attribute.attribute);
	}
	
	public String selectString(Attribute attribute) {

		if(attribute instanceof Attribute.AttributeWindow) {
			StringBuilder sb = new StringBuilder();
			Attribute.AttributeWindow attributeWindow = (Attribute.AttributeWindow) attribute;
			sb.append(attributeWindow.function.render(attributeWindow.attribute)).append(" OVER (");

			if(attributeWindow.partitionBy != null) {
				sb.append(" PARTITION BY ").append(attributeWindow.partitionBy.attribute);
			}
			if(attributeWindow.orderBy != null) {
				sb.append(" ORDER BY ").append(attributeWindow.orderBy.attribute);
			}
			sb.append(")");
			if(attributeWindow.alias != null) {
				sb.append(" AS ").append(context.mappedEntityField(attributeWindow.alias));
			}
			return sb.toString();
		}

		if(attribute.function != null && attribute.function.equals(Estivate.Functions.count) && (attribute.entity == null || attribute.entity.entity == null)) {
			return "COUNT(*)" + aliasClause(attribute.alias);
		}
		else if (attribute.function != null) {
			return attribute.function.render(context.mappedTableAttribute(attribute.entity, attribute.attribute)) + aliasClause(attribute.alias);
		}

		String alias = attribute.alias != null && !attribute.alias.isEmpty()
				? context.mappedEntityField(attribute.alias)
				: context.nameMapper.toEntityNameAttribute(attribute.entity, attribute.attribute);
		return context.mappedTableAttribute(attribute.entity, attribute.attribute) + " as " + context.dialect.quoteIdentifier(alias);
	}

	private String aliasClause(String alias) {
		if(alias == null || alias.isEmpty()) {
			return "";
		}
		return " as " + context.dialect.quoteIdentifier(context.mappedEntityField(alias));
	
	}

	
	public void appendNodeToStatement(EstivateNode node, boolean rootNode) {
		
		if(node instanceof Aggregator) {
			Aggregator aggregator = (Aggregator) node;

			List<EstivateNode> criterions = aggregator.getCriterions()
				.stream()
				.filter(x -> x != null && !x.isEmpty())
				.collect(Collectors.toList());

				

			if(!rootNode && criterions.size() > 1) {
				appendQuery("("); 
			}
			for(int i = 0; i < criterions.size(); i++) {
				if(i > 0) {
					appendQuery(aggregator.getGroupType().toString());
				}
				appendNodeToStatement(criterions.get(i), false);
			}
			if(!rootNode && criterions.size() > 1) {
				appendQuery(")");
			}
			
		}
		else if(node instanceof Criterion.Operator) {
			Criterion.Operator operator = (Criterion.Operator) node;
			appendAttributeAsParameter(operator.attribute);
			appendQuery(operator.type.symbol);
			appendParameterAsValue(operator.attribute, operator.value);
		}
		else if(node instanceof Criterion.In) {
			Criterion.In in = (Criterion.In) node;
			appendAttributeAsParameter(in.attribute);
			appendQuery("IN (");
			appendQuery(in.getValues().stream().map(x -> writeParameter(in.attribute, x)).collect(Collectors.joining(", ")));
			appendQuery(")");
		}
		else if(node instanceof Criterion.NotIn) {
			Criterion.NotIn in = (Criterion.NotIn) node;
			appendAttributeAsParameter(in.attribute);
			appendQuery("NOT IN (");
			appendQuery(in.getValues().stream().map(x -> writeParameter(in.attribute, x)).collect(Collectors.joining(", ")));
			appendQuery(")");
		}
		else if(node instanceof Criterion.Between) {
			Criterion.Between between = (Criterion.Between) node;
			appendAttributeAsParameter(between.attribute);
			appendQuery("BETWEEN");
			appendParameterAsValue(between.attribute, between.min);
			appendQuery("AND");
			appendParameterAsValue(between.attribute, between.max);
		}
		else if(node instanceof Criterion.NullCheck) {
			Criterion.NullCheck nullcheck = (Criterion.NullCheck) node;
			appendAttributeAsParameter(nullcheck.attribute);
			appendQuery(nullcheck.isNull ? "IS NULL":"IS NOT NULL");
		}
		else if(node instanceof Criterion.MatchAgainst) {
			
			Criterion.MatchAgainst matchAgainst = (Criterion.MatchAgainst) node;
			if(!matchAgainst.inclusive){
				appendQuery("NOT");
			}
			appendQuery("MATCH" + context.mappedTableAttribute(matchAgainst.attribute.entity, matchAgainst.attribute.attribute));
			appendQuery("AGAINST(");
			appendParameterAsValue(matchAgainst.attribute, matchAgainst.value);
			appendQuery(")");
			
		}
		else if(node instanceof Criterion.Regexp) {
			Criterion.Regexp regexp = (Criterion.Regexp) node;
			appendAttributeAsParameter(regexp.attribute);
			appendQuery("REGEXP '"+regexp.pattern+"'");
		}
		else if(node instanceof Criterion.NativeCriterion) {
			Criterion.NativeCriterion nativeCriterion = (Criterion.NativeCriterion) node;
			appendAttributeAsParameter(nativeCriterion.attribute);
			appendQuery(nativeCriterion.criterion);
		}
		else if(node instanceof Criterion.InSubQuery) {
			Criterion.InSubQuery subQuery = (Criterion.InSubQuery) node;
			appendAttributeAsParameter(subQuery.attribute);
			appendQuery(subQuery.include ? "IN" : "NOT IN");
			Statement subStatement = new Statement(context, connection, subQuery.subQuery);
			appendQuery("("+subStatement.query()+")");
			parameters.addAll(subStatement.parameters);
		}
		else if(node instanceof Criterion.ExistsSubQuery){
			Criterion.ExistsSubQuery subQuery = (Criterion.ExistsSubQuery) node;
			Statement subStatement = new Statement(context, connection, subQuery.subQuery);
			appendQuery(subQuery.include ? "EXISTS": "NOT EXISTS");
			appendQuery("("+subStatement.query()+")");
			parameters.addAll(subStatement.parameters);
		}
		else if(node instanceof Keyword) {
			appendQuery(((Keyword) node).getValue().toString().toLowerCase());
		}
		else {
			throw new RuntimeException("Node type not supported : "+node.getClass());
		}
		
	}
	
	
	Object compileObject(Class<?> entity, String attribute, Object value) {
		if(entity == null || attribute == null) {
			return value;
		}

		try {
			Field field = ClassMeta.findField(entity, attribute);
			if(field == null) {
				return value;
			}
			return compileObject(field, value);
		}
		catch(Exception e) {
			log.error("Exception while trying to map field "+entity.getSimpleName()+"."+attribute, e);
		}
		
		log.warn("Could not determine type of field "+entity.getSimpleName()+"."+attribute);
		
		return compileGenericType(value);

	}

	Object compileObject(Field field, Object value) {
		if(field == null) {
			return value;
		}
		try {
			ClassMeta.FieldBinding binding = ClassMeta.fieldBinding(field);
			if(binding == null || !binding.needsConversion()) {
				return value;
			}
			return binding.toDatabaseColumn(value);
		}
		catch(Exception e) {
			log.error("Exception while trying to map field "+field.getDeclaringClass().getSimpleName()+"."+field.getName(), e);
			return compileGenericType(value);
		}
	}
	
	String compileGenericType(Object value) {
		if(value instanceof String) {
			
			return "'"+((String) value)
					.replace("\\", "\\\\")
					//.replace("'", "\'\'")
					.replace("\"", "\"\"")
					.replace(":", "\\:")
					+"'";
		}
		else if(value instanceof java.util.Date) {
			return "\"" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format((java.util.Date) value) + "\""; 
		}
		else if(value instanceof Instant) {
			return "\"" + new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(Timestamp.from((Instant) value)) + "\"";
		}
		else if(value instanceof Boolean) {
			return (boolean) value ? "1":"0";
		}
		else {
			return value.toString();
		}
	}
	
	String compileAttribute(Class<?> entity, String attribute, Object value) {
		return compileGenericType(compileObject(entity, attribute, value));
	}

	@Override
	public void close() throws Exception {
		// Connection lifecycle is owned by Context.ConnectionLease / Spring DataSourceUtils.
		// Cached insert PreparedStatements stay open until the connection session ends.
		if(statement != null && !cachedPreparedStatement) {
			statement.close();
		}
		statement = null;
		cachedPreparedStatement = false;
	}
	

}
