package application_controlsview;

import application.SMTEditor;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.layout.VBox;

public class ControlsView extends Group{

	
	private VBox left;
	private CustomButton clear,
				   removeAllDestinations,
				   removeAllNonDestinations,
				   removeAllLinks;
	
	private VBox right;
	private CustomButton highlightHeaviestLinks,
			       destinationsIntoNonDestinations,
			       nonDestinationsIntoDestinations,
			       reloadCachedTree;
	
	final double p = 5; // padding

    public ControlsView() {
    	super();
    	
    	clear = new CustomButton("Clear");
    	removeAllDestinations = new CustomButton("Remove all\ndestinations");
    	removeAllNonDestinations = new CustomButton("Remove all\nnon-destinations");
    	removeAllLinks = new CustomButton("Remove all\nlinks");
    	
    	highlightHeaviestLinks = new CustomButton("Highlight\n heaviest links");
    	destinationsIntoNonDestinations = new CustomButton("Destinations\n->non-destinations");
    	nonDestinationsIntoDestinations = new CustomButton("Non-destinations\n->destinations");
    	reloadCachedTree = new CustomButton("Reload latest\ntree file");
    	Insets padding = new Insets(5, 5, 5, 5);
    	left = new VBox();
    	left.setPadding(padding);
    	
    	left.getChildren().addAll(clear, removeAllDestinations, removeAllNonDestinations, removeAllLinks);
    	
    	right = new VBox();
    	right.setPadding(padding);
    	
    	
    	right.getChildren().addAll(highlightHeaviestLinks, destinationsIntoNonDestinations, nonDestinationsIntoDestinations, reloadCachedTree);
    	
    	this.getChildren().addAll(left, right);
    	
    	clear.setOnMouseClicked(event -> buttonClicked(Buttons.CLEAR));
    	removeAllDestinations.setOnMouseClicked(event -> buttonClicked(Buttons.REMOVE_DESTINATIONS));
        removeAllNonDestinations.setOnMouseClicked(event -> buttonClicked(Buttons.REMOVE_NONDESTINATIONS)); 
        removeAllLinks.setOnMouseClicked(event -> buttonClicked(Buttons.REMOVE_LINKS));    
        highlightHeaviestLinks.setOnMouseClicked(event -> buttonClicked(Buttons.HIGHLIGHT_HEAVIEST_LINKS)); 
        destinationsIntoNonDestinations.setOnMouseClicked(event -> buttonClicked(Buttons.DESTINATIONS_TO_NONDESTINATIONS));
        nonDestinationsIntoDestinations.setOnMouseClicked(event -> buttonClicked(Buttons.NONDESTINATIONS_TO_DESTINATIONS));
        reloadCachedTree.setOnMouseClicked(event -> buttonClicked(Buttons.RELOAD_CACHED_TREE));
    }
    
    public void resizeRelocate(double x, double y, double width, double height) {
    	super.resizeRelocate(x, y, width, height);
    	layoutSubviews(width, height);
    }
    
    private void layoutSubviews(double width, double height) {
    	double CustomButtonPrefWidth = width*0.27 - 2*p;
    	double CustomButtonPrefHeight = height/6 - 2*p;
    	
    	double CustomButtonMinWidth = CustomButtonPrefWidth/2;
    	double CustomButtonMinHeight = CustomButtonPrefHeight/2;
    	
    	double CustomButtonMaxWidth = width - 2*p;
    	double CustomButtonMaxHeight = height - 2*p;
    
    	CustomButton[] allCustomButtons = {clear, removeAllDestinations, removeAllNonDestinations, removeAllLinks, highlightHeaviestLinks, destinationsIntoNonDestinations, nonDestinationsIntoDestinations, reloadCachedTree};

    	for(CustomButton b : allCustomButtons) {
    		b.setPrefWidth(CustomButtonPrefWidth);
    		b.setPrefHeight(CustomButtonPrefHeight);

    		b.setMaxHeight(CustomButtonMaxHeight);
    		b.setMaxWidth(CustomButtonMaxWidth);

    		b.setMinWidth(CustomButtonMinWidth);
    		b.setMinHeight(CustomButtonMinHeight);
    	}
    	
    	VBox[] allVBoxes = {left, right};
    	
    	for(VBox v : allVBoxes) {
    		v.setPrefWidth(2*CustomButtonPrefWidth + 2*p);
    		v.setPrefHeight(4*CustomButtonPrefHeight + 5*p);

    		v.setMaxWidth(2*CustomButtonMaxWidth + 2*p);
    		v.setMaxHeight(4*CustomButtonMaxHeight + 5*p);
    		
    		v.setMinWidth(2*CustomButtonMinWidth + 2*p);
    		v.setMinHeight(4*CustomButtonMinHeight + 5*p);
    	}
    	
    	left.relocate(0, 0);
    	right.relocate(width/2, 0);

    }
    
    private void buttonClicked(Buttons type) {
    	SMTEditor editor = (SMTEditor) getScene();
    	editor.buttonClicked(type);
    }
    
    public enum Buttons {
    	CLEAR,
    	REMOVE_DESTINATIONS,
    	REMOVE_NONDESTINATIONS,
    	REMOVE_LINKS,
    	HIGHLIGHT_HEAVIEST_LINKS,
    	DESTINATIONS_TO_NONDESTINATIONS,
    	NONDESTINATIONS_TO_DESTINATIONS,
    	RELOAD_CACHED_TREE;
    }
}
