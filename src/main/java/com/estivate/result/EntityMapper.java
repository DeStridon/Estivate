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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

import javax.naming.directory.AttributeInUseException;
import javax.persistence.AttributeConverter;
import javax.persistence.Convert;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import org.apache.commons.lang3.StringUtils;

import com.estivate.Entity;
import com.estivate.query.Attribute;
import com.estivate.result.ResultMapping.Column;
import com.estivate.util.Chronometer;
import com.estivate.util.EstivateException;
import com.estivate.util.FieldUtils;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EntityMapper<U> extends IMapper<U>{
    final Class<U> targetClass;
    final Constructor<U> constructor;

    final Set<Field> fields;
    final Set<Method> postLoadMethods;
    
    final Chronometer chronometer;
    
    
    
    Map<Integer, String> fieldNames = new HashMap<>();
    
    
    //final DateTimeFormatter dateTimeFormater2 = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss").withZone(ZoneId.systemDefault());
    
    List<Field> columnFields = new ArrayList<>();
    

    public void setColumnNames(String[] columnNames) {
        
        // Map columns to fields
        Entity<U> entity = new Entity<>(targetClass);
        columnFields = new ArrayList<>();
                    
        for(int i = 0; i < columnNames.length; i++) {
            String columnName = columnNames[i];
            Field field = fields.stream().filter(x -> columnName.equals(getFieldName(entity, x))).findFirst().orElse(null);
            if(field != null) {
                while(columnFields.size() <= i) {
                    columnFields.add(null);
                }
                columnFields.set(i, field);
            }
        }
        
    }

    @SneakyThrows
    public EntityMapper(Class<U> targetClass, boolean tracePerformances) {
        chronometer = new Chronometer("Mapper "+targetClass.getSimpleName(), tracePerformances).timeThreshold(100);
        
        
        this.targetClass = targetClass;
        

        // Get constructor
        constructor = targetClass.getConstructor();
        
        // Get Fields
        fields = FieldUtils.getEntityFields(targetClass);
        
        
        // Get PostLoadMethods
        postLoadMethods = FieldUtils.getPostLoadMethods(targetClass);
        
        
        chronometer.step("mapper constructor");
    }
    
    public EntityMapper(Class<U> targetClass) {
        this(targetClass, false);
    }
    
    

    
    
    
    @SneakyThrows
    public U map(String[] row) {
        U obj = constructor.newInstance();
        Entity<U> entity = new Entity<>(targetClass);
        chronometer.step("constructor & entity");
        
        for(int i = 0; i < row.length; i++) {
            if(i >= columnFields.size()) {
                continue;
            }
            Field field = columnFields.get(i);
            chronometer.step("get field");
            if(field != null) {
                setGeneratedField(entity, field, obj, row[i]);
                chronometer.step("generate field "+field.getName());
            }
        }
        for(Method method : postLoadMethods) {
            method.invoke(obj);
            chronometer.step("invoke method "+method.getName());
        }
        return obj;
    }
    
    @SneakyThrows
    public U map(Map<String, String> arguments) {
        
        U obj = constructor.newInstance();
        
        Entity<U> entity = new Entity<>(targetClass);
        
        Class<?> currentClass = targetClass;
        while(currentClass != Object.class) {

            Set<Field> fields = FieldUtils.getEntityFields(currentClass);
            for(Field field : fields) {

                // check if field has mapping annotation
                if(field.getDeclaredAnnotation(ResultMapping.Attribute.class) != null) {
                    ResultMapping.Attribute mappingAnnotation = field.getDeclaredAnnotation(ResultMapping.Attribute.class);

                    Field mappingField = FieldUtils.getEntityFields(mappingAnnotation.entity()).stream().filter(x -> x.getName().equals(mappingAnnotation.attribute())).findFirst().orElse(null);
                    if(mappingField == null){
                        log.error("Field "+mappingAnnotation.attribute()+" not found in entity "+mappingAnnotation.entity());
                        continue;
                    }
                    String value = arguments.get(getFieldName(new Entity<>(mappingAnnotation.entity()), mappingField));
                    setGeneratedField(entity, field, obj, value);
                }
                else if(field.getDeclaredAnnotation(Column.class) != null) {
                    Column mappingAnnotation = field.getDeclaredAnnotation(Column.class);
                    String value = arguments.get(mappingAnnotation.column());
                    setGeneratedField(entity, field, obj, value);
                }
                else{
                    String value = arguments.get(getFieldName(entity, field));
                    setGeneratedField(entity, field, obj, value);	
                }

            }

            currentClass = currentClass.getSuperclass();

        }
        
        Set<Method> methods = FieldUtils.getPostLoadMethods(obj.getClass());
        for(Method method : methods) {
            method.invoke(obj);
        }
        
        return obj;
        
    }

    
    // can this method be used for several entities ?
    public String getFieldName(Entity<?> entity, Field field) {
        int hash = Objects.hash(entity, field);
        String fieldName = fieldNames.get(hash);
        if(fieldName == null) {
            fieldName = nameMapper.mapEntity(entity, field.getName());
            fieldNames.put(hash, fieldName);
        }
        return fieldName;
        
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
	

	
	
