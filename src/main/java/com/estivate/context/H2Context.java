package com.estivate.context;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.estivate.Result;
import com.estivate.Statement;
import com.estivate.entity.Index.ColumnIndex;
import com.estivate.entity.Index.CompositeIndex;
import com.estivate.query.Query;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class H2Context extends Context {
	
	
	public boolean tracePerformances = false;
	
	public H2Context(DataSource datasource) {
		super(datasource);
	}
		
	
	// Should be removed
	@SneakyThrows
	public List<CompositeIndex> listIndexes(Class<?> c) {
		
		List<CompositeIndex> indexes = new ArrayList<>();
		
		try (Connection connection = datasource.getConnection()){
			
			Statement indexQueryStatement = new Statement(this, connection).appendQuery("SELECT * FROM information_schema.indexes WHERE table_schema = 'PUBLIC' AND table_name=").appendQuery("'"+nameMapper.mapDatabaseClass(c)+"'");
			Statement indexColumnQueryStatement = new Statement(this, connection).appendQuery("SELECT * FROM information_schema.index_columns WHERE table_schema = 'PUBLIC' AND table_name=").appendQuery("'"+nameMapper.mapDatabaseClass(c)+"'");
			
			List<Result> indexResults = list(indexQueryStatement);
			List<Result> columnResults = list(indexColumnQueryStatement);
			
			for(Result indexResult : indexResults) {
				List<Result> indexColumnResults = columnResults.stream().filter(x -> x.getAsString("INDEX_NAME").equals(indexResult.getAsString("INDEX_NAME"))).collect(Collectors.toList());
				
				List<ColumnIndex> indexColumns = indexColumnResults.stream().map(x-> ColumnIndex(findEntityName(c, x.getAsString("COLUMN_NAME")), null)).collect(Collectors.toList());
				CompositeIndex ci = CompositeIndex(indexResult.getAsString("INDEX_NAME"), indexColumns);
				indexes.add(ci);
			}
			
			return indexes;
		}
    }
	
	

}
