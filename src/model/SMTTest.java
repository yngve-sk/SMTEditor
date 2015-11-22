package model;

import static org.junit.Assert.*;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.Scanner;

import org.junit.Test;

import utils.Dictionary;

/**
 * Test class, tests a few central properties for the model object, including parsing
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTTest {

	/**
	 * Tests that the number of neighbors reflect the number of links
	 */
	@Test
	public void testNodeLinkCountIsConsistentWithNeighbours() {
		SMTNode node = SMTNodeFactory.newNode(5, 5, true);
		assertEquals(node.getAllLinks().size(), 0);
		
		for(int i = 0; i < 20; i++) {
			assertEquals(node.getAllLinks().size(), i);
			node.addNeighbor(IdTracker.getNewNodeId());
		}
		
		int highestNeighborId = IdTracker.getNextNodeId() - 1;
		assertTrue(node.getNeighboursWithinRange().contains(highestNeighborId));
		for(int i = 20; i < 1; i--) {
			node.removeNeighbor(highestNeighborId--);
			assertEquals(node.getAllLinks().size(), i - 1);
		}
	}
	
	/**
	 * Tests that you can't add duplicate neighbors to a node
	 */
	@Test
	public void testNoDuplicateNeighbors() {
		SMTNode node = SMTNodeFactory.newNode(5,  5,  true);
		
		for(int i = 0; i < 100; i++)
			node.addNeighbor(1);
		
		assertEquals(1, node.getAllLinks().size());
	}
	
	/**
	 * Tests that a->b is equal to b->a
	 */
	@Test
	public void testSMTLinkEquals() {
		for(int x = 0; x < 100; x++)
			for(int y = 0; y < 100; y++) {
				SMTLink l1 = new SMTLink(x, y);
				SMTLink l2 = new SMTLink(y, x);
				assertEquals(l1, l2);
			}
	}
	
	/**
	 * Checks that no links will be added if the nodes aren't present.
	 * Also checks that if the nodes are present, the links are added,
	 * and that they can be removed.
	 */
	@Test
	public void testSMTAddRemoveLink() {
		IdTracker.reset();
		SharedMulticastTree smt = SMTFactory.emptyTree();
		assertEquals(smt.getAllDistinctLinks().size(), 0);
		
		for(int i = 0; i < 20; i++) {
			assertEquals(smt.getAllDistinctLinks().size(), 0); // the nodes aren't in the tree yet
			smt.addLink(i, i + 1);
		}
		assertEquals(smt.getAllDistinctLinks().size(), 0);
		
		// Add the nodes
		for(int i = 0; i < 20; i++)
			smt.addNode(5, 5, true, i, null);
	
		for(int i = 0; i < 20; i++) {
			assertEquals(smt.getAllDistinctLinks().size(), i); // the nodes aren't in the tree yet
			smt.addLink(i, i + 1);
		}
	}
	
	/**
	 * Checks that you can't add duplicate links. I.e adding (a->b) and (b->a) 
	 * shouldn't result in two added links, only one since they are equal
	 */
	@Test
	public void testNoDuplicateLinksAllowed() {
		SharedMulticastTree smt = SMTFactory.emptyTree();
		IdTracker.reset();

		smt.addNode(5, 5, true, 0, null);
		smt.addNode(5, 5, true, 1, null);
		
		assertEquals(0, smt.getAllDistinctLinks().size());
		smt.addLink(0, 1);
		smt.addLink(0, 1);
		smt.addLink(1, 0);
		smt.addLink(1, 0);
		assertEquals(1, smt.getAllDistinctLinks().size());
	}
	
	@Test
	public void testNodeNeighborListsAreUpdatedWhenLinksAreAddedToTree() {
		SharedMulticastTree tree = SMTFactory.emptyTree();
		IdTracker.reset();

		int iMax = 30;
		
		for(int i = 0; i < iMax; i++)
			tree.addNode(i, i, true, i, null);
		tree.addNode(iMax, iMax, true, iMax, null);
		
		for(int i = 0; i < iMax; i++) { 
			assertEquals(tree.getAllDistinctLinks().size(), i);
			tree.addLink(i, i + 1);
			assertTrue(tree.getNeighborsOfNode(i).contains(tree.getNode(i + 1)));
		}
		
		for(int i = 0; i < iMax; i++) {
			tree.removeLink(i, i + 1);
			assertFalse(tree.getNeighborsOfNode(i).contains(tree.getNode(i + 1)));
		}
	}
	
	/**
	 * Reads in a tree from a file, calculates the node cost for each node and
	 * compares to the correct answer
	 */
	@Test
	public void testCalculateNodeCost() {
		TestData data = TestData.One;
		SMTParser.parseFromFile(new File(data.getFilePath()));
		SharedMulticastTree tree = SMTParser.getCachedTree();
		double[] expectedValues = new double[6];
		
		try {
			Scanner reader = new Scanner(new File("src/smt_data/10500-nodes"));
			String str = reader.nextLine();
			reader.close();
			String[] strValues = str.split(",");
			for(int i = 0; i < expectedValues.length; i++)
				expectedValues[i] = Double.parseDouble(strValues[i]);
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
			
		for(int i = 0; i < expectedValues.length; i++)
			assertEquals(expectedValues[i], tree.getNode(i).getNodeCost(), 0);
	}
	
	/**
	 * Reads in a tree from a file, calculates the total cost and compares it to
	 * the correct answer
	 */
	@Test
	public void testCalculateTotalCost() {
		for(TestData data : TestData.values()) {
			SMTParser.parseFromFile(new File(data.getFilePath()));
			assertTrue("Unsuccessful parsing", SMTParser.didParseSuccessfully());
			SharedMulticastTree tree = SMTParser.getCachedTree();
			int costApprox = (int) Math.round(tree.getCost());
			assertEquals(data.getCorrectCost(), costApprox);
		}
	}
	
	/**
	 * Reads in from a file, and compares it to the data in the file manually plotted in here
	 */
	@Test
	public void testParseTree() {
		//TODO
	}

	@Test
	public void testSMTLinkDict() {
		Dictionary<SMTLinkKey, SMTLink> d = new Dictionary<SMTLinkKey, SMTLink>();
		
		SMTLinkKey key = new SMTLinkKey(1,2);
		d.put(new SMTLinkKey(1,2), new SMTLink(1,2));
	
		d.get(key).setSubtreeSize(100);
		assertEquals(d.get(key).getSubtreeSize(1), 100);
		
		d.get(key).setOppositeSubtreeSize(200);
		assertEquals(d.get(key).getSubtreeSize(2), 200);
	}
	
}
