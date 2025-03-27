package cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums;

public enum ErrorMsgsEnum {
    SAVE_JSON_FILE("Error saving JSON file."),
    LOAD_JSON_FILE("Error loading JSON file."),
    LOAD_JSON_PARSE("Error parsing JSON file."),
    LOAD_JSON_KEY_NOT_FOUND("Key not found in JSON file."),
    LOAD_ORIENTATION("Invalid orientation. Must be int 0 (left), 90 (left), 180 (down), 270 (right)."),
    LOAD_LIST_IntIntOrientation("Invalid list of integers. Must be in format [int, int, orientation]."),
    LOAD_LIST_IntInt("Invalid list of integers. Must be in format [int, int]."),
    LOAD_EXPECTED_INT("Expected integer."),
    LOAD_EXPECTED_LIST("Expected list."),
    LOAD_WRONG_NUMBER_OF_ITEMS("Wrong number of items in list."),;

    private final String value;

    ErrorMsgsEnum(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

    public String getValue(String info) {
        return value + " (" + info + ")";
    }
}
