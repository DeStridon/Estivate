package com.estivate.result;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
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

    public <T> T map(IMapper<T> mapper) {  return rows.isEmpty() ? null : mapper.map(rows.get(0).getColumnValues());}

    public <T> T get(Class<T> entity) { return map(new EntityMapper<>(context, query, new Entity<>(entity))); }
    public <T> T get(Entity<T> entity) { return map(new EntityMapper<>(context, query, entity)); }
    public Object get(Class<?> entity, String attribute) { return map(new AttributeMapper(entity, attribute)); }
    public Object get(Entity<?> entity, String attribute) { return map(new AttributeMapper(entity.entity, attribute)); }
    public String getAsString() { return map(new StringMapper()); }
    public Short getAsShort() { return map(new ShortMapper()); }
    public Integer getAsInteger() { return map(new IntegerMapper()); }
    public Long getAsLong() { return map(new LongMapper()); }
    public Float getAsFloat() { return map(new FloatMapper()); }
    public Double getAsDouble() { return map(new DoubleMapper()); }
    public Boolean getAsBoolean() { return map(new BooleanMapper()); }
    public Date getAsDate() { return map(new DateMapper()); }
    public LocalDateTime getAsLocalDateTime() { return map(new LocalDateTimeMapper()); }

    // public LocalDate getAsLocalDate() { return mapSingle(new LocalDateMapper()); }
    // public LocalTime getAsLocalTime() { return mapSingle(new LocalTimeMapper()); }

    public <E extends Enum<E>> E getAsStringEnum(Class<E> enumClass) { return map(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> E getAsOrdinalEnum(Class<E> enumClass) { return map(new OrdinalEnumMapper<>(enumClass)); }

    public <T> List<T> mapList(IMapper<T> mapper, Integer index) {

        List<T> results = new ArrayList<>();
        for(ResultRow row : rows) {
            T result = mapper.map(index == null ? row.getColumnValues() : new String[] { row.getColumnValues()[index] });
            results.add(result);
        }
        return results;
    }

    public <T> List<T> mapList(IMapper<T> mapper) { return mapList(mapper, null); }

    public <T> Set<T> mapSet(IMapper<T> mapper, Integer index) {
        return mapList(mapper, index).stream().collect(Collectors.toSet());
    }
    public <T> Set<T> mapSet(IMapper<T> mapper) { return mapSet(mapper, null); }

    public <T> List<T> getAsList(Class<T> entity) { return mapList(new EntityMapper<>(context, query, new Entity<>(entity))); }
    public <T> List<T> getAsList(Entity<T> entity) { return mapList(new EntityMapper<>(context, query, entity)); }


    public List<?> getAsList(Attribute attribute) { return mapList(new AttributeMapper(attribute.getEntity().entity, attribute.attribute)); }
    public List<?> getAsList(Class<?> entity, String attributeName) { return mapList(new AttributeMapper(entity, attributeName)); }
    public List<?> getAsList(Entity<?> entity, String attributeName) { return mapList(new AttributeMapper(entity.entity, attributeName)); }
    public <T, P> List<P> getAsList(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) mapList(new AttributeMapper(attribute.entity.entity, attribute.attribute)); }
    

    public List<String> getAsListString() { return mapList(new StringMapper()); }
    public List<String> getAsListString(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : getRows().stream().map(row -> row.getColumnValues()[index]).collect(Collectors.toList()); }
    public List<String> getAsListString(Class<?> entity, String attributeName)         { return getAsListString(Estivate.attribute(entity, attributeName)); }
    public List<String> getAsListString(Entity<?> entity, String attributeName)        { return getAsListString(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<String> getAsListString(AttributeGetter<T, P> attributeGetter)  { return getAsListString(Estivate.attribute(attributeGetter)); }
    public List<String> getAsListString(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : getRows().stream().map(row -> row.getColumnValues()[index]).collect(Collectors.toList()); }

    public List<Short> getAsListShort() { return mapList(new ShortMapper()); }
    public List<Short> getAsListShort(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapList(new ShortMapper(), indexOf(attribute)); }
    public List<Short> getAsListShort(Class<?> entity, String attributeName)           { return getAsListShort(Estivate.attribute(entity, attributeName)); }
    public List<Short> getAsListShort(Entity<?> entity, String attributeName)          { return getAsListShort(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Short> getAsListShort(AttributeGetter<T, P> attributeGetter)    { return getAsListShort(Estivate.attribute(attributeGetter)); }
    public List<Short> getAsListShort(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapList(new ShortMapper(), index); }

    public List<Integer> getAsListInteger() { return mapList(new IntegerMapper()); }
    public List<Integer> getAsListInteger(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapList(new IntegerMapper(), index); }
    public List<Integer> getAsListInteger(Class<?> entity, String attributeName)       { return getAsListInteger(Estivate.attribute(entity, attributeName)); }
    public List<Integer> getAsListInteger(Entity<?> entity, String attributeName)      { return getAsListInteger(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Integer> getAsListInteger(AttributeGetter<T, P> attributeGetter) { return getAsListInteger(Estivate.attribute(attributeGetter)); }
    public List<Integer> getAsListInteger(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapList(new IntegerMapper(), index); }

    public List<Long> getAsListLong() { return mapList(new LongMapper()); }
    public List<Long> getAsListLong(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapList(new LongMapper(), index); }
    public List<Long> getAsListLong(Class<?> entity, String attributeName)             { return getAsListLong(Estivate.attribute(entity, attributeName)); }
    public List<Long> getAsListLong(Entity<?> entity, String attributeName)            { return getAsListLong(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Long> getAsListLong(AttributeGetter<T, P> attributeGetter)      { return getAsListLong(Estivate.attribute(attributeGetter)); }
    public List<Long> getAsListLong(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapList(new LongMapper(), index); }
    
    public List<Float> getAsListFloat() { return mapList(new FloatMapper()); }
    public List<Float> getAsListFloat(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapList(new FloatMapper(), index); }
    public List<Float> getAsListFloat(Class<?> entity, String attributeName)           { return getAsListFloat(Estivate.attribute(entity, attributeName)); }
    public List<Float> getAsListFloat(Entity<?> entity, String attributeName)          { return getAsListFloat(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Float> getAsListFloat(AttributeGetter<T, P> attributeGetter)    { return getAsListFloat(Estivate.attribute(attributeGetter)); }
    public List<Float> getAsListFloat(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapList(new FloatMapper(), index); }

    public List<Double> getAsListDouble() { return mapList(new DoubleMapper()); }
    public List<Double> getAsListDouble(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapList(new DoubleMapper(), index); }
    public List<Double> getAsListDouble(Class<?> entity, String attributeName)         { return getAsListDouble(Estivate.attribute(entity, attributeName)); }
    public List<Double> getAsListDouble(Entity<?> entity, String attributeName)        { return getAsListDouble(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Double> getAsListDouble(AttributeGetter<T, P> attributeGetter)  { return getAsListDouble(Estivate.attribute(attributeGetter)); }
    public List<Double> getAsListDouble(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapList(new DoubleMapper(), index); }

    public List<Boolean> getAsListBoolean() { return mapList(new BooleanMapper()); }
    public List<Boolean> getAsListBoolean(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapList(new BooleanMapper(), index); }
    public List<Boolean> getAsListBoolean(Class<?> entity, String attributeName)       { return getAsListBoolean(Estivate.attribute(entity, attributeName)); }
    public List<Boolean> getAsListBoolean(Entity<?> entity, String attributeName)      { return getAsListBoolean(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Boolean> getAsListBoolean(AttributeGetter<T, P> attributeGetter){ return getAsListBoolean(Estivate.attribute(attributeGetter)); }
    public List<Boolean> getAsListBoolean(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapList(new BooleanMapper(), index); }
    
    public List<Date> getAsListDate() { return mapList(new DateMapper()); }
    public List<Date> getAsListDate(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapList(new DateMapper(), index); }
    public List<Date> getAsListDate(Class<?> entity, String attributeName)             { return getAsListDate(Estivate.attribute(entity, attributeName)); }
    public List<Date> getAsListDate(Entity<?> entity, String attributeName)            { return getAsListDate(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<Date> getAsListDate(AttributeGetter<T, P> attributeGetter)      { return getAsListDate(Estivate.attribute(attributeGetter)); }
    public List<Date> getAsListDate(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapList(new DateMapper(), index); }
    
    public List<LocalDateTime> getAsListLocalDateTime() { return mapList(new LocalDateTimeMapper()); }
    public List<LocalDateTime> getAsListLocalDateTime(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapList(new LocalDateTimeMapper(), index); }
    public List<LocalDateTime> getAsListLocalDateTime(Class<?> entity, String attributeName)       { return getAsListLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public List<LocalDateTime> getAsListLocalDateTime(Entity<?> entity, String attributeName)      { return getAsListLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public <T, P> List<LocalDateTime> getAsListLocalDateTime(AttributeGetter<T, P> attributeGetter){ return getAsListLocalDateTime(Estivate.attribute(attributeGetter)); }
    public List<LocalDateTime> getAsListLocalDateTime(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapList(new LocalDateTimeMapper(), index); }

    public <E extends Enum<E>> List<E> getAsListStringEnum(Class<E> enumClass) { return mapList(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> List<E> getAsListOrdinalEnum(Class<E> enumClass) { return mapList(new OrdinalEnumMapper<>(enumClass)); }
    
    public <T> Set<T> getAsSet(IMapper<T> mapper){ return mapList(mapper).stream().collect(Collectors.toSet());}
    public <T> Set<T> getAsSet(Class<T> entity) { return getAsSet(new EntityMapper<>(context, query, new Entity<>(entity))); }
    public <T> Set<T> getAsSet(Entity<T> entity) { return getAsSet(new EntityMapper<>(context, query, entity)); }
    public Set<?> getAsSet(Attribute attribute) { return getAsSet(new AttributeMapper(attribute.getEntity().entity, attribute.attribute)); }
    public Set<?> getAsSet(Class<?> entity, String attributeName) { return getAsSet(new AttributeMapper(entity, attributeName)); }
    public Set<?> getAsSet(Entity<?> entity, String attributeName) { return getAsSet(new AttributeMapper(entity.entity, attributeName)); }
    public <T, P> Set<P> getAsSet(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) getAsSet(new AttributeMapper(attribute.entity.entity, attribute.attribute)); }
    
    public Set<String> getAsSetString() { return getAsSet(new StringMapper()); }
    public Set<String> getAsSetString(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapSet(new StringMapper(), index); }
    public Set<String> getAsSetString(Class<?> entity, String attributeName) { return getAsSetString(Estivate.attribute(entity, attributeName)); }
    public Set<String> getAsSetString(Entity<?> entity, String attributeName) { return getAsSetString(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<String> getAsSetString(AttributeGetter<T, P> attributeGetter) { return getAsSetString(Estivate.attribute(attributeGetter)); }
    public Set<String> getAsSetString(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapSet(new StringMapper(), index); }

    public Set<Short> getAsSetShort() { return getAsSet(new ShortMapper()); }
    public Set<Short> getAsSetShort(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapSet(new ShortMapper(), index); }
    public Set<Short> getAsSetShort(Class<?> entity, String attributeName) { return getAsSetShort(Estivate.attribute(entity, attributeName)); }
    public Set<Short> getAsSetShort(Entity<?> entity, String attributeName) { return getAsSetShort(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Short> getAsSetShort(AttributeGetter<T, P> attributeGetter) { return getAsSetShort(Estivate.attribute(attributeGetter)); }
    public Set<Short> getAsSetShort(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapSet(new ShortMapper(), index); }

    public Set<Integer> getAsSetInteger() { return getAsSet(new IntegerMapper()); }
    public Set<Integer> getAsSetInteger(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapSet(new IntegerMapper(), index); }
    public Set<Integer> getAsSetInteger(Class<?> entity, String attributeName) { return getAsSetInteger(Estivate.attribute(entity, attributeName)); }
    public Set<Integer> getAsSetInteger(Entity<?> entity, String attributeName) { return getAsSetInteger(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Integer> getAsSetInteger(AttributeGetter<T, P> attributeGetter) { return getAsSetInteger(Estivate.attribute(attributeGetter)); }
    public Set<Integer> getAsSetInteger(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapSet(new IntegerMapper(), index); }

    public Set<Long> getAsSetLong() { return getAsSet(new LongMapper()); }
    public Set<Long> getAsSetLong(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapSet(new LongMapper(), index); }
    public Set<Long> getAsSetLong(Class<?> entity, String attributeName) { return getAsSetLong(Estivate.attribute(entity, attributeName)); }
    public Set<Long> getAsSetLong(Entity<?> entity, String attributeName) { return getAsSetLong(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Long> getAsSetLong(AttributeGetter<T, P> attributeGetter) { return getAsSetLong(Estivate.attribute(attributeGetter)); }
    public Set<Long> getAsSetLong(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapSet(new LongMapper(), index); }

    public Set<Float> getAsSetFloat() { return getAsSet(new FloatMapper()); }
    public Set<Float> getAsSetFloat(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapSet(new FloatMapper(), index); }
    public Set<Float> getAsSetFloat(Class<?> entity, String attributeName) { return getAsSetFloat(Estivate.attribute(entity, attributeName)); }
    public Set<Float> getAsSetFloat(Entity<?> entity, String attributeName) { return getAsSetFloat(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Float> getAsSetFloat(AttributeGetter<T, P> attributeGetter) { return getAsSetFloat(Estivate.attribute(attributeGetter)); }
    public Set<Float> getAsSetFloat(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapSet(new FloatMapper(), index); }

    public Set<Double> getAsSetDouble() { return getAsSet(new DoubleMapper());}
    public Set<Double> getAsSetDouble(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapSet(new DoubleMapper(), index); }
    public Set<Double> getAsSetDouble(Class<?> entity, String attributeName) { return getAsSetDouble(Estivate.attribute(entity, attributeName)); }
    public Set<Double> getAsSetDouble(Entity<?> entity, String attributeName) { return getAsSetDouble(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Double> getAsSetDouble(AttributeGetter<T, P> attributeGetter) { return getAsSetDouble(Estivate.attribute(attributeGetter)); }
    public Set<Double> getAsSetDouble(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapSet(new DoubleMapper(), index); }

    public Set<Boolean> getAsSetBoolean() { return getAsSet(new BooleanMapper()); }
    public Set<Boolean> getAsSetBoolean(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapSet(new BooleanMapper(), index); }
    public Set<Boolean> getAsSetBoolean(Class<?> entity, String attributeName) { return getAsSetBoolean(Estivate.attribute(entity, attributeName)); }
    public Set<Boolean> getAsSetBoolean(Entity<?> entity, String attributeName) { return getAsSetBoolean(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Boolean> getAsSetBoolean(AttributeGetter<T, P> attributeGetter) { return getAsSetBoolean(Estivate.attribute(attributeGetter)); }
    public Set<Boolean> getAsSetBoolean(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapSet(new BooleanMapper(), index); }

    public Set<Date> getAsSetDate() { return getAsSet(new DateMapper()); }
    public Set<Date> getAsSetDate(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapSet(new DateMapper(), index); }
    public Set<Date> getAsSetDate(Class<?> entity, String attributeName) { return getAsSetDate(Estivate.attribute(entity, attributeName)); }
    public Set<Date> getAsSetDate(Entity<?> entity, String attributeName) { return getAsSetDate(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<Date> getAsSetDate(AttributeGetter<T, P> attributeGetter) { return getAsSetDate(Estivate.attribute(attributeGetter)); }
    public Set<Date> getAsSetDate(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapSet(new DateMapper(), index); }

    public Set<LocalDateTime> getAsSetLocalDateTime() { return getAsSet(new LocalDateTimeMapper()); }
    public Set<LocalDateTime> getAsSetLocalDateTime(Attribute attribute) { int index = indexOf(attribute); return index == -1 ? null : mapSet(new LocalDateTimeMapper(), index); }
    public Set<LocalDateTime> getAsSetLocalDateTime(Class<?> entity, String attributeName) { return getAsSetLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public Set<LocalDateTime> getAsSetLocalDateTime(Entity<?> entity, String attributeName) { return getAsSetLocalDateTime(Estivate.attribute(entity, attributeName)); }
    public <T, P> Set<LocalDateTime> getAsSetLocalDateTime(AttributeGetter<T, P> attributeGetter) { return getAsSetLocalDateTime(Estivate.attribute(attributeGetter)); }
    public Set<LocalDateTime> getAsSetLocalDateTime(String columnName) { int index = columnNames.indexOf(columnName); return index == -1 ? null : mapSet(new LocalDateTimeMapper(), index); }

    public <E extends Enum<E>> Set<E> getAsSetStringEnum(Class<E> enumClass) { return getAsSet(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> Set<E> getAsSetOrdinalEnum(Class<E> enumClass) { return getAsSet(new OrdinalEnumMapper<>(enumClass)); }


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
