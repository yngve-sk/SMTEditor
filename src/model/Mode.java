package model;

/**
 * Keeps track of which "mode" the editor is in, this would be made into an enum tracker
 * if more modes were added
 * @author Yngve Sekse Kristiansen
 *
 */
public class Mode {

	private static boolean isInDiscreteMode = false;
	private static int discreteCellSize = 2;
	static final int defaultDimension = 2000;
	
	static boolean isInDiscreteMode() {
		return isInDiscreteMode;
	}
	
	static void setDiscreteMode(boolean isInDiscreteMode, int discreteCellSize) {
		Mode.isInDiscreteMode = isInDiscreteMode;
		Mode.discreteCellSize = discreteCellSize;
	}
	
	static int getDiscreteCellSize() {
		return Mode.discreteCellSize;
	}
	
}
