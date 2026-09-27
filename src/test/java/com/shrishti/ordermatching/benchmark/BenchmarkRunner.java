package com.shrishti.ordermatching.benchmark;

import com.shrishti.ordermatching.engine.MatchingEngine;
import com.shrishti.ordermatching.engine.Order;

import java.math.BigDecimal;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Standalone benchmark for the matching engine's core hot path (no Spring
 * context, no persistence) so the numbers reflect the matching algorithm
 * itself. Not a JUnit test -- run it directly:
 *
 *   mvn test-compile
 *   java -cp target/classes:target/test-classes com.shrishti.ordermatching.benchmark.BenchmarkRunner
 *
 * Results depend heavily on the machine it's run on; treat these as relative,
 * repeatable numbers rather than an absolute production SLA.
 */
public class BenchmarkRunner {

    private static final String[] SYMBOLS = {"AAPL", "MSFT", "GOOG"};

    public static void main(String[] args) {
        runSingleThreaded();
        runMultiThreaded();
    }

    private static void runSingleThreaded() {
        MatchingEngine engine = new MatchingEngine();
        Random rnd = new Random(42);
        int warmup = 50_000;
        int iterations = 500_000;

        for (int i = 0; i < warmup; i++) {
            submitRandomOrder(engine, rnd, i);
        }

        long start = System.nanoTime();
        for (int i = 0; i < iterations; i++) {
            submitRandomOrder(engine, rnd, i);
        }
        long elapsed = System.nanoTime() - start;

        double avgMicros = (elapsed / 1000.0) / iterations;
        System.out.printf("[Single-threaded] %,d orders in %.2f ms  (avg %.3f microseconds/order)%n",
                iterations, elapsed / 1_000_000.0, avgMicros);
    }

    private static void runMultiThreaded() {
        MatchingEngine engine = new MatchingEngine();
        int threadCount = 8;
        int perThread = 100_000;
        Thread[] threads = new Thread[threadCount];
        AtomicLong totalOps = new AtomicLong();

        long start = System.nanoTime();
        for (int t = 0; t < threadCount; t++) {
            final int seed = t;
            threads[t] = new Thread(() -> {
                Random rnd = new Random(seed);
                for (int i = 0; i < perThread; i++) {
                    submitRandomOrder(engine, rnd, i);
                    totalOps.incrementAndGet();
                }
            });
            threads[t].start();
        }
        for (Thread thread : threads) {
            try {
                thread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
        long elapsed = System.nanoTime() - start;

        long ops = totalOps.get();
        double avgMicros = (elapsed / 1000.0) / ops;
        double throughput = ops / (elapsed / 1_000_000_000.0);
        System.out.printf("[Multi-threaded, %d threads] %,d orders in %.2f ms  (avg %.3f microseconds/order, %,.0f orders/sec)%n",
                threadCount, ops, elapsed / 1_000_000.0, avgMicros, throughput);
    }

    private static void submitRandomOrder(MatchingEngine engine, Random rnd, int i) {
        String symbol = SYMBOLS[i % SYMBOLS.length];
        Order.Side side = rnd.nextBoolean() ? Order.Side.BUY : Order.Side.SELL;
        BigDecimal price = BigDecimal.valueOf(100 + rnd.nextInt(20)).setScale(2);
        long qty = 1 + rnd.nextInt(100);
        engine.submitOrder(symbol, side, price, qty);
    }
}
