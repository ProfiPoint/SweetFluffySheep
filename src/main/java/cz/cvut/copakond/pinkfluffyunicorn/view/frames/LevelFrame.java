package cz.cvut.copakond.pinkfluffyunicorn.view.frames;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.GameStatusEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.SoundManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.GamePhysics;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.AppViewManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IDrawableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.ILevelFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IResizableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.GameLoop;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IClickListener;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelFrameUtils;
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

public class LevelFrame extends VBox implements ILevelFrame, IResizableFrame, IDrawableFrame, IClickListener {
    private final GameLoop gameLoop;
    private final Canvas canvas;
    private final Region hudBar;

    private final Button speedButton = new Button("1x");
    private final Label coinsLabel = new Label("0/10");
    private final Label lifesLabel = new Label("3♥");
    private final Label timeLabel = new Label("180s");
    private final Button pauseButton = new Button("Pause");
    private final Button retryButton = new Button("Retry");
    private final Button menuButton = new Button("Menu");

    private boolean popupShown = false;
    private final boolean isEditor;

    public LevelFrame(Level level, boolean isEditor) {
        gameLoop = new GameLoop(this, level);
        this.isEditor = isEditor;
        this.canvas = new Canvas();
        this.gameLoop.getLevel().Play();

        setAlignment(Pos.CENTER);

        getChildren().add(canvas);

        // Create the HUD bar and add it
        hudBar = createHudBar();
        getChildren().add(hudBar); // HUD bar is added after canvas to place it below

        AppViewManager.get().setClickListener(this);
        gameLoop.setObjects(gameLoop.getLevel().getListOfObjects());
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
        // init and set canvas size
        int[] canvasSize = {(int) canvas.getWidth(), (int) canvas.getHeight()};

        //get the size of AppViewManagers canvas
        int[] appCanvasSize = {(int) AppViewManager.get().getScene().getWidth(),
                (int) AppViewManager.get().getScene().getHeight()};

        int[] tileClick = LevelFrameUtils.getTileClicked((int)event.getX(), (int)event.getY(), appCanvasSize,
                canvasSize, gameLoop.getLevel());
        if (tileClick[0] == -1) {
            return;
        }

        if (!GamePhysics.tileExists(tileClick)) {
            return;
        }

        gameLoop.getLevel().PlaceRotateRemoveArrow(tileClick, button);

        // update objects, to include the new arrows.
        gameLoop.setObjects(gameLoop.getLevel().getListOfObjects());
        gameLoop.getObjects().sort(Comparator.comparingInt(GameObject::getRenderPriority));
    }

    private void showPopup(String message, boolean isWin) {
        if (popupShown) {
            return;
        }

        SoundManager.stopMusic();
        if (isWin){
            SoundManager.playSound(SoundListEnum.FINISH);
        } else {
            SoundManager.playSound(SoundListEnum.GAME_OVER);
        }

        popupShown = true;
        Stage popupStage = new Stage();
        popupStage.setTitle("Game Result");

        // make the popup modal
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(this.getScene().getWindow());

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button backToMenuButton = new Button("Back to Menu");
        if (isEditor) {
            backToMenuButton.setText("Retry");
            backToMenuButton.setOnAction(e -> {
                popupStage.close();
                resetLevel();
            });
        } else {
            backToMenuButton.setOnAction(e -> {
                popupStage.close();
                gameLoop.unload();
                AppViewManager.get().switchTo(new LevelSelectionFrame(false));
            });
        }


        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        if (isWin) {
            Button nextLevelButton = new Button("Next Level");
            if (isEditor) {
                messageLabel.setText("Level Accepted!");
                nextLevelButton.setText("Edit");
                nextLevelButton.setOnAction(e -> {
                    String[] levelData = gameLoop.getLevel().getLevelData();
                    Level level = new Level(levelData[0], levelData[1].equals("true"), levelData[2].equals("true"));
                    gameLoop.unload();
                    if (!level.loadLevel()) {
                        System.out.println("Level not loaded successfully");
                        return;
                    }
                    popupStage.close();
                    AppViewManager.get().switchTo(new LevelEditorFrame(level));
                });
            } else {
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
            buttonBox.getChildren().addAll(backToMenuButton, nextLevelButton);
        } else {
            Button retryLevelButton = new Button("Retry");
            if (isEditor) {
                messageLabel.setText("Level Not Accepted!");
                retryLevelButton.setText("Edit");
                retryLevelButton.setOnAction(e -> {
                    String[] levelData = gameLoop.getLevel().getLevelData();
                    Level level = new Level(levelData[0], levelData[1].equals("true"), levelData[2].equals("true"));
                    gameLoop.unload();
                    if (!level.loadLevel()) {
                        System.out.println("Level not loaded successfully");
                        return;
                    }
                    popupStage.close();
                    AppViewManager.get().switchTo(new LevelEditorFrame(level));
                });
            } else {
                retryLevelButton.setOnAction(e -> {
                    popupStage.close();
                    resetLevel();
                });
            }

            buttonBox.getChildren().addAll(backToMenuButton, retryLevelButton);
        }

        VBox popupLayout = new VBox(20, messageLabel, buttonBox);
        popupLayout.setPadding(new Insets(20));
        popupLayout.setAlignment(Pos.CENTER);
        popupLayout.setStyle("-fx-background-color: #333; -fx-border-color: white; -fx-border-width: 2px;");

        Scene popupScene = new Scene(popupLayout, 300, 200);
        popupStage.setScene(popupScene);
        popupStage.setResizable(false);

        popupStage.setOnCloseRequest(e -> {
            e.consume();
        });

        popupStage.showAndWait();
    }

    public void checkGameStatus(){
        GameStatusEnum gameStatus = GameObject.getGameStatus();
        if (gameStatus == GameStatusEnum.WIN) {
            gameLoop.getLevel().Completed(); // mark the level as completed
            showPopup("You Win!", true);
        } else if (gameStatus == GameStatusEnum.LOSE || gameLoop.getLevel().getTimeLeft() <= 0) {
            showPopup("You Lose!", false);
        }
    }

    public void drawLevelObjects() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHEAT);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (GameObject object : gameLoop.getObjects()) {
            if (object.isVisible()) {
                drawObject(gc, object);
            }
        }

