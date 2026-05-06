package com.estivate.spring;

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
import com.estivate.manager.ManagerQueryWrapper;

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
            new ManagerQueryWrapper(method.getDeclaringClass(), method.getName());
        } else if (methodName.startsWith("findAllBy")) {
            new ManagerQueryWrapper(method.getDeclaringClass(), method.getName());
        } else if (methodName.startsWith("findOneBy")) {
            new ManagerQueryWrapper(method.getDeclaringClass(), method.getName());
        } else if (methodName.startsWith("countBy")) {
            new ManagerQueryWrapper(method.getDeclaringClass(), method.getName());
        } else if (methodName.startsWith("existsBy")) {
            new ManagerQueryWrapper(method.getDeclaringClass(), method.getName());
        } else if (methodName.startsWith("findBy")) {
            new ManagerQueryWrapper(method.getDeclaringClass(), method.getName());
        } else {
            throw new Exception(String.format("Unsupported method name pattern. Method must start with one of: findAll, findAllBy, findOneBy, findBy, countBy, existsBy"));
        }
    }
    
    
    

    
    /**
     * Validates the query suffix (field + criterion combinations) and argument count.
     */
   
    
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

