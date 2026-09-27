package com.shrishti.ordermatching.engine;

import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Holds one {@link OrderBook} per symbol in a {@link ConcurrentHashMap} so
 * that orders for different symbols can be matched fully in parallel, while
 * orders for the same symbol are serialized through that symbol's book lock
 * to preserve price-time priority.
 */
@Component
public class MatchingEngine {

    private final ConcurrentHashMap<String, OrderBook> books = new ConcurrentHashMap<>();
    private final AtomicLong orderIdGenerator = new AtomicLong(0);

    public MatchResult submitOrder(String symbol, Order.Side side, BigDecimal price, long quantity) {
        OrderBook book = books.computeIfAbsent(symbol, OrderBook::new);
        Order order = new Order(orderIdGenerator.incrementAndGet(), symbol, side, price, quantity);
        List<Trade> trades = book.submit(order);
        return new MatchResult(order, trades);
    }

    public OrderBook getBook(String symbol) {
        return books.get(symbol);
    }

    /** Result of submitting an order: the order (with its remaining quantity) and any trades it produced. */
    public record MatchResult(Order order, List<Trade> trades) {}
}
