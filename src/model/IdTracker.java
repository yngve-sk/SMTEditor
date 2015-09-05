package model;

/**
 * Keeps track of the node ID's, ID's are used to keep the model and graphics objects in sync.
 * @author Yngve Sekse Kristiansen
 *
 */
public class IdTracker {

    private static int nodeId = 0;
    private static int linkId = 0;

    /**
     * Gets a new node ID, this should never return the same id unless it's literally called 2^32 times
     * which for this application will probably never happen.
     * @return
     */
    public static int getNewNodeId() {
        return nodeId++;
    }

    /**
     * Gets the next node id, call this to "peek" without incrementing the id tracker
     * @return
     */
    public static int getNextNodeId() {
        return nodeId;
    }

    /**
     * Gets a new link ID, this should never return the same id unless it's literally called 2^32 times
     * which for this application will probably never happen.
     * @return
     */
    public static int getNewLinkId() {
        return linkId++;
    }

    /**
     * Gets the next link id, call this to "peek" without incrementing the id tracker
     * @return
     */
    public static int getNextLinkId() {
        return linkId;
    }
}
