package com.shrishti.ordermatching.entity;

import jakarta.persistence.*;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "trades")
public class TradeEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String symbol;

    @Column(nullable = false)
    private Long buyOrderId;

    @Column(nullable = false)
    private Long sellOrderId;

    @Column(nullable = false, precision = 19, scale = 4)
    private BigDecimal price;

    @Column(nullable = false)
    private long quantity;

    @Column(nullable = false)
    private Instant executedAt = Instant.now();

    protected TradeEntity() {
        // JPA
    }

    public TradeEntity(String symbol, Long buyOrderId, Long sellOrderId, BigDecimal price, long quantity) {
        this.symbol = symbol;
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getId() { return id; }
    public String getSymbol() { return symbol; }
    public Long getBuyOrderId() { return buyOrderId; }
    public Long getSellOrderId() { return sellOrderId; }
    public BigDecimal getPrice() { return price; }
    public long getQuantity() { return quantity; }
    public Instant getExecutedAt() { return executedAt; }
}
