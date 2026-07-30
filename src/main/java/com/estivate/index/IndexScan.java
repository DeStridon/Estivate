//package com.estivate.index;
//
//
//import java.util.ArrayList;
//import java.util.HashSet;
//import java.util.List;
//import java.util.Set;
//
//import org.reflections.Reflections;
//
//import com.estivate.context.Context;
//import com.estivate.index.Annotations.TableIndex;
//import com.estivate.index.Annotations.TableIndexes;
//
//import lombok.Getter;
//import lombok.extern.slf4j.Slf4j;
//
//@Slf4j
//public class IndexScan {
//
//	String packageName;
//	Context context;
//
//	@Getter
//	List<IndexDiff> indexDiffs = new ArrayList<>();
//
//	public IndexScan(Context context, String packageName) {
//		this.context = context;
//		this.packageName = packageName;
//		
//		Reflections reflections = new Reflections(packageName);
//
//		Set<Class<?>> classes = new HashSet<>();
//		classes.addAll(reflections.getTypesAnnotatedWith(TableIndexes.class));
//		classes.addAll(reflections.getTypesAnnotatedWith(TableIndex.class));
//		
//
//		// list all entities in the package
//		for(Class<?> c : classes) {
//			indexDiffs.add(new IndexDiff(context, c));
//		}
//
//	}
//
//	public void cleanAll(){
//		for(IndexDiff indexDiff : indexDiffs) {
//			indexDiff.removeUndeclared();
//		}
//	}
//	
//	public void applyAll(){
//		for(IndexDiff indexDiff : indexDiffs) {
//			indexDiff.addUnimplemented();
//		}
//	}
//
//	public void cleanAndApplyAll(){
//		for(IndexDiff indexDiff : indexDiffs) {
//			indexDiff.cleanAndApply();
//		}
//	}
//
//
//	
//
//
//}
