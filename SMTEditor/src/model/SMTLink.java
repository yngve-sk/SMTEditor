package model;

/**
 * Represents an immutable link object between two nodes.
 * It can be deleted from a node, but not changed
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTLink {

    public final SMTNode source, target;
    public final boolean isRelayOnly;

    /**
     * Initializes a new link
     * @param n1
     *      the origin node
     * @param n2
     *      the target node
     */
    public SMTLink(SMTNode source, SMTNode target, boolean isRelayOnly) {
        this.source = source;
        this.target = target;
        this.isRelayOnly = isRelayOnly;
    }
}
