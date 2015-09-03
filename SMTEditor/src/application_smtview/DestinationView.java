package application_smtview;

import model.SMTNode;
import application.Components;


/**
 * Represents a destination view
 * @author Yngve Sekse Kristiansen
 *
 */
public class DestinationView extends SMTNodeView {

    public DestinationView(double x, double y, double width, double height, SMTNode data) {
        super(x, y, width, height, Components.DESTINATION.getImagePath(), data);
    }

}
