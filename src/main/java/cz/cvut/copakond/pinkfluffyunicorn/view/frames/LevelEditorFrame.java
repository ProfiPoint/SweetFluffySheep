package cz.cvut.copakond.pinkfluffyunicorn.view.frames;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.LevelEditorObjectsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelEditorUtils;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.AppViewManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.Level;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.GameLoop;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.IClickListener;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelFrameUtils;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.IDrawableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.ILevelFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.IResizableFrame;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.Node;
import javafx.scene.control.Label;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class LevelEditorFrame extends VBox implements ILevelFrame, IResizableFrame, IDrawableFrame, IClickListener {
    private final GameLoop gameLoop;
    private final Canvas canvas;
    private final GridPane hudBar;
    private LevelEditorObjectsEnum selectedObject = LevelEditorObjectsEnum.EMPTY;

    private final Button playButton = new Button("Play");
    private final Button menuButton = new Button("Menu");

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

    private GridPane createEditorHudBar() {
        GridPane bar = new GridPane();
        bar.setStyle("-fx-background-color: #222;");
        bar.setAlignment(Pos.CENTER);
        bar.setPadding(new Insets(5));
        bar.setHgap(5);

        String[] imageNames = {
                "path", "removePath", "cloud", "coin", "fire", "rainbow", "start", "goal", "destroy"
        };

        int col = 0;
        List<Button> imgButtons = new ArrayList<>();
        for (String name : imageNames) {
            Image image = new Image(getClass().getResourceAsStream("/textures/editor/" + name + ".png"));
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

        });

        menuButton.setOnAction(e -> {
            gameLoop.unload();
            AppViewManager.get().switchTo(new MenuFrame());
        });

        bar.add(playButton, col++, 0);
        bar.add(menuButton, col++, 0);

        for (int i = 0; i < col; i++) {
            ColumnConstraints cc = new ColumnConstraints();
            cc.setPercentWidth(100.0 / col);
            bar.getColumnConstraints().add(cc);
        }

        return bar;
    }

    private void adjustFontSize(double width) {
        double fontSize = width / 50;

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
        if (!gameLoop.isRunning() || gameLoop.elapsedSeconds() < 1) return;

        int button = event.getButton().name().equals("PRIMARY") ? 1 :
                event.getButton().name().equals("SECONDARY") ? 2 : 0;

        int[] canvasSize = {(int) canvas.getWidth(), (int) canvas.getHeight()};
        int[] appCanvasSize = {(int) AppViewManager.get().getScene().getWidth(),
                (int) AppViewManager.get().getScene().getHeight()};

        int[] tileClick = LevelFrameUtils.getTileClicked(
                (int) event.getX(), (int) event.getY(),
                appCanvasSize, canvasSize,
                gameLoop.getLevel()
        );

        if (tileClick[0] == -1) return;

        gameLoop.getLevel().PlaceRotateRemoveArrow(tileClick, button);
        gameLoop.setObjects(gameLoop.getLevel().getListOfObjects());
        gameLoop.getObjects().sort(Comparator.comparingInt(GameObject::getRenderPriority));
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
