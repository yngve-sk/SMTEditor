package application_smtview;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import model.SMTNode;

/**
 * Represents all the stats of a node
 * @author Yngve Sekse Kristiansen
 *
 */
public class StatsView extends Region {

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

    private final double width = 150;
    private final double height = 150;
    private ImageView background;

    /**
     * Initiates a new stats view
     */
    public StatsView() {
        super();

//        background = new ImageView(new Image("images/statsviewbackground.jpg"));
//        background.setFitWidth(width);
//        background.setFitHeight(height);

        // negative insets to expand outward
        BackgroundFill fill = new BackgroundFill(Color.BLACK, new CornerRadii(5), new Insets(-8, -8, -8, -8));
        Background background = new Background(fill);
        this.setBackground(background);

        this.setOpacity(0.8);

        cost = new Label();
        type = new Label();

        powerLevelOne = new Label();
        powerLevelTwo = new Label();

        position = new Label();

        Label[] allLabels = {type, cost, powerLevelOne, powerLevelTwo, position};
        for(Label l : allLabels) {
            l.setAlignment(Pos.BASELINE_CENTER);
            l.setTextFill(Color.SNOW);
        }

        getChildren().addAll(allLabels);

        this.resizeRelocate(0, 0, width, height);
        this.setVisible(false);
    }

    /**
     * Displays the stats of a SMTNode
     * @param node
     *      the node
     */
    public void displayNode(SMTNode node) {
    	type.setText(TYPE_STR + node.getType() + " (id = " + node.id + ")");
        cost.setText(COST_STR + node.getNodeCost());
        powerLevelOne.setText(POWER_ONE_STR + node.getHighestPowerLevel());
        powerLevelTwo.setText(POWER_TWO_STR + node.getSecondHighestPowerLevel());
        position.setText(POSITION_STR + node.getPosition());
        this.setVisible(true);
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

    }
}
