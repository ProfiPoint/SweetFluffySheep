package cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public class AppViewManager {

    private static AppViewManager instance;

    private final Canvas canvas = new Canvas();
    private final StackPane overlay = new StackPane(); // for frames/views
    private final StackPane root = new StackPane();
    private final Scene scene;
    private final Stage stage;

    private Pane currentFrame; // currently visible frame

    public static void init(Stage stage) {
        if (instance == null) {
            instance = new AppViewManager(stage);
        }
    }

    public static AppViewManager get() {
        return instance;
    }

    private AppViewManager(Stage stage) {
        this.stage = stage;

        StackPane canvasHolder = new StackPane(canvas);
        canvasHolder.setAlignment(Pos.CENTER);
        canvasHolder.setStyle("-fx-background-color: black;");

        overlay.setPickOnBounds(false);
        overlay.setAlignment(Pos.CENTER);

        root.getChildren().addAll(canvasHolder, overlay);
        root.setStyle("-fx-background-color: black;");

        scene = new Scene(root, stage.getWidth(), stage.getHeight(), Color.BLACK);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        scene.widthProperty().addListener(onResize());
        scene.heightProperty().addListener(onResize());

        stage.setScene(scene);
        stage.show();

        updateCanvasSize();
    }

    private ChangeListener<Number> onResize() {
        return (obs, oldVal, newVal) -> updateCanvasSize();
    }

    private void updateCanvasSize() {
        double maxW = scene.getWidth();
        double maxH = scene.getHeight();
        double ratio = 16.0 / 9.0;

        double w = maxW;
        double h = maxW / ratio;

        if (h > maxH) {
            h = maxH;
            w = h * ratio;
        }

        canvas.setWidth(w);
        canvas.setHeight(h);

        overlay.setMinSize(w, h);
        overlay.setPrefSize(w, h);
        overlay.setMaxSize(w, h);

        if (currentFrame instanceof ResizableFrame frame) {
            frame.onResizeCanvas(w, h);
        }

        draw();
    }

    public void update() {
        this.updateCanvasSize();
    }

    private void draw() {
        var gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        if (currentFrame instanceof DrawableFrame frame) {
            frame.draw(gc);
        }
    }

    public void switchTo(Pane newFrame) {
        overlay.getChildren().clear();
        currentFrame = newFrame;
        overlay.getChildren().add(newFrame);
        updateCanvasSize();
    }

    public Scene getScene() {
        return scene;
    }

    public Stage getStage() {
        return stage;
    }
}
