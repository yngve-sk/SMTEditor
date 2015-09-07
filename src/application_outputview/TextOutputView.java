package application_outputview;

import application_componentview.Components;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

public class TextOutputView extends Group {
	
	private VBox left;
	private OutputField avgNodeCost,
					    avgLinkLength,
					    mostExpensiveNode,
					    longestLink,
					    totalTreeCost;
	
	private VBox middle;
	private OutputField numNodes,
						numLinks,
						numDestinations,
						numNonDestinations,
						calculationTime;
	
	private final String genericNodePath = "images/genericnode.png";
	private final String treeExamplePath = "images/tree-example.png";
	private final String clockPath = "images/clock.png";
	private final String dropAreaImagePath = "images/droparea.png";
	
	private ImageView right; // drop area for files
	
	public TextOutputView() {
		left = new VBox();
		avgNodeCost = new OutputField(genericNodePath, "Avg node cost");
		avgLinkLength = new OutputField(Components.LINK.getImagePath(), "Avg link length");
		mostExpensiveNode = new OutputField(genericNodePath, "Most expensive");
		longestLink = new OutputField(Components.LINK.getImagePath(), "Longest link");
		totalTreeCost = new OutputField(treeExamplePath, "Total cost");
		left.getChildren().addAll(avgNodeCost, avgLinkLength, mostExpensiveNode, longestLink, totalTreeCost);
		
		middle = new VBox();
		numNodes = new OutputField(genericNodePath, "#Nodes");
		numLinks = new OutputField(treeExamplePath, "#Links");
		numDestinations = new OutputField(Components.DESTINATION.getImagePath(), "#Dest");
		numNonDestinations = new OutputField(Components.NONDESTINATION.getImagePath(), "#Non-Dest");
		calculationTime = new OutputField(clockPath, "Calc time");
		middle.getChildren().addAll(numNodes, numLinks, numDestinations, numNonDestinations, calculationTime);
		
		right = new ImageView(new Image(dropAreaImagePath));
		
		getChildren().addAll(left, middle, right);
	}
	
	public void resizeRelocate(double x, double y, double width, double height) {
		super.resizeRelocate(x, y, width, height);
		layoutSubviews(width, height);
	}
	
	private void layoutSubviews(double width, double height) {
		double boxWidth = width/3;
		double padding = 3;
		
		double outputFieldWidth = boxWidth - 2*padding;
		double outputFieldHeight = height/8 - 2*padding;
		
		VBox[] bothVBoxes = {left, middle};
		for(VBox v : bothVBoxes) {
			for(Node n : v.getChildren()) {
				OutputField field = (OutputField) n;
				field.resize(outputFieldWidth, outputFieldHeight);
			}
		}
		
		middle.relocate(boxWidth, 0);
		right.relocate(1.8*boxWidth, 0);
		
		right.setFitWidth(boxWidth*0.8);
		right.setFitHeight(height*0.6);
	}
	
	/**
	 * Updates the field
	 * @param field
	 *  	the type of the field. See {@link OutputFields}
	 * @param newValue
	 *  	new value, as string
	 */
	public void updateFieldWithNewValue(OutputFields field, String newValue) {
		fieldWithType(field).setStringValue(newValue);
	}
	
	/**
	 * Maps the enum to a field
	 * @param field
	 *  	the enum type
	 * @return
	 *  	the corresponding field type
	 */
	private OutputField fieldWithType(OutputFields field) {
		switch(field) {
		case AVG_NODE_COST : return avgNodeCost;
		case AVG_LINK_LENGTH : return avgLinkLength;
		case MOST_EXPENSIVE_NODE : return mostExpensiveNode;
		case LONGEST_LINK : return longestLink;
		case TOTAL_TREE_COST : return totalTreeCost;
		case NUM_NODES : return numNodes;
		case NUM_LINKS : return numLinks;
		case NUM_DESTINATIONS : return numDestinations;
		case NUM_NONDESTINATIONS : return numNonDestinations;
		case CALCULATION_TIME : return calculationTime;
		default : return null;
		}
	}
	
	/**
	 * Enum for the different kinds of output fields
	 * @author Yngve Sekse Kristiansen
	 *
	 */
	public enum OutputFields {
		AVG_NODE_COST,
		AVG_LINK_LENGTH,
		MOST_EXPENSIVE_NODE,
		LONGEST_LINK,
		TOTAL_TREE_COST,
		NUM_NODES,
		NUM_LINKS,
		NUM_DESTINATIONS,
		NUM_NONDESTINATIONS,
		CALCULATION_TIME;
	}
}
