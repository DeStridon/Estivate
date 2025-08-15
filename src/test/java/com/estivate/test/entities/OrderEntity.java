package com.estivate.test.entities;

import java.util.Date;

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
public class OrderEntity {

    long id;

    @VirtualForeignKey(entity = CustomerEntity.class)
    long customerId;

    Date created;

    Float totalAmount;



}
