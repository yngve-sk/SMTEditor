package application_componentview;

import javafx.scene.Group;
import javafx.scene.Node;
import application_smtview.SMTView;

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
    private SMTComponent select;

    private SMTView editor;

    /**
     * Initiate a component view, this should only be called once during initialization of the GUI
     */
	public SMTComponentView(SMTView editor) {
	    nonDestination = new SMTComponent(Components.NONDESTINATION);
	    destination = new SMTComponent(Components.DESTINATION);
	    link = new SMTComponent(Components.LINK);
	    select = new SMTComponent(Components.CURSOR);
	    
	    select.select(); // this is the initial selected type

	    this.getChildren().addAll(select, nonDestination, destination, link);
	    this.editor = editor;
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


	/**
	 * Called when an icon was selected, unselects all icons but the source,
	 * ensuring only one item can be selected at a time
	 * @param source
	 */
    public void iconWasSelected(SMTComponent source) {
        SMTComponent[] components = {select, nonDestination, destination, link};

        for(SMTComponent c : components)
            if(c != source)
                c.unselect();

        editor.componentSelectionDidChange(source.getComponentType());
    }
}


