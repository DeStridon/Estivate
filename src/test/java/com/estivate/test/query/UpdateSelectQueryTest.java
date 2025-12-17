package com.estivate.test.query;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.query.UpdateQuery;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.Estivate;

public class UpdateSelectQueryTest {

    Context context = DatabaseGenerator.getContext();

    @Test
    public void testUpdateSelectQuery() {

        UpdateQuery<CustomerEntity> updateQuery = Estivate.updateQuery(CustomerEntity.class)
        .set(CustomerEntity.Fields.name, "John Doe")
        .set(CustomerEntity.Fields.email, "john.doe@example.com")
        .eq(AbstractEntity.Fields.id, Estivate.selectQuery(CustomerEntity.class)
            .select(AbstractEntity.Fields.id)
            .eq(CustomerEntity.Fields.name, "John Doe")
        );
        
        String query = context.queryAsString(updateQuery);
        Assert.assertTrue(query.contains("WHERE CUSTOMERENTITY.ID = (SELECT CUSTOMERENTITY.ID as `CUSTOMER_ENTITY.ID`"));

    }

}
