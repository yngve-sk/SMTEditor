package application_smtview;

import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Line;
import model.SMTNode;
import model.SMTNodeFactory;
import model.SharedMulticastTree;
import application__componentview.Components;


@SuppressWarnings("unused")
/**
 * This will be placed inside the SMTView, this represents the tree itself,
 * the SMTView will be the ScrollView viewing this
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTContentView extends Group {

    private SharedMulticastTree tree;
    private Components componentType;

    private final double maxDimension = 5000;
    private final double referenceDimension = 1000; // TODO might need to be fit to default input dimension
    private final double referenceNodeDimension = 25;

    private double currentDimension;
    private double currentNodeDimension;

    private ImageView background;

    private ImageView phantom;
    private Image destination;
    private Image nonDestination;

    private double nodeScale = 1;

    private StatsView statsPopup;

    public SMTContentView() {
        currentDimension = referenceDimension;
        currentNodeDimension = referenceNodeDimension;

        this.statsPopup = new StatsView();
        this.statsPopup.setVisible(false);

        this.resize(maxDimension, maxDimension);
        this.setBlendMode(BlendMode.DARKEN);
        background = new ImageView(new Image("images/background.jpg"));
        this.getChildren().add(background);
        background.autosize();
        background.setFitWidth(maxDimension);
        background.setFitHeight(maxDimension);

        destination = new Image(Components.DESTINATION.getImagePath());
        nonDestination = new Image(Components.NONDESTINATION.getImagePath());

        phantom = new ImageView();
        phantom.setOpacity(0.5);

        getChildren().add(phantom);
        phantom.setVisible(false);
    }

    public void draw(SharedMulticastTree tree) {

        ObservableList<Node> children = getChildren();

        children.clear();
        this.tree = tree;

        List<SMTNode> nodes = tree.getNodes();

        for(SMTNode n : nodes) { // translate all nodes into views, render them and add to content view
            double visualX = transformCoordinateValueFromModelToVisual(n.getX());
            double visualY = transformCoordinateValueFromModelToVisual(n.getY());

            SMTNodeView view = SMTNodeViewFactory.nodeView(
                    visualX, visualY,
                    referenceNodeDimension, referenceNodeDimension,
                    n, n.isDestination());

            children.add(view);

            Line l = new Line();
        }

        for(SMTNode n : nodes) {

        }

    }
/* adding lines underneath instead of all this
    private Line getLineBetween(SMTNode node, SMTNode neighbor) {

        // 1. Get inital coordinates
        double nodeX = transformCoordinateValueFromModelToVisual(node.getX());
        double nodeY = transformCoordinateValueFromModelToVisual(node.getY());

        double neighborX = transformCoordinateValueFromModelToVisual(neighbor.getX());
        double neighborY = transformCoordinateValueFromModelToVisual(neighbor.getY());

        // 2. Displace the coordinates by 1 radios AWAY from its origin point (nodeX, nodeY)
        // in same direction as line
        Line l = new Line(nodeX, nodeY, neighborX, neighborY);
        adaptLineToFit(l);

        return l;
    }
*/
    /**
     * Displaces and shortens the line by one diameter, towards the end point.
     * @param l
     *      the line containing the coordinates
     */
    private void adaptLineToFit(Line l) {
        // Displace start and end points towards each other by 1 radius
        double angle = getAngleRelativeToHorizontalPlane(l);

        // TODO these math calculations are likely wrong and it might be that the lines
        // can just be "tucked under" the vertices anyway.............................................
        double startX = l.getStartX() + Math.sin(angle)*currentNodeDimension;
        double startY = l.getStartY() + Math.cos(angle)*currentNodeDimension;

        double endX = l.getEndX() - Math.cos(angle)*currentNodeDimension;
        double endY = l.getEndY() - Math.sin(angle)*currentNodeDimension;

        l.setStartX(startY);
        l.setStartY(startY);

        l.setEndX(endX);
        l.setEndY(endY);
    }

    private double getAngleRelativeToHorizontalPlane(Line l) {
        double distX = getDist(l.getStartX(), l.getEndX());
        double distY = getDist(l.getStartY(), l.getEndY());

        double angle = Math.atan((distY/distX));
        return angle;
    }

    private double getDist(double src, double dest) {
        return dest - src;
    }




    private double transformCoordinateValueFromModelToVisual(double modelValue) {
        return modelValue*modelToVisual();
    }

    private double modelToVisual() {
        return currentDimension/referenceDimension;
    }


    private double transformCoordinateValueFromVisualToModel(double visualValue) {
        return visualValue*visualToModel();
    }

    private double visualToModel() {
        return referenceDimension/currentDimension;
    }

    /**
     * Shows the stats popup, makes it pop up on top of the node where it's displayed visually.
     * @param sender
     *      the node to be displayed
     */
    void showStatsPopup(SMTNode sender, double x, double y) {
        statsPopup.relocate(x, y);
        statsPopup.displayNode(sender);
        statsPopup.setVisible(true);
    }

    /**
     * Hides the stats popup
     */
    void hideStatsPopup() {
        statsPopup.setVisible(false);
    }

    public void componentSelectionDidChange(Components componentType) {
        this.componentType = componentType;
        if(componentType.isNode()) {
            phantom.setImage(componentType == Components.DESTINATION ? destination : nonDestination);
            phantom.setVisible(true);
        }
        else
            phantom.setVisible(false);
    }

    public void mouseOver(Point2D coordinate) {
        if(!phantom.isVisible())
            return;
        phantom.relocate(coordinate.getX(), coordinate.getY());
    }

    public void zoomDidChange(int percentage) {
        double previousNodeScale = nodeScale;
        nodeScale = (1.0*percentage/100.0);
        double ratio = nodeScale/previousNodeScale;

        for(Node n : getChildren())
            if(n instanceof SMTNodeView) {
                double x = n.getLayoutX()*ratio;
                double y = n.getLayoutY()*ratio;
                double width = n.getLayoutBounds().getWidth()*ratio;
                double height = n.getLayoutBounds().getHeight()*ratio;

                n.resizeRelocate(x, y, width, height);
            }
    }

    public void mouseClicked() {
        if(componentType == Components.CURSOR) {

        }
        else if(componentType == Components.DESTINATION) {
            SMTNode newNode = SMTNodeFactory.newNode(
                    transformCoordinateValueFromVisualToModel(phantom.getLayoutX()),
                    transformCoordinateValueFromVisualToModel(phantom.getLayoutY()), true);

            SMTNodeView view = SMTNodeViewFactory.nodeView(phantom.getLayoutX(), phantom.getLayoutY(), getCurrentNodeDimension(), getCurrentNodeDimension(), newNode, true);
            getChildren().add(view);
        }
        else if(componentType == Components.NONDESTINATION) {
            SMTNode newNode = SMTNodeFactory.newNode(
                    transformCoordinateValueFromVisualToModel(phantom.getLayoutX()),
                    transformCoordinateValueFromVisualToModel(phantom.getLayoutY()), false);

            SMTNodeView view = SMTNodeViewFactory.nodeView(phantom.getLayoutX(), phantom.getLayoutY(), getCurrentNodeDimension(), getCurrentNodeDimension(), newNode, false);
            getChildren().add(view);
        }
        else if(componentType == Components.LINK) {

        }
    }

    private double getCurrentNodeDimension() {
        return currentNodeDimension*nodeScale;
    }

}
