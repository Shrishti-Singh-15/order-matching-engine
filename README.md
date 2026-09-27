# High-Performance Order Matching Engine

A concurrent order matching engine built with Java and Spring Boot. It matches
buy and sell orders by price-time priority using per-symbol priority queues,
exposes the engine through a REST API, and persists orders and trades via
Spring Data JPA.

## Tech Stack
Java 17, Spring Boot, Spring Data JPA, PostgreSQL (H2 for local/dev runs),
REST API, `java.util.concurrent` (`PriorityQueue` + `ReentrantLock`,
`ConcurrentHashMap`), JUnit 5.

## How it works

- Each traded symbol gets its own `OrderBook`, holding two `PriorityQueue<Order>`
  instances — one for buys (highest price first, then earliest order at that
  price) and one for sells (lowest price first, then earliest order at that
  price). This gives standard price-time priority matching.
- A `ReentrantLock` per `OrderBook` guards the matching loop so that
  concurrent submissions for the *same* symbol are serialized and priority is
  preserved, while orders for *different* symbols go through different locks
  and can be matched fully in parallel.
- `MatchingEngine` holds one `OrderBook` per symbol in a `ConcurrentHashMap`
  and assigns each incoming order a unique id and sequence number.
- `OrderService` calls the engine, then persists the resulting order and any
  trades in a single `@Transactional` block so the two never fall out of sync.

## Running it

### Quick start (H2, no setup required)
```bash
mvn spring-boot:run
```
The app starts on `http://localhost:8080` using an in-memory H2 database
(console at `/h2-console`). Good for trying the API immediately.

### With PostgreSQL
```bash
docker-compose up -d
mvn spring-boot:run -Dspring-boot.run.profiles=postgres
```

### Submit an order
```bash
curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"symbol": "AAPL", "side": "SELL", "price": 100.00, "quantity": 50}'

curl -X POST http://localhost:8080/api/orders \
  -H "Content-Type: application/json" \
  -d '{"symbol": "AAPL", "side": "BUY", "price": 100.50, "quantity": 20}'
```
The second call crosses the resting sell order and returns the resulting
trade(s) in the response body.

### View the order book
```bash
curl http://localhost:8080/api/orders/book/AAPL
```

## Tests
```bash
mvn test
```
Covers non-crossing orders, crossing matches, partial fills, price-time
priority at equal prices, per-symbol independence, and a concurrent
stress test (10 threads submitting 1,000 orders each against one symbol,
asserting the book ends up consistent with no lost or duplicated quantity).

## Benchmark

A standalone benchmark (no Spring context, no persistence — just the
matching algorithm itself) is included at
`src/test/java/.../benchmark/BenchmarkRunner.java`:

```bash
mvn test-compile
java -cp target/classes:target/test-classes com.shrishti.ordermatching.benchmark.BenchmarkRunner
```

Measured results on a local development machine (results will vary by
hardware — treat these as relative/repeatable numbers, not a production SLA):

| Scenario | Orders | Total time | Avg latency/order | Throughput |
|---|---|---|---|---|
| Single-threaded | 500,000 | 577 ms | ~1.15 microseconds (sub-millisecond) | ~866K orders/sec |
| 8 threads, 3 symbols | 800,000 | 413 ms | ~0.52 microseconds | ~1.94M orders/sec |

These numbers come from the in-memory matching hot path only (order
submission → priority queue matching), run with a JIT warm-up phase before
timing. They don't include HTTP or database overhead, which is normal for
this kind of microbenchmark — the point is to show the matching algorithm
itself operates well under a millisecond per order, and that per-symbol
locking lets throughput scale with concurrent load rather than collapsing
under contention.

## Project structure
```
src/main/java/com/shrishti/ordermatching/
  engine/       Core matching logic (Order, Trade, OrderBook, MatchingEngine) - no framework dependencies
  entity/       JPA entities (OrderEntity, TradeEntity)
  repository/   Spring Data JPA repositories
  dto/          Request/response objects for the REST API
  service/      Orchestrates engine calls + persistence in a transaction
  controller/   REST endpoints
src/test/java/com/shrishti/ordermatching/
  engine/       JUnit 5 tests for matching correctness and concurrency
  benchmark/    Standalone latency/throughput benchmark
```

## Notes / possible extensions
- Order cancellation and order status lookup endpoints are natural next steps.
- The engine currently matches limit orders only (no market orders, no
  stop orders).
- Trade persistence happens synchronously in the request path; under very
  high load this would typically move to an async/queued write pattern.
