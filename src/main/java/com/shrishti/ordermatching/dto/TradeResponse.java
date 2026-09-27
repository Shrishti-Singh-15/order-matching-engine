package com.shrishti.ordermatching.dto;

import java.math.BigDecimal;

public class TradeResponse {

    private Long buyOrderId;
    private Long sellOrderId;
    private BigDecimal price;
    private long quantity;

    public TradeResponse(Long buyOrderId, Long sellOrderId, BigDecimal price, long quantity) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.price = price;
        this.quantity = quantity;
    }

    public Long getBuyOrderId() { return buyOrderId; }
    public Long getSellOrderId() { return sellOrderId; }
    public BigDecimal getPrice() { return price; }
    public long getQuantity() { return quantity; }
}
