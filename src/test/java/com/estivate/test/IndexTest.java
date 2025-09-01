package com.estivate.test;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.index.IndexDiff;
import com.estivate.index.IndexScan;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.index.Annotations.IndexType;
import com.estivate.test.entities.ParentEntity;

public class IndexTest {

	Context context = DatabaseGenerator.getContext();
	

	@Test
	public void scanTest() throws IOException {
		IndexScan is = new IndexScan(context, "com.estivate.test.entities");
		System.out.println(is.getIndexDiffs());
	}
	

	@Test
	public void uniqueTest() {
		
		List<ParentEntity> parentEntities = context.fetchListAs(Estivate.selectQuery(ParentEntity.class), ParentEntity.class);
		
		List<TableIndex> indexes = context.listIndexes(ParentEntity.class);
		
		Assert.assertTrue(indexes.stream().anyMatch(x -> x.type() == IndexType.UNIQUE));
		
		System.out.println(indexes);
	}
	
	@Test
	public void entityIndexTest() {
		
		IndexDiff id = new IndexDiff(context, ParentEntity.class);
		
		List<TableIndex> indexes = id.getEntityIndexes();
		Assert.assertEquals(3, indexes.size());
		
		Assert.assertEquals(0, id.listUnimplemented().size());
		
		id.cleanSpecific(indexes.get(0));
		
		List<TableIndex> missingDbIndexes = id.listUnimplemented();
		Assert.assertEquals(1, missingDbIndexes.size());

	}
	
}
