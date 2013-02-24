package com.gerardmeier.tree;

import com.gerardmeier.tree.visitor.SpaceVisitor;
import com.gerardmeier.tree.geometry.Vector;
import java.util.ArrayList;


public class Tree {
    private Space root = null;
    Counter numSpaces  = new Counter();

    // Default to a quad tree.
    float minScaleRatio = 0.5f;
    float maxScaleRatio = 0.5f;


    public Tree(float width, float height) {
        root = new Space(this, 0, 0, width, height);
    }

    public void insert(Entity entity) {
        root.insert(entity);
    }

    public void insert(Iterable<? extends Entity> collection) {
        // Support bulk inserts:
        for(Entity entity : collection) {
            insert(entity);
        }
    }

    public void clean() {
        root.clean();
    }

    public void reset() {
        numSpaces.restart();

        // Unsure how garbage collection works here.
        root = new Space(this, 0, 0, root.getWidth(), root.getHeight());
    }


    // Visit spaces by a set criterion, great for collision detection.
    public void accept(Criterion criterion, SpaceVisitor visitor) {
        for(Space space : getAllSpaces(criterion)) {
            if(!visitor.visit(space)) {
                // Premature halt, the visitor returned "false". TODO:
                // visit the tree directly rather than via "getSpaceAt".
                return;
            }
        }
    }

    // Visit all available Spaces recursively. Used for debugging, if anything.
    public void accept(SpaceVisitor visitor) {
        root.accept(visitor);
    }

    public ArrayList<Space> getNeighbours(final Space center, final Criterion criterion) {
        final ArrayList<Space> spaces = new ArrayList<Space>();
        root.getNeighbours(spaces, center, criterion);
        return spaces;
    }

    public ArrayList<Space> getAllSpaces(final Criterion criterion) {
        final ArrayList<Space> spaces = new ArrayList<Space>();
        root.getAllSpaces(spaces, criterion);
        return spaces;
    }

    public Space getSpaceAt(final Vector center, final Criterion criterion) {
        return root.getSpaceAt(center, criterion);
    }

    public int getSpaceCount() {
        return numSpaces.get();
    }

    public void setScaleRatios(float minScaleRatio, float maxScaleRatio) {
        this.minScaleRatio = minScaleRatio;
        this.maxScaleRatio = maxScaleRatio;
    }

    public float getwidth() {
        return root.width;
    }

    public float getHeight() {
        return root.height;
    }
}
