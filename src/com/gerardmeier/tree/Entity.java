package com.gerardmeier.tree;

import com.gerardmeier.tree.geometry.Rectangle;


public abstract class Entity extends Rectangle {

    public Entity() {
        this(0, 0, 1, 1);
    }

    public Entity(float x, float y) {
        this(x, y, 1, 1);
    }

    public Entity(float x, float y, float width, float height) {
        super(x, y, width, height);
    }

    public abstract int getNumericType();
}
