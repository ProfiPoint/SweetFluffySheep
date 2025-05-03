package cz.cvut.copakond.sweetfluffysheep.model.utils.enums;

/**
 * Enum representing all error messages used in the game.
 * This enum is used to define all error messages that can be displayed to the user.
 * It is used in the game to provide feedback to the user when an error occurs.
 */
public enum ErrorMsgsEnum {
    CUSTOM_ERROR(""),

    // Folders, files
    UNKNOWN_FOLDER("Folder does not exist."),
    FOLDER_CREATE_ERROR("Error creating folder at:"),
    FOLDER_EXISTS("Folder already exists at:"),
    LOAD_COMPLETED_LEVELS("Error loading completed levels."),
    LOAD_BUTTON_NOT_FOUND("Button not found for: "),
    SAVE_FILE("Error saving file."),
    COPY_FILE("Error copying file."),

    // Textures
    TEXTURE_MISSING("Texture missing or corrupted."),
    TEXTURE_MISSING_IS_MISSING("Missing texture of missing 'missing_texture.png' is missing or corrupted."),
    TEXTURE_UNKNOWN_NAME("Unknown texture name in classes"),

    // Physics
    PHYSICS_ALREADY_INIT("PHYSICS: Physics already initialized."),

    // Threads
    THREAD_INTERRUPTED("THREAD: Thread interrupted."),

    // Load / Save JSON
    SAVE_JSON_FILE("LEVEL SAVE: Error saving JSON file."),
    LOAD_ERROR("LEVEL LOAD: Error loading level."),
    LOAD_DEFAULT("LEVEL LOAD: Error loading default file _TEMPLATE.json"),
    LOAD_JSON_FILE("LEVEL LOAD: Error loading JSON file."),
    LOAD_JSON_PARSE("LEVEL LOAD: Error parsing JSON file."),
    LOAD_JSON_KEY_NOT_FOUND("LEVEL LOAD: Key not found in JSON file."),
    LOAD_VALUES4_LIMIT("LEVEL LOAD: Values out of range [x,y,itemId,duration], x, y must be lower than the map size and itemId must be a valid item id."),
    LOAD_VALUES3_LIMIT("LEVEL LOAD: Values out of range [x,y,orientation], x, y must be lower than the map size and orientation must be 0, 90, 180 or 270."),
    LOAD_VALUES3path_LIMIT("LEVEL LOAD: Values out of range [x,y,pathTextureId], x, y must be lower than the map size and pathTextureId must be a valid path texture id."),
    LOAD_LIST_IntIntOrientation("LEVEL LOAD: Invalid list of integers. Must be in format [int, int, orientation]."),
    LOAD_LIST_IntInt("LEVEL LOAD: Invalid list of integers. Must be in format [int, int]."),
    LOAD_EMPTY_LIST("LEVEL LOAD: The given list/dictionary cannot be empty."),
    LOAD_VALUE_OUT_OF_RANGE("LEVEL LOAD: Value out of range."),
    LOAD_INVALID_LEVEL_NAME("LEVEL LOAD: Invalid level name."),
    LOAD_PARSING_ERROR("LEVEL LOAD: Error parsing JSON file.");

    private final String message;

    /**
     * Constructor for ErrorMsgsEnum.
     *
     * @param message The error message associated with the enum constant.
     */
    ErrorMsgsEnum(String message) {
        this.message = message;
    }

    /**
     * Returns the error message.
     *
     * @return The formated error message.
     */
    public String getValue() {
        return message + "\n\n";
    }

    /**
     * Returns the error message with additional information.
     *
     * @param info Additional information to be included in the error message.
     * @return The formatted error message.
     */
    public String getValue(String info) {
        return message + " (" + info + ")\n\n";
    }

    /**
     * Returns the error message with additional information and an exception.
     *
     * @param info Additional information to be included in the error message.
     * @param error The exception to be included in the error message.
     * @return The formatted error message.
     */
    public String getValue(String info, Exception error) {
        return message + " (" + info + ")\n" + error + "\n\n";
    }
}
