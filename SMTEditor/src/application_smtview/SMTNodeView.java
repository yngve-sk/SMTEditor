package application_smtview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.SMTLink;

/**
 * Represents a generic node view
 * @author Yngve Sekse Kristiansen
 *
 */
public abstract class SMTNodeView extends ImageView {

    private int nodeId;

    private HashMap<Integer, Boolean> links;

    public SMTNodeView(double x, double y, double dimension, String imagePath, int nodeId) {
        super(new Image(imagePath));

        this.resizeRelocate(x, y, dimension, dimension);

        this.nodeId = nodeId;

        this.setOnMouseEntered(event -> mouseEntered(event));
        this.setOnMouseExited(event -> mouseExited(event));
        this.setOnMouseDragged(event -> mouseDragged(event));
        this.setOnMouseReleased(event -> mouseDragReleased(event));
        this.setOnMouseClicked(event -> mouseClicked(event));

        links = new HashMap<Integer, Boolean>();
    }

    private void mouseClicked(MouseEvent event) {
        System.out.println("SMTNodeView Mouse clicked!!!");
     //   getContentView().mouseClicked();
    }

    public void addLink(int id, boolean isEnd) {
        links.put(id, isEnd);
    }

    public void removeLink(SMTLinkView link) {
        links.remove(link);
    }

    List<SMTLink> getAllLinks() {
        List<SMTLink> links = new ArrayList<SMTLink>();
        for(Integer id : this.links.keySet())
            links.add(new SMTLink(nodeId, id));

        return links;
    }

    private List<SMTLinkView> getAllLinkViews() {

        return getContentView().getLinkViews(getAllLinks());
    }

    public void relocateLinkEndpoints(double centerX, double centerY) {
        if(links.isEmpty())
            return;

        for(SMTLinkView l : getAllLinkViews()) {
            if(links.get(l)) { // isEnd
                l.setStartX(centerX);
                l.setStartY(centerY);
            }
            else {
                l.setEndX(centerX);
                l.setEndY(centerY);
            }
        }
    }

    /**
     *
     * @param link
     * @return
     *      true if this node is the START of a link (visually)
     */
    public boolean isLinkStart(SMTLink link) {
        return links.get(link.id1 == nodeId ? link.id2 : link.id1);
    }

    /**
     * Call on mouse enter
     */
    private void highlightAllLinks() {
        if(links.isEmpty())
            return;

        SMTLinkViewFilter filter = new SMTLinkViewFilter();
        for(SMTLinkView link : getAllLinkViews()) {
            link.highlight();
            filter.runThroughFilter(link);
        }

        SMTLinkView[] highestTwo = filter.getHighestTwo();
        if(highestTwo[0] != null)
            highestTwo[0].highlightAsPowerLevelTwo();
        if(highestTwo[1] != null)
            highestTwo[1].highlightAsPowerLevelOne();
    }

    /**
     * Call on mouse exit
     */
    private void resetLinkHighlighting() {
        if(links.isEmpty())
            return;

        for(SMTLinkView link : getAllLinkViews()) {
            link.reset();
        }
    }

    private boolean dragInProgress;

    private void mouseDragReleased(MouseEvent event) {
        if(!dragInProgress)
            return;
        System.out.println("mouseDragReleased... x = " + event.getX() + ", y = " + event.getY());
        getContentView().nodeWasDroppedAfterDragMove();
        dragInProgress = false;
    }


    private void mouseDragged(MouseEvent me) {
        if(!getContentView().isDraggingNodesAllowed())
            return;

        dragInProgress = true;
        Point2D d = localToParent(me); // dx and dy is subtracted to emulate the node being clicked at its anchor
        double x = d.getX();
        double y = d.getY();

        SMTContentView contentView = getContentView();
        contentView.nodeWasDragged(this, x, y);
        this.relocate(x, y);
    }

    private Point2D localToParent(MouseEvent me) {
        return this.localToParent(me.getX(), me.getY());
    }

    @Override
    public void resizeRelocate(double x, double y, double width, double height) {
        super.resizeRelocate(x, y, width, height);
        this.setFitWidth(width);
        this.setFitHeight(height);
    }

    /**
     * Shows the popup displaying info about this node, highlights outgoing links.
     * @param me
     *      the mouse event
     */
    private void mouseEntered(MouseEvent me) {
        Point2D parentCoords = localToParent(me);
        getContentView().updateSelectedNode(this);
        getContentView().showStatsPopup(nodeId, parentCoords.getX(), parentCoords.getY());
        highlightAllLinks();
    }

    /**
     * Hides stats popup, clears node cache in the content view and resets
     * highlighting of all the outgoing links from this node
     * @param me
     *      the mouse event, for now there is no apparent use for it but it's left there in case
     */
    private void mouseExited(MouseEvent me) {
        getContentView().hideStatsPopup();
        getContentView().clearSelectedNode();
        resetLinkHighlighting();
    }

    private SMTContentView getContentView() {
        return (SMTContentView) this.getParent();
    }

    public int getNodeId() {
        return nodeId;
    }

    public Point2D getCoordinatesWithinParent() {
        return localToParent(getX(), getY());
    }

// @deprecated, this should be done from content view directly to model
//    public void updateModelCoordinates(
//            double modelX,
//            double modelY) {
//        System.out.println("updateModelCoordinates x y = (" + modelX + ", " + modelY + ")");
//        nodeId.setX(modelX);
//        nodeId.setY(modelY);
//    }

    private class SMTLinkViewFilter {

        private SMTLinkView highest;
        private SMTLinkView secondHighest;

        public SMTLinkViewFilter() {
            highest = null;
            secondHighest = null;
        }

        /**
         * Runs a node through the filter, retains if it's longer than the 2nd longest currently beign retained
         * @param link
         */
        public void runThroughFilter(SMTLinkView link) {
            if(link == null)
                return;

            if(highest == null)
                highest = link;
            else if(secondHighest == null)
                secondHighest = link;

            else {
                if(highest.getLength() < link.getLength()) {
                    secondHighest = highest;
                    highest = link;
                }
                else if(link.getLength() > secondHighest.getLength() && link.getLength() < highest.getLength()) {
                    secondHighest = link;
                }
            }
        }

        /**
         * Gets the two highest, highest at index 1
         * @return
         */
        public SMTLinkView[] getHighestTwo() {
            SMTLinkView[] highestTwo = {secondHighest, highest};
            return highestTwo;
        }
    }
}
