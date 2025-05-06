package cz.cvut.copakond.sweetfluffysheep.model.world;

import cz.cvut.copakond.sweetfluffysheep.model.entities.Wolf;
import cz.cvut.copakond.sweetfluffysheep.model.items.Item;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.DirectionEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.GameStatusEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ItemEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.FileUtils;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.InitClasses;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.GameObject;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.ProfileManager;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/* assuming the file hash of level 1.json is 38abac4f0688d22604ee8f0719f2298009d7a3d310be8ae5204d1e165dad2734
 * check load tests are to check if the JSON file is loaded correctly
 * runGame tests are to check if the game is running correctly
 */

/**
 * Test class for the Level class.
 * This class contains unit tests to verify the functionality of the Level class.
 * It includes tests for loading levels (including parsing JSON of the level data),
 * checking start and goal positions, tiles, enemies, items,
 * and various game scenarios such as winning, losing with item interactions.
 */
class LevelTest {
    private static final int FPS = 60;
    private static final int TIME_LIMIT_SECONDS = 180;
    private static Level levelNow;

    @BeforeAll
    static void setUp() {
        // Simulate JavaFX application thread
        try {
            javafx.application.Platform.startup(() -> {});
        } catch (IllegalStateException e) {
            // JavaFX runtime is already initialized
        }

        new InitClasses(
                "resources/textures",
                "resources/datasaves/levels",
                "src/test/resources/datasaves/profiles",
                "resources/sounds"
        );

        String currentProfile = FileUtils.readFile("src/test/resources/datasaves/profiles/_CURRENT.txt");
        assertNotNull(currentProfile);
        if (!currentProfile.isBlank()) {
            ProfileManager.switchProfile(currentProfile);
        }

        SoundManager.setSfxVolume(0);
        SoundManager.setMusicVolume(0);
    }

    @AfterEach
    void unload() {
        levelNow.Completed();
        levelNow.Unload();
    }

    Level createLevel(String levelName, boolean isLevelEditor, boolean storyLevel) {
        /* CURRENT MAP (Level 1):
         * Legend:
         * S: start,
         * G: goal,
         * E: enemy starting position,
         * C: coin,
         * R: rage,
         * F: freeze,
         * A/>/V/<: arrow rotated to UP/LEFT/RIGHT/DOWN
         *
         * Map:
         * S0C0G
         * 000R0
         * 0000E
         */

        Level level = new Level(levelName, isLevelEditor, storyLevel);
        level.loadLevel();
        levelNow = level;
        return level;
    }

    @Test
    void checkLoad_startAndGoal() {
        Level level = createLevel("1", false, false);

        Start start = level.getStart();
        assertNotNull(start, "Start object is null");
        assertArrayEquals(new double[]{0.0, 0.0}, start.getPosition(), "Start position incorrect");

        Goal goal = level.getGoal();
        assertNotNull(goal, "Goal object is null");
        assertArrayEquals(new double[]{4.0, 0.0}, goal.getPosition(), "Goal position incorrect");
    }

    @Test
    void checkLoad_tiles() {
        Level level = createLevel("1", false, false);

        List<Tile> tiles = level.getTiles();
        assertNotNull(tiles, "Tiles list is null");
        assertEquals(23, tiles.size(), "Unexpected number of tiles");

        List<Tile> walkableTiles = new ArrayList<>();
        for (Tile tile : tiles) {
            if (tile.isWalkable()) {
                walkableTiles.add(tile);
            }
        }
        assertEquals(15, walkableTiles.size(), "Unexpected number of walkable tiles");

        HashMap<String, Integer> expectedTiles = getStringIntegerHashMap();

        for (Tile tile : walkableTiles) {
            String key = (int) tile.getPosition()[0] + "-" + (int) tile.getPosition()[1];
            assertTrue(expectedTiles.containsKey(key), "Unexpected tile at position: " + key);
            assertEquals(expectedTiles.get(key), tile.getTextureType(), "Incorrect tile type at position: " + key);
            expectedTiles.remove(key);
        }
    }

    private static HashMap<String, Integer> getStringIntegerHashMap() {
        HashMap<String, Integer> expectedTiles = new HashMap<>();
        expectedTiles.put("0-0", 1);
        expectedTiles.put("0-1", 2);
        expectedTiles.put("0-2", 1);
        expectedTiles.put("1-0", 2);
        expectedTiles.put("1-1", 1);
        expectedTiles.put("1-2", 2);
        expectedTiles.put("2-0", 1);
        expectedTiles.put("2-1", 2);
        expectedTiles.put("2-2", 1);
        expectedTiles.put("3-0", 2);
        expectedTiles.put("3-1", 1);
        expectedTiles.put("3-2", 2);
        expectedTiles.put("4-0", 1);
        expectedTiles.put("4-1", 2);
        expectedTiles.put("4-2", 1);
        return expectedTiles;
    }

    @Test
    void checkLoad_levelValues() {
        Level level = createLevel("1", false, false);

        assertEquals(TIME_LIMIT_SECONDS, level.getTimeLimit(), "Time limit incorrect");
        assertArrayEquals(new int[]{24, 12}, level.getMapSize(), "Map size incorrect");
        assertFalse(level.isStoryLevel(), "Story level flag incorrect");
    }

