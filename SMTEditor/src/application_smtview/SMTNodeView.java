package application_smtview;

import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import model.SMTNode;

/**
 * Represents a generic node view
 * @author Yngve Sekse Kristiansen
 *
 */
public abstract class SMTNodeView extends ImageView {

    private SMTNode node;

    public SMTNodeView(double x, double y, double width, double height, String imagePath, SMTNode node) {
        super(new Image(imagePath));

        this.resizeRelocate(x, y, width, height);

        this.node = node;

        this.setOnMouseEntered(event -> mouseEntered());
        this.setOnMouseExited(event -> mouseExited());
    }

    @Override
    public void resizeRelocate(double x, double y, double width, double height) {
        super.resizeRelocate(x, y, width, height);
        this.setFitWidth(width);
        this.setFitHeight(height);
    }

    private void mouseEntered() {
        getContentView().showStatsPopup(node, this.getLayoutX(), this.getLayoutY());
        System.out.println("MouseEntered!!!");
    }

    private void mouseExited() {
        getContentView().hideStatsPopup();
        System.out.println("MouseExited!!!");
  }

    private SMTContentView getContentView() {
        return (SMTContentView) this.getParent();
    }
}
