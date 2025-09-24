package com.estivate.test.query;

import org.junit.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.OrderLineEntity;
import com.estivate.test.entities.ProductEntity;

import org.junit.Assert;

public class QueryPruneUnusedJoinsTest {

    Context context = DatabaseGenerator.getContext();

    @Test
    public void testPruneUnusedJoins() {
    	
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .joinInner(CustomerEntity.class, OrderEntity.class)
            .joinInner(OrderEntity.class, OrderLineEntity.class)
            .joinInner(OrderLineEntity.class, ProductEntity.class)
            .gt(OrderEntity.class, OrderEntity.Fields.totalAmount, 100.0);
        
        String beforePrune = context.queryAsString(query);
        System.out.println("Before pruning: " + beforePrune);
        Assert.assertTrue(beforePrune.contains("INNER JOIN ORDERENTITY_D"));
        Assert.assertTrue(beforePrune.contains("INNER JOIN ORDERLINEENTITY_D"));
        Assert.assertTrue(beforePrune.contains("INNER JOIN PRODUCTENTITY_D")); 

        
        query.pruneUnusedJoins();
        
        String afterPrune = context.queryAsString(query);
        System.out.println("After pruning: " + afterPrune);
        Assert.assertTrue(afterPrune.contains("INNER JOIN ORDERENTITY_D"));
        Assert.assertFalse(afterPrune.contains("INNER JOIN ORDERLINEENTITY_D"));
        Assert.assertFalse(afterPrune.contains("INNER JOIN PRODUCTENTITY_D")); 
        
    }


}
