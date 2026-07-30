package com.estivate.test.entities.projection;

import com.estivate.query.Projection;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.CustomerEntity;
import com.estivate.test.entities.CustomerEntity.Country;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

public class CustomerProjection {

    /**
     * Projection class with country grouping
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CustomerCountAliasByCountryProjection {
        @Projection.Attribute(entity = CustomerEntity.class, attribute = CustomerEntity.Fields.country)
        private Country country;
        
        @Projection.Count(entity = CustomerEntity.class, attribute = AbstractEntity.Fields.id, alias = "count")
        private Long count;
    }
}
