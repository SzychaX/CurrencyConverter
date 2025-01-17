package com.example.currencyconverter;

public class Currency {
    private String name;
    private double rate;
    private String capital;

    public Currency(String name, double rate, String capital) {
        this.name = name;
        this.rate = rate;
        this.capital = capital;
    }

    public String getName() {
        return name;
    }

    public double getRate() {
        return rate;
    }

    public String getCapital() {
        return capital;
    }
}
