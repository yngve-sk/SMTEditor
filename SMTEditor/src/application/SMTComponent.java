package application;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SMTComponent extends Group {

    private ImageView icon;
    private Label text;

    private final double iconHeightUsageRatio = 0.80;
    private final double iconSizeWithinBoxRatio = 0.75;

    private final double labelWidthRatio = 1; // max value is 1

    public SMTComponent(String imagePath, String label) {
        icon = new ImageView(new Image(imagePath));
        text = new Label(label);

        this.getChildren().addAll(icon, text);
    }

    /**
     * Decorates resizeRelocate, resizing subviews.
     * width and height must be the width/height property of the SMTComponent
     * @param width
     * @param height
     */
    private void layoutSubviews(double width, double height) {
        double iconBoxHeight = height*iconHeightUsageRatio;
        double iconBoxWidth = width;

        boolean heightIsSmallerThanWidth = iconBoxHeight < iconBoxWidth;

        double iconSize = iconSizeWithinBoxRatio*(heightIsSmallerThanWidth ? iconBoxHeight : iconBoxWidth);
        double iconX = (iconBoxWidth - iconSize)/2;
        double iconY = (iconBoxHeight - iconSize)/2;

        double labelY = iconBoxHeight;
        double labelHeight = height - labelY;
        double labelWidth = labelWidthRatio*width;
        double labelX = (width - labelWidth)/2; // center it no matter what the label ratio, max ratio is 1 though

        icon.relocate(iconX, iconY);
        icon.setFitWidth(iconSize);
        icon.setFitHeight(iconSize);

        text.resizeRelocate(labelX, labelY, labelWidth, labelHeight);
    }

    @Override
    public void resizeRelocate(double x, double y, double width, double height) {
        super.resizeRelocate(x, y, width, height);
        layoutSubviews(width, height);
    }
}
