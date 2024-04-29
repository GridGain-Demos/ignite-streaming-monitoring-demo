package org.gridgain.demo;

import java.sql.Timestamp;

public class Trade {
    private String symbol;

    private int order_quantity;

    private double bid_price;

    private String trade_type;

    private Timestamp order_date;

    public Trade(String symbol, int order_quantity, double bid_price, String trade_type, Timestamp order_date) {
        this.symbol = symbol;
        this.order_quantity = order_quantity;
        this.bid_price = bid_price;
        this.trade_type = trade_type;
        this.order_date = order_date;
    }

    public String getSymbol() {
        return symbol;
    }

    public int getOrderQuantity() {
        return order_quantity;
    }

    public void setOrder_quantity(int order_quantity) {
        this.order_quantity = order_quantity;
    }

    public double getBidPrice() {
        return bid_price;
    }

    public String getTradeType() {
        return trade_type;
    }

    public Timestamp getOrderDate() {
        return order_date;
    }
}

