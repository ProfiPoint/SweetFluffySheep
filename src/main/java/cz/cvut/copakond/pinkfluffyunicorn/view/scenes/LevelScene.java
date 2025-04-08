package cz.cvut.copakond.pinkfluffyunicorn.view.scenes;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LevelScene extends ResponsiveScene {

    private final Label levelLabel = new Label("Level Scene - Gameplay!");

    public LevelScene(Stage stage) {
        super(stage);

        levelLabel.setTextFill(Color.LIGHTGREEN);
        overlay.getChildren().add(levelLabel);
    }

    @Override
    protected void onResizeCanvas(double width, double height) {
        double fontSize = height / 20;
        levelLabel.setFont(Font.font("Arial", fontSize));
    }

    @Override
    protected void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.DARKSLATEBLUE);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        // Optional: draw game elements here
    }
}
