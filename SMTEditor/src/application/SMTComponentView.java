package application;

import javafx.scene.Group;
import javafx.scene.Node;

/**
 * Displays components used to built a SMT.
 * Components that'll be used are: <p>
 * 1. Nondestination nodes, <p>
 * 2. Destination nodes, <p>
 * 3. Links
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTComponentView extends Group {

    private SMTComponent nonDestination;
    private SMTComponent destination;
    private SMTComponent link;

    /**
     * Initiate a component view, this should only be called once during initialization of the GUI
     */
	public SMTComponentView() {
	    this.nonDestination = new SMTComponent(Components.NONDESTINATION.getImagePath(), Components.NONDESTINATION.getDescription());
	    this.destination = new SMTComponent(Components.DESTINATION.getImagePath(), Components.DESTINATION.getDescription());
	    this.link = new SMTComponent(Components.LINK.getImagePath(), Components.LINK.getDescription());

	    this.getChildren().addAll(nonDestination, destination, link);
	}

	@Override
    public void resizeRelocate(double x, double y, double width, double height) {
	    super.resizeRelocate(x, y, width, height);
	    layoutSubviews(width, height);
	}

	/**
	 * Decorates resizeRelocate so that subviews are resized too
	 * @param width
	 * @param height
	 */
	private void layoutSubviews(double width, double height) {
	    int children = this.getChildren().size();
	    double cellHeight = height/children;

	    int cellNumber = 0;

	    for(Node n : this.getChildren())
	        n.resizeRelocate(0, cellNumber++*cellHeight, width, cellHeight);
	}
}


