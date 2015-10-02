package application_outputview;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class OutputField extends Group {

	private ImageView icon;
	private Label textOutput;
	
	private final String labelText;
	
	public OutputField(String imagePath, String labelText) {
		super();
		
		icon = new ImageView(new Image(imagePath));
		textOutput = new Label();
		
		this.labelText = labelText + ": ";
		textOutput.setText(this.labelText);
		textOutput.setAlignment(Pos.BASELINE_CENTER);
		
		getChildren().addAll(icon, textOutput);
		this.setStringValue("N/A");
	}
	
	private void layoutSubviews(double width, double height) {
		double iconHeight = height*0.7;
		double iconY = (height - iconHeight)/2;
		
		icon.resizeRelocate(0, iconY, iconHeight, iconHeight);
		icon.setFitWidth(iconHeight);
		icon.setFitHeight(iconHeight);
		textOutput.resizeRelocate(height, 0, width - height, height);
		textOutput.setPrefHeight(height);
	}
	
	public void resize(double width, double height) {
		super.resize(width, height);
		layoutSubviews(width, height);
	} 
	
	public void resizeRelocate(double x, double y, double width, double height) {
		super.resizeRelocate(x, y, width, height);
		layoutSubviews(width, height);
	}
	
	public void setStringValue(String s) {
		this.textOutput.setText(labelText + s);
	}
}
