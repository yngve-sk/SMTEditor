package application_smtview;

import javafx.geometry.Point2D;


/**
 * Small factory to produce node views, all constructor calls should be done through this
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTNodeViewFactory {

    static SMTNodeView newNodeView(int id, double x, double y, double dimension, boolean isDestination) {
        return isDestination ? new DestinationView(x, y, dimension, id) :
                               new NonDestinationView(x, y, dimension, id);
    }

    static SMTNodeView newNodeView(int id, Point2D p, double dimension, boolean isDestination) {
        return isDestination ? new DestinationView(p.getX(), p.getY(), dimension, id) :
                               new NonDestinationView(p.getX(), p.getY(), dimension, id);
    }
}
