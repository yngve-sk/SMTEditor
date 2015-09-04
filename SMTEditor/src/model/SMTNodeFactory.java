package model;

import java.util.ArrayList;
import java.util.List;

public class SMTNodeFactory {
    public static SMTNode newNode(double x, double y, boolean isDestination) {
        SMTNode node = isDestination ? new Destination(x, y) : new NonDestination(x, y);

        // When it's not a parsed node it needs an empty list to start out with
        List<SMTNode> neighborList = new ArrayList<SMTNode>();
        node.setNeighbors(neighborList);

        node.checkState();

        return node;
    }
}
