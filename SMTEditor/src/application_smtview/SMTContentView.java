package application_smtview;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.IdTracker;
import model.SMTFactory;
import model.SMTLink;
import model.SMTNode;
import model.SharedMulticastTree;
import utils.Dictionary;
import application_componentview.Components;


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
    private final double referenceNodeDimension = 35;

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

    private Dictionary<SMTLink, SMTLinkView> linkDictionary;
    private HashMap<Integer, SMTNodeView> nodeDictionary;

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
        phantom.setFitWidth(getCurrentNodeDimension());
        phantom.setFitHeight(getCurrentNodeDimension());

        linkDictionary = new Dictionary<SMTLink, SMTLinkView>();

        nodeDictionary = new HashMap<Integer, SMTNodeView>();
        componentType = Components.CURSOR;

        getChildren().addAll(background, phantom, statsPopup);
    }


    public void draw() {
        ObservableList<Node> children = getChildren();
        children.clear();
        children.add(background);

        linkDictionary.clear();
        nodeDictionary.clear();

        Collection<SMTNode> nodes = tree.getNodes();

        HashMap<Integer, Integer> endPointIds = new HashMap<Integer, Integer>();

        double d = currentNodeDimension/2;
        // Create views out of all the nodes and links
        for(SMTNode n : nodes) {
            Point2D start = nodeCoordinatesToVisual(n);
            Point2D startCenter = start.add(d, d);

            SMTNodeView view = SMTNodeViewFactory.newNodeView(n.id, start, getCurrentNodeDimension(), n.isDestination);
            nodeDictionary.put(n.id, view);

            for(SMTNode neighbor : tree.getNeighborsOfNode(n.id)) {
                Point2D end = nodeCoordinatesToVisual(neighbor);
                Point2D endCenter = end.add(d, d);

                SMTLink link = new SMTLink(n.id, neighbor.id);
                if(!linkDictionary.containsKey(link)) {
                    SMTLinkView linkView = SMTLinkViewFactory.newLinkView(startCenter, n.id, endCenter, neighbor.id);
                    linkDictionary.put(link, linkView);

                    view.addLink(neighbor.id, true);
                    endPointIds.put(neighbor.id, n.id);
                }
            }
        }

        for(Integer i : endPointIds.keySet())
            nodeDictionary.get(i).addLink(endPointIds.get(i), false);

        // Add views to children
        children.addAll(linkDictionary.values());
        children.addAll(nodeDictionary.values());

        // Add stats popup
        children.add(statsPopup);
        // Done
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
        return visualValue*visualToModel();
    }

    private double visualToModel() {
        return referenceDimension/(currentDimension*nodeScale);
    }

    /**
     * Shows the stats popup, makes it pop up on top of the node where it's displayed visually.
     * @param senderId
     *      the id of the node to be displayed
     */
    void showStatsPopup(int senderId, double x, double y) {
        double dx = 0;
        double dy = 0;
        System.out.println("ShowStatsPopup!");
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
        statsPopup.displayNode(tree.getNode(senderId));
        statsPopup.toFront();
    }

    /**
     * Hides the stats popup
     */
    void hideStatsPopup() {
        statsPopup.setVisible(false);
    }

    public void componentSelectionDidChange(Components componentType) {
        System.out.println("Component type changed... is now " + componentType);
        this.componentType = componentType;
        if(componentType.isNode()) {
            phantom.setImage(componentType == Components.DESTINATION ? destination : nonDestination);
            phantom.setVisible(true);
        }
        else
            phantom.setVisible(false);
    }

    public void mouseOver(Point2D coordinate) {
        if(componentType == Components.LINK && isLinking) {
        //  System.out.println("SMTContentView.mouseOver(" + coordinate.toString() + ")");
            linkInProgress.setEndX(coordinate.getX());
            linkInProgress.setEndY(coordinate.getY());
            return;
        }
        if(phantom.isVisible()) // if a link is being made don't show phantom...
            phantom.relocate(coordinate.getX(), coordinate.getY());
    }


    public void zoomDidChange(int percentage) {
        double previousNodeScale = nodeScale;
        nodeScale = (1.0*percentage/100.0);
        double ratio = nodeScale/previousNodeScale;

        for(Node n : getChildren())
            if(n instanceof SMTNodeView || n == phantom) {
                double x = n.getLayoutX()*ratio;
                double y = n.getLayoutY()*ratio;
                double width = n.getLayoutBounds().getWidth()*ratio;
                double height = n.getLayoutBounds().getHeight()*ratio;

                n.resizeRelocate(x, y, width, height);
            }
            else if(n instanceof SMTLinkView) {
                SMTLinkView view = (SMTLinkView) n;
                double sx = view.getStartX();
                double sy = view.getStartY();

                double dx = view.getEndX();
                double dy = view.getEndY();

                view.setStartX(sx*ratio);
                view.setStartY(sy*ratio);

                view.setEndX(dx*ratio);
                view.setEndY(dy*ratio);
            }

    }


    private boolean isLinking = false;
    private SMTNodeView linkOrigin = null;
    private SMTNodeView selectedNode;
    private SMTLinkView linkInProgress;

    /**
     * Called every time the mouse enters a node, a reference to it is stored until it exits
     * @param selected
     *      the selected node
     */
    public void updateSelectedNode(SMTNodeView selected) {
        selectedNode = selected;
    }

    /**
     * Clears the selected node, this is called when the mouse exits a node
     */
    public void clearSelectedNode() {
        selectedNode = null;
    }

    public void mouseClicked() { //System.out.println("SMTContentView.mouseClicked() ");
        // If a node is placed and the tree is null, init a new tree
        if(componentType.isNode() && tree == null) {
            tree = SMTFactory.emptyTree();
         // System.out.println("New tree created, node placed");
        }

        if(componentType == Components.CURSOR) {

        }
        else if(componentType.isNode()) {
            double modelX = transformCoordinateValueFromVisualToModel(phantom.getLayoutX());
            double modelY = transformCoordinateValueFromVisualToModel(phantom.getLayoutY());

            SMTNodeView view = SMTNodeViewFactory.newNodeView(IdTracker.getNextNodeId(),
                    phantom.getLayoutX(), phantom.getLayoutY(), getCurrentNodeDimension(),
                    componentType == Components.DESTINATION);

            tree.addNode(modelX, modelY, componentType == Components.DESTINATION,
                    IdTracker.getNextNodeId(), null); // order of these two calls is important

            getChildren().add(view);
        }
        else if(componentType == Components.LINK) { //System.out.println("Component type is link... selected Node = " + selectedNode);
            if(isLinking) { // A center node is set, link center node to selected node and isLinking to false
                if(selectedNode == null) { // remove the node in progress and reset the stuff
                  //System.out.println("selectedNode is null, reseting!");
                    getChildren().remove(linkInProgress);
                    resetLinkInProgress();
                }
                else
                    anchorLinkTo(selectedNode);
            }
            else { // No center node is set, set center node to selected node and isLinking to true
                if(selectedNode != null) // if null do nothing
                    createLinkAtOrigin(selectedNode);
            }
        }
    }

    /**
     * Anchors the link "in progress" to the selected node, i.e the selected node will be the end point of the link.
     * This also finalizes the node in progress and resets related fields
     * @param selectedNode
     */
    private void anchorLinkTo(SMTNodeView selectedNode) {
        double d = getCurrentNodeDimension()/2;
        System.out.println("Anchoring link!");
        Point2D coordinates = selectedNode.getCoordinatesWithinParent();
        Point2D centerCoordinates = coordinates.add(d, d); // anchor at center of node

        linkInProgress.setEndPoint(centerCoordinates, selectedNode.getNodeId());

        // Add link to tree
        updateTreeWithNewLink(linkInProgress);

        // Reset link cache
        resetLinkInProgress();

        // Redraw now that tree is updated
        draw();
    }

    /**
     * This is called when a new link is added visually, this will update the
     * tree model object so that it is synchronized with the view
     * @param newLink
     */
    private void updateTreeWithNewLink(SMTLinkView newLink) {
        tree.addLink(newLink.getStartId(), newLink.getEndId());
    }

    /**
     * Resets the link cache
     */
    private void resetLinkInProgress() {
        isLinking = false;
        linkOrigin = null;
        selectedNode = null;
        linkInProgress = null;
    }

    /**
     * Creates a "link in progress", it anchors its start and end point at origin, at the mouse moves, its end point
     * will move with the mouse.
     * @param origin
     *      the origin anchor of the link
     */
    private void createLinkAtOrigin(SMTNodeView origin) {
        System.out.println("SMTContentView.createLinkAtOrigin()");
        double d = getCurrentNodeDimension()/2;

        Point2D coordinates = origin.getCoordinatesWithinParent();
        Point2D centerCoordinates = coordinates.add(d, d); // Anchor link at center of node

        linkInProgress = SMTLinkViewFactory.newLinkInProgress(centerCoordinates, origin.getNodeId());

        isLinking = true;
        linkOrigin = origin;

        getChildren().add(linkInProgress); // Add link to children
        setZOrderUnderNodes(linkInProgress); // Make sure link isn't on top of nodes
    }

    /**
     * Sets the node to the lowest Z-order, but still over the background
     * @param node
     */
    private void setZOrderUnderNodes(Node node) {
        node.toBack();
        background.toBack();
    }

    private double getCurrentNodeDimension() {
        return currentNodeDimension*nodeScale;
    }

    public boolean isDraggingNodesAllowed() {
        return componentType == Components.CURSOR;
    }

    /**
     * Called when a node is being dragged.
     * @param node
     *      the node being dragged, its data should contain updated coordinates
     */
    public void nodeWasDragged(SMTNodeView node, double x, double y) {

        double modelX = transformCoordinateValueFromVisualToModel(x);
        double modelY = transformCoordinateValueFromVisualToModel(y);

        tree.relocateNode(modelX, modelY, node.getNodeId());

        double d = getCurrentNodeDimension()/2;

        node.relocate(x + d, y + d);
        relocateLinksConnectedToNode(node, x + d, y + d);
    }


    /**
     * Relocates all links connected to this node, they will relocate the end of the link that's connected to the node,
     * to the nodes center coordinates.
     * @param node
     * @param x
     * @param y
     */
    private void relocateLinksConnectedToNode(SMTNodeView node, double x, double y) {

        List<SMTLink> linksOfNodeBeingDragged = node.getAllLinks();
        System.out.println("node.getAllLinks.size() = " + linksOfNodeBeingDragged.size());
        int i = 0;
        for(SMTLink l : linksOfNodeBeingDragged) {
            SMTLinkView view = linkDictionary.get(l);
            if(node.isLinkStart(view.getLink())) {
                view.setStartX(x);
                view.setStartY(y);
            }
            else {
                view.setEndX(x);
                view.setEndY(y);
            }
        }
    }

    public void nodeWasDroppedAfterDragMove() {
        if(!isDraggingNodesAllowed())
            return;

        // Recalculate data
        double time = tree.recalculate(); // TODO pass time up in hierarchy for display...
        System.out.println("recalculation took " + time + "!");
        // Redraw tree, cache scroll position
        parent.cacheScroll();
        draw();
        parent.restoreScrollFromCache();
    }


    public List<SMTLinkView> getLinkViews(List<SMTLink> links) {
        List<SMTLinkView> linkViews = new ArrayList<SMTLinkView>();
        for(SMTLink l : links)
            linkViews.add(linkDictionary.get(l));
        return linkViews;
    }





}
