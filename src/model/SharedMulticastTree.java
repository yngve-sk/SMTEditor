package model;

import java.util.ArrayList;
import java.util.Collection;
import utils.Dictionary;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;

import com.sun.javafx.image.impl.ByteIndexed.Getter;

import application_outputview.InputView.InputViewType;
import application_outputview.TextOutputView.OutputFields;
import javafx.geometry.Point2D;

@SuppressWarnings("unused")
/**
 * Represents a shared multicast tree
 * @author Yngve Sekse Kristiansen
 *
 */
public class SharedMulticastTree {

	public static final int KAPPA_DEFAULT = 1, ALPHA_DEFAULT = 2;
	
	private HashMap<Integer, SMTNode> nodes;
	private double cost;
	private Dictionary<SMTLinkKey, SMTLink> distinctLinks;
	private double calculationTime;
	
	private int numberOfDestinations;
	private double kappa = KAPPA_DEFAULT, alpha = ALPHA_DEFAULT;

	/**
	 * Initializes a SMT
	 * @param nodes
	 *     the list of nodes, destinations placed before non-destinations
	 * @param links
	 *     the list of the nodes neighbor lists, must be of same length as nodes and correspond to the nodes list.
	 * @param numberOfDestinations
	 *     number of destinations
	 */
	public SharedMulticastTree(List<Point2D> nodes, List<List<Integer>> links, int numberOfDestinations) throws IllegalArgumentException {
	    if(nodes.size() != links.size())
	        throw new IllegalArgumentException("Amount of nodes and amount of neighbor lists must be of same length, nodes.size() = " + nodes.size() + ", links.size() = " + links.size());

	    if(numberOfDestinations > nodes.size())
	        throw new IllegalArgumentException("Number of destination = " + numberOfDestinations + ", number of nodes = " + nodes.size());

	    IdTracker.reset();
	    
		this.nodes = new HashMap<Integer, SMTNode>();
		this.numberOfDestinations = numberOfDestinations;
		
		int i = 0;
		for(Point2D p : nodes) { // Convert the coordinates into nodes
		    SMTNode newNode = SMTNodeFactory.newNode(p.getX(), p.getY(), i++ < numberOfDestinations);
		    this.nodes.put(newNode.id, newNode);
		}

		i = 0; // Add neighbor lists to the nodes
		for(SMTNode n : this.nodes.values()) {
		    n.setNeighbors(links.get(i++));
		}

		distinctLinks = new Dictionary<SMTLinkKey, SMTLink>();

		recalculate();
	}



	/**
	 *
	 * @param id1
	 * @param id2
	 */
	public void addLink(int id1, int id2) {
		if(!(nodes.containsKey(id1) && nodes.containsKey(id2)))
			return;

		SMTLink newLink = new SMTLink(id1, id2);
		SMTLinkKey newLinkKey = new SMTLinkKey(id1, id2);
		
		if(distinctLinks.containsKey(newLinkKey))
			return;

		nodes.get(id1).addNeighbor(id2);
	    nodes.get(id2).addNeighbor(id1);
	   // distinctLinks.put(newLinkKey, newLink);
	    recalculate();
	}

	/**
	 * Removes a link
	 * @param l
	 */
	public void removeLink(int id1, int id2) {
	     nodes.get(id1).removeNeighbor(id2);
	     nodes.get(id2).removeNeighbor(id1);
	     distinctLinks.remove(new SMTLinkKey(id1, id2));
	     recalculate();
	}


	/**
	 *
	 * @return
	 *     All distinct links
	 */
	public List<SMTLink> getAllDistinctLinks() {
	    return distinctLinks.values();
	}

	/**
	 * Iterates over all the neighbor lists and copies the links into the set of distinct links
	 */
	private void updateLinks() {
	    distinctLinks.clear();
	    for(SMTNode n : nodes.values()) 
	    	for(SMTLink l : n.getAllLinks()) {
	    		SMTLinkKey key = new SMTLinkKey(l.id1, l.id2);
	    		if(!distinctLinks.containsKey(key))
	    			distinctLinks.put(key, l);
	    		else {
	    			nodes.get(l.id2).reverseLinkTo(l.id1);
	    		}
	    	}
	    
	}


