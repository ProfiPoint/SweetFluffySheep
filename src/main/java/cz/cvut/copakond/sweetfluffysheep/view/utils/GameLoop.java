package cz.cvut.copakond.sweetfluffysheep.view.utils;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.utils.levels.LevelStatusUtils;
import cz.cvut.copakond.sweetfluffysheep.model.world.Level;
import cz.cvut.copakond.sweetfluffysheep.view.interfaces.ILevelFrame;
import javafx.application.Platform;

import java.util.Comparator;
import java.util.List;
import java.util.concurrent.locks.LockSupport;
import java.util.logging.Logger;

public class GameLoop {
    private static final Logger logger = Logger.getLogger(GameLoop.class.getName());

    private final ILevelFrame levelFrame;
    private final int[] speedOptions = {1, 2, 4, 8}; // internally used multiplier (2x is default)

    private boolean isRunning = false;

    private int currentSpeedIndex = 1;
    private long currentFrame = 0;

    private Level level;
    private List<GameObject> objects;

    private Thread gameLoopThread;

    public GameLoop(ILevelFrame levelFrame, Level level) {
        this.levelFrame = levelFrame;
        this.level = level;
    }

    public Level getLevel() {return level;}

    public int getAndChangeSpeed() {
        currentSpeedIndex = (currentSpeedIndex + 1) % speedOptions.length;
        logger.info("Game speed changed to " + speedOptions[currentSpeedIndex]/2 + "x (" + speedOptions[currentSpeedIndex] + "x)");
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

    public long elapsedSeconds() {
        return currentFrame / GameObject.getFPS();
    }

    void run() {
        gameLoopThread = new Thread(() -> {
            final int fps = GameObject.getFPS();
            final long frameDuration = 1000 / fps;
            currentFrame = 0;

            while (isRunning) {
                long startTime = System.currentTimeMillis();
                currentFrame++;

                // check if the game is over or won
                Platform.runLater(levelFrame::checkGameStatus);

                // if the game speed is not 1 (on screen 0.5), skip the frame, for faster speeds
                if (currentFrame % speedOptions[currentSpeedIndex] != 0) {
                    level.tick(false);
                    continue;
                } else {
                    level.tick(true);
                }

                // javafx thread
                Platform.runLater(levelFrame::drawLevelObjects);

                long endTime = System.currentTimeMillis();
                long sleepTime = frameDuration - (endTime - startTime);

                if (sleepTime > 0) {
                    LockSupport.parkNanos(sleepTime * 1_000_000);
                }
            }
        });

        gameLoopThread.setDaemon(true);
        gameLoopThread.start();
    }

    public void pause() {
        if (!isRunning) {
            return;
        }
        logger.info("Game Paused");
        isRunning = false;
    }

    public void resume() {
        if (isRunning) {
            return;
        }
        logger.info("Game Resumed");
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

    public void renderScene() {
        if (level.getGoal() != null) {
            level.getGoal().unlockForLevelEditor(); // unlock goal for level editor
        }
        Platform.runLater(levelFrame::drawLevelObjects);
    }

    public void resetLevel() {
        String[] levelData = level.getLevelData();
        boolean isEditor = levelData[1].equals("true");
        boolean isStory = levelData[2].equals("true");

        isRunning = false;
        level.Unload();
        level = new Level(levelData[0], isEditor, isStory);

        if (!level.loadLevel()) {
            logger.warning(ErrorMsgsEnum.LOAD_ERROR.getValue());
            return;
        }

        currentSpeedIndex = 1;

        // wait for the loop to finish; alternatively, we could use a thread join
        if (gameLoopThread != null && gameLoopThread.isAlive()) {
            try {
                gameLoopThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                logger.warning(ErrorMsgsEnum.THREAD_INTERRUPTED.getValue());
            }
        }

        level.Play();
        resume();
    }
}