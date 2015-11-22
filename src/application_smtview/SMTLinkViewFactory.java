package application_smtview;

import javafx.geometry.Point2D;

/**
 * Simply factory for creating links
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTLinkViewFactory {

    public static SMTLinkView newLinkView(Point2D start, int startId, Point2D end, int endId) {
        return new SMTLinkView(start, startId, end, endId);
    }

    /**
     * Used when user is creating links
     * @param start
     *  	start coords(graphical)
     * @param startId
     *  	id of start node
     * @return
     *  	the link in progress
     */
    public static SMTLinkView newLinkInProgress(Point2D start, int startId) {
        return new SMTLinkView(start, startId);
    }

}
