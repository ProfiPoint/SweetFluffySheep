package cz.cvut.copakond.sweetfluffysheep.model.world;

import cz.cvut.copakond.sweetfluffysheep.model.entities.Wolf;
import cz.cvut.copakond.sweetfluffysheep.model.entities.Sheep;
import cz.cvut.copakond.sweetfluffysheep.model.items.Coin;
import cz.cvut.copakond.sweetfluffysheep.model.items.IItem;
import cz.cvut.copakond.sweetfluffysheep.model.items.Item;
import cz.cvut.copakond.sweetfluffysheep.model.items.ItemFactory;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.*;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.ProfileManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.utils.json.JsonFileManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.json.LoadManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.json.SaveManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.levels.GamePhysics;
import cz.cvut.copakond.sweetfluffysheep.model.utils.levels.LevelStatusUtils;
import org.json.JSONObject;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.logging.Logger;

public class Level {
    private static final Logger logger = Logger.getLogger(Level.class.getName());
    
    // for render purposes
    private static JSONObject levelData;
    private static CopyOnWriteArrayList<GameObject> objects; // CopyOnWriteArrayList is used to avoid thread issues
    private static String levelPath;
    private static String profilesPath;
    private static String path;

    // 10 seconds with 60 FPS and 2x speed => currentRenderedFrame = 600, currentCalculatedFrame = 1200
    private static long currentCalculatedFrame = 0;

    // level info
    private final String levelName;
    private boolean isLevelEditor = false;
    private boolean isStoryLevel = false; // false = custom level, true = non deletable default level

    // game objects itself
    private int[] mapSize;
    private boolean defaultLevel = false;
    private boolean levelIsCompleted = false;

    // level objects
    private Start start;
    private Goal goal;
    private List<Tile> tiles = new ArrayList<>();
    private final List<Wolf> enemies = new ArrayList<>();
    private final List<Sheep> sheep = new ArrayList<>();
    private final List<Item> items = new ArrayList<>();
    private final List<Arrow> arrows = new ArrayList<>();

    // creator, creatorUpdated
    private final Map<String, String> playerInfo = new HashMap<>();

    // timeLimit, sheep, goalSheep, maxArrows, creationTime, updatedTime
    private final Map<String, Integer> levelInfo = new HashMap<>();

    private double timeLeft;

    public Level(String level, boolean isLevelEditor, boolean storyLevel, boolean newLevel) {
        this.levelName = level;
        initLevel(level, isLevelEditor, storyLevel, newLevel);
    }

    public Level(String level, boolean isLevelEditor, boolean storyLevel) {
        this.levelName = level;
        initLevel(level, isLevelEditor, storyLevel, false);
    }

    // used to render the level
    public List<GameObject> getListOfObjects() {
        buildObjectsList();
        return objects;
    }

    public int[] getMapSize() {
        return mapSize;
    }

    public boolean isLevelEditor() {
        return isLevelEditor;
    }

    public boolean isStoryLevel() {
        return isStoryLevel;
    }

    public static long getCurrentCalculatedFrame() {
        return currentCalculatedFrame;
    }

    public int getTimeLimit() {
        return levelInfo.get("timeLimit");
    }

    public int getTimeLeft() {
        return (int) timeLeft / GameObject.getFPS();
    }

    public int[] getCoinsLeftAndCoins() {
        return new int[]{Coin.getCoinsLeft(), Coin.getTotalCoins()};
    }

    public Start getStart() {
        return start;
    }

    public Goal getGoal() {
        return goal;
    }

    public List<Tile> getTiles() {
        return tiles;
    }

    public List<Wolf> getEnemies() {
        return enemies;
    }

    public List<Item> getItems() {
        return items;
    }

    public Map<String, Integer> getLevelInfo() {
        return levelInfo;
    }

    public int getLives() {
        return Sheep.getSheepAlive() - levelInfo.get("goalSheep") + 1;
    }

    public String[] getLevelData() {
        String isLevelEditor = this.isLevelEditor ? "true" : "false";
        String isStoryLevel = this.isStoryLevel ? "true" : "false";
        return new String[]{levelName, isLevelEditor, isStoryLevel};
    }

