package com.gerardmeier.tree.geometry;

import java.util.Random;


public class Vector {
    public static Vector Zero = new Vector(0, 0);

    public static Vector CreateRandom(float min, float max) {
        Random random = new Random();

        return new Vector(
                min + random.nextFloat() * (max - min),
                min + random.nextFloat() * (max - min)
        );
    }

    public float x;
    public float y;

    public Vector() {
        this(0, 0);
    }

    public Vector(float x, float y) {
        this.x = x;
        this.y = y;
    }

    public Vector reverse() {
        x *= -1;
        y *= -1;

        return this;
    }

    public Vector scale(double scale) {
        return scale((float)scale);
    }

    public Vector scale(float scale) {
        x *= scale;
        y *= scale;
        return this;
    }

    public float dot(Vector b) {
        return x * b.x + y * b.y;
    }

    public float distanceTo(Vector other) {
        return (float) Math.sqrt(Math.pow(other.x - x, 2) + Math.pow(other.y - y, 2));
    }

    public Vector normalize() {
        float len = (float) Math.sqrt(x * x + y * y);

        if(len != 0) {
            x /= len;
            y /= len;
        }

        return this;
    }

    @Override
    public String toString() {
        return "[Vector: x:" + x + ", y:" + y + "]";
    }

    @Override
    public Vector clone() {
        return new Vector(x, y);
    }
}
