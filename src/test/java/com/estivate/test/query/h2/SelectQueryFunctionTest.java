package com.estivate.test.query.h2;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.entities.CustomerEntity;

public class SelectQueryFunctionTest {

    Context context = H2TestDatabase.getContext();

    @Test
	void lowerTest(){

        SelectQuery<CustomerEntity> query = Estivate.selectQuery(CustomerEntity.class)
            .eq(Estivate.attribute(CustomerEntity.class, CustomerEntity.Fields.name, Estivate.Functions.lower), "john");

        String queryString = context.queryAsString(query);

        Assert.assertTrue(queryString.contains("WHERE lower("));

    }
}
