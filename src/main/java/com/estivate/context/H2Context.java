package com.estivate.context;


import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.sql.DataSource;

import com.estivate.Statement;
import com.estivate.index.Annotations;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.result.ResultRow;
import com.estivate.index.Annotations.IndexType;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class H2Context extends Context {
	
	
	public boolean tracePerformances = false;
	
	public H2Context(DataSource datasource) {
		super(datasource);
	}
		
	@SneakyThrows
	public List<TableIndex> listIndexes(Class<?> c) {
		
		List<TableIndex> indexes = new ArrayList<>();
		
		try(Connection connection = datasource.getConnection(); 
			Statement indexQueryStatement = new Statement(this, connection);
			Statement indexColumnQueryStatement = new Statement(this, connection); ){
			
			indexQueryStatement.appendQuery("SELECT * FROM information_schema.indexes WHERE table_schema = 'PUBLIC' AND table_name=").appendQuery("'"+nameMapper.toTableName(c)+"'");
			indexColumnQueryStatement.appendQuery("SELECT * FROM information_schema.index_columns WHERE table_schema = 'PUBLIC' AND table_name=").appendQuery("'"+nameMapper.toTableName(c)+"'");
			
			List<ResultRow> indexResults = fetchListAsResults(indexQueryStatement);
			List<ResultRow> columnResults = fetchListAsResults(indexColumnQueryStatement);
			
			for(ResultRow indexResult : indexResults) {
				List<ResultRow> indexColumnResults = columnResults.stream().filter(x -> x.columnAsString("INDEX_NAME").equals(indexResult.columnAsString("INDEX_NAME"))).collect(Collectors.toList());
				
				List<IndexColumn> indexColumns = indexColumnResults.stream().map(x-> Annotations.ColumnIndex(findEntityName(c, x.columnAsString("COLUMN_NAME")), 0)).collect(Collectors.toList());

				TableIndex ci = Annotations.CompositeIndex(indexResult.columnAsString("INDEX_NAME"), getIndexType(indexResult.columnAsString("INDEX_TYPE_NAME")), indexColumns);
				indexes.add(ci);
			}
			
			return indexes;
		}
    }


	public IndexType getIndexType(String typeName) {
		switch(typeName) {
			case "PRIMARY KEY": return IndexType.PRIMARY;
			case "UNIQUE INDEX": return IndexType.UNIQUE;
			default: return IndexType.DEFAULT;
		}
	}
	
	

}
