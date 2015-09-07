package application_smtview;


import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.ImageCursor;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;

import java.io.File;

import application_componentview.Components;
import application_controlsview.ControlsView.Buttons;

/**
 * Contains the SMTContentView
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTView extends ScrollPane {

    private static final long serialVersionUID = -1363103688083413403L;

    private SMTContentView content;
    private Components cursor;

    private Cursor defaultCursor = Cursor.OPEN_HAND;
    private ImageCursor destinationCursor;
    private ImageCursor nonDestinationCursor;
    private Cursor linkCursor = Cursor.CROSSHAIR;

    /**
     * Initializes a new SMTView
     */
    public SMTView() {
        content = new SMTContentView(this);
        this.setContent(content);

        cursor = Components.CURSOR;
        updateCursorForComponentType();

        Image destinationCursorImage = new Image(Components.DESTINATION.getImagePath());
        double hotspotX = destinationCursorImage.getWidth()/2;
        double hotspotY = destinationCursorImage.getHeight()/2;
        Image[] dest = {destinationCursorImage};

        Image nonDestinationCursorImage = new Image(Components.NONDESTINATION.getImagePath());
        // same hotspot
        Image[] nonDest = {nonDestinationCursorImage};

        destinationCursor = ImageCursor.chooseBestCursor(dest, hotspotX, hotspotY);
        nonDestinationCursor = ImageCursor.chooseBestCursor(nonDest, hotspotX, hotspotY);


        this.setOnMouseEntered(event -> mouseEntered());
        this.setOnMouseExited(event -> mouseExited());
        this.setOnMouseClicked(event -> mouseClicked(event));
        this.setOnMouseMoved(event -> mouseOver(event));

        this.getChildren().add(content);
 }

    private void mouseClicked(MouseEvent e) {
//        System.out.println("CLICKED");
        content.mouseClicked();

//        System.out.println("e coords : " + e.getX() + ", " + e.getY());
//        System.out.println("content.parentToLocal : " + content.parentToLocal(parentX, parentY));
//        System.out.println("content.screenToLocal : " + content.screenToLocal(parentX, parentY));
//        System.out.println("content.localToParent : " + content.localToParent(parentX, parentY));
//        System.out.println("content.localToScreen : " + content.localToScreen(parentX, parentY));
//        System.out.println("content.localToScene : " + content.localToScene(parentX, parentY));
//        System.out.println("content.sceneToLocal : " + content.sceneToLocal(parentX, parentY));
//        System.out.println("content.screenToLocal : " + content.screenToLocal(parentX, parentY));
//
//        System.out.println("this.parentToLocal : " + this.parentToLocal(parentX, parentY));
//        System.out.println("this.screenToLocal : " + this.screenToLocal(parentX, parentY));
//        System.out.println("this.localToParent : " + this.localToParent(parentX, parentY));
//        System.out.println("this.localToScreen : " + this.localToScreen(parentX, parentY));
//        System.out.println("this.localToScene : " + this.localToScene(parentX, parentY));
//        System.out.println("this.sceneToLocal : " + this.sceneToLocal(parentX, parentY));
//        System.out.println("this.screenToLocal : " + this.screenToLocal(parentX, parentY));
    }

    /**
     * Translates a coordinate in the scrollpane to a coordinate on the content view
     * (contentView.parentToLocal did not work...)
     * @param x
     * @param y
     * @return
     */
    private Point2D scrollPaneToContent(double x, double y) {
        double viewPortHeight = this.getViewportBounds().getHeight();
        double viewPortWidth = this.getViewportBounds().getWidth();

        double contentHeight = content.getBoundsInLocal().getHeight();
        double contentWidth = content.getBoundsInLocal().getWidth();

        double vScroll = this.getVvalue();
        double hScroll = this.getHvalue();

        double hScrollRange = contentWidth - viewPortWidth;
        double vScrollRange = contentHeight - viewPortHeight;

        double dx = hScroll*hScrollRange;
        double dy = vScroll*vScrollRange;

        Point2D contentCoordinates = new Point2D(x + dx, y + dy);

        /*
        System.out.println("hScroll = " + hScroll + "vScroll = " + vScroll);
        System.out.println("x = " + x + ", y = " + y);
        System.out.println("dx = " + dx + ", dy = " + dy + ", x + dx = " + (x + dx) + ", y + dy = " + (y + dy));
        */

        return contentCoordinates;
    }

    private void mouseEntered() {
        // TODO Auto-generated method stub
    }

    private void mouseExited() {
        // TODO Auto-generated method stub
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
        content.zoomDidChange(newPercentageValue);
    }

    private void updateCursorForComponentType() {
        if(cursor == Components.CURSOR) {
            this.setCursor(defaultCursor);
        }
        else if(cursor == Components.DESTINATION) {
            this.setCursor(destinationCursor);
        }
        else if(cursor == Components.NONDESTINATION) {
            this.setCursor(nonDestinationCursor);
        }
        else if(cursor == Components.LINK) {
            this.setCursor(linkCursor);
        }
        else {
            this.setCursor(defaultCursor);
        }
    }

    private void mouseOver(MouseEvent me) {
        Point2D contentLoc = scrollPaneToContent(me.getX(), me.getY());
        content.mouseOver(contentLoc);
    }

    public void componentSelectionDidChange(Components componentType) {
        content.componentSelectionDidChange(componentType);
        cursor = componentType;
     //   updateCursorForComponentType();
    }

    private double hScrollCache = 0;
    private double vScrollCache = 0;

    public void cacheScroll() {
        hScrollCache = this.getHvalue();
        vScrollCache = this.getVvalue();
    }

    public void restoreScrollFromCache() {
        setHvalue(hScrollCache);
        setVvalue(vScrollCache);
    }

	public void buttonClicked(Buttons type) {
		content.buttonClicked(type);
	}

	public void fileWasDropped(File file) {
		content.fileWasDropped(file);
	}

}
