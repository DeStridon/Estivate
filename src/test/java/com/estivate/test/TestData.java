package com.estivate.test;

import java.util.Arrays;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

import com.estivate.Estivate;
import com.estivate.NameMapper;
import com.estivate.context.Context;
import com.estivate.index.Annotations.TableIndex;
import com.estivate.index.IndexColumn;
import com.estivate.reconciliation.EstivateReconciliation.AddColumnDelta;
import com.estivate.reconciliation.EstivateReconciliation.AddIndexDelta;
import com.estivate.reconciliation.EstivateReconciliation.CreateTableDelta;
import com.estivate.reconciliation.EstivateReconciliation.DropColumnDelta;
import com.estivate.reconciliation.EstivateReconciliation.DropIndexDelta;
import com.estivate.reconciliation.EstivateReconciliation.DropTableDelta;
import com.estivate.reconciliation.EstivateReconciliation.IAddColumnResolver;
import com.estivate.reconciliation.EstivateReconciliation.IAddIndexResolver;
import com.estivate.reconciliation.EstivateReconciliation.ICreateTableResolver;
import com.estivate.reconciliation.EstivateReconciliation.IDropColumnResolver;
import com.estivate.reconciliation.EstivateReconciliation.IDropIndexResolver;
import com.estivate.reconciliation.EstivateReconciliation.IDropTableResolver;
import com.estivate.reconciliation.EstivateReconciliation.ReconciliationScope;
import com.estivate.reconciliation.ReconciliationManager;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.ProductEntity;

/**
 * Shared fixtures for database-specific test contexts: name mapping, sample rows, and schema reconciliation.
 */
public class TestData {

	/** Guarantees unique emails/names across a shared test database. */
	private static final AtomicLong UNIQUE_SEQ = new AtomicLong();

	private TestData() {
	}

	public static void install(Context context) {
		context.nameMapper = new TestNameMapper();

		ReconciliationManager reconciliationManager = new ReconciliationManager(context);
		reconciliationManager.addEntitiesFromPackages("com.estivate.test.entities");
		reconciliationManager.addResolvers(BasicResolver.class);
		reconciliationManager.applyResolvers();

		context.selectInterceptor = query -> {};
		context.updateInterceptor = query -> {};
		context.deleteInterceptor = query -> {};
		context.insertInterceptor = object -> {};
	}

	public static CustomerEntity createRandomCustomer() {
		long seq = UNIQUE_SEQ.incrementAndGet();

		CustomerEntity customerEntity = new CustomerEntity();
		customerEntity.setName("Random Customer " + seq);
		customerEntity.setEmail("customer" + seq + "@example.com");
		customerEntity.setAddress("Random Address " + randomInt(1, 100));
		customerEntity.setCountry(randomEnum(CustomerEntity.Country.class));
		customerEntity.setCreated(new java.util.Date());

		return customerEntity;
	}

	public static ProductEntity createRandomProduct() {
		ProductEntity productEntity = new ProductEntity();
		productEntity.setName("Random Product " + randomInt(1, 1000));
		productEntity.setDescription("Random Description " + randomInt(1, 1000));
		productEntity.setPrice(randomInt(1, 1000) + 0.0f);
		productEntity.setStock(randomInt(1, 1000));
		productEntity.setCategory(randomEnum(ProductEntity.ProductCategory.class));
		return productEntity;
	}

	public static class TestNameMapper extends NameMapper {
		public String mapEntityClass(Class<?> c) { return toSnakeCase(c.getSimpleName()).toUpperCase(); }
		public String mapEntityField(String field) { return toSnakeCase(field).toUpperCase(); }
		public String mapDatabaseClass(Class<?> c) { return c.getSimpleName().toUpperCase(); }
		public String mapDatabaseField(String field) { return field.toUpperCase(); }
		public String mapIndex(TableIndex index) { return super.mapIndex(index.name(), index.type(), Arrays.asList(index.columns()).stream().map(x -> IndexColumn.of(x)).collect(Collectors.toList())).toUpperCase(); }
	}

	public static String toSnakeCase(String name) {
		return name.replaceAll("([a-z]+)([A-Z]+)", "$1\\_$2").toLowerCase();
	}

	public static int randomInt(int from, int to) {
		return (int) Math.floor(Math.random() * (to - from)) + from;
	}

	public static <T extends Enum<?>> T randomEnum(Class<T> clazz) {
		int x = randomInt(0, clazz.getEnumConstants().length);
		return clazz.getEnumConstants()[x];
	}

	@ReconciliationScope
	public static class BasicResolver implements ICreateTableResolver, IDropTableResolver, IAddColumnResolver, IDropColumnResolver, IAddIndexResolver, IDropIndexResolver {

		@Override
		public void resolve(Context context, CreateTableDelta delta) {
			Estivate.Tools.createTableFullQuery(delta.entityClass, context)
				.execute(context);
		}

		@Override
		public void resolve(Context context, DropTableDelta delta) {
			context.dropTableIfExists(delta.tableName);
		}

		@Override
		public void resolve(Context context, AddColumnDelta delta) {
			Estivate.alterQuery(delta.entityClass)
				.addColumn(delta.projectedColumn)
				.execute(context);
		}

		@Override
		public void resolve(Context context, DropColumnDelta delta) {
			try {
				context.dropColumn(delta.entityClass, delta.tableColumnName);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}

		@Override
		public void resolve(Context context, AddIndexDelta delta) {
			Estivate.alterQuery(delta.getEntityClass())
				.addIndex(delta.indexName, delta.type, delta.columns)
				.execute(context);
		}

		@Override
		public void resolve(Context context, DropIndexDelta delta) {
			Estivate.alterQuery(delta.getEntityClass())
				.dropIndex(delta.indexName)
				.execute(context);
		}

	}

}
