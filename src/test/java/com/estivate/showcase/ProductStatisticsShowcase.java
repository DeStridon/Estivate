package com.estivate.showcase;

import org.junit.Test;

import com.estivate.Estivate;
import com.estivate.query.SelectQuery;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.OrderLineEntity;
import com.estivate.test.entities.ProductEntity;
import com.estivate.test.entities.OrderEntity.OrderStatus;

public class ProductStatisticsShowcase {


    @Test
    public void getMostOrderedProducts(){


        // Simulate multiple orders with 3 different products


        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .selectCount(ProductEntity.class, AbstractEntity.Fields.id, "orderCount")
            .joinInner(ProductEntity.class, OrderLineEntity.class)
            .joinInner(OrderLineEntity.class, OrderEntity.class)
            .eq(OrderEntity.class, OrderEntity.Fields.status, OrderStatus.COMPLETED);



    }

    @Test
    public void getMostOrderedProductsInValue(){

        // Simulate multiple orders with 3 different products

        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .selectSum(OrderLineEntity.class, OrderLineEntity.Fields.totalPrice, "orderCount")
            .joinInner(ProductEntity.class, OrderLineEntity.class)
            .joinInner(OrderLineEntity.class, OrderEntity.class)
            .eq(OrderEntity.class, OrderEntity.Fields.status, OrderStatus.COMPLETED);

        //add order by sum
    }

    @Test
    public void getUnorderedProducts(){

        SelectQuery<ProductEntity> query = Estivate.selectQuery(ProductEntity.class)
            .joinLeft(ProductEntity.class, OrderLineEntity.class)
            .joinLeft(OrderLineEntity.class, OrderEntity.class)
            .eq(OrderEntity.class, OrderEntity.Fields.status, OrderStatus.COMPLETED)
            .isNull(OrderLineEntity.class, OrderLineEntity.Fields.productId);


    }





}
