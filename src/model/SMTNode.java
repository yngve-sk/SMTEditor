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
	    this.mostDistant = -1;
	    this.secondMostDistant = -1;
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



	private int roundModelForDiscreteCellSize(double coord) {
		int cellSize = Mode.getDiscreteCellSize();
		
		int intCoord = (int) coord;
		
		if(intCoord%cellSize < cellSize/2 || (intCoord >= Mode.defaultDimension - cellSize/2)) { // round down
			return intCoord - (intCoord%cellSize);
		}
		else if(intCoord < cellSize) { // Round up so it's "in" in the grid
			return cellSize; 
		}
		else { // round up if it's "past" the mid point
			return intCoord - (intCoord%cellSize) + cellSize;
		}
	}

	private double roundVisualForDiscreteCellSize(double coord) {
		int cellSize = Mode.getDiscreteCellSize();
		
		System.out.println("actual coord : " + coord + ", round visual, cellSize = " + cellSize);
		
		double lowerOption = coord - (coord % cellSize);
		double higherOption = lowerOption + cellSize;
		double d = cellSize*0.5;
		
		System.out.println("lower : " + lowerOption + ", higher : " + higherOption);
		
		if(coord%cellSize == 0)
			return coord + d;
		else if(coord - lowerOption < higherOption - coord) {
			System.out.println("returning lowerOption + d = " + (lowerOption + d));
			return lowerOption + d;
		} 
		else {
			System.out.println("returning higherOption + d = " + (higherOption + d));
			return higherOption + d;
		}
	}

	/**
	 *
	 * @return
	 *     a string representation of the nodes position in form (x,y)
	 */
	public String getPosition() {
	    return Mode.isInDiscreteMode() ? "(" + roundModelForDiscreteCellSize(x) + ", " + roundModelForDiscreteCellSize(y) + ")" : 
	    	"(" + utils.Math.trim(x) + ", " + utils.Math.trim(y) + ")";
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
	 *     the power levels, highest power level at index 0, next highest at index 1
	 */
	void setPowerLevels(double[] powerLevels) throws IllegalArgumentException {
		if(powerLevels[1] != -1 && powerLevels[0] < powerLevels[1])
			throw new IllegalArgumentException("FROM NODE " + this.id + "Invalid power levels, expected biggest at index 0");
	    highestPowerLevel = utils.Math.trim(powerLevels[0] == -1 ? 0 : powerLevels[0]);
	    lowestPowerLevel = utils.Math.trim(powerLevels[1] == -1 ? 0 : powerLevels[1]);
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
	
	/**
	 * Gets the visual X, to display correctly in grid under discrete mode it must have
	 * double coordinates, yet "pretend" to have integer coordinates model-wise
	 * @return
	 */
	public double getVisualX() {
        return Mode.isInDiscreteMode() ? roundVisualForDiscreteCellSize(x) : x;
	}
	
	/**
	 * Gets the visual Y, to display correctly in grid under discrete mode it must have
	 * double coordinates, yet "pretend" to have integer coordinates model-wise
	 * @return
	 */
	public double getVisualY() {
        return Mode.isInDiscreteMode() ? roundVisualForDiscreteCellSize(y) : y;
	}

	/**
	 * Gets x used in MODEL operations, for graphical display getVisualX() must be called
	 * @return
	 */
    public double getX() {
        return Mode.isInDiscreteMode() ? roundModelForDiscreteCellSize(x) : x;
    }

    public void setX(double x) {
        this.x = x;
    }

	/**
	 * Gets y used in MODEL operations, for graphical display getVisualY() must be called
	 * @return
	 */
    public double getY() {
        return Mode.isInDiscreteMode() ? roundModelForDiscreteCellSize(y) : y;
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
	
	private int mostDistant = -1;
	private int secondMostDistant = -1;
	
	public void setTwoMostDistant(int[] twoMostDistant) {
		mostDistant = twoMostDistant[0];
		secondMostDistant = twoMostDistant[1];
	}
	
	int getMostDistant() {
		return mostDistant;
	}

	int getSecondMostDistant() {
		return secondMostDistant;
	}
	
	/**
	 * Reverses the link to node with given id, this is done to keep the data up to sync with the
	 * way it's presented as a set of distinct links.
	 * @param id
	 */
	public void reverseLinkTo(int id) {
		SMTLink reverse = new SMTLink(id, this.id);
		this.allLinks.remove(reverse);
		this.allLinks.add(reverse);
	}
	
	public boolean isLeaf() {
		return this.neighborsWithinRange.size() == 1;
	}
	
	/************ COST ALGORITHM ************/
	public double getCost(int nod, SMTLink j1) {
		if(this.allLinks.isEmpty()) {
			this.nodeCost = 0;
			return this.nodeCost;
		}
						
		int subtreeSize = j1.getSubtreeSize(this.id); // passing in this.id,
		// if this.id is the id1 (start) of link, then it gets the subtree size, else it gets the "opposite" subtree size
		
		this.nodeCost = subtreeSize*getHighestPowerLevel() + (nod - subtreeSize)*getSecondHighestPowerLevel();
		return this.nodeCost;
	}
	
	/**
	 * Stores the node cost value in the node. This is useful on non-destination leaves, which need manual updating
	 * of value after calculation is done
	 * @param nodeCost
	 *     the node cost
	 */
	void setNodeCost(double nodeCost) {
	    this.nodeCost = nodeCost;
	}



	


}
