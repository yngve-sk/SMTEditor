package application_smtview;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import application.DefaultValues;
import application.SMTEditor;
import application_componentview.Components;
import application_controlsview.ControlsView.Buttons;
import application_controlsview.ControlsView.CheckBoxes;
import application_controlsview.ControlsView.RadioButtons;
import application_controlsview.ControlsView.ToggleButtons;
import application_stats_save_view.InputView.InputViewType;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.paint.Color;
import javafx.scene.shape.StrokeLineCap;
import model.IdTracker;
import model.Mode;
import model.SMTFactory;
import model.SMTLink;
import model.SMTNode;
import model.SMTParser;
import model.SharedMulticastTree;
import utils.Dictionary;

@SuppressWarnings("unused")
/**
 * This is placed inside the SMTView, this represents the plane and the tree to
 * be drawn upon it. All graphical tree logic, including logic for converting
 * coordinates from graphical to visual and vice versa should be done here.
 * 
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTContentView extends Group {

    private SharedMulticastTree tree;
    private Components componentType;

    // the visual dimension, size of the entire plane embedded within the
    // scrollpane
    private double visualPlaneDimension = DefaultValues.DEFAULT_GRAPHICAL_DIMENSION;

    // the "actual" dimension, i.e if this is 100 then the max x and
    // y-coordinate is 100
    private double referenceDimension = DefaultValues.DEFAULT_REFERENCE_DIMENSION;
    private double referenceNodeDimension = DefaultValues.DEFAULT_REFERENCE_NODE_DIMENSION; // relative
											    // size
											    // between
											    // nodes
											    // and
											    // the
											    // plane
											    // as
											    // a
											    // whole
    // i.e if referenceDimension is 10 and referenceNodeDimension is 1, a node
    // will occupy a square with side lengths
    // the same as 10% of the visual plane dimension

    // private double currentDimension;
    private double currentNodeDimension; // When the zoom changes the reference
					 // node dimension remains unchanged,
    // this variable will take the zoom degree into account

    private ImageView background;
    private Canvas grid;

    private ImageView phantom;
    private Image destination;
    private Image nonDestination;

    private double zoom = 1;
    private SMTNodeView beingDragged;

    private SMTNodeStatsView statsPopup;
    private SMTView parent;

    private Dictionary<SMTLink, SMTLinkView> linkDictionary;
    private HashMap<Integer, SMTNodeView> nodeDictionary;
    private ArrayList<Label> nodeLabels;
    private ArrayList<Label> linkLabels;
    private Label coordinateLabel;

    private boolean isUpdating; // this should be set to true whenever mouse
				// actions should be blocked

    /**
     * Initializes
     * 
     * @param parent
     *            the parent, a reference is needed to communicate back and
     *            forth at certain events.
     */
    public SMTContentView(SMTView parent) {
	this.parent = parent;
	// currentDimension = 1000;

	tree = SMTFactory.emptyTree();

	// System.out.println("current node dimension = " +
	// currentNodeDimension);

	this.statsPopup = new SMTNodeStatsView();
	this.statsPopup.setVisible(false);

	this.resize(visualPlaneDimension, visualPlaneDimension);
	this.setBlendMode(BlendMode.DARKEN);

	background = new ImageView(new Image("images/background.jpg"));
	background.autosize();
	background.setFitWidth(visualPlaneDimension);
	background.setFitHeight(visualPlaneDimension);
	background.setOpacity(1);

	destination = new Image(Components.DESTINATION.getImagePath());
	nonDestination = new Image(Components.NONDESTINATION.getImagePath());

	recalculateCurrentNodeDimension();

	phantom = new ImageView();
	phantom.setOpacity(0.5);
	phantom.setVisible(false);
	phantom.setFitWidth(getCurrentNodeDimension());
	phantom.setFitHeight(getCurrentNodeDimension());

	this.grid = new Canvas(visualPlaneDimension, visualPlaneDimension);
	grid.getGraphicsContext2D().setLineCap(StrokeLineCap.ROUND);
	grid.getGraphicsContext2D().setLineWidth(1);
	grid.getGraphicsContext2D().setGlobalAlpha(0.5);

	linkDictionary = new Dictionary<SMTLink, SMTLinkView>();
	nodeLabels = new ArrayList<Label>();
	linkLabels = new ArrayList<Label>();
	coordinateLabel = newLabel();

	nodeDictionary = new HashMap<Integer, SMTNodeView>();
	componentType = Components.CURSOR;

	getChildren().addAll(background, phantom, statsPopup, grid, coordinateLabel);
    }

    /**
     * Updates the visual node dimension This should be called after any change
     * in reference node dimension.
     */
    private void referenceNodeDimensionDidChange() {
	recalculateCurrentNodeDimension();
	draw();
    }

    private void recalculateCurrentNodeDimension() {
	// System.out.println("recalculating current node dimension: refNode/ref
	// = " + (referenceNodeDimension/referenceDimension));
	double ratio = referenceNodeDimension / referenceDimension;
	currentNodeDimension = visualPlaneDimension * ratio;
    }

    private void resizeBackgroundAndGrid() {
	this.background.setFitWidth(visualPlaneDimension);
	this.background.setFitHeight(visualPlaneDimension);
	this.grid.resize(visualPlaneDimension, visualPlaneDimension);
	this.resize(visualPlaneDimension, visualPlaneDimension);
	this.autosize();
    }

    /**
     * Main method for drawing, calling this renders the model object
     * (this.tree) onto the plane and updates all displays accordingly.
     */
    public void draw() {
	if (tree == null)
	    return;

	updateMinNodeXY();

	isUpdating = true; // block mouse actions when drawing
	getChildren().retainAll(background, statsPopup, phantom, grid, coordinateLabel);
	ObservableList<Node> children = getChildren();

	linkDictionary.clear();
	nodeDictionary.clear();

	// if discrete mode is on, draw the grid
	if (isInDiscreteMode)
	    drawGrid();

	Collection<SMTNode> nodes = tree.getNodes();
	Stack<Integer> toBeRemoved = new Stack<Integer>();

	// Generate all node views
	for (SMTNode n : nodes) {
	    double x = transformCoordinateValueFromModelToVisual(n.getX());
	    double y = transformCoordinateValueFromModelToVisual(n.getY());

	    if (isOutOfBounds(x) || isOutOfBounds(y)) {
		System.out.println("coord (" + x + ", " + y + ") is out of boudns!");
		toBeRemoved.push(n.id);// avoiding
				       // ConcurrentModificationException
	    } else {
		SMTNodeView view = SMTNodeViewFactory.newNodeView(n.id, x, y, getCurrentNodeDimension(),
			n.isDestination);
		nodeDictionary.put(n.id, view);
	    }
	}

	for (Integer i : toBeRemoved)
	    tree.removeNode(i);

	updateOutput();

	List<SMTLink> allLinks = tree.getAllDistinctLinks();

	double dCenter = getNodeDisplacement();
	for (SMTLink l : allLinks) {
	    int startId = l.id1;
	    int endId = l.id2;

	    SMTNodeView startView = nodeDictionary.get(startId);
	    SMTNodeView endView = nodeDictionary.get(endId);

	    Point2D start = startView.getCoordinatesWithinParent().add(dCenter, dCenter);
	    Point2D end = endView.getCoordinatesWithinParent().add(dCenter, dCenter);

	    SMTLinkView view = SMTLinkViewFactory.newLinkView(start, startId, end, endId);

	    startView.addLink(endId, true);
	    endView.addLink(startId, false);

	    linkDictionary.put(l, view);
	}

	// Add views to children
	children.addAll(linkDictionary.values());
	children.addAll(nodeDictionary.values());

	if (this.nodeLabels.size() < tree.getNodes().size()) {
	    while (this.nodeLabels.size() < tree.getNodes().size()) {
		this.nodeLabels.add(newLabel());
	    }
	}

	if (this.linkLabels.size() < tree.getAllDistinctLinks().size()) {
	    while (this.linkLabels.size() < tree.getAllDistinctLinks().size()) {
		this.linkLabels.add(newLabel());
	    }
	}

	children.addAll(nodeLabels);
	children.addAll(linkLabels);

	updateLabels();
	this.grid.toBack();
	this.background.toBack();

	phantom.setFitHeight(getCurrentNodeDimension());
	phantom.setFitWidth(getCurrentNodeDimension());

	updateMinNodeXY();

	isUpdating = false;
    }

    private double displacementX = 0, displacementY = 0;

    private void updateMinNodeXY() {
	Point2D minXY = tree.getMinXY();

	double visualMinX = transformCoordinateValueFromModelToVisual(minXY.getX());
	double visualMinY = transformCoordinateValueFromModelToVisual(minXY.getY());

	double d = getNodeDisplacement();

	double actualVisualMinX = visualMinX - d;
	double actualVisualMinY = visualMinY - d;

	displacementX = actualVisualMinX < 0 ? Math.abs(actualVisualMinX) : 0;
	displacementY = actualVisualMinY < 0 ? Math.abs(actualVisualMinY) : 0;

	// System.out.println("updateMinNodeXY(), displacementX = " +
	// displacementX + ", displacementY = " + displacementY);
    }

    /**
     * X displacement, subtract/add this from the coordinate (depending on the
     * context) to get the "centered" placement of nodes and links
     * 
     * @return
     */
    private double getXDisplacement() {
	return getNodeDisplacement() + displacementX / 2;
    }

    /**
     * Y displacement, subtract this from the coordinate to correct the
     * "centered" placement of nodes and links
     * 
     * @return
     */
    private double getYDisplacement() {
	return getNodeDisplacement() + displacementY / 2;
    }

    private double getNodeDisplacement() {
	return getCurrentNodeDimension() / 2;
    }

    private boolean isOutOfBounds(double visualCoord) {
	// getFitWidth() takes zoom into account
	return visualCoord > this.background.getFitWidth();
    }

    private void updateLabels() {
	radioButtonClicked(selectedRadioButton);
	showNodeIds(isShowingNodeIds);
    }

    /**
     * Draws a grid with dimension same as node dimension, scaled to current
     * zoom
     */
    private void drawGrid() {
	this.grid.setVisible(true);
	double cellSize = calculateCellSizeForCellDimension(modelDiscreteCellSize);

	double backgroundDimension = background.getFitWidth();
	this.grid.setWidth(backgroundDimension);
	this.grid.setHeight(backgroundDimension);

	double numVHCells = Math.floor(backgroundDimension / cellSize);

	GraphicsContext c = grid.getGraphicsContext2D();
	c.clearRect(0, 0, backgroundDimension + 0.1, backgroundDimension + 0.1);
	// +0.1 for it not to leave uncleared lines when decreasing size

	for (int i = 0; i < numVHCells; i++) {
	    double p = (i + 1) * cellSize;
	    c.strokeLine(0, p, backgroundDimension, p);
	    c.strokeLine(p, 0, p, backgroundDimension);
	}
    }

    /**
     * Configure label style options in this method
     * 
     * @return a new label with set style options
     */
    private Label newLabel() {
	Label l = new Label();
	l.setBackground(
		new Background(new BackgroundFill(Color.BLACK, new CornerRadii(3), new Insets(-3, -5, -3, -5))));
	l.setOpacity(0.8);
	l.setTextFill(Color.WHITE);
	l.setLayoutX(5);
	l.setLayoutY(5);
	return l;
    }

    private void updateOutput() {
	SMTEditor editor = (SMTEditor) getScene();
	editor.updateOutput(tree);
    }

    private Point2D nodeCoordinatesToVisual(SMTNode node) {
	return new Point2D(transformCoordinateValueFromModelToVisual(node.getX()),
		transformCoordinateValueFromModelToVisual(node.getY()));
    }

    private double transformCoordinateValueFromModelToVisual(double modelValue) {
	return modelValue * modelToVisual() - getNodeDisplacement();
    }

    private double modelToVisual() {
	// return zoom*currentDimension/referenceDimension;
	return 1 / visualToModel();
    }

    private double transformCoordinateValueFromVisualToModel(double visualValue) {
	return (visualValue + getNodeDisplacement()) * visualToModel();
    }

    private double visualToModel() {
	return referenceDimension / (zoom * visualPlaneDimension);
    }

    /**
     * Shows the stats popup, makes it pop up on top of the node where it's
     * displayed visually.
     * 
     * @param senderId
     *            the id of the node to be displayed
     */
    void showStatsPopup(int senderId, double x, double y) {
	if (isUpdating || nodeDragInProgress)
	    return;

	Point2D correctPointAccordingToQuadrant = getCoordinateAccordingToCurrentQuadrant(new Point2D(x, y),
		statsPopup.getWidth(), statsPopup.getHeight(), 1);

	statsPopup.relocate(correctPointAccordingToQuadrant.getX(), correctPointAccordingToQuadrant.getY());
	statsPopup.displayNode(tree.getNode(senderId));
	statsPopup.toFront();
    }

    /**
     * Gets the coordinate, taking the current quadrant into account. This is so
     * that the control won't disappear to an area that's not visible within the
     * parent scroll pane.
     * 
     * @param coords
     *            the current coordinates of the control that's going to be
     *            displayed
     * @param controlWidth
     *            the height of the control that's going to be displayed
     * @param controlHeight
     *            the width of the control that's going to be displayed
     * @param scale
     *            the imaginary scale of the circle the control isn't allowed to
     *            touch, allows fine tuning
     * @return a new visual coordinate ensuring the control will be "attached"
     *         to its circle, yet not fall outside of visual bounds within the
     *         parent scrollpane.
     */
    private Point2D getCoordinateAccordingToCurrentQuadrant(Point2D coords, double controlWidth, double controlHeight,
	    double scale) {
	double x = coords.getX();
	double y = coords.getY();

	double dx = 0;
	double dy = 0;

	Point2D parentCoords = this.localToParent(coords);
	double parentMidX = parent.getWidth() / 2;
	double parentMidY = parent.getHeight() / 2;

	double scaledNodeDimension = getCurrentNodeDimension() * scale;

	if (parentMidX - x < 0) // on the right side, move left
	    dx = -1 * controlWidth - scaledNodeDimension;
	else // x on left side, displace only by node dim
	    dx = scaledNodeDimension;

	if (parentMidY - y < 0) // y on the lower side, move up
	    dy = -1 * controlHeight - scaledNodeDimension;
	else // move down by node dim
	    dy = scaledNodeDimension;

	return coords.add(dx, dy);
    }

    void hideStatsPopup() {
	statsPopup.setVisible(false);
    }

    /**
     * Called when user changes component selection in the components view.
     * 
     * @param componentType
     *            the new component type
     */
    public void componentSelectionDidChange(Components componentType) {
	this.componentType = componentType;
	if (componentType.isNode()) {
	    phantom.setImage(componentType == Components.DESTINATION ? destination : nonDestination);
	    phantom.setVisible(true);
	} else
	    phantom.setVisible(false);
    }

    /**
     * Called on mouseover, the action that happens depends on current component
     * selected, and the current state of the SMTView
     * 
     * @param coordinate
     */
    public void mouseOver(Point2D coordinate) {
	// double d = getNodeDisplacement();

	double modelX = roundModelCoordinate(
		transformCoordinateValueFromVisualToModel(coordinate.getX() - getXDisplacement())),
		modelY = roundModelCoordinate(
			transformCoordinateValueFromVisualToModel(coordinate.getY() - getYDisplacement()));

	if (isInDiscreteMode) {
	    modelX = Mode.roundModelForDiscreteCellSize(modelX);
	    modelY = Mode.roundModelForDiscreteCellSize(modelY);
	}

	relocateCoordinateLabel(coordinate, modelX, modelY);

	// coordinateLabel.setText("(" + utils.Math.trim(modelX) + ", " +
	// utils.Math.trim(modelY) + ")");
	//
	// Point2D coordsAccordingToQuadrant =
	// getCoordinateAccordingToCurrentQuadrant(coordinate,
	// this.coordinateLabel.getWidth(), this.coordinateLabel.getHeight(),
	// 0.70);
	// coordinateLabel.relocate(coordsAccordingToQuadrant.getX(),
	// coordsAccordingToQuadrant.getY());
	// coordinateLabel.toFront();

	if (isUpdating)
	    return;

	if (componentType == Components.LINK && isLinking) {
	    linkInProgress.setEndX(coordinate.getX() - getXDisplacement() + getCurrentNodeDimension() / 2);
	    linkInProgress.setEndY(coordinate.getY() - getYDisplacement() + getCurrentNodeDimension() / 2);
	    return;
	}
	if (phantom.isVisible()) { // if a link is being made don't show
				   // phantom...
	    phantom.relocate(coordinate.getX() - getXDisplacement(), coordinate.getY() - getYDisplacement());
	}
    }

    /**
     * Relocates coordinate label
     * 
     * @param coordinate
     *            the visual coordinate of the location
     * @param modelX
     * @param modelY
     */
    private void relocateCoordinateLabel(Point2D coordinate, double modelX, double modelY) {
	double displayX = modelX;
	double displayY = modelY;

	if (isInDiscreteMode) {
	    displayX = Mode.roundModelForDiscreteCellSize(modelX);
	    displayY = Mode.roundModelForDiscreteCellSize(modelY);
	}
	coordinateLabel.setText("(" + utils.Math.trim(displayX) + ", " + utils.Math.trim(displayY) + ")");
	Point2D coordsAccordingToQuadrant = getCoordinateAccordingToCurrentQuadrant(coordinate,
		this.coordinateLabel.getWidth(), this.coordinateLabel.getHeight(), 0.70);

	double x = coordsAccordingToQuadrant.getX(), y = coordsAccordingToQuadrant.getY();

	coordinateLabel.relocate(x >= 0 ? x : 0, y >= 0 ? y : 0);
	coordinateLabel.toFront();
    }

    /**
     * Called when zoom changed, should resize all components accordingly
     * 
     * @param percentage
     *            the new zoom percentage
     */
    public void zoomDidChange(int percentage) {
	double previousNodeScale = zoom;
	zoom = (1.0 * percentage / 100.0);
	double ratio = zoom / previousNodeScale;

	double scaledDim = visualPlaneDimension * percentage / 100.0;
	this.background.setFitWidth(scaledDim);
	this.background.setFitHeight(scaledDim);

	for (Node n : getChildren())
	    if (n == phantom) {
		ImageView phantom = (ImageView) n;
		double x = n.getLayoutX() * ratio;
		double y = n.getLayoutY() * ratio;
		double width = n.getLayoutBounds().getWidth() * ratio;
		double height = n.getLayoutBounds().getHeight() * ratio;

		phantom.resizeRelocate(x, y, width, height);
		phantom.setFitWidth(width);
		phantom.setFitHeight(height);
	    } else if (n instanceof SMTNodeView) {
		double x = n.getLayoutX() * ratio;
		double y = n.getLayoutY() * ratio;
		double width = n.getLayoutBounds().getWidth() * ratio;
		double height = n.getLayoutBounds().getHeight() * ratio;

		n.resizeRelocate(x, y, width, height);
	    } else if (n instanceof SMTLinkView) {
		SMTLinkView view = (SMTLinkView) n;
		double sx = view.getStartX();
		double sy = view.getStartY();

		double dx = view.getEndX();
		double dy = view.getEndY();

		view.setStartX(sx * ratio);
		view.setStartY(sy * ratio);

		view.setEndX(dx * ratio);
		view.setEndY(dy * ratio);
	    }

	if (isInDiscreteMode)
	    drawGrid();

	updateLabels();
	updateMinNodeXY();
    }

    // these private fields are here, and not on top because they are only used
    // for
    // the user linking nodes together
    private boolean isLinking = false;
    private SMTNodeView linkOrigin = null;
    private SMTNodeView selectedNode;
    private SMTLinkView linkInProgress;

    /**
     * Called every time the mouse enters a node, a reference to it is stored
     * until it exits
     * 
     * @param selected
     *            the selected node
     */
    public void updateSelectedNode(SMTNodeView selected) {
	if (isUpdating)
	    return;
	selectedNode = selected;
    }

    /**
     * Clears the selected node, this is called when the mouse exits a node
     */
    public void clearSelectedNode() {
	if (isUpdating)
	    return;
	selectedNode = null;
    }

    /**
     * If mouse is clicked, and this returns false, do nothing
     * 
     * @param parentX
     * @param parentY
     * @return
     */
    private boolean isParentLocationWithinBounds(double parentX, double parentY) {
	Point2D localXY = this.parentToLocal(parentX, parentY);

	// System.out.println("parentXY = " + parentX + ", " + parentY);
	double localX = localXY.getX(), localY = localXY.getY();
	// System.out.println("localX Y = " + localX + ", " + localY);
	double maxX = background.getFitWidth();
	double maxY = background.getFitHeight();

	// System.out.println("maxX = " + maxX + ", maxY = " + maxY);

	return localX >= 0 && localY >= 0 && localX <= maxX && localY <= maxY;
    }

    /**
     * Called when mouse is clicked
     * 
     * @param e
     */
    public void mouseClicked(MouseEvent e) {
	if (isUpdating)
	    return;

	// If a node is placed and the tree is null, init a new tree
	if (componentType.isNode() && tree == null) {
	    tree = SMTFactory.emptyTree();
	    updateOutput();
	    tree.setDiscreteMode(isInDiscreteMode, modelDiscreteCellSize);
	}

	if (componentType == Components.CURSOR) {

	} else if (componentType.isNode()) {
	    addNode();
	} else if (componentType == Components.LINK) {
	    if (isLinking) { // A center node is set, link center node to
			     // selected node and isLinking to false
		if (selectedNode == null) { // remove the node in progress and
					    // reset the stuff
		    getChildren().remove(linkInProgress);
		    resetLinkInProgress();
		} else
		    anchorLinkTo(selectedNode);
	    } else { // No center node is set, set center node to selected node
		     // and isLinking to true
		if (selectedNode != null) // if null do nothing
		    createLinkAtOrigin(selectedNode);
	    }
	} else if (componentType == Components.REMOVECURSOR) {
	    if (selectedNode != null)
		removeNode(selectedNode);
	}
    }

    /**
     * Adds a node to the tree
     */
    private void addNode() {
	double modelX = transformCoordinateValueFromVisualToModel(phantom.getLayoutX());
	double modelY = transformCoordinateValueFromVisualToModel(phantom.getLayoutY());

	/*
	 * SMTNodeView view =
	 * SMTNodeViewFactory.newNodeView(IdTracker.getNextNodeId(),
	 * phantom.getLayoutX(), phantom.getLayoutY(),
	 * getCurrentNodeDimension(), componentType == Components.DESTINATION);
	 */
	tree.addNode(modelX, modelY, componentType == Components.DESTINATION, IdTracker.getNextNodeId(), null);

	// getChildren().add(view);
	updateOutput();
	draw();
	showNodeIds(isShowingNodeIds);
    }

    /**
     * Removes a node, removes it from tree, then redraws the tree.
     * 
     * @param selectedNode
     */
    private void removeNode(SMTNodeView selectedNode) {
	tree.removeNode(selectedNode.getNodeId());
	clearSelectedNode();
	updateOutput();
	draw();
    }

    /**
     * Anchors the link "in progress" to the selected node, i.e the selected
     * node will be the end point of the link. This also finalizes the node in
     * progress and resets related fields
     * 
     * @param selectedNode
     */
    private void anchorLinkTo(SMTNodeView selectedNode) {
	if (selectedNode == linkOrigin || linkOrigin.isLinkedTo(selectedNode.getNodeId()))
	    return;
	isUpdating = true;
	double d = getNodeDisplacement();

	Point2D coordinates = selectedNode.getCoordinatesWithinParent();
	Point2D centerCoordinates = coordinates.add(d, d); // anchor at center
							   // of node

	linkInProgress.setEndPoint(centerCoordinates, selectedNode.getNodeId());

	// Add link to tree
	updateTreeWithNewLink(linkInProgress);

	// Reset link cache
	resetLinkInProgress();

	updateOutput();

	// Redraw now that tree is updated
	draw(); // will set isUpdating to false
	radioButtonClicked(selectedRadioButton);
    }

    /**
     * This is called when a new link is added visually, this will update the
     * tree model object so that it is synchronized with the view
     * 
     * @param newLink
     */
    private void updateTreeWithNewLink(SMTLinkView newLink) {
	tree.addLink(newLink.getStartId(), newLink.getEndId());
	updateOutput();
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
     * Creates a "link in progress", it anchors its start and end point at
     * origin, at the mouse moves, its end point will move with the mouse.
     * 
     * @param origin
     *            the origin anchor of the link
     */
    private void createLinkAtOrigin(SMTNodeView origin) {
	double d = getNodeDisplacement();

	Point2D coordinates = origin.getCoordinatesWithinParent();
	Point2D centerCoordinates = coordinates.add(d, d); // Anchor link at
							   // center of node

	linkInProgress = SMTLinkViewFactory.newLinkInProgress(centerCoordinates, origin.getNodeId());

	isLinking = true;
	linkOrigin = origin;

	updateMinNodeXY();

	getChildren().add(linkInProgress); // Add link to children
	setZOrderUnderNodes(linkInProgress); // Make sure link isn't on top of
					     // nodes
    }

    /**
     * Sets the node to the lowest Z-order, but still over the background
     * 
     * @param node
     */
    private void setZOrderUnderNodes(Node node) {
	node.toBack();
	background.toBack();
    }

    /**
     * Gets the current node dimension with the zoom accounted in. The
     * currentNodeDimension itself would work only if zoom is at 100%.
     * 
     * @return
     */
    private double getCurrentNodeDimension() {
	return currentNodeDimension * zoom;
    }

    public boolean isDraggingNodesAllowed() {
	return componentType == Components.CURSOR;
    }

    private boolean nodeDragInProgress = false;

    /**
     * Called when a node is being dragged.
     * 
     * @param node
     *            the node being dragged, its data should contain updated
     *            coordinates
     */
    public void nodeWasDragged(SMTNodeView node, double x, double y) {
	if (isUpdating)
	    return;

	nodeDragInProgress = true;
	this.hideStatsPopup();

	double d = getNodeDisplacement();
	double visualX = x - getXDisplacement() / 2;
	double visualY = y - getYDisplacement() / 2;

	double modelX = roundModelCoordinate(transformCoordinateValueFromVisualToModel(visualX));
	double modelY = roundModelCoordinate(transformCoordinateValueFromVisualToModel(visualY));

	double actualVisualX = transformCoordinateValueFromModelToVisual(modelX);
	double actualVisualY = transformCoordinateValueFromModelToVisual(modelY);

	tree.relocateNode(modelX, modelY, node.getNodeId());

	node.relocate(actualVisualX, actualVisualY);
	relocateCoordinateLabel(new Point2D(x, y), modelX, modelY);
	updateMinNodeXY();

	relocateLinksConnectedToNode(node, x, y);
	updateOutput();
	radioButtonClicked(selectedRadioButton); // relocate labels too
	showNodeIds(isShowingNodeIds);
    }

    private double roundModelCoordinate(double coord) {
	if (coord < 0)
	    return 0;
	if (coord > referenceDimension)
	    return referenceDimension;
	return coord;
    }

    private double roundVisualCoordinateForLink(double coord) {
	if (coord < 0)
	    return 0;
	if (coord > visualPlaneDimension)
	    return visualPlaneDimension;
	return coord;
    }

    /**
     * Relocates all links connected to this node, they will relocate the end of
     * the link that's connected to the node, to the nodes center coordinates.
     * 
     * @param node
     * @param x
     * @param y
     */
    private void relocateLinksConnectedToNode(SMTNodeView node, double x, double y) {

	List<SMTLink> linksOfNodeBeingDragged = node.getAllLinks();

	int i = 0;
	for (SMTLink l : linksOfNodeBeingDragged) {
	    SMTLinkView view = linkDictionary.get(l);
	    if (node.isLinkStart(view.getLink())) {
		view.setStartX(roundVisualCoordinateForLink(x));
		view.setStartY(roundVisualCoordinateForLink(y));
	    } else {
		view.setEndX(roundVisualCoordinateForLink(x));
		view.setEndY(roundVisualCoordinateForLink(y));
	    }
	}
    }

    public void nodeWasDroppedAfterDragMove() {
	if (isUpdating)
	    return;
	if (!isDraggingNodesAllowed())
	    return;

	nodeDragInProgress = false;

	// Recalculate data
	tree.recalculate();
	// updateOutput();

	// Redraw tree, cache scroll position
	parent.cacheScroll();
	draw();
	parent.restoreScrollFromCache();
    }

    /**
     * Gets all the link views corresponding with the SMTLink objects in the
     * given list
     * 
     * @param links
     *            the list of SMTLink s
     * @return the corresponding link views
     */
    public List<SMTLinkView> getLinkViews(List<SMTLink> links) {
	List<SMTLinkView> linkViews = new ArrayList<SMTLinkView>();
	for (SMTLink l : links)
	    linkViews.add(linkDictionary.get(l));
	return linkViews;
    }

    /**
     * Called when a link is removed, links can only be removed in remove mode
     * 
     * @param id1
     * @param id2
     */
    public void linkWasRemoved(int id1, int id2) {
	if (componentType != Components.REMOVECURSOR)
	    return;

	tree.removeLink(id1, id2);
	tree.recalculate();
	updateOutput();
	draw();
    }

    /**
     * Called by SMTLinkView to see if it can highlight as red, should only
     * highlight as red if it's removable by clicking
     * 
     * @return
     */
    boolean canRemoveLink() {
	return this.componentType == Components.REMOVECURSOR;
    }

    /**
     * Called when a button in the controls view is clicked
     * 
     * @param type
     */
    public void buttonClicked(Buttons type) {
	if (tree == null)
	    return;

	switch (type) {
	case CLEAR:
	    clear();
	    break;
	case REMOVE_DESTINATIONS:
	    removeDestinations();
	    break;
	case REMOVE_NONDESTINATIONS:
	    removeNonDestinations();
	    break;
	case REMOVE_LINKS:
	    removeLinks();
	    break;
	case RELOAD_CACHED_TREE:
	    reloadCachedTree();
	    break;
	default:
	    ;
	}
    }

    private void clear() {
	tree.clear();
	draw();
    }

    private void removeDestinations() {
	tree.removeDestinations();
	draw();
    }

    private void removeNonDestinations() {
	tree.removeNonDestinations();
	draw();
    }

    private void removeLinks() {
	tree.removeLinks();
	draw();
    }

    private void reloadCachedTree() {
	tree = SMTParser.getCachedTree();
	draw();
    }

    public void fileWasDropped(File file) {
	SMTParser.parseFromFile(file);
	if (SMTParser.didParseSuccessfully()) {
	    tree = SMTParser.getCachedTree();
	    draw();
	}
    }

    public void saveButtonClicked() {
	SMTEditor editor = (SMTEditor) getScene();
	editor.saveTree(tree);
    }

    public void inputDidChange(double value, InputViewType type) {
	// update tree etc
	if (tree == null)
	    return;

	tree.updateValue(value, type);
	updateOutput();
    }

    public void toggleButtonClicked(ToggleButtons type, boolean isSelected) {
	// right now there is only discrete mode but use type if more is added
	setDiscreteMode(isSelected);
    }

    private boolean isInDiscreteMode = false;

    private void setDiscreteMode(boolean isSelected) {
	isInDiscreteMode = isSelected;

	if (tree == null)
	    return;

	tree.setDiscreteMode(isSelected, modelDiscreteCellSize);
	tree.recalculate();
	// System.out.println("cell size = " + modelDiscreteCellSize);
	grid.setVisible(isSelected);
	draw();
	// if (isSelected) {
	// draw();
	// } else {
	// grid.setVisible(false);
	// draw();
	// }
    }

    public void checkBoxClicked(CheckBoxes type, boolean isSelected) {
	// only "show node id" type implemented at the moment
	showNodeIds(isSelected);
    }

    private boolean isShowingNodeIds = false;

    private void showNodeIds(boolean isSelected) {
	isShowingNodeIds = isSelected;
	int index = 0;
	for (SMTNodeView view : this.nodeDictionary.values()) {
	    Label l = nodeLabels.get(index++);
	    l.setVisible(isSelected);
	    if (isSelected) {
		l.setText(Integer.toString(view.getNodeId()));
		Point2D center = view.getLabelAnchor();
		l.relocate(center.getX(), center.getY());
	    }
	}

	while (index < nodeLabels.size())
	    nodeLabels.get(index++).setVisible(false);
    }

    private RadioButtons selectedRadioButton;

    public void radioButtonClicked(RadioButtons type) {
	selectedRadioButton = type;

	if (type == RadioButtons.NOTHING) {
	    hideALlLabels();
	} else if (type == RadioButtons.LINK_COST) {
	    showLinkCosts();
	} else if (type == RadioButtons.LINK_LENGTH) {
	    showLinkLengths();
	} else {
	    // should never happen
	}
    }

    private void hideALlLabels() {
	if (this.linkLabels.isEmpty())
	    return;

	for (Label l : this.linkLabels) {
	    l.setVisible(false);
	}
    }

    private void showLinkCosts() {
	if (this.linkLabels.isEmpty())
	    return;

	int index = 0;
	for (SMTLink l : tree.getAllDistinctLinks()) {
	    Point2D linkCenter = this.linkDictionary.get(l).getCenter();
	    Label label = linkLabels.get(index++);
	    label.setText(tree.getLinkCost(l));
	    label.setVisible(true);
	    label.relocate(linkCenter.getX(), linkCenter.getY());
	}

	// hide the rest of the labels
	while (index < linkLabels.size())
	    linkLabels.get(index++).setVisible(false);
    }

    private void showLinkLengths() {
	if (this.linkLabels.isEmpty())
	    return;

	int index = 0;
	for (SMTLink l : tree.getAllDistinctLinks()) {
	    Point2D linkCenter = this.linkDictionary.get(l).getCenter();
	    Label label = linkLabels.get(index++);
	    label.setVisible(true);
	    label.setText(tree.getLinkLength(l));
	    label.relocate(linkCenter.getX(), linkCenter.getY());
	}

	// hide the rest of the labels
	while (index < linkLabels.size())
	    linkLabels.get(index++).setVisible(false);
    }

    private int modelDiscreteCellSize = 1;

    public void cellSizeDidChange(int intValue) {
	modelDiscreteCellSize = intValue;
	tree.setDiscreteMode(isInDiscreteMode, intValue);
	if (!isInDiscreteMode)
	    return;
	draw();
	updateOutput();
    }

    private double calculateCellSizeForCellDimension(int dimension) {
	return this.zoom * visualPlaneDimension * dimension / referenceDimension;
    }

    public void referenceNodeDimensionDidChange(int newReferenceNodeDimension) {
	this.referenceNodeDimension = newReferenceNodeDimension;
	referenceNodeDimensionDidChange();
    }

    public void referenceDimensionDidChange(int newReferenceDimension) {
	// System.out.println("old ref dimension = " + referenceDimension + ",
	// old ref node dimension = " + this.referenceNodeDimension);
	double ratio = newReferenceDimension / this.referenceDimension;
	this.referenceDimension = newReferenceDimension;
	double newReferenceNodeDimension = this.referenceNodeDimension * ratio;
	referenceNodeDimensionDidChange(newReferenceDimension / 50);
    }

}