	 /**
     * Adds a node to the tree. This will result in an increment of the node id tracker
     * @param x
     * @param y
     * @param isDestination
     * @param nextNodeId
     *     the upcoming id must be passed in here, i.e IdTracker.getNextNodeId()
     * @param neighbors
     *     the neighbor list, pass null if no neighbors
     */
    public void addNode(double x, double y, boolean isDestination, int nextNodeId, List<Integer> neighbors) {
        nodes.put(nextNodeId, SMTNodeFactory.newNode(x, y, isDestination));
        if(neighbors != null) {
            nodes.get(nextNodeId).setNeighbors(neighbors);
            // Update neighbor list of other nodes
            for(Integer i : neighbors)
                nodes.get(i).addNeighbor(nextNodeId);
        }
        else
            nodes.get(nextNodeId).setNeighbors(new ArrayList<Integer>());
        
        if(isDestination)
        	++numberOfDestinations;
	     recalculate();
    }

	/**
	 * Removes a node from the tree, from all its neighbors neighbor lists, all the links associated with the node.
	 * @param id
	 *     the node
	 */
	public void removeNode(Integer id) {
	    SMTNode n = nodes.get(id);
	    if(n.isDestination)
	    	--numberOfDestinations;

	    // Remove this node from all its neighbors neighbor list...
	    for(Integer i : n.getNeighboursWithinRange())
	        nodes.get(i).removeNeighbor(id);

	    // Remove links from distinctLinks
	    for(Integer i : n.getNeighboursWithinRange())
	        distinctLinks.remove(new SMTLinkKey(id, i));

	    // Remove node from this list
	    nodes.remove(id);
	    recalculate();
	}

    /**
     * Relocates a node
     * @param x
     * @param y
     * @param id
     */
    public void relocateNode(double x, double y, int id) {
        nodes.get(id).relocate(x, y);
	    recalculate();
    }

	/**
	 *
	 * @return
	 *     the total cost of the tree
	 */
	public double getCost() {
	    return this.cost;
	}

	/**
	 * @return
	 *     All the nodes, node cost and links lie within the sMTNodes (assuming a calculation has been done)
	 */
	public Collection<SMTNode> getNodes() {
	    return nodes.values();
	}

	/**
	 * Gets node with the given id
	 * @param id
	 * @return
	 */
    public SMTNode getNode(int id) {
        return nodes.get(id);
    }

    /**
     * Gets the neighbors of node with id.
     * @param nodeId
     * @return
     */
    public List<SMTNode> getNeighborsOfNode(int nodeId) {
        List<Integer> neighborsWithinRangeIds = nodes.get(nodeId).getNeighboursWithinRange();
        List<SMTNode> neighbors = new ArrayList<SMTNode>();

        for(Integer i : neighborsWithinRangeIds)
            neighbors.add(nodes.get(i));

        return neighbors;
    }

	/**
	 * Recalculates the value and returns the time it took in milliseconds.
	 * The clearing of node data is not counted into the calculation.
	 * @return
	 *     the time of the recalculation
	 */
    public void recalculate() {
        for(SMTNode n : nodes.values())
            n.recalculateData();
        
        updateLinks();
        updatePowerLevelsAndMostDistantLinks();
        
        validate();
        if(!isValid()) {
        	System.out.println("Not valid tree");
        	this.cost = -1;
        	return;
        }
        
        System.out.println("Valid tree!!");

        double start = System.currentTimeMillis();

        evaluate();

        double end = System.currentTimeMillis();

        calculationTime = end - start;
    }

    /**
     * Updates power levels and most distant links, ensuring they are stored in their 
     * respective nodes 
     */
    private void updatePowerLevelsAndMostDistantLinks() {
    	for(SMTNode n : nodes.values()) {
    		n.setPowerLevels(getPowerLevels(n.id));
    		n.setTwoMostDistant(twoMostDistant(n.id));
    	}
    }



	/**
     * Checks whether two nodes are linked
     * @param id1
     * @param id2
     * @return
     */
	private boolean isLinked(int id1, int id2) {
	    return nodes.get(id1).getNeighboursWithinRange().contains(id2);
	} 
 
