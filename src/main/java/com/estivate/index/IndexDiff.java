package com.estivate.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.context.Context;
import com.estivate.index.Annotations.IndexColumn;
import com.estivate.index.Annotations.TableIndex;

import lombok.Getter;
import lombok.ToString;
import lombok.extern.slf4j.Slf4j;


@Slf4j
@ToString
public class IndexDiff {
	
	Context context;
	
	@Getter
	Class<?> entity;
	
	
	public IndexDiff(Context context, Class<?> c) {
		this.context = context;
		this.entity = c;
	}
	
	
    public void cleanAndApply() {
		clean();
		apply();
    }


	public List<TableIndex> listToClean(){
		
		List<TableIndex> entityIndexes = getEntityIndexes();
		List<TableIndex> databaseIndexes = getDatabaseIndexes();
		List<TableIndex> resultIndexes = new ArrayList<>();

		for(TableIndex databaseIndex : databaseIndexes) {
			TableIndex entityIndex = entityIndexes.stream().filter(x -> indexEquals(x, databaseIndex)).findFirst().orElse(null);
			if(entityIndex == null) {
				resultIndexes.add(databaseIndex);
			}
		}

		return resultIndexes;

	}
	
	public List<TableIndex> listToApply(){
	
		List<TableIndex> entityIndexes = getEntityIndexes();
		List<TableIndex> databaseIndexes = getDatabaseIndexes();
		List<TableIndex> resultIndexes = new ArrayList<>();
		
		for(TableIndex entityIndex : entityIndexes) {
			TableIndex databaseIndex = databaseIndexes.stream().filter(x -> indexEquals(x, entityIndex)).findFirst().orElse(null);
			if(databaseIndex == null) {
				resultIndexes.add(entityIndex);
			}
		}
		
		return resultIndexes;
		
	}

	public void clean() {
		for(TableIndex index : listToClean()) {
			cleanSpecific(index);
		}
	}

	

	public void apply() {
		for(TableIndex index : listToApply()) {
			applySpecific(index);
		}
	}
	
	public boolean cleanSpecific(TableIndex index){
		return context.removeIndex(entity, index.name());
	}

	public boolean applySpecific(TableIndex index) {

		if(getDatabaseIndexes().stream().anyMatch(x -> x.name().equals(index.name()))) {
			log.error("Table " + entity.getSimpleName() + ", aborting index creation : index with same name already exists: "+index.name());
			return false;
		}

		List<String> columns = Arrays.asList(index.columns()).stream().map(x -> context.nameMapper.mapDatabaseField(x.value())+ (x.length() > 0 ? "("+x.length()+")":"")).collect(Collectors.toList());
		return context.addIndex(entity, index.name(), index.type(), columns);

	}
	
	
	public List<TableIndex> getEntityIndexes(){
		
		TableIndex[] compositeIndex = entity.getDeclaredAnnotationsByType(TableIndex.class);
		
		List<TableIndex> indexes = new ArrayList<>();
		for(TableIndex index : compositeIndex) {
			indexes.add(Context.CompositeIndex(context.nameMapper.mapIndex(index), index.type(), Arrays.asList(index.columns())));
		}
		
		return indexes;
	
	}
	
	public List<TableIndex> getDatabaseIndexes(){
		List<TableIndex> indexStrings = context.listIndexes(entity);
		return indexStrings;
	}
	
	
	boolean indexEquals(TableIndex left, TableIndex right) {
		if(!left.name().toUpperCase().equals(right.name().toUpperCase())) {
			return false;
		}
		if(left.columns().length != right.columns().length) {
			return false;
		}
		for(int i = 0; i < left.columns().length; i++) {
			IndexColumn leftColumn = left.columns()[i];
			IndexColumn rightColumn = right.columns()[i];
			
			if(!leftColumn.value().equals(rightColumn.value())) {
				return false;
			}
		}
		return true;
	}




	
}
