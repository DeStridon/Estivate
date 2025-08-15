package com.estivate.test.entities;

import com.estivate.Entity.VirtualForeignKey;

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
public class OrderLineEntity {

    long id;

    @VirtualForeignKey(entity = OrderEntity.class)
    long orderId;
    
    @VirtualForeignKey(entity = ProductEntity.class)
    long productId;

    int amount;

    float unitPrice;

    float totalPrice;

}
