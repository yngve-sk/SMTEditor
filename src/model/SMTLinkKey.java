package model;

/**
 * Represents a link key. Contains a minimal amount of information, and also takes into
 * account the symmetric nature of links. I.e a->b === b->a. Used to avoid overlapping
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTLinkKey {
	
	public int id1, id2;
	
	public SMTLinkKey(int id1, int id2) {
		this.id1 = id1;
		this.id2 = id2;
	}
	
	@Override
	public boolean equals(Object obj) {
		if(this == obj)
			return true;
		
		SMTLinkKey otherKey = (SMTLinkKey) obj;
		if((this.id1 == otherKey.id1 && this.id2 == otherKey.id2) || 
				(this.id1 == otherKey.id2 && this.id2 == otherKey.id1))
			return true;
		return false;
	}
	
	@Override
	public String toString() {
		return "Key for link from " + id1 + " to " + id2;
	}
	
}
