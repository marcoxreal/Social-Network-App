package com.ubb.event.strategy;

public abstract class BaseStrategy implements Strategy {
    protected double bestTime = Double.POSITIVE_INFINITY;
    protected int[] bestAssign = null;

    public double getBestTime() { return bestTime; }
    public int[] getBestAssign() { return bestAssign; }
}