	/**
	 *
	 * @param id
	 * @return
	 *     gets the two power levels of the node. Highest at index 0, second highest at index 1
	 */
	private double[] getPowerLevels(Integer id) {
	    double[] powerLevels = {-1,-1};
	    int[] mostDistant = twoMostDistant(id);

	    if(mostDistant[0] != -1)
	    	powerLevels[0] = powerCost(id, mostDistant[0]);
	    
	    if(mostDistant[1] != -1)
	    	powerLevels[1] = powerCost(id, mostDistant[1]);

	    return powerLevels;
	}

	/**
	 * Gets the distance between two nodes
	 * @param id1
	 * @param id2
	 * @return
	 */
	private double getDistanceBetween(int id1, int id2) {
	    SMTNode n1 = nodes.get(id1);
	    SMTNode n2 = nodes.get(id2);

	    return Math.sqrt(
	            Math.pow((n2.getX() - n1.getX()), 2)
	            + Math.pow((n2.getY() - n1.getY()), 2)
	            );
	}

	/**
	 * <code>p_ij = kappa*(dist_ij)^alpha</code>
	 * @param n1
	 * @param n2
	 * @return
	 *  	The power cost of the transmission, taking kappa and gamma into consideration.
	 */
	private double powerCost(int n1, int n2) {
	    SMTNode node1 = nodes.get(n1);
	    SMTNode node2 = nodes.get(n2);

	    double lx = node2.getX() - node1.getX();
	    double ly = node2.getY() - node1.getY();

	    double dist = Math.sqrt(Math.pow(lx, 2) + Math.pow(ly, 2));

		return kappa*Math.pow(dist, alpha);
	}
	

	/**
	 *
	 * @param id
	 * @return
	 *  	the two most distant nodes, the most distant at index 0, second most distant at index 1
	 */
	private int[] twoMostDistant(int id) {
		List<Integer> withinRange = nodes.get(id).getNeighboursWithinRange();
		int size = withinRange.size();

		int[] mostDistant = {-1, -1};

		if(size == 1) {
		    mostDistant[0] = withinRange.get(0);
		}


		if(size > 1) {
		    NodeNeighborDistanceFilter filter = new NodeNeighborDistanceFilter(nodes.get(id));
		    for(Integer neighborId : withinRange)
		        filter.runThroughFilter(nodes.get(neighborId));

		    mostDistant[0] = filter.getTwoMostDistantNodes()[0].id;
		    mostDistant[1] = filter.getTwoMostDistantNodes()[1].id;
		}

		return mostDistant;
	}


//	/**
//	 * Class to represent a subtree with minimal functionality
//	 * @author Yngve Sekse Kristiansen
//	 *
//	 */
//	private class Subtree {
//		private List<SMTNode> nodes;
//
//		/**
//		 * Initializes a new subtree
//		 * @param nodes
//		 *    the list of sMTNodes, can't be null or empty
//		 */
//		public Subtree(List<SMTNode> nodes) {
//			this.nodes = nodes;
//		}
//
//		public Subtree() {
//		    // empty subtree
//		}
//
//        /**
//		 *
//		 * @return
//		 *    The number of sMTNodes in the tree
//		 */
//		int size() {
//		    return (nodes == null) ? 0 : nodes.size();
//		}
//	}
//


	/**
	 * Tiny class to pass double values through. After passing
	 * n doubles through the filter, it will retain the two largest
	 * values. All values passed through must be >= 0.
	 * @author Yngve Sekse Kristiansen
	 *
	 */
	private class PowerFilter {

	    private double highest, secondHighest;

	    /**
	     * Initializes a new power filter.
	     */
	    public PowerFilter() {
	        highest = 0; secondHighest = -1;
	    }

	    /**
	     * Passes a double value through, if it is greater than the highest,
	     * or second highest (but not equal to) it'll be retained and stored.
	     * @param value
	     */
	    public void runThroughFilter(double value) {
	        if(value > highest) {
	            secondHighest = highest;
	            highest = value;
	        }
	        else if(value > secondHighest && value < highest)
	            secondHighest = value;
	    }

	    /**
	     *
	     * @return
	     *     The highest two values retained in the filter.
	     *     The highest value at index 0, second highest at index 1
	     */
	    public double[] getHighestTwo() {
	        double[] highestTwo = {highest, secondHighest};
	        return highestTwo;
	    }
	}



