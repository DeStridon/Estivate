package com.estivate.test.entities;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class ProductEntity {

    long id;

    String name;

    ProductCategory category;

    Float price;

    Integer available;


    public enum ProductCategory{
        Camera,
        Computer,
        Screen,
        Accessories
    }

}
