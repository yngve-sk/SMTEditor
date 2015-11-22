package model;

import java.util.ArrayList;
import java.util.List;

/**
 * Small factory class for SMTNodes, synchronizing the id's with the IdTracker class.
 * @author Yngve Sekse Kristiansen
 *
 */
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
    
    /**
     * Makes an exact copy of node, but with the opposite type. I.e
     * destination becomes nondestination and vice versa.
     * @param n
     * @param isDestination
     * @return
     *  	the copy of the node
     */
    public static SMTNode transformNode(SMTNode n) {
    	return !n.isDestination ? new Destination(n) : new NonDestination(n);
    }
}
