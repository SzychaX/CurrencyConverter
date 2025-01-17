package com.example.currencyconverter;
import java.io.Serializable;

public class CurrencyItem implements Serializable {
    private String currencyCode;
    private String exchangeRate;

    public CurrencyItem(String currencyCode, String exchangeRate) {
        this.currencyCode = currencyCode;
        this.exchangeRate = exchangeRate;
    }

    public String getCurrencyCode() {
        return currencyCode;
    }

    public String getExchangeRate() {
        return exchangeRate;
    }
}
