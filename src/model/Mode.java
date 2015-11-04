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
		
	/**
	 * Rounds a coordinate value to a model coordinate value
	 * @param coord
	 *  	the model input value
	 * @return
	 *  	the rounded coordinate value according to current discrete mode settings
	 */
	public static int roundModelForDiscreteCellSize(double coord) {
		int cellSize = Mode.getDiscreteCellSize();
		
		int intCoord = (int) coord;
		
		if(intCoord%cellSize < cellSize/2 || (intCoord >= Mode.defaultDimension - cellSize/2)) { // round down
			return intCoord - (intCoord%cellSize);
		}
		else if(intCoord < cellSize) { // Round up so it's "in" in the grid
			return cellSize; 
		}
		else { // round up if it's "past" the mid point
			return intCoord - (intCoord%cellSize) + cellSize;
		}
	}

	/**
	 * Rounds a visual coordinate according to the discrete mode settings
	 * @param coord
	 *  	the input visual coordinate
	 * @return
	 *  	the coordinate visually valid for the discrete mode settings
	 */
	public static double roundVisualForDiscreteCellSize(double coord) {
		int cellSize = Mode.getDiscreteCellSize();
		
		System.out.println("actual coord : " + coord + ", round visual, cellSize = " + cellSize);
		
		double lowerOption = coord - (coord % cellSize);
		double higherOption = lowerOption + cellSize;
		double d = cellSize*0.5;
		
		System.out.println("lower : " + lowerOption + ", higher : " + higherOption);
		
		if(coord%cellSize == 0)
			return coord + d;
		else if(coord - lowerOption < higherOption - coord) {
			System.out.println("returning lowerOption + d = " + (lowerOption + d));
			return lowerOption + d;
		} 
		else {
			System.out.println("returning higherOption + d = " + (higherOption + d));
			return higherOption + d;
		}
	}

}
