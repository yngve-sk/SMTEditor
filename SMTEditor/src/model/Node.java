package model;

import java.util.List;

public abstract class Node {

	private double highestPowerLevel, lowestPowerLevel;
	private double nodeCost;
	private double x, y;

	private boolean isDestination;
	private List<Node> neighborsWithinRange;

	/**
	 * Resets all data on this node (cost, neighbours within range, highest and second highest power level)
	 */
	void flushData() {
	    this.neighborsWithinRange = null;
	    this.nodeCost = 0;
	    this.highestPowerLevel = 0;
	    this.lowestPowerLevel = 0;
	}

	/**
	 * Initializes a new node
	 * @param isDestination
	 */
	public Node(boolean isDestination) {
	    this.isDestination = isDestination;
	}

	/**
	 *
	 * @return
	 *     true if the node is a destination, false if it's a nondestination
	 */
	public boolean isDestination() {
	    return isDestination;
	}

	/**
	 * Stores the neighbour list in the node
	 * @param neighborsWithinRange
	 */
	public void setNeighbors(List<Node> neighborsWithinRange) {
	    this.neighborsWithinRange = neighborsWithinRange;
	}

	/**
	 *
	 * @return
	 *     the neighbours within range currently stored in this node
	 */
	public List<Node> getNeighboursWithinRange() {
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

	double getHighestPowerLevel() {
        return this.highestPowerLevel;
    }

	double getLowestPowerLevel() {
        return this.lowestPowerLevel;
    }

	double getNodeCost() {
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
