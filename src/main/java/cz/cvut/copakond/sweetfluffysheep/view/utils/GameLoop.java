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

/**
 * GameLoop class is responsible for managing the game loop, including updating the game state and rendering.
 * It handles the speed of the game, pausing and resuming the game, and resetting the level.
 */
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

    /**
     * Constructor for GameLoop.
     *
     * @param levelFrame the level frame to be used for rendering
     * @param level      the level to be managed by the game loop
     */
    public GameLoop(ILevelFrame levelFrame, Level level) {
        this.levelFrame = levelFrame;
        this.level = level;
    }

    public Level getLevel() {return level;}

    /**
     * Changes the speed of the game.
     *
     * @return the new speed of the game
     */
    public int getAndChangeSpeed() {
        currentSpeedIndex = (currentSpeedIndex + 1) % speedOptions.length;
        logger.info("Game speed changed to " + speedOptions[currentSpeedIndex]/2 + "x (" + speedOptions[currentSpeedIndex] + "x)");
        return speedOptions[currentSpeedIndex];
    }

    /**
     * Sets the new level for the next level called from the level frame.
     */
    public void setNewLevel() {
        level = LevelStatusUtils.getNextLevel(level);
        resetLevel();
    }

    public List<GameObject> getObjects() {return objects;}

    /**
     * Sets the list of game objects to be managed by the game loop.
     *
     * @param objects the list of game objects
     */
    public void setObjects(List<GameObject> objects) {
        this.objects = objects;
        objects.sort(Comparator.comparingInt(GameObject::getRenderPriority));
    }

    public boolean isRunning() {
        return isRunning;
    }

    /**
     * Returns the current frame of the game loop.
     *
     * @return the current frame
     */
    public long elapsedSeconds() {
        return currentFrame / GameObject.getFPS();
    }

    /**
     * Creates a new thread for the game loop and starts it.
     * The thread is responsible for updating the game state and rendering the game objects.
     * It runs at a fixed frame rate based on the FPS defined in GameObject.
     * The game loop will continue running until the isRunning flag is set to false.
     */
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

        // set the thread as a daemon thread, so it doesn't block the JVM from exiting
        gameLoopThread.setDaemon(true);
        gameLoopThread.start();
    }

    /**
     * Pauses the game loop. The game loop will stop updating the game state and rendering.
     * The pause method can be called multiple times without any effect.
     */
    public void pause() {
        if (!isRunning) {
            return;
        }
        logger.info("Game Paused");
        isRunning = false;
    }

    /**
     * Resumes the game loop. The game loop will start updating the game state and rendering again.
     * The resume method can be called multiple times without any effect.
     */
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

    /**
     * Unloads the game loop. The game loop will stop updating the game state and rendering.
     * The unload method can be called multiple times without any effect.
     */
    public void unload() {
        pause();
        AppViewManager.get().setClickListener(null);
        level.Unload();
    }

    /**
     * Renders the game scene. This method is called from the JavaFX thread.
     * It unlocks the goal for level editor and draws the level objects.
     */
    public void renderScene() {
        if (level.getGoal() != null) {
            level.getGoal().unlockForLevelEditor(); // unlock goal for level editor
        }
        Platform.runLater(levelFrame::drawLevelObjects);
    }

    /**
     * Resets the level. This method is called when the level is changed or when the game is restarted.
     * It unloads the current level and loads a new one based on the level data.
     * The resetLevel method can be called multiple times without any effect.
     */
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