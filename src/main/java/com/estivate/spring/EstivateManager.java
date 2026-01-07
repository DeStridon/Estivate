package com.estivate.spring;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an EntityManager subclass for automatic Spring bean registration.
 * 
 * <p>When used in conjunction with {@link EnableEstivateManagers}, classes annotated
 * with {@code @EstivateManager} will be automatically implemented and registered
 * as Spring beans, making them available for autowiring.</p>
 * 
 * <p>Example usage:</p>
 * <pre>
 * {@code @EstivateManager}
 * public abstract class CustomerManager extends EntityManager&lt;CustomerEntity&gt; {
 *     public abstract CustomerEntity findById(long id);
 *     public abstract List&lt;CustomerEntity&gt; findByNameAndEmail(String name, String email);
 * }
 * </pre>
 * 
 * <p>Then in your Spring configuration:</p>
 * <pre>
 * {@code @Configuration}
 * {@code @EnableEstivateManagers(basePackages = "com.example.managers")}
 * public class AppConfig {
 *     {@code @Bean}
 *     public Context estivateContext() {
 *         return new MySQLContext(dataSource);
 *     }
 * }
 * </pre>
 * 
 * <p>Now you can autowire the manager:</p>
 * <pre>
 * {@code @Autowired}
 * private CustomerManager customerManager;
 * </pre>
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface EstivateManager {
}

