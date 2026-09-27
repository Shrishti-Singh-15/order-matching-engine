package com.shrishti.ordermatching.entity;

import com.shrishti.ordermatching.engine.Order;
import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "orders")
public class OrderEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Order.Side side;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(nullable = false)
    private long originalQuantity;

    @Column(nullable = false)
    private long remainingQuantity;

    @Column(nullable = false)
    private Instant createdAt = Instant.now();

    protected OrderEntity() {
        // JPA
    }

    public OrderEntity(String symbol, Order.Side side, BigDecimal price, long originalQuantity, long remainingQuantity) {
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.originalQuantity = originalQuantity;
        this.remainingQuantity = remainingQuantity;
    }

    public Long getId() { return id; }
    public String getSymbol() { return symbol; }
    public Order.Side getSide() { return side; }
    public BigDecimal getPrice() { return price; }
    public long getOriginalQuantity() { return originalQuantity; }
    public long getRemainingQuantity() { return remainingQuantity; }
    public Instant getCreatedAt() { return createdAt; }
}
