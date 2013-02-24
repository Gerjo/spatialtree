package com.gerardmeier.tree;

import com.gerardmeier.tree.visitor.SpaceVisitor;
import com.gerardmeier.tree.geometry.Rectangle;
import com.gerardmeier.tree.geometry.Vector;
import java.util.ArrayList;
import java.util.BitSet;

public class Space extends Rectangle {

    // A* logic, embedded into space for simplicity sake.
    public boolean isInOpen = false;
    public Space route = null;
    public float g = 0;
    public float h = 0;
    public float f = 0;
    public int generation = 0;


    // There are the "children".
    private Space[] nodes = null;

    private final Tree tree;
    private boolean hasDisbursed             = false;
    private final ArrayList<Entity> entities = new ArrayList<Entity>();


    private BitSet types = new BitSet();

    private static enum CriterionResponse { DISBURSE, FAIL, PASS, ERROR };

    public Space(Tree tree, float x, float y, float width, float height) {
        super(x, y, width, height);
        this.tree = tree;
        tree.numSpaces.increment();

    }

    private void partition() {
        // We've already split this space. No worries, carry on.
        if(!isLeaf()) {
            return;
        }

        assert nodes == null;

        final float nodeWidth;
        final float nodeHeight;

        if(width > height) {
            nodeWidth  = width * tree.maxScaleRatio;
            nodeHeight = height * tree.minScaleRatio;
        } else {
            nodeWidth  = width * tree.minScaleRatio;
            nodeHeight = height * tree.maxScaleRatio;
        }

        final int xSteps = (int) Math.ceil(width / nodeWidth);
        final int ySteps = (int) Math.ceil(height / nodeHeight);

        //System.out.println("x:" + xSteps + ", y:" + ySteps);

        nodes = new Space[xSteps * ySteps];

        short i = 0;
        for(short xIndex = 0; xIndex < xSteps; ++xIndex) {
            for(short yIndex = 0; yIndex < ySteps; ++yIndex, ++i) {
                float xOffset = x + xIndex * nodeWidth;
                float yOffset = y + yIndex * nodeHeight;

                nodes[i] = new Space(tree, xOffset, yOffset, nodeWidth , nodeHeight);
                //System.out.println("node created: " + nodes[i]);
            }
        }
    }

    private void disburse() {
        if(hasDisbursed) {
            return;
        }

        hasDisbursed = true;

        // Break this space into two spaces:
        partition();

        // We were enable to break this space into furter partitions, but
        // that's OK, just halt here.
        if(isLeaf()) {
            return;
        }

        // Insert al children in the newly created spaces:
        for(Entity entity : entities) {
            for(Space space : nodes) {
                space.insert(entity);
            }
        }
    }

    public final void insert(Entity entity) {
        if(intersects(entity)) {
            types.set(entity.getNumericType());
            entities.add(entity);
        }
    }

    public final boolean isLeaf()  {
        return nodes == null;
    }

    // Visitor pattern.
    public final void accept(SpaceVisitor visitor) {
        // The return type is captured, this gives the visitor a change
        // to halt any further recursion in this space.
        boolean proceed = visitor.visit(this);

        if(proceed && !isLeaf()) {
            for(Space space : nodes) {
                space.accept(visitor);
            }
        }
    }

    public final void clean() {
        // If this space has no entities, the left and right spaces will
        // be empty, too. Preemptive halt.
        if(entities.isEmpty()) {
            return;
        }

        types.clear();
        entities.clear();

         // If there are no left and right spaces, halt.
        if(!isLeaf() && hasDisbursed) {
            // Recurse into the deep:
            for(Space space : nodes) {
                space.clean();
            }
        }

        hasDisbursed = false;
    }

    private CriterionResponse getCriterionReponse(final Criterion heuristic) {
        // Check if the entity quantity criterea is met:
        if(numEntities() >= heuristic.minimalEntities && numEntities() <= heuristic.maximalEntities) {
            if(width >= heuristic.minimalSize && width <= heuristic.maximalSize) {
                // good numEntities and width are in range.

                return CriterionResponse.PASS;
            } else if(width <= heuristic.minimalSize) {
                // numEntities is in range, but width is too much. However we cannot
                // disburse a small space. Deal with it:
                return CriterionResponse.FAIL;

            } else if(width <= 1) {
                System.out.println("Cannot partition width <= 1");
                assert false;
                return CriterionResponse.ERROR;

            } else {
                //System.out.println("Disburse: " + toString());
                return CriterionResponse.DISBURSE;
            }

        // Too many entities:
        } else if(numEntities() > heuristic.minimalEntities) {
            // We cannot subdivide "1". Spaces assume a pixel unit-type, there
            // is no such thing as a "0.5" pixel. For other units (seen with OpenGL)
            // this check may or may not make sense. For OpenGL, "1" could mean
            // "100 physical pixels".
            if(width <= 1) {
                if(numEntities() > heuristic.maximalEntities) {
                    return CriterionResponse.FAIL;
                }

                // Cannot shrink. Deal with it.
                return CriterionResponse.PASS;
            } else {
                if(width > heuristic.minimalSize) {
                    return CriterionResponse.DISBURSE;
                } else {
                    return CriterionResponse.FAIL;
                }
            }

        // Too few entities, the heuristic doen't care - ignore this space.
        } else {
            return CriterionResponse.FAIL;
        }
    }

    public void getNeighbours(final ArrayList<Space> spaces, final Space center, final Criterion criterion) {
        if(intersects(center)) {


            if(!criterion.permitDiagonal) {
                final float epsilon = 0.01f;
                Rectangle i = intersection(center);
                if(i.width < epsilon && i.height < epsilon) {
                    return;
                }
            }

            switch(getCriterionReponse(criterion)) {
                case PASS:
                    if(this != center) {
                        spaces.add(this);
                    }
                    break;
                case DISBURSE:
                    disburse();
                    assert !isLeaf();

                    for(Space space : nodes) {
                        space.getNeighbours(spaces, center, criterion);
                    }

                    break;
            }
        }
    }

    public Space getSpaceAt(final Vector center, final Criterion criterion) {
        if(intersects(center)) {
            switch(getCriterionReponse(criterion)) {
                case PASS:
                    return this;

                case DISBURSE:
                    disburse();
                    assert !isLeaf();

                    for(Space space : nodes) {
                        Space at = space.getSpaceAt(center, criterion);
                        if(at != null) {
                            return at;
                        }
                    }

                    return null;
            }
        }

        return null;
    }

    public void getAllSpaces(final ArrayList<Space> spaces, final Criterion criterion) {
        switch(getCriterionReponse(criterion)) {
            case PASS:
                spaces.add(this);
                break;
            case DISBURSE:
                disburse();
                assert !isLeaf();

                for(Space space : nodes) {
                    space.getAllSpaces(spaces, criterion);
                }
                break;
        }
    }

    public ArrayList<Entity> getEntities() {
        return entities;
    }


    @Override
    public final String toString() {
        return "Space: [x: " + x + ", y: " + y + ", width: " + width + ", height: " + height + "]";
    }

    public int numEntities() {
        return entities.size();
    }
}
