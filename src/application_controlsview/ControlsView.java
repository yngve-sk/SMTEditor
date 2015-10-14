package application_controlsview;


import application.SMTEditor;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Insets;
import javafx.scene.Group;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.Slider;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.VBox;

public class ControlsView extends Group{

	
	private VBox left;
	private ToggleButton discreteMode;
	private VBox cellSize;
	private Label cellSizeLabel;
	private Slider cellSizeSlider;
	private CheckBox showNodeId;
	private LinkOptionsView linkOptions;
	
	private VBox right;
	private CustomButton clear,
			     reloadCachedTree,
			     removeAllDestinations,
			     removeAllNonDestinations,
			     removeAllLinks;
	
	final ToggleGroup toggleGroup = new ToggleGroup(); // for radio buttons
			       
	
	final double p = 5; // padding

    public ControlsView() {
    	super();
       
    	left = new VBox();
    	discreteMode = new ToggleButton("Enter Discrete Mode");
    	discreteMode.setOnAction(event -> toggleButtonClicked(ToggleButtons.DISCRETE_MODE, discreteMode.isSelected()));

    	cellSize = new VBox();
    	cellSizeLabel = new Label("Discrete Mode Cell Size: 1");
    	cellSizeSlider = new Slider();
    	cellSizeSlider.setMin(1);
    	cellSizeSlider.setMax(100);
    	cellSizeSlider.setMajorTickUnit(1);
    	cellSizeSlider.setSnapToTicks(true);
    	cellSizeSlider.valueProperty().addListener(new ChangeListener<Number>() {
			@Override
			public void changed(ObservableValue<? extends Number> observable, Number oldValue, Number newValue) {
				ControlsView.this.cellSizeDidChange(newValue.intValue());
			}

			});
    	
    	cellSize.getChildren().addAll(cellSizeLabel, cellSizeSlider);
    	
    	linkOptions = new LinkOptionsView();
    	showNodeId = new CheckBox("Show node id");
    	showNodeId.setOnAction(event -> checkBoxClicked(CheckBoxes.SHOW_NODE_ID, showNodeId.isSelected()));
    	
    	left.setSpacing(15);
    	left.getChildren().addAll(discreteMode, cellSize, linkOptions, showNodeId);
    	
    	right = new VBox();
    	right.setPadding(new Insets(5,5,5,5));

    	clear = new CustomButton("Clear");
    	reloadCachedTree = new CustomButton("Reload latest\ntree file");

    	removeAllDestinations = new CustomButton("Remove all\ndestinations");
    	removeAllNonDestinations = new CustomButton("Remove all\nnon-destinations");
    	removeAllLinks = new CustomButton("Remove all\nlinks");
    	
    	right.getChildren().addAll(clear, reloadCachedTree, removeAllDestinations, removeAllNonDestinations, removeAllLinks);
    	
    	this.getChildren().addAll(left, right);
    	
    	clear.setOnMouseClicked(event -> buttonClicked(Buttons.CLEAR));
    	reloadCachedTree.setOnMouseClicked(event -> buttonClicked(Buttons.RELOAD_CACHED_TREE));
    	removeAllDestinations.setOnMouseClicked(event -> buttonClicked(Buttons.REMOVE_DESTINATIONS));
        removeAllNonDestinations.setOnMouseClicked(event -> buttonClicked(Buttons.REMOVE_NONDESTINATIONS)); 
        removeAllLinks.setOnMouseClicked(event -> buttonClicked(Buttons.REMOVE_LINKS));    
    }
    


    protected void cellSizeDidChange(int intValue) {
		String newText = "Discrete mode cell size : " + intValue;
		cellSizeLabel.setText(newText);	
		getEditor().cellSizeDidChange(intValue);
    }



	private SMTEditor getEditor() {
    	SMTEditor editor = (SMTEditor) getScene();
    	return editor;
    }
    
	public void resizeRelocate(double x, double y, double width, double height) {
    	super.resizeRelocate(x, y, width, height);
    	layoutSubviews(width, height);
    }
    
    private void layoutSubviews(double width, double height) {
    	right.relocate(width/2, 0);
    	right.setPrefHeight(height);
    	right.setPrefWidth(width/2);
    	left.resizeRelocate(0, 0, width/2, height);
    	cellSize.setPrefHeight(height*0.05);
    	cellSizeSlider.setPrefSize(width*0.4, height*0.03);
    }
    
    private void buttonClicked(Buttons type) {
    	getEditor().buttonClicked(type);
    }
    
    private void radioButtonClicked(RadioButtons type) {
    	getEditor().radioButtonClicked(type);
    }
    
    private void checkBoxClicked(CheckBoxes type, boolean isSelected) {
    		getEditor().checkBoxClicked(type, isSelected);
    }
    
    private void toggleButtonClicked(ToggleButtons type, boolean isSelected) {
    	if(type == ToggleButtons.DISCRETE_MODE) {
    		discreteMode.setText((discreteMode.isSelected() ? "Exit Discrete Mode" : "Enter Discrete Mode"));
    		getEditor().toggleButtonClicked(type, isSelected);
    	}
    	else {
    		// do nothing
    	}
    }
    
    public enum Buttons {
    	CLEAR,
    	RELOAD_CACHED_TREE,
    	REMOVE_DESTINATIONS,
    	REMOVE_NONDESTINATIONS,
    	REMOVE_LINKS;
    }
    
    public enum ToggleButtons {
    	DISCRETE_MODE;
    }
    
    public enum RadioButtons {
		LINK_LENGTH, LINK_COST, NOTHING;
    	
    }
    
    public enum CheckBoxes {
    	SHOW_NODE_ID;
    }
    
    private class LinkOptionsView extends VBox {
    	
    	private Label showValueOf;
    	private RadioButton linkCost, linkLength, nothing;
    	private final ToggleGroup toggleGroup = new ToggleGroup();
    	
    	public LinkOptionsView() {
    		showValueOf = new Label("Show value of:");
    		    		
    		linkCost = new RadioButton("link cost");
    		linkLength = new RadioButton("link length");
    		nothing = new RadioButton("nothing");

    		
    		linkCost.setToggleGroup(toggleGroup);    		
    		linkLength.setToggleGroup(toggleGroup);
    		nothing.setToggleGroup(toggleGroup);
    		
    		toggleGroup.selectedToggleProperty().addListener(new ChangeListener<Toggle>() {
				@Override
				public void changed(ObservableValue<? extends Toggle> observable, Toggle oldValue, Toggle newValue) {
					toggle(newValue);
				}
			});
    		
    		getChildren().addAll(showValueOf, linkCost, linkLength, nothing);
    	}
    	
    	private void toggle(Toggle newValue) {
			ControlsView.this.radioButtonClicked(getTypeForButton(newValue));;
		}
    	
    	private RadioButtons getTypeForButton(Toggle t) {
    		if(t == linkCost) 
    			return RadioButtons.LINK_COST;
    		else if(t == linkLength)
    			return RadioButtons.LINK_LENGTH;
    		else
    			return RadioButtons.NOTHING;
    	}

		public void resizeRelocate(double x, double y, double width, double height) {
    		super.resizeRelocate(x, y, width, height);
    		layoutSubviews(width, height);
    	}
    	
    	private void layoutSubviews(double width, double height) {    		
    		this.setPrefHeight(height);
    		this.setPrefWidth(width);
    	}
    	
    }
}
