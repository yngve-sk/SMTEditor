package application_smtview;

import javafx.geometry.Point2D;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
        this.setOnMouseDragged(event -> mouseDragged(event));
        this.setOnMouseReleased(event -> mouseDragReleased(event));

    }


    private void mouseDragReleased(MouseEvent event) {
        getContentView().nodeWasDragged(this);
    }


    private void mouseDragged(MouseEvent event) {
        double dx = event.getX();
        double dy = event.getY();

        Point2D d = this.localToParent(dx, dy); // dx and dy is subtracted to emulate the node being clicked at its anchor
        double x = d.getX();
        double y = d.getY();


//        System.out.println("x = " + x + ", dx = " + dx);
//        System.out.println("y = " + y + ", dy = " + dy);
//
//        final double xdx = x - dx;
//        final double ydy = y - dy;
//
//        System.out.println("xdx = " + xdx);
//        System.out.println("ydy = " + ydy);

        this.relocate(x, y);
    }

    @Override
    public void resizeRelocate(double x, double y, double width, double height) {
        super.resizeRelocate(x, y, width, height);
        this.setFitWidth(width);
        this.setFitHeight(height);
    }

    private void mouseEntered() {
        getContentView().showStatsPopup(node, this.getLayoutX(), this.getLayoutY());
    }

    private void mouseExited() {
        getContentView().hideStatsPopup();
    }

    private SMTContentView getContentView() {
        return (SMTContentView) this.getParent();
    }

    public SMTNode getData() {
        return node;
    }


    public void updateModelCoordinates(
            double modelX,
            double modelY) {
        System.out.println("x y = (" + modelX + ", " + modelY + ")");
        node.setX(modelX);
        node.setY(modelY);
    }
}
