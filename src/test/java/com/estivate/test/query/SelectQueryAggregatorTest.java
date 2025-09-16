package com.estivate.test.query;



import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.ProductEntity;

public class SelectQueryAggregatorTest {

    Context context = DatabaseGenerator.getContext();
	
	@Test
    public void orAggregatorTest(){

        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .or(null, null, null)
            .or(null, null, null)
            .or(null, null, null);


        String queryString = context.queryAsString(query);

        Assertions.assertFalse(queryString.contains("WHERE AND"));

    }

}
