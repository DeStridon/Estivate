package com.estivate.spring;

import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

import com.estivate.context.Context;
import com.estivate.manager.ManagerInterceptor;
import com.estivate.manager.ManagerInterceptor.EntityManager;
import com.estivate.util.FieldUtils;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Spring FactoryBean that creates Estivate manager implementations.
 * 
 * <p>This factory bean is responsible for creating the actual implementation
 * of an abstract EntityManager subclass using ByteBuddy. It autowires the
 * {@link Context} from the application context and also autowires any
 * dependencies declared in the manager class itself.</p>
 * 
 * @param <T> The manager type (must extend EntityManager)
 */
public class EstivateManagerFactoryBean<T extends EntityManager<?>> implements FactoryBean<T> {

    private final Class<T> managerClass;
    
    @Autowired
    private Context context;
    
    @Autowired
    private AutowireCapableBeanFactory autowireCapableBeanFactory;

    public EstivateManagerFactoryBean(Class<T> managerClass) {
        this.managerClass = managerClass;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public T getObject() throws Exception {
        
        // Validate all abstract methods before creating the proxy
        validateAbstractMethods();
        
        T instance = (T) new ByteBuddy()
            .subclass(managerClass)
            .method(ElementMatchers.isAbstract())
            .intercept(MethodDelegation.to(new ManagerInterceptor((Class) managerClass, context)))
            .make()
            .load(managerClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
            .getLoaded()
            .getDeclaredConstructor()
            .newInstance();
        
        // Autowire dependencies declared in the manager class
        autowireCapableBeanFactory.autowireBean(instance);
        
        return instance;
    }
    
    /**
     * Validates all abstract methods in the manager class to ensure they follow
     * the expected naming conventions and have valid field/criterion combinations.
     */
    private void validateAbstractMethods() {
        Class<?> entityClass = getEntityClass();
        List<String> errors = new ArrayList<>();
        
        for (Method method : managerClass.getDeclaredMethods()) {
            if (!Modifier.isAbstract(method.getModifiers())) {
                continue;
            }
            
            try {
                validateMethod(method, entityClass);
            } catch (Exception e) {
                errors.add(String.format("Method '%s': %s", method.getName(), e.getMessage()));
            }
        }
        
        if (!errors.isEmpty()) {
            throw new IllegalStateException(String.format(
                "Invalid EstivateManager '%s'. The following abstract methods have validation errors:%n%s",
                managerClass.getSimpleName(),
                errors.stream().map(e -> "  - " + e).collect(Collectors.joining("\n"))
            ));
        }
    }
    
    /**
     * Extracts the entity class from the manager's generic type parameter.
     */
    private Class<?> getEntityClass() {
        Type genericSuperclass = managerClass.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            Type[] typeArgs = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                return (Class<?>) typeArgs[0];
            }
        }
        throw new IllegalStateException(String.format(
            "Cannot determine entity class for manager '%s'. Ensure it extends EntityManager<T> with a concrete type.",
            managerClass.getSimpleName()
        ));
    }
    
    /**
     * Validates a single abstract method.
     */
    private void validateMethod(Method method, Class<?> entityClass) throws Exception {
        String methodName = method.getName();
        
        // Determine which pattern the method follows
        if (methodName.equals("findAll")) {
            validateFindAllMethod(method, entityClass);
        } else if (methodName.startsWith("findAllBy")) {
            validateQueryMethod(method, entityClass, methodName.substring(9), true, false);
        } else if (methodName.startsWith("findOneBy")) {
            validateQueryMethod(method, entityClass, methodName.substring(9), false, false);
        } else if (methodName.startsWith("countBy")) {
            validateCountMethod(method, entityClass, methodName.substring(7));
        } else if (methodName.startsWith("existsBy")) {
            validateExistsMethod(method, entityClass, methodName.substring(8));
        } else if (methodName.startsWith("findBy")) {
            validateFindByMethod(method, entityClass, methodName.substring(6));
        } else {
            throw new Exception(String.format(
                "Unsupported method name pattern. Method must start with one of: findAll, findAllBy, findOneBy, findBy, countBy, existsBy"
            ));
        }
    }
    
    /**
     * Validates a findAll method (no query suffix).
     */
    private void validateFindAllMethod(Method method, Class<?> entityClass) throws Exception {
        Class<?> returnType = method.getReturnType();
        if (!List.class.isAssignableFrom(returnType)) {
            throw new Exception(String.format(
                "Return type must be List, but found '%s'",
                returnType.getSimpleName()
            ));
        }
        if (method.getParameterCount() != 0) {
            throw new Exception("findAll method must have no parameters");
        }
    }
    
    /**
     * Validates a findBy method which can return either a List or a single entity.
     */
    private void validateFindByMethod(Method method, Class<?> entityClass, String querySuffix) throws Exception {
        Class<?> returnType = method.getReturnType();
        boolean returnsList = List.class.isAssignableFrom(returnType);
        boolean returnsSingle = returnType.equals(entityClass);
        
        if (!returnsList && !returnsSingle) {
            throw new Exception(String.format(
                "Return type must be either List or '%s', but found '%s'",
                entityClass.getSimpleName(),
                returnType.getSimpleName()
            ));
        }
        
        validateQuerySuffix(method, entityClass, querySuffix);
    }
    
