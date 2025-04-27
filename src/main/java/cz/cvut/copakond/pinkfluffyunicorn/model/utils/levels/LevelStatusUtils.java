package cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.ProfileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.FileUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.json.JsonFileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;

import java.util.ArrayList;
import java.util.List;

public class LevelStatusUtils {
    private static String levelPath;
    private static String profilesPath;
    private static String currentProfileName;

    public static void setLevelPath(String lp) {
        levelPath = lp;
    }

    public static void setProfilesPath(String p) {
        profilesPath = p;
    }

    public static boolean markLevelAsCompleted(Level level){
        if (level.isLevelEditor()) return true;

        currentProfileName = ProfileManager.getCurrentProfile();
        List<List<Integer>> original = JsonFileManager.getProfileLFromJsonFile(
                profilesPath + "/" + currentProfileName + "/_DATA.json"
        );
        if (original == null) return false;

        // Create mutable copy of everything
        List<List<Integer>> levelData = new ArrayList<>();
        levelData.add(new ArrayList<>(original.get(0))); // normal levels
        levelData.add(new ArrayList<>(original.get(1))); // editor levels

        try {
            int levelNumber = Integer.parseInt(level.getLevelData()[0]);
            if (!level.isStoryLevel()) {
                List<Integer> editorLevels = levelData.get(1);
                for (int i = 0; i < editorLevels.size(); i++) {
                    if (editorLevels.get(i) == levelNumber) {
                        editorLevels.remove(i);
                        break;
                    }
                }
                editorLevels.add(levelNumber);

            } else {
                List<Integer> normalLevels = levelData.get(0);
                for (int i = 0; i < normalLevels.size(); i++) {
                    if (normalLevels.get(i) == levelNumber) {
                        normalLevels.remove(i);
                        break;
                    }
                }
                normalLevels.add(levelNumber);

            }
        } catch (NumberFormatException e) {
            ErrorMsgsEnum.LOAD_JSON_PARSE.getValue("Error parsing level number: " + e.getMessage());
            return false;
        }

        return JsonFileManager.saveProfileLToJsonFile(
                profilesPath + "/" + currentProfileName + "/_DATA.json",
                levelData
        );
    }

    // [levelId,  customLevel] customLevel = 0 story mode, 1 custom level
    // [1,0] if all levels are completed
    public static int[] getNextUncompletedLevel(){
        currentProfileName = ProfileManager.getCurrentProfile();
        List<List<Integer>> levelData = JsonFileManager.getProfileLFromJsonFile(profilesPath + "/" + currentProfileName + "/_DATA.json");
        if (levelData == null) {
            return new int[]{1, 0};
        }
        int numberOfStoryLevels = FileUtils.getNumberOfFilesInDirectory(levelPath);
        int numberOfCustomLevels = FileUtils.getNumberOfFilesInDirectory(profilesPath + "/" + currentProfileName);
        for (int i = 0; i < numberOfStoryLevels; i++) {
            if (!levelData.get(0).contains(i+1)) {
                return new int[]{i + 1, 0};
            }
        }
        for (int i = 0; i < numberOfCustomLevels; i++) {
            if (!levelData.get(1).contains(i+1)) {
                return new int[]{i + 1, 1};
            }
        }

        return new int[]{1, 0};
    }

    // [levelId, customLevel] customLevel = 0 story mode, 1 custom level
    // [0,0] if this was the last level of story/custom level
    public static Level getNextLevel(Level level){
        currentProfileName = ProfileManager.getCurrentProfile();
        int numberOfStoryLevels = FileUtils.getNumberOfFilesInDirectory(levelPath);
        int numberOfCustomLevels = FileUtils.getNumberOfFilesInDirectory(profilesPath + "/" + currentProfileName);
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
            ErrorMsgsEnum.LOAD_JSON_PARSE.getValue(e.getMessage());
        }
        return null;
    }
}
