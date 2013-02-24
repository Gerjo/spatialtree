package com.gerardmeier.tree;

public class Counter {
    private int value = 0;

    public int get() {
        return value;
    }

    public Counter increment() {
        ++value;
        return this;
    }

    public Counter restart() {
        value = 0;
        return this;
    }

    public Counter set(int num) {
        value = num;
        return this;
    }

    public Counter decrement() {
        --value;
        return this;
    }

    @Override
    public String toString() {
        return "Counter: " + value;
    }
}
