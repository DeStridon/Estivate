package com.estivate.index;


import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.LinkedHashSet;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import java.util.List;
import java.util.Set;

import com.estivate.context.Context;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.index.Annotations.TableIndexes;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class IndexScan {

	String packageName;
	Context context;

	@Getter
	List<IndexDiff> indexDiffs = new ArrayList<>();

	public IndexScan(Context context, String packageName) {
		this.context = context;
		this.packageName = packageName;

		// list all entities in the package
		for(Class<?> c : listClasses()) {
			if(c.getDeclaredAnnotation(TableIndexes.class) == null && c.getDeclaredAnnotationsByType(TableIndex.class).length == 0) {
				continue;
			}
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

//	public Set<Class<?>> listClasses() {
//		InputStream stream = ClassLoader.getSystemClassLoader().getResourceAsStream(packageName.replaceAll("[.]", "/"));
//		BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
//		return reader.lines()
//				.filter(line -> line.endsWith(".class"))
//				.map(line -> getClass(line, packageName))
//				.filter(clazz -> clazz != null && clazz.isAnnotationPresent(TableIndexes.class))
//				.collect(Collectors.toSet());
//	}
//
//	private Class<?> getClass(String className, String packageName) {
//		try {
//			return Class.forName(packageName + "." + className.substring(0, className.lastIndexOf('.')));
//		} catch (ClassNotFoundException e) {
//			// handle the exception
//		}
//		return null;
//	}
	
	 /**
     * Scans all classes accessible from the context class loader which belong to the given package and subpackages.
     *
     * @param packageName The base package
     * @return The classes
     * @throws ClassNotFoundException
     * @throws IOException
     */
    private Set<Class<?>> listClasses() {
        try {
	    	ClassLoader classLoader = Thread.currentThread().getContextClassLoader();
	        String path = packageName.replace('.', '/');
	        Enumeration<URL> resources = classLoader.getResources(path);
	        List<File> dirs = new ArrayList<>();
	        while (resources.hasMoreElements()) {
	            URL resource = resources.nextElement();
	            dirs.add(new File(resource.getFile()));
	        }
	        Set<Class<?>> classes = new LinkedHashSet<>();
	        for (File directory : dirs) {
	            classes.addAll(findClasses(directory, packageName));
	        }
	        return classes;
        }
        catch(Exception e) {
        	log.error("Error scanning package", e);
        }
        return new LinkedHashSet<>();
    }

    /**
     * Recursive method used to find all classes in a given directory and subdirs.
     *
     * @param directory   The base directory
     * @param packageName The package name for classes found inside the base directory
     * @return The classes
     * @throws ClassNotFoundException
     */
    private Set<Class<?>> findClasses(File directory, String packageName) {
        Set<Class<?>> classes = new LinkedHashSet<>();
        if (!directory.exists()) {
            return classes;
        }
        File[] files = directory.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                assert !file.getName().contains(".");
                classes.addAll(findClasses(file, packageName + "." + file.getName()));
            } else if (file.getName().endsWith(".class")) {
            	try {
					classes.add(Class.forName(packageName + '.' + file.getName().substring(0, file.getName().length() - 6)));
				} catch (ClassNotFoundException e) {
					log.error("ClassNotFound", e);
					e.printStackTrace();
				}
            }
        }
        return classes;
    }

}
