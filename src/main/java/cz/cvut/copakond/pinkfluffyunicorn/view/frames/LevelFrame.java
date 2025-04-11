package cz.cvut.copakond.pinkfluffyunicorn.view.frames;

import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.AppViewManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.IDrawableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.IResizableFrame;
import cz.cvut.copakond.pinkfluffyunicorn.model.data.Level;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.IClickListener;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.LevelFrameUtils;
import javafx.geometry.Pos;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;

import java.util.List;
import java.util.Comparator;

public class LevelFrame extends VBox implements IResizableFrame, IDrawableFrame, IClickListener {
    private final Level level;
    private final Canvas canvas;
    private boolean isRunning = false;
    private List<GameObject> objects;

    public LevelFrame(Level level, boolean isEditor) {
        this.level = level;
        this.canvas = new Canvas();
        this.level.Play();

        setAlignment(Pos.CENTER);
        getChildren().add(canvas);

        AppViewManager.get().setClickListener(this);

        this.objects = level.getListOfObjects();

        // sort objects by render priority
        objects.sort(Comparator.comparingInt(GameObject::getRenderPriority));

        isRunning = true;
        run();
    }

    @Override
    public void handleClick(MouseEvent event) {
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

        System.out.println("! Tile clicked: " + tileClick[0] + ", " + tileClick[1]);

        level.PlaceRotateRemoveArrow(tileClick, button);

        // update objects, to include the new arrows.
        this.objects = level.getListOfObjects();
        objects.sort(Comparator.comparingInt(GameObject::getRenderPriority));
    }


    void run() {
        Thread gameLoopThread = new Thread(() -> {
            final int fps = GameObject.getFPS();
            final long frameDuration = 1000 / fps;

            while (isRunning) {
                //System.out.println("--- New frame ---");
                long startTime = System.currentTimeMillis();

                // javafx thread
                javafx.application.Platform.runLater(() -> drawLevelObjects());

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


    private void drawLevelObjects() {
        GraphicsContext gc = canvas.getGraphicsContext2D();

        gc.setFill(Color.WHEAT);
        gc.fillRect(0, 0, canvas.getWidth(), canvas.getHeight());

        level.tick();
        for (GameObject object : objects) {
            if (object.isVisible()) {
                drawObject(gc, object);
            }
        }
    }

    private void drawObject(GraphicsContext gc, GameObject object) {
        //double[] position = object.getPosition();
        double[] position = object.getScaledPositionSizePercentage(level);
        // multiply by scene height and width to get the size in pixels
        position[0] = position[0] * canvas.getWidth() ;
        position[1] = position[1] * canvas.getHeight() * (1 - 11.111 / 100);

        Image texture = object.getTexture();
        //int[] textureSize = object.getTextureSize();

        double[] textureSizeRatio = object.getScaledTextureSizePercentage(level);
        int[] textureSize = new int[2];
        // multiply by scene height and width to get the size in pixels
        textureSize[0] = (int) (canvas.getWidth() * textureSizeRatio[0]);
        textureSize[1] = (int) (canvas.getHeight() * (1-  11.111 / 100) * textureSizeRatio[1]);

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
        isRunning = false;
    }
}
