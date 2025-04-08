package cz.cvut.copakond.pinkfluffyunicorn.view.scenes;

import javafx.beans.value.ChangeListener;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

public abstract class ResponsiveScene {

    protected final Canvas canvas = new Canvas();
    protected final StackPane overlay = new StackPane(); // for UI controls
    protected final Scene scene;
    protected final Stage stage;

    public ResponsiveScene(Stage stage) {
        this.stage = stage;

        StackPane canvasHolder = new StackPane(canvas);
        canvasHolder.setAlignment(Pos.CENTER);
        canvasHolder.setStyle("-fx-background-color: black;");

        overlay.setPickOnBounds(false);
        StackPane root = new StackPane(canvasHolder, overlay);
        root.setStyle("-fx-background-color: black;");

        double width = stage.getWidth();
        double height = stage.getHeight();

        scene = new Scene(root, width, height, Color.BLACK);

        scene.setOnKeyPressed(e -> {
            if (e.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        scene.widthProperty().addListener(onResize());
        scene.heightProperty().addListener(onResize());
    }


    public void show() {
        updateCanvasSize(); // prevent flicker
        stage.setScene(scene);
        stage.show();
        updateCanvasSize();
    }

    private ChangeListener<Number> onResize() {
        return (obs, oldVal, newVal) -> updateCanvasSize();
    }

    protected void updateCanvasSize() {
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

        onResizeCanvas(w, h);
        draw();
    }

    protected abstract void onResizeCanvas(double width, double height);

    protected abstract void draw();

    public Scene getScene() {
        return scene;
    }
}
