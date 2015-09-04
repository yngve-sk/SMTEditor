package application_smtview;

import java.util.HashMap;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.SMTFactory;
import model.SMTLink;
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

    private final double maxDimension = 5000; /* TODO some of this "extra" data might be redundant...
                                               * was supposed to be used to custom zooming,
                                               * but using scale seems to work fine for now...
                                               */

    private final double referenceDimension = 1000; // TODO might need to be fit to default input dimension
    private final double referenceNodeDimension = 25;

    private double currentDimension;
    private double currentNodeDimension;

    private ImageView background;

    private ImageView phantom;
    private Image destination;
    private Image nonDestination;

    private double nodeScale = 1;
    private boolean isDragging = false;
    private SMTNodeView beingDragged;

    private StatsView statsPopup;
    private SMTView parent;

    private HashMap<SMTLinkView, SMTLink> linkDictionary; // quick lookup of clicked links


    public SMTContentView(SMTView parent) {
        this.parent = parent;
        currentDimension = referenceDimension;
        currentNodeDimension = referenceNodeDimension;

        this.statsPopup = new StatsView();
        this.statsPopup.setVisible(false);

        this.resize(maxDimension, maxDimension);
        this.setBlendMode(BlendMode.DARKEN);
        background = new ImageView(new Image("images/background.jpg"));
        background.autosize();
        background.setFitWidth(maxDimension);
        background.setFitHeight(maxDimension);

        destination = new Image(Components.DESTINATION.getImagePath());
        nonDestination = new Image(Components.NONDESTINATION.getImagePath());

        phantom = new ImageView();
        phantom.setOpacity(0.5);
        phantom.setVisible(false);

        getChildren().addAll(background, phantom, statsPopup);
    }


    public void draw(SharedMulticastTree tree) {

        ObservableList<Node> children = getChildren();

        children.clear();
        children.add(background);
        this.tree = tree;

        List<SMTNode> nodes = tree.getNodes();
        System.out.println("Tree.getNodes() = " + nodes.size());
        for(SMTNode n : nodes) { // 1. Draw up all the links since they will be "under" the nodes
            Point2D start = nodeCoordinatesToVisual(n);
            for(SMTLink l : n.getAllLinks()) {
                Point2D dest = nodeCoordinatesToVisual(l.target);
                SMTLinkView view = new SMTLinkView(start, dest, l.isRelayOnly);

                linkDictionary.put(view, l);
                children.add(view);
            }
        }

        for(SMTNode n : nodes) { // translate all nodes into views, render them and add to content view
            double visualX = transformCoordinateValueFromModelToVisual(n.getX());
            double visualY = transformCoordinateValueFromModelToVisual(n.getY());

            SMTNodeView view = SMTNodeViewFactory.nodeView(
                    visualX, visualY,
                    referenceNodeDimension, referenceNodeDimension,
                    n, n.isDestination());

            children.add(view);
        }
    }

    private Point2D nodeCoordinatesToVisual(SMTNode node) {
        return new Point2D(transformCoordinateValueFromModelToVisual(node.getX()),
                           transformCoordinateValueFromModelToVisual(node.getY())
                           );
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
/*      private void adaptLineToFit(Line l) {
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

*/


    private double transformCoordinateValueFromModelToVisual(double modelValue) {
        return modelValue*modelToVisual();
    }

    private double modelToVisual() {
        return nodeScale*currentDimension/referenceDimension;
    }


    private double transformCoordinateValueFromVisualToModel(double visualValue) {
        System.out.println("transforming visual value " + visualValue + ", multiplying by " + visualToModel());
        return visualValue*visualToModel();
    }

    private double visualToModel() {
        return referenceDimension/(currentDimension*nodeScale);
    }

    /**
     * Shows the stats popup, makes it pop up on top of the node where it's displayed visually.
     * @param sender
     *      the node to be displayed
     */
    void showStatsPopup(SMTNode sender, double x, double y) {
        double dx = 0;
        double dy = 0;

        Point2D parentCoords = this.localToParent(x, y);
        double parentMidX = parent.getWidth()/2;
        double parentMidY = parent.getHeight()/2;

        if(parentMidX - x < 0) // on the right side, move left
            dx = -1*statsPopup.getWidth() - getCurrentNodeDimension();
        else // x on left side, displace only by node dim
            dx = getCurrentNodeDimension();

        if(parentMidY - y < 0) // y on the lower side, move up
            dy = -1*statsPopup.getHeight() - getCurrentNodeDimension();
        else // move down by node dim
            dy = getCurrentNodeDimension();

        statsPopup.relocate(x + dx, y + dy);
        statsPopup.displayNode(sender);
        statsPopup.toFront();
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
        if(phantom.isVisible())
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

        // If a node is placed and the tree is null, init a new tree
        if(componentType.isNode() && tree == null) {
            tree = SMTFactory.emptyTree();
            System.out.println("New tree created, node placed");
        }

        if(componentType == Components.CURSOR) {

        }
        else if(componentType == Components.DESTINATION) {
            SMTNode newNode = SMTNodeFactory.newNode(
                    transformCoordinateValueFromVisualToModel(phantom.getLayoutX()),
                    transformCoordinateValueFromVisualToModel(phantom.getLayoutY()), true);
            tree.addNode(newNode);

            SMTNodeView view = SMTNodeViewFactory.nodeView(phantom.getLayoutX(), phantom.getLayoutY(), getCurrentNodeDimension(), getCurrentNodeDimension(), newNode, true);
            getChildren().add(view);
        }
        else if(componentType == Components.NONDESTINATION) {
            SMTNode newNode = SMTNodeFactory.newNode(
                    transformCoordinateValueFromVisualToModel(phantom.getLayoutX()),
                    transformCoordinateValueFromVisualToModel(phantom.getLayoutY()), false);
            tree.addNode(newNode);

            SMTNodeView view = SMTNodeViewFactory.nodeView(phantom.getLayoutX(), phantom.getLayoutY(), getCurrentNodeDimension(), getCurrentNodeDimension(), newNode, false);
            getChildren().add(view);
        }
        else if(componentType == Components.LINK) {

        }

    }

    private double getCurrentNodeDimension() {
        return currentNodeDimension*nodeScale;
    }


    /**
     * Called when a node is done being dragged
     * @param node
     *      the node being dragged, its data should contain updated coordinates
     */
    public void nodeWasDragged(SMTNodeView node, double x, double y) {
        // Update the data
        int index = tree.getNodes().indexOf(node.getData());
        System.out.println("tree node coords BEFORE: (" + tree.getNodes().get(index).getX() + ", " + tree.getNodes().get(index).getY() + ")");

        node.updateModelCoordinates(transformCoordinateValueFromVisualToModel(x), transformCoordinateValueFromVisualToModel(y));
        tree.relocateNode(node.getData());

        System.out.println("data node coords: (" + node.getData().getX() + ", " + node.getData().getY() + ")");
        System.out.println("tree node coords AFTER: (" + tree.getNodes().get(index).getX() + ", " + tree.getNodes().get(index).getY() + ")");
    }

    public void nodeWasDroppedAfterDragMove(SMTNodeView node) {
        // Recalculate data
        double time = tree.recalculate(); // TODO pass time up in hierarchy for display...
        System.out.println("recalculation took " + time + "!");
        // Redraw tree, cache scroll position
        parent.cacheScroll();
        draw(tree);
        parent.restoreScrollFromCache();
    }





}
