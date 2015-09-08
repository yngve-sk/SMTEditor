package application;

import java.awt.Dimension;
import java.awt.Toolkit;

import javafx.application.Application;
import javafx.scene.Group;
import javafx.stage.Stage;


public class Main extends Application {
	@Override
	public void start(Stage primaryStage) {
		try {

		    Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();

		    SMTEditor editorScene = new SMTEditor(new Group(),  screenSize.width,  screenSize.height, primaryStage);

		    primaryStage.setScene(editorScene);
			primaryStage.show();
		} catch(Exception e) {
			e.printStackTrace();
		}
	}




	public static void main(String[] args) {
		launch(args);
	}
}

