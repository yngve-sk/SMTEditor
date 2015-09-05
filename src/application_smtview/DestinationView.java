package application_smtview;

import application_componentview.Components;


/**
 * Represents a destination view
 * @author Yngve Sekse Kristiansen
 *
 */
public class DestinationView extends SMTNodeView {

    public DestinationView(double x, double y, double dimension, int nodeId) {
        super(x, y, dimension, Components.DESTINATION.getImagePath(), nodeId);
    }

}
