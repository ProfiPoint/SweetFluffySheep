package cz.cvut.copakond.pinkfluffyunicorn.view.utils;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelStatusUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;
import cz.cvut.copakond.pinkfluffyunicorn.view.frames.LevelFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.scenebuilder.AppViewManager;

import java.util.Comparator;
import java.util.List;


public class GameLoop {
    LevelFrame levelFrame;
    private boolean isRunning = false;
    private Level level;
    private List<GameObject> objects;
    private long currentFrame = 0;
    private final int[] speedOptions = {1, 2, 4, 8}; // default speed is 2x, but is presented to user as 1x
    private int currentSpeedIndex = 1; // index of speedOptions

    public GameLoop(LevelFrame levelFrame, Level level) {
        this.levelFrame = levelFrame;
        this.level = level;
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
                if (currentFrame % speedOptions[currentSpeedIndex] != 0) {
                    level.tick(false);
                    continue;
                } else {
                    level.tick(true);
                }

                // javafx thread
                javafx.application.Platform.runLater(levelFrame::drawLevelObjects);

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

    public Level getLevel() {return level;}

    public int getAndChangeSpeed() {
        currentSpeedIndex = (currentSpeedIndex + 1) % speedOptions.length;
        return speedOptions[currentSpeedIndex];
    }

    public void setNewLevel() {
        level = LevelStatusUtils.getNextLevel(level);
        resetLevel();
    }

    public List<GameObject> getObjects() {return objects;}

    public void setObjects(List<GameObject> objects) {
        this.objects = objects;
        objects.sort(Comparator.comparingInt(GameObject::getRenderPriority));
    }

    public boolean isRunning() {
        return isRunning;
    }

    public int elapsedSeconds() {
        return (int)currentFrame / GameObject.getFPS();
    }

    public void pause() {
        isRunning = false;
    }

    public void resume() {
        if (isRunning) {
            return;
        }
        objects = level.getListOfObjects();
        objects.sort(Comparator.comparingInt(GameObject::getRenderPriority));
        isRunning = true;
        run();
    }

    public void unload() {
        pause();
        AppViewManager.get().setClickListener(null);
        level.Unload();
    }

    public void resetLevel() {
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
        currentSpeedIndex = 1;
        level.Play();
        resume();
    }
}