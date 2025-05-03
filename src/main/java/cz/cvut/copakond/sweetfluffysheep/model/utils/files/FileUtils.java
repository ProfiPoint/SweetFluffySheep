package cz.cvut.copakond.sweetfluffysheep.model.utils.files;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ErrorMsgsEnum;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import java.util.logging.Logger;

/**
 * Utility class for file operations.
 * Provides methods to read, write, and copy files, as well as count files in a directory.
 */
public class FileUtils {
    private static final Logger logger = Logger.getLogger(FileUtils.class.getName());

    /**
     * Reads the content of a file and returns it as a String.
     *
     * @param filePath the path to the file
     * @return the content of the file as a String, or null if an error occurs
     */
    public static String readFile(String filePath) {
        try {
            return Files.readString(Path.of(filePath));
        } catch (IOException e) {
            logger.severe(ErrorMsgsEnum.LOAD_ERROR.getValue(filePath, e));
            return null;
        }
    }

    /**
     * Writes the given content to a file.
     *
     * @param filePath the path to the file
     * @param content   the content to write to the file
     * @return true if the operation was successful, false otherwise
     */
    public static boolean writeFile(String filePath, String content) {
        try {
            Files.writeString(Path.of(filePath), content);
            return true;
        } catch (IOException e) {
            logger.severe(ErrorMsgsEnum.SAVE_FILE.getValue(filePath, e));
            return false;
        }
    }

    /**
     * Copies a file from the source path to the destination path.
     *
     * @param sourcePath      the path to the source file
     * @param destinationPath the path to the destination file
     * @return true if the operation was successful, false otherwise
     */
    public static boolean copyFile(String sourcePath, String destinationPath) {
        try {
            Files.copy(Path.of(sourcePath), Path.of(destinationPath));
            return true;
        } catch (IOException e) {
            logger.severe(ErrorMsgsEnum.COPY_FILE.getValue(sourcePath + " -> " + destinationPath, e));
            return false;
        }
    }

    /**
     * Counts the number of files in a directory, excluding default files that start with "_".
     *
     * @param directoryPath the path to the directory
     * @return the number of files in the directory, or -1 if an error occurs
     */
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