    @Test
    void checkLoad_enemies() {
        Level level = createLevel("1", false, false);

        List<Wolf> enemies = level.getEnemies();
        assertNotNull(enemies, "Enemies list is null");
        assertEquals(1, enemies.size(), "Unexpected number of enemies");

        Wolf enemy = enemies.getFirst();
        assertNotNull(enemy, "Enemy object is null");
        assertEquals(4.0, enemy.getPosition()[0], "Enemy X position incorrect");
        assertEquals(2.0, enemy.getPosition()[1], "Enemy Y position incorrect");
        assertEquals(DirectionEnum.LEFT, enemy.getDirection(), "Enemy direction incorrect");
    }

    @Test
    void checkLoad_items() {
        Level level = createLevel("1", false, false);

        List<Item> items = level.getItems();
        assertNotNull(items, "Items list is null");
        assertEquals(2, items.size(), "Unexpected number of items");

        Item item1 = items.getFirst();
        assertEquals(2.0, item1.getPosition()[0], "Item1 X position incorrect");
        assertEquals(0.0, item1.getPosition()[1], "Item1 Y position incorrect");
        assertEquals(ItemEnum.COIN, item1.getItemEffect(), "Item1 effect incorrect");

        Item item2 = items.get(1);
        assertEquals(3.0, item2.getPosition()[0], "Item2 X position incorrect");
        assertEquals(1.0, item2.getPosition()[1], "Item2 Y position incorrect");
        assertEquals(ItemEnum.RAGE, item2.getItemEffect(), "Item2 effect incorrect");
    }

    @Test
    void runGame_normalWin() {
        /* Map:
            * S0C0G
            * 000R0
            * 0000E
         */
        GameObject.setFPS(FPS);
        Level level = createLevel("1", false, false);
        level.Play();

        for (int i = 0; i < FPS * 7; i++) {
            level.tick(true);
            assertEquals(GameStatusEnum.RUNNING, GameObject.getGameStatus(), "Game not running at tick: " + i);
        }

        level.tick(true);
        assertEquals(GameStatusEnum.WIN, GameObject.getGameStatus(), "Game not won after finish");
    }

    @Test
    void runGame_placeArrows_timeoutLose() {
        /* Map:
         * S>C<G
         * 000R0
         * ><00E
         */

        GameObject.setFPS(FPS);
        Level level = createLevel("1", false, false);
        level.Play();

        placeAndRotateArrow(level, new int[]{1, 0}, 2);
        placeAndRotateArrow(level, new int[]{3, 0}, 4);
        placeAndRotateArrow(level, new int[]{0, 2}, 2);
        placeAndRotateArrow(level, new int[]{1, 2}, 4);

        level.buildObjectsList();

        for (int i = TIME_LIMIT_SECONDS * FPS - 1; i > 0; i--) {
            level.tick(true);
            assertEquals(GameStatusEnum.RUNNING, GameObject.getGameStatus(), "Game prematurely ended at tick: " + i);
        }
        level.tick(true);
        assertEquals(GameStatusEnum.LOSE, GameObject.getGameStatus(), "Game should lose due to timeout");
    }

    @Test
    void runGame_enemyKillsPlayer() {
        /* Map:
         * S>C<G
         * 000R0
         * 0000E
         */

        GameObject.setFPS(FPS);
        Level level = createLevel("1", false, false);
        level.Play();

        placeAndRotateArrow(level, new int[]{1, 0}, 2);
        placeAndRotateArrow(level, new int[]{3, 0}, 4);

        level.buildObjectsList();

        for (int i = 30 * FPS - 1; i > 0; i--) {
            level.tick(true);
            assertNotEquals(GameStatusEnum.WIN, GameObject.getGameStatus(), "Unexpected win at tick: " + i);
        }

        level.tick(true);
        assertEquals(GameStatusEnum.LOSE, GameObject.getGameStatus(), "Game should lose by enemy");
    }

    @Test
    void runGame_killEnemy_win() {
        /* Map:
         * S0CVG
         * V00R0
         * 0000E
         */

        GameObject.setFPS(FPS);
        Level level = createLevel("1", false, false);
        level.Play();

        placeAndRotateArrow(level, new int[]{3, 0}, 3);
        placeAndRotateArrow(level, new int[]{0, 1}, 3);

        level.buildObjectsList();
        Wolf enemy = level.getEnemies().getFirst();
        assertTrue(enemy.isAlive(), "Enemy should initially be alive");

        for (int i = 10 * FPS - 1; i > 0; i--) {
            level.tick(true);
            assertNotEquals(GameStatusEnum.LOSE, GameObject.getGameStatus(), "Unexpected loss at tick: " + i);
        }

        assertFalse(enemy.isAlive(), "Enemy should be dead after 10 seconds");

        for (int i = 10 * FPS - 1; i > 0; i--) {
            level.tick(true);
            assertNotEquals(GameStatusEnum.LOSE, GameObject.getGameStatus(), "Unexpected loss at tick: " + i);
        }

        assertEquals(GameStatusEnum.WIN, GameObject.getGameStatus(), "Game should be won after killing enemy");
    }

    private void placeAndRotateArrow(Level level, int[] position, int rotations) {
        for (int i = 0; i < rotations; i++) {
            level.placeRotateRemoveArrow(position, 1);
        }
    }
}