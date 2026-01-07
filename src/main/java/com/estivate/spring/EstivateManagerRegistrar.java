package com.estivate.spring;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.springframework.beans.factory.support.BeanDefinitionBuilder;
import org.springframework.beans.factory.support.BeanDefinitionRegistry;
import org.springframework.context.annotation.ImportBeanDefinitionRegistrar;
import org.springframework.core.type.AnnotationMetadata;
import org.springframework.util.ClassUtils;
import org.springframework.util.StringUtils;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

/**
 * Registers Estivate manager beans with the Spring application context.
 * 
 * <p>This registrar is triggered by {@link EnableEstivateManagers} and scans
 * for classes annotated with {@link EstivateManager}. For each discovered
 * manager class, it registers an {@link EstivateManagerFactoryBean} that
 * will create the implementation at runtime.</p>
 */
public class EstivateManagerRegistrar implements ImportBeanDefinitionRegistrar {

    @Override
    public void registerBeanDefinitions(AnnotationMetadata importingClassMetadata, BeanDefinitionRegistry registry) {
        
        Map<String, Object> attrs = importingClassMetadata.getAnnotationAttributes(EnableEstivateManagers.class.getName());
        if (attrs == null) {
            return;
        }
        
        Set<String> packagesToScan = getPackagesToScan(importingClassMetadata, attrs);
        
        for (String basePackage : packagesToScan) {
            scanAndRegisterManagers(basePackage, registry);
        }
    }

    private Set<String> getPackagesToScan(AnnotationMetadata metadata, Map<String, Object> attrs) {
        Set<String> packages = new HashSet<>();
        
        // Check basePackages attribute
        String[] basePackages = (String[]) attrs.get("basePackages");
        if (basePackages != null) {
            for (String pkg : basePackages) {
                if (StringUtils.hasText(pkg)) {
                    packages.add(pkg);
                }
            }
        }
        
        // Check basePackageClasses attribute
        Class<?>[] basePackageClasses = (Class<?>[]) attrs.get("basePackageClasses");
        if (basePackageClasses != null) {
            for (Class<?> clazz : basePackageClasses) {
                packages.add(ClassUtils.getPackageName(clazz));
            }
        }
        
        // If no packages specified, use the package of the importing class
        if (packages.isEmpty()) {
            packages.add(ClassUtils.getPackageName(metadata.getClassName()));
        }
        
        return packages;
    }

    private void scanAndRegisterManagers(String basePackage, BeanDefinitionRegistry registry) {
        Reflections reflections = new Reflections(
            new ConfigurationBuilder()
                .forPackage(basePackage)
                .addScanners(Scanners.TypesAnnotated)
        );
        
        Set<Class<?>> managerClasses = reflections.getTypesAnnotatedWith(EstivateManager.class);
        
        for (Class<?> managerClass : managerClasses) {
            registerManagerBean(managerClass, registry);
        }
    }

    private void registerManagerBean(Class<?> managerClass, BeanDefinitionRegistry registry) {
        String beanName = generateBeanName(managerClass);
        
        if (registry.containsBeanDefinition(beanName)) {
            return; // Already registered
        }
        
        BeanDefinitionBuilder builder = BeanDefinitionBuilder
            .genericBeanDefinition(EstivateManagerFactoryBean.class)
            .addConstructorArgValue(managerClass);
        
        registry.registerBeanDefinition(beanName, builder.getBeanDefinition());
    }

    private String generateBeanName(Class<?> managerClass) {
        String simpleName = managerClass.getSimpleName();
        // Convert first character to lowercase (standard Spring convention)
        return Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);
    }
}

