package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

import java.io.IOException;

public enum ErrorMsgsEnum {
    CUSTOM_ERROR(""),

    UNKNOWN_FOLDER("Folder does not exist."),
    FOLDER_CREATE_ERROR("Error creating folder at:"),
    FOLDER_EXISTS("Folder already exists at:"),
    LOAD_COMPLETED_LEVELS("Error loading completed levels."),

    TEXTURE_MISSING("Texture missing or corrupted."),
    TEXTURE_MISSING_IS_MISSING("Missing texture of missing 'missing_texture.png' is missing or corrupted."),
    TEXTURE_UNKNOWN_NAME("Unknown texture name in classes"),
    TEXTURE_OUT_OF_INDEX("Texture out of index."),

    PHISICS_ALREADY_INIT("PHISICS: Phisics already initialized."),

    LOAD_ERROR("LEVEL LOAD: Error loading level."),
    SAVE_JSON_FILE("LEVEL SAVE: Error saving JSON file."),
    LOAD_DEFAULT("LEVEL LOAD: Error loading default file _TEMPLATE.json"),
    LOAD_JSON_FILE("LEVEL LOAD: Error loading JSON file."),
    LOAD_JSON_PARSE("LEVEL LOAD: Error parsing JSON file."),
    LOAD_JSON_KEY_NOT_FOUND("LEVEL LOAD: Key not found in JSON file."),
    LOAD_VALUES4_LIMIT("LEVEL LOAD: Values out of range [x,y,itemId,duration], x, y must be lower than the map size and itemId must be a valid item id."),
    LOAD_VALUES3_LIMIT("LEVEL LOAD: Values out of range [x,y,orientation], x, y must be lower than the map size and orientation must be 0, 90, 180 or 270."),
    LOAD_VALUES3path_LIMIT("LEVEL LOAD: Values out of range [x,y,pathTextureId], x, y must be lower than the map size and pathTextureId must be a valid path texture id."),
    LOAD_VALUES2_LIMIT("LEVEL LOAD: Values out of range [x,y], x, y must be lower than the map size"),
    LOAD_LIST_IntIntOrientation("LEVEL LOAD: Invalid list of integers. Must be in format [int, int, orientation]."),
    LOAD_LIST_IntInt("LEVEL LOAD: Invalid list of integers. Must be in format [int, int]."),
    LOAD_EMPTY_LIST("LEVEL LOAD: The given list/dictionary cannot be empty."),
    LOAD_VALUE_OUT_OF_RANGE("LEVEL LOAD: Value out of range."),
    LOAD_INVALID_LEVEL_NAME("LEVEL LOAD: Invalid level name."),
    LOAD_PARSING_ERROR("LEVEL LOAD: Error parsing JSON file."),

    LOAD_BUTTON_NOT_FOUND("Button not found for: "),;

    private final String value;

    ErrorMsgsEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value + "\n\n";
    }

    public String getValue(String info) {
        String msg = value + " (" + info + ")\n\n";
        System.err.println(msg);
        return msg;
    }

    public String getValue(String info, Exception error) {
        String msg = value + " (" + info + ")\n" + error.toString() + "\n\n";
        System.err.println(msg);
        return msg;
    }
}
