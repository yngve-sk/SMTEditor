package model;

public class Destination extends SMTNode{

    public Destination(double x, double y, int id) {
        super(x, y, true, id);
    }

    public Destination(SMTNode data) {
    	super(data, true);
    }
}
