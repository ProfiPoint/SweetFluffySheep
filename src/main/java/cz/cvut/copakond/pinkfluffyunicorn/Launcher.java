package cz.cvut.copakond.pinkfluffyunicorn;

import cz.cvut.copakond.pinkfluffyunicorn.model.data.Level;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

import java.util.HashMap;
import java.util.Map;

public class Launcher extends Application {

    private final Map<String, Image> textureMap = new HashMap<>();
    private final int tileCols = 24;
    private final int tileRows = 12;

    private final Canvas canvas = new Canvas();

    @Override
    public void start(Stage stage) {
        // Load textures
        textureMap.put("tile1", new Image("tile1.png"));
        textureMap.put("tile2", new Image("tile2.png"));

        // Create scene
        StackPane root = new StackPane(canvas);
        root.setStyle("-fx-background-color: black;");
        Scene scene = new Scene(root, 1280, 720, Color.BLACK);

        // Resize logic
        scene.widthProperty().addListener((obs, oldW, newW) -> updateCanvasSize(scene));
        scene.heightProperty().addListener((obs, oldH, newH) -> updateCanvasSize(scene));

        // Fullscreen
        scene.setOnKeyPressed(event -> {
            if (event.getCode() == KeyCode.F11) {
                stage.setFullScreen(!stage.isFullScreen());
            }
        });

        stage.setTitle("Pink Fluffy Unicorn");
        stage.setScene(scene);
        stage.show();

        updateCanvasSize(scene); // Initial draw
    }

    private void updateCanvasSize(Scene scene) {
        double maxW = scene.getWidth();
        double maxH = scene.getHeight();
        double targetRatio = 16.0 / 9.0;

        double newW = maxW;
        double newH = maxW / targetRatio;

        if (newH > maxH) {
            newH = maxH;
            newW = newH * targetRatio;
        }

        canvas.setWidth(newW);
        canvas.setHeight(newH);

        draw();
    }

    private void draw() {
        double canvasWidth = canvas.getWidth();
        double canvasHeight = canvas.getHeight();
        double topBarHeight = canvasHeight * 11.111 / 100; // 11.111% of height

        double tileAreaHeight = canvasHeight - topBarHeight;
        double tileHeight = tileAreaHeight / tileRows;
        double tileWidth = canvasWidth / tileCols;

        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.setFill(Color.BLACK);
        gc.clearRect(0, 0, canvasWidth, canvasHeight);

        gc.setFill(Color.ORANGE);
        gc.fillRect(0, 0, canvasWidth, topBarHeight);

        // Draw tiles
        for (int row = 0; row < tileRows; row++) {
            for (int col = 0; col < tileCols; col++) {
                String textureKey = (row + col) % 2 == 0 ? "tile1" : "tile2";
                Image texture = textureMap.get(textureKey);
                gc.drawImage(
                        texture,
                        0, 0, texture.getWidth(), texture.getHeight(),
                        col * tileWidth,
                        topBarHeight + row * tileHeight,
                        tileWidth,
                        tileHeight
                );
            }
        }
    }

    public static void main(String[] args) {
        //launch();
        Level level = new Level("_TEMPLATE", false);
        if (!level.loadLevel()) {
            System.err.println("Error loading level data - main launcher");
            return;
        }
        System.out.println("Level loaded successfully from MAIN LAUNCHER:D");
        if (!level.saveLevel("test_level")) {
            System.out.println("Error saving level data - main launcher");
        }
        System.out.println("Level saved successfully from MAIN LAUNCHER:D");

    }
}
