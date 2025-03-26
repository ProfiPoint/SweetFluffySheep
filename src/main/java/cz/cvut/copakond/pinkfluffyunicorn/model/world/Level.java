package cz.cvut.copakond.pinkfluffyunicorn.model.world;

import cz.cvut.copakond.pinkfluffyunicorn.model.data.SaveManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.GameObject;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class Level {
    List<GameObject> objects;
    int timeLimit;
    int score;

    public Level(String level) {
        JSONObject levelData = SaveManager.readJsonFromFile(level + ".json");
        if (levelData == null) {
            System.err.println("Error loading level data");
            return;
        } else {
            System.out.println("Level loaded");
        }
    }
}
