package cz.cvut.copakond.pinkfluffyunicorn.model.data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class FileUtils {
    public static String readFile(String filePath) {
        try {
            return Files.readString(Path.of(filePath));
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static boolean writeFile(String filePath, String content) {
        try {
            Files.writeString(Path.of(filePath), content);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean copyFile(String sourcePath, String destinationPath) {
        try {
            Files.copy(Path.of(sourcePath), Path.of(destinationPath));
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static int getNumberOfFilesInDirectory(String directoryPath) {
        try {
            return (int) Files.list(Path.of(directoryPath)).count() -1; // Exclude the "default" file
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        }
    }
}