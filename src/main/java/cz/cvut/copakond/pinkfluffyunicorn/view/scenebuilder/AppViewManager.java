package cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder;

import cz.cvut.copakond.pinkfluffyunicorn.view.utils.IClickListener;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.IDrawableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.IResizableFrame;
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

    private IClickListener clickListener;
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

        scene.setOnMouseClicked(event -> {
            if (clickListener != null) {
                clickListener.handleClick(event);
            }
        });

        stage.setScene(scene);
        stage.show();

        updateCanvasSize();
    }

    protected int[] getOverlaySize() {
        return new int[]{(int) overlay.getWidth(), (int) overlay.getHeight()};
    }

    private ChangeListener<Number> onResize() {
        return (obs, oldVal, newVal) -> updateCanvasSize();
    }

    protected int[] getSceneSize() {
        return new int[]{(int) scene.getWidth(), (int) scene.getHeight()};
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

        if (currentFrame instanceof IResizableFrame frame) {
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

        if (currentFrame instanceof IDrawableFrame frame) {
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

    public void setClickListener(IClickListener listener) {
        this.clickListener = listener;
    }

}
