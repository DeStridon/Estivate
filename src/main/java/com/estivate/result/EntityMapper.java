package com.estivate.result;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.naming.directory.AttributeInUseException;

import org.apache.commons.lang3.StringUtils;

import com.estivate.Entity;
import com.estivate.query.Projection;
import com.estivate.util.Chronometer;
import com.estivate.util.EstivateException;
import com.estivate.util.FieldUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityMapper<U> extends IMapper<U> {

	// Entity side
	final Class<U> entityClass;
	final Set<Field> entityFields;
	final Constructor<U> entityConstructor;
	final Set<Method> entityPostLoadMethods;
	
	
	// Result side

	final Chronometer chronometer;

	// TODO : field is empty for row mapping
	List<Field> columnFields = new ArrayList<>();

	public void setResultColumnNames(String[] columnNames) {

		// Map columns to fields
		Entity<U> entity = new Entity<>(entityClass);
		columnFields = new ArrayList<>();

		for (int i = 0; i < columnNames.length; i++) {
			String columnName = columnNames[i];
			Field field = entityFields.stream().filter(x -> columnName.equals(fieldToColumnName(entity, x))).findFirst()
					.orElse(null);
			if (field != null) {
				while (columnFields.size() <= i) {
					columnFields.add(null);
				}
				columnFields.set(i, field);
			}
		}

	}

	@SneakyThrows
	public EntityMapper(Class<U> targetClass, boolean tracePerformances) {
		chronometer = new Chronometer("Mapper " + targetClass.getSimpleName(), tracePerformances).timeThreshold(100);

		this.entityClass = targetClass;

		// Get constructor
		entityConstructor = targetClass.getConstructor();

		// Get Fields
		entityFields = FieldUtils.getEntityFields(targetClass);

		// Get PostLoadMethods
		entityPostLoadMethods = FieldUtils.getPostLoadMethods(targetClass);

		chronometer.step("mapper constructor");
	}

	public EntityMapper(Class<U> targetClass) {
		this(targetClass, false);
	}

	@SneakyThrows
	public U map(String[] row) {
		U obj = entityConstructor.newInstance();
		Entity<U> entity = new Entity<>(entityClass);
		chronometer.step("constructor & entity");

		for (int i = 0; i < row.length; i++) {
			if (i >= columnFields.size()) {
				continue;
			}
			Field field = columnFields.get(i);
			chronometer.step("get field");
			if (field != null) {
				setGeneratedField(entity, field, obj, row[i]);
				chronometer.step("generate field " + field.getName());
			}
		}
		for (Method method : entityPostLoadMethods) {
			method.invoke(obj);
			chronometer.step("invoke method " + method.getName());
		}
		return obj;
	}

	@SneakyThrows
	public U map(Map<String, String> arguments) {

		U obj = entityConstructor.newInstance();

		Entity<U> entity = new Entity<>(entityClass);

		Class<?> currentClass = entityClass;
		while (currentClass != Object.class) {

			Set<Field> fields = FieldUtils.getEntityFields(currentClass);
			for (Field field : fields) {
				String columnName = fieldToColumnName(entity, field);
				String value = arguments.get(columnName);
				setGeneratedField(entity, field, obj, value);
			}

			currentClass = currentClass.getSuperclass();

		}

		Set<Method> methods = FieldUtils.getPostLoadMethods(obj.getClass());
		for (Method method : methods) {
			method.invoke(obj);
		}

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

		Projection.Function functionAnnotation = field.getDeclaredAnnotation(Projection.Function.class);
		if (functionAnnotation != null) {
			return getColumnNameFromAnnotation(functionAnnotation.entity(), functionAnnotation.attribute(), functionAnnotation.alias());
		}
		
		return nameMapper.toEntityNameAttribute(entity, field.getName());
		
	}



	/**
	 * Common logic to get column name from annotations with entity, attribute, and
	 * alias
	 */
	private String getColumnNameFromAnnotation(Class<?> entityClass, String attribute, String alias) {
		// If alias is not null and not empty, use it directly
		if (alias != null && !alias.isEmpty()) {
			return nameMapper.mapEntityField(alias);
		}

		// If alias is null or empty, find the corresponding field in the entity and use
		// its mapped name
		Field mappingField = FieldUtils.getEntityFields(entityClass).stream().filter(x -> x.getName().equals(attribute))
				.findFirst().orElse(null);

		if (mappingField == null) {
			log.error("Field " + attribute + " not found in entity " + entityClass);
			return null;
		}

		return fieldToColumnName(new Entity<>(entityClass), mappingField);
	}

	public void setGeneratedField(Entity<?> entity, Field field, U obj, String value) throws EstivateException {
        try {
            
            if(value == null) {
                return;
            }
            
            Type type = field.getGenericType();
            
            if(type == String.class) { field.set(obj, value); }
            else if(type == short.class) { field.setShort(obj, Short.parseShort(value)); }
            else if(type == Short.class) { field.set(obj, Short.parseShort(value)); }
            else if(type == int.class) { field.setInt(obj, Integer.parseInt(value)); }
            else if(type == Integer.class) { field.set(obj, Integer.parseInt(value)); }
            else if(type == long.class) { field.setLong(obj, Long.parseLong(value)); }
            else if(type == Long.class) { field.set(obj, Long.parseLong(value)); }
            
            else if(type == float.class) { field.setFloat(obj, Float.parseFloat(value)); }
            else if(type == Float.class) { field.set(obj, Float.parseFloat(value));}
            else if(type == double.class) { field.setDouble(obj, Double.parseDouble(value)); }
            else if(type == Double.class) { field.set(obj, Double.parseDouble(value)); }
            else if(type == BigDecimal.class) { field.set(obj, new BigDecimal(value)); }
            
            else if(type == boolean.class) { field.setBoolean(obj, StringUtils.equals("true", value.toLowerCase()) || StringUtils.equals("1", value)); }
            else if(type == Boolean.class) { field.set(obj, StringUtils.equals("true", value.toLowerCase()) || StringUtils.equals("1", value)); }
            else if(type == Byte.class) { field.set(obj, Byte.parseByte(value)); }
            else if(type == Character.class && value.length() > 0) { field.set(obj, value.charAt(0)); }
            
            
            else if(type == Date.class) {
                LocalDateTime dateTime = DateMapper.mapDate(value);
                field.set(obj, Date.from(dateTime.atZone(ZoneOffset.systemDefault()).toInstant()));
            }
            else if(type == LocalDateTime.class) {
                field.set(obj, DateMapper.mapDate(value));
            }
            else if(type == LocalDate.class) {
                field.set(obj, DateMapper.mapDate(value).toLocalDate());
            }
            // @Convert (might be enum, this condition should be tested before classic enum)
            else if(field.getDeclaredAnnotation(javax.persistence.Convert.class) != null) {
                javax.persistence.Convert convertAnnotation = field.getDeclaredAnnotation(javax.persistence.Convert.class);
                Object converter = convertAnnotation.converter().getConstructor().newInstance();
                if(!(converter instanceof javax.persistence.AttributeConverter)) {
                    log.error("Cannot convert with converter "+converter.getClass());
                    return;
                }
                javax.persistence.AttributeConverter attributeConverter = (javax.persistence.AttributeConverter) converter;
                Object attributeValue = attributeConverter.convertToEntityAttribute(value);
                field.set(obj, attributeValue);
            }
			else if(field.getDeclaredAnnotation(jakarta.persistence.Convert.class) != null) {
				jakarta.persistence.Convert convertAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Convert.class);
				Object converter = convertAnnotation.converter().getConstructor().newInstance();
				if(!(converter instanceof jakarta.persistence.AttributeConverter)) {
					log.error("Cannot convert with converter "+converter.getClass());
					return;
				}
				jakarta.persistence.AttributeConverter attributeConverter = (jakarta.persistence.AttributeConverter) converter;
				Object attributeValue = attributeConverter.convertToEntityAttribute(value);
				field.set(obj, attributeValue);
			}
            // @Enumerated
            else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(javax.persistence.Enumerated.class) != null) {
    
                javax.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
                if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == javax.persistence.EnumType.STRING) {
                    field.set(obj, Enum.valueOf((Class)type, value));
                }
                else {
                    int ordinal = Integer.parseInt(value);
                    field.set(obj, field.getType().getEnumConstants()[ordinal]);
                }
            }
			else if(type instanceof Class && ((Class<?>) type).isEnum() && field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class) != null) {
				jakarta.persistence.Enumerated enumeratedAnnotation = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
				if(enumeratedAnnotation.value() != null && enumeratedAnnotation.value() == jakarta.persistence.EnumType.STRING) {
					field.set(obj, Enum.valueOf((Class)type, value));
				}
				else {
					int ordinal = Integer.parseInt(value);
					field.set(obj, field.getType().getEnumConstants()[ordinal]);
				}
			}
            else {
                log.error("This type is not mapped yet : "+type);
                throw new AttributeInUseException("This type is not mapped yet : "+type);
            }
        }
        catch(Throwable e) {
            log.error("Impossible to map Entity="+entity.toString()+ ", Field="+field.getName()+", Value="+value+", Object="+obj.toString(), e);
            throw new EstivateException("Impossible to map Entity="+entity.toString()+ ", Field="+field.getName()+", Value="+value+", Object="+obj.toString(), e);
        }
    }

	public String getStats() {
		return chronometer.getLog();
	}

}
