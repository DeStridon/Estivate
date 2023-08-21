package com.estivate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import javax.naming.directory.AttributeInUseException;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;

import com.estivate.EstivateQuery.Entity;
import com.estivate.util.StringPipe;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionExecutor {
	
	final private Connection connection;
	
	public ConnectionExecutor(Connection connection) {
		this.connection = connection;
	}
	
	@SneakyThrows
	public <U> U uniqueResult(EstivateQuery query, Class<U> clazz) {
		
		
		EstivateStatement statement = toStatement(query);
		
		statement.execute();
        
        ResultSet resultSet = statement.getResultSet();
    	ResultSetMetaData metadata = resultSet.getMetaData();
        
        if(resultSet.next()) {

        	Map<String, String> map = new HashMap<>();
        	for(int i = 1; i <= metadata.getColumnCount(); i++) {
        		map.put(metadata.getColumnLabel(i), resultSet.getString(i));
        	}

        	EstivateResult result = new EstivateResult(query, map);
        	U object = result.mapAs(clazz);
        	
        	return object;
        }
        return null;
	}
	
	
	@SneakyThrows
	public List<EstivateResult> list(EstivateQuery joinQuery){
		
		EstivateStatement statement = toStatement(joinQuery);
		
        statement.execute();
        
        ResultSet resultSet = statement.getResultSet();
        
        
        List<EstivateResult> results = new ArrayList<>();
        
        while(resultSet.next()) {
        	Map<String, String> map = new HashMap<>();
        	ResultSetMetaData metadata = resultSet.getMetaData();

        	for(int i = 1; i <= metadata.getColumnCount(); i++) {
        		map.put(metadata.getColumnLabel(i), resultSet.getString(i));
        	}
        	EstivateResult result = new EstivateResult(joinQuery, map);
        	results.add(result);
        }
        
        return results;
		
	}
	
	public EstivateStatement toStatement(EstivateQuery joinQuery) {
		
		EstivateStatement statement = new EstivateStatement(connection);
		
		statement.appendQuery("SELECT ");
		
		//TODO : avoid modifying joinQuery
		if(joinQuery.selects.isEmpty()) {
			joinQuery.select(joinQuery.baseClass);
		}
		
		if(joinQuery.selects.stream().allMatch(x -> x.contains(".")) && joinQuery.groupBys.isEmpty()) {
			statement.appendQuery("distinct ");
		}
		
		statement.appendQuery(String.join(", ", joinQuery.selects)+"\n");
		statement.appendQuery("FROM "+joinQuery.nameMapper.mapEntity(joinQuery.baseClass)+" \n");
		
        for(EstivateJoin join : joinQuery.buildJoins()) {
        	statement.appendQuery(join.toString()+'\n');
        }
        
        if(!joinQuery.criterions.isEmpty()) {
        	statement.appendQuery("WHERE");
        	attachWhere(statement, joinQuery);
        }
        
		// Append group bys (if any)
		if(!joinQuery.groupBys.isEmpty()) {
			statement.appendQuery(joinQuery.groupBys.stream().collect(Collectors.joining(", ", "GROUP BY ", ""))+"\n");
		}
		
		// Append order
		if(!joinQuery.orders.isEmpty()) {
			statement.appendQuery(joinQuery.orders.stream().collect(Collectors.joining(", ", "ORDER BY ", ""))+"\n");
		}
		
		// Append limit & offset
		if(joinQuery.limit != null) {
			statement.appendQuery("LIMIT "+joinQuery.limit+"\n");
		}
		if(joinQuery.offset != null) {
			statement.appendQuery("OFFSET "+ joinQuery.offset +"\n");
		}
        
        return statement;
        
	}
	
	public void attachWhere(EstivateStatement statement, EstivateNode node) {
		
		if(node instanceof EstivateAggregator aggregator) {
			for(int i = 0; i < aggregator.criterions.size(); i++) {
				if(i > 0) {
					statement.appendQuery(" "+aggregator.groupType.toString()+" ");
				}
				attachWhere(statement, aggregator.criterions.get(i));
			}
			
		}
		else if(node instanceof EstivateCriterion.Operator operator) {
			statement.appendQuery(operator.entity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(operator.attribute));
			statement.appendQuery(operator.type.symbol);
			statement.appendQuery("?");
			statement.appendValue(operator.entity.entity, operator.attribute, operator.value);
		}
		else if(node instanceof EstivateCriterion.In in) {
			statement.appendQuery(in.entity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(in.attribute));
			statement.appendQuery(" in (");
			statement.appendQuery(in.values.stream().map(x -> "?").collect(Collectors.joining(", ")));
			statement.appendQuery(")");
			for(Object value : in.values) {
				statement.appendValue(in.entity.entity, in.attribute, value);
			}
		}
		else if(node instanceof EstivateCriterion.Between between) {
			statement.appendQuery(between.entity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(between.attribute));
			statement.appendQuery(between.entity.getName()+"."+ EstivateQuery.nameMapper.mapAttribute(between.attribute)+" ? and ?");
			statement.appendValue(between.entity.entity, between.attribute, between.min);
			statement.appendValue(between.entity.entity, between.attribute, between.max);
			
		}
		else if(node instanceof EstivateCriterion.NullCheck nullcheck) {
			statement.appendQuery(nullcheck.entity.getName() + "." + EstivateQuery.nameMapper.mapAttribute(nullcheck.attribute)+(nullcheck.isNull ? " is null":" is not null"));
		}
		else {
			throw new RuntimeException("Node type not supported : "+node.getClass());
		}
		
	}
	
	
	
	public <U> List<U> listAs(EstivateQuery joinQuery, Class<U> clazz) {
		List<EstivateResult> results = list(joinQuery);
		List<U> output = new ArrayList<>();
		for(EstivateResult result : results) {
			output.add(result.mapAs(clazz));
		}
		return output;
	}
	
	@SneakyThrows
	public <U> U saveOrUpdate(U object) {
		
		Field idField = getIdField(object);
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
		

		List<String> fieldValueList = new ArrayList<>();

		EstivateStatement statement = new EstivateStatement(connection)
				.appendQuery("INSERT INTO ")
				.appendQuery(EstivateQuery.nameMapper.mapEntity(object.getClass()));

		
		for(Field field : object.getClass().getDeclaredFields()) {
			field.setAccessible(true);
			// Skip Id, will be auto generated by db
			if(field.isAnnotationPresent(Id.class)) {
				continue;
			}
			
			try {
				
				if(field.get(object) == null) {
					continue;
				}
				
				fieldValueList.add(field.getName());
				statement.appendValue(object.getClass(), field.getName(), field.get(object));
				//EstivateUtil.compileAttribute(entity.getClass(), field.getName(), field.get(entity)));
				
			}
			catch(Exception e) {
				log.error("Cannot map field "+field.getName(), e);
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
			Field field = getIdField(object);
			field.setAccessible(true);
			field.setLong(object, rs.getLong(1));
		}
		else {
			return null;
		}
		
		return object;
	}
	
//	@SneakyThrows
//	private <U> U insert_old(U object) {
//		
//		String request = EstivateBasic.insert(object);
//		
//		try {
//			PreparedStatement preparedStatement = connection.prepareStatement(request, PreparedStatement.RETURN_GENERATED_KEYS);
//			preparedStatement.execute();
//			ResultSet rs = preparedStatement.getGeneratedKeys();
//			if (rs.next()) {
//				Field field = getIdField(object);
//				field.setAccessible(true);
//				field.setLong(object, rs.getLong(1));
//			}
//			else {
//				return null;
//			}
//		}
//		catch(Exception e) {
//			log.error("Error with request : "+request, e);
//		}
//		
//		return object;
//		
//	}
	
	@SneakyThrows
	public boolean create(Class entityClass) {
		
		List<String> fields = new ArrayList<>();
		for(Field field : entityClass.getDeclaredFields()) {
			StringPipe fieldCreation = new StringPipe();
			fieldCreation.separator(" ");
			
			fieldCreation.append(field.getName());
			
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
			
			if(returnClass == Integer.class || returnClass == Long.class || returnClass == Long.TYPE) {
				fieldCreation.append("INT");
			}
			else if(returnClass == String.class) {
				fieldCreation.append("VARCHAR");
			}
			else {
				System.out.println("Cannot map type "+field.getType());
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
		
		String result = "CREATE TABLE "+entityClass.getSimpleName()+" ("+fields.stream().collect(Collectors.joining(", "))+")";
		
		PreparedStatement statement = connection.prepareStatement(result);
		
		return statement.execute();
		
		
	}
	
	private Field getIdField(Object object) {
		for(Field field : object.getClass().getDeclaredFields()) {
			
			// Skip Id, will be auto generated by db
			if(field.isAnnotationPresent(Id.class)) {
				return field;
			}
		}
		return null;
	}
	
	@SneakyThrows
	public void update(Object entity) {

		List<Field> updatedFields = List.of(entity.getClass().getFields());

		if(entity instanceof CachedEntity) {
			updatedFields = ((CachedEntity) entity).updatedFields();
		}
		
		// No change to entity
		if(updatedFields.isEmpty()) {
			return;
		}
		
		Field idField = EstivateUtil.getFieldWithAnnotation(entity.getClass(), Id.class);
		
		if(idField == null) {
			log.error("No @Id field on class "+entity.getClass());
			return;
		}
		
		idField.setAccessible(true);
		Long id = idField.getLong(entity);
		if(id == null || id == 0) {
			log.error("Null or 0 value id for entity, no update possible");
		}
		
		// 1. Create query
		EstivateStatement statement = new EstivateStatement(connection)
				.appendQuery("UPDATE ")
				.appendQuery(EstivateQuery.nameMapper.mapEntity(entity.getClass()))
				.appendQuery(" SET ");
				
				//String query = "UPDATE "+EstivateQuery.nameMapper.mapEntity(entity.getClass())+" SET ";

		// 2. List updated fields
		statement.appendQuery(updatedFields.stream().map(x-> EstivateQuery.nameMapper.mapEntityAttribute(entity.getClass(), x.getName()) + " = ?").collect(Collectors.joining(", ")));
		
		for(Field field : updatedFields) {
			statement.appendValue(entity.getClass(), field.getName(), field.get(entity));
		}
		
		
		statement.appendQuery(" WHERE "+EstivateQuery.nameMapper.mapAttribute(idField.getName())+" = ?");
		statement.appendValue(entity.getClass(), idField.getName(), idField.getLong(entity));
		
		
		//PreparedStatement preparedStatement = connection.prepareStatement(query, PreparedStatement.RETURN_GENERATED_KEYS);
		
		
		
		boolean check = statement.execute();
		
		System.out.println(check);
		
	}
	
	
	//@AllArgsConstructor
	public static class EstivateResult{
		
		EstivateQuery query;
		Map<String, String> columns;
		
		public EstivateResult(EstivateQuery query, Map<String, String> columns) {
			this.query = query;
			this.columns = columns;
		}
		
		private Map<String, Object> cache = new HashMap<>();
		
		public <U> U mapAs(Class<U> clazz) throws SecurityException, IllegalArgumentException {
			
			if(!cache.containsKey(clazz.getSimpleName())) {
				cache.put(clazz.getSimpleName(), generateObject(clazz, columns));
			}
			
			return (U) cache.get(clazz.getSimpleName());
			
		}
		
		public String mapAsString(Class c, String attribute) {
			return columns.get(EstivateQuery.nameMapper.mapEntityAttribute(c, attribute));
		}
		
		
		public static <U> U generateObject(Class<U> clazz, Map<String, String> arguments) {
			try {
				Constructor<U> constructor = clazz.getConstructor();
				U obj = constructor.newInstance();

				Class<?> currentClazz = clazz;
				Entity entity = new Entity(clazz);

				while(currentClazz != Object.class) {

					for(Field field : currentClazz.getDeclaredFields()) {
						setGeneratedField(entity, arguments, field, obj);
					}
					currentClazz = currentClazz.getSuperclass();
				}
				
				if(obj instanceof CachedEntity) {
					((CachedEntity) obj).saveState();
				}

				return obj;
			}
			catch(Exception e) {
				e.printStackTrace();
				return null;
			}
		}

		public static <U> void setGeneratedField(Entity entity, Map<String, String> arguments, Field field, U obj) throws IllegalAccessException, AttributeInUseException, NoSuchMethodException, ParseException, InvocationTargetException, InstantiationException {
			Type type = field.getGenericType();
			field.setAccessible(true);
			
			String value = arguments.get(entity.getName()+"."+EstivateQuery.nameMapper.mapAttribute(field.getName()));

			if(value == null) {
				return;
			}

			if(type == String.class) {
				field.set(obj, value);
			}
			else if(type == long.class) {
				field.setLong(obj, Long.parseLong(value));
			}
			else if(type == Long.class) {
				field.set(obj, Long.parseLong(value));
			}
			else if(type == boolean.class) {
				field.setBoolean(obj, Boolean.parseBoolean(value));
			}
			else if(type == Boolean.class) {
				field.set(obj, Boolean.parseBoolean(value));
			}
			else if(type == Byte.class) {
				field.set(obj, Byte.parseByte(value));
			}
			else if(type == double.class) {
				field.setDouble(obj, Double.parseDouble(value));
			}
			else if(type == Double.class) {
				field.set(obj, Double.parseDouble(value));
			}
			else if(type == Character.class && value.length() > 0) {
				field.set(obj, value.charAt(0));
			}
			else if(type == Float.class) {
				field.set(obj, Float.parseFloat(value));
			}
			else if(type == int.class) {
				field.setInt(obj, Integer.parseInt(value));
			}
			else if(type == Integer.class) {
				field.set(obj, Integer.parseInt(value));
			}
			else if(type == short.class) {
				field.setShort(obj, Short.parseShort(value));
			}
			else if(type == Short.class) {
				field.set(obj, Short.parseShort(value));
			}
			else if(type == Date.class) {
				// TODO : check date format is the right one
				field.set(obj, new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").parse(value));
			}
			// @Convert (might be enum, this condition should be tested before classic enum)
			else if(field.getDeclaredAnnotation(Convert.class) != null) {
				Convert convertAnnotation = field.getDeclaredAnnotation(Convert.class);
				Object converter = convertAnnotation.converter().getConstructor().newInstance();
				if(!(converter instanceof AttributeConverter)) {
					log.error("Cannot convert with converter "+converter.getClass());
					return;
				}
				AttributeConverter attributeConverter = (AttributeConverter) converter;
				Object attributeValue = attributeConverter.convertToEntityAttribute(value);
				field.set(obj, attributeValue);
			}
			// @Enumerated
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(Enumerated.class) != null) {

				Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == EnumType.STRING) {
					field.set(obj, Enum.valueOf((Class)type, value));
				}
				else {
					int ordinal = Integer.parseInt(value);
					Object finalValue = field.getType().getEnumConstants()[ordinal];
					field.set(obj, field.getType().getEnumConstants()[ordinal]);
				}
			}
			else {
				log.error("This type is not mapped yet : "+type);
				throw new AttributeInUseException("This type is not mapped yet : "+type);
			}
		}

		
	}


	
	
//	public <U> List<U> listAs(JoinQueryPreparedStatement joinQuery, Class<U> clazz) {
//		Session session = this.sessionFactory.openSession();
//
//		try {
//			Query<Tuple> query = session.createNativeQuery(joinQuery.query, Tuple.class);
//			for(int i = 0; i < joinQuery.parameters.size(); i++) {
//				query.setParameter(i, joinQuery.parameters.get(i));
//			}
//			List<Tuple> tuples = query.getResultList();
//			
//			List<U> results = tuples.stream().map(tuple -> new JoinQueryResult(joinQuery, tuple).mapAs(clazz)).collect(Collectors.toList());
//			return results;
//			
//		} catch (HibernateException e) {
//			log.error("DB Error", e, joinQuery.compile());
//			return null;
//		} catch (Exception exception) {
//			log.error("DB Error", exception, joinQuery.compile());
//			return null;
//		} finally {
//			session.close();
//		}
//	}




}
