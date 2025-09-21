package com.estivate.test.entities;

import com.estivate.Entity.VirtualForeignKey;
import lombok.experimental.FieldNameConstants;


@FieldNameConstants
public class UserProductRatingEntity {

    @VirtualForeignKey(entity = CustomerEntity.class)
    private CustomerEntity user;

    @VirtualForeignKey(entity = ProductEntity.class)
    private ProductEntity product;

    private int rating;
	
}
