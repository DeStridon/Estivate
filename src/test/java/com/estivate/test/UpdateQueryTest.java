package com.estivate.test;

import org.junit.Assert;
import org.junit.Test;

import com.estivate.context.Context;
import com.estivate.query.UpdateQuery;
import com.estivate.test.entities.CustomerEntity;

public class UpdateQueryTest {

    Context context = DatabaseGenerator.getContext();

    @Test
    public void testUpdateQuery() {
        UpdateQuery<CustomerEntity> updateQuery = new UpdateQuery<>(CustomerEntity.class)
        .set(CustomerEntity.Fields.name, "John Doe")
        .set(CustomerEntity.Fields.email, "john.doe@example.com")
        .eq(CustomerEntity.Fields.id, 1);

//        String query = context.queryAsString(updateQuery);
//        Assert.assertEquals("UPDATE customer SET name = ?, email = ? WHERE id = ?", query);
    }

}
