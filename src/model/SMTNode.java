package model;

import java.util.ArrayList;
import java.util.List;

public abstract class SMTNode {

    private int sortingId; // Used when finding subtree

    public final int id;
	private double highestPowerLevel, lowestPowerLevel;
	private double nodeCost;
	private double x, y;

	public final boolean isDestination;
	private List<Integer> neighborsWithinRange;
	private List<SMTLink> allLinks = new ArrayList<SMTLink>(); // this is implicit in neighbors, but it's a way to
	                                                     // store the results instead of having a huge structure in the GUI
	/**
	 * Initializes a new node
	 * @param x
	 *     the x-coordinate
	 * @param y
	 *     the y-coordinate
	 * @param isDestination
	 *     true if it's a destination
	 * @param id
	 *     the node id
	 */
	public SMTNode(double x, double y, boolean isDestination, int id) {
	    this.x = x;
	    this.y = y;

	    this.isDestination = isDestination;

	    allLinks = new ArrayList<SMTLink>();

	    this.id = id;
	}
	
	/**
	 * Creates a copy SMTNode, used for transforming destinations to non destinations and vice versa
	 * @param data
	 * @param isDestination
	 */
	public SMTNode(SMTNode data, boolean isDestination) {
		this.id = data.id;
		this.isDestination = isDestination;
		
		this.highestPowerLevel = data.highestPowerLevel;
		this.lowestPowerLevel = data.lowestPowerLevel;
		this.nodeCost = data.nodeCost;
	
		this.x = data.x;
		this.y = data.y;
		
		this.neighborsWithinRange = data.neighborsWithinRange;
		this.allLinks = data.allLinks;
	}
	
	/**
	 * Recalculates data on this node (resets cost, highest and second highest power level), and recalculates links
	 */
	void recalculateData() {
	    this.nodeCost = 0;
	    this.highestPowerLevel = 0;
	    this.lowestPowerLevel = 0;
	    allLinks.clear();
	    updateLinks();
    }

	private void updateLinks() {
		allLinks.clear();
        for(Integer neighbor : neighborsWithinRange)
            allLinks.add(new SMTLink(this.id, neighbor));
	}

	public void checkState() {
	    assert neighborsWithinRange != null;
	    assert allLinks != null;
	}

	/**
	 * Gets all the links. This might return an empty list, that would mean the node has no neighbors / no links.
	 * @return
	 *     all the links
	 */
	public List<SMTLink> getAllLinks() {
	    return allLinks;
	}




	/**
	 *
	 * @return
	 *     a string representation of the nodes position in form (x,y)
	 */
	public String getPosition() {
	    return "(" + utils.Math.trim(x) + ", " + utils.Math.trim(y) + ")";
	}


	public String getType() {
	   return isDestination ? "Destination" : "Non-Destination";
	}

	/**
	 * Stores the neighbour list in the node
	 * @param neighborsWithinRange
	 */
	public void setNeighbors(List<Integer> neighborsWithinRange) {
	    this.neighborsWithinRange = neighborsWithinRange;
	}

	/**
	 *
	 * @return
	 *     the neighbours within range currently stored in this node
	 */
	List<Integer> getNeighboursWithinRange() {
	    return this.neighborsWithinRange;
	}

	/**
	 * Stores the power levels in the node
	 * @param powerLevels
	 *     the power levels, highest power level at index 1, next highest at index 0
	 */
	void setPowerLevels(double[] powerLevels) {
	    highestPowerLevel = utils.Math.trim(powerLevels[1]);
	    lowestPowerLevel = utils.Math.trim(powerLevels[0]);
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


    @Override // not taking neighbor list and all links list into consideration for performance
    public boolean equals(Object obj) {
        SMTNode other = (SMTNode) obj;
        return this.id == other.id;
    }

    /**
     * Adds a neighbor to the node (neighbor must already exist in tree before adding it)
     * @param neighborId
     *      the node
     */
    public void addNeighbor(int neighborId) {
    	if(neighborsWithinRange.contains(neighborId))
    		return;

        neighborsWithinRange.add(neighborId);
        updateLinks();
    }

    /**
     * Removes a neighbor from the node
     * @param id
     *      the id
     */
    public void removeNeighbor(Integer id) { // Integer so it calls remove(Object) and not remove(index)
        neighborsWithinRange.remove(id);
        updateLinks();
    }

    /**
     * Relocates a node
     * @param x
     *      the new x coordinate
     * @param y
     *      the new y coordinate
     */
    public void relocate(double x, double y) {
        this.x = x;
        this.y = y;
    }

    public int getSortingId() {
        return sortingId;
    }

    public void setSortingId(int sortingId) {
        this.sortingId = sortingId;
    }

	public void removeAllNeighbors() {
		neighborsWithinRange.clear();
		allLinks.clear();
//		highestPowerLevel = 0; 
//		lowestPowerLevel = 0;
	}

	/************ COST ALGORITHM ************/
	public double getCost(int nod, SMTNode j1) {
		int subtreeSize = j1.getSubtreeSize();
		
		return subtreeSize*getHighestPowerLevel() + (nod - subtreeSize)*getSecondHighestPowerLevel();
	}

	private int getSubtreeSize() {
		// TODO ?
		return 0;
	}


}
