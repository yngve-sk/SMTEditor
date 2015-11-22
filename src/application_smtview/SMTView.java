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
import application_controlsview.ControlsView.CheckBoxes;
import application_controlsview.ControlsView.RadioButtons;
import application_controlsview.ControlsView.ToggleButtons;
import application_stats_save_view.InputView.InputViewType;

/**
 * Contains the SMTContentView. This is the ScrollPane encasing it.
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
        setContent(content);

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


        setOnMouseEntered(event -> mouseEntered());
        setOnMouseExited(event -> mouseExited());
        setOnMouseClicked(event -> mouseClicked(event));
        setOnMouseMoved(event -> mouseOver(event));

        getChildren().add(content);
 }

    private void mouseClicked(MouseEvent e) {
//        System.out.println("CLICKED");
        content.mouseClicked(e);

//        System.out.println("e coords : " + e.getX() + ", " + e.getY());
//        System.out.println("content.parentToLocal : " + content.parentToLocal(parentX, parentY));
//        System.out.println("content.screenToLocal : " + content.screenToLocal(parentX, parentY));
//        System.out.println("content.localToParent : " + content.localToParent(parentX, parentY));
//        System.out.println("content.localToScreen : " + content.localToScreen(parentX, parentY));
//        System.out.println("content.localToScene : " + content.localToScene(parentX, parentY));
//        System.out.println("content.sceneToLocal : " + content.sceneToLocal(parentX, parentY));
//        System.out.println("content.screenToLocal : " + content.screenToLocal(parentX, parentY));
//
//        System.out.println("parentToLocal : " + parentToLocal(parentX, parentY));
//        System.out.println("screenToLocal : " + screenToLocal(parentX, parentY));
//        System.out.println("localToParent : " + localToParent(parentX, parentY));
//        System.out.println("localToScreen : " + localToScreen(parentX, parentY));
//        System.out.println("localToScene : " + localToScene(parentX, parentY));
//        System.out.println("sceneToLocal : " + sceneToLocal(parentX, parentY));
//        System.out.println("screenToLocal : " + screenToLocal(parentX, parentY));
    }

    /**
     * Translates a coordinate in the scrollpane to a coordinate on the content view
     * (contentView.parentToLocal did not work...)
     * @param x
     * @param y
     * @return
     */
    private Point2D scrollPaneToContent(double x, double y) {
        double viewPortHeight = getViewportBounds().getHeight();
        double viewPortWidth = getViewportBounds().getWidth();

        double contentHeight = content.getBoundsInLocal().getHeight();
        double contentWidth = content.getBoundsInLocal().getWidth();

        double vScroll = getVvalue();
        double hScroll = getHvalue();

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
        resizeRelocate(x, y, width, height);
    }

    @Override
    public void resizeRelocate(double x, double y, double width, double height) {
        super.resizeRelocate(x, y, width, height);
        setPrefSize(width, height);
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
            setCursor(defaultCursor);
        }
        else if(cursor == Components.DESTINATION) {
            setCursor(destinationCursor);
        }
        else if(cursor == Components.NONDESTINATION) {
            setCursor(nonDestinationCursor);
        }
        else if(cursor == Components.LINK) {
            setCursor(linkCursor);
        }
        else {
            setCursor(defaultCursor);
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
        hScrollCache = getHvalue();
        vScrollCache = getVvalue();
    }

    public void restoreScrollFromCache() {
        setHvalue(hScrollCache);
        setVvalue(vScrollCache);
    }
    
    public SMTContentView getContentView() {
    	return this.content;
    }
//
//    // 
//    // 			Methods below this point are routed to content
//    //
//	public void buttonClicked(Buttons type) {
//		content.buttonClicked(type);
//	}
//
//	public void fileWasDropped(File file) {
//		content.fileWasDropped(file);
//	}
//
//	public void saveButtonClicked() {
//		content.saveButtonClicked();
//	}
//
//	public void inputDidChange(double value, InputViewType type) {
//		content.inputDidChange(value, type);
//	}
//
//	public void toggleButtonClicked(ToggleButtons type, boolean isSelected) {
//		content.toggleButtonClicked(type, isSelected);
//	}
//
//	public void checkBoxClicked(CheckBoxes type, boolean isSelected) {
//		content.checkBoxClicked(type, isSelected);
//	}
//
//	public void radioButtonClicked(RadioButtons type) {
//		content.radioButtonClicked(type);
//	}
//
//	public void cellSizeDidChange(int intValue) {
//		content.cellSizeDidChange(intValue);
//	}
//
//	public void graphicalDimensionDidChange(int newGraphicalDimension) {
////		this.setVmax(newGraphicalDimension); // also update scroll bar
////		this.setHmax(newGraphicalDimension);
//		content.graphicalDimensionDidChange(newGraphicalDimension);
//		this.autosize();
//	}
//
//	public void referenceDimensionDidChange(int newReferenceDimension) {
//		content.referenceDimensionDidChange(newReferenceDimension);
//	}
}
