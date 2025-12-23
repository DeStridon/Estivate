package com.estivate.context;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.estivate.Statement;
import com.estivate.index.Annotations;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.result.ResultRow;

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
				
				IndexRow indexRow = IndexRow.builder()
					.table(result.columnAsString("Table"))
					.keyName(result.columnAsString("Key_name"))
					.nonUnique(result.columnAsBoolean("Non_unique"))
					.seqInIndex(result.columnAsInteger("Seq_in_index"))
					.columnName(result.columnAsString("Column_name"))
					.columnLength(result.columnAsInteger("Sub_part"))
					.build();
			
				indexRows.add(indexRow);
				
			}
			
			Map<String, List<IndexRow>> indexRowMap = indexRows.stream().collect(Collectors.groupingBy(IndexRow::getKeyName));
	        
			
			for(Entry<String, List<IndexRow>> indexRowMapEntry : indexRowMap.entrySet()) {

				List<IndexColumn> indexColumns = indexRowMapEntry.getValue().stream().map(x-> Annotations.ColumnIndex(findEntityName(c, x.getColumnName()), x.getColumnLength() != null ? x.getColumnLength() : 0)).collect(Collectors.toList());

				IndexType indexType = IndexType.DEFAULT;
				if(indexRowMapEntry.getKey().equals("PRIMARY")) {
					indexType = IndexType.PRIMARY;
				}
				else if(!indexRowMapEntry.getValue().get(0).getNonUnique()) {
					indexType = IndexType.UNIQUE;
				}
				
				TableIndex ci = Annotations.CompositeIndex(indexRowMapEntry.getKey(), indexType, indexColumns);
				indexes.add(ci);
			}
			
		}
		return indexes;
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
		
	}
	
	
	

}
