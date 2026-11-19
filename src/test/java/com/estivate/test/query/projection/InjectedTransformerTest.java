package com.estivate.test.query.projection;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.function.Function;

import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.context.H2Context;
import com.estivate.inject.CacheObjectProvider;
import com.estivate.query.Projection;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * A custom {@link com.estivate.inject.ObjectProvider} on the context supplies transformers.
 */
public class InjectedTransformerTest {

    @Test
    public void mapToUsesContextProvider() {
        Context context = new H2Context(null);
        MarkerTransformer managed = new MarkerTransformer("custom:");
        context.setObjectProvider(type -> {
            if (type == MarkerTransformer.class) {
                return type.cast(managed);
            }
            throw new IllegalStateException("No instance for " + type.getName());
        });

        NameProjection source = new NameProjection();
        source.setName("Alice");

        Named target = Projection.mapTo(source, new Named(), context);

        assertEquals("custom:Alice", target.getName());
    }

    @Test
    public void mapToUsesCachedInstance() {
        Context context = new H2Context(null);
        CacheObjectProvider cache = new CacheObjectProvider();
        cache.set(new MarkerTransformer("cached:"));
        context.setObjectProvider(cache);

        NameProjection source = new NameProjection();
        source.setName("Alice");

        Named target = Projection.mapTo(source, new Named(), context);

        assertEquals("cached:Alice", target.getName());
    }

    @Test
    public void defaultProviderUsesConstructor() {
        Context context = new H2Context(null);
        context.setObjectProvider(null);

        ConstructedNameProjection source = new ConstructedNameProjection();
        source.setName("Alice");

        Named target = Projection.mapTo(source, new Named(), context);

        assertEquals("ctor:Alice", target.getName());
    }

    @Data
    @NoArgsConstructor
    public static class NameProjection {
        @Projection.Attribute(entity = Named.class, attribute = "name", transformer = MarkerTransformer.class)
        private String name;
    }

    @Data
    @NoArgsConstructor
    public static class ConstructedNameProjection {
        @Projection.Attribute(entity = Named.class, attribute = "name", transformer = ConstructorTransformer.class)
        private String name;
    }

    @Data
    @NoArgsConstructor
    public static class Named {
        private String name;
    }

    public static class MarkerTransformer implements Function<Object, Object> {
        private final String prefix;

        public MarkerTransformer(String prefix) {
            this.prefix = prefix;
        }

        @Override
        public Object apply(Object value) {
            return value instanceof String ? prefix + value : value;
        }
    }

    public static class ConstructorTransformer implements Function<Object, Object> {
        @Override
        public Object apply(Object value) {
            return value instanceof String ? "ctor:" + value : value;
        }
    }
}