        // update the HUD
        int[] coinsInfo = gameLoop.getLevel().getCoinsLeftAndCoins();
        coinsLabel.setText(coinsInfo[1]-coinsInfo[0] + "/" + coinsInfo[1]);
        lifesLabel.setText(gameLoop.getLevel().getLifes() + "♥");
        timeLabel.setText(gameLoop.getLevel().getTimeLeft() + "s");
    }

    private void drawObject(GraphicsContext gc, GameObject object) {
        //double[] position = object.getPosition();
        double[] position = object.getScaledPositionSizePercentage(gameLoop.getLevel());
        // multiply by scene height and width to get the size in pixels
        position[0] = position[0] * canvas.getWidth() ;
        position[1] = position[1] * canvas.getHeight();

        Image texture = object.getTexture();
        //int[] textureSize = object.getTextureSize();

        double[] textureSizeRatio = object.getScaledTextureSizePercentage(gameLoop.getLevel());
        int[] textureSize = new int[2];
        // multiply by scene height and width to get the size in pixels
        textureSize[0] = (int) Math.ceil(canvas.getWidth() * textureSizeRatio[0]);
        textureSize[1] = (int) Math.ceil(canvas.getHeight() * textureSizeRatio[1]);

        double x = position[0];
        double y = position[1];

        double width = textureSize[0];
        double height = textureSize[1];

        gc.drawImage(texture, x, y, width, height);
    }

    private void adjustFontSize(double width) {
        double fontSize = width / 50;

        // loop through all children of the hudBar and set the font size
        for (javafx.scene.Node node : hudBar.getChildrenUnmodifiable()) {
            if (node instanceof Button) {
                Button button = (Button) node;
                String currentStyle = button.getStyle();
                button.setStyle(currentStyle + "-fx-font-size: " + fontSize + "px;");
            } else if (node instanceof Label) {
                Label label = (Label) node;
                String currentStyle = label.getStyle();
                label.setStyle(currentStyle + "-fx-font-size: " + fontSize + "px;");
            }
        }
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
                String[] levelData = gameLoop.getLevel().getLevelData();
                Level level = new Level(levelData[0], levelData[1].equals("true"), levelData[2].equals("true"));
                gameLoop.unload();
                if (!level.loadLevel()) {
                    System.out.println("Level not loaded successfully");
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

        speedButton.setOnAction(e -> {
            double newSpeed = gameLoop.getAndChangeSpeed()/2.0;
            speedButton.setText(String.valueOf(newSpeed).replace(".0", "") + "x");
            pauseButton.setText("Pause");
            gameLoop.resume();
        });

        coinsLabel.setStyle("-fx-text-fill: gold; -fx-font-weight: bold;");
        lifesLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        javafx.scene.Node[] nodes = {
                speedButton, coinsLabel, lifesLabel, timeLabel,
                pauseButton, retryButton, menuButton
        };

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

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
    
    void resetLevel() {
        popupShown = false;
        speedButton.setText("1x");
        pauseButton.setText("Pause");
        gameLoop.resetLevel();
        SoundManager.playSound(SoundListEnum.GAME_THEME);
    }
}
