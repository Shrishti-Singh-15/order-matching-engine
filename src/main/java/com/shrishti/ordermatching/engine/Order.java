package com.shrishti.ordermatching.engine;

import java.math.BigDecimal;
import java.util.concurrent.atomic.AtomicLong;

/**
 * In-memory representation of an order used by the matching engine.
 * This is distinct from {@link com.shrishti.ordermatching.entity.OrderEntity},
 * which is the persisted record of an order.
 */
public class Order {

    private static final AtomicLong SEQUENCE = new AtomicLong(0);

    public enum Side { BUY, SELL }

    private final long id;
    private final long sequence;
    private final String symbol;
    private final Side side;
    private final BigDecimal price;
    private volatile long quantity;
    private final long timestampNanos;

    public Order(long id, String symbol, Side side, BigDecimal price, long quantity) {
        this.id = id;
        this.sequence = SEQUENCE.incrementAndGet();
        this.symbol = symbol;
        this.side = side;
        this.price = price;
        this.quantity = quantity;
        this.timestampNanos = System.nanoTime();
    }

    public long getId() { return id; }
    public long getSequence() { return sequence; }
    public String getSymbol() { return symbol; }
    public Side getSide() { return side; }
    public BigDecimal getPrice() { return price; }
    public long getQuantity() { return quantity; }
    public void setQuantity(long quantity) { this.quantity = quantity; }
    public long getTimestampNanos() { return timestampNanos; }

    @Override
    public String toString() {
        return String.format("Order{id=%d, symbol=%s, side=%s, price=%s, qty=%d}",
                id, symbol, side, price, quantity);
    }
}
