package com.estivate.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.springframework.context.annotation.Import;

/**
 * Enables automatic registration of Estivate managers as Spring beans.
 * 
 * <p>This annotation should be placed on a {@code @Configuration} class.
 * It will scan for classes annotated with {@link EstivateManager} and 
 * automatically create implementations that can be autowired.</p>
 * 
 * <p>A {@link com.estivate.context.Context} bean must be available in the 
 * application context for the managers to function.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code @Configuration}
 * {@code @EnableEstivateManagers(basePackages = "com.example.managers")}
 * public class AppConfig {
 *     
 *     {@code @Bean}
 *     public Context estivateContext(DataSource dataSource) {
 *         return new MySQLContext(dataSource);
 *     }
 * }
 * </pre>
 * 
 * <p>If {@code basePackages} is not specified, scanning will start from the 
 * package of the class declaring this annotation.</p>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
@Import(EstivateManagerRegistrar.class)
public @interface EnableEstivateManagers {
    
    /**
     * Base packages to scan for {@link EstivateManager} annotated classes.
     * If empty, the package of the annotated configuration class is used.
     */
    String[] basePackages() default {};
    
    /**
     * Type-safe alternative to {@link #basePackages()} for specifying packages 
     * to scan. The package of each class specified will be scanned.
     */
    Class<?>[] basePackageClasses() default {};
}

