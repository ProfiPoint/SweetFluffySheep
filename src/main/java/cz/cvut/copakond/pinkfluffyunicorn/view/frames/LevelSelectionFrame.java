package cz.cvut.copakond.pinkfluffyunicorn.view.frames;

import cz.cvut.copakond.pinkfluffyunicorn.model.data.FileUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.data.FolderUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.data.JsonFileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.data.Level;
import cz.cvut.copakond.pinkfluffyunicorn.model.profile.ProfileManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.AppViewManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.DrawableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.ResizableFrame;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;

public class LevelSelectionFrame extends VBox implements ResizableFrame, DrawableFrame {
    private static String profileName;
    private static int storyLevelsCount;
    private static int customLevelsCount;

    private final Label storyLabel = new Label("Story Mode");
    private final Label userLabel = new Label("User Created Levels");
    private final Button backButton = new Button("Back");

    private final GridPane storyGrid = new GridPane();
    private final GridPane userGrid = new GridPane();
    private final VBox contentLayout = new VBox(50); // vertical spacing

    private final List<Button> storyButtons = new ArrayList<>();
    private final List<Button> userButtons = new ArrayList<>();

    private boolean editorMode = false;

    public LevelSelectionFrame() {
        init(false);
    }

    public LevelSelectionFrame(boolean editorMode) {
        init(editorMode);
    }

    private void init(boolean editorMode){
        this.editorMode = editorMode;

        profileName = ProfileManager.getCurrentProfile();
        storyLevelsCount = FileUtils.getNumberOfFilesInDirectory(FolderUtils.getLevelsPath());
        customLevelsCount = FileUtils.getNumberOfFilesInDirectory(
                FolderUtils.getProfilesPath() + "/" + profileName
        );

        if (editorMode) {
            customLevelsCount++;
            userLabel.setText("User Created Levels (Editor Mode: Click to edit)");
            storyLabel.setText("Story Mode (Editor Mode: Click to edit)");
        }

        setupGrid(storyGrid, storyLevelsCount, "Story", storyButtons);
        setupGrid(userGrid, customLevelsCount, "User", userButtons);

        storyLabel.setTextFill(Color.WHITE);
        userLabel.setTextFill(Color.WHITE);

        contentLayout.setAlignment(Pos.CENTER);
        contentLayout.getChildren().addAll(storyLabel, storyGrid, userLabel, userGrid);

        StackPane layout = new StackPane();
        layout.getChildren().add(contentLayout);

        backButton.setOnAction(e -> {
            AppViewManager.get().switchTo(new MenuFrame());
        });

        StackPane.setAlignment(backButton, Pos.TOP_RIGHT);
        backButton.setTranslateX(-20);
        backButton.setTranslateY(20);

        layout.getChildren().add(backButton);

        getChildren().add(layout);

        show(); // Initial draw
    }

    private void setupGrid(GridPane grid, int levelCount, String prefix, List<Button> list) {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        int cols = 10;
        for (int i = 0; i < levelCount; i++) {
            final int levelNumber = i + 1;

            int row = i / cols;
            int col = i % cols;

            Button levelButton = new Button(String.valueOf(levelNumber));
            if (!editorMode) {
                levelButton.setStyle("-fx-background-color: #444; -fx-text-fill: white;");
            } else if (i == levelCount -1) {
                levelButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: black;");
                levelButton.setText("+");
            } else {
                levelButton.setStyle("-fx-background-color: #fdab4d; -fx-text-fill: white;");
            }

            levelButton.setOnAction(e -> {
                System.out.println(prefix + " Level " + levelNumber + " clicked");
                Integer levelNum = levelNumber;
                Level level = new Level(Integer.toString(levelNum), editorMode);
                if (!level.loadLevel()) {
                    System.out.println("Level not loaded successfully");
                    return;
                }
                AppViewManager.get().switchTo(new LevelFrame(level, editorMode));
            });

            grid.add(levelButton, col, row);
            list.add(levelButton);
            GridPane.setHalignment(levelButton, HPos.CENTER);
        }
    }

    @Override
    public void onResizeCanvas(double width, double height) {
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

        backButton.setPrefWidth(buttonSize * 2);
        backButton.setPrefHeight(buttonSize);
        backButton.setStyle("-fx-font-size: " + (fontSize * 0.8) + "px;");
        backButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: " + (fontSize * 0.8) + "px;");

        contentLayout.setSpacing(height / 20);
    }

    private void updateButtonStyles(List<Integer> levelData, List<Button> buttons, String prefix) {
        for (int i = 0; i < levelData.size(); i++) {
            int levelNumber = levelData.get(i) - 1;
            Button button = buttons.get(levelNumber);
            if (button != null) {
                button.setStyle("-fx-background-color: green; -fx-text-fill: white;");
            } else {
                System.out.println("Button not found for " + prefix + " level " + (levelNumber + 1));
            }
        }
    }

    public void setButtonsCompleted(){
        List<List<Integer>> completedLevels = JsonFileManager.getProfileLFromJsonFile(
                FolderUtils.getProfilesPath() + "/" + profileName + "/_DATA.json"
        );
        if (completedLevels == null) {
            System.out.println("Error loading completed levels");
            return;
        }

        updateButtonStyles(completedLevels.get(0), storyButtons, "Story");
        updateButtonStyles(completedLevels.get(1), userButtons, "User");

    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, gc.getCanvas().getWidth(), gc.getCanvas().getHeight());
    }

    public void show() {
        if (!editorMode){setButtonsCompleted();}
        AppViewManager.get().update();
    }
}
