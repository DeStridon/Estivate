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


    public <T> List<T> mapToList(Class<T> entity) { return mapList(new EntityMapper<>(context, query, entity)); }
    public List<?> mapToListAttribute(Class<?> entity, String attributeName) { return mapList(new AttributeMapper(entity, attributeName)); }
    public List<?> mapToListAttribute(Entity<?> entity, String attributeName) { return mapList(new AttributeMapper(entity.entity, attributeName)); }
    public List<?> mapToListAttribute(Attribute attribute) { return mapList(new AttributeMapper(attribute.getEntity().entity, attribute.attribute)); }
    public <T, P> List<P> mapToListAttribute(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (List<P>) mapList(new AttributeMapper(attribute.entity.entity, attribute.attribute)); }
    public List<String> mapToListString() { return mapList(new StringMapper()); }
    public List<Short> mapToListShort() { return mapList(new ShortMapper()); }
    public List<Integer> mapToListInteger() { return mapList(new IntegerMapper()); }
    public List<Long> mapToListLong() { return mapList(new LongMapper()); }
    public List<Float> mapToListFloat() { return mapList(new FloatMapper()); }
    public List<Double> mapToListDouble() { return mapList(new DoubleMapper()); }
    public List<Boolean> mapToListBoolean() { return mapList(new BooleanMapper()); }
    public List<Date> mapToListDate() { return mapList(new DateMapper()); }
    public List<LocalDateTime> mapToListLocalDateTime() { return mapList(new LocalDateTimeMapper()); }


    public <E extends Enum<E>> List<E> mapToListStringEnum(Class<E> enumClass) { return mapList(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> List<E> mapToListOrdinalEnum(Class<E> enumClass) { return mapList(new OrdinalEnumMapper<>(enumClass)); }
    
    public <T> Set<T> mapSet(IMapper<T> mapper){ return mapList(mapper).stream().collect(Collectors.toSet());}
    public <T> Set<T> mapToSet(Class<T> entity) { return mapSet(new EntityMapper<>(context, query, entity)); }
    public Set<Object> mapToSetAttribute(Class<?> entity, String attributeName) { return mapSet(new AttributeMapper(entity, attributeName)); }
    public Set<Object> mapToSetAttribute(Entity<?> entity, String attributeName) { return mapSet(new AttributeMapper(entity.entity, attributeName)); }
    public Set<Object> mapToSetAttribute(Attribute attribute) { return mapSet(new AttributeMapper(attribute.getEntity().entity, attribute.attribute)); }
    public <T, P> Set<P> mapToSetAttribute(AttributeGetter<T, P> attributeGetter) { Attribute attribute = Estivate.attribute(attributeGetter); return (Set<P>) mapSet(new AttributeMapper(attribute.entity.entity, attribute.attribute)); }
    public Set<String> mapToSetString() { return mapSet(new StringMapper()); }
    public Set<Short> mapToSetShort() { return mapSet(new ShortMapper()); }
    public Set<Integer> mapToSetInteger() { return mapSet(new IntegerMapper()); }
    public Set<Long> mapToSetLong() { return mapSet(new LongMapper()); }
    public Set<Float> mapToSetFloat() { return mapSet(new FloatMapper()); }
    public Set<Double> mapToSetDouble() { return mapSet(new DoubleMapper());}
    public Set<Boolean> mapToSetBoolean() { return mapSet(new BooleanMapper()); }
    public Set<Date> mapToSetDate() { return mapSet(new DateMapper()); }
    public Set<LocalDateTime> mapToSetLocalDateTime() { return mapSet(new LocalDateTimeMapper()); }

    public <E extends Enum<E>> Set<E> mapToSetStringEnum(Class<E> enumClass) { return mapSet(new StringEnumMapper<>(enumClass)); }
    public <E extends Enum<E>> Set<E> mapToSetOrdinalEnum(Class<E> enumClass) { return mapSet(new OrdinalEnumMapper<>(enumClass)); }


    public boolean isEmpty() { return rows.isEmpty(); }
    public int size() { return rows.size(); }
    public ResultRow getFirst() { return rows.isEmpty() ? null : rows.get(0); }
    public ResultRow get(int index) { return rows.get(index); }
    public ResultRow getLast() { return rows.isEmpty() ? null : rows.get(rows.size() - 1); }
}
