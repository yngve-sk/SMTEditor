package application;

import javafx.scene.Parent;
import javafx.scene.Scene;
import model.SharedMulticastTree;

@SuppressWarnings("unused")

public class SMTEditor extends Scene {

    private SharedMulticastTree tree; // all the data to be displayed is here

    private SMTView editor; // displays the tree itself
    private SMTComponentView components; // displays components that can be dragged into editor
    private TextOutputView output; // displays stats for tree
    private ButtonsView buttons; // displays buttons

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
    private final double componentsWidthRatio = 1 - editorWidthRatio - horizontalPaddingBetweenEditorAndComponentsRatio;
    private final double componentsHeightRatio = editorHeightRatio; // same height but written for clarity/future modification

    // Buttons view width and height
    private final double buttonsViewWidthRatio = 0.30;
    private final double buttonsViewHeightRatio = 1 - editorHeightRatio - verticalEdgePaddingRatio;

    // Output view width and height
    private final double outputViewWidthRatio = 1 - buttonsViewWidthRatio - horizontalPaddingBetweenButtonsAndOutputRatio;
    private final double outputViewHeightRatio = buttonsViewHeightRatio; // might be modified later


    public SMTEditor(Parent root, double width, double height) {
        super(root, width, height);
        editor = new SMTView();
        components = new SMTComponentView();
        buttons = new ButtonsView();
        output = new TextOutputView();

        layoutSubviews(width, height); // layout logic separated for autoresizing behavior
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
    }



}
