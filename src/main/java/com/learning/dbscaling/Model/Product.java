package com.learning.dbscaling.Model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.ReadOnlyProperty;
import org.springframework.data.mongodb.core.index.CompoundIndex;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

/**
 * Product:
 *
 * 1. B-Tree index on `price` for range queries (e.g., price BETWEEN $10 and $50)
 * 2. Composite index on {category, price} for multi-field queries
 * 3. Covering index — query only indexed fields, skip full-document fetch
 * 4. Text index on `name` + `description` for full-text search
 * 5. TTL index on `lastUpdated` (optional, shown in IndexManager)
 *
 * WHY Serializable? — Required by Redis cache serialization.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "products")
@CompoundIndex(name = "idx_category_price", def = "{'category': 1, 'price': 1}")
// ↑ COMPOSITE INDEX
// Field ordering matters: put the EQUALITY filter first (category),
// then the RANGE/SORT field second (price).
// MongoDB can satisfy "category = X AND price > Y" with a single index scan.
// Reversing the order (price first) would force a full scan of all prices
// before filtering by category.
public class Product implements Serializable {

    @Id
    private String id;

    /** Text-indexed for full-text search (see SearchService) */
    @TextIndexed(weight = 3)
    private String name;

    @TextIndexed
    private String description;

    /**
     * B-TREE INDEX — ideal for range queries.
     *
     * MongoDB uses B-tree structures for its default indexes.
     * A B-tree keeps data sorted, enabling efficient:
     *  - Range scans: price >= 10 AND price <= 50
     *  - Sort operations: ORDER BY price ASC
     *  - Min/Max lookups
     *
     * Without this index → COLLSCAN (full collection scan)
     * With this index → IXSCAN (index scan) — orders of magnitude faster
     */
    @Indexed(name = "idx_price")
    private BigDecimal price;
    @Indexed(name = "idx_category")
    private String category;
    private String brand;
    private List<String> tags;
    private Integer stockQuantity;
    private Instant lastUpdated;
    private Instant createdAt;
    private String sku;
}
