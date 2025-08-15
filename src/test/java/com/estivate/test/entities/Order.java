// package com.estivate.test.entities;

// import java.math.BigDecimal;
// import java.util.Date;

// import javax.persistence.EnumType;
// import javax.persistence.Enumerated;

// import com.estivate.Entity.InsertDate;
// import com.estivate.Entity.UpdateDate;
// import com.estivate.Entity.VirtualForeignKey;
// import com.estivate.index.Annotations.IndexColumn;
// import com.estivate.index.Annotations.IndexType;
// import com.estivate.index.Annotations.TableIndex;
// import com.estivate.index.Annotations.TableIndexes;

// import lombok.AllArgsConstructor;
// import lombok.Data;
// import lombok.EqualsAndHashCode;
// import lombok.NoArgsConstructor;
// import lombok.experimental.FieldNameConstants;
// import lombok.experimental.SuperBuilder;

// @Data
// @EqualsAndHashCode(callSuper = false)
// @SuperBuilder
// @NoArgsConstructor
// @AllArgsConstructor
// @FieldNameConstants
// @TableIndexes({
//     @TableIndex(name = "idx_order_user", columns = {@IndexColumn(Order.Fields.userId)}),
//     @TableIndex(name = "idx_order_status", columns = {@IndexColumn(Order.Fields.status)}),
//     @TableIndex(name = "idx_order_created", columns = {@IndexColumn(Order.Fields.createdAt)}),
//     @TableIndex(name = "idx_order_total", columns = {@IndexColumn(Order.Fields.totalAmount)}),
//     @TableIndex(name = "idx_order_user_status", columns = {@IndexColumn(Order.Fields.userId), @IndexColumn(Order.Fields.status)})
// })
// public class Order extends AbstractEntity {
    
//     @VirtualForeignKey(entity = User.class)
//     private Long userId;
    
//     private String orderNumber;
//     private BigDecimal totalAmount;
//     private BigDecimal taxAmount;
//     private BigDecimal shippingAmount;
//     private String shippingAddress;
//     private String billingAddress;
    
//     @Enumerated(EnumType.STRING)
//     private OrderStatus status;
    
//     @Enumerated(EnumType.STRING)
//     private PaymentMethod paymentMethod;
    
//     @InsertDate
//     private Date createdAt;
    
//     @UpdateDate
//     private Date updatedAt;
    
//     private Date processedAt;
//     private Date shippedAt;
//     private Date deliveredAt;
//     private String notes;
    
//     public enum OrderStatus {
//         PENDING, PROCESSING, SHIPPED, DELIVERED, CANCELLED, REFUNDED
//     }
    
//     public enum PaymentMethod {
//         CREDIT_CARD, DEBIT_CARD, PAYPAL, BANK_TRANSFER, CASH
//     }
// } 