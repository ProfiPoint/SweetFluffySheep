package cz.cvut.copakond.sweetfluffysheep.view.frames;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.FileUtils;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.FolderUtils;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.json.JsonFileManager;
import cz.cvut.copakond.sweetfluffysheep.model.world.Level;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.ProfileManager;
import cz.cvut.copakond.sweetfluffysheep.view.utils.AppViewManager;
import cz.cvut.copakond.sweetfluffysheep.view.interfaces.IInteractableFrame;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * The LevelSelectionFrame class represents the level selection screen in the game.
 * It allows players to choose between story mode and user-created levels.
 */
public class LevelSelectionFrame extends VBox implements IInteractableFrame {
    private static final Logger logger = Logger.getLogger(LevelSelectionFrame.class.getName());
    private static String profileName;

    private final Label storyLabel = new Label("Story Mode");
    private final Label userLabel = new Label("User Created Levels");
    private final Button backButton = new Button("Back");

    private final GridPane storyGrid = new GridPane();
    private final GridPane userGrid = new GridPane();
    private final VBox contentLayout = new VBox(50);

    private final List<Button> storyButtons = new ArrayList<>();
    private final List<Button> userButtons = new ArrayList<>();

    private boolean editorMode = false;

    /**
     * Constructor for the LevelSelectionFrame class.
     * Initializes the frame with the specified editor mode.
     *
     * @param editorMode true if the frame is in editor mode, false otherwise
     */
    public LevelSelectionFrame(boolean editorMode) {
        init(editorMode);
        SoundManager.playSound(SoundListEnum.MENU_THEME);
    }

    /**
     * Constructor for the LevelSelectionFrame class.
     * Initializes the frame in normal mode.
     * @param editorMode if true, it will allow levels to be edited
     */
    private void init(boolean editorMode) {
        this.editorMode = editorMode;

        profileName = ProfileManager.getCurrentProfile();
        int storyLevelsCount = FileUtils.getNumberOfFilesInDirectory(FolderUtils.getLevelsPath());
        int customLevelsCount = FileUtils.getNumberOfFilesInDirectory(FolderUtils.getProfilesPath() + "/" + profileName);

        // Set up the grid based on the editor mode
        if (editorMode) {
            customLevelsCount++;
            userLabel.setText("User Created Levels (Editor Mode: Click to edit)");
            contentLayout.getChildren().addAll(storyGrid, userLabel, userGrid);
        } else {
            setupGrid(storyGrid, storyLevelsCount, "Story", storyButtons);
            contentLayout.getChildren().addAll(storyLabel, storyGrid, userLabel, userGrid);
        }

        setupGrid(userGrid, customLevelsCount, "User", userButtons);

        storyLabel.setTextFill(Color.WHITE);
        userLabel.setTextFill(Color.WHITE);
        contentLayout.setAlignment(Pos.CENTER);

        StackPane layout = new StackPane();
        layout.getChildren().add(contentLayout);

        backButton.setOnAction(e -> AppViewManager.get().switchTo(new MenuFrame()));
        StackPane.setAlignment(backButton, Pos.TOP_RIGHT);
        backButton.setTranslateX(-20);
        backButton.setTranslateY(20);

        layout.getChildren().add(backButton);
        getChildren().add(layout);

        show();
    }

    /**
     * Sets up the grid for the level selection screen.
     * Creates buttons for each level and adds them to the grid.
     *
     * @param grid       The GridPane to set up
     * @param levelCount The number of levels to display
     * @param prefix     The prefix for the level type (e.g., "Story" or "User")
     * @param list       The list to store the buttons
     */
    private void setupGrid(GridPane grid, int levelCount, String prefix, List<Button> list) {
        grid.setAlignment(Pos.CENTER);
        grid.setHgap(10);
        grid.setVgap(10);

        int cols = 10;
        for (int i = 0; i < levelCount; i++) {
            final int levelNumber = i + 1;
            int row = i / cols;
            int col = i % cols;

            // Create a button for each level, if in editor mode, add a "+" button for the last level
            Button levelButton = new Button(String.valueOf(levelNumber));
            if (!editorMode) {
                levelButton.setStyle("-fx-background-color: #444; -fx-text-fill: white;");
            } else if (i == levelCount - 1) {
                levelButton.setStyle("-fx-background-color: #ffffff; -fx-text-fill: black;");
                levelButton.setText("+");
            } else {
                levelButton.setStyle("-fx-background-color: #fdab4d; -fx-text-fill: white;");
            }

            levelButton.setOnAction(e -> {
                logger.info(prefix + " Level " + levelNumber + " clicked");
                Level level = new Level(Integer.toString(levelNumber), editorMode, prefix.equals("Story"),
                        levelButton.getText().equals("+"));

                if (!level.loadLevel()) {
                    logger.severe(ErrorMsgsEnum.LOAD_ERROR.getValue());
                    return;
                }

                // Sets to red, to detect the level cannot be loaded, if is loaded, the user won't see it
                levelButton.setStyle("-fx-background-color: red; -fx-text-fill: white;");
                if (editorMode) {
                    AppViewManager.get().switchTo(new LevelEditorFrame(level)); // Switch to the level editor
                } else {
                    AppViewManager.get().switchTo(new LevelFrame(level, false)); // Switch to the level frame
                }
            });

            grid.add(levelButton, col, row);
            list.add(levelButton);
            GridPane.setHalignment(levelButton, HPos.CENTER);
        }
    }

