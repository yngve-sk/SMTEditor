package application_smtview;

import javafx.geometry.Point2D;

public class SMTLinkViewFactory {

    public static SMTLinkView newLinkView(Point2D start, int startId, Point2D end, int endId) {
        return new SMTLinkView(start, startId, end, endId);
    }

    public static SMTLinkView newLinkInProgress(Point2D start, int startId) {
        return new SMTLinkView(start, startId);
    }

}
