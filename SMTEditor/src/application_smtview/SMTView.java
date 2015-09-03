package application_smtview;


import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;

/**
 * Contains the SMTContentView
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTView extends ScrollPane {

    private static final long serialVersionUID = -1363103688083413403L;

    private SMTContentView content;
    private Slider zoom;

    private final double zoomXRatio = 0.60;
    private final double zoomWidthRatio = 0.30;
    private final double zoomYRatio = 0.08;

    /**
     * Initializes a new SMTView
     */
    public SMTView() {
        content = new SMTContentView();
        this.setContent(content);

        zoom = new Slider();
    }

    /**
     * Initializes a new SMTView with given size (no negative inputs)
     * @param x
     * @param y
     * @param width
     * @param height
     */
    public SMTView(double x, double y, double width, double height) {
        this();
        this.resizeRelocate(x, y, width, height);
    }

    @Override
    public void resizeRelocate(double x, double y, double width, double height) {
        super.resizeRelocate(x, y, width, height);
        this.setPrefSize(width, height);
    }

    /**
     * Called when the zoom is changed
     * @param newPercentageValue
     */
    public void zoomDidChange(int newPercentageValue) {
        double scale = newPercentageValue/100.0;
        content.setScaleX(scale);
        content.setScaleY(scale);
    }

}
