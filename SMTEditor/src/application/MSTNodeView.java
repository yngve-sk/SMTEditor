package application;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

public class MSTNodeView extends ImageView {

    public MSTNodeView(double x, double y, double width, double height, String imagePath) {
        super(new Image(imagePath));

        this.resizeRelocate(x, y, width, height);
    }

    @Override
    public void resizeRelocate(double x, double y, double width, double height) {
        super.resizeRelocate(x, y, width, height);
        this.setFitWidth(width);
        this.setFitHeight(height);
 }

}
