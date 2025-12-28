package com.estivate.result;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import com.estivate.Entity;
import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Attribute;
import com.estivate.query.SelectQuery;
import com.estivate.result.IMapper.AttributeMapper;
import com.estivate.result.IMapper.BooleanMapper;
import com.estivate.result.IMapper.DateMapper;
import com.estivate.result.IMapper.DoubleMapper;
import com.estivate.result.IMapper.FloatMapper;
import com.estivate.result.IMapper.IntegerMapper;
import com.estivate.result.IMapper.LocalDateTimeMapper;
import com.estivate.result.IMapper.LongMapper;
import com.estivate.result.IMapper.OrdinalEnumMapper;
import com.estivate.result.IMapper.ShortMapper;
import com.estivate.result.IMapper.StringEnumMapper;
import com.estivate.result.IMapper.StringMapper;
import com.estivate.util.FieldUtils.AttributeGetter;

import lombok.Data;


@Data
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

    public <T> T asMapped(IMapper<T> mapper, Integer index) { return rows.isEmpty() ? null : mapper.map(index == null ? rows.get(0).getColumnValues() : new String[] { rows.get(0).getColumnValues()[index] }); }
    public <T> T asMapped(IMapper<T> mapper) { return rows.isEmpty() ? null : mapper.map(rows.get(0).getColumnValues()); }

    // Entity mapping
    public <T> T as(Class<T> entity) { return asMapped(new EntityMapper<>(context, query, new Entity<>(entity))); }
    public <T> T as(Entity<T> entity) { return asMapped(new EntityMapper<>(context, query, entity)); }
    
    // Attribute mapping
    public Object as(Attribute attribute) { return asMapped(new AttributeMapper(attribute.getEntity().entity, attribute.attribute)); }
    public Object as(Class<?> entity, String attribute) { return asMapped(new AttributeMapper(entity, attribute)); }
    public Object as(Entity<?> entity, String attribute) { return asMapped(new AttributeMapper(entity.entity, attribute)); }
    public <T, P> P as(AttributeGetter<T, P> attributeGetter) { return (P) as(Estivate.attribute(attributeGetter)); }


    // Primitive type mapping
    public String asString() { return asMapped(new StringMapper()); }
    public String asString(Attribute attribute) { return asMapped(new StringMapper(), indexOf(attribute)); }
    public String asString(Class<?> entity, String attributeName) { return asString(Estivate.attribute(entity, attributeName)); }
    public String asString(Entity<?> entity, String attributeName) { return asString(Estivate.attribute(entity, attributeName)); }
    public <T, P> String asString(AttributeGetter<T, P> attributeGetter) { return asString(Estivate.attribute(attributeGetter)); }
    public String asString(String columnName) { return asMapped(new StringMapper(), columnNames.indexOf(columnName)); }

    public Short asShort() { return asMapped(new ShortMapper()); }
    public Short asShort(Attribute attribute) { return asMapped(new ShortMapper(), indexOf(attribute)); }
    public Short asShort(Class<?> entity, String attributeName) { return asShort(Estivate.attribute(entity, attributeName)); }
    public Short asShort(Entity<?> entity, String attributeName) { return asShort(Estivate.attribute(entity, attributeName)); }
    public <T, P> Short asShort(AttributeGetter<T, P> attributeGetter) { return asShort(Estivate.attribute(attributeGetter)); }
    public Short asShort(String columnName) { return asMapped(new ShortMapper(), columnNames.indexOf(columnName)); }

    public Integer asInteger() { return asMapped(new IntegerMapper()); }
    public Integer asInteger(Attribute attribute) { return asMapped(new IntegerMapper(), indexOf(attribute)); }
    public Integer asInteger(Class<?> entity, String attributeName) { return asInteger(Estivate.attribute(entity, attributeName)); }
    public Integer asInteger(Entity<?> entity, String attributeName) { return asInteger(Estivate.attribute(entity, attributeName)); }
    public <T, P> Integer asInteger(AttributeGetter<T, P> attributeGetter) { return asInteger(Estivate.attribute(attributeGetter)); }
    public Integer asInteger(String columnName) { return asMapped(new IntegerMapper(), columnNames.indexOf(columnName)); }

    public Long asLong() { return asMapped(new LongMapper()); }
    public Long asLong(Attribute attribute) { return asMapped(new LongMapper(), indexOf(attribute)); }
    public Long asLong(Class<?> entity, String attributeName) { return asLong(Estivate.attribute(entity, attributeName)); }
    public Long asLong(Entity<?> entity, String attributeName) { return asLong(Estivate.attribute(entity, attributeName)); }
    public <T, P> Long asLong(AttributeGetter<T, P> attributeGetter) { return asLong(Estivate.attribute(attributeGetter)); }
    public Long asLong(String columnName) { return asMapped(new LongMapper(), columnNames.indexOf(columnName)); }

    public Float asFloat() { return asMapped(new FloatMapper()); }
    public Float asFloat(Attribute attribute) { return asMapped(new FloatMapper(), indexOf(attribute)); }
    public Float asFloat(Class<?> entity, String attributeName) { return asFloat(Estivate.attribute(entity, attributeName)); }
    public Float asFloat(Entity<?> entity, String attributeName) { return asFloat(Estivate.attribute(entity, attributeName)); }
    public <T, P> Float asFloat(AttributeGetter<T, P> attributeGetter) { return asFloat(Estivate.attribute(attributeGetter)); }
    public Float asFloat(String columnName) { return asMapped(new FloatMapper(), columnNames.indexOf(columnName)); }

    public Double asDouble() { return asMapped(new DoubleMapper()); }
    public Double asDouble(Attribute attribute) { return asMapped(new DoubleMapper(), indexOf(attribute)); }
    public Double asDouble(Class<?> entity, String attributeName) { return asDouble(Estivate.attribute(entity, attributeName)); }
    public Double asDouble(Entity<?> entity, String attributeName) { return asDouble(Estivate.attribute(entity, attributeName)); }
    public <T, P> Double asDouble(AttributeGetter<T, P> attributeGetter) { return asDouble(Estivate.attribute(attributeGetter)); }
    public Double asDouble(String columnName) { return asMapped(new DoubleMapper(), columnNames.indexOf(columnName)); }
    
    public Boolean asBoolean() { return asMapped(new BooleanMapper()); }
    public Boolean asBoolean(Attribute attribute) { return asMapped(new BooleanMapper(), indexOf(attribute)); }
    public Boolean asBoolean(Class<?> entity, String attributeName) { return asBoolean(Estivate.attribute(entity, attributeName)); }
    public Boolean asBoolean(Entity<?> entity, String attributeName) { return asBoolean(Estivate.attribute(entity, attributeName)); }
    public <T, P> Boolean asBoolean(AttributeGetter<T, P> attributeGetter) { return asBoolean(Estivate.attribute(attributeGetter)); }
    public Boolean asBoolean(String columnName) { return asMapped(new BooleanMapper(), columnNames.indexOf(columnName)); }

    public Date asDate() { return asMapped(new DateMapper()); }
    public Date asDate(Attribute attribute) { return asMapped(new DateMapper(), indexOf(attribute)); }
    public Date asDate(Class<?> entity, String attributeName) { return asDate(Estivate.attribute(entity, attributeName)); }
    public Date asDate(Entity<?> entity, String attributeName) { return asDate(Estivate.attribute(entity, attributeName)); }
    public <T, P> Date asDate(AttributeGetter<T, P> attributeGetter) { return asDate(Estivate.attribute(attributeGetter)); }
    public Date asDate(String columnName) { return asMapped(new DateMapper(), columnNames.indexOf(columnName)); }

    public LocalDateTime asLocalDateTime() { return asMapped(new LocalDateTimeMapper()); }
    public LocalDateTime asLocalDateTime(Attribute attribute) { return asMapped(new LocalDateTimeMapper(), indexOf(attribute)); }
    public LocalDateTime asLocalDateTime(Class<?> entity, String attributeName) { return asLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public LocalDateTime asLocalDateTime(Entity<?> entity, String attributeName) { return asLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public <T, P> LocalDateTime asLocalDateTime(AttributeGetter<T, P> attributeGetter) { return asLocalDateTime(Estivate.attribute(attributeGetter)); }
    public LocalDateTime asLocalDateTime(String columnName) { return asMapped(new LocalDateTimeMapper(), columnNames.indexOf(columnName)); }

    // Enum mapping
    public <E extends Enum<E>> E asStringEnum(Class<E> enumClass) { return asMapped(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> E asOrdinalEnum(Class<E> enumClass) { return asMapped(new OrdinalEnumMapper<>(enumClass)); }


    // ==================== OPTIONAL MAPPING ====================

    public <T> Optional<T> asOptional(Class<T> entity) { return Optional.ofNullable(as(entity)); }
    public <T> Optional<T> asOptional(Entity<T> entity) { return Optional.ofNullable(as(entity)); }


    public <T> Optional<T> asOptionalMapped(IMapper<T> mapper) { return Optional.ofNullable(asMapped(mapper)); }
    public Optional<?> asOptional(Attribute attribute) { return Optional.ofNullable(as(attribute)); }
    public Optional<?> asOptional(Class<?> entity, String attributeName) { return asOptional(Estivate.attribute(entity, attributeName)); }
    public Optional<?> asOptional(Entity<?> entity, String attributeName) { return asOptional(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<P> asOptional(AttributeGetter<T, P> attributeGetter) { return (Optional<P>) asOptional(Estivate.attribute(attributeGetter)); }

    public Optional<String> asOptionalString() { return Optional.ofNullable(asString()); }
    public Optional<String> asOptionalString(Attribute attribute) { return Optional.ofNullable(asString(attribute)); }
    public Optional<String> asOptionalString(Class<?> entity, String attributeName) { return asOptionalString(Estivate.attribute(entity, attributeName)); }
    public Optional<String> asOptionalString(Entity<?> entity, String attributeName) { return asOptionalString(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<String> asOptionalString(AttributeGetter<T, P> attributeGetter) { return asOptionalString(Estivate.attribute(attributeGetter)); }
    public Optional<String> asOptionalString(String columnName) { return Optional.ofNullable(asString(columnName)); }

    public Optional<Short> asOptionalShort() { return Optional.ofNullable(asShort()); }
    public Optional<Short> asOptionalShort(Attribute attribute) { return Optional.ofNullable(asShort(attribute)); }
    public Optional<Short> asOptionalShort(Class<?> entity, String attributeName) { return asOptionalShort(Estivate.attribute(entity, attributeName)); }
    public Optional<Short> asOptionalShort(Entity<?> entity, String attributeName) { return asOptionalShort(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Short> asOptionalShort(AttributeGetter<T, P> attributeGetter) { return asOptionalShort(Estivate.attribute(attributeGetter)); }
    public Optional<Short> asOptionalShort(String columnName) { return Optional.ofNullable(asShort(columnName)); }

    public Optional<Integer> asOptionalInteger() { return Optional.ofNullable(asInteger()); }
    public Optional<Integer> asOptionalInteger(Attribute attribute) { return Optional.ofNullable(asInteger(attribute)); }
    public Optional<Integer> asOptionalInteger(Class<?> entity, String attributeName) { return asOptionalInteger(Estivate.attribute(entity, attributeName)); }
    public Optional<Integer> asOptionalInteger(Entity<?> entity, String attributeName) { return asOptionalInteger(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Integer> asOptionalInteger(AttributeGetter<T, P> attributeGetter) { return asOptionalInteger(Estivate.attribute(attributeGetter)); }
    public Optional<Integer> asOptionalInteger(String columnName) { return Optional.ofNullable(asInteger(columnName)); }

    public Optional<Long> asOptionalLong() { return Optional.ofNullable(asLong()); }
    public Optional<Long> asOptionalLong(Attribute attribute) { return Optional.ofNullable(asLong(attribute)); }
    public Optional<Long> asOptionalLong(Class<?> entity, String attributeName) { return asOptionalLong(Estivate.attribute(entity, attributeName)); }
    public Optional<Long> asOptionalLong(Entity<?> entity, String attributeName) { return asOptionalLong(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Long> asOptionalLong(AttributeGetter<T, P> attributeGetter) { return asOptionalLong(Estivate.attribute(attributeGetter)); }
    public Optional<Long> asOptionalLong(String columnName) { return Optional.ofNullable(asLong(columnName)); }

    public Optional<Float> asOptionalFloat() { return Optional.ofNullable(asFloat()); }
    public Optional<Float> asOptionalFloat(Attribute attribute) { return Optional.ofNullable(asFloat(attribute)); }
    public Optional<Float> asOptionalFloat(Class<?> entity, String attributeName) { return asOptionalFloat(Estivate.attribute(entity, attributeName)); }
    public Optional<Float> asOptionalFloat(Entity<?> entity, String attributeName) { return asOptionalFloat(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Float> asOptionalFloat(AttributeGetter<T, P> attributeGetter) { return asOptionalFloat(Estivate.attribute(attributeGetter)); }
    public Optional<Float> asOptionalFloat(String columnName) { return Optional.ofNullable(asFloat(columnName)); }

    public Optional<Double> asOptionalDouble() { return Optional.ofNullable(asDouble()); }
    public Optional<Double> asOptionalDouble(Attribute attribute) { return Optional.ofNullable(asDouble(attribute)); }
    public Optional<Double> asOptionalDouble(Class<?> entity, String attributeName) { return asOptionalDouble(Estivate.attribute(entity, attributeName)); }
    public Optional<Double> asOptionalDouble(Entity<?> entity, String attributeName) { return asOptionalDouble(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Double> asOptionalDouble(AttributeGetter<T, P> attributeGetter) { return asOptionalDouble(Estivate.attribute(attributeGetter)); }
    public Optional<Double> asOptionalDouble(String columnName) { return Optional.ofNullable(asDouble(columnName)); }

    public Optional<Boolean> asOptionalBoolean() { return Optional.ofNullable(asBoolean()); }
    public Optional<Boolean> asOptionalBoolean(Attribute attribute) { return Optional.ofNullable(asBoolean(attribute)); }
    public Optional<Boolean> asOptionalBoolean(Class<?> entity, String attributeName) { return asOptionalBoolean(Estivate.attribute(entity, attributeName)); }
    public Optional<Boolean> asOptionalBoolean(Entity<?> entity, String attributeName) { return asOptionalBoolean(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Boolean> asOptionalBoolean(AttributeGetter<T, P> attributeGetter) { return asOptionalBoolean(Estivate.attribute(attributeGetter)); }
    public Optional<Boolean> asOptionalBoolean(String columnName) { return Optional.ofNullable(asBoolean(columnName)); }

    public Optional<Date> asOptionalDate() { return Optional.ofNullable(asDate()); }
    public Optional<Date> asOptionalDate(Attribute attribute) { return Optional.ofNullable(asDate(attribute)); }
    public Optional<Date> asOptionalDate(Class<?> entity, String attributeName) { return asOptionalDate(Estivate.attribute(entity, attributeName)); }
    public Optional<Date> asOptionalDate(Entity<?> entity, String attributeName) { return asOptionalDate(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<Date> asOptionalDate(AttributeGetter<T, P> attributeGetter) { return asOptionalDate(Estivate.attribute(attributeGetter)); }
    public Optional<Date> asOptionalDate(String columnName) { return Optional.ofNullable(asDate(columnName)); }

    public Optional<LocalDateTime> asOptionalLocalDateTime() { return Optional.ofNullable(asLocalDateTime()); }
    public Optional<LocalDateTime> asOptionalLocalDateTime(Attribute attribute) { return Optional.ofNullable(asLocalDateTime(attribute)); }
    public Optional<LocalDateTime> asOptionalLocalDateTime(Class<?> entity, String attributeName) { return asOptionalLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public Optional<LocalDateTime> asOptionalLocalDateTime(Entity<?> entity, String attributeName) { return asOptionalLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public <T, P> Optional<LocalDateTime> asOptionalLocalDateTime(AttributeGetter<T, P> attributeGetter) { return asOptionalLocalDateTime(Estivate.attribute(attributeGetter)); }
    public Optional<LocalDateTime> asOptionalLocalDateTime(String columnName) { return Optional.ofNullable(asLocalDateTime(columnName)); }


    // ==================== LIST MAPPING ====================

    public <T> List<T> asListMapped(IMapper<T> mapper, Integer index) {
        List<T> results = new ArrayList<>();
        for(ResultRow row : rows) {
            T result = mapper.map(index == null ? row.getColumnValues() : new String[] { row.getColumnValues()[index] });
            results.add(result);
        }
        return results;
    }
    public <T> List<T> asListMapped(IMapper<T> mapper) { return asListMapped(mapper, null); }

    // Entity list mapping
    public <T> List<T> asList(Class<T> entity) { return asListMapped(new EntityMapper<>(context, query, new Entity<>(entity))); }
    public <T> List<T> asList(Entity<T> entity) { return asListMapped(new EntityMapper<>(context, query, entity)); }

    // Attribute list mapping
    public List<?> asList(Attribute attribute) { return asListMapped(new AttributeMapper(attribute.getEntity().entity, attribute.attribute)); }
    public List<?> asList(Class<?> entity, String attributeName) { return asListMapped(new AttributeMapper(entity, attributeName)); }
    public List<?> asList(Entity<?> entity, String attributeName) { return asListMapped(new AttributeMapper(entity.entity, attributeName)); }
    @SuppressWarnings("unchecked")
    public <T, P> List<P> asList(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) asListMapped(new AttributeMapper(attribute.entity.entity, attribute.attribute)); }

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
    
    public List<Date> asListDate() { return asListMapped(new DateMapper()); }
    public List<Date> asListDate(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asListMapped(new DateMapper(), index); }
    public List<Date> asListDate(Class<?> entity, String attributeName) { return asListDate(Estivate.attribute(entity, attributeName)); }
    public List<Date> asListDate(Entity<?> entity, String attributeName) { return asListDate(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Date> asListDate(AttributeGetter<T, P> attributeGetter) { return asListDate(Estivate.attribute(attributeGetter)); }
    public List<Date> asListDate(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asListMapped(new DateMapper(), index); }
    
    public List<LocalDateTime> asListLocalDateTime() { return asListMapped(new LocalDateTimeMapper()); }
    public List<LocalDateTime> asListLocalDateTime(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asListMapped(new LocalDateTimeMapper(), index); }
    public List<LocalDateTime> asListLocalDateTime(Class<?> entity, String attributeName) { return asListLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public List<LocalDateTime> asListLocalDateTime(Entity<?> entity, String attributeName) { return asListLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<LocalDateTime> asListLocalDateTime(AttributeGetter<T, P> attributeGetter) { return asListLocalDateTime(Estivate.attribute(attributeGetter)); }
    public List<LocalDateTime> asListLocalDateTime(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asListMapped(new LocalDateTimeMapper(), index); }

    // Enum list mapping
    public <E extends Enum<E>> List<E> asListStringEnum(Class<E> enumClass) { return asListMapped(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> List<E> asListOrdinalEnum(Class<E> enumClass) { return asListMapped(new OrdinalEnumMapper<>(enumClass)); }

    // ==================== SET MAPPING ====================

    public <T> Set<T> asSetMapped(IMapper<T> mapper, Integer index) { return asListMapped(mapper, index).stream().collect(Collectors.toSet()); }
    public <T> Set<T> asSetMapped(IMapper<T> mapper) { return asSetMapped(mapper, null); }

    // Entity set mapping
    public <T> Set<T> asSet(Class<T> entity) { return asSetMapped(new EntityMapper<>(context, query, new Entity<>(entity))); }
    public <T> Set<T> asSet(Entity<T> entity) { return asSetMapped(new EntityMapper<>(context, query, entity)); }

    // Attribute set mapping
    public Set<?> asSetAttribute(Attribute attribute) { return asSetMapped(new AttributeMapper(attribute.getEntity().entity, attribute.attribute)); }
    public Set<?> asSetAttribute(Class<?> entity, String attributeName) { return asSetMapped(new AttributeMapper(entity, attributeName)); }
    public Set<?> asSetAttribute(Entity<?> entity, String attributeName) { return asSetMapped(new AttributeMapper(entity.entity, attributeName)); }
    @SuppressWarnings("unchecked")
    public <T, P> Set<P> asSetAttribute(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) asSetMapped(new AttributeMapper(attribute.entity.entity, attribute.attribute)); }
    
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

    public Set<Date> asSetDate() { return asSetMapped(new DateMapper()); }
    public Set<Date> asSetDate(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asSetMapped(new DateMapper(), index); }
    public Set<Date> asSetDate(Class<?> entity, String attributeName) { return asSetDate(Estivate.attribute(entity, attributeName)); }
    public Set<Date> asSetDate(Entity<?> entity, String attributeName) { return asSetDate(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Date> asSetDate(AttributeGetter<T, P> attributeGetter) { return asSetDate(Estivate.attribute(attributeGetter)); }
    public Set<Date> asSetDate(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asSetMapped(new DateMapper(), index); }

    public Set<LocalDateTime> asSetLocalDateTime() { return asSetMapped(new LocalDateTimeMapper()); }
    public Set<LocalDateTime> asSetLocalDateTime(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : asSetMapped(new LocalDateTimeMapper(), index); }
    public Set<LocalDateTime> asSetLocalDateTime(Class<?> entity, String attributeName) { return asSetLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public Set<LocalDateTime> asSetLocalDateTime(Entity<?> entity, String attributeName) { return asSetLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<LocalDateTime> asSetLocalDateTime(AttributeGetter<T, P> attributeGetter) { return asSetLocalDateTime(Estivate.attribute(attributeGetter)); }
    public Set<LocalDateTime> asSetLocalDateTime(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : asSetMapped(new LocalDateTimeMapper(), index); }

    // Enum set mapping
    public <E extends Enum<E>> Set<E> asSetStringEnum(Class<E> enumClass) { return asSetMapped(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> Set<E> asSetOrdinalEnum(Class<E> enumClass) { return asSetMapped(new OrdinalEnumMapper<>(enumClass)); }

    // ==================== UTILITY METHODS ====================

    public boolean isEmpty() { return rows.isEmpty(); }
    public int size() { return rows.size(); }
    public ResultRow getFirst() { return rows.isEmpty() ? null : rows.get(0); }
    public ResultRow get(int index) { return rows.get(index); }
    public ResultRow getLast() { return rows.isEmpty() ? null : rows.get(rows.size() - 1); }

    private int indexOf(Attribute attribute) { 
        return new ArrayList<>(query.getSelects()).indexOf(attribute);
    }

    @Override
    public Iterator<ResultRow> iterator() { return rows.iterator(); }

    

}
