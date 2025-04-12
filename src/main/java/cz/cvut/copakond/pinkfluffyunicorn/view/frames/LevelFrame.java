package cz.cvut.copakond.pinkfluffyunicorn.view.frames;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.GameStatusEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelStatusUtils;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.AppViewManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.IDrawableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.IResizableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.IClickListener;
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

import java.util.List;
import java.util.Comparator;

public class LevelFrame extends VBox implements IResizableFrame, IDrawableFrame, IClickListener {
    private Level level;
    private final Canvas canvas;
    private boolean isRunning = false;
    private List<GameObject> objects;
    private final Region hudBar;

    private long currentFrame = 0;

    int[] speedOptions = {1, 2, 4, 8}; // default speed is 2x, but is presented to user as 1x
    int[] currentSpeedIndex = {1}; // array to pass it by reference in button action

    private final Button speedButton = new Button("1x");
    private final Label coinsLabel = new Label("0/10");
    private final Label lifesLabel = new Label("3♥");
    private final Label timeLabel = new Label("180s");
    private final Button pauseButton = new Button("Pause");
    private final Button retryButton = new Button("Retry");
    private final Button menuButton = new Button("Menu");

    private boolean popupShown = false;

    public LevelFrame(Level level, boolean isEditor) {
        this.level = level;
        this.canvas = new Canvas();
        this.level.Play();

        setAlignment(Pos.CENTER);

        getChildren().add(canvas);

        // Create the HUD bar and add it
        hudBar = createHudBar();
        getChildren().add(hudBar); // HUD bar is added after canvas to place it below

        AppViewManager.get().setClickListener(this);
        this.objects = level.getListOfObjects();

        // sort objects by render priority
        objects.sort(Comparator.comparingInt(GameObject::getRenderPriority));

        isRunning = true;
        run();
    }

    @Override
    public void handleClick(MouseEvent event) {
        // ignore if not running or if the click is within the first second of run time
        if (!isRunning || currentFrame / GameObject.getFPS() < 1) {
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
                canvasSize, level);
        if (tileClick[0] == -1) {
            return;
        }

        level.PlaceRotateRemoveArrow(tileClick, button);

        // update objects, to include the new arrows.
        this.objects = level.getListOfObjects();
        objects.sort(Comparator.comparingInt(GameObject::getRenderPriority));
    }


    void run() {
        Thread gameLoopThread = new Thread(() -> {
            final int fps = GameObject.getFPS();
            final long frameDuration = 1000 / fps;
            currentFrame = 0;

            while (isRunning) {
                //System.out.println("--- New frame ---");
                long startTime = System.currentTimeMillis();
                currentFrame++;

                // if the game speed is not 1 (on screen 0.5), skip the frame, for faster speeds
                if (currentFrame % speedOptions[currentSpeedIndex[0]] != 0) {
                    level.tick(false);
                    continue;
                } else {
                    level.tick(true);
                }

                // javafx thread
                javafx.application.Platform.runLater(this::drawLevelObjects);

                long endTime = System.currentTimeMillis();
                long sleepTime = frameDuration - (endTime - startTime);

                if (sleepTime > 0) {
                    try {
                        Thread.sleep(sleepTime);
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                        break;
                    }
                }
            }
        });

        gameLoopThread.setDaemon(true);
        gameLoopThread.start();
    }

    private void showPopup(String message, boolean isWin) {
        if (popupShown) {
            return;
        }
        popupShown = true;
        Stage popupStage = new Stage();
        popupStage.setTitle("Game Result");

        // Make the popup modal (blocks input to other windows)
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.initOwner(this.getScene().getWindow());

        Label messageLabel = new Label(message);
        messageLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: white;");

        Button backToMenuButton = new Button("Back to Menu");
        backToMenuButton.setOnAction(e -> {
            popupStage.close();
            unload();
            AppViewManager.get().switchTo(new MenuFrame());
        });

        VBox buttonBox = new VBox(10);
        buttonBox.setAlignment(Pos.CENTER);

        if (isWin) {
            level.Completed(); // mark the level as completed

            Button nextLevelButton = new Button("Next Level");
            nextLevelButton.setOnAction(e -> {
                popupStage.close();
                level = LevelStatusUtils.getNextLevel(level);
                resetLevel();
            });
            buttonBox.getChildren().addAll(backToMenuButton, nextLevelButton);
        } else {
            Button retryLevelButton = new Button("Retry");
            retryLevelButton.setOnAction(e -> {
                popupStage.close();
                resetLevel();
            });
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
            // Ensure game stays paused or handled if user clicks X
            e.consume(); // Prevent closing if you want full control
        });

