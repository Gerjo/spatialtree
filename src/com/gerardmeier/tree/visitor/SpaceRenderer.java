package com.gerardmeier.tree.visitor;

import com.gerardmeier.tree.Space;
import com.gerardmeier.tree.Space;
import com.gerardmeier.tree.visitor.SpaceVisitor;
import java.awt.Graphics;


public class SpaceRenderer implements SpaceVisitor {
    private Graphics g;

    public SpaceRenderer(Graphics g) {
        this.g = g;
    }

    @Override
    public boolean visit(Space space) {
       
        g.drawRect(
                Math.round(space.x),
                Math.round(space.y),
                Math.round(space.width),
                Math.round(space.height)
        );

        return true;
    }

}
