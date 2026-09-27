package com.shrishti.ordermatching.service;

import com.shrishti.ordermatching.dto.*;
import com.shrishti.ordermatching.engine.MatchingEngine;
import com.shrishti.ordermatching.engine.Order;
import com.shrishti.ordermatching.engine.Trade;
import com.shrishti.ordermatching.entity.OrderEntity;
import com.shrishti.ordermatching.entity.TradeEntity;
import com.shrishti.ordermatching.repository.OrderRepository;
import com.shrishti.ordermatching.repository.TradeRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class OrderService {

    private final MatchingEngine matchingEngine;
    private final OrderRepository orderRepository;
    private final TradeRepository tradeRepository;

    public OrderService(MatchingEngine matchingEngine, OrderRepository orderRepository, TradeRepository tradeRepository) {
        this.matchingEngine = matchingEngine;
        this.orderRepository = orderRepository;
        this.tradeRepository = tradeRepository;
    }

    /**
     * Submits an order to the in-memory matching engine, then persists both
     * the resulting order record and any trades it generated in a single
     * transaction so the two never fall out of sync.
     */
    @Transactional
    public OrderResponse submitOrder(OrderRequest request) {
        MatchingEngine.MatchResult result = matchingEngine.submitOrder(
                request.getSymbol(), request.getSide(), request.getPrice(), request.getQuantity());

        Order order = result.order();
        List<Trade> trades = result.trades();

        OrderEntity orderEntity = new OrderEntity(
                order.getSymbol(), order.getSide(), order.getPrice(),
                request.getQuantity(), order.getQuantity());
        orderRepository.save(orderEntity);

        List<TradeResponse> tradeResponses = trades.stream()
                .map(trade -> {
                    TradeEntity entity = new TradeEntity(
                            trade.getSymbol(), trade.getBuyOrderId(), trade.getSellOrderId(),
                            trade.getPrice(), trade.getQuantity());
                    tradeRepository.save(entity);
                    return new TradeResponse(trade.getBuyOrderId(), trade.getSellOrderId(),
                            trade.getPrice(), trade.getQuantity());
                })
                .collect(Collectors.toList());

        return new OrderResponse(
                orderEntity.getId(), order.getSymbol(), order.getSide().name(),
                order.getPrice(), order.getQuantity(), tradeResponses);
    }

    public BookSnapshotResponse getBookSnapshot(String symbol) {
        var book = matchingEngine.getBook(symbol);
        if (book == null) {
            return new BookSnapshotResponse(symbol, null, null, 0);
        }
        return new BookSnapshotResponse(symbol, book.bestBid(), book.bestAsk(), book.bookDepth());
    }
}
