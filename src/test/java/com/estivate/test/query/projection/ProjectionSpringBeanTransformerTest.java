package com.estivate.test.query.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.function.Function;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.annotation.DirtiesContext.ClassMode;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Projection;
import com.estivate.spring.EnableEstivateManagers;
import com.estivate.test.TestData;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.query.h2.H2TestDatabase;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * {@link com.estivate.spring.EnableEstivateManagers} replaces the constructor {@code ObjectProvider}
 * with the Spring one, so projection transformers are Spring beans.
 */
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = ProjectionSpringBeanTransformerTest.TestConfig.class)
@DirtiesContext(classMode = ClassMode.AFTER_CLASS)
public class ProjectionSpringBeanTransformerTest {

    @Autowired Context context;

    @BeforeEach
    public void cleanCustomers() {
        Estivate.deleteQuery(CustomerEntity.class).execute(context);
    }

    @Test
    public void extractUsesSpringBeanInsteadOfConstructor() {
        CustomerEntity customer = TestData.createRandomCustomer();
        customer.setName("Alice");
        context.insert(customer);

        SpringBeanNameProjection fromBean = Estivate.selectQuery(CustomerEntity.class)
            .eq(CustomerEntity.class, AbstractEntity.Fields.id, customer.getId())
            .selectAll(SpringBeanNameProjection.class)
            .extractSingle(context, SpringBeanNameProjection.class);

        assertEquals("bean:Alice", fromBean.getName());
    }

    @Test
    public void mapToUsesSpringBean() {
        NameProjection source = new NameProjection();
        source.setName("Alice");

        Named target = Projection.mapTo(source, new Named(), context);

        assertEquals("bean:Alice", target.getName());
    }

    @Test
    public void missingSpringBeanFailsMapping() {
        MissingBeanProjection source = new MissingBeanProjection();
        source.setName("Alice");

        RuntimeException error = assertThrows(RuntimeException.class, () -> Projection.mapTo(source, new Named(), context));
        assertTrue(error.getCause().getMessage().contains(MissingTransformer.class.getName()));
    }

    @Data
    public static class SpringBeanNameProjection {
        @Projection.Attribute(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.name, transformer = PrefixTransformer.class)
        private String name;
    }

    @Data
    @NoArgsConstructor
    public static class NameProjection {
        @Projection.Attribute(entity = Named.class, attribute = "name", transformer = PrefixTransformer.class)
        private String name;
    }

    @Data
    @NoArgsConstructor
    public static class MissingBeanProjection {
        @Projection.Attribute(entity = Named.class, attribute = "name", transformer = MissingTransformer.class)
        private String name;
    }

    @Data
    @NoArgsConstructor
    public static class Named {
        private String name;
    }

    public static class PrefixTransformer implements Function<Object, Object> {
        private final String prefix;

        public PrefixTransformer(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Object apply(Object value) {
            return value instanceof String ? prefix + value : value;
        }
    }

    public static class MissingTransformer implements Function<Object, Object> {
        @Override
        public Object apply(Object value) {
            return value;
        }
    }

    @Configuration
    @EnableEstivateManagers(basePackages = "com.estivate.test.repository")
    static class TestConfig {

        @Bean
        public Context estivateContext() {
            return H2TestDatabase.getContext();
        }

        @Bean
        public PrefixTransformer prefixTransformer() {
            return new PrefixTransformer("bean:");
        }
    }
}
