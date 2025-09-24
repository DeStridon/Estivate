package com.estivate.test.query;

import org.junit.Assert;
import org.junit.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;


public class QueryPreProcessorTest {
 
    Context context = DatabaseGenerator.getContext();

    @Test
    public void testPreProcessor() {
    	
        context.fetchQueryPreProcessor = (query) -> {
            if(query instanceof SelectQuery && query.getOrders().isEmpty()) {
                ((SelectQuery) query).orderAsc(AbstractEntity.Fields.id);
            }
        };

        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class);

        String queryString = context.queryAsString(query);

        System.out.println(queryString);
        Assert.assertTrue(queryString.contains("ORDER BY"));
        
        
    }


}