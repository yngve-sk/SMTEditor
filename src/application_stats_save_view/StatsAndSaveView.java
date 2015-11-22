package application_stats_save_view;

import application.SMTEditor;
import application_componentview.Components;
import application_stats_save_view.InputView.InputViewType;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

/**
 * Displays output stats, and the save button
 * @author Yngve Sekse Kristiansen
 *
 */
public class StatsAndSaveView extends Group {
	
	private VBox left;
	private StatsOutputField avgNodeCost,
					    avgLinkLength,
					    mostExpensiveNode,
					    longestLink,
					    totalTreeCost;
	
	private VBox middle;
	private StatsOutputField numNodes,
						numLinks,
						numDestinations,
						numNonDestinations,
						calculationTime;
	
	private final String genericNodePath = "images/genericnode.png";
	private final String treeExamplePath = "images/tree-example.png";
	private final String clockPath = "images/clock.png";
	
	private InputView alpha, kappa;
	private Button save; // save
	
	
	public StatsAndSaveView() {
		left = new VBox();
		avgNodeCost = new StatsOutputField(genericNodePath, "Avg node cost");
		avgLinkLength = new StatsOutputField(Components.LINK.getImagePath(), "Avg link length");
		mostExpensiveNode = new StatsOutputField(genericNodePath, "Most expensive");
		longestLink = new StatsOutputField(Components.LINK.getImagePath(), "Longest link");
		totalTreeCost = new StatsOutputField(treeExamplePath, "Total cost");
		left.getChildren().addAll(avgNodeCost, avgLinkLength, mostExpensiveNode, longestLink, totalTreeCost);
		
		middle = new VBox();
		numNodes = new StatsOutputField(genericNodePath, "#Nodes");
		numLinks = new StatsOutputField(treeExamplePath, "#Links");
		numDestinations = new StatsOutputField(Components.DESTINATION.getImagePath(), "#Dest");
		numNonDestinations = new StatsOutputField(Components.NONDESTINATION.getImagePath(), "#Non-Dest");
		calculationTime = new StatsOutputField(clockPath, "Calc time");
		middle.getChildren().addAll(numNodes, numLinks, numDestinations, numNonDestinations, calculationTime);
				
		kappa = new InputView(InputViewType.KAPPA);
		alpha = new InputView(InputViewType.ALPHA);
		save = new Button("Save Tree");
		save.setOnMouseClicked(event -> save());
		
		getChildren().addAll(left, middle, kappa, alpha, save);
	}
	
	private void save() {
		SMTEditor editor = (SMTEditor) getScene();
		editor.saveButtonClicked();
	}

	public void resizeRelocate(double x, double y, double width, double height) {
		super.resizeRelocate(x, y, width, height);
		layoutSubviews(width, height);
	}
	
	private void layoutSubviews(double width, double height) {
		double boxWidth = width*0.4;
		double padding = 3;
		
		double outputFieldWidth = boxWidth - 2*padding;
		double outputFieldHeight = height/8 - 2*padding;
		
		VBox[] bothVBoxes = {left, middle};
		for(VBox v : bothVBoxes) {
			for(Node n : v.getChildren()) {
				StatsOutputField field = (StatsOutputField) n;
				field.resize(outputFieldWidth, outputFieldHeight);
			}
		}
		
		middle.relocate(boxWidth, 0);
		
		double rightX = 1.5*boxWidth;
		double rightWidth = width - 2*boxWidth;
		double rightHeight = height/3;
		
		kappa.resizeRelocate(rightX, 0, rightWidth, rightHeight/2);
		alpha.resizeRelocate(rightX, rightHeight/2, rightWidth, rightHeight/2);
		
		save.resizeRelocate(rightX, rightHeight, rightWidth, rightHeight);
		save.setPrefWidth(rightWidth);
		save.setPrefHeight(rightHeight);
		save.setMinWidth(rightWidth);
		save.setMinHeight(rightHeight);
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
	private StatsOutputField fieldWithType(OutputFields field) {
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