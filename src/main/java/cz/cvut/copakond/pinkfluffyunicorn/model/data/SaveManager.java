package cz.cvut.copakond.pinkfluffyunicorn.model.data;

import java.io.IOException;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ErrorMsgsEnum;
import org.json.JSONObject;
import org.json.JSONException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class SaveManager {
    public static JSONObject readJsonFromFile(String filePath) {
        try {
            String content = new String(Files.readAllBytes(Paths.get(filePath)));
            return new JSONObject(content);
        } catch (IOException e) {
            System.err.println(ErrorMsgsEnum.LOAD_JSON_FILE.getValue(filePath));
        } catch (JSONException e) {
            System.err.println(ErrorMsgsEnum.LOAD_JSON_PARSE.getValue(filePath));
        }
        return null;
    }

    public static boolean writeJsonToFile(String filePath, String json) {
        try {
            Files.write(Paths.get(filePath), json.getBytes());
            return true;
        } catch (IOException e) {
            System.err.println(ErrorMsgsEnum.SAVE_JSON_FILE.getValue(filePath));
        }
        return false;
    }
}
