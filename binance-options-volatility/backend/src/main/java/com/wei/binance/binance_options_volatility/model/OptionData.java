package com.wei.binance.binance_options_volatility.model;

public class OptionData {
    private double strikePrice;
    private double volatility;

    public OptionData(double strikePrice, double volatility) {
        this.strikePrice = strikePrice;
        this.volatility = volatility;
    }

    public double getStrikePrice() {
        return strikePrice;
    }

    public void setStrikePrice(double strikePrice) {
        this.strikePrice = strikePrice;
    }

    public double getVolatility() {
        return volatility;
    }

    public void setVolatility(double volatility) {
        this.volatility = volatility;
    }
}
