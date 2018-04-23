package com.company.wumpusworld;

public class WumpusPercept {
    private boolean stench;
    private boolean breeze;
    private boolean glitter;
    private boolean bump;
    private boolean scream;

    public WumpusPercept setStench() {
        stench = true;
        return this;
    }

    public WumpusPercept setBreeze() {
        breeze = true;
        return this;
    }

    public WumpusPercept setGlitter() {
        glitter = true;
        return this;
    }

    public WumpusPercept setBump() {
        bump = true;
        return this;
    }

    public WumpusPercept setScream() {
        scream = true;
        return this;
    }

    public boolean isStench() {
        return stench;
    }

    public boolean isBreeze() {
        return breeze;
    }

    public boolean isGlitter() {
        return glitter;
    }

    public boolean isBump() {
        return bump;
    }

    public boolean isScream() {
        return scream;
    }

    @Override
    public String toString() {
        StringBuilder result = new StringBuilder("{");
        if (stench) result.append("Stench, ");
        if (breeze) result.append("Breeze, ");
        if (glitter) result.append("Glitter, ");
        if (bump) result.append("Bump, ");
        if (scream) result.append("Scream, ");
        if (result.length() > 1) result.delete(result.length() - 2, result.length());
        result.append("}");
        return result.toString();
    }
}
