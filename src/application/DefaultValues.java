package application;

import java.awt.Toolkit;

/**
 *  Default values is be stored here.
 * @author Yngve Sekse Kristiansen
 *
 */
public class DefaultValues {

	public static final int DEFAULT_REFERENCE_DIMENSION = 400,
							DEFAULT_GRAPHICAL_DIMENSION = Toolkit.getDefaultToolkit().getScreenSize().width*8/10,
							DEFAULT_REFERENCE_NODE_DIMENSION = DEFAULT_REFERENCE_DIMENSION/40,
							
					//		GRAPHICAL_MAX_DIMENSION = 4000,
					//		GRAPHICAL_MIN_DIMENSION = 2000,
							
							REFERENCE_MIN_DIMENSION = 10,
							REFERENCE_MAX_DIMENSION = 10000,
	
							DEFAULT_REFERENCE_NODE_MIN_DIMENSION = DEFAULT_REFERENCE_DIMENSION/40,
							DEFAULT_REFERENCE_NODE_MAX_DIMENSION = DEFAULT_REFERENCE_DIMENSION/10;
	
}
