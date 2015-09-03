package application_smtview;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class SMTLinkView extends Line {

    private final Color DEFAULT_COLOR;
    private final double DEFAULT_BOLDNESS = 1;
    private final double HIGHLIGHTED_BOLDNESS = 2;

    /**
     * s = source, d = destination
     * @param sx
     * @param sy
     * @param dx
     * @param dy
     */
    public SMTLinkView(Point2D start, Point2D end, boolean isRelayOnly) {
        super(start.getX(), start.getY(), end.getX(), end.getY());

        DEFAULT_COLOR = isRelayOnly ? Color.SLATEGRAY : Color.BLACK;

        this.setOnMouseEntered(event -> mouseEntered());
        this.setOnMouseExited(event -> mouseExited());
    }

    private void mouseEntered() {
        this.setFill(Color.RED);
    }

    private void mouseExited() {
        this.setFill(DEFAULT_COLOR);
    }

    private void embolden() {
        this.setStrokeWidth(HIGHLIGHTED_BOLDNESS);
    }

    private void unembolden() {
        this.setStrokeWidth(DEFAULT_BOLDNESS);
    }

    void reset() {
        mouseExited();
        unembolden();
    }

    void highlightAsPowerLevelOne() {
        this.setFill(Color.YELLOW);
        embolden();
    }

    void highlightAsPowerLevelTwo() {
        this.setFill(Color.KHAKI);
        embolden();
    }
}
