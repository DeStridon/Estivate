package com.estivate.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import com.estivate.Entity.InsertDate;
import com.estivate.context.Context;
import com.estivate.util.ClassMeta;

import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class InsertQuery<E> {

    private static final Map<Class<?>, InsertMeta> META = new ConcurrentHashMap<>();

    private final Class<E> entity;

    private List<E> values = new ArrayList<>();

    private final Set<Field> fields;

    private Field idField;

    public InsertQuery(Class<E> entity) {
        this.entity = entity;
        InsertMeta meta = META.computeIfAbsent(entity, InsertQuery::buildMeta);
        this.fields = meta.fields;
        this.idField = meta.idField;
    }

    private static InsertMeta buildMeta(Class<?> entity) {
        LinkedHashSet<Field> fields = new LinkedHashSet<>();
        Field idField = null;

        for(Field field : ClassMeta.of(entity).getPersistableFields()) {
            if(field.isAnnotationPresent(javax.persistence.Id.class) || field.isAnnotationPresent(jakarta.persistence.Id.class)) {
                if(idField != null) {
                    throw new IllegalArgumentException("Multiple id fields found for entity: " + entity.getName());
                }
                idField = field;
                continue;
            }
            field.setAccessible(true);
            fields.add(field);
        }

        return new InsertMeta(Collections.unmodifiableSet(fields), idField);
    }

    @SneakyThrows
    public InsertQuery<E> value(E value) {
        for(Field field : fields) {
            if(field.isAnnotationPresent(InsertDate.class)
                    && (field.getType() == java.util.Date.class || field.getType() == java.sql.Date.class)
                    && field.get(value) == null) {
                field.set(value, new Date());
            }
        }
        values.add(value);
        return this;
    }

    public InsertQuery<E> values(Collection<E> values) {
        if(values != null) {
            for(E v : values) {
                value(v);
            }
        }
        return this;
    }

    @SafeVarargs
    public final InsertQuery<E> values(E... values) {
        if(values != null) {
            for(E v : values) {
                value(v);
            }
        }
        return this;
    }

    public void execute(Context context){
        context.execute(this);
    }

    public InsertQuery<E> clone(){
        InsertQuery<E> query = new InsertQuery<>(entity);
        query.values = new ArrayList<>(values);
        return query;
    }

    private static final class InsertMeta {
        private final Set<Field> fields;
        private final Field idField;

        private InsertMeta(Set<Field> fields, Field idField) {
            this.fields = fields;
            this.idField = idField;
        }
    }

}
