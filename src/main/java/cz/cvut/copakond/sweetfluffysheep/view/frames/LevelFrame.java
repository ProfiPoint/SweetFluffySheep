package cz.cvut.copakond.sweetfluffysheep.view.frames;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.GameStatusEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.levels.GamePhysics;
import cz.cvut.copakond.sweetfluffysheep.view.utils.AppViewManager;
import cz.cvut.copakond.sweetfluffysheep.view.interfaces.IInteractableFrame;
import cz.cvut.copakond.sweetfluffysheep.view.interfaces.ILevelFrame;
import cz.cvut.copakond.sweetfluffysheep.model.world.Level;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.view.utils.GameLoop;
import cz.cvut.copakond.sweetfluffysheep.view.interfaces.IClickListener;
import cz.cvut.copakond.sweetfluffysheep.model.utils.levels.LevelFrameUtils;
import javafx.event.Event;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.util.Comparator;
import java.util.List;
import java.util.logging.Logger;

/**
 * The LevelFrame class represents the main game screen where the level is displayed.
 * It handles user interactions, game loop, and rendering of game objects.
 */
public class LevelFrame extends VBox implements ILevelFrame, IInteractableFrame, IClickListener {
    private static final Logger logger = Logger.getLogger(LevelFrame.class.getName());

    private final GameLoop gameLoop;
    private final Canvas canvas;
    private final Region hudBar;

    private final Button speedButton = new Button("1x");
    private final Label coinsLabel = new Label("0/10");
    private final Label lifesLabel = new Label("3♥");
    private final Label sheepLabel = new Label("10🐑");
    private final Label arrowsLabel = new Label("0/20⬆");
    private final Label timeLabel = new Label("180s");
    private final Button pauseButton = new Button("Pause");
    private final Button retryButton = new Button("Retry");
    private final Button settingsButton = new Button("Settings");
    private final Button menuButton = new Button("Menu");

    private boolean popupShown = false;
    private final boolean isEditor;

    /**
     * Constructor for the LevelFrame class.
     *
     * @param level    The level to be displayed.
     * @param isEditor Indicates if the frame is in editor mode.
     */
    public LevelFrame(Level level, boolean isEditor) {
        AppViewManager.stopBackgroundVideo();

        this.isEditor = isEditor;
        this.canvas = new Canvas();
        this.gameLoop = new GameLoop(this, level);

        gameLoop.getLevel().Play();
        setAlignment(Pos.CENTER);
        getChildren().add(canvas);

        hudBar = createHudBar();
        getChildren().add(hudBar);

        AppViewManager.get().setClickListener(this); // register the click listener
        gameLoop.setObjects(level.getListOfObjects());
        gameLoop.getObjects().sort(Comparator.comparingInt(GameObject::getRenderPriority));
        gameLoop.resume();

        if (isEditor) {
            menuButton.setText("Edit");
        }

        SoundManager.playSound(SoundListEnum.GAME_THEME);
    }

    @Override
    public void handleClick(MouseEvent event) {
        // ignore if not running or if the click is within the first second of run time
        if (!gameLoop.isRunning() || gameLoop.elapsedSeconds() < 1) {
            return;
        }

        int button = event.getButton().name().equals("PRIMARY") ? 1 :
                event.getButton().name().equals("SECONDARY") ? 2 : 0;

        // status (-1 = invalid tile, 0 = remove, 1 = place), tile x (0 - mapSizeX-1), tile y (0 - mapSizeY-1)
        int[] canvasSize = {(int) canvas.getWidth(), (int) canvas.getHeight()};

        int[] appCanvasSize = {(int) AppViewManager.get().getScene().getWidth(),
                (int) AppViewManager.get().getScene().getHeight()};

        int[] tileClick = LevelFrameUtils.getTileClicked((int)event.getX(), (int)event.getY(), appCanvasSize,
                canvasSize, gameLoop.getLevel());

        if (tileClick[0] == -1) {
            return;
        }

        if (GamePhysics.tileNotExists(tileClick)) {
            return;
        }

        gameLoop.getLevel().placeRotateRemoveArrow(tileClick, button);

        // update objects to include the new arrows.
        gameLoop.setObjects(gameLoop.getLevel().getListOfObjects());
        gameLoop.getObjects().sort(Comparator.comparingInt(GameObject::getRenderPriority));
    }

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
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    /**
     * This method is called every frame to update the game state and render the game objects.
     */
    public void checkGameStatus(){
        GameStatusEnum gameStatus = GameObject.getGameStatus();
        if (gameStatus == GameStatusEnum.WIN) {
            gameLoop.getLevel().Completed(); // mark the level as completed
            showPopup("You Win!", true);
        } else if (gameStatus == GameStatusEnum.LOSE) {
            showPopup("You Lose!", false);
        }
    }

