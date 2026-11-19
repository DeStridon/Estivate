package com.estivate.spring;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;

import com.estivate.context.Context;
import com.estivate.inject.ObjectProvider;

/**
 * {@link ObjectProvider} backed by a Spring {@link ApplicationContext}.
 * Registered by {@link EnableEstivateManagers}, which assigns it to every
 * {@link Context} bean. Applications that do not enable Estivate managers can
 * {@code @Import(SpringObjectProvider.class)} or call {@code context.setProvider} directly.
 */
public class SpringObjectProvider implements ObjectProvider, BeanPostProcessor, ApplicationContextAware, DisposableBean {

    private ApplicationContext applicationContext;

    private final List<Context> bound = new ArrayList<>();

    @Override
    public Object get(Class<?> type) {
        return applicationContext.getBean(type);
    }

    @Override
    public void setApplicationContext(ApplicationContext applicationContext) {
        this.applicationContext = applicationContext;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) {
        if (bean instanceof Context context && context.getObjectProvider() != this) {
            context.setObjectProvider(this);
            bound.add(context);
        }
        return bean;
    }

    @Override
    public void destroy() {
        for (Context context : bound) {
            if (context.getObjectProvider() == this) {
                context.setObjectProvider(null);
            }
        }
        bound.clear();
    }
}
