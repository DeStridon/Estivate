package com.estivate.util;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.persistence.Index;
import javax.persistence.Table;

import com.estivate.Context;



public class IndexDiff {
	
	Context context;
	Class<?> c;
	
	
	public IndexDiff(Context context, Class<?> c) {
		this.context = context;
		this.c = c;
	}
	
	public List<Index> getMissingDatabaseIndex(){
	
		// 1. List indexes of class
		
		// 2. List indexes in database
		
		// 3. Do the difference
		return null;
	}
	
	public List<Index> getCodeIndex(){
		
		c.getAnnotationsByType(Table.class);
		Table[] tables = c.getDeclaredAnnotationsByType(Table.class);
		
		List<Index> indexes = new ArrayList<>();
		for(Table table : tables) {
			indexes.addAll(Arrays.asList(table.indexes()));
		}
		
		return indexes;
	
	}

	public List<Index> getDatabaseIndex(){
		List<String> indexStrings = context.listIndexes(c);
		return null;
	}
	
	

	
	
}
