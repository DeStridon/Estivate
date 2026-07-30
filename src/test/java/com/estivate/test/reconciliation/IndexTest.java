package com.estivate.test.reconciliation;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.junit.Assert;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.index.Annotations.IndexType;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.CustomerEntity;

public class IndexTest {

	Context context = DatabaseGenerator.getContext();
	

//	@Test
//	public void scanTest() throws IOException {
//		IndexScan is = new IndexScan(context, "com.estivate.test.entities");
//		System.out.println(is.getIndexDiffs());
//	}
	

	@Test
	public void uniqueTest() {

		List<TableIndex> indexes = context.listIndexes(CustomerEntity.class);
		
		Assert.assertTrue(indexes.stream().anyMatch(x -> x.type() == IndexType.UNIQUE));
		
		System.out.println(indexes);
		
	} 
	
	// @Test
	// public void entityIndexTest() {
		
	// 	IndexDiff id = new IndexDiff(context, CustomerEntity.class);
		
	// 	List<TableIndex> indexes = id.getEntityIndexes();
	// 	Assert.assertEquals(2, indexes.size());
		
	// 	Assert.assertEquals(0, id.listUnimplemented().size());
		
	// 	id.cleanSpecific(indexes.get(1));
		
	// 	List<TableIndex> missingDbIndexes = id.listUnimplemented();
	// 	Assert.assertEquals(1, missingDbIndexes.size());

	// }


//	@Test
//	@Disabled
//	public void entityIndexes_includeFullTextDeclaration() {
//		IndexDiff diff = new IndexDiff(context, CustomerEntity.class);
//		List<TableIndex> entityIndexes = diff.getEntityIndexes().stream().filter(x -> x.type() == IndexType.FULLTEXT).collect(Collectors.toList());
//
//		Assert.assertEquals(1, entityIndexes.size());
//		Assert.assertEquals(IndexType.FULLTEXT, entityIndexes.get(0).type());
//		Assert.assertEquals("FT_NAME", entityIndexes.get(0).name());
//		Assert.assertEquals(CustomerEntity.Fields.name, entityIndexes.get(0).columns()[0].value());
//	}
//
//	@Test
//	@Disabled
//	public void indexDiff_detectsTypeMismatchBetweenEntityAndDatabase() {
//		String indexName = "IDX_BODY_FULLTEXT";
//		context.removeIndex(CustomerEntity.class, indexName);
//		Assert.assertTrue(
//				context.addIndex(
//						CustomerEntity.class,
//						indexName,
//						IndexType.DEFAULT,
//						Arrays.asList("BODY")));
//
//		IndexDiff diff = new IndexDiff(context, CustomerEntity.class);
//
//		Assert.assertEquals(1, diff.getEntityIndexes().size());
//		Assert.assertEquals(IndexType.FULLTEXT, diff.getEntityIndexes().get(0).type());
//
//		Optional<TableIndex> databaseIndex = diff.getDatabaseIndexes().stream()
//				.filter(i -> indexName.equalsIgnoreCase(i.name()))
//				.findFirst();
//		Assert.assertTrue(databaseIndex.isPresent());
//		Assert.assertEquals(IndexType.DEFAULT, databaseIndex.get().type());
//
//		Assert.assertEquals(1, diff.listUnimplemented().size());
//		Assert.assertEquals(IndexType.FULLTEXT, diff.listUnimplemented().get(0).type());
//		Assert.assertTrue(diff.listUndeclared().stream()
//				.anyMatch(i -> indexName.equalsIgnoreCase(i.name()) && i.type() == IndexType.DEFAULT));
//	}
//
//	@Test
//	@Disabled
//	public void applySpecific_abortsWhenIndexNameAlreadyExistsWithDifferentType() {
//		String indexName = "IDX_BODY_FULLTEXT";
//		context.removeIndex(CustomerEntity.class, indexName);
//		Assert.assertTrue(
//				context.addIndex(
//						CustomerEntity.class,
//						indexName,
//						IndexType.DEFAULT,
//						Arrays.asList("BODY")));
//
//		IndexDiff diff = new IndexDiff(context, CustomerEntity.class);
//		TableIndex expectedFullText = diff.getEntityIndexes().get(0);
//
//		Assert.assertFalse(diff.applySpecific(expectedFullText));
//	}

	@Test
	@Disabled
	public void addFullTextIndex_roundTripsInListIndexesWhenSupported() {
		String indexName = "FT_ROUNDTRIP";
		context.removeIndex(CustomerEntity.class, indexName);

		boolean created = context.addIndex(
				CustomerEntity.class,
				indexName,
				IndexType.FULLTEXT,
				Arrays.asList("BODY"));
		Assert.assertTrue( "H2 does not support CREATE FULLTEXT INDEX in this mode", created);

		Optional<TableIndex> index = context.listIndexes(CustomerEntity.class).stream()
				.filter(i -> indexName.equalsIgnoreCase(i.name()))
				.findFirst();

		Assert.assertTrue(index.isPresent());
		Assert.assertEquals(IndexType.FULLTEXT, index.get().type());
		Assert.assertEquals(CustomerEntity.Fields.name, index.get().columns()[0].value());
	}
	
}
