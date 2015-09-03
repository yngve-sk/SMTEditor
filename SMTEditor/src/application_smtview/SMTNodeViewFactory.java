package application_smtview;

import model.SMTNode;

/**
 * Small factory to produce node views
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTNodeViewFactory {

    static SMTNodeView nodeView(double x, double y, double width, double height, SMTNode data, boolean isDestination) {
        return isDestination ? new DestinationView(x, y, width, height, data) :
                               new NonDestinationView(x, y, width, height, data);
    }
}
