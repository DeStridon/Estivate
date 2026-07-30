package com.estivate.test;

import java.sql.SQLException;
import java.util.stream.Collectors;

import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.h2.jdbcx.JdbcConnectionPool;
import org.h2.tools.Server;

import com.estivate.Estivate;
import com.estivate.NameMapper;
import com.estivate.context.Context;
import com.estivate.context.H2Context;
import com.estivate.index.Annotations.TableIndex;
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

public class DatabaseGenerator {

	private static Context context = null;

	
	
	public static Context getContext() {
		
		if(context == null) {
			
			try {
				context = new H2Context(datasource());
			} catch (NamingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			try {
				Server.createWebServer("-web", "-webAllowOthers", "-webPort", "8083").start();
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			context.nameMapper = new TestNameMapper();
			
		}
		
		ReconciliationManager reconciliationManager = new ReconciliationManager(context);
		
		reconciliationManager.addEntitiesFromPackages("com.estivate.test.entities");
		reconciliationManager.addResolvers(BasicResolver.class);
		reconciliationManager.applyResolvers();
		

//		context.createTableIfNotExists(ProductEntity.class);
//		IndexDiff productIndexDiff = new IndexDiff(context, ProductEntity.class);
//		productIndexDiff.addUnimplemented();
//
//		context.createTableIfNotExists(OrderLineEntity.class);
//		IndexDiff orderLineIndexDiff = new IndexDiff(context, OrderLineEntity.class);
//		orderLineIndexDiff.addUnimplemented();
//
//		context.createTableIfNotExists(OrderEntity.class);
//		IndexDiff orderIndexDiff = new IndexDiff(context, OrderEntity.class);
//		orderIndexDiff.addUnimplemented();
//
//		context.createTableIfNotExists(CustomerEntity.class);
//		System.out.println(context.showTables());
//		IndexDiff customerIndexDiff = new IndexDiff(context, CustomerEntity.class);
//		customerIndexDiff.addUnimplemented();
//		
//		context.createTableIfNotExists(UserProductRatingEntity.class);
//		IndexDiff userProductRatingIndexDiff = new IndexDiff(context, UserProductRatingEntity.class);
//		userProductRatingIndexDiff.addUnimplemented();

		
		System.out.println(context.showTables().stream().collect(Collectors.joining(", ")));
		System.out.println();
		
		context.selectInterceptor = query -> {};
		context.updateInterceptor = query -> {};
		context.deleteInterceptor = query -> {};
		context.insertInterceptor = object -> {};
		
	
		
		
		
		return context;
				
	}
	
	public static CustomerEntity createRandomCustomer() {
		
		CustomerEntity customerEntity = new CustomerEntity();
		customerEntity.setName("Random Customer "+randomInt(1, 1000));
		customerEntity.setEmail("customer" + randomInt(1, 1000) + "@example.com");
		customerEntity.setAddress("Random Address " + randomInt(1, 100));
		customerEntity.setCountry(randomEnum(CustomerEntity.Country.class));
		customerEntity.setCreated(new java.util.Date());
	
		return customerEntity;

	}

	public static ProductEntity createRandomProduct() {
		ProductEntity productEntity = new ProductEntity();
		productEntity.setName("Random Product "+randomInt(1, 1000));
		productEntity.setDescription("Random Description "+randomInt(1, 1000));
		productEntity.setPrice(randomInt(1, 1000) + 0.0f);
		productEntity.setStock(randomInt(1, 1000));
		productEntity.setCategory(randomEnum(ProductEntity.ProductCategory.class));
		return productEntity;
	}
	
	public static class TestNameMapper extends NameMapper{
		public String mapEntityClass(Class<?> c) { return toSnakeCase(c.getSimpleName()).toUpperCase();}
		public String mapEntityField(String field) { return toSnakeCase(field).toUpperCase();  }
		public String mapDatabaseClass(Class<?> c) { return c.getSimpleName().toUpperCase(); }
		public String mapDatabaseField(String field) { return field.toUpperCase(); }
		public String mapIndex(TableIndex index) { return super.mapIndex(index).toUpperCase(); }
	}
	
	public static String toSnakeCase(String name) {
		return name.replaceAll("([a-z]+)([A-Z]+)", "$1\\_$2").toLowerCase();
    }
	
	public static int randomInt(int from, int to) {
		return (int) Math.floor(Math.random()*(to-from)) + from;
	}
	
	public static <T extends Enum<?>> T randomEnum(Class<T> clazz){
        int x = randomInt(0, clazz.getEnumConstants().length);
        return clazz.getEnumConstants()[x];
    }
	
	public static DataSource datasource() throws NamingException {
	    System.setProperty(javax.naming.Context.INITIAL_CONTEXT_FACTORY, "org.apache.naming.java.javaURLContextFactory");
        System.setProperty(javax.naming.Context.URL_PKG_PREFIXES, "org.apache.naming");
        InitialContext ic = new InitialContext();

        ic.createSubcontext("java:");
        ic.createSubcontext("java:/comp");
        ic.createSubcontext("java:/comp/env");
        ic.createSubcontext("java:/comp/env/jdbc");
        
        JdbcConnectionPool ds = JdbcConnectionPool.create("jdbc:h2:mem:test;FILE_LOCK=NO;MODE=MySQL;DB_CLOSE_ON_EXIT=TRUE", "sa", "sasasa");
       
        ic.bind("java:/dsName", ds);
         
        return ds;

	}
	
	
	@ReconciliationScope
	public static class BasicResolver implements ICreateTableResolver, IDropTableResolver, IAddColumnResolver, IDropColumnResolver, IAddIndexResolver, IDropIndexResolver {

		@Override
		public void resolve(Context context, CreateTableDelta delta) {
			Estivate.Tools.createTableFullQuery(delta.entityClass)
				.execute(context);
		}

		@Override
		public void resolve(Context context, DropTableDelta delta) {
			// TODO : drop table
		}
		
		@Override
		public void resolve(Context context, AddColumnDelta delta) {
			Estivate.alterQuery(delta.entityClass)
				.addColumn(delta.getEntityColumnDefinition())
				.execute(context);
		}

		@Override
		public void resolve(Context context, DropColumnDelta delta) {
			try {
				context.dropColumn(delta.entityClass, delta.tableColumnName);
			} catch (Exception e) {
				// TODO Auto-generated catch block
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
