package com.estivate.result;

import java.lang.reflect.Field;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;

import org.apache.commons.lang3.StringUtils;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Attribute;
import com.estivate.query.Projection;
import com.estivate.query.Projection.IdentityFunction;
import com.estivate.query.SelectQuery;
import com.estivate.result.IMapper.DateMapper;
import com.estivate.util.EstivateException;
import com.estivate.util.ClassMeta;
import com.estivate.util.ClassMeta.FieldBinding;
import com.estivate.util.FieldUtils;
import com.estivate.util.ReflectionUtils;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityMapper<U> {

	// Entity side
	final Entity<U> entity;

	final ClassMeta entityClassMeta;

	List<ColumnMapping> columnMappings = new ArrayList<>();
	/** Indexes into {@link #columnMappings} that have a field mapping (primitive array for hot path). */
	int[] columnMappingIndexes = new int[0];

	List<EntityMapping> entityMappings = new ArrayList<>();
	
	// Result side

	
	final Context context;

	final SelectQuery<?> query;


	@SneakyThrows
	public EntityMapper(Context context, SelectQuery<?> query, Entity<U> entity, boolean tracePerformances) {
	
		this.context = context;
		this.entity = entity;
		this.query = query;

		entityClassMeta = ClassMeta.of(entity.entity);

		// Get Fields
		columnMappings = getColumnMappings(query, entity);
		List<Integer> indexes = new ArrayList<>();
		for(int i = 0; i < columnMappings.size(); i++) {
			if(columnMappings.get(i) != null) {
				indexes.add(i);
			}
		}
		columnMappingIndexes = new int[indexes.size()];
		for(int i = 0; i < indexes.size(); i++) {
			columnMappingIndexes[i] = indexes.get(i);
		}

		// Entity Mappings
		entityMappings = getEntityMappings(query, entity);

		

	}

	public EntityMapper(Context context, SelectQuery<?> query, Entity<U> entity) {
		this(context, query, entity, false);
	}

	public U map(String[] row) {
		return map(row, null);
	}

	@SneakyThrows
	public U map(String[] row, U obj) {

		// if all row values are null for columnMapping, return null
		boolean allNull = true;
		for(int index : columnMappingIndexes) {
			if(row[index] != null) {
				allNull = false;
				break;
			}
		}
		if(allNull) {
			return null;
		}

		if(obj == null) {
			obj = entityClassMeta.newInstance();
			for (int i = 0; i < row.length; i++) {
				if (i >= columnMappings.size() || columnMappings.get(i) == null) {
					continue;
				}
				ColumnMapping columnMapping = columnMappings.get(i);
				Field field = columnMapping.getField();
				if (field != null) {
					setGeneratedField(entity, field, obj, row[i], columnMapping.getTransformer());
				}
			}
			entityClassMeta.invokePostLoad(obj);
		}

		mapNestedEntities(row, obj);

		return obj;
	}

	/**
	 * Maps one JDBC row directly into an entity (typed getters, no {@code String[]} buffer).
	 */
	@SneakyThrows
	public U map(ResultSet resultSet) {
		boolean allNull = true;
		Object[] values = new Object[columnMappings.size()];
		for(int index : columnMappingIndexes) {
			ColumnMapping columnMapping = columnMappings.get(index);
			Object value = readTyped(resultSet, index + 1, columnMapping.getField());
			values[index] = value;
			if(value != null) {
				allNull = false;
			}
		}
		if(allNull) {
			return null;
		}

		U obj = entityClassMeta.newInstance();
		for(int index : columnMappingIndexes) {
			ColumnMapping columnMapping = columnMappings.get(index);
			Field field = columnMapping.getField();
			Object value = values[index];
			if(field == null) {
				continue;
			}
			if(columnMapping.getTransformer() != null && !(columnMapping.getTransformer() instanceof IdentityFunction)) {
				value = ((Function<Object, Object>) columnMapping.getTransformer()).apply(value);
			}
			if(value != null) {
				field.set(obj, value);
			}
		}
		entityClassMeta.invokePostLoad(obj);

		for(EntityMapping entityMapping : entityMappings) {
			Object subObj = entityMapping.getEntityMapper().map(resultSet);
			applyNested(obj, entityMapping, subObj);
		}
		return obj;
	}

	@SneakyThrows
	private void mapNestedEntities(String[] row, U obj) {
		for(EntityMapping entityMapping : entityMappings) {
			Object subObj = entityMapping.getEntityMapper().map(row, null);
			applyNested(obj, entityMapping, subObj);
		}
	}

	@SneakyThrows
	@SuppressWarnings("unchecked")
	private void applyNested(U obj, EntityMapping entityMapping, Object subObj) {
		if(entityMapping.getField().getType().isAssignableFrom(List.class)) {
			List<Object> list = (List<Object>) entityMapping.getField().get(obj);
			if(list == null) {
				list = new ArrayList<>();
				entityMapping.getField().set(obj, list);
			}
			if(subObj != null) {
				list.add(subObj);
			}
		}
		else {
			entityMapping.getField().set(obj, subObj);
		}
	}

	@SneakyThrows
	private Object readTyped(ResultSet resultSet, int jdbcIndex, Field field) throws SQLException {
		if(field == null) {
			return null;
		}

		Projection.Attribute projectionAttribute = field.getDeclaredAnnotation(Projection.Attribute.class);
		if(projectionAttribute != null) {
			Field mappingField = ClassMeta.findField(projectionAttribute.entity(), projectionAttribute.attribute());
			return readTyped(resultSet, jdbcIndex, mappingField);
		}

		FieldBinding binding = entityClassMeta.getFieldBinding(field);
		if(binding != null && binding.hasConverter()) {
			Object raw = resultSet.getObject(jdbcIndex);
			return binding.toEntityAttribute(raw);
		}

		Class<?> type = field.getType();
		if(type.isEnum()) {
			String raw = resultSet.getString(jdbcIndex);
			if(raw == null || resultSet.wasNull()) {
				return null;
			}
			FieldBinding enumBinding = binding != null ? binding : ClassMeta.fieldBinding(field);
			if(enumBinding != null && enumBinding.getEnumMode() == FieldBinding.EnumMode.ORDINAL) {
				int ordinal = Integer.parseInt(raw);
				return type.getEnumConstants()[ordinal];
			}
			return Enum.valueOf((Class) type, raw);
		}

		if(type == String.class) {
			return resultSet.getString(jdbcIndex);
		}
		if(type == short.class || type == Short.class) {
			short value = resultSet.getShort(jdbcIndex);
			if(resultSet.wasNull()) {
				return type == short.class ? Short.valueOf((short) 0) : null;
			}
			return value;
		}
		if(type == int.class || type == Integer.class) {
			int value = resultSet.getInt(jdbcIndex);
			if(resultSet.wasNull()) {
				return type == int.class ? Integer.valueOf(0) : null;
			}
			return value;
		}
		if(type == long.class || type == Long.class) {
			long value = resultSet.getLong(jdbcIndex);
			if(resultSet.wasNull()) {
				return type == long.class ? Long.valueOf(0L) : null;
			}
			return value;
		}
		if(type == float.class || type == Float.class) {
			float value = resultSet.getFloat(jdbcIndex);
			if(resultSet.wasNull()) {
				return type == float.class ? Float.valueOf(0f) : null;
			}
			return value;
		}
		if(type == double.class || type == Double.class) {
			double value = resultSet.getDouble(jdbcIndex);
			if(resultSet.wasNull()) {
				return type == double.class ? Double.valueOf(0d) : null;
			}
			return value;
		}
		if(type == BigDecimal.class) {
			return resultSet.getBigDecimal(jdbcIndex);
		}
		if(type == boolean.class || type == Boolean.class) {
			boolean value = resultSet.getBoolean(jdbcIndex);
			if(resultSet.wasNull()) {
				return type == boolean.class ? Boolean.FALSE : null;
			}
			return value;
		}
		if(type == Byte.class || type == byte.class) {
			byte value = resultSet.getByte(jdbcIndex);
			if(resultSet.wasNull()) {
				return type == byte.class ? Byte.valueOf((byte) 0) : null;
			}
			return value;
		}
		if(type == Character.class || type == char.class) {
			String value = resultSet.getString(jdbcIndex);
			if(value == null || value.isEmpty()) {
				return type == char.class ? '\0' : null;
			}
			return value.charAt(0);
		}
		if(type == Date.class) {
			Timestamp timestamp = resultSet.getTimestamp(jdbcIndex);
			return timestamp == null ? null : new Date(timestamp.getTime());
		}
		if(type == LocalDateTime.class) {
			Timestamp timestamp = resultSet.getTimestamp(jdbcIndex);
			return timestamp == null ? null : timestamp.toLocalDateTime();
		}
		if(type == LocalDate.class) {
			java.sql.Date date = resultSet.getDate(jdbcIndex);
			return date == null ? null : date.toLocalDate();
		}
		if(type == Instant.class) {
			Timestamp timestamp = resultSet.getTimestamp(jdbcIndex);
			return timestamp == null ? null : timestamp.toInstant();
		}

		return resultSet.getObject(jdbcIndex);
	}

	@SneakyThrows
	public U map(Map<String, String> arguments) {

		U obj = entityClassMeta.newInstance();

		Class<?> currentClass = entity.entity;
		while (currentClass != Object.class) {

			Set<Field> fields = ClassMeta.of(currentClass).getFields();
			for (Field field : fields) {
				String columnName = fieldToColumnName(entity, field);
				String value = arguments.get(columnName);
				setGeneratedField(entity, field, obj, value);
			}

			currentClass = currentClass.getSuperclass();

		}

		ClassMeta.of(obj.getClass()).invokePostLoad(obj);

		return obj;

	}

	// can this method be used for several entities ?
	public String fieldToColumnName(Entity<?> entity, Field field) {
		
		Projection.Attribute attributeAnnotation = field.getDeclaredAnnotation(Projection.Attribute.class);
		if (attributeAnnotation != null) {
			return getColumnNameFromAnnotation(attributeAnnotation.entity(), attributeAnnotation.attribute(), attributeAnnotation.alias());
		}

		Projection.Count countAnnotation = field.getDeclaredAnnotation(Projection.Count.class);
		if (countAnnotation != null) {
			return getColumnNameFromAnnotation(countAnnotation.entity(), countAnnotation.attribute(), countAnnotation.alias());
		}

		Projection.Sum sumAnnotation = field.getDeclaredAnnotation(Projection.Sum.class);
		if (sumAnnotation != null) {
			return getColumnNameFromAnnotation(sumAnnotation.entity(), sumAnnotation.attribute(), sumAnnotation.alias());
		}

		Projection.Min minAnnotation = field.getDeclaredAnnotation(Projection.Min.class);
		if (minAnnotation != null) {
			return getColumnNameFromAnnotation(minAnnotation.entity(), minAnnotation.attribute(), minAnnotation.alias());
		}

		Projection.Max maxAnnotation = field.getDeclaredAnnotation(Projection.Max.class);
		if (maxAnnotation != null) {
			return getColumnNameFromAnnotation(maxAnnotation.entity(), maxAnnotation.attribute(), maxAnnotation.alias());
		}

		Projection.Avg avgAnnotation = field.getDeclaredAnnotation(Projection.Avg.class);
		if (avgAnnotation != null) {
			return getColumnNameFromAnnotation(avgAnnotation.entity(), avgAnnotation.attribute(), avgAnnotation.alias());
		}

		Projection.IsNull isNullAnnotation = field.getDeclaredAnnotation(Projection.IsNull.class);
		if (isNullAnnotation != null) {
			return getColumnNameFromAnnotation(isNullAnnotation.entity(), isNullAnnotation.attribute(), isNullAnnotation.alias());
		}
		Projection.IsNotNull isNotNullAnnotation = field.getDeclaredAnnotation(Projection.IsNotNull.class);
		if (isNotNullAnnotation != null) {
			return getColumnNameFromAnnotation(isNotNullAnnotation.entity(), isNotNullAnnotation.attribute(), isNotNullAnnotation.alias());
		}

		Projection.Function functionAnnotation = field.getDeclaredAnnotation(Projection.Function.class);
		if (functionAnnotation != null) {
			return getColumnNameFromAnnotation(functionAnnotation.entity(), functionAnnotation.attribute(), functionAnnotation.alias());
		}
		
		return context.nameMapper.toEntityNameAttribute(entity, field.getName());
		
	}



	/**
	 * Common logic to get column name from annotations with entity, attribute, and
	 * alias
	 */
	private String getColumnNameFromAnnotation(Class<?> entityClass, String attribute, String alias) {
		// If alias is not null and not empty, use it directly
		if (alias != null && !alias.isEmpty()) {
			return context.nameMapper.mapEntityField(alias);
		}

		// If alias is null or empty, find the corresponding field in the entity and use
		// its mapped name
		Field mappingField = ClassMeta.of(entityClass).getFields().stream().filter(x -> x.getName().equals(attribute))
				.findFirst().orElse(null);

		if (mappingField == null) {
			log.error("Field " + attribute + " not found in entity " + entityClass);
			return null;
		}

		return fieldToColumnName(new Entity<>(entityClass), mappingField);
	}

	public void setGeneratedField(Entity<?> entity, Field field, U obj, String value) throws EstivateException {
		setGeneratedField(entity, field, obj, value, null);
	}

	public void setGeneratedField(Entity<?> entity, Field field, U obj, String value, Function<?, ?> transformer) throws EstivateException {
        try {
			Object convertedValue = convertValue(field, value);
			if(transformer != null && !(transformer instanceof IdentityFunction)) {
				convertedValue = ((Function<Object, Object>) transformer).apply(convertedValue);
			}
			if(convertedValue != null) {
				field.set(obj, convertedValue);
			}
        }
        catch(Throwable e) {
            log.error("Impossible to map Entity="+entity.toString()+ ", Field="+field.getName()+", Value="+value+", Object="+obj.toString(), e);
            throw new EstivateException("Impossible to map Entity="+entity.toString()+ ", Field="+field.getName()+", Value="+value+", Object="+obj.toString(), e);
        }
    }

	@SneakyThrows
	private Object convertValue(Field field, String value) {
		
		if(field == null) {
			return null;
		}

		 // @Projection.Attribute
		 if(field.getDeclaredAnnotation(Projection.Attribute.class) != null) {
		 	Projection.Attribute annotation = field.getDeclaredAnnotation(Projection.Attribute.class);
		 	Field mappingField = ClassMeta.findField(annotation.entity(), annotation.attribute());
		 	return convertValue(mappingField, value);
		 }

		FieldBinding binding = ClassMeta.fieldBinding(field);
		// @Convert — must run for null DB values too
		if(binding != null && binding.hasConverter()) {
			return binding.toEntityAttribute(value);
		}

		if(value == null) {
			return null;
		}

		Type type = field.getGenericType();

		// @Enumerated via cached FieldBinding
		if(type instanceof Class && ((Class<?>) type).isEnum() && binding != null && binding.getEnumMode() != FieldBinding.EnumMode.NONE) {
			if(binding.getEnumMode() == FieldBinding.EnumMode.STRING) {
				return Enum.valueOf((Class) type, value);
			}
			int ordinal = Integer.parseInt(value);
			return field.getType().getEnumConstants()[ordinal];
		}

		// Classic types
		if(type == String.class) { return value; }
		if(type == short.class 	|| type == Short.class) 	{ return Short.parseShort(value); }
		if(type == int.class 	|| type == Integer.class) 	{ return Integer.parseInt(value); }
		if(type == long.class 	|| type == Long.class) 		{ return Long.parseLong(value); }
		if(type == float.class 	|| type == Float.class) 	{ return Float.parseFloat(value); }
		if(type == double.class || type == Double.class) 	{ return Double.parseDouble(value); }
		if(type == BigDecimal.class) { return new BigDecimal(value); }
		if(type == boolean.class || type == Boolean.class) { return StringUtils.equals("true", value.toLowerCase()) || StringUtils.equals("1", value); }
		if(type == Byte.class) { return Byte.parseByte(value); }
		if(type == Character.class && value.length() > 0) { return value.charAt(0); }
		
		// Date
		if(type == Date.class) {
			LocalDateTime dateTime = DateMapper.mapDate(value);
            return Date.from(dateTime.atZone(context.serverZoneId).toInstant());
        }
        else if(type == LocalDateTime.class) { return DateMapper.mapDate(value); }
        else if(type == LocalDate.class) { return DateMapper.mapDate(value).toLocalDate(); }
		else if(type == Instant.class) { return DateMapper.mapDate(value).atZone(context.serverZoneId).toInstant(); }
        
		log.error("This type is not mapped yet : "+type);
		return null;

	}
	
	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class ColumnMapping {
		Attribute attribute;
		Field field;
		Function<?, ?> transformer;
	}

	@Data
	@NoArgsConstructor
	@AllArgsConstructor
	public static class EntityMapping {
		Field field;
		EntityMapper<?> entityMapper;
	}


	List<ColumnMapping> getColumnMappings(SelectQuery<?> query, Entity<?> entityClass) {
		List<ColumnMapping> columnMappings = new ArrayList<>(query.getSelects().size());

		for(int i = 0; i < query.getSelects().size(); i++) {
			columnMappings.add(null);
		}

		List<String> unmappedFields = new ArrayList<>();

		for(Field field : ClassMeta.of(entityClass.entity).getFields()) {
			ColumnMapping columnMapping = getColumnMapping(entityClass, field);

			int index = FieldUtils.indexOf(query.getSelects(), columnMapping.getAttribute());

			if(index != -1) {
				columnMappings.set(index, columnMapping);
			} else {
				unmappedFields.add(field.getName());
			}

		}

		if(!unmappedFields.isEmpty()) {
			log.warn("In following query : " + query.toString()+", trying to map to entity : " + entityClass.toString() + ", fields missing : " + String.join(", ", unmappedFields));
		}
		return columnMappings;
	}

	List<EntityMapping> getEntityMappings(SelectQuery<?> query, Entity<?> entityClass){
		List<EntityMapping> list = new ArrayList<>();
		for(Field field : ClassMeta.of(entityClass.entity).getFields()) {

			if(!field.isAnnotationPresent(Projection.Nested.class)){
				continue;
			}
			EntityMapping entityMapping = new EntityMapping();
				
			// Get type in List
			if(field.getType().isAssignableFrom(List.class)) {
				Class<?> listType = ReflectionUtils.getListType(field);
				if(listType == null) {
					log.error("List type is not found for field " + field.getName());
					continue;
				}
				entityMapping.setEntityMapper(new EntityMapper<>(context, query, new Entity<>(listType)));
			}
			else {
				entityMapping.setEntityMapper(new EntityMapper<>(context, query, new Entity<>(field.getType())));
			}
			entityMapping.setField(field);
			list.add(entityMapping);
	
		}
		return list;
	}

	public static ColumnMapping getColumnMapping(Entity<?> entityClass, Field field) {

		Projection.Attribute attributeAnnotation = field.getDeclaredAnnotation(Projection.Attribute.class);

		if (attributeAnnotation != null) {
			Function<?, ?> transformer = null;
			if (attributeAnnotation.transformer() != IdentityFunction.class) {
				try {
					transformer = attributeAnnotation.transformer().getConstructor().newInstance();
				} catch (Exception e) {
					log.error("Impossible to create transformer for field " + field.getName(), e);
				}
			}
			return new ColumnMapping(
					Estivate.attribute(attributeAnnotation.entity(), attributeAnnotation.attribute(), attributeAnnotation.alias()),
					field,
					transformer);
		}

		Projection.Count countAnnotation = field.getDeclaredAnnotation(Projection.Count.class);
		if (countAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(countAnnotation.entity(), countAnnotation.attribute(), Estivate.Functions.count, countAnnotation.alias()), field, null);
		}
		
		Projection.CountDistinct countDistinctAnnotation = field.getDeclaredAnnotation(Projection.CountDistinct.class);
		if (countDistinctAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(countDistinctAnnotation.entity(), countDistinctAnnotation.attribute(), Estivate.Functions.countDistinct, countDistinctAnnotation.alias()), field, null);
		}

		Projection.Sum sumAnnotation = field.getDeclaredAnnotation(Projection.Sum.class);
		if (sumAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(sumAnnotation.entity(), sumAnnotation.attribute(), Estivate.Functions.sum, sumAnnotation.alias()), field, null);
		}

		Projection.Min minAnnotation = field.getDeclaredAnnotation(Projection.Min.class);
		if (minAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(minAnnotation.entity(), minAnnotation.attribute(), Estivate.Functions.min, minAnnotation.alias()), field, null);
		}

		Projection.Max maxAnnotation = field.getDeclaredAnnotation(Projection.Max.class);
		if (maxAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(maxAnnotation.entity(), maxAnnotation.attribute(), Estivate.Functions.max, maxAnnotation.alias()), field, null);
		}

		Projection.Avg avgAnnotation = field.getDeclaredAnnotation(Projection.Avg.class);
		if (avgAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(avgAnnotation.entity(), avgAnnotation.attribute(), Estivate.Functions.avg, avgAnnotation.alias()), field, null);
		}

		Projection.IsNull isNullAnnotation = field.getDeclaredAnnotation(Projection.IsNull.class);
		if (isNullAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(isNullAnnotation.entity(), isNullAnnotation.attribute(), Estivate.Functions.isNull, isNullAnnotation.alias()), field, null);
		}
		Projection.IsNotNull isNotNullAnnotation = field.getDeclaredAnnotation(Projection.IsNotNull.class);
		if (isNotNullAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(isNotNullAnnotation.entity(), isNotNullAnnotation.attribute(), Estivate.Functions.isNotNull, isNotNullAnnotation.alias()), field, null);
		}

		Projection.Function functionAnnotation = field.getDeclaredAnnotation(Projection.Function.class);
		if (functionAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(functionAnnotation.entity(), functionAnnotation.attribute(), Estivate.function(functionAnnotation.functionPrefix(), functionAnnotation.functionSuffix()), functionAnnotation.alias()), field, null);
		}

		return new ColumnMapping(Estivate.attribute(entityClass, field.getName()), field, null);
	}
		
}
