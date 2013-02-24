spatialtree
===========

A pathfinding aware data structure. The tree is to be queried using a criterion, and will adjust itself accordingly.

Usage excerpt, assumes the "tree" member has been initialized elsewhere, and all entities have been inserted.

```java
public Route findPath(Vector startPos, Vector goalPos) {
    Route path = new Route(Route.State.IMPOSSIBLE);

    Criterion criterion = new Criterion();
    criterion.minimalSize = 10;
    criterion.maximalSize = 20;
    criterion.minimalEntities = 0;
    criterion.maximalEntities = 0;
    criterion.permitDiagonal = false;

    pathfindingGeneration++;

    Space start = tree.getSpaceAt(startPos, criterion);
    Space end   = tree.getSpaceAt(goalPos, criterion);

    if(start == null || end == null) {
        // Start or end location is not available.
        return path;
    }

    PriorityQueue<Space> open = new PriorityQueue<Space>(100, AStarCompare.fn);
    open.add(start);
    start.isInOpen = true;
    start.generation = pathfindingGeneration;

    while(!open.isEmpty()) {
        Space current = open.poll();
        
        if(current == end) {
            path.setState(Route.State.FOUND);
            break;
        }

        // This is where the tree will perform its feats:
        ArrayList<Space> neighbours = tree.getNeighbours(current, criterion);

        for(Space neighbour : neighbours) {
            if(!neighbour.isInOpen || neighbour.generation != pathfindingGeneration) {
                neighbour.generation = pathfindingGeneration;
                neighbour.route = current;
                neighbour.isInOpen = true;
                
                // Note the variable step costs, we're not dealing with uniform cost grids:
                neighbour.g = current.g + Math.abs(current.x - neighbour.x) + Math.abs(current.y - neighbour.y);
                
                // Manhattan distance.
                neighbour.h = Math.abs(goalPos.x - neighbour.x) + Math.abs(goalPos.y - neighbour.y);
                
                // The usual, with a tiebreaker.
                neighbour.f = neighbour.g + neighbour.h * 1.1f;
                
                open.add(neighbour);
            }
        }
    }
    
    Space node   = end;

    while(node != null) {
        Vector c = node.getCenter();
        path.push(c);
        node = node.route;
    }

    return path;
}
```