    public static void setLevelPath(String p) {
        levelPath = p;
    }

    public static void setProfilesPath(String p) {
        profilesPath = p;
    }

    public void setStart(Start start) {
        this.start = start;
    }

    public void setGoal(Goal goal) {
        this.goal = goal;
    }

    private void initLevel(String level, boolean isLevelEditor, boolean storyLevel, boolean newLevel) {
        path = storyLevel ? levelPath + "/" : profilesPath + "/" + ProfileManager.getCurrentProfile() + "/";
        this.isLevelEditor = isLevelEditor;
        this.isStoryLevel = storyLevel;

        if (newLevel) {
            levelData = JsonFileManager.readJsonFromFile(levelPath + "/_TEMPLATE.json");
            if (levelData == null) {
                logger.severe(ErrorMsgsEnum.LOAD_DEFAULT.getValue());
            } else {
                logger.info("Default Level loaded");
            }
        } else {
            levelData = JsonFileManager.readJsonFromFile(path + level + ".json");
        }

        if (levelData == null) {
            logger.severe(ErrorMsgsEnum.CUSTOM_ERROR.getValue("Error loading level data"));
        } else {
            logger.info("Level loaded");
        }
    }

    // main function to load the level
    public boolean loadLevel() {
        if (levelData == null) return false;

        LoadManager lm = new LoadManager(levelData);

        mapSize = lm.getList2NoLimit("mapSize");
        defaultLevel = lm.getBoolean("defaultLevel");
        if (mapSize == null) return false;

        int[] startCoords = lm.getList3("start", mapSize);
        int[] endCoords = lm.getList3("goal", mapSize);
        if (startCoords == null || endCoords == null) return false;

        start = new Start(new double[]{startCoords[0], startCoords[1]}, DirectionEnum.fromValue(startCoords[2]));
        goal = new Goal(new double[]{endCoords[0], endCoords[1]}, DirectionEnum.fromValue(endCoords[2]));

        playerInfo.put("creator", lm.getString("creator"));
        playerInfo.put("creatorUpdated", lm.getString("creatorUpdated"));
        if (playerInfo.get("creator") == null || playerInfo.get("creatorUpdated") == null) return false;

        levelInfo.put("timeLimit", lm.getIntLimit("timeLimit", 60 * 60 * 24));
        levelInfo.put("sheep", lm.getIntLimit("sheep", 1000));
        levelInfo.put("maxArrows", Math.min(lm.getInt("maxArrows"), mapSize[0] * mapSize[1]));
        levelInfo.put("creationTime", lm.getInt("creationTime"));
        levelInfo.put("updatedTime", lm.getInt("updatedTime"));
        levelInfo.put("goalSheep", lm.getIntLimit("goalSheep", levelData.getInt("sheep")));
        levelInfo.put("defaultItemDuration", 10);
        if (levelInfo.get("updatedTime") == 0) {
            levelInfo.put("updatedTime", (int) (System.currentTimeMillis() / (1000 * 60)));
        }

        Sheep.setGoalSheep(levelData.getInt("goalSheep"));

        List<int[]> tileCoords = lm.getListOfListsWithLimitFromDict("tiles", mapSize, TextureListEnum.TILE.getCount());
        if (tileCoords == null) return false;
        for (int[] coord : tileCoords) {
            tiles.add(new Tile(new double[]{coord[0], coord[1]}, coord[2] * 16, true));
        }

        List<int[]> enemyCoords = lm.getListOfListsWithDirFromDict("enemies", mapSize, true);
        if (enemyCoords == null) return false;
        for (int[] coord : enemyCoords) {
            enemies.add(new Wolf(new double[]{coord[0], coord[1]}, DirectionEnum.fromValue(coord[2])));
        }

        List<int[]> itemCoords = lm.getListOfListsWithLimit("items", mapSize, ItemEnum.getNumberOfItems());
        if (itemCoords == null) return false;
        for (int[] coord : itemCoords) {
            ItemEnum itemEnum = ItemEnum.values()[coord[2]];
            IItem iitem = ItemFactory.createItem(itemEnum, new double[]{coord[0], coord[1]}, coord[3]);
            items.add((Item) iitem);
        }

        buildObjectsList();
        logger.info("Level " + levelName + " loaded");
        return true; // level is loaded successfully, without any errors :D
    }

