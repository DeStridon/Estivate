package com.estivate.test.entities;

import com.estivate.Entity.VirtualForeignKey;

import jakarta.persistence.Entity;
import lombok.experimental.FieldNameConstants;

@Entity
@FieldNameConstants
public class UserProductRatingEntity {

    @VirtualForeignKey(entity = CustomerEntity.class)
    private Long userId;

    @VirtualForeignKey(entity = ProductEntity.class)
    private Long productId;

    private float rating;
	
}
