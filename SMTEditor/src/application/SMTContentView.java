package application;

import java.util.List;

import javafx.scene.Group;
import model.Node;
import model.SharedMulticastTree;


@SuppressWarnings("unused")
/**
 * This will be placed inside the SMTView, this represents the tree itself,
 * the SMTView will be the ScrollView viewing this
 * @author admin
 *
 */
public class SMTContentView extends Group {


    private final double referenceDimension = 1000; // TODO might need to be fit to default input dimension
    private final double maxDimension = 5000;

    private double currentDimension;

    public SMTContentView() {
        currentDimension = referenceDimension;
    }

    public void draw(SharedMulticastTree tree) {
        this.getChildren().clear();

        List<Node> nodes = tree.getNodes();
        for(Node n : nodes) {

        }

    }

    private double transformCoordinateValueFromModelToVisual(double modelValue) {
        return modelValue*modelToVisual();
    }

    private double modelToVisual() {
        return currentDimension/referenceDimension;
    }

    private double transformCoordinateValueFromVisualToModel(double visualValue) {
        return visualValue*visualToModel();
    }

    private double visualToModel() {
        return referenceDimension/currentDimension;
    }

}