    /**
     * Validates a countBy method.
     */
    private void validateCountMethod(Method method, Class<?> entityClass, String querySuffix) throws Exception {
        Class<?> returnType = method.getReturnType();
        if (!isNumericType(returnType)) {
            throw new Exception(String.format(
                "Return type must be a numeric type (int, long, Integer, Long), but found '%s'",
                returnType.getSimpleName()
            ));
        }
        
        validateQuerySuffix(method, entityClass, querySuffix);
    }
    
    /**
     * Validates an existsBy method.
     */
    private void validateExistsMethod(Method method, Class<?> entityClass, String querySuffix) throws Exception {
        Class<?> returnType = method.getReturnType();
        if (returnType != boolean.class && returnType != Boolean.class) {
            throw new Exception(String.format(
                "Return type must be boolean or Boolean, but found '%s'",
                returnType.getSimpleName()
            ));
        }
        
        validateQuerySuffix(method, entityClass, querySuffix);
    }
    
    /**
     * Validates a query method (findAllBy, findOneBy).
     */
    private void validateQueryMethod(Method method, Class<?> entityClass, String querySuffix, 
                                     boolean expectsList, boolean allowBoth) throws Exception {
        Class<?> returnType = method.getReturnType();
        
        if (expectsList) {
            if (!List.class.isAssignableFrom(returnType)) {
                throw new Exception(String.format(
                    "Return type must be List, but found '%s'",
                    returnType.getSimpleName()
                ));
            }
        } else {
            if (!returnType.equals(entityClass)) {
                throw new Exception(String.format(
                    "Return type must be '%s', but found '%s'",
                    entityClass.getSimpleName(),
                    returnType.getSimpleName()
                ));
            }
        }
        
        validateQuerySuffix(method, entityClass, querySuffix);
    }
    
    /**
     * Validates the query suffix (field + criterion combinations) and argument count.
     */
    private void validateQuerySuffix(Method method, Class<?> entityClass, String querySuffix) throws Exception {
        if (querySuffix.isEmpty()) {
            throw new Exception("Query suffix cannot be empty - must specify at least one field condition");
        }
        
        List<String> criterionList = ManagerInterceptor.getMethods().stream()
            .map(x -> x.substring(0, 1).toUpperCase() + x.substring(1))
            .sorted((a, b) -> Integer.compare(b.length(), a.length()))
            .collect(Collectors.toList());
        
        List<String> fieldList = FieldUtils.getEntityFields(entityClass).stream()
            .map(Member::getName)
            .map(x -> x.substring(0, 1).toUpperCase() + x.substring(1))
            .sorted((a, b) -> Integer.compare(b.length(), a.length()))
            .collect(Collectors.toList());
        
        int expectedArgCount = 0;
        int position = 0;
        
        while (position < querySuffix.length()) {
            // Match field
            final int fieldPos = position;
            String field = fieldList.stream()
                .filter(f -> querySuffix.startsWith(f, fieldPos))
                .findFirst()
                .orElse(null);
            
            if (field == null) {
                String remaining = querySuffix.substring(position);
                throw new Exception(String.format(
                    "Unknown field at position %d: '%s'. Available fields: %s",
                    position,
                    remaining.length() > 20 ? remaining.substring(0, 20) + "..." : remaining,
                    fieldList.stream().map(f -> f.substring(0, 1).toLowerCase() + f.substring(1))
                        .collect(Collectors.joining(", "))
                ));
            }
            
            position += field.length();
            
            // Match criterion (optional - defaults to 'eq')
            final int criterionPos = position;
            String criterion = criterionList.stream()
                .filter(c -> querySuffix.startsWith(c, criterionPos))
                .findFirst()
                .orElse("");
            
            position += criterion.length();
            
            // Count expected arguments based on criterion
            String criterionLower = criterion.isEmpty() ? "eq" : 
                criterion.substring(0, 1).toLowerCase() + criterion.substring(1);
            
            if (criterionLower.startsWith("between")) {
                expectedArgCount += 2;
            } else if (criterionLower.equals("isNull") || criterionLower.equals("isNotNull")) {
                // No arguments needed
            } else {
                expectedArgCount += 1;
            }
            
            // Check for 'And' separator
            if (position < querySuffix.length()) {
                if (!querySuffix.startsWith("And", position)) {
                    throw new Exception(String.format(
                        "Expected 'And' at position %d but found '%s'",
                        position,
                        querySuffix.substring(position, Math.min(position + 10, querySuffix.length()))
                    ));
                }
                position += 3;
            }
        }
        
        // Validate argument count
        int actualArgCount = method.getParameterCount();
        if (actualArgCount != expectedArgCount) {
            throw new Exception(String.format(
                "Expected %d parameter(s) based on query conditions, but found %d",
                expectedArgCount,
                actualArgCount
            ));
        }
    }
    
    /**
     * Checks if a type is a numeric type.
     */
    private boolean isNumericType(Class<?> type) {
        return type == int.class || type == Integer.class ||
               type == long.class || type == Long.class ||
               type == short.class || type == Short.class ||
               type == byte.class || type == Byte.class;
    }

    @Override
    public Class<?> getObjectType() {
        return managerClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

