package model;

import java.util.ArrayList;
import java.util.List;

import javafx.geometry.Point2D;

/**
 * Small factory for creating a blank SMT
 * @author Yngve Sekse Kristiansen
 *
 */
public class SMTFactory {

    public static SharedMulticastTree emptyTree() {
        return new SharedMulticastTree(new ArrayList<Point2D>(), new ArrayList<List<Integer>>(), 0);
    }
}
