package com.gerardmeier.tree;

import java.util.Comparator;


public class AStarCompare implements Comparator<Space> {
    public static final AStarCompare fn = new AStarCompare();
    
    @Override
    public int compare(Space n1, Space n2) {
        if (n1.f > n2.f) {
            return 1;

        } else if (n1.f < n2.f) {

            return -1;
        }

        return 0;
    }

}
