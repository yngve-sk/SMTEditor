package application_outputview;

import application.SMTEditor;
import javafx.scene.Group;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

public class InputView extends Group {

	private Label variableName;
	private TextField input;
	private final InputViewType type;
	
	private final double labelWidthRatio = 0.25;
	
	public InputView(InputViewType type) {
		this.variableName = new Label(type.getString());
		this.input = new NumericTextField("enter " + type.getString() + "...");
	
		this.type = type;
		
		getChildren().addAll(variableName, input);
	}
	
	@Override
	public void resizeRelocate(double x, double y, double width, double height) {
		super.resizeRelocate(x, y, width, height);
		layoutSubviews(width, height);
	}
	
	private void layoutSubviews(double width, double height) {
		double labelWidth = width*labelWidthRatio;
		variableName.resizeRelocate(0, 0, labelWidth, height);
		input.resizeRelocate(labelWidth, 0, width - labelWidth, height);
	}
	
	private void inputDidChange() {
		double value = input.getText().isEmpty() ? 0 : Double.parseDouble(input.getText());
		System.out.println("Input changed to " + value + " ...!");
		SMTEditor editor = (SMTEditor) getScene();
		editor.inputDidChange(value, this.type);
	}
	
	public class NumericTextField extends TextField {
		
		public NumericTextField(String prompt) {
			this.setPromptText(prompt);
		}
		
		@Override
		public void replaceText(int start, int end, String text) {
		    String pre = getText(0, start),
				   post = getText(end, this.getLength()),
				   fullString = pre + text + post;

		    if (!text.isEmpty() && text.matches("\\d|\\.")) 
		        try {
		            Double.parseDouble(fullString); 
		            super.replaceText(start, end, text);
			        InputView.this.inputDidChange();
		        } catch (NumberFormatException e) {
		        	// do nothing
		        }

		    if (text.isEmpty()) {
		        super.replaceText(start, end, text);
		        InputView.this.inputDidChange();
		    }
		}
	}
	
	public enum InputViewType {
		KAPPA("\u03F0"), ALPHA("\u03B1");
		
		InputViewType(String s) {
			this.s = s;
		}
		
		private String s;
		
		public String getString() {
			return s;
		}
	}
}


