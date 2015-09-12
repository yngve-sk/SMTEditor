package model;

/**
 * Represents an immutable link object between two nodes.
 * It can be deleted from a node, but not changed
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTLink {

    public final int id1, id2;
    
    private int subtreeSize, oppositeSubtreeSize;

	public int getSubtreeSize(int id) {
		if(id == id1)
			return subtreeSize;
		else if(id == id2)
			return oppositeSubtreeSize;
		return -1;
	}

	/**
	 * Sets subtree size id1->id2
	 * @param subtreeSize
	 */
	public void setSubtreeSize(int subtreeSize) {
		this.subtreeSize = subtreeSize;
	}


	/**
	 * Sets subtree size id2->id1
	 * @param subtreeSize
	 */
	public void setOppositeSubtreeSize(int oppositeSubtreeSize) {
		this.oppositeSubtreeSize = oppositeSubtreeSize;
	}

	/**
     * Initializes a new link
     * @param id1
     *      id of node 1
     * @param id2
     *      id of node 2
     */
    public SMTLink(int id1, int id2) {
        this.id1 = id1;
        this.id2 = id2;
    }

    @Override
    public String toString() {
        return "[Link: " + id1 + "<---->" + id2 + "]";
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        SMTLink other = (SMTLink) obj;
        return (this.id1 == other.id1 && this.id2 == other.id2) || (this.id1 == other.id2 && this.id2 == other.id1);
    }

}
