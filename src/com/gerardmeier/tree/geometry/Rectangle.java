package com.gerardmeier.tree.geometry;


public class Rectangle extends Vector {
    public float width;
    public float height;

    public Rectangle() {
        this(0, 0, 0, 0);
    }

    public Rectangle(float x, float y, float width, float height) {
        super(x, y);
        //position = new Vector(x, y);
        this.width  = width;
        this.height = height;
    }

    public boolean intersects(Vector other) {
        if(width < 0 || height < 0) {
            return false;
        }

        // Logic borrowed from PhantomCPP.
        return
                other.x >= x && other.x <= x + width
                &&
                other.y >= y && other.y <= y + height;

    }

    public Rectangle intersection(Rectangle other) {
        Rectangle r = new Rectangle(
                Math.max(x, other.x),
                Math.max(y, other.y),

                Math.min(x + width, other.x + other.width),
                Math.min(y + height, other.y + other.height)
        );

        r.width  = r.width - r.x;
        r.height = r.height - r.y;
        return r;
    }

    public boolean intersects(Rectangle other) {
        return intersects(other, true);
    }

    public boolean intersects(Rectangle other, boolean permitDiagonal) {
        if(width < 0 || height < 0) {
            return false;
        }

        // Logic borrowed from AWT's Rectangle2D
        double x0 = getX();
        double y0 = getY();
        return (other.getX() + other.getWidth() >= x0 &&
                other.getY() + other.getHeight() >= y0 &&
                other.getX() <= x0 + getWidth() &&
                other.getY() <= y0 + getHeight());
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }

    public float getCenterDistance(Rectangle other) {
        Vector a = other.getCenter();
        Vector b = getCenter();

        return a.distanceTo(b);
    }

    public Vector getCenter() {
        return new Vector(x + width  * 0.5f, y + height * 0.5f);
    }

    @Override
    public Rectangle clone() {
        return new Rectangle(x, y, width, height);
    }

    @Override
    public String toString() {
        return "[Size: x:" + x + ", y:" + y + ", width:" + width + ", height:" + height + "]";
    }

    public void render(java.awt.Graphics g) {
        g.drawRect(Math.round(x), Math.round(y), Math.round(width), Math.round(height));
    }

    public void render(java.awt.Graphics g, boolean isSolid) {
        if(isSolid) {
            g.fillRect(Math.round(x), Math.round(y), Math.round(width), Math.round(height));
        } else {
            render(g);
        }
    }
}
