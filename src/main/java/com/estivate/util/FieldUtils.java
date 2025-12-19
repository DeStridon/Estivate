package com.estivate.util;

import java.io.Serializable;
import java.lang.annotation.Annotation;
import java.lang.invoke.SerializedLambda;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.query.Attribute;
import com.estivate.query.Projection;
import com.estivate.query.SelectQuery;
import com.estivate.result.EntityMapper.ColumnMapping;

import lombok.extern.slf4j.Slf4j;


@Slf4j
public class FieldUtils {
	
	static Map<Class<?>, Set<Method>> classPostLoadMethods = new HashMap<>();
	
	static Map<Class<?>, Set<Field>> classFields = new HashMap<>();
	
	static Map<Integer, String> fieldNames = new HashMap<>();
	
	public static Set<Field> getEntityFields(Class<?> objectClass){
		
		if(objectClass == null || objectClass == Object.class || objectClass == CachedEntity.class){
			return new HashSet<>();
		}
		
		Set<Field> fields = classFields.get(objectClass);
		if(fields == null) {
			fields = new HashSet<>();
			fields.addAll(getEntityFields(objectClass.getSuperclass()));
			
			
			for(Field field : objectClass.getDeclaredFields()) {
				
				// avoid synthetic fields
				if(field.isSynthetic()) {
					continue;
				}
				if(field.isAnnotationPresent(javax.persistence.Transient.class) || field.isAnnotationPresent(jakarta.persistence.Transient.class)) {
					continue;
				}
				if(field.getType() == org.slf4j.Logger.class) {
					continue;
				}
				
				field.setAccessible(true);
				
				fields.add(field);
				
			}
				
			classFields.put(objectClass, fields);
		
		}
		
		return fields;
		
	}
	
	
	public static Field findField(Class<? extends Object> objectClass, String attribute) {
		
		Field result = getEntityFields(objectClass).stream().filter(x -> x.getName().equals(attribute)).findFirst().orElse(null);
		return result;
				
	}
	
	public static Set<Method> getEntityMethods(Class<? extends Object> objectClass){
		
		if(objectClass == null) {
			return new HashSet<>();
		}
		
		Set<Method> methods = new HashSet<>();
		for(Method method : objectClass.getDeclaredMethods()) {
			methods.add(method);
		}
		
		methods.addAll(getEntityMethods(objectClass.getSuperclass()));

		return methods;

	}

	
	
	public static Set<Method> findMethodWithAnnotation(Class<? extends Object> objectClass, Class<? extends Annotation> annotation) {
		return getEntityMethods(objectClass).stream().filter(x -> x.isAnnotationPresent(annotation)).collect(Collectors.toSet());
	}
	
	public static Set<Method> getPostLoadMethods(Class<? extends Object> objectClass) {
		
		Set<Method> methods = classPostLoadMethods.get(objectClass);
		if(methods == null) {
			methods = getEntityMethods(objectClass).stream().filter(x -> x.isAnnotationPresent(javax.persistence.PostLoad.class) || x.isAnnotationPresent(jakarta.persistence.PostLoad.class)).collect(Collectors.toSet());
			classPostLoadMethods.put(objectClass, methods);
		}
		
		return methods;
	
	}
	
	/**
	 * Gets the ID field from an entity class
	 */
	public static Field getIdField(Class<?> entityClass) {
		if(entityClass == null) {
			return null;
		}
		for(Field field : FieldUtils.getEntityFields(entityClass)) {
			if(field.isAnnotationPresent(javax.persistence.Id.class) || field.isAnnotationPresent(jakarta.persistence.Id.class)) {
				return field;
			}
		}
		return null;
	}
	
	
	/**
	 * Invokes methods with a specific annotation on an entity
	 */
	public static void invokeLifecycleMethods(Object entity, Class<? extends Annotation> annotationClass) {
		try {
			for(Method method : FieldUtils.findMethodWithAnnotation(entity.getClass(), annotationClass)) {
				method.setAccessible(true);
				method.invoke(entity);
			}
		} catch (Exception e) {
			log.error("Error invoking lifecycle method with annotation " + annotationClass.getSimpleName(), e);
		}
	}
	
	
	

	
	public static <E, P> Attribute attributeFromLambda(AttributeGetter<E, P> getter, Attribute.Function function, String alias) {
		try {
            SerializedLambda sl = serializeLambda(getter);

            // target class: parse from instantiatedMethodType
			String methodType = sl.getInstantiatedMethodType(); // e.g. "(Lcom/example/Person;)Ljava/lang/Long;"
			String classInternalName = methodType.substring(2, methodType.indexOf(';'));
			Class<?> targetClass = Class.forName(classInternalName.replace('/', '.'));
            

			String implMethod = sl.getImplMethodName();

            String propertyName = methodToProperty(implMethod);
            return new Attribute(Estivate.entity(targetClass), propertyName, null, null);

        } catch (Exception e) {
            throw new RuntimeException("Failed to extract property name from lambda", e);
        }
	}

	


	
	
