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
import com.estivate.repository.Repository;
import com.estivate.repository.RepositoryQuery;
import com.estivate.repository.RepositoryQueryExecutor;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

/**
 * Spring FactoryBean that creates Estivate repository implementations.
 *
 * <p>Creates the implementation of an abstract {@link Repository} subclass using
 * ByteBuddy. It autowires the {@link Context} and any dependencies declared on
 * the repository class itself.</p>
 *
 * @param <T> The repository type (must extend Repository)
 */
public class EstivateManagerFactoryBean<T extends Repository<?>> implements FactoryBean<T> {

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
        validateAbstractMethods();

        T instance = (T) new ByteBuddy()
            .subclass(managerClass)
            .method(ElementMatchers.isAbstract())
            .intercept(MethodDelegation.to(new RepositoryQueryExecutor((Class) managerClass, context)))
            .make()
            .load(managerClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
            .getLoaded()
            .getDeclaredConstructor()
            .newInstance();

        autowireCapableBeanFactory.autowireBean(instance);

        return instance;
    }

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

    private Class<?> getEntityClass() {
        Type genericSuperclass = managerClass.getGenericSuperclass();
        if (genericSuperclass instanceof ParameterizedType) {
            Type[] typeArgs = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
            if (typeArgs.length > 0 && typeArgs[0] instanceof Class) {
                return (Class<?>) typeArgs[0];
            }
        }
        throw new IllegalStateException(String.format(
            "Cannot determine entity class for manager '%s'. Ensure it extends Repository<T> with a concrete type.",
            managerClass.getSimpleName()
        ));
    }

    private void validateMethod(Method method, Class<?> entityClass) throws Exception {
        String methodName = method.getName();

        if (methodName.equals("findAll")) {
            new RepositoryQuery(method.getDeclaringClass(), method.getName());
        } else if (methodName.startsWith("findAllBy")) {
            new RepositoryQuery(method.getDeclaringClass(), method.getName());
        } else if (methodName.startsWith("findOneBy")) {
            new RepositoryQuery(method.getDeclaringClass(), method.getName());
        } else if (methodName.startsWith("findDistinct")) {
            new RepositoryQuery(method.getDeclaringClass(), method.getName());
        } else if (methodName.startsWith("countBy")) {
            new RepositoryQuery(method.getDeclaringClass(), method.getName());
        } else if (methodName.startsWith("existsBy")) {
            new RepositoryQuery(method.getDeclaringClass(), method.getName());
        } else if (methodName.startsWith("findBy")) {
            new RepositoryQuery(method.getDeclaringClass(), method.getName());
        } else if (methodName.startsWith("find")) {
            new RepositoryQuery(method.getDeclaringClass(), method.getName());
        } else {
            throw new Exception(String.format("Unsupported method name pattern. Method must start with one of: findAll, findAllBy, findOneBy, findDistinct, find<Field>By, findBy, countBy, existsBy"));
        }
    }

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
