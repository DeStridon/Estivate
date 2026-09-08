package com.estivate.result;

import java.lang.reflect.Field;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Attribute;
import com.estivate.query.Projection;
import com.estivate.query.SelectQuery;
import com.estivate.result.IMapper.AttributeMapper;
import com.estivate.result.IMapper.BooleanMapper;
import com.estivate.result.IMapper.DateMapper;
import com.estivate.result.IMapper.DoubleMapper;
import com.estivate.result.IMapper.FloatMapper;
import com.estivate.result.IMapper.InstantMapper;
import com.estivate.result.IMapper.IntegerMapper;
import com.estivate.result.IMapper.LocalDateTimeMapper;
import com.estivate.result.IMapper.LongMapper;
import com.estivate.result.IMapper.OrdinalEnumMapper;
import com.estivate.result.IMapper.ShortMapper;
import com.estivate.result.IMapper.StringEnumMapper;
import com.estivate.result.IMapper.StringMapper;
import com.estivate.util.FieldUtils;
import com.estivate.util.FieldUtils.AttributeGetter;

import lombok.Data;
import lombok.SneakyThrows;
import lombok.experimental.SuperBuilder;
import lombok.extern.slf4j.Slf4j;



@Slf4j
@Data
@SuperBuilder
public class ResultTable implements Iterable<ResultRow>{

	final Context context;
    final List<String> columnNames;
    final SelectQuery<?> query;

    final List<ResultRow> rows;

    public ResultTable(Context context, String[] columnNames, SelectQuery<?> query){
    	this.context = context;
    	this.columnNames = Arrays.asList(columnNames);
    	this.query = query;
    	this.rows = new ArrayList<>();
    }


    public ResultRow addRow(String[] values) {
    	ResultRow row = new ResultRow(this, values);
    	rows.add(row);
    	return row;
    }

    // ==================== SINGLE VALUE MAPPING ====================

    public <T> T asSingleMapped(IMapper<T> mapper, int index) { return rows.isEmpty() ? null : mapper.map(rows.get(0).getColumnValues()[index]); }
    public <T> T asSingleMapped(IMapper<T> mapper) { return asSingleMapped(mapper, 0); }
    
    // Entity mapping
    public <T> T asSingle(Class<T> entity) { return rows.isEmpty() ? null : new EntityMapper<>(context, query, new Entity<>(entity)).map(rows.get(0).getColumnValues()); }
    public <T> T asSingle(Entity<T> entity) { return rows.isEmpty() ? null : new EntityMapper<>(context, query, entity).map(rows.get(0).getColumnValues()); }
    
    // Attribute mapping
    public Object asSingle(Attribute attribute) { return asSingleMapped(new AttributeMapper(context, attribute.getEntity().entity, attribute.attribute), indexOf(attribute)); }
    public Object asSingle(Class<?> entity, String attribute) { return asSingleMapped(new AttributeMapper(context, entity, attribute), indexOf(Estivate.attribute(entity, attribute))); }
    public Object asSingle(Entity<?> entity, String attribute) { return asSingleMapped(new AttributeMapper(context, entity.entity, attribute), indexOf(Estivate.attribute(entity, attribute))); }
    public <T, P> P asSingle(AttributeGetter<T, P> attributeGetter) { return (P) asSingle(Estivate.attribute(attributeGetter)); }


    // Primitive type mapping
    public String asSingleString() { return asSingleMapped(new StringMapper()); }
    public String asSingleString(Attribute attribute) { return asSingleMapped(new StringMapper(), indexOf(attribute)); }
    public String asSingleString(Class<?> entity, String attributeName) { return asSingleString(Estivate.attribute(entity, attributeName)); }
    public String asSingleString(Entity<?> entity, String attributeName) { return asSingleString(Estivate.attribute(entity, attributeName)); }
    public <T, P> String asSingleString(AttributeGetter<T, P> attributeGetter) { return asSingleString(Estivate.attribute(attributeGetter)); }
    public String asSingleString(String columnName) { return asSingleMapped(new StringMapper(), columnNames.indexOf(columnName)); }

    public Short asSingleShort() { return asSingleMapped(new ShortMapper()); }
    public Short asSingleShort(Attribute attribute) { return asSingleMapped(new ShortMapper(), indexOf(attribute)); }
    public Short asSingleShort(Class<?> entity, String attributeName) { return asSingleShort(Estivate.attribute(entity, attributeName)); }
    public Short asSingleShort(Entity<?> entity, String attributeName) { return asSingleShort(Estivate.attribute(entity, attributeName)); }
    public <T, P> Short asSingleShort(AttributeGetter<T, P> attributeGetter) { return asSingleShort(Estivate.attribute(attributeGetter)); }
    public Short asSingleShort(String columnName) { return asSingleMapped(new ShortMapper(), columnNames.indexOf(columnName)); }

    public Integer asSingleInteger() { return asSingleMapped(new IntegerMapper()); }
    public Integer asSingleInteger(Attribute attribute) { return asSingleMapped(new IntegerMapper(), indexOf(attribute)); }
    public Integer asSingleInteger(Class<?> entity, String attributeName) { return asSingleInteger(Estivate.attribute(entity, attributeName)); }
    public Integer asSingleInteger(Entity<?> entity, String attributeName) { return asSingleInteger(Estivate.attribute(entity, attributeName)); }
    public <T, P> Integer asSingleInteger(AttributeGetter<T, P> attributeGetter) { return asSingleInteger(Estivate.attribute(attributeGetter)); }
    public Integer asSingleInteger(String columnName) { return asSingleMapped(new IntegerMapper(), columnNames.indexOf(columnName)); }

