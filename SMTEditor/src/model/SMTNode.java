package model;

import java.util.ArrayList;
import java.util.List;

public abstract class SMTNode {

	private double highestPowerLevel, lowestPowerLevel;
	private double nodeCost;
	private double x, y;

	private boolean isDestination;
	private List<SMTNode> neighborsWithinRange;
	private List<SMTLink> allLinks = new ArrayList<SMTLink>(); // this is implicit in neighbors, but it's a way to
	                                                     // store the results instead of having a huge structure in the GUI

	private boolean linksAreUpdated = false; // a node might have 0 links so isEmpty/null checking isn't sufficent

	/**
	 * Resets all data on this node (cost, neighbours within range, highest and second highest power level)
	 */
	void flushData() {
	    this.neighborsWithinRange = null;
	    this.nodeCost = 0;
	    this.highestPowerLevel = 0;
	    this.lowestPowerLevel = 0;
	    allLinks.clear();
	    linksAreUpdated = false;
	}

	/**
	 * Gets all the links. This might return an empty list, that would mean the node has no neighbors / no links.
	 * @return
	 *     all the links
	 */
	public List<SMTLink> getAllLinks() {
	    if(!linksAreUpdated) {
	        for(SMTNode neighbor : neighborsWithinRange)
	            allLinks.add(new SMTLink(this, neighbor, !isDestination));
	        linksAreUpdated = true;
	    }
	    return allLinks;
	}

	/**
	 * Initializes a new node
	 * @param isDestination
	 */
	public SMTNode(double x, double y, boolean isDestination) {
	    this.x = x;
	    this.y = y;

	    this.isDestination = isDestination;
	}

	/**
	 *
	 * @return
	 *     a string representation of the nodes position in form (x,y)
	 */
	public String getPosition() {
	    return "(" + x + ", " + y + ")";
	}

	/**
	 *
	 * @return
	 *     true if the node is a destination, false if it's a nondestination
	 */
	public boolean isDestination() {
	    return isDestination;
	}

	public String getType() {
	   return isDestination ? "Destination" : "Non-Destination";
	}

	/**
	 * Stores the neighbour list in the node
	 * @param neighborsWithinRange
	 */
	public void setNeighbors(List<SMTNode> neighborsWithinRange) {
	    this.neighborsWithinRange = neighborsWithinRange;
	}

	/**
	 *
	 * @return
	 *     the neighbours within range currently stored in this node
	 */
	public List<SMTNode> getNeighboursWithinRange() {
	    return this.neighborsWithinRange;
	}

	/**
	 * Stores the power levels in the node
	 * @param powerLevels
	 *     the power levels, highest power level at index 0, next highest at index 1
	 */
	void setPowerLevels(double[] powerLevels) {
	    highestPowerLevel = powerLevels[0];
	    lowestPowerLevel = powerLevels[1];
	}

	/**
	 * Stores the node cost value in the node
	 * @param nodeCost
	 *     the node cost
	 */
	void setNodeCost(double nodeCost) {
	    this.nodeCost = nodeCost;
	}

	public double getHighestPowerLevel() {
        return this.highestPowerLevel;
    }

	public double getSecondHighestPowerLevel() {
        return this.lowestPowerLevel;
    }

	public double getNodeCost() {
	    return this.nodeCost;
	}

    public double getX() {
        return x;
    }

    public void setX(double x) {
        this.x = x;
    }

    public double getY() {
        return y;
    }

    public void setY(double y) {
        this.y = y;
    }


}