	// Correct functional interface: matches Person::getName
    @FunctionalInterface
    public interface AttributeGetter<T, R> extends Serializable {
        R get(T bean);
    }

    

    private static SerializedLambda serializeLambda(Serializable lambda) throws Exception {
        Method m = lambda.getClass().getDeclaredMethod("writeReplace");
        m.setAccessible(true);
        return (SerializedLambda) m.invoke(lambda);
    }

    private static String methodToProperty(String name) {
        if (name.startsWith("get") && name.length() > 3) {
            String base = name.substring(3);
            return Character.toLowerCase(base.charAt(0)) + base.substring(1);
        }
        if (name.startsWith("is") && name.length() > 2) {
            String base = name.substring(2);
            return Character.toLowerCase(base.charAt(0)) + base.substring(1);
        }
        throw new IllegalArgumentException("Not a getter method: " + name);
    }
	

	public static List<ColumnMapping> getColumnMappings(SelectQuery<?> query, Entity<?> entityClass) {
		List<ColumnMapping> columnMappings = new ArrayList<>(query.getSelects().size());

		for(int i = 0; i < query.getSelects().size(); i++) {
			columnMappings.add(null);
		}

		for(Field field : getEntityFields(entityClass.entity)) {
			ColumnMapping columnMapping = getColumnMapping(entityClass, field);

			
			int index = indexOf(query.getSelects(), columnMapping.getAttribute());

			if(index != -1) {
				columnMappings.set(index, columnMapping);
			} else {
				log.error("Field {} is not in the query", field.getName());
			}

		}
		return columnMappings;
	}


	public static int indexOf(Collection<?> collection, Object object) {

		List<Object> list = new ArrayList<>(collection);
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).equals(object)) {
				return i;
			}
		}
		return -1;
	}

	

	public static ColumnMapping getColumnMapping(Entity<?> entityClass, Field field) {

		Projection.Attribute attributeAnnotation = field.getDeclaredAnnotation(Projection.Attribute.class);

		if (attributeAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(attributeAnnotation.entity(), attributeAnnotation.attribute(), attributeAnnotation.alias()), field);
		}

		Projection.Count countAnnotation = field.getDeclaredAnnotation(Projection.Count.class);
		if (countAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(countAnnotation.entity(), countAnnotation.attribute(), Estivate.Functions.count, countAnnotation.alias()), field);
		}

		Projection.Sum sumAnnotation = field.getDeclaredAnnotation(Projection.Sum.class);
		if (sumAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(sumAnnotation.entity(), sumAnnotation.attribute(), Estivate.Functions.sum, sumAnnotation.alias()), field);
		}

		Projection.Min minAnnotation = field.getDeclaredAnnotation(Projection.Min.class);
		if (minAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(minAnnotation.entity(), minAnnotation.attribute(), Estivate.Functions.min, minAnnotation.alias()), field);
		}

		Projection.Max maxAnnotation = field.getDeclaredAnnotation(Projection.Max.class);
		if (maxAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(maxAnnotation.entity(), maxAnnotation.attribute(), Estivate.Functions.max, maxAnnotation.alias()), field);
		}

		Projection.Avg avgAnnotation = field.getDeclaredAnnotation(Projection.Avg.class);
		if (avgAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(avgAnnotation.entity(), avgAnnotation.attribute(), Estivate.Functions.avg, avgAnnotation.alias()), field);
		}

		Projection.Function functionAnnotation = field.getDeclaredAnnotation(Projection.Function.class);
		if (functionAnnotation != null) {
			return new ColumnMapping(Estivate.attribute(functionAnnotation.entity(), functionAnnotation.attribute(), Estivate.function(functionAnnotation.functionPrefix(), functionAnnotation.functionSuffix()), functionAnnotation.alias()), field);
		}

		return new ColumnMapping(Estivate.attribute(entityClass, field.getName()), field);
	}

	
}
