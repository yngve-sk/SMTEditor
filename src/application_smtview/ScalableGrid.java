package application_smtview;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.ObservableList;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class ScalableGrid extends Canvas {

	private final double strokeWidth = 5;
	private double cellSize;
	private int numHLines, numVLines;
	private double width, height;
	
	
	public ScalableGrid(double width, double height, double initialCellSize) {
		this.width = width;
		this.height = height;
		this.cellSize = initialCellSize;
		System.out.println("width = " + width + ", height = " + height + ", cellSize = " + cellSize);

		this.setHeight(height);
		this.setWidth(width);
		
		this.resizeRelocate(0, 0, width, height);

		this.getGraphicsContext2D().moveTo(0, 0);
		this.getGraphicsContext2D().lineTo(width, height);
		
		int maxNumVLines = (int) Math.ceil(width/cellSize);
		int maxNumHLines = (int) Math.ceil(height/cellSize);
		
		System.out.println("maxnum H / V lines = " + maxNumHLines + ", " + maxNumVLines);
		
		this.getGraphicsContext2D().setFill(Color.BLACK);
		
	}
	
	
	
//	private void adjustLines() {
//		for(int i = 0; i < numVLines; i++) {
//			Line l = verticalLines.get(i);
//			l.setVisible(true);
//			System.out.println("setting startX of VLine to: " + (i+1)*cellSize);
//			l.setStartX((i+1)*cellSize);
//			l.setEndX((i+1)*cellSize);
//		}
//		for(int i = numVLines; i < verticalLines.size(); i++)
//			verticalLines.get(i).setVisible(false);
//	
//		for(int i = 0; i < numHLines; i++) {
//			Line l = horizontalLines.get(i);
//			l.setVisible(true);
//			l.setStartY((i+1)*cellSize);
//			l.setEndY((i+1)*cellSize);
//		}
//		for(int i = numHLines; i < horizontalLines.size(); i++)
//			horizontalLines.get(i).setVisible(false);
//	}
//	
	/**
	 * Adjusts the cell size and lays out the grid again,
	 * scaling of cell size relative to node dimension etc
	 * must be done in SMTContentView before passing the resulting
	 * dimension in here.
	 * @param newCellSize
	 */
	public void adjustCellSize(double newCellSize) {
		System.out.println("adjust cell size to " + newCellSize);
		this.cellSize = newCellSize;
		
		GraphicsContext c = this.getGraphicsContext2D();
		
		double d = cellSize/2;
		
		for(int i = 0; i < numHLines; i++) {
			c.fillRect(0, (i+1)*cellSize - d, width, strokeWidth);
		}
		
		for(int i = 0; i < numVLines; i++) {
			c.fillRect((i+1)*cellSize - d, 0, strokeWidth, height);
		}
	}
}
