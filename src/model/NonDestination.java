package model;

/**
 * Displays a NonDestination node, extends SMTNode, containing all the node logic.
 * @author Yngve Sekse Kristiansen
 *
 */
public class NonDestination extends SMTNode{

    public NonDestination(double x, double y, int id) {
        super(x, y, false, id);
    }
    
    public NonDestination(SMTNode data) {
    	super(data, false);
    }
}
