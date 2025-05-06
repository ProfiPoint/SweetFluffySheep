package cz.cvut.copakond.sweetfluffysheep.model.utils.json;

import java.io.IOException;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ErrorMsgsEnum;
import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.List;
import java.util.logging.Logger;

/**
 * JsonFileManager is a utility class for reading and writing JSON files.
 * It provides methods to read JSON data from a file, write JSON data to a file,
 * and manage specific JSON structures related to game profiles and settings.
 */
public class JsonFileManager {
    private static final Logger logger = Logger.getLogger(JsonFileManager.class.getName());

    /**
     * Reads JSON data from a file and returns it as a JSONObject.
     *
     * @param filePath the path to the JSON file
     * @return the JSONObject containing the JSON data, or null if an error occurs
     */
    public static JSONObject readJsonFromFile(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONObject(content);
        } catch (IOException e) {
           logger.severe(ErrorMsgsEnum.LOAD_JSON_FILE.getValue(filePath, e));
        } catch (JSONException e) {
            logger.severe(ErrorMsgsEnum.LOAD_JSON_PARSE.getValue(filePath, e));
        }
        return null;
    }

    /**
     * Writes a JSONObject to a file.
     *
     * @param filePath the path to the JSON file
     * @param data     the JSONObject to write
     * @return true if the write operation is successful, false otherwise
     */
    public static boolean writeJsonToFile(String filePath, JSONObject data) {
        try {
            String jsonString = formatJson(data, 0); // format the JSON with custom list formatting
            Files.write(Paths.get(filePath), jsonString.getBytes());
            return true;
        } catch (IOException e) {
            logger.severe(ErrorMsgsEnum.SAVE_JSON_FILE.getValue(filePath, e));
        }
        return false;
    }

    /**
     * Reads profile data from a JSON file and returns it as a list of lists of integers.
     *
     * @param filePath the path to the JSON file
     * @return a list of lists of integers representing the profile data
     */
    public static List<List<Integer>> getProfileLFromJsonFile(String filePath) {
        List<List<Integer>> profileData = List.of(List.of(), List.of());
        JSONObject jsonObject = readJsonFromFile(filePath);

        if (jsonObject != null) {
            JSONArray completedStory = jsonObject.optJSONArray("completedStory");
            JSONArray completedCustom = jsonObject.optJSONArray("completedCustom");

            if (completedStory != null && completedCustom != null) {
                profileData = List.of(
                        completedStory.toList().stream().map(Object::toString).map(Integer::valueOf).sorted().toList(),
                        completedCustom.toList().stream().map(Object::toString).map(Integer::valueOf).sorted().toList()
                );
            }
        }
        return profileData;
    }

    /**
     * Saves profile data to a JSON file.
     *
     * @param filePath the path to the JSON file
     * @param data     the list of lists of integers representing the profile data
     * @return true if the save operation is successful, false otherwise
     */
    public static boolean saveProfileLToJsonFile(String filePath, List<List<Integer>> data) {
        JSONObject jsonObject = new JSONObject();
        JSONArray completedStory = new JSONArray(data.get(0));
        JSONArray completedCustom = new JSONArray(data.get(1));
        jsonObject.put("completedStory", completedStory);
        jsonObject.put("completedCustom", completedCustom);
        return writeJsonToFile(filePath, jsonObject);
    }

    /**
     * Reads settings from a JSON file and returns them as a list of integers.
     *
     * @param filePath the path to the JSON file
     * @return a list of integers representing the settings or null if an error occurs
     */
    public static List<Integer> readSettingsFromJson(String filePath) {
        JSONObject jsonObject = readJsonFromFile(filePath);

        if (jsonObject != null) {
            int musicVolume = jsonObject.optInt("music", 50);
            int sfxVolume = jsonObject.optInt("sfx", 50);
            int fps = jsonObject.optInt("fps", 60);
            int fullScreen = jsonObject.optBoolean("fullScreen", false) ? 1 : 0;

            return List.of(musicVolume, sfxVolume, fps, fullScreen);
        }
        return null;
    }

    /**
     * Writes user settings to a JSON file.
     *
     * @param filePath    the path to the JSON file
     * @param musicVolume  the music volume setting
     * @param sfxVolume    the sound effects volume setting
     * @param fps          the frames per second setting
     * @param fullScreen   the full-screen setting
     * @return true if the write operation is successful, false otherwise
     */
    public static boolean writeSettingsToJson(String filePath, int musicVolume, int sfxVolume, int fps, boolean fullScreen) {
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("music", musicVolume);
        jsonObject.put("sfx", sfxVolume);
        jsonObject.put("fps", fps);
        jsonObject.put("fullScreen", fullScreen);
        return writeJsonToFile(filePath, jsonObject);
    }

    /**
     * Formats a JSONObject or JSONArray into a string with custom formatting.
     * Generated by GitHub Copilot.
     * @param obj    the JSON object or array to format
     * @param indent the current indentation level
     * @return a formatted string representation of the JSON object or array
     */
    private static String formatJson(Object obj, int indent) {
        StringBuilder sb = new StringBuilder();
        String indentStr = "  ".repeat(indent);  // spaces for indentation

        // check if the object is null
        switch (obj) {
            case JSONObject json -> {
                sb.append("{\n");
                int count = 0;
                // iterate through the keys and values of the JSON object
                for (String key : json.keySet()) {
                    if (count++ > 0) sb.append(",\n");
                    sb.append(indentStr).append("  \"").append(key).append("\": ");
                    sb.append(formatJson(json.get(key), indent + 1));
                }
                sb.append("\n").append(indentStr).append("}");
            }
            case JSONArray arr -> {

                // check if it's a list of lists that should be in one line
                boolean isInnerList = !arr.isEmpty() && arr.get(0) instanceof JSONArray;

                // check if the inner list is empty
                if (isInnerList) {
                    sb.append("[\n");
                    for (int i = 0; i < arr.length(); i++) {
                        if (i > 0) sb.append(",\n");
                        sb.append(indentStr).append("  ").append(arr.get(i).toString());
                    }
                    sb.append("\n").append(indentStr).append("]");
                } else {
                    sb.append(arr);
                }
            }
            // check if the object is a string
            case String ignored -> sb.append("\"").append(obj).append("\"");
            case null, default -> sb.append(obj);
        }

        return sb.toString();
    }
}
