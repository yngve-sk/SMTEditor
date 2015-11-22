package application_smtview;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import model.SMTLink;

/**
 * Represents a generic node view, to be extended by DestinationView and NonDestinationView
 * @author Yngve Sekse Kristiansen
 *
 */
public abstract class SMTNodeView extends ImageView {

    private int nodeId;

    // if this id is 1, and a links.get(2) returns true, that means
    // this is the graphical START point of the link from 1 to 2
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
    
    boolean isLinkedTo(int id) {
    	return this.links.keySet().contains(id);
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
            highestTwo[0].highlightAsPowerLevelOne();
        if(highestTwo[1] != null)
            highestTwo[1].highlightAsPowerLevelTwo();
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
        getContentView().nodeWasDroppedAfterDragMove();
        dragInProgress = false;
    }


    private void mouseDragged(MouseEvent me) {
        if(!getContentView().isDraggingNodesAllowed())
            return;

        dragInProgress = true;
        Point2D d = localToParent(me);
        double x = d.getX();
        double y = d.getY();

        SMTContentView contentView = getContentView();
        contentView.nodeWasDragged(this, x, y);
        highlightAllLinks();
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
            	if(highest.getLength() < link.getLength()) {            		
            		secondHighest = highest;
            		highest = link;
            	}
            	else 
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
         * Gets the two highest, highest at index 0
         * @return
         */
        public SMTLinkView[] getHighestTwo() {
            SMTLinkView[] highestTwo = {highest, secondHighest};
            return highestTwo;
        }
    }


	public Point2D getLabelAnchor() {
		Bounds bounds = this.getBoundsInParent();
		
		double x = bounds.getMinX() + bounds.getWidth();
		double y = bounds.getMinY() + bounds.getHeight();
		
		return new Point2D(x,y);
	}
}
