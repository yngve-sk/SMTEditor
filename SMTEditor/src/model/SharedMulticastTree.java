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

	// TODO add node to all neighbors of node?
	public void addNode(SMTNode node) {
	    nodes.add(node);
//	    for(SMTNode neighbor : node.getNeighboursWithinRange())
//	        neighbor.getNeighboursWithinRange().add(node);
	}

	/**
	 * Adds a link to the tree
	 * @param l
	 *     the link
	 */
	public void addLink(SMTLink l) {
	    l.source.addNeighbor(l.target);
	}

	/**
	 * Removes a node from the tree
	 * @param node
	 *     the node
	 */
	public void removeNode(SMTNode node) {
	    nodes.remove(node);
	    for(SMTNode n : nodes)
	        n.removeNeighbor(node);
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
            n.resetData();

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
	    for(SMTNode node : linked)
	        filter.runThroughFilter(getDistanceBetween(n, node));

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

		List<SMTNode> nodesWithinSendersRange = sender.getNeighboursWithinRange();
		int indexOfReceiver = nodesWithinSendersRange.indexOf(sender);

		return new Subtree(nodesWithinSendersRange.subList(0, indexOfReceiver));
	}

	/**
	 * Gets the subtree of a node
	 * @param n
	 * @return
	 */
	private Subtree arc(SMTNode n) {
		List<SMTNode> receivers = n.getNeighboursWithinRange();
		if(receivers.isEmpty())
		    return new Subtree();
		SMTNode receiver = receivers.get(receivers.size() - 1);
		return arc(n, receiver);
	}

	/**
	 *
	 * @param n
	 * @return
	 *  	the two most distant nodes, the most distant at index 1, second most distant at index 0
	 */
	private SMTNode[] twoMostDistant(SMTNode n) {
		List<SMTNode> withinRange = n.getNeighboursWithinRange();
		int size = withinRange.size();

		SMTNode[] mostDistant = {null, null};

		if(size == 1) {
		    mostDistant[1] = withinRange.get(0);
		}


		if(size > 1) {
		    NodeNeighborDistanceFilter filter = new NodeNeighborDistanceFilter(n);
		    for(SMTNode neighbor : withinRange)
		        filter.runThroughFilter(neighbor);

		    return filter.getTwoMostDistantNodes();
		}
		return mostDistant;
	}

	/**
	 *
	 * @param n
	 * @return
	 *     the cost of n
	 */
	private double getCost(SMTNode n) {
		int numberOfDestinationsSubtree = arc(n).size();

		if(numberOfDestinationsSubtree == 0)
		    return 0;

		SMTNode[] mostDistantN = twoMostDistant(n);
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
		private List<SMTNode> nodes;

		/**
		 * Initializes a new subtree
		 * @param nodes
		 *    the list of sMTNodes, can't be null or empty
		 */
		public Subtree(List<SMTNode> nodes) {
			this.nodes = nodes;
		}

		public Subtree() {
		    // empty subtree
		}

        /**
		 *
		 * @return
		 *    The number of sMTNodes in the tree
		 */
		int size() {
		    return (nodes == null) ? 0 : nodes.size();
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

	/**
	 * Utility to help find the two most distant neighbors of a node
	 * @author Yngve Sekse Kristiansen
	 *
	 */
	private class NodeNeighborDistanceFilter {

	    private SMTNode origin;

	    private SMTNode furthest;
	    private double furthestDistance;

	    private SMTNode nextFurthest;
	    private double nextFurthestDistance;

	    /**
	     * Initializes a new filter, taking in origin node
	     * @param origin
	     */
	    public NodeNeighborDistanceFilter(SMTNode origin) {
	        this.origin = origin;
	    }

	    public SMTNode[] getTwoMostDistantNodes() {
	        SMTNode[] twoMostDistant = {nextFurthest, furthest};
            return twoMostDistant;
        }

        /**
	     * Runs a node through the filter, retains it if its distance is greater than the next biggest
	     * distance currently being retained
	     * @param neighbor
	     *     the neighbor
	     */
	    public void runThroughFilter(SMTNode neighbor) {
	        if(furthest == null) {
	            furthest = neighbor;
	            furthestDistance = distanceTo(neighbor);
	        }
	        else if(nextFurthest == null) {
	            nextFurthest = neighbor;
	            furthestDistance = distanceTo(neighbor);
	        }

	        double dist = distanceTo(neighbor);

	        if(dist > furthestDistance) {
	            furthest = neighbor;
	            furthestDistance = dist;
	        }
	        else if(dist < nextFurthestDistance && dist < furthestDistance) {
	            nextFurthest = neighbor;
	            nextFurthestDistance = dist;
	        }
	    }

	    private double distanceTo(SMTNode neighbor) {
	        double dx = neighbor.getX() - origin.getX();
	        double dy = neighbor.getY() - origin.getY();

	        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
	    }
	}

	/**
	 * Called when a node is relocated visually, this transfers
	 * the new coordinates onto the node in the tree.
	 * @param data
	 *     the updated data object
	 */
    public void relocateNode(SMTNode data) {
        int index = nodes.indexOf(data);
        nodes.get(index).relocate(data.getX(), data.getY());
    }
}
