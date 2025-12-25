package com.estivate.test.query;

import org.junit.Assert;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.SelectQuery;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.CustomerEntity;

public class SelectQueryWindowTest {

    Context context = DatabaseGenerator.getContext();

    @Test
    public void testWindow() {
     
        SelectQuery<CustomerEntity> query  = Estivate.selectQuery(CustomerEntity.class)
            .select(Estivate.attributeWindow(Estivate.Functions.rowNumber, "rn")
                .partitionBy(CustomerEntity.class, CustomerEntity.Fields.country)
                .orderBy(CustomerEntity.class, CustomerEntity.Fields.name));

        String queryString = context.queryAsString(query);
        Assert.assertTrue(queryString.contains("ROW_NUMBER() OVER ("));
        


                


    }

    /*
    SELECT * 
        FROM (
            SELECT *,
                ROW_NUMBER() OVER (
                    PARTITION BY action
                    ORDER BY
                        FIELD(status,'Progress','Queued','Error','Done'),
                        completed DESC
                ) AS rn
            FROM queue_job
            WHERE resource_id = 59
            AND payload = ' {} '
        ) t
        WHERE rn = 1; 
*/

}
