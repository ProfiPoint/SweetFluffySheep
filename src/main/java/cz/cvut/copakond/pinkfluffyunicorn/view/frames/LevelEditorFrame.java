package cz.cvut.copakond.pinkfluffyunicorn.view.frames;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.LevelEditorObjectsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelEditorUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.PathFinder;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.AppViewManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.GameLoop;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IClickListener;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelFrameUtils;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IDrawableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.ILevelFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.interfaces.IResizableFrame;
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

import java.io.File;
import java.util.*;

public class LevelEditorFrame extends VBox implements ILevelFrame, IResizableFrame, IDrawableFrame, IClickListener {
    private final GameLoop gameLoop;
    private final Canvas canvas;
    private final GridPane hudBar;
    private LevelEditorObjectsEnum selectedObject = LevelEditorObjectsEnum.EMPTY;

    private final Button playButton =     new Button("   Play   ");
    private final Button settingsButton = new Button("Settings");
    private final Button menuButton =     new Button("  Menu  ");

    private static String texturePath;

    public static void setTexturePath(String path) {
        texturePath = path;
    }

    public LevelEditorFrame(Level level) {
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
    }

    private boolean saveLevel(String headerText) {
        Level level = gameLoop.getLevel();
        PathFinder pathFinder = new PathFinder(level);
        boolean valid = pathFinder.canLevelBeCompleted();
        if (valid) {
            level.saveLevel();
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Invalid Level");
            alert.setHeaderText(headerText);
            alert.setContentText("Reason: " + pathFinder.reasonForFailure());
            alert.showAndWait();
            return false;
        }
    }

    private GridPane createEditorHudBar() {
        GridPane bar = new GridPane();
        bar.setStyle("-fx-background-color: #222;");
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(1));
        bar.setHgap(5);

        String[] imageNames = {
                "tile", "removeTile", "cloud", "coin", "fire", "rainbow", "start", "goal", "rotate", "destroy"
        };

        int col = 0;
        List<Button> imgButtons = new ArrayList<>();
        for (String name : imageNames) {
            String fullPath = texturePath + "/editor/" + name + ".png";

            Image image;
            try {
                image = new Image(new File(fullPath).toURI().toURL().toExternalForm());
                ImageView imageView = new ImageView(image);
                imageView.setPreserveRatio(true);
            } catch (java.net.MalformedURLException e) {
                e.printStackTrace();
                continue;
            }
            ImageView imageView = new ImageView(image);
            imageView.setPreserveRatio(true);

            Button imgButton = new Button();
            imgButton.setGraphic(imageView);
            imgButton.setStyle("-fx-background-color: transparent;");

            // image sizing will be handled in onResizeCanvas
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

        playButton.setOnAction(event -> {
            if (!saveLevel("Level cannot be completed")) {
                return;
            }

            String[] levelData = gameLoop.getLevel().getLevelData();
            Level newLevel = new Level(levelData[0], levelData[1].equals("true"), levelData[2].equals("true"));
            gameLoop.unload();
            if (!newLevel.loadLevel()) {
                System.out.println("Level not loaded successfully");
                return;
            }
            AppViewManager.get().switchTo(new LevelFrame(newLevel, true));
        });

        settingsButton.setOnAction(event -> {
            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Level Settings");

            ButtonType confirmButtonType = new ButtonType("Confirm");
            ButtonType cancelButtonType = new ButtonType("Cancel");
            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, cancelButtonType);

            Map<String, Integer> levelInfo = gameLoop.getLevel().getLevelInfo();
            int[] mapSize = gameLoop.getLevel().getMapSize();

            // set the limits for the values
            Spinner<Integer> timeLimitSpinner = new Spinner<>(10, Integer.MAX_VALUE, levelInfo.get("timeLimit"));
            Spinner<Integer> unicornsSpinner = new Spinner<>(1, Integer.MAX_VALUE, levelInfo.get("unicorns"));
            Spinner<Integer> goalUnicornsSpinner = new Spinner<>(1, unicornsSpinner.getValue(), levelInfo.get("goalUnicorns"));
            Spinner<Integer> maxArrowsSpinner = new Spinner<>(1, Integer.MAX_VALUE, levelInfo.get("maxArrows"));
            Spinner<Integer> mapSizeXSpinner = new Spinner<>(1, Integer.MAX_VALUE, mapSize[0]);
            Spinner<Integer> mapSizeYSpinner = new Spinner<>(1, Integer.MAX_VALUE, mapSize[1]);
            Spinner<Integer> itemDurationSpinner = new Spinner<>(1, Integer.MAX_VALUE, levelInfo.get("deafultItemDuration"));

            // user can edit the values with keyboard
            timeLimitSpinner.setEditable(true);
            unicornsSpinner.setEditable(true);
            maxArrowsSpinner.setEditable(true);
            goalUnicornsSpinner.setEditable(true);
            mapSizeXSpinner.setEditable(true);
            mapSizeYSpinner.setEditable(true);
            itemDurationSpinner.setEditable(true);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));

