package com.estivate.query;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.estivate.context.Context;
import com.estivate.util.ClassMeta;

import lombok.Getter;
import lombok.SneakyThrows;

@Getter
public class InsertQuery<E> {

    private final Class<E> entity;

    private List<E> values = new ArrayList<>();

    private final Set<Field> fields;

    private final Field idField;

    public InsertQuery(Class<E> entity) {
        this.entity = entity;
        ClassMeta meta = ClassMeta.of(entity);
        this.fields = new LinkedHashSet<>(meta.getWritableFields());
        this.idField = meta.getIdField();
    }

    @SneakyThrows
    public InsertQuery<E> value(E value) {
        for(Field field : ClassMeta.of(entity).getInsertDateFields()) {
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

    public InsertQuery<E> clone(){
        InsertQuery<E> query = new InsertQuery<>(entity);
        query.values = new ArrayList<>(values);
        return query;
    }

}
