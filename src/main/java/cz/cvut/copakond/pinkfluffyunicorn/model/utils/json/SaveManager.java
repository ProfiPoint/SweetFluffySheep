package cz.cvut.copakond.pinkfluffyunicorn.model.utils.json;

import cz.cvut.copakond.pinkfluffyunicorn.model.entities.Cloud;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Goal;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Start;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Tile;
import org.json.JSONArray;
import org.json.JSONObject;
import cz.cvut.copakond.pinkfluffyunicorn.model.items.*;

import java.util.List;
import java.util.Map;

public class SaveManager {
    private JSONObject data;

    public SaveManager(JSONObject data) {
        this.data = data;
    }

    public void addDefaultLevelData(boolean isDefaultLevel) {
        data.put("defaultLevel", isDefaultLevel);
    }

    public void addStartGoalData(Start start, Goal goal) {
        data.put("start", new JSONArray(new int[]{(int)Math.round(start.getPosition()[0]),
                (int)Math.round(start.getPosition()[1]),
                start.getDirection().getValue()}));
        data.put("goal", new JSONArray(new int[]{(int)Math.round(goal.getPosition()[0]),
                (int)Math.round(goal.getPosition()[1]), goal.getDirection().getValue()}));
    }

    public void addMapSizeData(int[] mapSize) {
        data.put("mapSize", new JSONArray(mapSize));
    }

    public void addLevelInfo(Map<String, Integer> levelInfo) {
        int currentTimeInMinutes = (int) (System.currentTimeMillis() / (1000 * 60));
        data.put("timeLimit", levelInfo.get("timeLimit"));
        data.put("unicorns", levelInfo.get("unicorns"));
        data.put("goalUnicorns", levelInfo.get("goalUnicorns"));
        data.put("maxArrows", levelInfo.get("maxArrows"));
        data.put("creationTime", levelInfo.get("creationTime"));
        data.put("updatedTime", currentTimeInMinutes); // in minutes
    }

    public void addPlayerInfo(Map<String, String> playerInfo) {
        data.put("creator", playerInfo.get("creator"));
        data.put("creatorUpdated", playerInfo.get("creatorUpdated"));
    }

    public void addTilesData(List<Tile> tiles) {
        JSONObject tilesObj = new JSONObject();
        for (Tile tile : tiles) {
            String key = (int)Math.round(tile.getPosition()[0]) + "-" + (int)Math.round(tile.getPosition()[1]);
            tilesObj.put(key, tile.getTextureType());
        }
        data.put("tiles", tilesObj);
    }

    public void addEnemiesData(List<Cloud> enemies) {
        JSONObject enemiesObj = new JSONObject();
        for (Cloud enemy : enemies) {
            String key = (int)Math.round(enemy.getPosition()[0]) + "-" + (int)Math.round(enemy.getPosition()[1]);
            enemiesObj.put(key, enemy.getDirection().getValue());
        }
        data.put("enemies", enemiesObj);
    }

    public void addCoinsData(List<Coin> coins) {
        JSONArray coinsArray = new JSONArray();
        for (Coin coin : coins) {
            coinsArray.put(new JSONArray(new int[]{(int)Math.round(coin.getPosition()[0]),
                    (int)Math.round(coin.getPosition()[1])}));
        }
        data.put("coins", coinsArray);
    }

    public void addItemsData(List<Item> items) {
        JSONArray itemsArray = new JSONArray();
        for (Item item : items) {
            double[] itemPos = item.getPosition();
            itemsArray.put(new JSONArray(new int[]{(int)Math.round(itemPos[0]), (int)Math.round(itemPos[1]),
                    item.getItemEffect().ordinal(), 15})); // Assuming duration is 15
        }
        data.put("items", itemsArray);
    }
}
