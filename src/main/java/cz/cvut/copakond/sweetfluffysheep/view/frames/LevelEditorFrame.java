package cz.cvut.copakond.sweetfluffysheep.view.frames;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.LevelEditorObjectsEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.levels.LevelEditorUtils;
import cz.cvut.copakond.sweetfluffysheep.model.utils.levels.PathFinder;
import cz.cvut.copakond.sweetfluffysheep.view.utils.AppViewManager;
import cz.cvut.copakond.sweetfluffysheep.model.world.Level;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.view.utils.GameLoop;
import cz.cvut.copakond.sweetfluffysheep.view.interfaces.IClickListener;
import cz.cvut.copakond.sweetfluffysheep.model.utils.levels.LevelFrameUtils;
import cz.cvut.copakond.sweetfluffysheep.view.interfaces.IInteractableFrame;
import cz.cvut.copakond.sweetfluffysheep.view.interfaces.ILevelFrame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import javafx.stage.Modality;

import java.io.File;
import java.util.*;
import java.util.logging.Logger;

/**
 * The LevelEditorFrame class represents the level editor interface in the game.
 * It allows users to create and edit levels by placing objects on a grid.
 * The class handles user interactions, rendering of objects, and saving levels.
 */
public class LevelEditorFrame extends VBox implements ILevelFrame, IInteractableFrame, IClickListener {
    private static final Logger logger = Logger.getLogger(LevelEditorFrame.class.getName());

    private final GameLoop gameLoop;
    private final Canvas canvas;
    private final GridPane hudBar;

    private LevelEditorObjectsEnum selectedObject = LevelEditorObjectsEnum.EMPTY;

    private final Button playButton =      new Button("   [Play]   ");
    private final Button variablesButton = new Button("Variables");
    private final Button settingsButton =  new Button("Settings ");
    private final Button menuButton =      new Button("   Menu   ");

    private static String texturePath;

    public static void setTexturePath(String path) {
        texturePath = path;
    }

    /**
     * Constructor for the LevelEditorFrame class.
     * Initializes the game loop, canvas, and HUD bar.
     * Sets up the click listener and loads the level.
     *
     * @param level The level to be edited.
     */
    public LevelEditorFrame(Level level) {
        AppViewManager.stopBackgroundVideo();

        gameLoop = new GameLoop(this, level);
        this.canvas = new Canvas();
        this.gameLoop.getLevel().Play();

        setAlignment(Pos.CENTER);
        getChildren().add(canvas);

        hudBar = createEditorHudBar();
        getChildren().add(hudBar);

        AppViewManager.get().setClickListener(this);
        LevelEditorUtils.setLevel(level);
        gameLoop.setObjects(gameLoop.getLevel().getListOfObjects());
        gameLoop.getObjects().sort(Comparator.comparingInt(GameObject::getRenderPriority));
        gameLoop.renderScene();

        SoundManager.playSound(SoundListEnum.EDITOR_THEME);
    }

    // Level Editor can never be won or lost, only the level itself.
    public void checkGameStatus(){}

    @Override
    public void onResizeCanvas(double width, double height) {
        double canvasHeight = height * (1 - 11.111 / 100);
        canvas.setWidth(width);
        canvas.setHeight(canvasHeight);

        double hudHeight = height * 11.111 / 100;
        hudBar.setMinHeight(hudHeight);
        hudBar.setMaxHeight(hudHeight);
        hudBar.setPrefHeight(hudHeight);
        hudBar.setPrefWidth(width);

        hudBar.setLayoutX(0);
        hudBar.setLayoutY(canvasHeight);

        adjustFontSize(width);
        gameLoop.renderScene();
    }

    @Override
    public void handleClick(MouseEvent event) {
        int[] canvasSize = {(int) canvas.getWidth(), (int) canvas.getHeight()};
        int[] appCanvasSize = {(int) AppViewManager.get().getScene().getWidth(),
                (int) AppViewManager.get().getScene().getHeight()};

        int[] tileClick = LevelFrameUtils.getTileClicked(
                (int) event.getX(), (int) event.getY(),
                appCanvasSize, canvasSize,
                gameLoop.getLevel()
        );
        if (tileClick[0] == -1) return;

        // Sends a signal to add the object to the level
        LevelEditorUtils.addObjectToLevel(
                new double[]{tileClick[0], tileClick[1]},
                selectedObject
        );

        gameLoop.setObjects(gameLoop.getLevel().getListOfObjects());
        gameLoop.getObjects().sort(Comparator.comparingInt(GameObject::getRenderPriority));
        gameLoop.renderScene();
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * Draws the level objects on the canvas.
     * Clears the canvas and draws the background image.
     * Iterates through all game objects and draws them if they are visible.
     */
    public void drawLevelObjects() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(GameObject.getTextureManager().getTexture("background").getFirst(), 0, 0, canvas.getWidth(), canvas.getHeight());

        for (GameObject object : gameLoop.getObjects()) {
            if (object.isVisible()) {
                drawObject(gc, object);
            }
        }
    }

