package com.shrishti.ordermatching.dto;

import com.shrishti.ordermatching.engine.Order;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;

public class OrderRequest {

    @NotBlank
    private String symbol;

    @NotNull
    private Order.Side side;

    @NotNull
    @DecimalMin(value = "0.0001")
    private BigDecimal price;

    @Min(1)
    private long quantity;

    public String getSymbol() { return symbol; }
    public void setSymbol(String symbol) { this.symbol = symbol; }
    public Order.Side getSide() { return side; }
    public void setSide(Order.Side side) { this.side = side; }
    public BigDecimal getPrice() { return price; }
    public void setPrice(BigDecimal price) { this.price = price; }
    public long getQuantity() { return quantity; }
    public void setQuantity(long quantity) { this.quantity = quantity; }
}
