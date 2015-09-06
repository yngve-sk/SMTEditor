package application_controlsview;

import javafx.scene.control.Button;
import javafx.scene.effect.BlendMode;

/**
 * Extension of Button, all styling of buttons are done in this class
 * @author Yngve Sekse Kristiansen
 *
 */
public class CustomButton extends Button {

	public CustomButton(String text) {
		super(text);
				
		this.setOnMouseEntered(event -> mouseEntered());
		this.setOnMouseExited(event -> mouseExited());	
	}


	private void mouseEntered() {
	}

	private void mouseExited() {
	}


}