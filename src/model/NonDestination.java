package model;

public class NonDestination extends SMTNode{

    public NonDestination(double x, double y, int id) {
        super(x, y, false, id);
    }
    
    public NonDestination(SMTNode data) {
    	super(data, false);
    }
}
