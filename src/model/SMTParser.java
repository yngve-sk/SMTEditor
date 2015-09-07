package model;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;

/**
 *  Parses a file to a SMT object
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTParser {

    private static int numNodes;
    private static int numDestinations;
    
    private static List<Point2D> nodes;
    private static List<List<Integer>> neighbors;
    
    private static boolean cacheIsEmpty = true;
    private static boolean didParseSuccessfully = false;
    
    /**
     * Parses a SharedMulticastTree from the file. After parsing it, if no new tree is parsed,
     * the data will be cached here. It can be cleared by calling clear(), and retrieved by calling
     * getCachedTree()
     * @param file
     *      the file
     * @return
     *      the tree
     */
    public static void parseFromFile(File file) {
        readFile(file);
        if(didParseSuccessfully)
        	cacheIsEmpty = false;
    }

    /**
     *
     * @return
     *      A new tree representing the latest tree loaded from a file. Returns null if no
     *      tree is cached currently //TODO Make some standard tree data to always return instead of null
     */
    public static SharedMulticastTree getCachedTree() {
        if(cacheIsEmpty)
            return null;
        return new SharedMulticastTree(nodes, neighbors, numDestinations);
    }

    /**
     * Clears all cached data
     */
    public static void clear() {
        numNodes = 0;
        numDestinations = 0;
        nodes = null;
        neighbors = null;
        cacheIsEmpty = true;
    }

    /**
     * Reads in a file representing a tree
     * @param file
     *      the file
     * @throws IllegalArgumentException
     *      if the file isn't as expected
     */
    private static void readFile(File file) throws IllegalArgumentException{
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            String line;

            // Get to start delimiter
            while((line = removeComments(reader.readLine())) != null) {
                if(line.equals(Delimiters.START.getStringValue()))
                    break;
            }

            // Read in num nodes, num destinations, 2 lines
            line = removeComments(reader.readLine()).trim();
            numNodes = Integer.parseInt(line);
            line = removeComments(reader.readLine()).trim();
            numDestinations = Integer.parseInt(line);

            // Read in coordinates
            List<Point2D> coordinates = new ArrayList<Point2D>();
            for(int i = 0; i < numNodes; i++) {
                line = removeComments(reader.readLine());
                System.out.println("Line = " + line);
                coordinates.add(parseCoordinate(line));
            }

            // Expecting a delimiter now...
            line = removeComments(reader.readLine());
            if(line.equals(Delimiters.NEIGHBORS_START.getStringValue())) {
                reader.close();
                throw new IllegalArgumentException("Expected delimiter " +
                Delimiters.NEIGHBORS_START.getStringValue() + ", got " + line);
            }

            // Read in neighbors
            reader.readLine();
            List<List<Integer>> links = new ArrayList<List<Integer>>();
            for(int i = 0; i < numNodes; i++) {
                line = removeComments(reader.readLine());
                links.add(parseNeighborList(line));
            }

            neighbors = links;

            // done

            reader.close();
            
            nodes = coordinates;
            
        	didParseSuccessfully = true;

        } catch (IOException e) {
        	didParseSuccessfully = false;
        }
    }
    
    private static String removeComments(String line) {
    	if(line.contains("#")) {
    		return line.split("#")[0];
    	}
    	return line;
    }

    /**
     * Parses a stringrep of a neighbor list in the form:<p>
     * <b> nodeIndex | neighborIndex1 neighborIndex2 (...) neighborIndexN</b>
     * @param str
     * @return
     *      a list of integers, representing the neighbor indexes
     */
    private static List<Integer> parseNeighborList(String str) {
    	int splitIndex = str.indexOf("|");
    	String neighbors = str.substring(splitIndex+1, str.length()).trim();
    	String[] neighborArray = neighbors.split(" ");
    	
    	List<Integer> neighborList = new ArrayList<Integer>();
    	
    	for(String s : neighborArray)
    		neighborList.add(Integer.parseInt(s));
    	
    	return neighborList;
    }

    /**
     * Parses a string rep in form x y into a Point2D(x,y)
     * @param string
     * @return
     *      the coordinate
     */
    private static Point2D parseCoordinate(String string) {

        string.trim();
        String[] coordsStr = string.split(" ");
        Point2D coordinate = new Point2D(Double.parseDouble(coordsStr[0]), Double.parseDouble(coordsStr[1]));

        return coordinate;
    }

    private enum Delimiters {
        START("---------"),
        NEIGHBORS_START("----------------");

        private String stringValue;

        String getStringValue() {
            return stringValue;
        }

        Delimiters(String s) {
            stringValue = s;
        }

    }
    
    

	public static boolean didParseSuccessfully() {
		return didParseSuccessfully;
	}

}