	public String getValueForField(OutputFields f) {
		switch(f) {
		case AVG_NODE_COST : return Double.toString(cost <= 0 ? 0 : cost/nodes.size());
		case AVG_LINK_LENGTH : return getAverageLinkLength();
		case MOST_EXPENSIVE_NODE : return getMostExpensiveNode();
		case LONGEST_LINK : return getLongestLink();
		case TOTAL_TREE_COST : return this.cost == -1 ? "Invalid SMT" : Double.toString(this.cost);
		case NUM_NODES : return Integer.toString(nodes.size());
		case NUM_LINKS : return Integer.toString(distinctLinks.values().size());
		case NUM_DESTINATIONS : return getNumDestinations();
		case NUM_NONDESTINATIONS : return getNumNonDestinations();
		case CALCULATION_TIME : return Double.toString(calculationTime);
		default : return null;
		}
	}



	private String getNumDestinations() {
		return Integer.toString(numberOfDestinations);
	}



	private String getNumNonDestinations() {
		return Integer.toString(nodes.size() - numberOfDestinations);
	}



	private String getLongestLink() {
		// Same as highest power level
		double highest = 0;
		for(SMTLink l : this.distinctLinks.values()) {
			double p = getDistanceBetween(l.id1, l.id2);
			if(p > highest)
				highest = p;
		}
			
		return Double.toString(utils.Math.trim(highest));
	}



	private String getMostExpensiveNode() {
		double mostExpensiveCost = 0;
		int mostExpensiveId = -1;
		for(SMTNode n : this.nodes.values()) {
			double cost = n.getNodeCost();
			if(cost > mostExpensiveCost) {
				mostExpensiveCost = cost;
				mostExpensiveId = n.id;
			}
		}
		
		return "#" + mostExpensiveId + " (" + mostExpensiveCost + ")";
	}



	private String getAverageLinkLength() {
		double totalLength = 0;
		for(SMTLink l : distinctLinks.values()) {
			totalLength += getDistanceBetween(l.id1, l.id2);
		}
		
		return Double.toString(totalLength/distinctLinks.values().size());
	}

	/**
	 * Resets all data, empties the tree
	 */
	public void clear() {
		nodes.clear();
		cost = 0;
		distinctLinks.clear();
		calculationTime = 0;
		numberOfDestinations = 0;
		IdTracker.reset();
	}



	public void nonDestinationsToDestinations() {
		for(SMTNode n : this.getNodes()) {
			if(n.isDestination)
				continue;
			SMTNode transform = SMTNodeFactory.transformNode(n);
			nodes.replace(n.id, transform);
		}
	}



	public void destinationsToNonDestinations() {
		for(SMTNode n : this.getNodes()) {
			if(!n.isDestination)
				continue;
			
			SMTNode transform = SMTNodeFactory.transformNode(n);
			nodes.replace(n.id, transform);
		}	
	}



	public SMTLink getHeaviestLink() {
		if(distinctLinks.isEmpty())
			return null;
		
		double highest = 0;
		SMTLink leader = null;
		for(SMTLink l : this.distinctLinks.values()) {
			double d = getDistanceBetween(l.id1, l.id2);
			if(d > highest) {
				highest = d;
				leader = l;
			}
		}
			
		return leader;
	}

	public void removeLinks() {
		for(SMTNode n : nodes.values())
			n.removeAllNeighbors();
		distinctLinks.clear();
	}



	public void removeNonDestinations() {
		for(SMTNode n : nodes.values()) {
			for(Integer neighbor : n.getNeighboursWithinRange())
				if(!nodes.get(neighbor).isDestination)
					n.removeNeighbor(neighbor);
		}
		
		List<Integer> toBeRemoved = new ArrayList<Integer>();
 		
		for(SMTNode n : nodes.values()) {
			if(!n.isDestination)
				toBeRemoved.add(n.id);
		}
		
		// Avoiding java.util.ConcurrentModificationException
		for(Integer i : toBeRemoved)
			nodes.remove(i);
	}



	public void removeDestinations() {
		for(SMTNode n : nodes.values()) {
			for(Integer neighbor : n.getNeighboursWithinRange())
				if(nodes.get(neighbor).isDestination)
					n.removeNeighbor(neighbor);
		}
		
		List<Integer> toBeRemoved = new ArrayList<Integer>();
 		
		for(SMTNode n : nodes.values()) {
			if(n.isDestination)
				toBeRemoved.add(n.id);
		}
		
		// Avoiding java.util.ConcurrentModificationException
		for(Integer i : toBeRemoved)
			nodes.remove(i);
	}


