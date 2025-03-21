package cz.cvut.copakond.pinkfluffyunicorn;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

import java.io.IOException;

/* JAVA FX TIPS AND TRICKS
 create one big canvas and draw everything on it using bitmap
 refresh it 60 fps
 */

public class HelloApplication extends Application {

    private final Image bgrImg = new Image("background.png");
    private final Image birdImage = new Image("bird1.png");
    private final Canvas canvas = new Canvas(bgrImg.getWidth(), bgrImg.getHeight());

    @Override
    public void start(Stage stage) throws IOException {
        GraphicsContext gc = canvas.getGraphicsContext2D();
        gc.drawImage(bgrImg, 0, 0);


        StackPane root = new StackPane(canvas);
        Scene scene = new Scene(root,bgrImg.getWidth(), bgrImg.getHeight());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}