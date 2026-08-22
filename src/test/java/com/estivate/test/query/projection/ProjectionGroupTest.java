package com.estivate.test.query.projection;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import com.estivate.Estivate;
import com.estivate.context.Context;
import com.estivate.query.Projection;
import com.estivate.result.ResultTable;
import com.estivate.test.DatabaseGenerator;
import com.estivate.test.entities.AbstractEntity;
import com.estivate.test.entities.OrderEntity;
import com.estivate.test.entities.OrderLineEntity;
import com.estivate.test.entities.ProductEntity;
import com.estivate.test.entities.ProductEntity.ProductCategory;

public class ProjectionGroupTest {

    Context context = DatabaseGenerator.getContext();

    @Projection.NestedBy(entity = OrderEntity.class, attribute = OrderEntity.Fields.customerId)
    public static class MainDto{

        @Projection.Attribute(entity = OrderEntity.class, attribute = OrderEntity.Fields.customerId)
        long customerId;

        @Projection.Attribute(entity = OrderEntity.class, attribute = OrderEntity.Fields.status)
        OrderEntity.OrderStatus status;

        @Projection.Attribute(entity = OrderEntity.class, attribute = OrderEntity.Fields.created)
        Date created;

        @Projection.Nested
        List<NestedListDto> groupedDtos;

        @Projection.Nested
        NestedSingleDto orderDetails;

    }

    public static class NestedListDto{

        @Projection.Attribute(entity = ProductEntity.class, attribute = ProductEntity.Fields.category)
        ProductEntity.ProductCategory category;

        @Projection.Count(entity = ProductEntity.class, attribute = AbstractEntity.Fields.id)
        int productCount;

        @Projection.Sum(entity = ProductEntity.class, attribute = ProductEntity.Fields.price)
        float totalPrice;

    }

    public static class NestedSingleDto{

        @Projection.Attribute(entity = OrderEntity.class, attribute = OrderEntity.Fields.status)
        OrderEntity.OrderStatus status;

        @Projection.Attribute(entity = OrderEntity.class, attribute = OrderEntity.Fields.created)
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


    @Test
    public void testEntityListWithNestedEntitiesFromTwoRequests_usingAsListWithExistingObjects() {

        ProductEntity product1 = ProductEntity.builder().name("Lipstick").category(ProductCategory.Beauty).price(2.0f).build();
        ProductEntity product2 = ProductEntity.builder().name("Daycream").category(ProductCategory.Beauty).price(1.5f).build();
        ProductEntity product3 = ProductEntity.builder().name("Dress").category(ProductCategory.Clothing).price(3.0f).build();
        ProductEntity product4 = ProductEntity.builder().name("Shirt").category(ProductCategory.Clothing).price(2.5f).build();

        context.insert(product1);
        context.insert(product2);
        context.insert(product3);
        context.insert(product4);

        OrderEntity order1 = OrderEntity.builder().customerId(100L).status(OrderEntity.OrderStatus.COMPLETED).created(new Date()).build();
        OrderEntity order2 = OrderEntity.builder().customerId(200L).status(OrderEntity.OrderStatus.COMPLETED).created(new Date()).build();

        context.insert(order1);
        context.insert(order2);

        context.insert(OrderLineEntity.builder().orderId(order1.getId()).productId(product1.getId()).build());
        context.insert(OrderLineEntity.builder().orderId(order1.getId()).productId(product2.getId()).build());
        context.insert(OrderLineEntity.builder().orderId(order1.getId()).productId(product3.getId()).build());
        context.insert(OrderLineEntity.builder().orderId(order2.getId()).productId(product2.getId()).build());
        context.insert(OrderLineEntity.builder().orderId(order2.getId()).productId(product3.getId()).build());
        context.insert(OrderLineEntity.builder().orderId(order2.getId()).productId(product4.getId()).build());

        // 1st QUERY: base MainDto rows (order attributes only — nested fields stay empty)
        ResultTable ordersTable = Estivate.selectQuery(OrderEntity.class)
            .in(OrderEntity::getId, Arrays.asList(order1.getId(), order2.getId()))
            .fetch(context);

        List<MainDto> dtos = ordersTable.asList(MainDto.class);

        Assertions.assertEquals(2, dtos.size());
        for (MainDto dto : dtos) {
            Assertions.assertTrue(dto.groupedDtos == null || dto.groupedDtos.isEmpty()
                || dto.groupedDtos.stream().allMatch(java.util.Objects::isNull));
            dto.groupedDtos = null;
            dto.orderDetails = null;
        }

        // 2nd QUERY: nested projection columns, merged into existing MainDto by @NestedBy customerId
        ResultTable nestedTable = Estivate.selectQuery(OrderEntity.class)
            .joinInner(OrderEntity.class, OrderLineEntity.class)
            .joinInner(OrderLineEntity.class, ProductEntity.class)
            .in(OrderEntity::getId, Arrays.asList(order1.getId(), order2.getId()))
            .select(OrderEntity::getCustomerId)
            .select(OrderEntity::getStatus)
            .select(OrderEntity::getCreated)
            .select(ProductEntity::getCategory)
            .selectCount(ProductEntity::getId)
            .selectSum(ProductEntity::getPrice)
            .groupBy(OrderEntity::getCustomerId)
            .groupBy(ProductEntity::getCategory)
            .fetch(context);

        List<MainDto> filledDtos = nestedTable.asList(MainDto.class, dtos);

        Assertions.assertSame(dtos, filledDtos);
        Assertions.assertEquals(2, filledDtos.size());

        MainDto mixedDto1 = filledDtos.stream().filter(d -> d.customerId == 100L).findFirst().orElse(null);
        Assertions.assertEquals(OrderEntity.OrderStatus.COMPLETED, mixedDto1.status);
        Assertions.assertNotNull(mixedDto1.created);
        Assertions.assertEquals(2, mixedDto1.groupedDtos.size());
        Assertions.assertEquals(ProductEntity.ProductCategory.Clothing, mixedDto1.groupedDtos.get(0).category);
        Assertions.assertEquals(1, mixedDto1.groupedDtos.get(0).productCount);
        Assertions.assertEquals(ProductEntity.ProductCategory.Beauty, mixedDto1.groupedDtos.get(1).category);
        Assertions.assertEquals(2, mixedDto1.groupedDtos.get(1).productCount);
        Assertions.assertEquals(3.5f, mixedDto1.groupedDtos.get(1).totalPrice);
        Assertions.assertNotNull(mixedDto1.orderDetails);
        Assertions.assertEquals(OrderEntity.OrderStatus.COMPLETED, mixedDto1.orderDetails.status);

        MainDto mixedDto2 = filledDtos.stream().filter(d -> d.customerId == 200L).findFirst().orElse(null);
        Assertions.assertEquals(2, mixedDto2.groupedDtos.size());
        Assertions.assertNotNull(mixedDto2.orderDetails);
    }

}