	/**
	 * Utility to help find the two most distant neighbors of a node
	 * @author Yngve Sekse Kristiansen
	 *
	 */
	private class NodeNeighborDistanceFilter {

	    private SMTNode origin;

	    private SMTNode furthest;
	    private double furthestDistance = 0;

	    private SMTNode nextFurthest;
	    private double nextFurthestDistance = 0;

	    /**
	     * Initializes a new filter, taking in origin node
	     * @param origin
	     */
	    public NodeNeighborDistanceFilter(SMTNode origin) {
	        this.origin = origin;
	    }

	    /**
	     * Gets the two most distant nodes, furthest at index 0, next furthest at index 1.
	     * @return
	     */
	    public SMTNode[] getTwoMostDistantNodes() {
	        SMTNode[] twoMostDistant = {furthest, nextFurthest};
            return twoMostDistant;
        }

        /**
	     * Runs a node through the filter, retains it if its distance is greater than the next biggest
	     * distance currently being retained
	     * @param node
	     *     the node
	     */
	    public void runThroughFilter(SMTNode node) {

	        double dist = distanceTo(node);
	        
	        if(dist > furthestDistance) {
	        	nextFurthest = furthest;
	        	nextFurthestDistance = furthestDistance;
	        	
	        	furthest = node;
	            furthestDistance = dist;
	        }
	        else if(dist > nextFurthestDistance && dist < furthestDistance) {
	            nextFurthest = node;
	            nextFurthestDistance = dist;
	        }
	    }

	    private double distanceTo(SMTNode neighbor) {
	        double dx = neighbor.getX() - origin.getX();
	        double dy = neighbor.getY() - origin.getY();

	        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
	    }
	}



	public int getNumberOfDestinations() {
		return numberOfDestinations;
	}

	public int getNumberOfNonDestinations() {
		return nodes.size() - numberOfDestinations;
	}
	
	public List<SMTNode> getAllDestinations() {
		List<SMTNode> list = new ArrayList<SMTNode>();
		for(SMTNode n : nodes.values()) {
			if(n.isDestination)
				list.add((Destination) n);
		}
		return list;
	} 
	
	public List<SMTNode> getAllNonDestinations() {
		List<SMTNode> list = new ArrayList<SMTNode>();
		for(SMTNode n : nodes.values()) {
			if(!n.isDestination)
				list.add((NonDestination) n);
		}
		return list;
	}

	/************ COST ALGORITHM ************/
	
	public double evaluate() {
		if(isValidButNotLinked()) {
			System.out.println("Valid but not linked");
			this.cost = 0;
			return 0;
		}
		
		double myCost = 0;
		int nod = this.getNumberOfDestinations();
		calculateSubtrees(findSomeLeafLinkKey(), nod);
		// subtree size and opposite subtree size should now be stored in each link
		
		for(SMTNode n : nodes.values()) 
			if(n.getMostDistant() != -1) { // same as if j has neighbors or not?
				SMTLinkKey linkKey = new SMTLinkKey(n.id, n.getMostDistant());
				if(!( !n.isDestination && n.isLeaf() )) { // if n isn't a non-destination leaf
					double nCost = n.getCost(nod, distinctLinks.get(linkKey));  // pass link with subtree info to node
					myCost += nCost;
				}
			}
		
		updateAllNonDestinationLeaves(myCost);
		
		this.cost = myCost;
		return myCost;
	}
	
	
	private void updateAllNonDestinationLeaves(double myCost) {
		for(SMTNode n : getAllNonDestinations())
			if(n.isLeaf())
				n.setNodeCost(myCost);
	}



