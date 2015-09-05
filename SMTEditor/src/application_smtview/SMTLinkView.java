package application_smtview;

import javafx.geometry.Point2D;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import model.SMTLink;

public class SMTLinkView extends Line {

    private final Color DEFAULT_COLOR = Color.BLACK;
    private final Color RELAY_COLOR = Color.SLATEGRAY;
    private final double DEFAULT_BOLDNESS = 3;
    private final double HIGHLIGHTED_BOLDNESS = 5;
    private SMTLink link;

    public int startId, endId;

    /**
     * s = source, d = destination
     * @param sx
     * @param sy
     * @param dx
     * @param dy
     */
    public SMTLinkView(Point2D start, int startId, Point2D end, int endId) {
        super(start.getX(), start.getY(), end.getX(), end.getY());

        this.setOnMouseEntered(event -> mouseEntered());
        this.setOnMouseExited(event -> mouseExited());

        this.startId = startId;
        this.endId = endId;
        link = new SMTLink(startId, endId);

        reset();
    }

    public SMTLinkView(Point2D start, int startId) {
        super(start.getX(), start.getY(), start.getX(), start.getY());

        this.setOnMouseEntered(event -> mouseEntered());
        this.setOnMouseExited(event -> mouseExited());

        this.startId = startId;
    }

    public void setEndPoint(Point2D end, int endId) {
        setEndX(end.getX());
        setEndY(end.getY());
        this.endId = endId;
        link = new SMTLink(startId, endId);
        System.out.println("setEndPoint");
    }

    public int getStartId() {
        return startId;
    }

    public int getEndId() {
        return endId;
    }

    private void mouseEntered() {
        this.setStroke(Color.RED);
    }

    private void mouseExited() {
        this.setStroke(DEFAULT_COLOR);
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
        System.out.println("Highlight as powerLevelOne");
        this.setStroke(Color.YELLOW);
        embolden();
    }

    void highlightAsPowerLevelTwo() {
        System.out.println("Highlight as powerLevelTwo");
        this.setStroke(Color.KHAKI);
        embolden();
    }

    void hightlightAsRelay() {
        this.setStroke(RELAY_COLOR);
        embolden();
    }

    void highlight() {
        this.setStroke(DEFAULT_COLOR);
        embolden();
    }

    double getLength() {
        double dx = this.getEndX() - this.getStartX();
        double dy = this.getEndY() - this.getStartY();

        return Math.sqrt(Math.pow(dx,2) + Math.pow(dy,2));
    }

    public SMTLink getLink() {
        return link;
    }

    @Override
    public boolean equals(Object other) {
        if(other instanceof SMTLink) // can also be compared to links
            return other.equals(link);

        else if(other instanceof SMTLinkView) {
            SMTLinkView otherView = (SMTLinkView) other;
            return (otherView.startId == this.startId) && (otherView.endId == this.endId);
        }
        return false;
    }
}
