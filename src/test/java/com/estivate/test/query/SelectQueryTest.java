package com.estivate.test.query;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;

public class SelectQueryTest {

    Context context = DatabaseGenerator.getContext();

    @Test
    public void testSelectQuery() {
    
        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .in(CustomerEntity.class, AbstractEntity.Fields.id, new ArrayList<>());

        boolean thrown = false;

        try{
            List<CustomerEntity> results = context.fetchListAs(query, CustomerEntity.class);
        }
        catch(Exception e) {
            thrown = true;
            e.printStackTrace();
        }

        Assert.assertTrue(thrown);

    }

}