	private int calculateSubtrees(SMTLinkKey linkKey, int nod) {
		SMTLink link = distinctLinks.get(linkKey);
		int id1 = linkKey.id1;
		int id2 = linkKey.id2;
		
		SMTNode n2 = nodes.get(id2);
		
		int subtreeSize = 0;
		if(n2.isDestination)
			subtreeSize = 1;
		
		if(n2.getNeighboursWithinRange().size() == 1) {
			link.setOppositeSubtreeSize(subtreeSize); 
			link.setSubtreeSize(nod - subtreeSize);
			return subtreeSize;
		}
		
		for(Integer i : n2.getNeighboursWithinRange())
			if((i != id1)) 
				subtreeSize += calculateSubtrees(new SMTLinkKey(id2, i), nod);
			
		link.setOppositeSubtreeSize(subtreeSize);
		link.setSubtreeSize(nod - subtreeSize);
		
		return subtreeSize;
	}
	
	private void printDistinctLinks() {

		System.out.println("----------------");
		System.out.println("----------------");
		System.out.println("----------------");
		
		System.out.println("NUM LINKS = " + distinctLinks.values().size());

		System.out.println("----------------");
		
		for(SMTLink k : distinctLinks.values())
			System.out.println(k);
		
		System.out.println("----------------");
	}
	
	/**
	 * Finds the key for a leaf link
	 * @return
	 */
	private SMTLinkKey findSomeLeafLinkKey() {
		for(SMTNode n : nodes.values()) 
			if(n.getNeighboursWithinRange().size() == 1) {
				SMTLinkKey key = new SMTLinkKey(n.id, n.getNeighboursWithinRange().get(0));
				return key;
			}
		return null;
	}
	
	private boolean isValidSMT = false;
	
	public boolean isValid() {
		return isValidSMT;
	}
	
	public void validate() {
		int numNodes = this.nodes.size();
		int numLinks = this.distinctLinks.values().size();
		int numLeafs = 0;
		int singletons = 0;
		
		if(numNodes <= numLinks) { // Cycle detected
			isValidSMT = false;
			return;
		}
		
		if(isValidButNotLinked()) { System.out.println("isValidButNotLinked()");
			isValidSMT = true;
			return;
		}
		
		for(SMTNode n : this.nodes.values()) {
			if(n.getNeighboursWithinRange().size() == 0) {
				if(n.isDestination) { System.out.println("if(n.isDestination)");
					isValidSMT = false;
					return;
				}
				singletons++;
			}
			
			if(n.getNeighboursWithinRange().size() == 1) 
					numLeafs++;
		}
				
		if(numLinks >= numNodes - singletons) { System.out.println("numLinks >= numNodes - singletons");
			isValidSMT = false;
			return;
		}
		
		if(numLeafs == 0) { System.out.println("numLeafs == 0");
			isValidSMT = false;
			return;
		}
		
		
		if(distinctLinks.isEmpty()) { System.out.println("distinctLinks.isEmpty()");
			isValidSMT = false;
			return;
		}			
		
		if(!isAllDestinationsConnected()) { System.out.println("if(!isAllDestinationsConnected())");
			isValidSMT = false;
			return;
		}
			
		
		isValidSMT = true;
	}
	
	private boolean isValidButNotLinked() {
		return this.distinctLinks.isEmpty() && getAllDestinations().size() <= 1;
	}
	
	private boolean isAllDestinationsConnected() {
		// 1. Find a destination
		SMTNode start = null;;
		for(SMTNode n : this.nodes.values())
			if(n.isDestination) {
				start = n;
				break;
			}
		
		if(start == null)
			return false;
		
		// 2. Do a dfs from n and count up num destinations
		Stack<SMTNode> stack = new Stack<SMTNode>();
		Stack<SMTNode> unvisited = new Stack<SMTNode>();
		for(SMTNode n : nodes.values())
			unvisited.add(n);
		
		stack.push(start);
		unvisited.remove(start);
		
		int count = 1;
		
		while(!stack.isEmpty()) {
			SMTNode current = stack.pop();
			
			for(Integer i : current.getNeighboursWithinRange()) {
				SMTNode neighbor = nodes.get(i);
				
				if(!unvisited.contains(neighbor))
					continue;
				
				stack.push(neighbor);
				unvisited.remove(neighbor);
				
				if(neighbor.isDestination)
					count++;
			}	
		}
		return count == getAllDestinations().size();
	}



	public void updateValue(double value, InputViewType type) {
		if(type == InputViewType.ALPHA)
			this.alpha = value;
		else if(type == InputViewType.KAPPA)
			this.kappa = value;
		
		recalculate();
	}
	
}
