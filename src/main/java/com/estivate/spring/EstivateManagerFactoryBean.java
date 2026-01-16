package com.estivate.spring;

import org.springframework.beans.factory.FactoryBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.config.AutowireCapableBeanFactory;

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

    @Override
    public Class<?> getObjectType() {
        return managerClass;
    }

    @Override
    public boolean isSingleton() {
        return true;
    }
}

