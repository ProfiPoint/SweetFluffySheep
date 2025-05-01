package cz.cvut.copakond.sweetfluffysheep.model.utils.json;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ErrorMsgsEnum;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

public class LoadManager {
    private static final Logger logger = Logger.getLogger(LoadManager.class.getName());

    private final JSONObject data;

    public LoadManager(JSONObject data) {
        this.data = data;
    }

    public boolean getBoolean(String key) {
        if (!data.has(key)) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_KEY_NOT_FOUND.getValue("Key: " + key));
            return false;
        }
        return data.optBoolean(key, false);
    }

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

    public Integer getInt(String key) {
        if (!data.has(key)) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_KEY_NOT_FOUND.getValue("Key: " + key));
            return null;
        }
        return data.optInt(key, -1);
    }

    public String getString(String key) {
        if (!data.has(key)) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_KEY_NOT_FOUND.getValue("Key: " + key));
            return null;
        }
        return data.optString(key, null);
    }

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

    // 3rd value is direction (0, 90, 180, 270)
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

    private boolean isNotValidCoordinate(int x, int y, int[] limit) {
        return x < 0 || x >= limit[0] || y < 0 || y >= limit[1];
    }

    private boolean isNotValidRotation(int z) {
        return z != 0 && z != 90 && z != 180 && z != 270;
    }

    private JSONArray getJSONArray(String key) {
        if (!data.has(key)) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_KEY_NOT_FOUND.getValue("Key: " + key));
            return null;
        }

        return data.optJSONArray(key);
    }
}