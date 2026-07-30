package com.estivate.test.query.projection;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Projection;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.OrderLineEntity;
import com.estivate.test.entities.ProductEntity;
import com.estivate.test.entities.ProductEntity.ProductCategory;

public class ProjectionGroupTest {

    Context context = DatabaseGenerator.getContext();

    @Projection.NestedBy(entity = OrderEntity.class, attribute = "customerId")
    public static class MainDto{

        @Projection.Attribute(entity = OrderEntity.class, attribute = "customerId")
        long customerId;

        @Projection.Attribute(entity = OrderEntity.class, attribute = "status")
        OrderEntity.OrderStatus status;

        @Projection.Attribute(entity = OrderEntity.class, attribute = "created")
        Date created;

        @Projection.Nested
        List<NestedListDto> groupedDtos;

        @Projection.Nested
        NestedSingleDto orderDetails;

    }

    public static class NestedListDto{

        @Projection.Attribute(entity = ProductEntity.class, attribute = "category")
        ProductEntity.ProductCategory category;

        @Projection.Count(entity = ProductEntity.class, attribute = "id")
        int productCount;

        @Projection.Sum(entity = ProductEntity.class, attribute = "price")
        float totalPrice;

    }

    public static class NestedSingleDto{

        @Projection.Attribute(entity = OrderEntity.class, attribute = "status")
        OrderEntity.OrderStatus status;

        @Projection.Attribute(entity = OrderEntity.class, attribute = "created")
        Date created;

    }


    @Test
    public void testGroupBy() {

        // Create 4 products
        ProductEntity product1 = ProductEntity.builder().name("Lipstick").category(ProductCategory.Beauty).price(2.0f).build();
        ProductEntity product2 = ProductEntity.builder().name("Daycream").category(ProductCategory.Beauty).price(1.5f).build();
        ProductEntity product3 = ProductEntity.builder().name("Dress").category(ProductCategory.Clothing).price(3.0f).build();
        ProductEntity product4 = ProductEntity.builder().name("Shirt").category(ProductCategory.Clothing).price(2.5f).build();


        context.insert(product1);
        context.insert(product2);
        context.insert(product3);
        context.insert(product4);

        // Create 2 orders
        OrderEntity order1 = OrderEntity.builder().customerId(100L).status(OrderEntity.OrderStatus.COMPLETED).created(new Date()).build();
        OrderEntity order2 = OrderEntity.builder().customerId(200L).status(OrderEntity.OrderStatus.COMPLETED).created(new Date()).build();

        context.insert(order1);
        context.insert(order2);

        // Create 3 order lines for each order
        OrderLineEntity line11 = OrderLineEntity.builder().orderId(order1.getId()).productId(product1.getId()).build();
        OrderLineEntity line12 = OrderLineEntity.builder().orderId(order1.getId()).productId(product2.getId()).build();
        OrderLineEntity line13 = OrderLineEntity.builder().orderId(order1.getId()).productId(product3.getId()).build();

        context.insert(line11);
        context.insert(line12);
        context.insert(line13);

        OrderLineEntity line21 = OrderLineEntity.builder().orderId(order2.getId()).productId(product2.getId()).build();
        OrderLineEntity line22 = OrderLineEntity.builder().orderId(order2.getId()).productId(product3.getId()).build();
        OrderLineEntity line23 = OrderLineEntity.builder().orderId(order2.getId()).productId(product4.getId()).build();

        context.insert(line21);
        context.insert(line22);
        context.insert(line23);


        List<MainDto> mainDtos = Estivate.selectQuery(OrderEntity.class)
            .joinInner(OrderEntity.class, OrderLineEntity.class)
            .joinInner(OrderLineEntity.class, ProductEntity.class)
            .in(OrderEntity::getId, Arrays.asList(order1.getId(), order2.getId()))
            
            .groupBy(OrderEntity::getCustomerId)
            .groupBy(ProductEntity::getCategory)

            .fetchAsList(context, MainDto.class);

        Assertions.assertEquals(2, mainDtos.size());

        MainDto mixedDto1 = mainDtos.get(0);
        Assertions.assertEquals(100L, mixedDto1.customerId);
        Assertions.assertEquals(OrderEntity.OrderStatus.COMPLETED, mixedDto1.status);
        Assertions.assertNotNull(mixedDto1.created);
        Assertions.assertEquals(2, mixedDto1.groupedDtos.size());
        Assertions.assertEquals(ProductEntity.ProductCategory.Clothing, mixedDto1.groupedDtos.get(0).category);
        Assertions.assertEquals(1, mixedDto1.groupedDtos.get(0).productCount);
        Assertions.assertEquals(ProductEntity.ProductCategory.Beauty, mixedDto1.groupedDtos.get(1).category);
        Assertions.assertEquals(2, mixedDto1.groupedDtos.get(1).productCount);
        Assertions.assertEquals(3.5f, mixedDto1.groupedDtos.get(1).totalPrice);



    }




}
