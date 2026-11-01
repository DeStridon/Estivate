package com.estivate.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

import com.estivate.context.Context;
import com.estivate.util.EntityMetaCache;

import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class InsertQuery<E> {

    private final Class<E> entity;

    private final EntityMetaCache entityMeta;

    private List<E> values = new ArrayList<>();

    public InsertQuery(Class<E> entity) {
        this.entity = entity;
        this.entityMeta = EntityMetaCache.of(entity);
    }

    private InsertQuery(Class<E> entity, EntityMetaCache entityMeta) {
        this.entity = entity;
        this.entityMeta = entityMeta;
    }

    public List<Field> getFields() {
        return entityMeta.getWritableFields();
    }

    public Field getIdField() {
        return entityMeta.getIdField();
    }

    @SneakyThrows
    public InsertQuery<E> value(E value) {
        for(Field field : entityMeta.getInsertDateFields()) {
            if(field.get(value) == null) {
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

    /**
     * Shallow clone: reuses {@link EntityMetaCache}, copies only the values list.
     */
    public InsertQuery<E> clone(){
        InsertQuery<E> query = new InsertQuery<>(entity, entityMeta);
        query.values = new ArrayList<>(values);
        return query;
    }

}