    /**
     * Saves the current level.
     * Checks if the level can be completed before saving.
     * Displays an error message if the level is invalid.
     *
     * @param headerText The header text for the error message.
     * @return true if the level is saved successfully, false otherwise.
     */
    private boolean saveLevel(String headerText) {
        Level level = gameLoop.getLevel();
        PathFinder pathFinder = new PathFinder(level);
        boolean valid = pathFinder.canLevelBeCompleted();
        if (valid) {
            return level.saveLevel();
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Level");
            alert.setHeaderText(headerText);
            alert.setContentText("Reason: " + pathFinder.reasonForFailure());
            alert.showAndWait();
            return false;
        }
    }

    /**
     * Creates the bottom HUD bar for the level editor.
     * Contains buttons for different actions and object selection.
     *
     * @return The created HUD bar as a GridPane.
     */
    private GridPane createEditorHudBar() {
        GridPane bar = new GridPane();
        bar.setStyle("-fx-background-color: #222;");
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(1));
        bar.setHgap(5);

        String[] imageNames = {
                "tile", "removeTile", "wolf", "coin", "freeze", "rage", "start", "goal", "rotate", "destroy"
        };

        int col = 0;
        List<Button> imgButtons = new ArrayList<>();
        for (String name : imageNames) {
            String fullPath = texturePath + "/editor/" + name + ".png";
            Image image;
            try {
                image = new Image(new File(fullPath).toURI().toURL().toExternalForm());
            } catch (java.net.MalformedURLException e) {
                logger.severe(ErrorMsgsEnum.TEXTURE_MISSING.getValue(fullPath + " " + e.getMessage()));
                continue;
            }

            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);

            Button imgButton = new Button();
            imgButton.setGraphic(imageView);
            imgButton.setStyle("-fx-background-color: transparent;");
            bar.add(imgButton, col++, 0);
            imgButtons.add(imgButton);