    /**
     * This method is called to draw the level objects on the canvas.
     */
    public void drawLevelObjects() {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(GameObject.getTextureManager().getTexture("background").getFirst(), 0, 0, canvas.getWidth(), canvas.getHeight());

        for (GameObject object : gameLoop.getObjects()) {
            if (object.isVisible()) {
                drawObject(gc, object);
            }
        }

        // update the HUD
        int[] coinsInfo = gameLoop.getLevel().getCoinsLeftAndCoins();
        coinsLabel.setText(coinsInfo[1]-coinsInfo[0] + "/" + coinsInfo[1]);
        lifesLabel.setText(gameLoop.getLevel().getLives() + "♥");
        sheepLabel.setText(gameLoop.getLevel().getSheepLeft() + "🐑");
        timeLabel.setText(gameLoop.getLevel().getTimeLeft() + "s");

        int[] arrowsInfo = gameLoop.getLevel().getArrowsInfo();
        arrowsLabel.setText(arrowsInfo[0] + "/" + arrowsInfo[1] + "⬆");
    }

    /**
     * This method is called to draw a specific game object on the canvas.
     *
     * @param gc     The GraphicsContext to draw on.
     * @param object The GameObject to be drawn.
     */
    private void drawObject(GraphicsContext gc, GameObject object) {
        double[] position = object.getScaledPositionSizePercentage(gameLoop.getLevel());
        position[0] *= canvas.getWidth();
        position[1] *= canvas.getHeight();

        double[] textureSizeRatio = object.getScaledTextureSizePercentage(gameLoop.getLevel());
        int width = (int) Math.ceil(canvas.getWidth() * textureSizeRatio[0]);
        int height = (int) Math.ceil(canvas.getHeight() * textureSizeRatio[1]);

        gc.drawImage(object.getTexture(), position[0], position[1], width, height);
    }

    /**
     * This method is called to adjust the font size of the HUD elements based on the canvas width.
     *
     * @param width The width of the canvas.
     */
    private void adjustFontSize(double width) {
        double fontSize = width / 60;

        // loop through all children of the hudBar and set the font size
        for (javafx.scene.Node node : hudBar.getChildrenUnmodifiable()) {
            if (node instanceof Button button) {
                String currentStyle = button.getStyle();
                button.setStyle(currentStyle + "-fx-font-size: " + fontSize + "px;");
            } else if (node instanceof Label label) {
                String currentStyle = label.getStyle();
                label.setStyle(currentStyle + "-fx-font-size: " + fontSize + "px;");
            }
        }
    }

