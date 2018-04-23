package com.company.wumpusworld;

public enum WumpusAction {

    FORWARD("Forward"), TURN_LEFT("TurnLeft"), TURN_RIGHT("TurnRight"), GRAB("Grab"), SHOOT("Shoot"), CLIMB("Climb");

    private String symbol;

    WumpusAction(String sym) {
        symbol = sym;
    }

    public String getSymbol() {
        return symbol;
    }
}
