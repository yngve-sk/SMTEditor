package application;

import java.io.File;

import application_componentview.SMTComponentView;
import application_controlsview.ControlsView;
import application_controlsview.CustomButton;
import application_controlsview.ControlsView.Buttons;
import application_controlsview.ControlsView.CheckBoxes;
import application_controlsview.ControlsView.RadioButtons;
import application_controlsview.ControlsView.ToggleButtons;
import application_outputview.InputView.InputViewType;
import application_outputview.InputView.NumericTextField;
import application_outputview.InputView;
import application_outputview.TextOutputView;
import application_outputview.TextOutputView.OutputFields;
import application_smtview.SMTView;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ContentDisplay;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;
import javafx.scene.text.TextAlignment;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import model.SMTParser;
import model.SharedMulticastTree;

@SuppressWarnings("unused")

public class SMTEditor extends Scene {

    private SMTView editor; // displays the tree itself
    private SMTComponentView components; // displays components that can be dragged into editor
    private TextOutputView output; // displays stats for tree
    private ControlsView buttons; // displays buttons
    
    private Slider referenceNodeDimension;
    private Label rNodeDimensionLabel;

    private BoundedNumericTextField referenceDimension;
    private CustomButton setReferenceDimension;
    private Label rDimensionLabel;
    
    private Label coordinateOutput;
    
    /* Layout in percentages, going through width-wise first */


    private final double setReferenceDimensionLabelWidthRatio = 0.06, 
			    		 setReferenceDimensionFieldWidthRatio = 0.14, 
			    		 setReferenceDimensionButtonWidthRatio = 0.10;
    
	private final double nodeDimensionInputWidthRatio = 0.20;
    private final double nodeDimensionSliderLabelWidthRatio = 0.10; // 10% of the slider space is occupied by the label
    
    private final double coordinateOutputWidthRatio = 0.20;
    
    // Content width
    private final double horizontalEdgePaddingRatio = 0.04;
    private final double contentWidthRatio = 1 - 2*horizontalEdgePaddingRatio;

    // Content height
    private final double verticalEdgePaddingRatio = 0.04;
    private final double contentHeightRatio = 1 - 2*verticalEdgePaddingRatio;

    // Editor width and height
    private final double editorWidthRatio = 0.80;
    private final double editorHeightRatio = 0.60;

    // Internal paddings
    private final double horizontalPaddingBetweenEditorAndComponentsRatio = 0.04;
    private final double horizontalPaddingBetweenButtonsAndOutputRatio = 0.05;

    private final double verticalInternalPaddingRatio = 0.04; // There is only one vertical internal padding

    // Components view width and height
    private final double componentsWidthRatio = 1 - editorWidthRatio - horizontalPaddingBetweenEditorAndComponentsRatio - 2*horizontalEdgePaddingRatio;
    private final double componentsHeightRatio = editorHeightRatio; // same height but written for clarity/future modification

    // Buttons view width and height
    private final double buttonsViewWidthRatio = 0.30;
    private final double buttonsViewHeightRatio = 1 - editorHeightRatio - verticalEdgePaddingRatio;

    // Output view width and height
    private final double outputViewWidthRatio = 1 - buttonsViewWidthRatio - horizontalPaddingBetweenButtonsAndOutputRatio;
    private final double outputViewHeightRatio = buttonsViewHeightRatio; // might be modified later


    private Slider zoom; /* Modifies the degree of zoom within the scroll pane */
    private Label zoomLabel; /* Shows degree of zoom */
    
    private Label alpha, kappa; // Displays alpha/kappa
    
    private final double zoomXRatio = 0.60; /* Ratio is relative to the scroll pane size, not the SMTEditor itself */
    private final double zoomWidthRatio = 0.30; /* Ratio is relative to the scroll pane size, not the SMTEditor itself */
    private final double zoomYRatio = 0.08; /* Ratio is relative to the scroll pane size, not the SMTEditor itself */
    private final double zoomHeight = 30;
    
