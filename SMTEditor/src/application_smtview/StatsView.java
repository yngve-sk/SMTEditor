package application_smtview;

import javafx.geometry.Pos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.effect.BlendMode;
import model.SMTNode;

/**
 * Represents all the stats of a node
 * @author Yngve Sekse Kristiansen
 *
 */
public class StatsView extends Group {

    private Label cost;
    private Label type;
    private Label powerLevelOne;
    private Label powerLevelTwo;
    private Label position;

    private final String COST_STR = "Cost: ";
    private final String TYPE_STR = "Type: ";
    private final String POWER_ONE_STR = "Highest Power Level: ";
    private final String POWER_TWO_STR = "2nd Highest Power Level: ";
    private final String POSITION_STR = "Position: ";

    private double width = 150;
    private double height = 200;

    /**
     * Initiates a new stats view
     */
    public StatsView() {
        super();

        cost = new Label();
        type = new Label();

        powerLevelOne = new Label();
        powerLevelTwo = new Label();

        position = new Label();

        Label[] allLabels = {cost, type, powerLevelOne, powerLevelTwo, position};
        for(Label l : allLabels)
            l.setAlignment(Pos.BASELINE_CENTER);

        this.setBlendMode(BlendMode.SRC_OVER);
        getChildren().addAll(allLabels);
    }

    /**
     * Displays the stats of a SMTNode
     * @param node
     *      the node
     */
    public void displayNode(SMTNode node) {
        cost.setText(COST_STR + node.getNodeCost());
        type.setText(TYPE_STR + node.getType());
        powerLevelOne.setText(POWER_ONE_STR + node.getHighestPowerLevel());
        powerLevelTwo.setText(POWER_TWO_STR + node.getSecondHighestPowerLevel());
        position.setText(POSITION_STR + node.getPosition());
    }

    @Override
    public void resizeRelocate(double x, double y, double width, double height) {
        super.resizeRelocate(x, y, width, height);
        layoutSubviews();
    }

    /**
     * Decorates resizeRelocate
     */
    private void layoutSubviews() {
        double cellWidth = width;

        int numCells = getChildren().size();
        double cellHeight = height/numCells;

        int cellNumber = 0;
        for(Node n : getChildren())
            n.resizeRelocate(0, cellNumber++*cellHeight, cellWidth, cellHeight);
        // TODO font resizing?
    }
}