    public Long asSingleLong() { return asSingleMapped(new LongMapper()); }
    public Long asSingleLong(Attribute attribute) { return asSingleMapped(new LongMapper(), indexOf(attribute)); }
    public Long asSingleLong(Class<?> entity, String attributeName) { return asSingleLong(Estivate.attribute(entity, attributeName)); }
    public Long asSingleLong(Entity<?> entity, String attributeName) { return asSingleLong(Estivate.attribute(entity, attributeName)); }
    public <T, P> Long asSingleLong(AttributeGetter<T, P> attributeGetter) { return asSingleLong(Estivate.attribute(attributeGetter)); }
    public Long asSingleLong(String columnName) { return asSingleMapped(new LongMapper(), columnNames.indexOf(columnName)); }

    public Float asSingleFloat() { return asSingleMapped(new FloatMapper()); }
    public Float asSingleFloat(Attribute attribute) { return asSingleMapped(new FloatMapper(), indexOf(attribute)); }
    public Float asSingleFloat(Class<?> entity, String attributeName) { return asSingleFloat(Estivate.attribute(entity, attributeName)); }
    public Float asSingleFloat(Entity<?> entity, String attributeName) { return asSingleFloat(Estivate.attribute(entity, attributeName)); }
    public <T, P> Float asSingleFloat(AttributeGetter<T, P> attributeGetter) { return asSingleFloat(Estivate.attribute(attributeGetter)); }
    public Float asSingleFloat(String columnName) { return asSingleMapped(new FloatMapper(), columnNames.indexOf(columnName)); }

    public Double asSingleDouble() { return asSingleMapped(new DoubleMapper()); }
    public Double asSingleDouble(Attribute attribute) { return asSingleMapped(new DoubleMapper(), indexOf(attribute)); }
    public Double asSingleDouble(Class<?> entity, String attributeName) { return asSingleDouble(Estivate.attribute(entity, attributeName)); }
    public Double asSingleDouble(Entity<?> entity, String attributeName) { return asSingleDouble(Estivate.attribute(entity, attributeName)); }
    public <T, P> Double asSingleDouble(AttributeGetter<T, P> attributeGetter) { return asSingleDouble(Estivate.attribute(attributeGetter)); }
    public Double asSingleDouble(String columnName) { return asSingleMapped(new DoubleMapper(), columnNames.indexOf(columnName)); }
    
    public Boolean asSingleBoolean() { return asSingleMapped(new BooleanMapper()); }
    public Boolean asSingleBoolean(Attribute attribute) { return asSingleMapped(new BooleanMapper(), indexOf(attribute)); }
    public Boolean asSingleBoolean(Class<?> entity, String attributeName) { return asSingleBoolean(Estivate.attribute(entity, attributeName)); }
    public Boolean asSingleBoolean(Entity<?> entity, String attributeName) { return asSingleBoolean(Estivate.attribute(entity, attributeName)); }
    public <T, P> Boolean asSingleBoolean(AttributeGetter<T, P> attributeGetter) { return asSingleBoolean(Estivate.attribute(attributeGetter)); }
    public Boolean asSingleBoolean(String columnName) { return asSingleMapped(new BooleanMapper(), columnNames.indexOf(columnName)); }

    public Date asSingleDate() { return asSingleMapped(new DateMapper(context)); }
    public Date asSingleDate(Attribute attribute) { return asSingleMapped(new DateMapper(context), indexOf(attribute)); }
    public Date asSingleDate(Class<?> entity, String attributeName) { return asSingleDate(Estivate.attribute(entity, attributeName)); }
    public Date asSingleDate(Entity<?> entity, String attributeName) { return asSingleDate(Estivate.attribute(entity, attributeName)); }
    public <T, P> Date asSingleDate(AttributeGetter<T, P> attributeGetter) { return asSingleDate(Estivate.attribute(attributeGetter)); }
    public Date asSingleDate(String columnName) { return asSingleMapped(new DateMapper(context), columnNames.indexOf(columnName)); }

