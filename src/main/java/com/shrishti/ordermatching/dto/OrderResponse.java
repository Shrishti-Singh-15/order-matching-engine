package com.shrishti.ordermatching.dto;

import java.math.BigDecimal;
import java.util.List;

public class OrderResponse {

    private Long orderId;
    private String symbol;
    private String side;
    private BigDecimal price;
    private long remainingQuantity;
    private List<TradeResponse> trades;

    public OrderResponse(Long orderId, String symbol, String side, BigDecimal price,
                          long remainingQuantity, List<TradeResponse> trades) {
        this.orderId = orderId;
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.remainingQuantity = remainingQuantity;
        this.trades = trades;
    }

    public Long getOrderId() { return orderId; }
    public String getSymbol() { return symbol; }
    public String getSide() { return side; }
    public BigDecimal getPrice() { return price; }
    public long getRemainingQuantity() { return remainingQuantity; }
    public List<TradeResponse> getTrades() { return trades; }
}
