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
		mostExpensiveNode = new OutputField(genericNodePath, "Most expensive node");
		longestLink = new OutputField(Components.LINK.getImagePath(), "Longest link");
		totalTreeCost = new OutputField(treeExamplePath, "Total cost");
		left.getChildren().addAll(avgNodeCost, avgLinkLength, mostExpensiveNode, longestLink, totalTreeCost);
		
		middle = new VBox();
		numNodes = new OutputField(genericNodePath, "#Nodes");
		numLinks = new OutputField(treeExamplePath, "Links");
		numDestinations = new OutputField(Components.DESTINATION.getImagePath(), "#Destinations");
		numNonDestinations = new OutputField(Components.NONDESTINATION.getImagePath(), "#Non-Destinations");
		calculationTime = new OutputField(clockPath, "Calculation time");
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
	
	
}
