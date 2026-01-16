package com.estivate;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.estivate.Entity.SubQueryEntity;
import com.estivate.context.Context;
import com.estivate.query.Aggregator;
import com.estivate.query.Attribute;
import com.estivate.query.Criterion;
import com.estivate.query.AlterQuery;
import com.estivate.query.DeleteQuery;
import com.estivate.query.EstivateNode;
import com.estivate.query.Join;
import com.estivate.query.Keyword;
import com.estivate.query.Query;
import com.estivate.query.Query.Order;
import com.estivate.query.SelectQuery;
import com.estivate.query.UpdateQuery;
import com.estivate.util.FieldUtils;

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
		
		// 1. Comments
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
		else if(query instanceof AlterQuery) {
			appendQuery("ALTER TABLE");
		}
		
		// 3. Append entity
		appendEntity(query.getEntity());
		
		
		
		// 4. Add Hint
		if(query instanceof SelectQuery && ((SelectQuery<?>) query).getIndexHint() != null && ((SelectQuery<?>) query).getIndexNames() != null && !((SelectQuery<?>) query).getIndexNames().isEmpty()) {
			appendQuery(((SelectQuery<?>) query).getIndexHint()+ " INDEX ("+((SelectQuery<?>) query).getIndexNames().stream().collect(Collectors.joining(", "))+")");
		}
		
		// 5. Add Join
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

				appendAttributeAsParameter(entry.getKey());
				appendQuery("=");
	  			appendQuery(writeParameter(entry.getKey().entity.entity, entry.getKey().attribute, entry.getValue()));
			}  
			
		}
		
		// 6b. If alter table query, add operations
		if(query instanceof AlterQuery) {
			AlterQuery<?> alterQuery = (AlterQuery<?>) query;
			List<AlterQuery.Operation> operations = alterQuery.getOperations();
			
			if(operations.isEmpty()) {
				throw new RuntimeException("ALTER TABLE query must have at least one operation");
			}
			
			boolean first = true;
			for(AlterQuery.Operation operation : operations) {
				if(!first) {
					appendQuery(", ");
				}
				first = false;
				
				// Use polymorphism - each operation knows how to render itself
				operation.render(context, this);
			}
		}
		
		// 7. Add Where (not applicable for ALTER TABLE)
		if(!(query instanceof AlterQuery) && !query.getCriterions().isEmpty()) {
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
		
		
		// 10. Append order
		if(!query.getOrders().isEmpty()) {
			appendQuery(query.getOrders().stream().map(x -> orderString(x)).collect(Collectors.joining(", ", "ORDER BY ", ""))+"\n");
		}
		
		// 11. Append limit & offset
		if(query.getLimit() != null) {
			appendQuery("LIMIT "+query.getLimit()+"\n");
		}
		if(query.getOffset() != null) {
			appendQuery("OFFSET "+ query.getOffset() +"\n");
		}
		
	}
	
	public Statement appendQuery(String queryContent) {
		if(query.length() > 0 && !Arrays.asList(" ", "(").contains(query.substring(query.length() - 1)) && !queryContent.equals(")")) {
			query.append(" ");
		}
		query.append(queryContent);
		return this;
	}

	public Statement appendAttributeAsParameter(Attribute attribute){
		
		//TODO : handle alias ?

		if(attribute.function != null) {
			appendQuery(attribute.function.render(context.nameMapper.toTableNameAttribute(attribute.entity, attribute.attribute)));
		}
		else {
			appendQuery(context.nameMapper.toTableNameAttribute(attribute.entity, attribute.attribute));
		}

		return this;
	}

	
	public String query() {
		return query.toString();
	}

	
	public Statement appendObjectAsValue(Class<?> entity, String fieldName, Object parameter) {
		parameters.add(compileObject(entity, fieldName, parameter));
		return this;
	}
	
	// private Statement appendParameterAsValue(Class<?> entity, String attribute, Object parameter) {
	// 	appendQuery(writeParameter(entity, attribute, parameter));		
	// 	return this;
	// }

	private Statement appendParameterAsValue(Attribute attribute, Object parameter) {
		appendQuery(writeParameter(attribute.entity.entity, attribute.attribute, parameter));
		return this;
	}
	
	private String writeParameter(Class<?> entity, String field, Object parameter) {
		
		if(parameter instanceof Attribute) {
			Attribute attribute = (Attribute) parameter;
			String attributeField = attribute.entity == null ? attribute.attribute : context.nameMapper.toTableNameAttribute(attribute.entity, attribute.attribute);
			if(attribute instanceof Attribute){
				Attribute Attribute = (Attribute) attribute;
				if(Attribute.function != null) {
					return Attribute.function.render(attributeField);
				}
			}
			return attributeField;
		}
		else if(parameter instanceof SelectQuery){
			SelectQuery<?> selectQuery = (SelectQuery<?>) parameter;
			Statement subStatement = new Statement(context, connection, selectQuery);
			parameters.addAll(subStatement.parameters);
			return "("+subStatement.query()+")";
		}
		else {
			appendObjectAsValue(entity, field, parameter);
			return "?";
		}
			
	}
	

	public boolean executeForValidation() throws SQLException{
		return execute(connection);
	}


	public ResultSet executeForGeneratedKeys() throws SQLException{
		if(statement == null) {
			execute(connection);
		}
		return statement.getGeneratedKeys();
	}
	
	public ResultSet executeForResultSet() throws SQLException {
		if(statement == null) {
			execute(connection);
		}
		return statement.getResultSet();
	}
	
	private boolean execute(Connection connection) throws SQLException {
		
		String queryString = query.toString();
		if(StringUtils.isBlank(queryString)) {
			return true;
		}
		
		try{
			statement = connection.prepareStatement(queryString, java.sql.Statement.RETURN_GENERATED_KEYS);

			for(int i = 0; i < parameters.size(); i++) {
				
				Object object = parameters.get(i);
				
				if(object instanceof String) {
					String s = (String) object;
					statement.setString(i+1, s);
				}
				else if(object instanceof Integer) {
					Integer n = (Integer) object;
					statement.setInt(i+1, n);
				}
				else if(object instanceof Long) {
					Long l = (Long) object;
					statement.setLong(i+1, l);
				}
				else if(object instanceof Float) {
					Float f = (Float) object;
					statement.setFloat(i+1, f);
				}
				else if(object instanceof Double) {
					Double f = (Double) object;
					statement.setDouble(i+1, f);
				}
				else if(object instanceof Boolean) {
					Boolean b = (Boolean) object;
					statement.setBoolean(i+1, b);
				}
				else if(object instanceof java.util.Date) {
					java.util.Date d = (java.util.Date) object;
					statement.setTimestamp(i+1, new Timestamp(d.getTime()));
				}
				else if(object instanceof java.sql.Date) {
					java.sql.Date d = (java.sql.Date) object;
					statement.setTimestamp(i+1, new Timestamp(d.getTime()));
				}
				else if(object instanceof java.sql.Time) {
					java.sql.Time t = (java.sql.Time) object;
					statement.setTimestamp(i+1, new Timestamp(t.getTime()));
				}
				else if(object instanceof java.sql.Timestamp) {
					java.sql.Timestamp ts = (java.sql.Timestamp) object;
					statement.setTimestamp(i+1, ts);
				}
				else if(object instanceof LocalDateTime) {
					LocalDateTime ldt = (LocalDateTime) object;
					statement.setTimestamp(i+1, Timestamp.valueOf(ldt));
				}
				else if(object instanceof java.time.LocalDate) {
					java.time.LocalDate ld = (java.time.LocalDate) object;
					statement.setTimestamp(i+1, Timestamp.valueOf(ld.atStartOfDay()));
				}
				else if(object instanceof java.time.LocalTime) {
					java.time.LocalTime lt = (java.time.LocalTime) object;
					statement.setTimestamp(i+1, Timestamp.valueOf(lt.atDate(java.time.LocalDate.now())));
				}
				else if(object == null) {
					statement.setObject(i+1, null);
				}
				else {
					log.error("Cannot map object of type "+object.getClass());
				}

			}
		
			return statement.execute();
		}
		catch(Exception e) {
			log.error("Error executing statement : "+query, e);
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
			appendQuery(context.nameMapper.toTableName(join.rightEntity.entity));
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
			appendQuery(context.nameMapper.toTableName(entity.entity));
			if(entity.alias != null) {
				appendQuery(entity.alias);
			}
		}
	}
	
	public String orderString(Order order) {

		StringBuilder sb = new StringBuilder();
	
		if(order.function != null) {
			sb.append(order.function.render(context.nameMapper.toTableNameAttribute(order.entity, order.attribute)));
		}
		else {
			sb.append(context.nameMapper.toTableNameAttribute(order.entity, order.attribute));
		}
		sb.append(order.direction != null ? " " + order.direction.toString().toUpperCase() : "");
	
		return sb.toString();
		
	}
		
	
	public String groupString(Attribute attribute) {
		// TODO : handle attribute function level
		return context.nameMapper.toTableNameAttribute(attribute.entity, attribute.attribute);
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
				sb.append(" AS ").append(context.nameMapper.mapEntityField(attributeWindow.alias));
			}
			return sb.toString();
		}

		if(attribute.function != null && attribute.function.equals(Estivate.Functions.count) && (attribute.entity == null || attribute.entity.entity == null)) {
			return "COUNT(*)"+(attribute.alias != null ? " as `"+context.nameMapper.mapEntityField(attribute.alias)+"`" : "");
		}
		else if (attribute.function != null) {
			return attribute.function.render(context.nameMapper.toTableNameAttribute(attribute.entity, attribute.attribute))+(attribute.alias != null ? " as `"+context.nameMapper.mapEntityField(attribute.alias)+"`" : "");
		}

		return context.nameMapper.toTableNameAttribute(attribute.entity, attribute.attribute)+" as `"+(attribute.alias != null ? context.nameMapper.mapEntityField(attribute.alias) : context.nameMapper.toEntityNameAttribute(attribute.entity, attribute.attribute))+"`";
	
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
			appendQuery(in.getValues().stream().map(x -> writeParameter(in.attribute.entity.entity, in.attribute.attribute, x)).collect(Collectors.joining(", ")));
			appendQuery(")");
		}
		else if(node instanceof Criterion.NotIn) {
			Criterion.NotIn in = (Criterion.NotIn) node;
			appendAttributeAsParameter(in.attribute);
			appendQuery("NOT IN (");
			appendQuery(in.getValues().stream().map(x -> writeParameter(in.attribute.entity.entity, in.attribute.attribute, x)).collect(Collectors.joining(", ")));
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
			appendQuery("MATCH" + context.nameMapper.toTableNameAttribute(matchAgainst.attribute.entity, matchAgainst.attribute.attribute));
			appendQuery("AGAINST(");
			appendParameterAsValue(matchAgainst.attribute, matchAgainst.value);
			appendQuery(")");
			
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
		try {
			Field field = FieldUtils.findField(entity, attribute);
			
			Type fieldType = field.getType();
			
			// Convert annotation takes priority
			if(field.getDeclaredAnnotation(javax.persistence.Convert.class) != null) {
				javax.persistence.Convert convertAnnotation = field.getDeclaredAnnotation(javax.persistence.Convert.class);
				Object converter = convertAnnotation.converter().getConstructor().newInstance();
				if(converter instanceof javax.persistence.AttributeConverter) {
					javax.persistence.AttributeConverter attributeConverter = (javax.persistence.AttributeConverter) converter;
					Object convertedValue = attributeConverter.convertToDatabaseColumn(value);
					return convertedValue;
				}
			}
			else if(field.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
				jakarta.persistence.Convert convertAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Convert.class);
				Object converter = convertAnnotation.converter().getConstructor().newInstance();
				if(converter instanceof jakarta.persistence.AttributeConverter) {
					jakarta.persistence.AttributeConverter attributeConverter = (jakarta.persistence.AttributeConverter) converter;
					Object convertedValue = attributeConverter.convertToDatabaseColumn(value);
					return convertedValue;
				}
			}
			// Then check if enum and with annotation
			if(fieldType instanceof Class && ((Class<?>) fieldType).isEnum()) {
				if(value == null) {
					return null;
				}
				
				if(field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null && field.getDeclaredAnnotation(javax.persistence.Enumerated.class).value() != null && field.getDeclaredAnnotation(javax.persistence.Enumerated.class).value() == javax.persistence.EnumType.STRING) {
					return value.toString();
				}
				else if(field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class).value() != null && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class).value() == jakarta.persistence.EnumType.STRING) {
					return value.toString();
				}
				
				return ((Enum<?>) value).ordinal();
				
			}

			return value;

		}
		catch(Exception e) {
			log.error("Exception while trying to map field "+entity.getSimpleName()+"."+attribute, e);
		}
		
		log.warn("Could not determine type of field "+entity.getSimpleName()+"."+attribute);
		
		return compileGenericType(value);

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
		if(statement != null) {
			connection.close();
			statement.close();
		}
	}
	

}