            imgButton.setOnAction(event -> {
                for (Button btn : imgButtons) {
                    btn.setStyle("-fx-background-color: transparent;");
                }
                imgButton.setStyle("-fx-background-color: green;");
                selectedObject = LevelEditorObjectsEnum.valueOf(name.toUpperCase());
            });
        }

        bar.add(playButton, col++, 0);
        bar.add(variablesButton, col++, 0);
        bar.add(settingsButton, col++, 0);
        bar.add(menuButton, col++, 0);

        // Make sure the buttons are evenly spaced
        for (int i = 0; i < col; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / col);
            bar.getColumnConstraints().add(cc);
        }

        setupHudBarButtonActions();

        return bar;
    }

    /**
     * Sets up the actions for the buttons in the HUD bar.
     * Defines what happens when each button is clicked.
     */
    private void setupHudBarButtonActions() {
        playButton.setOnAction(event -> {
            if (!saveLevel("Level cannot be completed")) return;
            loadLevel(false);
        });

        variablesButton.setOnAction(event -> {
            openLevelSettingsDialog();
        });

        settingsButton.setOnAction(e -> AppViewManager.get().openSettings());

        menuButton.setOnAction(e -> {
            gameLoop.unload();
            AppViewManager.get().switchTo(new LevelSelectionFrame(true));
        });
    }

    /**
     * Opens a dialog for level settings.
     * Allows the user to modify various parameters of the level.
     */
    private void openLevelSettingsDialog() {
        Dialog<ButtonType> dialog = new Dialog<>();
        dialog.setTitle("Level Settings");
        dialog.initOwner(AppViewManager.get().getStage());
        dialog.initModality(Modality.APPLICATION_MODAL);

        ButtonType confirmButtonType = new ButtonType("Confirm");
        ButtonType cancelButtonType = new ButtonType("Cancel");
        dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);

        Map<String, Integer> levelInfo = gameLoop.getLevel().getLevelInfo();
        int[] mapSize = gameLoop.getLevel().getMapSize();

        // Create spinners for each parameter
        Spinner<Integer> timeLimitSpinner = new Spinner<>(10, Integer.MAX_VALUE, levelInfo.get("timeLimit"));
        Spinner<Integer> sheepSpinner = new Spinner<>(1, Integer.MAX_VALUE, levelInfo.get("sheep"));
        Spinner<Integer> goalSheepSpinner = new Spinner<>(1, Integer.MAX_VALUE, levelInfo.get("goalSheep"));
        Spinner<Integer> maxArrowsSpinner = new Spinner<>(1, mapSize[0] * mapSize[1], levelInfo.get("maxArrows"));
        Spinner<Integer> mapSizeXSpinner = new Spinner<>(1, Integer.MAX_VALUE, mapSize[0]);
        Spinner<Integer> mapSizeYSpinner = new Spinner<>(1, Integer.MAX_VALUE, mapSize[1]);
        Spinner<Integer> itemDurationSpinner = new Spinner<>(1, Integer.MAX_VALUE, levelInfo.get("defaultItemDuration"));

        // Make spinners editable to allow manual input
        timeLimitSpinner.setEditable(true);
        sheepSpinner.setEditable(true);
        goalSheepSpinner.setEditable(true);
        maxArrowsSpinner.setEditable(true);
        mapSizeXSpinner.setEditable(true);
        mapSizeYSpinner.setEditable(true);
        itemDurationSpinner.setEditable(true);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        grid.add(new Label("Time Limit (s):"), 0, 0);
        grid.add(timeLimitSpinner, 1, 0);
        grid.add(new Label("Sheep:"), 0, 1);
        grid.add(sheepSpinner, 1, 1);
        grid.add(new Label("Goal Sheep:"), 2, 1);
        grid.add(goalSheepSpinner, 3, 1);
        grid.add(new Label("Max Arrows:"), 0, 2);
        grid.add(maxArrowsSpinner, 1, 2);
        grid.add(new Label("Map Size (Width):"), 0, 3);
        grid.add(mapSizeXSpinner, 1, 3);
        grid.add(new Label("Map Size (Height):"), 2, 3);
        grid.add(mapSizeYSpinner, 3, 3);
        grid.add(new Label("Item Duration (s):"), 0, 4);
        grid.add(itemDurationSpinner, 1, 4);

        dialog.getDialogPane().setContent(grid);

        // Set the default button to confirm
        Optional<ButtonType> result = dialog.showAndWait();
        if (result.isPresent() && result.get() == confirmButtonType) {
            if (goalSheepSpinner.getValue() > sheepSpinner.getValue()) {
                goalSheepSpinner.getValueFactory().setValue(sheepSpinner.getValue());
            }

            levelInfo.put("timeLimit", timeLimitSpinner.getValue());
            levelInfo.put("sheep", sheepSpinner.getValue());
            levelInfo.put("goalSheep", goalSheepSpinner.getValue());
            levelInfo.put("maxArrows", maxArrowsSpinner.getValue());
            levelInfo.put("defaultItemDuration", itemDurationSpinner.getValue());

            logger.info("Level settings updated: " + levelInfo);

            if (mapSize[0] != mapSizeXSpinner.getValue() || mapSize[1] != mapSizeYSpinner.getValue()) {
                mapSize[0] = mapSizeXSpinner.getValue();
                mapSize[1] = mapSizeYSpinner.getValue();
                logger.info("Map size updated: " + Arrays.toString(mapSize));

                // try to save the level before reloading
                if (!saveLevel("Level can not be resized")) return;

                // Reload the level with the new map size
                loadLevel(true);
            }
        }
    }

    /**
     * Loads the level from the game loop.
     * Unloads the current level and loads a new one.
     * Switches to the LevelEditorFrame or LevelFrame with the new level, if successful.
     */
    private void loadLevel(boolean switchToEditor) {
        String[] levelData = gameLoop.getLevel().getLevelData();
        Level newLevel = new Level(levelData[0], levelData[1].equals("true"), levelData[2].equals("true"));
        gameLoop.unload();
        if (!newLevel.loadLevel()) {
            logger.severe(ErrorMsgsEnum.LOAD_PARSING_ERROR.getValue());
            return;
        }
        if (switchToEditor){
            AppViewManager.get().switchTo(new LevelEditorFrame(newLevel));
        } else {
            AppViewManager.get().switchTo(new LevelFrame(newLevel, true));
        }

    }

    /**
     * Adjusts the font size of the buttons and labels in the HUD bar based on the canvas width.
     *
     * @param width The width of the canvas.
     */
    private void adjustFontSize(double width) {
        double fontSize = width / 90;

        for (Node node : hudBar.getChildrenUnmodifiable()) {
            if (node instanceof Button button) {
                String currentStyle = button.getStyle();
                button.setStyle(currentStyle + "-fx-font-size: " + fontSize + "px;");
            } else if (node instanceof Label label) {
                String currentStyle = label.getStyle();
                label.setStyle(currentStyle + "-fx-font-size: " + fontSize + "px;");
            }

            // Adjust the size of the image in the button
            if (node instanceof Button button && button.getGraphic() instanceof ImageView imageView) {
                double size = fontSize * 2.5;
                imageView.setFitWidth(size);
                imageView.setFitHeight(size);
            }
        }
    }

    /**
     * Draws a game object on the canvas.
     * Calculates the position and size based on the level and canvas dimensions.
     *
     * @param gc     The GraphicsContext to draw on.
     * @param object The GameObject to be drawn.
     */
    private void drawObject(GraphicsContext gc, GameObject object) {
        double[] position = object.getScaledPositionSizePercentage(gameLoop.getLevel());
        position[0] *= canvas.getWidth();
        position[1] *= canvas.getHeight();

        // Calculate the size of the object based on the canvas size and texture size ratio
        double[] textureSizeRatio = object.getScaledTextureSizePercentage(gameLoop.getLevel());
        int[] textureSize = {
                (int) Math.ceil(canvas.getWidth() * textureSizeRatio[0]),
                (int) Math.ceil(canvas.getHeight() * textureSizeRatio[1])
        };

        gc.drawImage(object.getTexture(), position[0], position[1], textureSize[0], textureSize[1]);
    }
}