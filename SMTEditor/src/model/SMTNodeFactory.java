package model;

import java.util.ArrayList;
import java.util.List;

public class SMTNodeFactory {

    public static SMTNode newNode(double x, double y, boolean isDestination) {
        int id = IdTracker.getNewNodeId();
        SMTNode node = isDestination ? new Destination(x, y, id) : new NonDestination(x, y, id);

        // When it's not a parsed node it needs an empty list to start out with
        List<Integer> neighborList = new ArrayList<Integer>();
        node.setNeighbors(neighborList);

        node.checkState();

        return node;
    }
}
