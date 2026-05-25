package com.estivate.context;


import java.lang.reflect.Field;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.estivate.Statement;
import com.estivate.index.Annotations;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.query.CreateQuery;
import com.estivate.reconciliation.ColumnModel;
import com.estivate.result.ResultRow;
import com.estivate.util.FieldUtils;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class MySQLContext extends Context {
	
	
	public boolean tracePerformances = false;
	
	public MySQLContext(DataSource datasource) {
		super(datasource);
	}
		
	
	
	@SneakyThrows
	public List<TableIndex> listIndexes(Class<?> c) {
		List<TableIndex> indexes = new ArrayList<>();

		try(Connection connection = datasource.getConnection();
			Statement statement = new Statement(this, connection); ){

			statement.appendQuery("SHOW INDEX FROM ").appendQuery(nameMapper.toTableName(c));
			
			List<ResultRow> results = this.fetchListAsResults(statement);
			List<IndexRow> indexRows = new ArrayList<>();
			for(ResultRow result : results) {
				String physicalIndexType = null;
				if(result.getResultTable().getColumnNames().contains("Index_type")) {
					physicalIndexType = result.asString("Index_type");
				}

				IndexRow indexRow = IndexRow.builder()
					.table(result.asString("Table"))
					.keyName(result.asString("Key_name"))
					.nonUnique(result.asBoolean("Non_unique"))
					.seqInIndex(result.asInteger("Seq_in_index"))
					.columnName(result.asString("Column_name"))
					.columnLength(result.asInteger("Sub_part"))
					.physicalIndexType(physicalIndexType)
					.build();
			
				indexRows.add(indexRow);
				
			}
			
			Map<String, List<IndexRow>> indexRowMap = indexRows.stream().collect(Collectors.groupingBy(IndexRow::getKeyName));
	        
			
			for(Entry<String, List<IndexRow>> indexRowMapEntry : indexRowMap.entrySet()) {

				List<IndexColumn> indexColumns = indexRowMapEntry.getValue().stream().map(x-> Annotations.ColumnIndex(findEntityName(c, x.getColumnName()), x.getColumnLength() != null ? x.getColumnLength() : 0)).collect(Collectors.toList());

				IndexRow firstRow = indexRowMapEntry.getValue().get(0);
				IndexType indexType = resolveIndexType(
						indexRowMapEntry.getKey(),
						firstRow.getNonUnique(),
						firstRow.getPhysicalIndexType());
				
				TableIndex ci = Annotations.CompositeIndex(indexRowMapEntry.getKey(), indexType, indexColumns);
				indexes.add(ci);
			}
			
		}
		return indexes;
    }

	/** Maps {@code SHOW INDEX} metadata to {@link IndexType}. Package-private for unit tests. */
	static IndexType resolveIndexType(String keyName, boolean nonUnique, String physicalIndexType) {
		if("PRIMARY".equals(keyName)) {
			return IndexType.PRIMARY;
		}
		if(!nonUnique) {
			return IndexType.UNIQUE;
		}
		if(physicalIndexType != null && physicalIndexType.equalsIgnoreCase("FULLTEXT")) {
			return IndexType.FULLTEXT;
		}
		return IndexType.DEFAULT;
	}
	
	@Data
	@Builder
	@NoArgsConstructor
	@AllArgsConstructor
	public static class IndexRow{
		String table;
		String keyName;
		Boolean nonUnique;
		Integer seqInIndex;
		String columnName; 
		Integer columnLength;
		/** MySQL {@code SHOW INDEX} {@code Index_type}: BTREE, HASH, FULLTEXT, SPATIAL, etc. */
		String physicalIndexType;
		
	}


	






	@Override
	public ColumnModel.ColumnFormat getColumnFormat(ColumnModel.EntityColumn entityColumn) {
		if (entityColumn.getType() == Integer.class || entityColumn.getType() == int.class) return new ColumnModel.ColumnFormat("INT");
        if (entityColumn.getType() == Long.class || entityColumn.getType() == long.class) return new ColumnModel.ColumnFormat("BIGINT");
        if (entityColumn.getType() == Short.class || entityColumn.getType() == short.class) return new ColumnModel.ColumnFormat("SMALLINT");
        if (entityColumn.getType() == Byte.class || entityColumn.getType() == byte.class) return new ColumnModel.ColumnFormat("TINYINT");
        if (entityColumn.getType() == Float.class || entityColumn.getType() == float.class) return new ColumnModel.ColumnFormat("FLOAT");
        if (entityColumn.getType() == Double.class || entityColumn.getType() == double.class) return new ColumnModel.ColumnFormat("DOUBLE");
		if (entityColumn.getType() == java.math.BigDecimal.class) return new ColumnModel.ColumnFormat("DECIMAL");
        if (entityColumn.getType() == Boolean.class || entityColumn.getType() == boolean.class) return new ColumnModel.ColumnFormat("BOOLEAN");
		if (entityColumn.getType() == String.class) return new ColumnModel.ColumnFormat("VARCHAR", 255); 
        if (entityColumn.getType() == Date.class || entityColumn.getType() == java.sql.Date.class) return new ColumnModel.ColumnFormat("DATETIME");
		if (entityColumn.getType() == java.sql.Timestamp.class) return new ColumnModel.ColumnFormat("DATETIME");
        if (entityColumn.getType() == LocalDateTime.class) return new ColumnModel.ColumnFormat("DATETIME");
        if (entityColumn.getType() == LocalDate.class) return new ColumnModel.ColumnFormat("DATE");
        if (entityColumn.getType() == byte[].class) return new ColumnModel.ColumnFormat("BLOB");
		throw new IllegalArgumentException("Unsupported type: " + entityColumn.getType());
	}
	

}