            grid.add(new Label("Time Limit (s):"), 0, 0);
            grid.add(timeLimitSpinner, 1, 0);
            grid.add(new Label("Unicorns:"), 0, 1);
            grid.add(unicornsSpinner, 1, 1);
            grid.add(new Label("Goal Unicorns:"), 2, 1);
            grid.add(goalUnicornsSpinner, 3, 1);
            grid.add(new Label("Max Arrows:"), 0, 2);
            grid.add(maxArrowsSpinner, 1, 2);
            grid.add(new Label("Map Size (Width):"), 0, 3);
            grid.add(mapSizeXSpinner, 1, 3);
            grid.add(new Label("Map Size (Height):"), 2, 3);
            grid.add(mapSizeYSpinner, 3, 3);
            grid.add(new Label("Item Duration (s):"), 0, 4);
            grid.add(itemDurationSpinner, 1, 4);

            dialog.getDialogPane().setContent(grid);

            // update the values, when the user changes the number of unicorns
            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == confirmButtonType) {
                // goal unicorns must be less than or equal to unicorns
                if (goalUnicornsSpinner.getValue() > unicornsSpinner.getValue()) {
                    goalUnicornsSpinner.getValueFactory().setValue(unicornsSpinner.getValue());
                }

                levelInfo.put("timeLimit", timeLimitSpinner.getValue());
                levelInfo.put("unicorns", unicornsSpinner.getValue());
                levelInfo.put("goalUnicorns", goalUnicornsSpinner.getValue());
                levelInfo.put("maxArrows", maxArrowsSpinner.getValue());
                levelInfo.put("deafultItemDuration", itemDurationSpinner.getValue());
                System.out.println("Level settings updated: " + levelInfo);
                // check if the map size is updated
                if (mapSize[0] != mapSizeXSpinner.getValue() || mapSize[1] != mapSizeYSpinner.getValue()) {
                    mapSize[0] = mapSizeXSpinner.getValue();
                    mapSize[1] = mapSizeYSpinner.getValue();
                    System.out.println("Map size updated: " + Arrays.toString(mapSize));

                    // now reload the level
                    if (!saveLevel("Level can not be resized")) {
                        return;
                    }

                    String[] levelData = gameLoop.getLevel().getLevelData();
                    Level newLevel = new Level(levelData[0], levelData[1].equals("true"), levelData[2].equals("true"));
                    gameLoop.unload();
                    if (!newLevel.loadLevel()) {
                        System.out.println("Level not loaded successfully");
                        return;
                    }
                    AppViewManager.get().switchTo(new LevelEditorFrame(newLevel));
                }
            }
        });

        menuButton.setOnAction(e -> {
            gameLoop.unload();
            AppViewManager.get().switchTo(new LevelSelectionFrame(true));
        });

        bar.add(playButton, col++, 0);
        bar.add(settingsButton, col++, 0);
        bar.add(menuButton, col++, 0);

        for (int i = 0; i < col; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / col);
            bar.getColumnConstraints().add(cc);
        }

        return bar;
    }

    private void adjustFontSize(double width) {
        double fontSize = width / 75;

        for (Node node : hudBar.getChildrenUnmodifiable()) {
            if (node instanceof Button button) {
                String currentStyle = button.getStyle();
                button.setStyle(currentStyle + "-fx-font-size: " + fontSize + "px;");
            } else if (node instanceof Label label) {
                String currentStyle = label.getStyle();
                label.setStyle(currentStyle + "-fx-font-size: " + fontSize + "px;");
            }

            if (node instanceof Button button && button.getGraphic() instanceof ImageView imageView) {
                double size = fontSize * 2.5;
                imageView.setFitWidth(size);
                imageView.setFitHeight(size);
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
        gameLoop.renderScene();
    }

    // Level Editor can never be won or lost, only the level itself.
    public void checkGameStatus(){}

    public void drawLevelObjects() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHEAT);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        for (GameObject object : gameLoop.getObjects()) {
            if (object.isVisible()) {
                drawObject(gc, object);
            }
        }
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
        textureSize[0] = (int) (canvas.getWidth() * textureSizeRatio[0]);
        textureSize[1] = (int) (canvas.getHeight() * textureSizeRatio[1]);

        double x = position[0];
        double y = position[1];

        double width = textureSize[0];
        double height = textureSize[1];

        gc.drawImage(texture, x, y, width, height);
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
}
