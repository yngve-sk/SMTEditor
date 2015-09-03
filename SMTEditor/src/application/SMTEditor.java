package application;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.text.TextAlignment;
import model.SharedMulticastTree;
import application_smtview.SMTView;

@SuppressWarnings("unused")

public class SMTEditor extends Scene {

    private SharedMulticastTree tree; // all the data to be displayed is here

    private SMTView editor; // displays the tree itself
    private SMTComponentView components; // displays components that can be dragged into editor
    private TextOutputView output; // displays stats for tree
    private ControlsView buttons; // displays buttons

    /* Layout in percentages, going through width-wise first */
    // Content width
    private final double horizontalEdgePaddingRatio = 0.04;
    private final double contentWidthRatio = 1 - 2*horizontalEdgePaddingRatio;

    // Content height
    private final double verticalEdgePaddingRatio = 0.04;
    private final double contentHeightRatio = 1 - 2*verticalEdgePaddingRatio;

    // Editor width and height
    private final double editorWidthRatio = 0.80;
    private final double editorHeightRatio = 0.70;

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

    private final double zoomXRatio = 0.60; /* Ratio is relative to the scroll pane size, not the SMTEditor itself */
    private final double zoomWidthRatio = 0.30; /* Ratio is relative to the scroll pane size, not the SMTEditor itself */
    private final double zoomYRatio = 0.08; /* Ratio is relative to the scroll pane size, not the SMTEditor itself */
    private final double zoomHeight = 30;

    public SMTEditor(Group root, double width, double height) {
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
                    Number old_val, Number new_val) {
                    SMTEditor.this.zoomDidChange(new_val.intValue());
                }
            });

        layoutSubviews(width, height); // layout logic separated for autoresizing behavior

        root.getChildren().addAll(editor, components, buttons, output, zoom, zoomLabel);
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

        double editorX = horizontalEdgePaddingRatio*width;
        double editorY = verticalEdgePaddingRatio*width;

        double editorWidth = editorWidthRatio*width;
        double editorHeight = editorHeightRatio*height;

        double zoomX = editorX + editorWidth*zoomXRatio;
        double zoomY = editorY + editorHeight*zoomYRatio;
        double zoomWidth = editorWidth*zoomWidthRatio;

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

        // TODO if the subview needs to resize its subviews, call resizeSubviews() in it
        editor.resizeRelocate(editorX, editorY, editorWidth, editorHeight);
        components.resizeRelocate(componentsX, componentsY, componentsWidth, componentsHeight);
        buttons.resizeRelocate(buttonsX, buttonsY, buttonsWidth, buttonsHeight);
        output.resizeRelocate(outputX, outputY, outputWidth, outputHeight);
        zoom.resizeRelocate(zoomX, zoomY, zoomWidth, zoomHeight);
        zoomLabel.resizeRelocate(zoomX + zoomWidth*0.15, zoomY + zoomHeight, zoomWidth, zoomHeight);
    }



}
