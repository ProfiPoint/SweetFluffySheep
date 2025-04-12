package cz.cvut.copakond.pinkfluffyunicorn.view;
import cz.cvut.copakond.pinkfluffyunicorn.model.entities.Cloud;
import cz.cvut.copakond.pinkfluffyunicorn.model.entities.Unicorn;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;

import java.time.Instant;
import java.util.List;

public class GameLoop implements Runnable {
    private boolean running = false;
    private final int targetFPS = GameObject.getFPS();
    private final long targetTime = 1000 / targetFPS;

    private List<GameObject> objects;
    private List<Unicorn> unicorns;
    private List<Cloud> clouds;

    public GameLoop(List<GameObject> objects, List<Unicorn> unicorns, List<Cloud> clouds) {
        this.objects = objects;
        this.unicorns = unicorns;
        this.clouds = clouds;
    }

    public void start() {
        running = true;
        new Thread(this).start();
    }

    public void stop() {
        running = false;
    }

    @Override
    public void run() {
        long previousTime = Instant.now().toEpochMilli();

        while (running) {
            long currentTime = Instant.now().toEpochMilli();
            long elapsedTime = currentTime - previousTime;
            previousTime = currentTime;

            // Update game objects
            for (GameObject object : objects) {
                object.tick(true);
            }

            long waitTime = targetTime - elapsedTime;
            if (waitTime > 0) {
                try {
                    Thread.sleep(waitTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}