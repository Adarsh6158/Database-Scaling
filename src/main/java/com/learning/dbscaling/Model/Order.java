package com.learning.dbscaling.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Order document:
 *
 * 1. N+1 query problem: naively loading each order’s products one-by-one
 * 2. Composite index on {customerId, orderDate} for "my recent orders" queries
 * 3. Async processing: heavy order-processing offloaded to Kafka consumer
 * 4. Read/write separation: reads from secondary, writes to primary
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "orders")
@CompoundIndex(
        name = "idx_customer_date",
        def = "{'customerId': 1, 'orderDate': -1}"
)
// customerId = equality match first, orderDate descending for "latest orders"
public class Order implements Serializable {

    @Id
    private String id;
    @Indexed(name = "idx_order_customerId")
    private String customerId;
    private List<OrderItem> items;
    private BigDecimal totalAmount;
    @Indexed(name = "idx_order_status")
    private String status;
    private Instant orderDate;
    private Instant processedAt;
    private String shippingAddress;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OrderItem implements Serializable {

        private String productId;
        private String productName;
        private int quantity;
        private BigDecimal unitPrice;
    }
}
