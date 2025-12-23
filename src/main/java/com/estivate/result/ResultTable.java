package com.estivate.result;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
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
public class ResultTable {

	final Context context;
    final String[] columnNames;
    final SelectQuery<?> query;

    final List<ResultRow> rows;

    public ResultTable(Context context, String[] columnNames, SelectQuery<?> query){
    	this.context = context;
    	this.columnNames = columnNames;
    	this.query = query;
    	this.rows = new ArrayList<>();
    }


    public ResultRow addRow(String[] values) {
    	ResultRow row = new ResultRow(this, values);
    	rows.add(row);
    	return row;
    }

    public <T> T map(IMapper<T> mapper) {  return rows.isEmpty() ? null : mapper.map(rows.get(0).getColumnValues());}

    public <T> T mapTo(Class<T> entity) { return map(new EntityMapper<>(context, query, entity)); }
    public <T> T mapTo(Entity<T> entity) { return map(new EntityMapper<>(context, query, entity.entity)); }
    public Object mapToAttribute(Class<?> entity, String attribute) { return map(new AttributeMapper(entity, attribute)); }
    public Object mapToAttribute(Entity<?> entity, String attribute) { return map(new AttributeMapper(entity.entity, attribute)); }
    public String mapToString() { return map(new StringMapper()); }
    public Short mapToShort() { return map(new ShortMapper()); }
    public Integer mapToInteger() { return map(new IntegerMapper()); }
    public Long mapToLong() { return map(new LongMapper()); }
    public Float mapToFloat() { return map(new FloatMapper()); }
    public Double mapToDouble() { return map(new DoubleMapper()); }
    public Boolean mapToBoolean() { return map(new BooleanMapper()); }
    public Date mapToDate() { return map(new DateMapper()); }
    public LocalDateTime mapToLocalDateTime() { return map(new LocalDateTimeMapper()); }

    // public LocalDate mapLocalDate() { return mapSingle(new LocalDateMapper()); }
    // public LocalTime mapLocalTime() { return mapSingle(new LocalTimeMapper()); }

    public <E extends Enum<E>> E mapToStringEnum(Class<E> enumClass) { return map(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> E mapToOrdinalEnum(Class<E> enumClass) { return map(new OrdinalEnumMapper<>(enumClass)); }

    public <T> List<T> mapList(IMapper<T> mapper) {
        List<T> results = new ArrayList<>();
        for(ResultRow row : rows) {
            T result = mapper.map(row.getColumnValues());
            results.add(result);
        }
        return results;
    }


    public <T> List<T> toList(Class<T> entity) { return mapList(new EntityMapper<>(context, query, entity)); }


    public List<?> toList(Attribute attribute) { return mapList(new AttributeMapper(attribute.getEntity().entity, attribute.attribute)); }
    public List<?> toList(Class<?> entity, String attributeName) { return mapList(new AttributeMapper(entity, attributeName)); }
    public List<?> toList(Entity<?> entity, String attributeName) { return mapList(new AttributeMapper(entity.entity, attributeName)); }
    public <T, P> List<P> toList(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) mapList(new AttributeMapper(attribute.entity.entity, attribute.attribute)); }
    

    public List<String> toListString() { return mapList(new StringMapper()); }
    public List<String> toListString(Attribute attribute) { return mapList(new StringMapper()); }
    public List<String> toListString(Class<?> entity, String attributeName) { return mapList(new StringMapper()); }
    public List<String> toListString(Entity<?> entity, String attributeName) { return mapList(new StringMapper()); }
    public <T, P> List<P> toListString(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) mapList(new StringMapper()); }
    public List<String> toListString(String columnName) { return mapList(new StringMapper()); }

