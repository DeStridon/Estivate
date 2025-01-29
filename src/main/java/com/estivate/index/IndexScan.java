package com.estivate.index;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.estivate.context.Context;
import com.estivate.index.Annotations.TableIndexes;

import lombok.Getter;

public class IndexScan {

	String packageName;
	Context context;

	@Getter
	List<IndexDiff> indexDiffs;

	public IndexScan(Context context, String packageName) {
		this.context = context;
		this.packageName = packageName;

		// list all entities in the package
		Set<Class<?>> classes = findAllClassesUsingClassLoader(packageName);

		for(Class<?> c : classes) {
			indexDiffs.add(new IndexDiff(context, c));
		}

	}

	public void cleanAll(){
		for(IndexDiff indexDiff : indexDiffs) {
			indexDiff.clean();
		}
	}
	
	public void applyAll(){
		for(IndexDiff indexDiff : indexDiffs) {
			indexDiff.apply();
		}
	}

	public void cleanAndApplyAll(){
		for(IndexDiff indexDiff : indexDiffs) {
			indexDiff.cleanAndApply();
		}
	}

	public Set<Class<?>> findAllClassesUsingClassLoader(String packageName) {
		InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
		return reader.lines()
				.filter(line -> line.endsWith(".class"))
				.map(line -> getClass(line, packageName))
				.filter(clazz -> clazz != null && clazz.isAnnotationPresent(TableIndexes.class))
				.collect(Collectors.toSet());
	}

	private Class<?> getClass(String className, String packageName) {
		try {
			return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
		} catch (ClassNotFoundException e) {
			// handle the exception
		}
		return null;
	}

}