    public boolean saveLevel() {
        JSONObject levelData = new JSONObject();
        SaveManager sm = new SaveManager(levelData);
        List<Tile> tilesFiltered = new ArrayList<>();

        for (Tile tile : tiles) {
            if (tile.isWalkable()) {
                tilesFiltered.add(tile);
            }
        }

        sm.addDefaultLevelData(defaultLevel);
        sm.addStartGoalData(start, goal);
        sm.addMapSizeData(mapSize);
        sm.addLevelInfo(levelInfo);
        sm.addPlayerInfo(playerInfo);
        sm.addTilesData(tilesFiltered);
        sm.addEnemiesData(enemies);
        sm.addItemsData(items);

        return JsonFileManager.writeJsonToFile(path + levelName + ".json", levelData);
    }

    // used to update the level by the game loop
    public void tick(boolean doesTimeFlow) {
        if (doesTimeFlow){
            if (timeLeft > 0 && GameObject.getGameStatus() == GameStatusEnum.RUNNING) {
                timeLeft--;

                if (timeLeft <= 0) {
                    timeLeft = 0;
                    GameObject.setGameStatusLose();
                }

                if (timeLeft/GameObject.getFPS() <= 6 && timeLeft % GameObject.getFPS() == 0 && timeLeft != 0) {
                    SoundManager.playSound(SoundListEnum.TIME_OUT);
                }
            }
        }
        currentCalculatedFrame++;

        // GameObjects are updated in another thread, so we need to catch the exception,
        // But this should not happen because we are using CopyOnWriteArrayList
        try {
            for (GameObject object : objects) {
                object.tick(doesTimeFlow);
            }
        } catch (ConcurrentModificationException e) {
            logger.warning("Frame skipped due to concurrent modification, if this happens often, consider lowering the FPS");
        }
    }

    public void placeRotateRemoveArrow(int[] tileClick, int button) {
        if (button == 1) {
            for (Arrow arrow : arrows) {
                if (arrow.getPosition()[0] == tileClick[0] && arrow.getPosition()[1] == tileClick[1]) {
                    arrow.rotate();
                    SoundManager.playSound(SoundListEnum.ARROW);
                    return;
                }
            }

            // no arrow found, create a new one
            Arrow arrow = new Arrow(new double[]{tileClick[0], tileClick[1]}, levelInfo.get("maxArrows"));
            if (arrow.isVisible()) {
                arrows.add(arrow);
                SoundManager.playSound(SoundListEnum.ARROW);
            } else {
                logger.info("Arrow not created");
            }
        } else if (button == 2) {
            // remove arrow
            for (int i = 0; i < arrows.size(); i++) {
                Arrow arrow = arrows.get(i);
                if (arrow.getPosition()[0] == tileClick[0] && arrow.getPosition()[1] == tileClick[1]) {
                    arrow.destroy();
                    arrows.remove(i);
                    SoundManager.playSound(SoundListEnum.ARROW_DEL);
                    return;
                }
            }
        }
    }

    public void buildObjectsList() {
        objects = new CopyOnWriteArrayList<>();
        buildDecorationTiles();
        if (start != null) {objects.add(start);}
        if (goal != null) {objects.add(goal);}
        objects.addAll(tiles);
        objects.addAll(enemies);
        objects.addAll(sheep);
        objects.addAll(arrows);
        for (IItem item : items) {
            objects.add((GameObject)item);
        }
    }

