package cz.cvut.copakond.sweetfluffysheep.model.utils.json;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ErrorMsgsEnum;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * LoadManager is responsible for loading data from a JSON object.
 * It provides methods to retrieve various types of data, including booleans, integers, strings,
 * and lists of integers with specific constraints.
 */
public class LoadManager {
    private static final Logger logger = Logger.getLogger(LoadManager.class.getName());

    private final JSONObject data;

    /**
     * Constructor for LoadManager.
     * @param data The JSON object containing the data to be loaded.
     */
    public LoadManager(JSONObject data) {
        this.data = data;
    }

    /**
     * Retrieves a boolean value from the JSON object.
     * @param key The key to look for in the JSON object.
     * @return The boolean value associated with the key, or false if not found.
     */
    public boolean getBoolean(String key) {
        if (!data.has(key)) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_KEY_NOT_FOUND.getValue("Key: " + key));
            return false;
        }
        return data.optBoolean(key, false);
    }

    /**
     * Retrieves a list of three integers from the JSON object.
     * The integers represent coordinates and a direction.
     * @param key The key to look for in the JSON object.
     * @param limit The limits for the coordinates.
     * @return An array of three integers representing coordinates and direction, or null if invalid.
     */
    public int[] getList3(String key, int[] limit) {
        JSONArray arr = getJSONArray(key);
        if (arr == null || arr.length() != 3) {
            logger.severe(ErrorMsgsEnum.LOAD_LIST_IntIntOrientation.getValue("Key: " + key));
            return null;
        }

        int x = arr.optInt(0, -1);
        int y = arr.optInt(1, -1);
        int z = arr.optInt(2, -1);

        if (isNotValidCoordinate(x, y, limit) || isNotValidRotation(z)) {
            logger.severe(ErrorMsgsEnum.LOAD_VALUES3_LIMIT.getValue("Key: " + key));
            return null;
        }
        return new int[]{x, y, z};
    }

    /**
     * Retrieves a list of two integers from the JSON object.
     * @param key The key to look for in the JSON object.
     * @return An array of two integers, or null if invalid.
     */
    public int[] getList2NoLimit(String key) {
        JSONArray arr = getJSONArray(key);
        if (arr == null || arr.length() != 2) {
            logger.severe(ErrorMsgsEnum.LOAD_LIST_IntInt.getValue("Key: " + key));
            return null;
        }

        int x = arr.optInt(0, -1);
        int y = arr.optInt(1, -1);

        return new int[]{x, y};
    }

    /**
     * Retrieves a list of two integers from the JSON object with specified limits.
     * @param key The key to look for in the JSON object.
     * @param limit The limits for the coordinates.
     * @return An array of two integers representing coordinates, or null if invalid.
     */
    public Integer getIntLimit(String key, int limit) {
        if (!data.has(key)) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_KEY_NOT_FOUND.getValue("Key: " + key));
            return null;
        }
        int value = data.optInt(key, -1);
        if (value < 0 || value > limit) {
            logger.severe(ErrorMsgsEnum.LOAD_VALUE_OUT_OF_RANGE.getValue("Key: " + key + " Lower limit: 0, Upper limit: " + limit));
            return null;
        }
        return value;
    }

    /**
     * Retrieves a list of two integers from the JSON object without limits.
     * @param key The key to look for in the JSON object.
     * @return An array of two integers representing coordinates, or null if invalid.
     */
    public Integer getInt(String key) {
        if (!data.has(key)) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_KEY_NOT_FOUND.getValue("Key: " + key));
            return null;
        }
        return data.optInt(key, -1);
    }

    /**
     * Retrieves a string value from the JSON object.
     * @param key The key to look for in the JSON object.
     * @return The string value associated with the key, or null if not found.
     */
    public String getString(String key) {
        if (!data.has(key)) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_KEY_NOT_FOUND.getValue("Key: " + key));
            return null;
        }
        return data.optString(key, null);
    }

    /**
     * Retrieves a list of lists of integers from the JSON object with specified limits.
     * @param key The key to look for in the JSON object.
     * @param limit The limits for the coordinates.
     * @param valueLimit The upper limit for the values.
     * @return A list of integer arrays representing coordinates and values, or null if invalid.
     */
    public List<int[]> getListOfListsWithLimit(String key, int[] limit, int valueLimit) {
        JSONArray arr = getJSONArray(key);
        if (arr == null) return null;
        List<int[]> result = new ArrayList<>();

        for (int i = 0; i < arr.length(); i++) {
            JSONArray subArr = arr.optJSONArray(i);
            if (subArr == null || subArr.length() != 4) continue;

            int x = subArr.optInt(0, -1);
            int y = subArr.optInt(1, -1);
            int v = subArr.optInt(2, -1);
            int d = subArr.optInt(3, -1);

            if (isNotValidCoordinate(x, y, limit) || v < 0 || v > valueLimit || d < 0) {
                logger.severe(ErrorMsgsEnum.LOAD_VALUES4_LIMIT.getValue("key: " + key + " x: " + x + " y: " + y + " v: " + v + " d: " + d));
                continue;
            }
            result.add(new int[]{x, y, v, d});
        }
        return result;
    }

    /**
     * Retrieves a list of lists of integers from the JSON object with specified limits and direction.
     * @param key The key to look for in the JSON object.
     * @param limit The limits for the coordinates.
     * @param canBeEmpty Whether the result can be empty.
     * @return A list of integer arrays representing coordinates and values, or null if invalid.
     * 3rd value is direction (0, 90, 180, 270)
     */
    public List<int[]> getListOfListsWithDirFromDict(String key, int[] limit, boolean canBeEmpty) {
        if (!data.has(key)) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_KEY_NOT_FOUND.getValue("Key: " + key));
            return null;
        }

        JSONObject obj = data.optJSONObject(key);
        if (obj == null) return null;

        List<int[]> result = new ArrayList<>();
        for (String k : obj.keySet()) {
            String[] parts = k.split("-");
            if (parts.length != 2) continue;

            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int v = obj.optInt(k, -1);

            if (isNotValidCoordinate(x, y, limit) || isNotValidRotation(v)) {
                logger.severe(ErrorMsgsEnum.LOAD_VALUES3_LIMIT.getValue("key: " + key + " x: " + x + " y: " + y + " v: " + v));
                continue;
            }
            result.add(new int[]{x, y, v});
        }

        if (result.isEmpty() && !canBeEmpty) {
            logger.severe(ErrorMsgsEnum.LOAD_EMPTY_LIST.getValue("dict Key: " + key));
            return null;
        }

        return result;
    }

    /**
     * Retrieves a list of lists of integers from the JSON object with specified limits and value limits.
     * @param key The key to look for in the JSON object.
     * @param limit The limits for the coordinates.
     * @param valueLimit The upper limit for the values.
     * @return A list of integer arrays representing coordinates and values, or null if invalid.
     */
    public List<int[]> getListOfListsWithLimitFromDict(String key, int[] limit, int valueLimit) {
        if (!data.has(key)) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_KEY_NOT_FOUND.getValue("Key: " + key));
            return null;
        }

        JSONObject obj = data.optJSONObject(key);
        if (obj == null) return null;

        List<int[]> result = new ArrayList<>();
        for (String k : obj.keySet()) {
            String[] parts = k.split("-");
            if (parts.length != 2) continue;

            int x = Integer.parseInt(parts[0]);
            int y = Integer.parseInt(parts[1]);
            int v = obj.optInt(k, -1);

            if (isNotValidCoordinate(x, y, limit) || v < 0 || v > valueLimit) {
                logger.severe(ErrorMsgsEnum.LOAD_VALUES3path_LIMIT.getValue("key: " + key + " x: " + x + " y: " + y + " v: " + v));
                continue;
            }
            result.add(new int[]{x, y, v});
        }

        if (result.isEmpty()) {
            logger.severe(ErrorMsgsEnum.LOAD_EMPTY_LIST.getValue("dict Key: " + key));
            return null;
        }

        return result;
    }

    /**
     * Checks if the given coordinates are valid within the specified limits.
     * @param x The x-coordinate.
     * @param y The y-coordinate.
     * @param limit The limits for the coordinates.
     * @return True if the coordinates are valid, false otherwise.
     */
    private boolean isNotValidCoordinate(int x, int y, int[] limit) {
        return x < 0 || x >= limit[0] || y < 0 || y >= limit[1];
    }

    /**
     * Checks if the given rotation value is valid.
     * @param r The rotation value.
     * @return True if the rotation value is valid, false otherwise.
     */
    private boolean isNotValidRotation(int r) {
        return r != 0 && r != 90 && r != 180 && r != 270;
    }

    /**
     * Retrieves a JSONArray from the JSON object.
     * @param key The key to look for in the JSON object.
     * @return The JSONArray associated with the key, or null if not found.
     */
    private JSONArray getJSONArray(String key) {
        if (!data.has(key)) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_KEY_NOT_FOUND.getValue("Key: " + key));
            return null;
        }

        return data.optJSONArray(key);
    }
}