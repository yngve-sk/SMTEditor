package model;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;

@SuppressWarnings("unused")
/**
 * Represents a shared multicast tree
 * @author Yngve Sekse Kristiansen
 *
 */
public class SharedMulticastTree {

	private List<SMTNode> nodes;
	private double cost;
	private List<SMTLink> distinctLinks;

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

		this.nodes = new ArrayList<SMTNode>();

		int i = 0;
		for(Point2D p : nodes)
		    this.nodes.add(SMTNodeFactory.newNode(p.getX(), p.getY(), i++ < numberOfDestinations));

		i = 0;
		for(SMTNode n : this.nodes) {
		    List<SMTNode> neighbors = new ArrayList<SMTNode>();
		    for(Integer j : links.get(i++))
		        neighbors.add(this.nodes.get(j));

		    n.setNeighbors(neighbors);
		}

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
	 *     All the sMTNodes, node cost and links lie within the sMTNodes (assuming a calculation has been done)
	 */
	public List<SMTNode> getNodes() {
	    return nodes;
	}


	/**
	 * Recalculates the value and returns the time it took in milliseconds.
	 * The clearing of node data is not counted into the calculation.
	 * @return
	 *     the time of the recalculation
	 */
    public double recalculate() {
        for(SMTNode n : nodes)
            n.flushData();

        distinctLinks.clear();

        double start = System.currentTimeMillis();

        for(SMTNode n : nodes) {
//          n.setNeighbors(links.get(nodes.indexOf(n)));
            n.setPowerLevels(getPowerLevels(n));
            n.setNodeCost(getCost(n));
        }

        calculateTotalCost();

        double end = System.currentTimeMillis();

        return end - start;
    }

    /**
     * Checks the adjacency matrix to see whether or not n1 and n2 are linked together
     * @param n1
     * @param n2
     * @return
     *      yes if n1 and n2 is linked together
     */
	private boolean isLinked(SMTNode n1, SMTNode n2) {
	    return n1.getNeighboursWithinRange().contains(n2);
	}

	/**
	 *
	 * @param n
	 * @return
	 *     gets the two power levels of the node.
	 */
	private double[] getPowerLevels(SMTNode n) {
	    // 1. Find all sMTNodes linked to n
	    List<SMTNode> linked = n.getNeighboursWithinRange();

	    // 2. Calculate distance between n and all sMTNodes, store highest two distances
	    PowerFilter filter = new PowerFilter(); // run all distances through filter, highest two will remain
	    for(SMTNode sMTNode : linked)
	        filter.runThroughFilter(getDistanceBetween(n, sMTNode));

	    return filter.getHighestTwo();
	}

	private List<SMTNode> getNodesLinkedTo(SMTNode n) throws IllegalStateException {
	    if(n.getNeighboursWithinRange() != null)
	        throw new IllegalStateException("n already has a set neighbor list. This call is redundant "
	                + "OR the node did not get flushed before recalculating.");

	    List<SMTNode> linked = new ArrayList<SMTNode>();

        for(SMTNode sMTNode : nodes)
            if(n != sMTNode && isLinked(n, sMTNode))
                linked.add(n);

        return linked;
	}

	private double getDistanceBetween(SMTNode n1, SMTNode n2) {
	    return Math.sqrt(
	            Math.pow((n2.getX() - n1.getX()), 2)
	            + Math.pow((n2.getY() - n1.getY()), 2)
	            );
	}

	/**
	 *
	 * @param sender
	 * @param receiver
	 * @return
	 *  	The power cost of the transmission
	 */
	private double powerCost(SMTNode sender, SMTNode receiver) {
	    double lx = receiver.getX() - sender.getX();
	    double ly = receiver.getY() - sender.getY();

	    double dist = Math.sqrt(Math.pow(lx, 2) + Math.pow(ly, 2));

		return dist;
	}

//	/**
//	 *
//	 * @param sender
//	 * @param receiver
//	 * @return
//	 *  	true if receiver is within senders range
//	 */
//	private boolean isWithinRange(SMTNode sender, SMTNode receiver) {
//		return sender.getNeighboursWithinRange().contains(receiver);
//	}

	/**
	 *
	 * @param sender
	 * @param receiver
	 * @return
	 */
	private Subtree arc(SMTNode sender, SMTNode receiver) {

		List<SMTNode> nodesWithinSendersRange = nodesWithinRange(sender);
		int indexOfReceiver = nodesWithinSendersRange.indexOf(sender);

		return new Subtree(nodesWithinSendersRange.subList(0, indexOfReceiver));
	}


	/**
	 * @param n
	 *  	the node
	 * @return
	 *  	The sMTNodes within range of node n. The closest node
	 * 		will be at index 0, whilst the node furthest away will be at
	 * 		the last index.
	 */
	private List<SMTNode> nodesWithinRange(SMTNode n) {
		// TODO
		return null;
	}

	private Subtree arc(SMTNode n) {
		List<SMTNode> receivers = nodesWithinRange(n);
		SMTNode receiver = receivers.get(receivers.size() - 1);
		return arc(n, receiver);
	}

	/**
	 *
	 * @param n
	 * @return
	 *  	the two most distant sMTNodes, the most distant at index 1, second most distant at index 0
	 */
	private SMTNode[] mostDistant(SMTNode n) {
		List<SMTNode> withinRange = nodesWithinRange(n);
		int size = withinRange.size();

		SMTNode[] mostDistant = {withinRange.get(size - 2), withinRange.get(size - 1)};
		return mostDistant;
	}

	/**
	 *
	 * @param n
	 * @return
	 *     the cost of n
	 */
	private double getCost(SMTNode n) {
		int numberOfDestinationsSubtree = arc(n).sMTNodes.size();

		SMTNode[] mostDistantN = mostDistant(n);
		double costMostDistant = powerCost(n, mostDistantN[1]);
		double costSecondMostDistant = powerCost(n, mostDistantN[0]);

		int numberOfDestinationsTree = nodes.size() - numberOfDestinationsSubtree; // TODO not sure if right

		double result = numberOfDestinationsSubtree*costSecondMostDistant + numberOfDestinationsTree*costMostDistant;

		return result;
	}

	/**
	 * Calculates the total cost
	 */
	private void calculateTotalCost() {
		double sum = 0;
		for(SMTNode n : nodes)
			sum += n.getNodeCost();
		this.cost = sum;
	}

	/**
	 * Class to represent a subtree with minimal functionality
	 * @author Yngve Sekse Kristiansen
	 *
	 */
	private class Subtree {
		private List<SMTNode> sMTNodes;

		/**
		 * Initializes a new subtree
		 * @param sMTNodes
		 *    the list of sMTNodes, can't be null or empty
		 */
		public Subtree(List<SMTNode> sMTNodes) {
			this.sMTNodes = sMTNodes;
		}

		/**
		 *
		 * @return
		 *    The number of sMTNodes in the tree
		 */
		int size() {
		    return sMTNodes.size();
		}
	}


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
}
