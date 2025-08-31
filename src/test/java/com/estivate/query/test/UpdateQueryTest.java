package com.estivate.query.test;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.context.Context;
import com.estivate.query.UpdateQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.CustomerEntity;

public class UpdateQueryTest {

    Context context = DatabaseGenerator.getContext();

    @Test
    public void testUpdateQuery() {
        UpdateQuery<CustomerEntity> updateQuery = new UpdateQuery<>(CustomerEntity.class)
        .set(CustomerEntity.Fields.name, "John Doe")
        .set(CustomerEntity.Fields.email, "john.doe@example.com")
        .eq(CustomerEntity.Fields.id, 1);

        String query = context.queryAsString(updateQuery);
        Assert.assertEquals("UPDATE CUSTOMERENTITY_D SET CUSTOMERENTITY_D.NAME_D  = ? , CUSTOMERENTITY_D.EMAIL_D  = ? WHERE CUSTOMERENTITY_D.ID_D = ?", query);
        
    }

}
