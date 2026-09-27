package com.shrishti.ordermatching.engine;

import java.math.BigDecimal;

/** Result of two orders matching against each other. */
public class Trade {

    private final long buyOrderId;
    private final long sellOrderId;
    private final String symbol;
    private final BigDecimal price;
    private final long quantity;
    private final long timestampNanos;

    public Trade(long buyOrderId, long sellOrderId, String symbol, BigDecimal price, long quantity) {
        this.buyOrderId = buyOrderId;
        this.sellOrderId = sellOrderId;
        this.symbol = symbol;
        this.price = price;
        this.quantity = quantity;
        this.timestampNanos = System.nanoTime();
    }

    public long getBuyOrderId() { return buyOrderId; }
    public long getSellOrderId() { return sellOrderId; }
    public String getSymbol() { return symbol; }
    public BigDecimal getPrice() { return price; }
    public long getQuantity() { return quantity; }
    public long getTimestampNanos() { return timestampNanos; }
}