    /**
     * This method is called to create the HUD bar at the bottom of the screen.
     *
     * @return The HBox containing the HUD elements.
     */
    private Pane createHudBar() {
        HBox bar = new HBox(20);
        bar.setPadding(new Insets(5, 15, 5, 15));
        bar.setStyle("-fx-background-color: #222;");
        bar.setAlignment(Pos.CENTER_LEFT);

        pauseButton.setOnAction(event -> {
            if (gameLoop.isRunning()) {
                pauseButton.setText("Resume");
                gameLoop.pause();
            } else {
                pauseButton.setText("Pause");
                gameLoop.resume();
            }
        });

        menuButton.setOnAction(e -> {
            if (isEditor) {
                // go back to the editor
                String[] levelData = gameLoop.getLevel().getLevelData();
                Level level = new Level(levelData[0], levelData[1].equals("true"), levelData[2].equals("true"));
                gameLoop.unload();
                if (!level.loadLevel()) {
                    logger.severe(ErrorMsgsEnum.LOAD_PARSING_ERROR.getValue());
                    return;
                }
                AppViewManager.get().switchTo(new LevelEditorFrame(level));
            } else {
                gameLoop.unload();
                AppViewManager.get().switchTo(new LevelSelectionFrame(false));
            }
        });

        retryButton.setOnAction(e -> {
            resetLevel();
        });

        settingsButton.setOnAction(e -> {
            AppViewManager.get().openSettings();
        });

        speedButton.setOnAction(e -> {
            double newSpeed = gameLoop.getAndChangeSpeed()/2.0;
            speedButton.setText(String.valueOf(newSpeed).replace(".0", "") + "x");
            pauseButton.setText("Pause");
            gameLoop.resume();
        });

        coinsLabel.setStyle("-fx-text-fill: gold; -fx-font-weight: bold;");
        lifesLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        sheepLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");
        arrowsLabel.setStyle("-fx-text-fill: #0077ff; -fx-font-weight: bold;");
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        coinsLabel.setMinWidth(60); coinsLabel.setPrefWidth(60);
        lifesLabel.setMinWidth(60); lifesLabel.setPrefWidth(60);
        sheepLabel.setMinWidth(60); sheepLabel.setPrefWidth(60);
        arrowsLabel.setMinWidth(60); arrowsLabel.setPrefWidth(60);
        timeLabel.setMinWidth(60); timeLabel.setPrefWidth(60);

        javafx.scene.Node[] nodes = {
                speedButton, coinsLabel, lifesLabel, sheepLabel, arrowsLabel,
                timeLabel, pauseButton, retryButton, settingsButton, menuButton
        };

        // set max width for all nodes in the bar
        for (javafx.scene.Node node : nodes) {
            if (node instanceof Region) {
                ((Region) node).setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(node, Priority.ALWAYS);
            }
        }

        bar.getChildren().addAll(nodes);

        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefWidth(Double.MAX_VALUE);

        return bar;
    }

    /**
     * This method is called to reset the level and start a new game.
     */
    private void resetLevel() {
        popupShown = false;
        speedButton.setText("1x");
        pauseButton.setText("Pause");
        gameLoop.resetLevel();
        SoundManager.playSound(SoundListEnum.GAME_THEME);
    }

