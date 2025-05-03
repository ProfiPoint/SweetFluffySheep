package cz.cvut.copakond.sweetfluffysheep.model.utils.levels;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.ProfileManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.FileUtils;
import cz.cvut.copakond.sweetfluffysheep.model.utils.json.JsonFileManager;
import cz.cvut.copakond.sweetfluffysheep.model.world.Level;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility class for managing level status (completed, next level, etc.)
 */
public class LevelStatusUtils {
    private static final Logger logger = Logger.getLogger(LevelStatusUtils.class.getName());
    
    private static String levelPath;
    private static String profilesPath;
    private static String currentProfileName;

    public static void setLevelPath(String lp) {
        levelPath = lp;
    }

    public static void setProfilesPath(String p) {
        profilesPath = p;
    }

    /**
     * Marks the level as completed in the profile data.
     *
     * @param level The level to mark as completed.
     * @return true if the level was marked as completed, false otherwise.
     */
    public static boolean markLevelAsCompleted(Level level){
        if (level.isLevelEditor()) return true;

        currentProfileName = ProfileManager.getCurrentProfile();
        List<List<Integer>> original = JsonFileManager.getProfileLFromJsonFile(
                profilesPath + "/" + currentProfileName + "/_DATA.json"
        );
        if (original == null) return false;

        // create a mutable copy of everything
        List<List<Integer>> levelData = new ArrayList<>();
        levelData.add(new ArrayList<>(original.get(0))); // normal levels
        levelData.add(new ArrayList<>(original.get(1))); // editor levels

        // preventing duplicates
        try {
            int levelNumber = Integer.parseInt(level.getLevelData()[0]);
            if (level.isStoryLevel()) {
                List<Integer> normalLevels = levelData.getFirst();
                normalLevels.removeIf(id -> id == levelNumber);
                normalLevels.add(levelNumber);
            } else {
                List<Integer> editorLevels = levelData.get(1);
                editorLevels.removeIf(id -> id == levelNumber);
                editorLevels.add(levelNumber);
            }
        } catch (NumberFormatException e) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_PARSE.getValue("Error parsing level number: " + e.getMessage()));
            return false;
        }

        return JsonFileManager.saveProfileLToJsonFile(
                profilesPath + "/" + currentProfileName + "/_DATA.json",
                levelData
        );
    }

    /**
     * Returns the next uncompleted level.
     * [levelId, customLevel] customLevel = 0-story mode, 1-custom level
     * [1,0] if all levels are completed
     *
     * @return An array containing the level ID and custom level flag (0 for a story, 1 for custom).
     */
    public static int[] getNextUncompletedLevel(){
        currentProfileName = ProfileManager.getCurrentProfile();
        List<List<Integer>> levelData = JsonFileManager.getProfileLFromJsonFile(profilesPath + "/" + currentProfileName + "/_DATA.json");
        if (levelData == null) {
            return new int[]{1, 0};
        }

        int numberOfStoryLevels = FileUtils.getNumberOfFilesInDirectory(levelPath);
        int numberOfCustomLevels = FileUtils.getNumberOfFilesInDirectory(profilesPath + "/" + currentProfileName);

        // Check if all story levels are completed, if not, return the first uncompleted level
        for (int i = 0; i < numberOfStoryLevels; i++) {
            if (!levelData.getFirst().contains(i+1)) {
                return new int[]{i + 1, 0};
            }
        }

        // Check if all custom levels are completed, if not, return the first uncompleted level
        for (int i = 0; i < numberOfCustomLevels; i++) {
            if (!levelData.get(1).contains(i+1)) {
                return new int[]{i + 1, 1};
            }
        }

        return new int[]{1, 0}; // all levels are completed, return first level
    }

    /**
     * Returns the next level based on the current level.
     * If the current level is the last one, repeats the last level.
     *
     * @param level The current level.
     * @return The next level.
     */
    public static Level getNextLevel(Level level){
        currentProfileName = ProfileManager.getCurrentProfile();

        int numberOfStoryLevels = FileUtils.getNumberOfFilesInDirectory(levelPath);
        int numberOfCustomLevels = FileUtils.getNumberOfFilesInDirectory(profilesPath + "/" + currentProfileName);

        // Check if the level is a custom level
        try {
            int levelNumber = Integer.parseInt(level.getLevelData()[0]);
            if (level.isStoryLevel()) {
                if (levelNumber == numberOfStoryLevels) {
                    return new Level(String.valueOf(levelNumber), false, true);
                }
                return new Level(String.valueOf(levelNumber + 1), false, true);
            } else {
                if (levelNumber == numberOfCustomLevels) {
                    return new Level(String.valueOf(levelNumber), false, false);
                }
                return new Level(String.valueOf(levelNumber + 1), false, false);
            }
        } catch (NumberFormatException e) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_PARSE.getValue(e.getMessage()));
        }

        return null;
    }
}