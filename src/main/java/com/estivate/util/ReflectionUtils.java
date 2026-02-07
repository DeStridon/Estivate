package com.estivate.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.reflections.Reflections;
import org.reflections.scanners.Scanners;
import org.reflections.util.ConfigurationBuilder;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ReflectionUtils {


     /**
     * Scans the specified packages for classes annotated with @Entity (javax or jakarta)
     */
     public static List<Class<?>> scanPackagesForEntities(List<String> packages) {
        List<Class<?>> entities = new ArrayList<>();
        
        for (String packageName : packages) {
            try {
                Reflections reflections = new Reflections(
                    new ConfigurationBuilder()
                        .forPackage(packageName)
                        .setScanners(Scanners.TypesAnnotated)
                );
                
                // Find classes with javax.persistence.Entity
                Set<Class<?>> javaxEntities = reflections.getTypesAnnotatedWith(javax.persistence.Entity.class);
                entities.addAll(javaxEntities);
                
                // Find classes with jakarta.persistence.Entity
                Set<Class<?>> jakartaEntities = reflections.getTypesAnnotatedWith(jakarta.persistence.Entity.class);
                entities.addAll(jakartaEntities);
                
                log.debug("Package '{}': found {} javax entities, {} jakarta entities", packageName, javaxEntities.size(), jakartaEntities.size());
                
            } catch (Exception e) {
                log.warn("Failed to scan package '{}': {}", packageName, e.getMessage());
            }
        }
        
        // Remove duplicates (in case a class has both annotations)
        return entities.stream().distinct().collect(Collectors.toList());
    }

}
