package com.shrishti.ordermatching.engine;

import org.junit.jupiter.api.Test;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class MatchingEngineTest {

    @Test
    void nonCrossingBuyDoesNotMatch() {
        MatchingEngine engine = new MatchingEngine();
        engine.submitOrder("AAPL", Order.Side.SELL, new BigDecimal("100.00"), 50);

        MatchingEngine.MatchResult result = engine.submitOrder("AAPL", Order.Side.BUY, new BigDecimal("99.00"), 30);

        assertTrue(result.trades().isEmpty());
        assertEquals(30, result.order().getQuantity());
    }

    @Test
    void crossingBuyMatchesAtRestingPrice() {
        MatchingEngine engine = new MatchingEngine();
        engine.submitOrder("AAPL", Order.Side.SELL, new BigDecimal("100.00"), 50);

        MatchingEngine.MatchResult result = engine.submitOrder("AAPL", Order.Side.BUY, new BigDecimal("100.50"), 20);

        assertEquals(1, result.trades().size());
        Trade trade = result.trades().get(0);
        assertEquals(20, trade.getQuantity());
        assertEquals(0, trade.getPrice().compareTo(new BigDecimal("100.00")));
        assertEquals(0, result.order().getQuantity(), "Buy order should be fully filled");
    }

    @Test
    void partialFillLeavesRemainderRestingOnBook() {
        MatchingEngine engine = new MatchingEngine();
        engine.submitOrder("AAPL", Order.Side.SELL, new BigDecimal("100.00"), 30);

        MatchingEngine.MatchResult result = engine.submitOrder("AAPL", Order.Side.BUY, new BigDecimal("100.00"), 50);

        assertEquals(1, result.trades().size());
        assertEquals(30, result.trades().get(0).getQuantity());
        assertEquals(20, result.order().getQuantity(), "20 shares should remain unfilled");
        assertEquals(0, engine.getBook("AAPL").bestBid().compareTo(new BigDecimal("100.00")));
        assertNull(engine.getBook("AAPL").bestAsk(), "Sell side should now be empty");
    }

    @Test
    void priceTimePriorityIsRespectedAtSamePrice() {
        MatchingEngine engine = new MatchingEngine();
        // Two sell orders at the same price; the earlier one should match first.
        MatchingEngine.MatchResult firstSell = engine.submitOrder("AAPL", Order.Side.SELL, new BigDecimal("100.00"), 10);
        engine.submitOrder("AAPL", Order.Side.SELL, new BigDecimal("100.00"), 10);

        MatchingEngine.MatchResult result = engine.submitOrder("AAPL", Order.Side.BUY, new BigDecimal("100.00"), 10);

        assertEquals(1, result.trades().size());
        assertEquals(firstSell.order().getId(), result.trades().get(0).getSellOrderId(),
                "The earlier resting order at the same price should be matched first");
    }

    @Test
    void differentSymbolsAreIndependent() {
        MatchingEngine engine = new MatchingEngine();
        engine.submitOrder("AAPL", Order.Side.SELL, new BigDecimal("100.00"), 10);
        engine.submitOrder("MSFT", Order.Side.SELL, new BigDecimal("200.00"), 10);

        assertEquals(1, engine.getBook("AAPL").bookDepth());
        assertEquals(1, engine.getBook("MSFT").bookDepth());
    }

    @Test
    void concurrentSubmissionsForSameSymbolStayConsistent() throws InterruptedException {
        MatchingEngine engine = new MatchingEngine();
        int threadCount = 10;
        int ordersPerThread = 1000;
        Thread[] threads = new Thread[threadCount];

        // Half the threads submit resting sells, half submit crossing buys.
        for (int t = 0; t < threadCount; t++) {
            final boolean isBuySide = t % 2 == 0;
            threads[t] = new Thread(() -> {
                for (int i = 0; i < ordersPerThread; i++) {
                    if (isBuySide) {
                        engine.submitOrder("AAPL", Order.Side.BUY, new BigDecimal("101.00"), 1);
                    } else {
                        engine.submitOrder("AAPL", Order.Side.SELL, new BigDecimal("100.00"), 1);
                    }
                }
            });
            threads[t].start();
        }
        for (Thread thread : threads) {
            thread.join();
        }

        // Equal buy and sell volume at crossing prices should leave the book empty or
        // near-empty (no exceptions, no lost/duplicated quantity, no deadlock).
        int depth = engine.getBook("AAPL").bookDepth();
        assertTrue(depth <= 1, "Book should be fully or almost fully matched, but depth was " + depth);
    }
}