    /**
     * This method is called to show a popup message when the game is over.
     *
     * @param message The message to be displayed.
     * @param isWin   Indicates if the game was won or lost.
     */
    private void showPopup(String message, boolean isWin) {
        if (popupShown) return;



        SoundManager.stopMusic();
        SoundManager.playSound(isWin ? SoundListEnum.FINISH : SoundListEnum.GAME_OVER);
        popupShown = true;

        Stage popupStage = new Stage();
        popupStage.setTitle("Game Result");
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(getScene().getWindow());

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Label descriptionLabel = new Label("");
        descriptionLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: lightgray;");
        descriptionLabel.setAlignment(Pos.CENTER);
        descriptionLabel.setTextAlignment(javafx.scene.text.TextAlignment.CENTER);
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(800);
        descriptionLabel.setPrefHeight(Region.USE_COMPUTED_SIZE);
        descriptionLabel.setMinHeight(Region.USE_PREF_SIZE);

        Button backToMenuButton = new Button(isEditor ? "Retry" : "Back to Menu");
        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        // set the icon for the popup window
        List<Image> iconImages = GameObject.getTextureManager().getTexture("icon");
        if (iconImages != null) {
            popupStage.getIcons().addAll(iconImages);
        } else {
            logger.severe(ErrorMsgsEnum.TEXTURE_MISSING.getValue("The file icon is missing"));
        }


        if (isWin) {
            Button nextLevelButton = new Button(isEditor ? "Edit" : "Next Level");

            // if the level is not accepted, show the retry button
            if (isEditor) {
                messageLabel.setText("Level Accepted!");
                descriptionLabel.setText("Level has been saved,\nyou can edit it any time later");
                nextLevelButton.setOnAction(e -> switchToEditor(popupStage));
            } else {
                // show all the stats
                int coinsCollected = gameLoop.getLevel().getCoinsLeftAndCoins()[1];
                int sheepInGoal = gameLoop.getLevel().getSheepInGoal();
                int sheepKilled = gameLoop.getLevel().getLevelInfo().get("sheep") - sheepInGoal;
                int enemiesKilled = gameLoop.getLevel().getEnemiesKilled();
                float totalTime = gameLoop.getLevel().getTimeElapsed();
                long totalScore = coinsCollected + sheepInGoal * 10L - sheepKilled * 20L + enemiesKilled * 20L;
                totalScore = (long) (100*totalScore * (gameLoop.getLevel().getLevelInfo().get("timeLimit")/totalTime));

                descriptionLabel.setText("Coins collected: " + coinsCollected + "\n" +
                        "Sheep saved: " + sheepInGoal + "\n" +
                        "Wolves killed: " + enemiesKilled + "\n" +
                        "Sheep killed: " + sheepKilled + "\n" +
                        "Level completed in: " + String.format("%.3f", totalTime) + " seconds\n" +
                        "TOTAL SCORE: " + String.format("%,d", totalScore));

                nextLevelButton.setOnAction(e -> {
                    gameLoop.pause();
                    popupStage.close();
                    speedButton.setText("1x");
                    pauseButton.setText("Pause");
                    SoundManager.playSound(SoundListEnum.GAME_THEME);
                    gameLoop.setNewLevel();
                    popupShown = false;
                });
            }

            backToMenuButton.setOnAction(e -> {
                popupStage.close();
                if (isEditor) resetLevel();
                else {
                    gameLoop.unload();
                    AppViewManager.get().switchTo(new LevelSelectionFrame(false));
                }
            });

            buttonBox.getChildren().addAll(backToMenuButton, nextLevelButton);

        } else {
            // lose

            Button retryLevelButton = new Button(isEditor ? "Edit" : "Retry");

            if (isEditor) {
                messageLabel.setText("Level Not Accepted!");
                descriptionLabel.setText("To save it properly,\nyou need to fix to complete the level to prove it is playable");
                retryLevelButton.setOnAction(e -> switchToEditor(popupStage));
            } else {
                // get the reason for losing
                if (gameLoop.getLevel().getTimeLeft() <= 0) {
                    descriptionLabel.setText("You have run out of time,\nbe faster next time");
                } else if (gameLoop.getLevel().getLives() <= 0) {
                    descriptionLabel.setText("You have lost all your lives,\nyou don't have enough sheep to complete the level");
                }

                retryLevelButton.setOnAction(e -> {
                    popupStage.close();
                    resetLevel();
                });
            }

            backToMenuButton.setOnAction(e -> {
                popupStage.close();
                if (isEditor) resetLevel();
                else {
                    gameLoop.unload();
                    AppViewManager.get().switchTo(new LevelSelectionFrame(false));
                }
            });

            buttonBox.getChildren().addAll(backToMenuButton, retryLevelButton);
        }

        VBox popupLayout = new VBox(20, messageLabel, descriptionLabel, buttonBox);
        popupLayout.setPadding(new Insets(20));
        popupLayout.setAlignment(Pos.CENTER);
        popupLayout.setStyle("-fx-background-color: #333; -fx-border-color: white; -fx-border-width: 2px;");

        Scene popupScene = new Scene(popupLayout, 350, 275);
        popupStage.setScene(popupScene);
        popupStage.setResizable(false);
        popupStage.setOnCloseRequest(Event::consume);
        popupStage.showAndWait();
    }

    /**
     * This method is called to switch to the level editor.
     *
     * @param popupStage The stage of the popup window.
     */
    private void switchToEditor(Stage popupStage) {
        String[] levelData = gameLoop.getLevel().getLevelData();
        Level level = new Level(levelData[0], levelData[1].equals("true"), levelData[2].equals("true"));
        gameLoop.unload();
        if (!level.loadLevel()) {
            logger.severe(ErrorMsgsEnum.LOAD_PARSING_ERROR.getValue());
            return;
        }
        popupStage.close();
        AppViewManager.get().switchTo(new LevelEditorFrame(level));
    }
}
