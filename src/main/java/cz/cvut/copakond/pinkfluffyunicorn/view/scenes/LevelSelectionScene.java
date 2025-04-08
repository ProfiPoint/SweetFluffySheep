package cz.cvut.copakond.pinkfluffyunicorn.view.scenes;

import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.stage.Stage;

public class LevelSelectionScene extends ResponsiveScene {

    private static final int STORY_MODE_LEVELS = 21;
    private static final int USER_CREATED_LEVELS = 7; // for now

    private final Label storyLabel = new Label("Story Mode");
    private final Label userLabel = new Label("User Created Levels");
    private final Button backButton = new Button("Back");

    private final GridPane storyGrid = new GridPane();
    private final GridPane userGrid = new GridPane();
    private final VBox contentLayout = new VBox(50); // vertical spacing

    public LevelSelectionScene(Stage stage) {
        super(stage);

        setupGrid(storyGrid, STORY_MODE_LEVELS, "Story");
        setupGrid(userGrid, USER_CREATED_LEVELS, "User");

        storyLabel.setTextFill(Color.WHITE);
        userLabel.setTextFill(Color.WHITE);

        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.getChildren().addAll(storyLabel, storyGrid, userLabel, userGrid);
        overlay.getChildren().add(contentLayout);




        backButton.setOnAction(e -> {
            new MenuScene(stage).show();
        });

        // Position Back button in top-right with offset
        StackPane.setAlignment(backButton, Pos.TOP_RIGHT);
        backButton.setTranslateX(-20); // 20px from right
        backButton.setTranslateY(20);  // 20px from top
        overlay.getChildren().add(backButton);

    }

    private void setupGrid(GridPane grid, int levelCount, String prefix) {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        int cols = 10;
        for (int i = 0; i < levelCount; i++) {
            final int levelNumber = i + 1; // 👈 create a final copy

            int row = i / cols;
            int col = i % cols;

            Button levelButton = new Button(String.valueOf(levelNumber));
            levelButton.setStyle("-fx-background-color: #444; -fx-text-fill: white;");
            levelButton.setOnAction(e -> System.out.println(prefix + " Level " + levelNumber + " clicked"));

            grid.add(levelButton, col, row);
            GridPane.setHalignment(levelButton, HPos.CENTER);
        }

    }

    @Override
    protected void onResizeCanvas(double width, double height) {
        double fontSize = height / 30; // adjust as needed
        double buttonSize = Math.min(width / 12, height / 12); // square button

        storyLabel.setFont(Font.font("Arial", fontSize * 1.5));
        userLabel.setFont(Font.font("Arial", fontSize * 1.5));

        storyGrid.getChildren().forEach(node -> {
            if (node instanceof Button b) {
                b.setMinSize(buttonSize, buttonSize);
                b.setMaxSize(buttonSize, buttonSize);
                b.setFont(Font.font(fontSize));
            }
        });

        userGrid.getChildren().forEach(node -> {
            if (node instanceof Button b) {
                b.setMinSize(buttonSize, buttonSize);
                b.setMaxSize(buttonSize, buttonSize);
                b.setFont(Font.font(fontSize));
            }
        });

        backButton.setPrefWidth(buttonSize*2);
        backButton.setPrefHeight(buttonSize);
        backButton.setStyle("-fx-font-size: " + (fontSize * 0.8) + "px;");
        backButton.setAlignment(Pos.CENTER);
        backButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: " + (fontSize * 0.8) + "px;");


        contentLayout.setSpacing(height / 20);
    }


    @Override
    protected void draw() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
