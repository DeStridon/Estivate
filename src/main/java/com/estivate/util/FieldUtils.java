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
            return new Attribute(Estivate.entity(targetClass), propertyName, function, alias);

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
	

	


	public static int indexOf(Collection<?> collection, Object object) {

		List<Object> list = new ArrayList<>(collection);
		for(int i = 0; i < list.size(); i++) {
			if(list.get(i).equals(object)) {
				return i;
			}
		}
		return -1;
	}

	

	

	/**
     * Checks if a field is nullable based on annotations
     */
    public static boolean isNullable(Field field) {
        // Primitives are never nullable
        if (field.getType().isPrimitive()) {
            return false;
        }

        // Check for @NotNull (javax.validation / jakarta.validation)
        if (hasAnnotationByName(field, "javax.validation.constraints.NotNull") || hasAnnotationByName(field, "jakarta.validation.constraints.NotNull")) {
           return false;
        }

        // Check for @Column(nullable = false)
        javax.persistence.Column javaxColumn = field.getDeclaredAnnotation(javax.persistence.Column.class);
        if (javaxColumn != null) {
            return javaxColumn.nullable();
        }
        
        jakarta.persistence.Column jakartaColumn = field.getDeclaredAnnotation(jakarta.persistence.Column.class);
        if (jakartaColumn != null) {
            return jakartaColumn.nullable();
        }

        // Default to nullable for object types
        return true;
    }

	public static boolean hasAnnotationByName(Field field, String annotationClassName) {
        for (java.lang.annotation.Annotation a : field.getDeclaredAnnotations()) {
            if (annotationClassName.equals(a.annotationType().getName())) {
                return true;
            }
        }
        return false;
    }

	/**
     * Extracts default value from entity field annotations
     */
    public static String extractDefaultValue(Field field) {
        // Check @Column(columnDefinition = ...) which might contain DEFAULT
        javax.persistence.Column javaxColumn = field.getDeclaredAnnotation(javax.persistence.Column.class);
        if (javaxColumn != null && javaxColumn.columnDefinition() != null && !javaxColumn.columnDefinition().isEmpty()) {
            String columnDef = javaxColumn.columnDefinition();
            // Try to extract DEFAULT value from columnDefinition
            if (columnDef.toUpperCase().contains("DEFAULT")) {
                // This is a simplified extraction - may need refinement
                int defaultIndex = columnDef.toUpperCase().indexOf("DEFAULT");
                if (defaultIndex >= 0) {
                    String afterDefault = columnDef.substring(defaultIndex + 7).trim();
                    // Extract the value (handles quoted and unquoted values)
                    if (afterDefault.startsWith("'") && afterDefault.contains("'")) {
                        int endQuote = afterDefault.indexOf("'", 1);
                        return afterDefault.substring(1, endQuote);
                    } else {
                        // Unquoted value - take until space or end
                        int spaceIndex = afterDefault.indexOf(" ");
                        if (spaceIndex > 0) {
                            return afterDefault.substring(0, spaceIndex);
                        }
                        return afterDefault;
                    }
                }
            }
        }
        
        jakarta.persistence.Column jakartaColumn = field.getDeclaredAnnotation(jakarta.persistence.Column.class);
        if (jakartaColumn != null && jakartaColumn.columnDefinition() != null && !jakartaColumn.columnDefinition().isEmpty()) {
            String columnDef = jakartaColumn.columnDefinition();
            if (columnDef.toUpperCase().contains("DEFAULT")) {
                int defaultIndex = columnDef.toUpperCase().indexOf("DEFAULT");
                if (defaultIndex >= 0) {
                    String afterDefault = columnDef.substring(defaultIndex + 7).trim();
                    if (afterDefault.startsWith("'") && afterDefault.contains("'")) {
                        int endQuote = afterDefault.indexOf("'", 1);
                        return afterDefault.substring(1, endQuote);
                    } else {
                        int spaceIndex = afterDefault.indexOf(" ");
                        if (spaceIndex > 0) {
                            return afterDefault.substring(0, spaceIndex);
                        }
                        return afterDefault;
                    }
                }
            }
        }
        
        return null;
    }

	/**
     * Checks if a field is auto-increment
     */
    public static boolean isAutoIncrement(Field field) {
        javax.persistence.GeneratedValue javaxGenerated = field.getDeclaredAnnotation(javax.persistence.GeneratedValue.class);
        if (javaxGenerated != null && javaxGenerated.strategy() == javax.persistence.GenerationType.IDENTITY) {
            return true;
        }

        jakarta.persistence.GeneratedValue jakartaGenerated = field.getDeclaredAnnotation(jakarta.persistence.GeneratedValue.class);
        if (jakartaGenerated != null && jakartaGenerated.strategy() == jakarta.persistence.GenerationType.IDENTITY) {
            return true;
        }

        return false;
    }


	/**
	 * Checks if an enum field is stored as STRING
	 */
	public static boolean isEnumeratedAsString(Field field) {
		javax.persistence.Enumerated javaxEnum = field.getDeclaredAnnotation(javax.persistence.Enumerated.class);
		if (javaxEnum != null && javaxEnum.value() == javax.persistence.EnumType.STRING) {
			return true;
		}

		jakarta.persistence.Enumerated jakartaEnum = field.getDeclaredAnnotation(jakarta.persistence.Enumerated.class);
		if (jakartaEnum != null && jakartaEnum.value() == jakarta.persistence.EnumType.STRING) {
			return true;
		}

		return false;
	}


    public static Integer readFieldForLength(Field field) {
        
		javax.persistence.Column javaxColumn = field.getDeclaredAnnotation(javax.persistence.Column.class);
		if (javaxColumn != null && javaxColumn.length() > 0) {
			return javaxColumn.length();
		}

		jakarta.persistence.Column jakartaColumn = field.getDeclaredAnnotation(jakarta.persistence.Column.class);
		if (jakartaColumn != null && jakartaColumn.length() > 0) {
			return jakartaColumn.length();
		}

		return null;

    }


    public static String readFieldForExplicitType(Field field) {
        javax.persistence.Column javaxColumn = field.getDeclaredAnnotation(javax.persistence.Column.class);
        if (javaxColumn != null && javaxColumn.columnDefinition() != null && !javaxColumn.columnDefinition().isEmpty()) {
            return javaxColumn.columnDefinition();
        }

        jakarta.persistence.Column jakartaColumn = field.getDeclaredAnnotation(jakarta.persistence.Column.class);
        if (jakartaColumn != null && jakartaColumn.columnDefinition() != null && !jakartaColumn.columnDefinition().isEmpty()) {
            return jakartaColumn.columnDefinition();
        }
        return null;
    }
    
    public static Boolean parseBoolean(String content) {
    	if(content == null){ return null; }
		else if(content.equals("1") || content.equalsIgnoreCase("true")){ return true; }
		else if(content.equals("0") || content.equalsIgnoreCase("false")){ return false; }
		return null;
    }

	
}
