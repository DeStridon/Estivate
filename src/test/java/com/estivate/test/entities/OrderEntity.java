package com.estivate.test.entities;

import java.util.Date;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;

import com.estivate.Entity.VirtualForeignKey;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldNameConstants;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@NoArgsConstructor
@AllArgsConstructor
@FieldNameConstants
public class OrderEntity extends AbstractEntity {

    @VirtualForeignKey(entity = CustomerEntity.class)
    long customerId;

    @Enumerated(EnumType.ORDINAL)
    OrderStatus status;

    Date created;

    Date updated;

    Float totalAmount;


    public enum OrderStatus{
        PENDING,
        PROCESSING,
        COMPLETED,
        CANCELLED;
    }


}
