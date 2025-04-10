package cz.cvut.copakond.pinkfluffyunicorn.view.frames;

import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.DrawableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.ResizableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.model.data.Level;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import javafx.animation.AnimationTimer;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Comparator;

public class LevelFrame extends VBox implements ResizableFrame, DrawableFrame {
    private final Level level;
    private final Canvas canvas;
    private final AnimationTimer timer;

    public LevelFrame(Integer levelNumber, boolean isEditor) {
        this.level = new Level(levelNumber.toString(), isEditor);
        this.level.loadLevel();
        this.level.Play();
        this.canvas = new Canvas();
        setAlignment(Pos.CENTER);
        getChildren().add(canvas);

        // 60 fps
        timer = new AnimationTimer() {
            @Override
            public void handle(long now) {
                drawLevelObjects();
            }
        };
        timer.start();

    }

    private void drawLevelObjects() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        level.tick();
        List<GameObject> objects = level.getListOfObjects();

        // sort objects by render priority
        objects.sort(Comparator.comparingInt(GameObject::getRenderPriority).reversed());

        for (GameObject object : objects) {
            drawObject(gc, object);
        }
    }

    private void drawObject(GraphicsContext gc, GameObject object) {
        //double[] position = object.getPosition();
        double[] position = object.getScaledPositionSizePercentage(level);
        // multiply by scene height and width to get the size in pixels
        position[0] = position[0] * canvas.getWidth();
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

    @Override
    public void onResizeCanvas(double width, double height) {
        canvas.setWidth(width);
        canvas.setHeight(height);
    }

    @Override
    public void draw(GraphicsContext gc) {
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

    public void stopDrawing() {
        if (timer != null) {
            timer.stop();
        }
    }
}
