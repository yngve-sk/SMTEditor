package application_smtview;

import model.SMTNode;
import application__componentview.Components;


/**
 * Represents a Non-Destination node
 * @author Yngve Sekse Kristiansen
 *
 */
public class NonDestinationView extends SMTNodeView {

    public NonDestinationView(double x, double y, double width, double height, SMTNode data) {
        super(x, y, width, height, Components.NONDESTINATION.getImagePath(), data);
    }
}
