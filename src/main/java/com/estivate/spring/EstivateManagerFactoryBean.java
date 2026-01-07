package com.estivate.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;

import com.estivate.context.Context;
import com.estivate.manager.ManagerInterceptor.EntityManager;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.dynamic.loading.ClassLoadingStrategy;
import net.bytebuddy.implementation.MethodDelegation;
import net.bytebuddy.matcher.ElementMatchers;

import com.estivate.manager.ManagerInterceptor;

/**
 * Spring FactoryBean that creates Estivate manager implementations.
 * 
 * <p>This factory bean is responsible for creating the actual implementation
 * of an abstract EntityManager subclass using ByteBuddy. It autowires the
 * {@link Context} from the application context.</p>
 * 
 * @param <T> The manager type (must extend EntityManager)
 */
public class EstivateManagerFactoryBean<T extends EntityManager<?>> implements FactoryBean<T> {

    private final Class<T> managerClass;
    
    @Autowired
    private Context context;

    public EstivateManagerFactoryBean(Class<T> managerClass) {
        this.managerClass = managerClass;
    }

    @Override
    @SuppressWarnings({"unchecked", "rawtypes"})
    public T getObject() throws Exception {
        return (T) new ByteBuddy()
            .subclass(managerClass)
            .method(ElementMatchers.isAbstract())
            .intercept(MethodDelegation.to(new ManagerInterceptor((Class) managerClass, context)))
            .make()
            .load(managerClass.getClassLoader(), ClassLoadingStrategy.Default.INJECTION)
            .getLoaded()
            .getDeclaredConstructor()
            .newInstance();
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