    /**
     * Updates the button styles based on the completed levels.
     * For each completed level, it sets the button style to indicate completion.
     *
     * @param levelData The list of completed levels
     * @param buttons   The list of buttons to update
     * @param prefix    The prefix for the level type (e.g., "Story" or "User")
     */
    private void updateButtonStyles(List<Integer> levelData, List<Button> buttons, String prefix) {
        for (Integer levelDatum : levelData) {
            int levelNumber = levelDatum - 1;
            if (levelNumber < 0 || levelNumber >= buttons.size()) {
                logger.info(ErrorMsgsEnum.LOAD_INVALID_LEVEL_NAME.getValue(String.valueOf(levelNumber)));
                continue;
            }

            Button button = buttons.get(levelNumber);
            if (button != null) {
                button.setStyle("-fx-background-color: green; -fx-text-fill: white;");
            } else {
                logger.info(prefix + " level " + (levelNumber + 1));
                logger.severe(ErrorMsgsEnum.LOAD_BUTTON_NOT_FOUND.getValue(prefix + " level " + (levelNumber + 1)));
            }
        }
    }

    /**
     * Sets the button styles to indicate completed levels.
     * Loads the completed levels from a JSON file and updates the button styles accordingly.
     */
    public void setButtonsCompleted() {
        List<List<Integer>> completedLevels = JsonFileManager.getProfileLFromJsonFile(
                FolderUtils.getProfilesPath() + "/" + profileName + "/_DATA.json"
        );

        if (completedLevels == null) {
            logger.severe(ErrorMsgsEnum.LOAD_COMPLETED_LEVELS.getValue());
            return;
        }

        updateButtonStyles(completedLevels.get(0), storyButtons, "Story");
        updateButtonStyles(completedLevels.get(1), userButtons, "User");
    }

    /**
     * Show the initial state of the ProfileFrame.
     */
    public void show() {
        if (!editorMode) {
            setButtonsCompleted();
        }
        AppViewManager.get().update();
    }

    @Override
    public void onResizeCanvas(double width, double height) {
        double fontSize = height / 30;
        double buttonSize = Math.min(width / 12, height / 12);

        storyLabel.setFont(Font.font("Arial", fontSize * 1.5));
        userLabel.setFont(Font.font("Arial", fontSize * 1.5));

        // Adapt the button size and font size for each button in the grids
        storyGrid.getChildren().forEach(node -> {
            if (node instanceof Button b) {
                b.setMinSize(buttonSize, buttonSize);
                b.setMaxSize(buttonSize, buttonSize);
                b.setFont(Font.font(fontSize));
            }
        });

        // Adapt the button size and font size for each button in the grids
        userGrid.getChildren().forEach(node -> {
            if (node instanceof Button b) {
                b.setMinSize(buttonSize, buttonSize);
                b.setMaxSize(buttonSize, buttonSize);
                b.setFont(Font.font(fontSize));
            }
        });

        backButton.setPrefWidth(buttonSize * 2);
        backButton.setPrefHeight(buttonSize);
        backButton.setStyle("-fx-background-color: red; -fx-text-fill: white; -fx-font-size: " + (fontSize * 0.8) + "px;");
        contentLayout.setSpacing(height / 20);
    }

    @Override
    public void draw(GraphicsContext gc) {
        AppViewManager.playBackgroundVideo();
        AppViewManager.get().resizeBackgroundVideo();
    }
}