package com.estivate;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Type;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
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
import javax.persistence.Id;
import javax.persistence.Tuple;

import org.hibernate.HibernateException;
import org.hibernate.query.NativeQuery;

import com.estivate.EstivateQuery.Entity;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ConnectionExecutor {
	
	
	

	@SneakyThrows
	public List<EstivateResult2> list(EstivateQuery joinQuery){

		Connection connection = DriverManager.getConnection("jdbc:h2:mem:test");
		
		String sql = "Create table students (ID int primary key, name varchar(50))";
        
	
		
        Statement statement = connection.createStatement();
         
        statement.execute(sql);
        
        ResultSet resultSet = statement.getResultSet();
        
        ResultSetMetaData metadata = resultSet.getMetaData();

        List<EstivateResult2> results = new ArrayList<>();
        
        while(resultSet.next()) {
        	
        	Map<String, String> map = new HashMap<>();
        	
        	for(int i = 0; i < metadata.getColumnCount(); i++) {
        		map.put(metadata.getColumnLabel(i), resultSet.getString(i));
        	}

        	EstivateResult2 result = new EstivateResult2(joinQuery, map);
        	
        	results.add(result);
        	
        }
        
        return results;
		
		
	}
	
	public <U> List<U> listAs(EstivateQuery joinQuery, Class<U> clazz) {
		
		try {
			NativeQuery<Tuple> query = session.createNativeQuery(joinQuery.compile(), Tuple.class);
			List<Tuple> tuples = query.getResultList();
			
			List<U> results = tuples.stream().map(tuple -> new EstivateResult(joinQuery, tuple).mapAs(clazz)).collect(Collectors.toList());
			return results;
			
		} catch (HibernateException e) {
			log.error("DB Error", e, joinQuery.compile());
			return null;
		} catch (Exception exception) {
			log.error("DB Error", exception, joinQuery.compile());
			return null;
		} finally {
			session.close();
		}
	}
	
	@SneakyThrows
	// TODO : implement query execution
	public void update(CachedEntity entity) {
		
		List<Field> updatedFields = entity.updatedFields();
		
		// No change to entity
		if(updatedFields.isEmpty()) {
			return;
		}
		
		Field idField = EstivateUtil.getFieldWithAnnotation(entity.getClass(), Id.class);
		
		if(idField == null) {
			log.error("No @Id field on class "+entity.getClass());
			return;
		}
		
		Long id = idField.getLong(entity);
		if(id == null || id == 0) {
			log.error("Null or 0 value id for entity, no update possible");
		}
		
		// 1. Create query
		String query = "UPDATE "+EstivateQuery.nameMapper.mapEntity(entity.getClass())+" SET ";

		// 2. List updated fields
		List<String> fieldUpdateQueries = new ArrayList<>();
		for(Field field : updatedFields) {
			fieldUpdateQueries.add(field.getName() + " = " + EstivateUtil.compileAttribute(entity.getClass(), field.getName(), field.get(entity)));
		}
		
		query += fieldUpdateQueries.stream().collect(Collectors.joining(","));
		
		query += "WHERE "+EstivateQuery.nameMapper.mapAttribute(idField.getName())+" = "+idField.getLong(entity);
		
	}
	
	
	//@AllArgsConstructor
	public static class EstivateResult2{
		
		EstivateQuery query;
		Map<String, String> columns;
		
		public EstivateResult2(EstivateQuery query, Map<String, String> columns) {
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

			String value = arguments.get(entity.getName()+"."+field.getName());

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