    public void Play() {
        timeLeft = (double) levelInfo.get("timeLimit") * GameObject.getFPS();
        start.setVisibility(isLevelEditor);

        // init sheep
        DirectionEnum direction = start.getDirection();
        double[] coords;
        DirectionEnum sheepDirection = direction.getOppositeDirection();
        double[] unitDirection = new double[]{0, 0};

        switch (direction) {
            case LEFT -> { coords = new double[]{-1, start.getPosition()[1]}; unitDirection[0] = -1; }
            case RIGHT -> { coords = new double[]{mapSize[0], start.getPosition()[1]}; unitDirection[0] = 1; }
            case UP -> { coords = new double[]{start.getPosition()[0], -1}; unitDirection[1] = -1; }
            default -> { coords = new double[]{start.getPosition()[0], mapSize[1]}; unitDirection[1] = 1; }
        }

        logger.info("number of sheep: " + levelInfo.get("sheep"));
        for (int i = 0; i < levelInfo.get("sheep"); i++) {
            sheep.add(new Sheep(new double[]{coords[0], coords[1]}, sheepDirection));
            coords[0] += unitDirection[0];
            coords[1] += unitDirection[1];
        }

        GamePhysics.loadMapObjects(start, goal, tiles, enemies, sheep, items, arrows);
        buildObjectsList();
        logger.info("Level Played");
    }

    public void Unload() {
        for (GameObject object : objects) {
            object.resetLevel();
        }

        objects = new CopyOnWriteArrayList<>();
        GamePhysics.unloadMapObjects();
        levelIsCompleted = false;
    }

    public void Completed() {
        if (levelIsCompleted) { return; }
        if (!LevelStatusUtils.markLevelAsCompleted(this)) {
            logger.severe(ErrorMsgsEnum.SAVE_JSON_FILE.getValue("Error saving level status"));
        }
        levelIsCompleted = true;
    }

    private void buildDecorationTiles() {
        Map<String, Tile> allTiles = new HashMap<>();
        List<Tile> newTiles = new ArrayList<>();

        for (Tile tile : tiles) {
            if (tile.isWalkable())  {
                newTiles.add(tile);
                allTiles.put(tile.getTileName(), tile);
            }
        }
        tiles = newTiles;

        // get all the border tiles
        List<Tile> toBeChecked = new ArrayList<>();
        for (Tile tile : tiles) {
            if (!tile.isWalkable()) {
                continue;
            }

            int[] position = new int[]{(int) tile.getPosition()[0], (int) tile.getPosition()[1]};
            int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}}; // left, down, right, up
            for (int[] direction : directions) {
                double[] newPosition = new double[]{position[0] + direction[0], position[1] + direction[1]};
                int[] newPositionInt = new int[]{(int) newPosition[0], (int) newPosition[1]};

                // check if the newPosition is in the map
                if (newPositionInt[0] >= 0 && newPositionInt[0] < mapSize[0] && newPositionInt[1] >= 0 && newPositionInt[1] < mapSize[1]) {
                    String name = newPositionInt[0] + "-" + newPositionInt[1];
                    if (!allTiles.containsKey(name)) {

                        // calculate the texture index based on the surrounding tiles
                        int tileIndex = 16 * ((newPositionInt[0] + newPositionInt[1] + 1) % 2);
                        boolean[] areWalkableTilesAround = getNumberOfWalkableTilesAround(newPositionInt, allTiles);

                        // check if the tile is walkable
                        for (int i = 0; i < areWalkableTilesAround.length; i++) {
                            if (areWalkableTilesAround[i]) {
                                tileIndex += (int) Math.pow(2, i);
                            }
                        }

                        // create the border decoration tile
                        Tile newTile = new Tile(newPosition, tileIndex, false);
                        toBeChecked.add(newTile);
                        allTiles.put(name, newTile);
                    }
                }
            }
        }

        tiles.addAll(toBeChecked);
    }

    private boolean[] getNumberOfWalkableTilesAround(int[] position, Map<String, Tile> allTiles) {
        boolean[] walkableTiles = new boolean[4];
        int[][] directions = {{-1, 0}, {0, 1}, {1, 0}, {0, -1}};
        for (int i = 0; i < directions.length; i++) {
            int[] newPosition = {position[0] + directions[i][0], position[1] + directions[i][1]};
            Tile tile = allTiles.get(newPosition[0] + "-" + newPosition[1]);
            walkableTiles[i] = tile != null && tile.isWalkable();
        }
        return walkableTiles;
    }
}

