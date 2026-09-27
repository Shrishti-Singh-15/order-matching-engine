package com.shrishti.ordermatching.dto;

import java.math.BigDecimal;

public class BookSnapshotResponse {

    private String symbol;
    private BigDecimal bestBid;
    private BigDecimal bestAsk;
    private int depth;

    public BookSnapshotResponse(String symbol, BigDecimal bestBid, BigDecimal bestAsk, int depth) {
        this.symbol = symbol;
        this.bestBid = bestBid;
        this.bestAsk = bestAsk;
        this.depth = depth;
    }

    public String getSymbol() { return symbol; }
    public BigDecimal getBestBid() { return bestBid; }
    public BigDecimal getBestAsk() { return bestAsk; }
    public int getDepth() { return depth; }
}
