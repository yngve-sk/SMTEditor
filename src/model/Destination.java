package model;

/**
 * Displays a Destination node, extends SMTNode, containing all the node logic.
 * @author Yngve Sekse Kristiansen
 *
 */
public class Destination extends SMTNode{

    public Destination(double x, double y, int id) {
        super(x, y, true, id);
    }

    public Destination(SMTNode data) {
    	super(data, true);
    }
}
