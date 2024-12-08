package com.estivate.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.apache.commons.lang3.StringUtils;

import com.estivate.context.Context;
import com.estivate.entity.Index.ColumnIndex;
import com.estivate.entity.Index.CompositeIndex;
import com.estivate.query.Query;



public class IndexDiff {
	
	Context context;
	Class<?> c;
	
	
	public IndexDiff(Context context, Class<?> c) {
		this.context = context;
		this.c = c;
	}
	
	public void addMissingIndexes() {
		for(CompositeIndex index : getMissingIndexes()) {
			applyIndex(index);
		}
	}
	
	public List<CompositeIndex> getMissingIndexes(){
	
		// 1. List indexes of class
		List<CompositeIndex> entityIndexes = getEntityIndexes();
		// 2. List indexes in database
		List<CompositeIndex> databaseIndexes = getDatabaseIndexes();
		
		List<CompositeIndex> resultIndexes = new ArrayList<>();
		
		for(CompositeIndex entityIndex : entityIndexes) {
			CompositeIndex databaseIndex = databaseIndexes.stream().filter(x -> indexEquals(x, entityIndex)).findFirst().orElse(null);
			if(databaseIndex == null) {
				resultIndexes.add(entityIndex);
			}
		}
		
		
		// 3. Do the difference
		return resultIndexes;
	}
	
	public List<CompositeIndex> getUndefinedIndexes(){
		// 1. List indexes of class
		List<CompositeIndex> entityIndexes = getEntityIndexes();
		// 2. List indexes in database
		List<CompositeIndex> databaseIndexes = getDatabaseIndexes();
		
		List<CompositeIndex> resultIndexes = new ArrayList<>();
		
		for(CompositeIndex databaseIndex : databaseIndexes) {
			CompositeIndex entityIndex = entityIndexes.stream().filter(x -> indexEquals(x, databaseIndex)).findFirst().orElse(null);
			if(entityIndex == null) {
				resultIndexes.add(databaseIndex);
			}
		}
		
		
		// 3. Do the difference
		return resultIndexes;
		
		
	}
	
	public List<CompositeIndex> getEntityIndexes(){
		
		CompositeIndex[] compositeIndex = c.getDeclaredAnnotationsByType(CompositeIndex.class);
		
		List<CompositeIndex> indexes = new ArrayList<>();
		for(CompositeIndex index : compositeIndex) {
			indexes.add(index);
		}
		
		return indexes;
	
	}
	
	public List<CompositeIndex> getDatabaseIndexes(){
		List<CompositeIndex> indexStrings = context.listIndexes(c);
		return indexStrings;
	}
	
	
	boolean indexEquals(CompositeIndex left, CompositeIndex right) {
		if(!left.name().toUpperCase().equals(right.name().toUpperCase())) {
			return false;
		}
		if(left.columns().length != right.columns().length) {
			return false;
		}
		for(int i = 0; i < left.columns().length; i++) {
			ColumnIndex leftColumn = left.columns()[i];
			ColumnIndex rightColumn = right.columns()[i];
			
			if(!leftColumn.value().equals(rightColumn.value())) {
				return false;
			}
		}
		return true;
	}
	
	public void applyIndex(CompositeIndex index) {
		
		List<String> columns = Arrays.asList(index.columns()).stream().map(x -> context.nameMapper.mapDatabaseField(x.value())+ (x.length() > 0 ? "("+x.length()+")":"")).collect(Collectors.toList());
		String indexName = index.name();
		if(StringUtils.isBlank(indexName)) {
			indexName = Arrays.asList(index.columns()).stream().map(x -> x.value()).collect(Collectors.joining("_"));
		}
		context.addIndex(c, indexName, columns);
		
	}

	
	
	
	
	

	
	
}