    public LocalDateTime asSingleLocalDateTime() { return asSingleMapped(new LocalDateTimeMapper()); }
    public LocalDateTime asSingleLocalDateTime(Attribute attribute) { return asSingleMapped(new LocalDateTimeMapper(), indexOf(attribute)); }
    public LocalDateTime asSingleLocalDateTime(Class<?> entity, String attributeName) { return asSingleLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public LocalDateTime asSingleLocalDateTime(Entity<?> entity, String attributeName) { return asSingleLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public <T, P> LocalDateTime asSingleLocalDateTime(AttributeGetter<T, P> attributeGetter) { return asSingleLocalDateTime(Estivate.attribute(attributeGetter)); }
    public LocalDateTime asSingleLocalDateTime(String columnName) { return asSingleMapped(new LocalDateTimeMapper(), columnNames.indexOf(columnName)); }

    public Instant asSingleInstant() { return asSingleMapped(new InstantMapper(context)); }
    public Instant asSingleInstant(Attribute attribute) { return asSingleMapped(new InstantMapper(context), indexOf(attribute)); }
    public Instant asSingleInstant(Class<?> entity, String attributeName) { return asSingleInstant(Estivate.attribute(entity, attributeName)); }
    public Instant asSingleInstant(Entity<?> entity, String attributeName) { return asSingleInstant(Estivate.attribute(entity, attributeName)); }
    public <T, P> Instant asSingleInstant(AttributeGetter<T, P> attributeGetter) { return asSingleInstant(Estivate.attribute(attributeGetter)); }
    public Instant asSingleInstant(String columnName) { return asSingleMapped(new InstantMapper(context), columnNames.indexOf(columnName)); }

    // Enum mapping
    public <E extends Enum<E>> E asSingleStringEnum(Class<E> enumClass) { return asSingleMapped(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> E asSingleOrdinalEnum(Class<E> enumClass) { return asSingleMapped(new OrdinalEnumMapper<>(enumClass)); }


    // ==================== OPTIONAL MAPPING ====================

    public <T> Optional<T> asOptional(Class<T> entity) { return Optional.ofNullable(asSingle(entity)); }
    public <T> Optional<T> asOptional(Entity<T> entity) { return Optional.ofNullable(asSingle(entity)); }


    public <T> Optional<T> asOptionalMapped(IMapper<T> mapper) { return Optional.ofNullable(asSingleMapped(mapper)); }
    public Optional<?> asOptional(Attribute attribute) { return Optional.ofNullable(asSingle(attribute)); }
    public Optional<?> asOptional(Class<?> entity, String attributeName) { return asOptional(Estivate.attribute(entity, attributeName)); }
    public Optional<?> asOptional(Entity<?> entity, String attributeName) { return asOptional(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<P> asOptional(AttributeGetter<T, P> attributeGetter) { return (Optional<P>) asOptional(Estivate.attribute(attributeGetter)); }

    public Optional<String> asOptionalString() { return Optional.ofNullable(asSingleString()); }
    public Optional<String> asOptionalString(Attribute attribute) { return Optional.ofNullable(asSingleString(attribute)); }
    public Optional<String> asOptionalString(Class<?> entity, String attributeName) { return asOptionalString(Estivate.attribute(entity, attributeName)); }
    public Optional<String> asOptionalString(Entity<?> entity, String attributeName) { return asOptionalString(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<String> asOptionalString(AttributeGetter<T, P> attributeGetter) { return asOptionalString(Estivate.attribute(attributeGetter)); }
    public Optional<String> asOptionalString(String columnName) { return Optional.ofNullable(asSingleString(columnName)); }

    public Optional<Short> asOptionalShort() { return Optional.ofNullable(asSingleShort()); }
    public Optional<Short> asOptionalShort(Attribute attribute) { return Optional.ofNullable(asSingleShort(attribute)); }
    public Optional<Short> asOptionalShort(Class<?> entity, String attributeName) { return asOptionalShort(Estivate.attribute(entity, attributeName)); }
    public Optional<Short> asOptionalShort(Entity<?> entity, String attributeName) { return asOptionalShort(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Short> asOptionalShort(AttributeGetter<T, P> attributeGetter) { return asOptionalShort(Estivate.attribute(attributeGetter)); }
    public Optional<Short> asOptionalShort(String columnName) { return Optional.ofNullable(asSingleShort(columnName)); }

    public Optional<Integer> asOptionalInteger() { return Optional.ofNullable(asSingleInteger()); }
    public Optional<Integer> asOptionalInteger(Attribute attribute) { return Optional.ofNullable(asSingleInteger(attribute)); }
    public Optional<Integer> asOptionalInteger(Class<?> entity, String attributeName) { return asOptionalInteger(Estivate.attribute(entity, attributeName)); }
    public Optional<Integer> asOptionalInteger(Entity<?> entity, String attributeName) { return asOptionalInteger(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Integer> asOptionalInteger(AttributeGetter<T, P> attributeGetter) { return asOptionalInteger(Estivate.attribute(attributeGetter)); }
    public Optional<Integer> asOptionalInteger(String columnName) { return Optional.ofNullable(asSingleInteger(columnName)); }

    public Optional<Long> asOptionalLong() { return Optional.ofNullable(asSingleLong()); }
    public Optional<Long> asOptionalLong(Attribute attribute) { return Optional.ofNullable(asSingleLong(attribute)); }
    public Optional<Long> asOptionalLong(Class<?> entity, String attributeName) { return asOptionalLong(Estivate.attribute(entity, attributeName)); }
    public Optional<Long> asOptionalLong(Entity<?> entity, String attributeName) { return asOptionalLong(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Long> asOptionalLong(AttributeGetter<T, P> attributeGetter) { return asOptionalLong(Estivate.attribute(attributeGetter)); }
    public Optional<Long> asOptionalLong(String columnName) { return Optional.ofNullable(asSingleLong(columnName)); }

    public Optional<Float> asOptionalFloat() { return Optional.ofNullable(asSingleFloat()); }
    public Optional<Float> asOptionalFloat(Attribute attribute) { return Optional.ofNullable(asSingleFloat(attribute)); }
    public Optional<Float> asOptionalFloat(Class<?> entity, String attributeName) { return asOptionalFloat(Estivate.attribute(entity, attributeName)); }
    public Optional<Float> asOptionalFloat(Entity<?> entity, String attributeName) { return asOptionalFloat(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Float> asOptionalFloat(AttributeGetter<T, P> attributeGetter) { return asOptionalFloat(Estivate.attribute(attributeGetter)); }
    public Optional<Float> asOptionalFloat(String columnName) { return Optional.ofNullable(asSingleFloat(columnName)); }

    public Optional<Double> asOptionalDouble() { return Optional.ofNullable(asSingleDouble()); }
    public Optional<Double> asOptionalDouble(Attribute attribute) { return Optional.ofNullable(asSingleDouble(attribute)); }
    public Optional<Double> asOptionalDouble(Class<?> entity, String attributeName) { return asOptionalDouble(Estivate.attribute(entity, attributeName)); }
    public Optional<Double> asOptionalDouble(Entity<?> entity, String attributeName) { return asOptionalDouble(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Double> asOptionalDouble(AttributeGetter<T, P> attributeGetter) { return asOptionalDouble(Estivate.attribute(attributeGetter)); }
    public Optional<Double> asOptionalDouble(String columnName) { return Optional.ofNullable(asSingleDouble(columnName)); }

    public Optional<Boolean> asOptionalBoolean() { return Optional.ofNullable(asSingleBoolean()); }
    public Optional<Boolean> asOptionalBoolean(Attribute attribute) { return Optional.ofNullable(asSingleBoolean(attribute)); }
    public Optional<Boolean> asOptionalBoolean(Class<?> entity, String attributeName) { return asOptionalBoolean(Estivate.attribute(entity, attributeName)); }
    public Optional<Boolean> asOptionalBoolean(Entity<?> entity, String attributeName) { return asOptionalBoolean(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Boolean> asOptionalBoolean(AttributeGetter<T, P> attributeGetter) { return asOptionalBoolean(Estivate.attribute(attributeGetter)); }
    public Optional<Boolean> asOptionalBoolean(String columnName) { return Optional.ofNullable(asSingleBoolean(columnName)); }

    public Optional<Date> asOptionalDate() { return Optional.ofNullable(asSingleDate()); }
    public Optional<Date> asOptionalDate(Attribute attribute) { return Optional.ofNullable(asSingleDate(attribute)); }
    public Optional<Date> asOptionalDate(Class<?> entity, String attributeName) { return asOptionalDate(Estivate.attribute(entity, attributeName)); }
    public Optional<Date> asOptionalDate(Entity<?> entity, String attributeName) { return asOptionalDate(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Date> asOptionalDate(AttributeGetter<T, P> attributeGetter) { return asOptionalDate(Estivate.attribute(attributeGetter)); }
    public Optional<Date> asOptionalDate(String columnName) { return Optional.ofNullable(asSingleDate(columnName)); }

    public Optional<LocalDateTime> asOptionalLocalDateTime() { return Optional.ofNullable(asSingleLocalDateTime()); }
    public Optional<LocalDateTime> asOptionalLocalDateTime(Attribute attribute) { return Optional.ofNullable(asSingleLocalDateTime(attribute)); }
    public Optional<LocalDateTime> asOptionalLocalDateTime(Class<?> entity, String attributeName) { return asOptionalLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public Optional<LocalDateTime> asOptionalLocalDateTime(Entity<?> entity, String attributeName) { return asOptionalLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<LocalDateTime> asOptionalLocalDateTime(AttributeGetter<T, P> attributeGetter) { return asOptionalLocalDateTime(Estivate.attribute(attributeGetter)); }
    public Optional<LocalDateTime> asOptionalLocalDateTime(String columnName) { return Optional.ofNullable(asSingleLocalDateTime(columnName)); }

    public Optional<Instant> asOptionalInstant() { return Optional.ofNullable(asSingleInstant()); }
    public Optional<Instant> asOptionalInstant(Attribute attribute) { return Optional.ofNullable(asSingleInstant(attribute)); }
    public Optional<Instant> asOptionalInstant(Class<?> entity, String attributeName) { return asOptionalInstant(Estivate.attribute(entity, attributeName)); }
    public Optional<Instant> asOptionalInstant(Entity<?> entity, String attributeName) { return asOptionalInstant(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Instant> asOptionalInstant(AttributeGetter<T, P> attributeGetter) { return asOptionalInstant(Estivate.attribute(attributeGetter)); }
    public Optional<Instant> asOptionalInstant(String columnName) { return Optional.ofNullable(asSingleInstant(columnName)); }


    // ==================== LIST MAPPING ====================

    public <T> List<T> asListMapped(IMapper<T> mapper, int index) {
        List<T> results = new ArrayList<>();
        for(ResultRow row : rows) {
            T result = mapper.map(row.getColumnValues()[index]);
            results.add(result);
        }
        return results;
    }
    public <T> List<T> asListMapped(IMapper<T> mapper) { return asListMapped(mapper, 0); }

    // Entity list mapping
    public <T> List<T> asList(Class<T> entity) { return asList(new Entity<>(entity)); }
    public <T> List<T> asList(Entity<T> entity) { 

        EntityMapper<T> entityMapper = new EntityMapper<>(context, query, entity);

        // If a key is defined
        Projection.NestedBy key = entity.entity.getDeclaredAnnotation(Projection.NestedBy.class);
        if(key == null) {
            List<T> results = new ArrayList<>();
            for(ResultRow row : rows) {
                T result = entityMapper.map(row.getColumnValues());
                results.add(result);
            }
            return results;
        }

        Map<Object, T> resultsMap = new LinkedHashMap<>();

        // TODO : 
        for(ResultRow row : rows) {
            Object keyValue = row.as(key.entity(), key.attribute());
            T object = resultsMap.get(keyValue);
            if(object == null) {
                object = entityMapper.map(row.getColumnValues());
                resultsMap.put(keyValue, object);
            }
            else{
                entityMapper.map(row.getColumnValues(), object);
            }

        }

        return new ArrayList<>(resultsMap.values());

    }

    /**
     * Maps this result into already-fetched projection rows, keyed by {@link Projection.NestedBy}.
     * Nested {@link Projection.Nested} fields are filled on the existing instances.
     */
    @SneakyThrows
    public <T> List<T> asList(Class<T> entityClass, List<T> existing) {
        if (existing == null || existing.isEmpty()) {
            return asList(entityClass);
        }

        Projection.NestedBy key = entityClass.getDeclaredAnnotation(Projection.NestedBy.class);
        if (key == null) {
            throw new IllegalArgumentException("asList with existing objects requires @Projection.NestedBy on " + entityClass.getName());
        }
        if(query.getSelects().stream().noneMatch(s -> s.entity.entity.equals(key.entity()) && s.attribute.equals(key.attribute()))) {
            throw new IllegalArgumentException("@Projection.NestedBy attribute '" + key.entity().getSimpleName() + "." + key.attribute() + "' of entity '" + key.entity().getSimpleName() + "' must be present in selects");
        }

        Field keyField = resolveNestedByKeyField(entityClass, key);
        keyField.setAccessible(true);

        Map<Object, T> existingByKey = new LinkedHashMap<>();
        for (T item : existing) {
            existingByKey.put(keyField.get(item), item);
        }

        EntityMapper<T> entityMapper = new EntityMapper<>(context, query, new Entity<>(entityClass));
        for (ResultRow row : rows) {
            Object keyValue = row.as(key.entity(), key.attribute());
            T object = existingByKey.get(keyValue);
            if (object == null) {
                object = entityMapper.map(row.getColumnValues());
                if (object != null) {
                    existingByKey.put(keyValue, object);
                }
            } 
            else {
                entityMapper.map(row.getColumnValues(), object);
            }
        }

        return existing;
    }

    /**
     * Resolves the field on {@code entityClass} that holds the {@link Projection.NestedBy} key value.
     */
    private Field resolveNestedByKeyField(Class<?> entityClass, Projection.NestedBy key) {
        if (key.entity().equals(entityClass)) {
            Field tKeyField = FieldUtils.findField(entityClass, key.attribute());
            if (tKeyField == null) {
                throw new IllegalArgumentException("Class '" + entityClass.getName() + "' does not have a field named '" + key.attribute() + "' (which is required by @Projection.NestedBy on " + key.entity().getName() + ")");
            }
            return tKeyField;
        }

        // Look for a field in entityClass that has a @Projection.Attribute mapping to the same key attribute in the key entity.
        for (Field f : entityClass.getDeclaredFields()) {
            Projection.Attribute projectionAttr = f.getDeclaredAnnotation(Projection.Attribute.class);
            if (projectionAttr != null && projectionAttr.entity().equals(key.entity()) && projectionAttr.attribute().equals(key.attribute())) {
                return f;
            }
        }
        throw new IllegalArgumentException("Could not find a field in " + entityClass.getName() + " with @Projection.Attribute(entity=" + key.entity().getSimpleName() + ", attribute=" + key.attribute() + ")");
    }

    

    // Attribute list mapping
    public List<?> asList(Attribute attribute) { return asListMapped(new AttributeMapper(context, attribute.getEntity().entity, attribute.attribute)); }
    public List<?> asList(Class<?> entity, String attributeName) { return asListMapped(new AttributeMapper(context, entity, attributeName)); }
    public List<?> asList(Entity<?> entity, String attributeName) { return asListMapped(new AttributeMapper(context, entity.entity, attributeName)); }
    @SuppressWarnings("unchecked")
    public <T, P> List<P> asList(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) asListMapped(new AttributeMapper(context, attribute.entity.entity, attribute.attribute)); }

    // Primitive type list mapping
    public List<String> asListString() { return asListMapped(new StringMapper()); }
    public List<String> asListString(Attribute attribute) { return asListMapped(new StringMapper(), indexOf(attribute)); }
    public List<String> asListString(Class<?> entity, String attributeName) { return asListString(Estivate.attribute(entity, attributeName)); }
    public List<String> asListString(Entity<?> entity, String attributeName) { return asListString(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<String> asListString(AttributeGetter<T, P> attributeGetter) { return asListString(Estivate.attribute(attributeGetter)); }
    public List<String> asListString(String columnName) { return asListMapped(new StringMapper(), columnNames.indexOf(columnName)); }

    public List<Short> asListShort() { return asListMapped(new ShortMapper()); }
    public List<Short> asListShort(Attribute attribute) { return asListMapped(new ShortMapper(), indexOf(attribute)); }
    public List<Short> asListShort(Class<?> entity, String attributeName) { return asListShort(Estivate.attribute(entity, attributeName)); }
    public List<Short> asListShort(Entity<?> entity, String attributeName) { return asListShort(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Short> asListShort(AttributeGetter<T, P> attributeGetter) { return asListShort(Estivate.attribute(attributeGetter)); }
    public List<Short> asListShort(String columnName) { return asListMapped(new ShortMapper(), columnNames.indexOf(columnName)); }

    public List<Integer> asListInteger() { return asListMapped(new IntegerMapper()); }
    public List<Integer> asListInteger(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asListMapped(new IntegerMapper(), index); }
    public List<Integer> asListInteger(Class<?> entity, String attributeName) { return asListInteger(Estivate.attribute(entity, attributeName)); }
    public List<Integer> asListInteger(Entity<?> entity, String attributeName) { return asListInteger(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Integer> asListInteger(AttributeGetter<T, P> attributeGetter) { return asListInteger(Estivate.attribute(attributeGetter)); }
    public List<Integer> asListInteger(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asListMapped(new IntegerMapper(), index); }

    public List<Long> asListLong() { return asListMapped(new LongMapper()); }
    public List<Long> asListLong(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asListMapped(new LongMapper(), index); }
    public List<Long> asListLong(Class<?> entity, String attributeName) { return asListLong(Estivate.attribute(entity, attributeName)); }
    public List<Long> asListLong(Entity<?> entity, String attributeName) { return asListLong(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Long> asListLong(AttributeGetter<T, P> attributeGetter) { return asListLong(Estivate.attribute(attributeGetter)); }
    public List<Long> asListLong(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asListMapped(new LongMapper(), index); }
    
    public List<Float> asListFloat() { return asListMapped(new FloatMapper()); }
    public List<Float> asListFloat(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asListMapped(new FloatMapper(), index); }
    public List<Float> asListFloat(Class<?> entity, String attributeName) { return asListFloat(Estivate.attribute(entity, attributeName)); }
    public List<Float> asListFloat(Entity<?> entity, String attributeName) { return asListFloat(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Float> asListFloat(AttributeGetter<T, P> attributeGetter) { return asListFloat(Estivate.attribute(attributeGetter)); }
    public List<Float> asListFloat(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asListMapped(new FloatMapper(), index); }

    public List<Double> asListDouble() { return asListMapped(new DoubleMapper()); }
    public List<Double> asListDouble(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asListMapped(new DoubleMapper(), index); }
    public List<Double> asListDouble(Class<?> entity, String attributeName) { return asListDouble(Estivate.attribute(entity, attributeName)); }
    public List<Double> asListDouble(Entity<?> entity, String attributeName) { return asListDouble(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Double> asListDouble(AttributeGetter<T, P> attributeGetter) { return asListDouble(Estivate.attribute(attributeGetter)); }
    public List<Double> asListDouble(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asListMapped(new DoubleMapper(), index); }

    public List<Boolean> asListBoolean() { return asListMapped(new BooleanMapper()); }
    public List<Boolean> asListBoolean(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asListMapped(new BooleanMapper(), index); }
    public List<Boolean> asListBoolean(Class<?> entity, String attributeName) { return asListBoolean(Estivate.attribute(entity, attributeName)); }
    public List<Boolean> asListBoolean(Entity<?> entity, String attributeName) { return asListBoolean(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Boolean> asListBoolean(AttributeGetter<T, P> attributeGetter) { return asListBoolean(Estivate.attribute(attributeGetter)); }
    public List<Boolean> asListBoolean(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asListMapped(new BooleanMapper(), index); }
    
    public List<Date> asListDate() { return asListMapped(new DateMapper(context)); }
    public List<Date> asListDate(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asListMapped(new DateMapper(context), index); }
    public List<Date> asListDate(Class<?> entity, String attributeName) { return asListDate(Estivate.attribute(entity, attributeName)); }
    public List<Date> asListDate(Entity<?> entity, String attributeName) { return asListDate(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Date> asListDate(AttributeGetter<T, P> attributeGetter) { return asListDate(Estivate.attribute(attributeGetter)); }
    public List<Date> asListDate(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asListMapped(new DateMapper(context), index); }
    
    public List<LocalDateTime> asListLocalDateTime() { return asListMapped(new LocalDateTimeMapper()); }
    public List<LocalDateTime> asListLocalDateTime(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asListMapped(new LocalDateTimeMapper(), index); }
    public List<LocalDateTime> asListLocalDateTime(Class<?> entity, String attributeName) { return asListLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public List<LocalDateTime> asListLocalDateTime(Entity<?> entity, String attributeName) { return asListLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<LocalDateTime> asListLocalDateTime(AttributeGetter<T, P> attributeGetter) { return asListLocalDateTime(Estivate.attribute(attributeGetter)); }
    public List<LocalDateTime> asListLocalDateTime(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asListMapped(new LocalDateTimeMapper(), index); }

    public List<Instant> asListInstant() { return asListMapped(new InstantMapper(context)); }
    public List<Instant> asListInstant(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asListMapped(new InstantMapper(context), index); }
    public List<Instant> asListInstant(Class<?> entity, String attributeName) { return asListInstant(Estivate.attribute(entity, attributeName)); }
    public List<Instant> asListInstant(Entity<?> entity, String attributeName) { return asListInstant(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Instant> asListInstant(AttributeGetter<T, P> attributeGetter) { return asListInstant(Estivate.attribute(attributeGetter)); }
    public List<Instant> asListInstant(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asListMapped(new InstantMapper(context), index); }

    // Enum list mapping
    public <E extends Enum<E>> List<E> asListStringEnum(Class<E> enumClass) { return asListMapped(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> List<E> asListOrdinalEnum(Class<E> enumClass) { return asListMapped(new OrdinalEnumMapper<>(enumClass)); }

    // ==================== SET MAPPING ====================

    public <T> Set<T> asSetMapped(IMapper<T> mapper, int index) { return asListMapped(mapper, index).stream().collect(Collectors.toSet()); }
    public <T> Set<T> asSetMapped(IMapper<T> mapper) { return asSetMapped(mapper, 0); }

    // Entity set mapping
    public <T> Set<T> asSet(Class<T> entity) { return asSet(new Entity<>(entity)); }
    public <T> Set<T> asSet(Entity<T> entity) {
        EntityMapper<T> entityMapper = new EntityMapper<>(context, query, entity);
        Set<T> results = new LinkedHashSet<>();
        for(ResultRow row : rows) {
            T result = entityMapper.map(row.getColumnValues());
            results.add(result);
        }
        return results;
    }

    // Attribute set mapping
    public Set<?> asSetAttribute(Attribute attribute) { return asSetMapped(new AttributeMapper(context, attribute.getEntity().entity, attribute.attribute)); }
    public Set<?> asSetAttribute(Class<?> entity, String attributeName) { return asSetMapped(new AttributeMapper(context, entity, attributeName)); }
    public Set<?> asSetAttribute(Entity<?> entity, String attributeName) { return asSetMapped(new AttributeMapper(context, entity.entity, attributeName)); }
    @SuppressWarnings("unchecked")
    public <T, P> Set<P> asSetAttribute(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) asSetMapped(new AttributeMapper(context, attribute.entity.entity, attribute.attribute)); }
    
    // Primitive type set mapping
    public Set<String> asSetString() { return asSetMapped(new StringMapper()); }
    public Set<String> asSetString(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asSetMapped(new StringMapper(), index); }
    public Set<String> asSetString(Class<?> entity, String attributeName) { return asSetString(Estivate.attribute(entity, attributeName)); }
    public Set<String> asSetString(Entity<?> entity, String attributeName) { return asSetString(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<String> asSetString(AttributeGetter<T, P> attributeGetter) { return asSetString(Estivate.attribute(attributeGetter)); }
    public Set<String> asSetString(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asSetMapped(new StringMapper(), index); }

    public Set<Short> asSetShort() { return asSetMapped(new ShortMapper()); }
    public Set<Short> asSetShort(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asSetMapped(new ShortMapper(), index); }
    public Set<Short> asSetShort(Class<?> entity, String attributeName) { return asSetShort(Estivate.attribute(entity, attributeName)); }
    public Set<Short> asSetShort(Entity<?> entity, String attributeName) { return asSetShort(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Short> asSetShort(AttributeGetter<T, P> attributeGetter) { return asSetShort(Estivate.attribute(attributeGetter)); }
    public Set<Short> asSetShort(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asSetMapped(new ShortMapper(), index); }

    public Set<Integer> asSetInteger() { return asSetMapped(new IntegerMapper()); }
    public Set<Integer> asSetInteger(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asSetMapped(new IntegerMapper(), index); }
    public Set<Integer> asSetInteger(Class<?> entity, String attributeName) { return asSetInteger(Estivate.attribute(entity, attributeName)); }
    public Set<Integer> asSetInteger(Entity<?> entity, String attributeName) { return asSetInteger(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Integer> asSetInteger(AttributeGetter<T, P> attributeGetter) { return asSetInteger(Estivate.attribute(attributeGetter)); }
    public Set<Integer> asSetInteger(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asSetMapped(new IntegerMapper(), index); }

    public Set<Long> asSetLong() { return asSetMapped(new LongMapper()); }
    public Set<Long> asSetLong(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asSetMapped(new LongMapper(), index); }
    public Set<Long> asSetLong(Class<?> entity, String attributeName) { return asSetLong(Estivate.attribute(entity, attributeName)); }
    public Set<Long> asSetLong(Entity<?> entity, String attributeName) { return asSetLong(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Long> asSetLong(AttributeGetter<T, P> attributeGetter) { return asSetLong(Estivate.attribute(attributeGetter)); }
    public Set<Long> asSetLong(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asSetMapped(new LongMapper(), index); }

    public Set<Float> asSetFloat() { return asSetMapped(new FloatMapper()); }
    public Set<Float> asSetFloat(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asSetMapped(new FloatMapper(), index); }
    public Set<Float> asSetFloat(Class<?> entity, String attributeName) { return asSetFloat(Estivate.attribute(entity, attributeName)); }
    public Set<Float> asSetFloat(Entity<?> entity, String attributeName) { return asSetFloat(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Float> asSetFloat(AttributeGetter<T, P> attributeGetter) { return asSetFloat(Estivate.attribute(attributeGetter)); }
    public Set<Float> asSetFloat(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asSetMapped(new FloatMapper(), index); }

    public Set<Double> asSetDouble() { return asSetMapped(new DoubleMapper());}
    public Set<Double> asSetDouble(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asSetMapped(new DoubleMapper(), index); }
    public Set<Double> asSetDouble(Class<?> entity, String attributeName) { return asSetDouble(Estivate.attribute(entity, attributeName)); }
    public Set<Double> asSetDouble(Entity<?> entity, String attributeName) { return asSetDouble(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Double> asSetDouble(AttributeGetter<T, P> attributeGetter) { return asSetDouble(Estivate.attribute(attributeGetter)); }
    public Set<Double> asSetDouble(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asSetMapped(new DoubleMapper(), index); }

    public Set<Boolean> asSetBoolean() { return asSetMapped(new BooleanMapper()); }
    public Set<Boolean> asSetBoolean(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asSetMapped(new BooleanMapper(), index); }
    public Set<Boolean> asSetBoolean(Class<?> entity, String attributeName) { return asSetBoolean(Estivate.attribute(entity, attributeName)); }
    public Set<Boolean> asSetBoolean(Entity<?> entity, String attributeName) { return asSetBoolean(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Boolean> asSetBoolean(AttributeGetter<T, P> attributeGetter) { return asSetBoolean(Estivate.attribute(attributeGetter)); }
    public Set<Boolean> asSetBoolean(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asSetMapped(new BooleanMapper(), index); }

    public Set<Date> asSetDate() { return asSetMapped(new DateMapper(context)); }
    public Set<Date> asSetDate(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asSetMapped(new DateMapper(context), index); }
    public Set<Date> asSetDate(Class<?> entity, String attributeName) { return asSetDate(Estivate.attribute(entity, attributeName)); }
    public Set<Date> asSetDate(Entity<?> entity, String attributeName) { return asSetDate(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Date> asSetDate(AttributeGetter<T, P> attributeGetter) { return asSetDate(Estivate.attribute(attributeGetter)); }
    public Set<Date> asSetDate(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asSetMapped(new DateMapper(context), index); }

    public Set<LocalDateTime> asSetLocalDateTime() { return asSetMapped(new LocalDateTimeMapper()); }
    public Set<LocalDateTime> asSetLocalDateTime(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asSetMapped(new LocalDateTimeMapper(), index); }
    public Set<LocalDateTime> asSetLocalDateTime(Class<?> entity, String attributeName) { return asSetLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public Set<LocalDateTime> asSetLocalDateTime(Entity<?> entity, String attributeName) { return asSetLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<LocalDateTime> asSetLocalDateTime(AttributeGetter<T, P> attributeGetter) { return asSetLocalDateTime(Estivate.attribute(attributeGetter)); }
    public Set<LocalDateTime> asSetLocalDateTime(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asSetMapped(new LocalDateTimeMapper(), index); }

    public Set<Instant> asSetInstant() { return asSetMapped(new InstantMapper(context)); }
    public Set<Instant> asSetInstant(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asSetMapped(new InstantMapper(context), index); }
    public Set<Instant> asSetInstant(Class<?> entity, String attributeName) { return asSetInstant(Estivate.attribute(entity, attributeName)); }
    public Set<Instant> asSetInstant(Entity<?> entity, String attributeName) { return asSetInstant(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Instant> asSetInstant(AttributeGetter<T, P> attributeGetter) { return asSetInstant(Estivate.attribute(attributeGetter)); }
    public Set<Instant> asSetInstant(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asSetMapped(new InstantMapper(context), index); }

    // Enum set mapping
    public <E extends Enum<E>> Set<E> asSetStringEnum(Class<E> enumClass) { return asSetMapped(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> Set<E> asSetOrdinalEnum(Class<E> enumClass) { return asSetMapped(new OrdinalEnumMapper<>(enumClass)); }


    // ==================== AGGREGATION METHODS ====================

    public <T, U, V> Map<U, V> asMapFromFunctions(Function<ResultRow,U> uType, Function<ResultRow,V> vType){
		Map<U, V> map = new LinkedHashMap<>();
		for(ResultRow result : rows){
			map.put(uType.apply(result), vType.apply(result));
		}
		return map;
	}

    public <T, U, V> Map<U, List<V>> asMapListFromFunctions(Function<ResultRow,U> uType, Function<ResultRow,V> vType){
		Map<U, List<V>> map = new LinkedHashMap<>();
		for(ResultRow result : rows){
			map.computeIfAbsent(uType.apply(result), k -> new ArrayList<>()).add(vType.apply(result));
		}
		return map;
	}

    public <A1E, A1T, A2E, A2T> Map<A1T, A2T> asMap(AttributeGetter<A1E, A1T> uAttribute, AttributeGetter<A2E, A2T> vAttribute) {
        Map<A1T, A2T> map = new LinkedHashMap<>();
        for(ResultRow result : rows){
            map.put(result.as(uAttribute), result.as(vAttribute));
        }
        return map;
    }

    public Map<Object, Object> asMap(Attribute keyAttribute, Attribute valueAttribute) {
        Map<Object, Object> map = new LinkedHashMap<>();
        for(ResultRow result : rows){
            map.put(result.as(keyAttribute), result.as(valueAttribute));
        }
        return map;
    }

    public <AE, AT, C> Map<AT, C> asMap(AttributeGetter<AE, AT> uAttribute, Class<C> vClass) {
        Map<AT, C> map = new LinkedHashMap<>();
        for(ResultRow result : rows){
            map.put(result.as(uAttribute), result.as(vClass));
        }
        return map;
    }

    public <C1, C2> Map<C1, C2> asMap(Class<C1> uClass, Class<C2> vClass) {
        Map<C1, C2> map = new LinkedHashMap<>();
        for(ResultRow result : rows){
            map.put(result.as(uClass), result.as(vClass));
        }
        return map;
    }

    public <C, AE, AT> Map<C, AT> asMap(Class<C> uClass, AttributeGetter<AE, AT> vAttribute) {
        Map<C, AT> map = new LinkedHashMap<>();
        for(ResultRow result : rows){
            map.put(result.as(uClass), result.as(vAttribute));
        }
        return map;
    }


    public <A1E, A1T, A2E, A2T> Map<A1T, List<A2T>> asMapList(AttributeGetter<A1E, A1T> uAttribute, AttributeGetter<A2E, A2T> vAttribute) {
        Map<A1T, List<A2T>> map = new LinkedHashMap<>();
        for(ResultRow result : rows){
            map.computeIfAbsent(result.as(uAttribute), k -> new ArrayList<>()).add(result.as(vAttribute));
        }
        return map;
    }

    public <AE, AT, C> Map<AT, List<C>> asMapList(AttributeGetter<AE, AT> uAttribute, Class<C> vClass) {
        Map<AT, List<C>> map = new LinkedHashMap<>();
        for(ResultRow result : rows){
            map.computeIfAbsent(result.as(uAttribute), k -> new ArrayList<>()).add(result.as(vClass));
        }
        return map;
    }

    public <C, AE, AT> Map<C, List<AT>> asMapList(Class<C> uClass, AttributeGetter<AE, AT> vAttribute) {
        Map<C, List<AT>> map = new LinkedHashMap<>();
        for(ResultRow result : rows){
            map.computeIfAbsent(result.as(uClass), k -> new ArrayList<>()).add(result.as(vAttribute));
        }
        return map;
    }

    public <C1, C2> Map<C1, List<C2>> asMapList(Class<C1> uClass, Class<C2> vClass) {
        Map<C1, List<C2>> map = new LinkedHashMap<>();
        for(ResultRow result : rows){
            map.computeIfAbsent(result.as(uClass), k -> new ArrayList<>()).add(result.as(vClass));
        }
        return map;
    }
    
    public <A1E, A1T, A2E, A2T> Map<A1T, Set<A2T>> asMapSet(AttributeGetter<A1E, A1T> uAttribute, AttributeGetter<A2E, A2T> vAttribute) {
        Map<A1T, Set<A2T>> map = new LinkedHashMap<>();
        for(ResultRow result : rows){
            map.computeIfAbsent(result.as(uAttribute), k -> new HashSet<>()).add(result.as(vAttribute));
        }
        return map;
    }

    public <C, AE, AT> Map<C, Set<AT>> asMapSet(Class<C> uClass, AttributeGetter<AE, AT> vAttribute) {
        Map<C, Set<AT>> map = new LinkedHashMap<>();
        for(ResultRow result : rows){
            map.computeIfAbsent(result.as(uClass), k -> new HashSet<>()).add(result.as(vAttribute));
        }
        return map;
    }

    public <AE, AT, C> Map<AT, Set<C>> asMapSet(AttributeGetter<AE, AT> uAttribute, Class<C> vClass) {
        Map<AT, Set<C>> map = new LinkedHashMap<>();
        for(ResultRow result : rows){
            map.computeIfAbsent(result.as(uAttribute), k -> new HashSet<>()).add(result.as(vClass));
        }
        return map;
    }

    public <C1, C2> Map<C1, Set<C2>> asMapSet(Class<C1> uClass, Class<C2> vClass) {
        Map<C1, Set<C2>> map = new LinkedHashMap<>();
        for(ResultRow result : rows){
            map.computeIfAbsent(result.as(uClass), k -> new HashSet<>()).add(result.as(vClass));
        }
        return map;
    }


    // ==================== UTILITY METHODS ====================

    public boolean isEmpty() { return rows.isEmpty(); }
    public int size() { return rows.size(); }
    public ResultRow getFirst() { return rows.isEmpty() ? null : rows.get(0); }
    public ResultRow get(int index) { return rows.get(index); }
    public ResultRow getLast() { return rows.isEmpty() ? null : rows.get(rows.size() - 1); }


    public Integer indexOf(Attribute attribute) {

        // Look by column matching
        List<Attribute> attributes = new ArrayList<>(query.getSelects());
        if(attribute.getEntity() != null && attribute.getAttribute() != null){ 
            for(int i = 0; i < query.getSelects().size(); i++){
                if(attributes.get(i).getEntity().equals(attribute.getEntity()) && attributes.get(i).getAttribute().equals(attribute.getAttribute()) && Objects.equals(attributes.get(i).getFunction(), attribute.getFunction())){
                    return i;
                }
            }
        }

        // Look by alias matching
        if(attribute.getAlias() != null){
            Integer indexByName = indexOf(attribute.getAlias());
            if(indexByName != null){
                return indexByName;
            }
        }

        return null;
        
    }


    public Integer indexOf(String column){

		for(int i = 0; i < columnNames.size(); i++){
			if(columnNames.get(i).equalsIgnoreCase(column)){
				return i;
			}
		}
		log.error("Column not found: "+column + ", available columns: " + columnNames);
		return null;
	}

    @Override
    public Iterator<ResultRow> iterator() { return rows.iterator(); }

    

}
