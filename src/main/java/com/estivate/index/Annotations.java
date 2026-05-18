package com.estivate.index;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Repeatable;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.List;

public class Annotations {
	
	@Target(ElementType.TYPE)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TableIndexes {
		
		TableIndex[] value();
	}
	
	@Repeatable(TableIndexes.class)
	@Retention(RetentionPolicy.RUNTIME)
	public @interface TableIndex {
		String name() default "";
		IndexType type() default IndexType.DEFAULT;
		IndexColumn[] columns() default {};	
	}
	
	@Retention(RetentionPolicy.RUNTIME)
	public static @interface IndexColumn{
		String value();
		int length() default 0;
	}

	public static enum IndexType{ 
		DEFAULT,
		UNIQUE,
		PRIMARY,
		FULLTEXT
	}
	
	public static TableIndex CompositeIndex(String name, IndexType type, List<IndexColumn> columns) {
		IndexColumn[] array = new IndexColumn[columns.size()];
		columns.toArray(array);
		
		TableIndex index = new TableIndex() {
			@Override
			public String name() { return name; }

			@Override
			public IndexType type() { return type; }

			@Override
			public Class<? extends Annotation> annotationType() { return null; }

			@Override
			public IndexColumn[] columns() { return array; }
		};
		
		return index;
	}
	
	public static IndexColumn ColumnIndex(String value, int length) {
		IndexColumn index = new IndexColumn() {
			@Override
			public Class<? extends Annotation> annotationType() { return null; }

			@Override
			public String value() { return value; }

			@Override
			public int length() { return length; }
		};
		
		return index;
	}
	

}
