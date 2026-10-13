package com.estivate.test.entities;

import com.estivate.Entity.VirtualForeignKey;
import com.estivate.query.Projection;

import jakarta.persistence.Entity;
import jakarta.persistence.Transient;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;

@Data
@Entity
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

    /** Filled on fetch when ProductEntity is joined; not persisted. */
    @Transient
    @Projection.Attribute(entity = ProductEntity.class, attribute = "name")
    String productName;

}
