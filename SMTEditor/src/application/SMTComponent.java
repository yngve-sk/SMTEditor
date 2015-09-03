package application;

import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class SMTComponent extends Group {

    private ImageView icon;
    private Label text;
    private boolean isSelected;
    private Components type;

    private final double iconHeightUsageRatio = 0.80;
    private final double iconSizeWithinBoxRatio = 0.75;

    private final double labelWidthRatio = 1; // max value is 1

    private final double SELECTED_OPACITY = 1;
    private final double NOT_SELECTED_OPACITY = 0.4;

    public SMTComponent(Components type) {
        icon = new ImageView(new Image(type.getImagePath()));
        text = new Label(type.getDescription());

        this.type = type;

        icon.setOpacity(NOT_SELECTED_OPACITY);
        isSelected = false;

        this.getChildren().addAll(icon, text);

        this.setOnMouseEntered(event -> mouseEntered());
        this.setOnMouseExited(event -> mouseExited());
        this.setOnMouseClicked(event -> mouseClicked());
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

    void unselect() {
        isSelected = false;
        mouseExited();
    }

    void select() {
        icon.setOpacity(SELECTED_OPACITY);
        isSelected = true;
    }

    @Override
    public void resizeRelocate(double x, double y, double width, double height) {
        super.resizeRelocate(x, y, width, height);
        layoutSubviews(width, height);
    }

    private void mouseEntered() {
        if(isSelected)
            return;
        icon.setOpacity(SELECTED_OPACITY);
    }

    private void mouseExited() {
        if(isSelected)
            return;
        icon.setOpacity(NOT_SELECTED_OPACITY);
    }

    private void mouseClicked() {
        icon.setOpacity(SELECTED_OPACITY);
        isSelected = true;
        SMTComponentView parent = (SMTComponentView) this.getParent();
        parent.iconWasSelected(this);
    }

    public Components getComponentType() {
        return type;
    }
}