    public List<Short> toListShort() { return mapList(new ShortMapper()); }
    public List<Short> toListShort(Attribute attribute) { return mapList(new ShortMapper()); }
    public List<Short> toListShort(Class<?> entity, String attributeName) { return mapList(new ShortMapper()); }
    public List<Short> toListShort(Entity<?> entity, String attributeName) { return mapList(new ShortMapper()); }
    public <T, P> List<P> toListShort(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) mapList(new ShortMapper()); }
    public List<Short> toListShort(String columnName) { return mapList(new ShortMapper()); }

    public List<Integer> toListInteger() { return mapList(new IntegerMapper()); }
    public List<Integer> toListInteger(Attribute attribute) { return mapList(new IntegerMapper()); }
    public List<Integer> toListInteger(Class<?> entity, String attributeName) { return mapList(new IntegerMapper()); }
    public List<Integer> toListInteger(Entity<?> entity, String attributeName) { return mapList(new IntegerMapper()); }
    public <T, P> List<P> toListInteger(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) mapList(new IntegerMapper()); }
    public List<Integer> toListInteger(String columnName) { return mapList(new IntegerMapper()); }

    public List<Long> toListLong() { return mapList(new LongMapper()); }
    public List<Long> toListLong(Attribute attribute) { return mapList(new LongMapper()); }
    public List<Long> toListLong(Class<?> entity, String attributeName) { return mapList(new LongMapper()); }
    public List<Long> toListLong(Entity<?> entity, String attributeName) { return mapList(new LongMapper()); }
    public <T, P> List<P> toListLong(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) mapList(new LongMapper()); }
    public List<Long> toListLong(String columnName) { return mapList(new LongMapper()); }
    
    public List<Float> toListFloat() { return mapList(new FloatMapper()); }
    public List<Float> toListFloat(Attribute attribute) { return mapList(new FloatMapper()); }
    public List<Float> toListFloat(Class<?> entity, String attributeName) { return mapList(new FloatMapper()); }
    public List<Float> toListFloat(Entity<?> entity, String attributeName) { return mapList(new FloatMapper()); }
    public <T, P> List<P> toListFloat(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) mapList(new FloatMapper()); }
    public List<Float> toListFloat(String columnName) { return mapList(new FloatMapper()); }

    public List<Double> toListDouble() { return mapList(new DoubleMapper()); }
    public List<Double> toListDouble(Attribute attribute) { return mapList(new DoubleMapper()); }
    public List<Double> toListDouble(Class<?> entity, String attributeName) { return mapList(new DoubleMapper()); }
    public List<Double> toListDouble(Entity<?> entity, String attributeName) { return mapList(new DoubleMapper()); }
    public <T, P> List<P> toListDouble(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) mapList(new DoubleMapper()); }
    public List<Double> toListDouble(String columnName) { return mapList(new DoubleMapper()); }

    public List<Boolean> toListBoolean() { return mapList(new BooleanMapper()); }
    public List<Boolean> toListBoolean(Attribute attribute) { return mapList(new BooleanMapper()); }
    public List<Boolean> toListBoolean(Class<?> entity, String attributeName) { return mapList(new BooleanMapper()); }
    public List<Boolean> toListBoolean(Entity<?> entity, String attributeName) { return mapList(new BooleanMapper()); }
    public <T, P> List<P> toListBoolean(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) mapList(new BooleanMapper()); }
    public List<Boolean> toListBoolean(String columnName) { return mapList(new BooleanMapper()); }
    
    public List<Date> toListDate() { return mapList(new DateMapper()); }
    public List<Date> toListDate(Attribute attribute) { return mapList(new DateMapper()); }
    public List<Date> toListDate(Class<?> entity, String attributeName) { return mapList(new DateMapper()); }
    public List<Date> toListDate(Entity<?> entity, String attributeName) { return mapList(new DateMapper()); }
    public <T, P> List<P> toListDate(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) mapList(new DateMapper()); }
    public List<Date> toListDate(String columnName) { return mapList(new DateMapper()); }
    
    public List<LocalDateTime> toListLocalDateTime() { return mapList(new LocalDateTimeMapper()); }
    public List<LocalDateTime> toListLocalDateTime(Attribute attribute) { return mapList(new LocalDateTimeMapper()); }
    public List<LocalDateTime> toListLocalDateTime(Class<?> entity, String attributeName) { return mapList(new LocalDateTimeMapper()); }
    public List<LocalDateTime> toListLocalDateTime(Entity<?> entity, String attributeName) { return mapList(new LocalDateTimeMapper()); }
    public <T, P> List<P> toListLocalDateTime(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) mapList(new LocalDateTimeMapper()); }
    public List<LocalDateTime> toListLocalDateTime(String columnName) { return mapList(new LocalDateTimeMapper()); }

    public <E extends Enum<E>> List<E> toListStringEnum(Class<E> enumClass) { return mapList(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> List<E> toListOrdinalEnum(Class<E> enumClass) { return mapList(new OrdinalEnumMapper<>(enumClass)); }
    
    public <T> Set<T> toSet(IMapper<T> mapper){ return mapList(mapper).stream().collect(Collectors.toSet());}
    public <T> Set<T> toSet(Class<T> entity) { return toSet(new EntityMapper<>(context, query, entity)); }
    public Set<?> toSet(Class<?> entity, String attributeName) { return toSet(new AttributeMapper(entity, attributeName)); }
    public Set<?> toSet(Entity<?> entity, String attributeName) { return toSet(new AttributeMapper(entity.entity, attributeName)); }
    public Set<?> toSet(Attribute attribute) { return toSet(new AttributeMapper(attribute.getEntity().entity, attribute.attribute)); }
    public <T, P> Set<P> toSet(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) toSet(new AttributeMapper(attribute.entity.entity, attribute.attribute)); }
    
    public Set<String> toSetString() { return toSet(new StringMapper()); }
    public Set<String> toSetString(Attribute attribute) { return toSet(new StringMapper()); }
    public Set<String> toSetString(Class<?> entity, String attributeName) { return toSet(new StringMapper()); }
    public Set<String> toSetString(Entity<?> entity, String attributeName) { return toSet(new StringMapper()); }
    public <T, P> Set<P> toSetString(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) toSet(new StringMapper()); }

    public Set<Short> toSetShort() { return toSet(new ShortMapper()); }
    public Set<Short> toSetShort(Attribute attribute) { return toSet(new ShortMapper()); }
    public Set<Short> toSetShort(Class<?> entity, String attributeName) { return toSet(new ShortMapper()); }
    public Set<Short> toSetShort(Entity<?> entity, String attributeName) { return toSet(new ShortMapper()); }
    public <T, P> Set<P> toSetShort(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) toSet(new ShortMapper()); }

    public Set<Integer> toSetInteger() { return toSet(new IntegerMapper()); }
    public Set<Integer> toSetInteger(Attribute attribute) { return toSet(new IntegerMapper()); }
    public Set<Integer> toSetInteger(Class<?> entity, String attributeName) { return toSet(new IntegerMapper()); }
    public Set<Integer> toSetInteger(Entity<?> entity, String attributeName) { return toSet(new IntegerMapper()); }
    public <T, P> Set<P> toSetInteger(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) toSet(new IntegerMapper()); }

    public Set<Long> toSetLong() { return toSet(new LongMapper()); }
    public Set<Long> toSetLong(Attribute attribute) { return toSet(new LongMapper()); }
    public Set<Long> toSetLong(Class<?> entity, String attributeName) { return toSet(new LongMapper()); }
    public Set<Long> toSetLong(Entity<?> entity, String attributeName) { return toSet(new LongMapper()); }
    public <T, P> Set<P> toSetLong(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) toSet(new LongMapper()); }

    public Set<Float> toSetFloat() { return toSet(new FloatMapper()); }
    public Set<Float> toSetFloat(Attribute attribute) { return toSet(new FloatMapper()); }
    public Set<Float> toSetFloat(Class<?> entity, String attributeName) { return toSet(new FloatMapper()); }
    public Set<Float> toSetFloat(Entity<?> entity, String attributeName) { return toSet(new FloatMapper()); }
    public <T, P> Set<P> toSetFloat(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) toSet(new FloatMapper()); }

    public Set<Double> toSetDouble() { return toSet(new DoubleMapper());}
    public Set<Double> toSetDouble(Attribute attribute) { return toSet(new DoubleMapper()); }
    public Set<Double> toSetDouble(Class<?> entity, String attributeName) { return toSet(new DoubleMapper()); }
    public Set<Double> toSetDouble(Entity<?> entity, String attributeName) { return toSet(new DoubleMapper()); }
    public <T, P> Set<P> toSetDouble(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) toSet(new DoubleMapper()); }

    public Set<Boolean> toSetBoolean() { return toSet(new BooleanMapper()); }
    public Set<Boolean> toSetBoolean(Attribute attribute) { return toSet(new BooleanMapper()); }
    public Set<Boolean> toSetBoolean(Class<?> entity, String attributeName) { return toSet(new BooleanMapper()); }
    public Set<Boolean> toSetBoolean(Entity<?> entity, String attributeName) { return toSet(new BooleanMapper()); }
    public <T, P> Set<P> toSetBoolean(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) toSet(new BooleanMapper()); }

    public Set<Date> toSetDate() { return toSet(new DateMapper()); }
    public Set<Date> toSetDate(Attribute attribute) { return toSet(new DateMapper()); }
    public Set<Date> toSetDate(Class<?> entity, String attributeName) { return toSet(new DateMapper()); }
    public Set<Date> toSetDate(Entity<?> entity, String attributeName) { return toSet(new DateMapper()); }
    public <T, P> Set<P> toSetDate(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) toSet(new DateMapper()); }
    public Set<LocalDateTime> toSetLocalDateTime() { return toSet(new LocalDateTimeMapper()); }
    public Set<LocalDateTime> toSetLocalDateTime(Attribute attribute) { return toSet(new LocalDateTimeMapper()); }
    public Set<LocalDateTime> toSetLocalDateTime(Class<?> entity, String attributeName) { return toSet(new LocalDateTimeMapper()); }
    public Set<LocalDateTime> toSetLocalDateTime(Entity<?> entity, String attributeName) { return toSet(new LocalDateTimeMapper()); }
    public <T, P> Set<P> toSetLocalDateTime(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) toSet(new LocalDateTimeMapper()); }

    public <E extends Enum<E>> Set<E> toSetStringEnum(Class<E> enumClass) { return toSet(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> Set<E> toSetOrdinalEnum(Class<E> enumClass) { return toSet(new OrdinalEnumMapper<>(enumClass)); }


    public boolean isEmpty() { return rows.isEmpty(); }
    public int size() { return rows.size(); }
    public ResultRow getFirst() { return rows.isEmpty() ? null : rows.get(0); }
    public ResultRow get(int index) { return rows.get(index); }
    public ResultRow getLast() { return rows.isEmpty() ? null : rows.get(rows.size() - 1); }
}
