package cz.cvut.copakond.pinkfluffyunicorn.model.utils.files;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ErrorMsgsEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.logging.Logger;

public class FileUtils {
    private static final Logger logger = Logger.getLogger(FileUtils.class.getName());

    public static String readFile(String filePath) {
        try {
            return Files.readString(Path.of(filePath));
        } catch (IOException e) {
            logger.severe(ErrorMsgsEnum.LOAD_ERROR.getValue(filePath, e));
            return null;
        }
    }

    public static boolean writeFile(String filePath, String content) {
        try {
            Files.writeString(Path.of(filePath), content);
            return true;
        } catch (IOException e) {
            logger.severe(ErrorMsgsEnum.SAVE_FILE.getValue(filePath, e));
            return false;
        }
    }

    public static boolean copyFile(String sourcePath, String destinationPath) {
        try {
            Files.copy(Path.of(sourcePath), Path.of(destinationPath));
            return true;
        } catch (IOException e) {
            logger.severe(ErrorMsgsEnum.COPY_FILE.getValue(sourcePath + " -> " + destinationPath, e));
            return false;
        }
    }

    public static int getNumberOfFilesInDirectory(String directoryPath) {
        try (var stream = Files.list(Path.of(directoryPath))) {
            return (int) stream
                    .filter(path -> !path.getFileName().toString().startsWith("_")) // don't count default files starting with "_"
                    .count();
        } catch (IOException e) {
            logger.severe(ErrorMsgsEnum.LOAD_ERROR.getValue("Path: " + directoryPath, e));
            return -1;
        }
    }
}