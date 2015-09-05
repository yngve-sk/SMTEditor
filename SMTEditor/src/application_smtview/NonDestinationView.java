package application_smtview;

import application_componentview.Components;


/**
 * Represents a Non-Destination node
 * @author Yngve Sekse Kristiansen
 *
 */
public class NonDestinationView extends SMTNodeView {

    public NonDestinationView(double x, double y, double dimension, int nodeId) {
        super(x, y, dimension, Components.NONDESTINATION.getImagePath(), nodeId);
    }
}
