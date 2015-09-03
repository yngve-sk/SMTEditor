package model;

public class SMTNodeFactory {
    public static SMTNode newNode(double x, double y, boolean isDestination) {
        return isDestination ? new Destination(x, y) : new NonDestination(x, y);
    }
}
