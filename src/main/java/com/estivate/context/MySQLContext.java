package com.estivate.context;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.estivate.Result;
import com.estivate.Statement;
import com.estivate.entity.Index.ColumnIndex;
import com.estivate.entity.Index.CompositeIndex;

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
	public List<CompositeIndex> listIndexes(Class<?> c) {
		List<CompositeIndex> indexes = new ArrayList<>();

		try (Connection connection = datasource.getConnection()){
			Statement statement = new Statement(this, connection).appendQuery("SHOW INDEX FROM ").appendQuery(nameMapper.mapDatabaseClass(c));
			
			List<Result> results = this.fetchList(statement);
			List<IndexRow> indexRows = new ArrayList<>();
			for(Result result : results) {
				
				IndexRow indexRow = IndexRow.builder()
					.table(result.getAsString("Table"))
					.keyName(result.getAsString("Key_name"))
					.seqInIndex(result.getAsInteger("Seq_in_index"))
					.columnName(result.getAsString("Column_name"))
					.build();
			
				indexRows.add(indexRow);
				
			}
			
			Map<String, List<IndexRow>> indexRowMap = indexRows.stream().collect(Collectors.groupingBy(IndexRow::getKeyName));
	        
			
			for(Entry<String, List<IndexRow>> indexRowMapEntry : indexRowMap.entrySet()) {
				List<ColumnIndex> indexColumns = indexRowMapEntry.getValue().stream().map(x-> ColumnIndex(findEntityName(c, x.getColumnName()), null)).collect(Collectors.toList());
				CompositeIndex ci = CompositeIndex(indexRowMapEntry.getKey(), indexColumns);
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
		Integer seqInIndex;
		String columnName;
		
	}
	
	
	

}
