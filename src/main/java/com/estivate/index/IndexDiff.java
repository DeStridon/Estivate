package com.estivate.index;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.estivate.context.Context;
import com.estivate.index.Annotations.ColumnIndex;
import com.estivate.index.Annotations.CompositeIndex;
import com.estivate.index.Annotations.TableIndexes;

import lombok.Getter;
import lombok.ToString;



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


	public List<CompositeIndex> listToClean(){
		
		List<CompositeIndex> entityIndexes = getEntityIndexes();
		List<CompositeIndex> databaseIndexes = getDatabaseIndexes();
		List<CompositeIndex> resultIndexes = new ArrayList<>();

		for(CompositeIndex databaseIndex : databaseIndexes) {
			CompositeIndex entityIndex = entityIndexes.stream().filter(x -> indexEquals(x, databaseIndex)).findFirst().orElse(null);
			if(entityIndex == null) {
				resultIndexes.add(databaseIndex);
			}
		}

		return resultIndexes;

	}
	
	public List<CompositeIndex> listToApply(){
	
		List<CompositeIndex> entityIndexes = getEntityIndexes();
		List<CompositeIndex> databaseIndexes = getDatabaseIndexes();
		List<CompositeIndex> resultIndexes = new ArrayList<>();
		
		for(CompositeIndex entityIndex : entityIndexes) {
			CompositeIndex databaseIndex = databaseIndexes.stream().filter(x -> indexEquals(x, entityIndex)).findFirst().orElse(null);
			if(databaseIndex == null) {
				resultIndexes.add(entityIndex);
			}
		}
		
		return resultIndexes;
		
	}

	public void clean() {
		for(CompositeIndex index : listToClean()) {
			cleanSpecific(index);
		}
	}

	

	public void apply() {
		for(CompositeIndex index : listToApply()) {
			applySpecific(index);
		}
	}
	
	public boolean cleanSpecific(CompositeIndex index){
		return context.removeIndex(entity, index.name());
	}

	public boolean applySpecific(CompositeIndex index) {
		List<String> columns = Arrays.asList(index.columns()).stream().map(x -> context.nameMapper.mapDatabaseField(x.value())+ (x.length() > 0 ? "("+x.length()+")":"")).collect(Collectors.toList());
		return context.addIndex(entity, index.name(), columns);
	}
	
	
	public List<CompositeIndex> getEntityIndexes(){
		
		CompositeIndex[] compositeIndex = entity.getDeclaredAnnotationsByType(CompositeIndex.class);
		
		List<CompositeIndex> indexes = new ArrayList<>();
		for(CompositeIndex index : compositeIndex) {
			indexes.add(Context.CompositeIndex(context.nameMapper.mapIndex(index), Arrays.asList(index.columns())));
		}
		
		return indexes;
	
	}
	
	public List<CompositeIndex> getDatabaseIndexes(){
		List<CompositeIndex> indexStrings = context.listIndexes(entity);
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




	
}