        popupStage.showAndWait(); // BLOCKS everything until popup closes
    }


    private void drawLevelObjects() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHEAT);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (GameObject object : objects) {
            if (object.isVisible()) {
                drawObject(gc, object);
            }
        }

        // update the HUD
        int[] coinsInfo = level.getCoinsLeftAndCoins();
        coinsLabel.setText(coinsInfo[1]-coinsInfo[0] + "/" + coinsInfo[1]);
        lifesLabel.setText(level.getLifes() + "♥");
        timeLabel.setText(level.getTimeLeft() + "s");

        GameStatusEnum gameStatus = GameObject.getGameStatus();
        if (gameStatus == GameStatusEnum.WIN) {
            showPopup("You Win!", true);
        } else if (gameStatus == GameStatusEnum.LOSE || level.getTimeLeft() <= 0) {
            showPopup("You Lose!", false);
        }

    }

    private void drawObject(GraphicsContext gc, GameObject object) {
        //double[] position = object.getPosition();
        double[] position = object.getScaledPositionSizePercentage(level);
        // multiply by scene height and width to get the size in pixels
        position[0] = position[0] * canvas.getWidth() ;
        position[1] = position[1] * canvas.getHeight();

        Image texture = object.getTexture();
        //int[] textureSize = object.getTextureSize();

        double[] textureSizeRatio = object.getScaledTextureSizePercentage(level);
        int[] textureSize = new int[2];
        // multiply by scene height and width to get the size in pixels
        textureSize[0] = (int) (canvas.getWidth() * textureSizeRatio[0]);
        textureSize[1] = (int) (canvas.getHeight() * textureSizeRatio[1]);

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

        // Create UI components

        pauseButton.setOnAction(event -> {
            if (isRunning) {
                pauseButton.setText("Resume");
                pause();
            } else {
                pauseButton.setText("Pause");
                resume();
            }
        });

        menuButton.setOnAction(e -> {
            unload();
            AppViewManager.get().switchTo(new MenuFrame());
        });

        retryButton.setOnAction(e -> {
            resetLevel();
        });

        speedButton.setOnAction(e -> {
            currentSpeedIndex[0] = (currentSpeedIndex[0] + 1) % speedOptions.length;
            double newSpeed = speedOptions[currentSpeedIndex[0]]/2.0;
            speedButton.setText(String.valueOf(newSpeed).replace(".0", "") + "x");
        });


        // Styling for labels
        coinsLabel.setStyle("-fx-text-fill: gold; -fx-font-weight: bold;");
        lifesLabel.setStyle("-fx-text-fill: red; -fx-font-weight: bold;");
        timeLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold;");

        // Put all elements in an array to loop through
        javafx.scene.Node[] nodes = {
                speedButton, coinsLabel, lifesLabel, timeLabel,
                pauseButton, retryButton, menuButton
        };

        for (javafx.scene.Node node : nodes) {
            if (node instanceof Region) {
                ((Region) node).setMaxWidth(Double.MAX_VALUE);
                HBox.setHgrow(node, Priority.ALWAYS); // Allow even horizontal growth
            }
        }

        // Add all to the bar (no spacer needed)
        bar.getChildren().addAll(nodes);

        // Let the bar expand fully
        bar.setMaxWidth(Double.MAX_VALUE);
        bar.setPrefWidth(Double.MAX_VALUE);

        return bar;
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    private void pause() {
        isRunning = false;
    }

    private void resume() {
        objects = level.getListOfObjects();
        objects.sort(Comparator.comparingInt(GameObject::getRenderPriority));
        isRunning = true;
        run();
    }

    private void unload() {
        pause();
        AppViewManager.get().setClickListener(null);
        level.Unload();
    }

    private void resetLevel() {
        String[] levelData = level.getLevelData();
        boolean isEditor = levelData[1].equals("true");
        boolean isStory = levelData[2].equals("true");
        isRunning = false;
        level.Unload();
        level = new Level(levelData[0], isEditor, isStory);
        if (!level.loadLevel()) {
            System.out.println("Level not loaded successfully");
            return;
        }
        level.Play();
        resume();
    }
}