    private final double alphaKappaXRatio = 0.05;
    private final double alphaKappaYRatio = 0.15;
    private final double alphaKappaHeightRatio = 0.08;
   
    private Stage stage;
    private double width, height;

    public SMTEditor(Group root, double width, double height, Stage primaryStage) {
        super(root, width, height);
        System.out.println("New SMTEditor, width = " + width + ", height = " + height);
        editor = new SMTView();
        components = new SMTComponentView(editor);
        buttons = new ControlsView();
        output = new TextOutputView();

        zoom = new Slider();
        zoom.setMin(10);
        zoom.setMax(200);
        zoom.setValue(100);

        zoomLabel = new Label("100%");
        zoomLabel.setAlignment(Pos.BASELINE_CENTER);
        zoomLabel.setTextAlignment(TextAlignment.CENTER);
        
        zoom.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number old, Number newValue) {
                    SMTEditor.this.zoomDidChange(newValue.intValue());
                }
            });
        
        alpha = new Label(InputViewType.ALPHA.getString() + " = " + SharedMulticastTree.ALPHA_DEFAULT);
        kappa = new Label(InputViewType.KAPPA.getString() + " = " + SharedMulticastTree.KAPPA_DEFAULT);
        
        alpha.setStyle("-fx-font: 24 arial;");
        kappa.setStyle("-fx-font: 24 arial;");

        referenceDimension = new BoundedNumericTextField(DefaultValues.REFERENCE_MIN_DIMENSION,   
												         DefaultValues.DEFAULT_REFERENCE_DIMENSION,   
												         DefaultValues.REFERENCE_MAX_DIMENSION,
												         "enter dimension");
        setReferenceDimension = new CustomButton("Set Dimension");
        setReferenceDimension.setOnAction(event -> setReferenceDimension());
        setReferenceDimension.setTooltip(new Tooltip("WARNING: It's possible "
        		+ "to modify reference dimension during creation, but it should "
        		+ "be done before. Nodes moving out of bounds due to a decrease/increase "
        		+ "in dimension will be removed from the tree."));
        rDimensionLabel = new Label("Reference Dimension:");
        rDimensionLabel.setAlignment(Pos.BASELINE_CENTER);
        rDimensionLabel.setContentDisplay(ContentDisplay.CENTER);
        
        referenceNodeDimension = new Slider();         
        rNodeDimensionLabel = new Label("Ref. node dimension: " + DefaultValues.DEFAULT_REFERENCE_NODE_DIMENSION);
        
	    referenceNodeDimension.setMin(DefaultValues.DEFAULT_REFERENCE_NODE_MIN_DIMENSION);
	    referenceNodeDimension.setMax(DefaultValues.DEFAULT_REFERENCE_NODE_MAX_DIMENSION);
        referenceNodeDimension.setValue(  DefaultValues.DEFAULT_REFERENCE_NODE_DIMENSION);

        coordinateOutput = new Label("cursor: (x, y)");
        
        referenceNodeDimension.valueProperty().addListener(new ChangeListener<Number>() {
            @Override
            public void changed(ObservableValue<? extends Number> ov,
                    Number old, Number newValue) {
                    SMTEditor.this.referenceNodeDimensionDidChange(newValue.intValue());
                }
            });

        layoutSubviews(width, height); // layout logic separated for autoresizing behavior
        
        this.stage = primaryStage;
        this.stage.widthProperty().addListener(new ChangeListener<Number>() {
		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneWidth, Number newSceneWidth) {
		        System.out.println("Width: " + newSceneWidth);
	        	SMTEditor.this.width = newSceneWidth.doubleValue();
		    	SMTEditor.this.layoutSubviews();
		    }
		});

		this.stage.heightProperty().addListener(new ChangeListener<Number>() {
		    @Override public void changed(ObservableValue<? extends Number> observableValue, Number oldSceneHeight, Number newSceneHeight) {
		    	System.out.println("Height: " + newSceneHeight);
		    	SMTEditor.this.height = newSceneHeight.doubleValue();
		    	SMTEditor.this.layoutSubviews();
		    }
		});
		
		this.stage.setMinHeight(800);
		this.stage.setMinWidth(1200);

        this.setOnDragOver(new EventHandler<DragEvent>() {
            @Override
            public void handle(DragEvent event) {
                Dragboard db = event.getDragboard();
                if (db.hasFiles()) {
                    event.acceptTransferModes(TransferMode.COPY);
                } else {
                    event.consume();
                }
            }
        });
        
		this.setOnDragDropped(event -> drop(event));

		this.setFill(Color.BEIGE);

        root.getChildren().addAll(editor, components, buttons, output, 
        		kappa, alpha, zoom, zoomLabel, 
        		referenceDimension, setReferenceDimension, rDimensionLabel, 
        		referenceNodeDimension, rNodeDimensionLabel/*,
        		coordinateOutput*/);
    }
    
	private void setReferenceDimension() {
		this.referenceDimensionDidChange(referenceDimension.getCurrentValue());
	}

	private void referenceDimensionDidChange(int newReferenceDimension) {
	//	rDimensionLabel.setText("Ref. Dimension: " + newReferenceDimension);
		referenceNodeDimension.setMax(newReferenceDimension/10);
		referenceNodeDimension.setMin(newReferenceDimension/50);
		editor.getContentView().referenceDimensionDidChange(newReferenceDimension);
	}

	private void referenceNodeDimensionDidChange(int newReferenceNodeDimension) {
		rNodeDimensionLabel.setText("Ref. node dimension: " + newReferenceNodeDimension);
		editor.getContentView().referenceNodeDimensionDidChange(newReferenceNodeDimension);
}

	private void drop(DragEvent event) {
		Dragboard db = event.getDragboard();
		
		boolean dropCompleted;
		
		if(db.getFiles().size() != 1)
			dropCompleted = false;
		else
			dropCompleted = true;
		
		File droppedFile = db.getFiles().get(0);
		fileWasDropped(droppedFile);
		
		event.setDropCompleted(dropCompleted);
        event.consume();

	}

    protected void layoutSubviews() {
    	layoutSubviews(width, height);
	}

	private void zoomDidChange(int newPercentageValue) {
        System.out.println("Zoom changed, new percentage!!! = " + newPercentageValue);
        zoomLabel.setText(newPercentageValue + "%");
        editor.zoomDidChange(newPercentageValue);
    }

    /**
     * Lays out all subviews in this view, given a width and a height.
     * The width and height must be the SMTEditor width/height, never arbitrary.
     * @param width
     * @param height
     */
    private void layoutSubviews(double width, double height) {
    	
    	double dimensionInputY = verticalEdgePaddingRatio*height/4;
    	double dimensionInputX = width/4;
    	double dimensionInputHeight = verticalEdgePaddingRatio*height/2;
    	
    	double refDimensionLabelX = dimensionInputX - 60;
    	double refDimensionLabelWidth = setReferenceDimensionLabelWidthRatio*width;
    	double refDimensionFieldWidth = setReferenceDimensionFieldWidthRatio*width;
    	double refDimensionButtonWidth = setReferenceDimensionButtonWidthRatio*width;
    	double nodeDimensionInputWidth = nodeDimensionInputWidthRatio*width;
    	double nodeDimensionSliderWidth = nodeDimensionInputWidth*(1 - nodeDimensionSliderLabelWidthRatio);
    	double nodeDimensionLabelWidth = nodeDimensionInputWidth*nodeDimensionSliderLabelWidthRatio;
    	
    	double coordinateOutputX = refDimensionLabelX + refDimensionLabelWidth + nodeDimensionInputWidth;
    	double coordinateOutputWidth = width*coordinateOutputWidthRatio;
    	
//    	double dimensionSlidersHeight = verticalEdgePaddingRatio*height/2;
//    	double dimensionSlidersTotalWidth = width/4;
//    	double dimensionSlidersLabelWidth = dimensionSlidersTotalWidth*dimensionSliderLabelWidthRatio;
//    	double dimensionSlidersSliderWidth = dimensionSlidersTotalWidth*(1 - dimensionSliderLabelWidthRatio);

        double editorX = horizontalEdgePaddingRatio*width;
        double editorY = verticalEdgePaddingRatio*width;

        double editorWidth = editorWidthRatio*width;
        double editorHeight = editorHeightRatio*height;

        double zoomX = editorX + editorWidth*zoomXRatio;
        double zoomY = editorY + editorHeight*zoomYRatio;
        double zoomWidth = editorWidth*zoomWidthRatio;
        
        double alphaKappaX = editorX + alphaKappaXRatio*editorWidth;
        double alphaKappaY = editorY + alphaKappaXRatio*editorHeight;
        double alphaKappaHeight = editorHeight*alphaKappaHeightRatio;
        double alphaY = alphaKappaY + alphaKappaHeight;
        double alphaKappaWidth = 50; // should be enough
        
        double componentsX = editorX + editorWidth + width*horizontalPaddingBetweenEditorAndComponentsRatio;
        double componentsY = editorY;

        double componentsWidth = width*componentsWidthRatio;
        double componentsHeight = height*componentsHeightRatio;


        double buttonsX = editorX;
        double buttonsY = editorY + editorHeight + height*verticalEdgePaddingRatio;

        double buttonsWidth = width*buttonsViewWidthRatio;
        double buttonsHeight = height*buttonsViewHeightRatio;


        double outputX = buttonsX + buttonsWidth + width*horizontalPaddingBetweenButtonsAndOutputRatio;
        double outputY = buttonsY;

        double outputWidth = width*outputViewWidthRatio;
        double outputHeight = height*outputViewHeightRatio;

        // TODO if the subview needs to resize its subviews, call 
        editor.resizeRelocate(editorX, editorY, editorWidth, editorHeight);
        components.resizeRelocate(componentsX, componentsY, componentsWidth, componentsHeight);
        buttons.resizeRelocate(buttonsX, buttonsY, buttonsWidth, buttonsHeight);
        output.resizeRelocate(outputX, outputY, outputWidth, outputHeight);
        zoom.resizeRelocate(zoomX, zoomY, zoomWidth, zoomHeight);
        zoomLabel.resizeRelocate(zoomX + zoomWidth*0.15, zoomY + zoomHeight, zoomWidth, zoomHeight);
        kappa.resizeRelocate(alphaKappaX, alphaKappaY, alphaKappaWidth, alphaKappaHeight);
        alpha.resizeRelocate(alphaKappaX, alphaY, alphaKappaWidth, alphaKappaHeight);
        
        referenceDimension.resizeRelocate(dimensionInputX + refDimensionLabelWidth, dimensionInputY, refDimensionFieldWidth, dimensionInputHeight);
        setReferenceDimension.resizeRelocate(dimensionInputX + refDimensionFieldWidth, 
        		dimensionInputY, refDimensionButtonWidth, dimensionInputHeight);
        rDimensionLabel.resizeRelocate(refDimensionLabelX, dimensionInputY, // *1.75 just to make it fit right
        		refDimensionLabelWidth, dimensionInputHeight);

        referenceNodeDimension.resizeRelocate(dimensionInputX + refDimensionFieldWidth + refDimensionButtonWidth, dimensionInputY, 
        		nodeDimensionSliderWidth, nodeDimensionSliderWidth);
        rNodeDimensionLabel.resizeRelocate(dimensionInputX + refDimensionFieldWidth + refDimensionButtonWidth, 
        		dimensionInputHeight + dimensionInputY, 
        		nodeDimensionLabelWidth, 
        		dimensionInputHeight);
        
        referenceDimension.setPrefWidth(refDimensionFieldWidth);
        referenceNodeDimension.setPrefWidth(nodeDimensionSliderWidth);
        
        coordinateOutput.resizeRelocate(coordinateOutputX, dimensionInputY, coordinateOutputWidth, dimensionInputHeight);
    }
    
    public void updateOutput(SharedMulticastTree tree) {
    	for(OutputFields f : TextOutputView.OutputFields.values()) {    		
    		output.updateFieldWithNewValue(f, tree.getValueForField(f));
    	}
    }

	public void buttonClicked(Buttons type) {
		editor.getContentView().buttonClicked(type);
	}

	public void fileWasDropped(File file) {
		editor.getContentView().fileWasDropped(file);
	}

	public void saveButtonClicked() {
		editor.getContentView().saveButtonClicked();
	}

	public void saveTree(SharedMulticastTree tree) {
		FileChooser chooser = new FileChooser();
		chooser.setTitle("Save Tree...");
		
		File file = chooser.showSaveDialog(this.stage);
		if(file != null) {
			SMTParser.writeTreeToFile(tree, file);
		}
	}

	public void inputDidChange(double value, InputViewType type) {
		this.editor.getContentView().inputDidChange(value, type);
		
		getLabelForType(type).setText(type.getString() + " = " + value);
	}
	
	private Label getLabelForType(InputViewType type) {
		if(type == InputViewType.ALPHA)
			return this.alpha;
		else if(type == InputViewType.KAPPA)
			return this.kappa;
		else 
			return null;
	}

	public void toggleButtonClicked(ToggleButtons type, boolean isSelected) {
		this.editor.getContentView().toggleButtonClicked(type, isSelected);
	}

	public void checkBoxClicked(CheckBoxes type, boolean isSelected) {
		this.editor.getContentView().checkBoxClicked(type, isSelected);
	}

	public void radioButtonClicked(RadioButtons type) {
		this.editor.getContentView().radioButtonClicked(type);
	}

	public void cellSizeDidChange(int intValue) {
		this.editor.getContentView().cellSizeDidChange(intValue);
	}


	/**
	 * Adapted from NumericTextField
	 * @author admin
	 *
	 */
	private class BoundedNumericTextField extends TextField {
		
		private int min, max;
		private int currentValue;
		
		public BoundedNumericTextField(int min, int init, int max, String prompt) {
			super(Integer.toString(init));
			this.setPromptText(prompt + " in range [" + min + ", " + max + "]");
			this.min = min;
			this.max = max;
		}
		
		@Override
		public void replaceText(int start, int end, String text) {
		    String pre = getText(0, start),
				   post = getText(end, this.getLength()),
				   fullString = pre + text + post;
		    
		    if (!text.isEmpty() && isInteger(text)) 
		        try {
		            int val = Integer.parseInt(fullString); 
		            
		            if(val == currentValue) // ignore redundant zeros, i.e a "0" prepended to n should be ignored
		            	return;
		            
		            if(val <= max) {
		            	super.replaceText(start, end, text);
		            	currentValue = val;
		            	SMTEditor.this.setReferenceDimension.setDisable(false);
		            }
		            
		            if(val < min) {
		            	SMTEditor.this.setReferenceDimension.setDisable(true);
		            }
		            
		            if(val > max) {
		            	super.setText(Integer.toString(max));
		            	currentValue = max;
		            	SMTEditor.this.setReferenceDimension.setDisable(false);
		            }
		            
//			        InputView.this.inputDidChange();
		        } catch (NumberFormatException e) {
		        	// do nothing
		        }

		    if (text.isEmpty()) {
		        super.replaceText(start, end, text);
		    }
		}

		/**
		 * Checks if every char in a string is a digit, i.e if it's an int
		 * @param text
		 * @return
		 */
		private boolean isInteger(String text) {
			for(char c : text.toCharArray())
				if(!Character.isDigit(c))
					return false;
			return true;
		}
		
		int getCurrentValue() {
			return this.currentValue;
		}

	} 
}
