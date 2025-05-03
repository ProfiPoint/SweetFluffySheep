package cz.cvut.copakond.sweetfluffysheep.model.utils.files;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ErrorMsgsEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;

/**
 * Utility class for managing folders in the application.
 * Provides methods to create folders and retrieve folder names.
 */
public class FolderUtils {
    private static final Logger logger = Logger.getLogger(FolderUtils.class.getName());
    
    private static String levelsPath;
    private static String profilesPath;

    public static void setLevelsPath(String path) {
        levelsPath = path;
    }

    public static void setProfilesPath(String path) {
        profilesPath = path;
    }

    public static String getLevelsPath() {
        return levelsPath;
    }

    public static String getProfilesPath() {
        return profilesPath;
    }

    /**
     * Retrieves all folder names in the specified path.
     *
     * @param path the path to search for folders
     * @return a list of folder names
     */
    public static List<String> getAllFolders(String path) {
        List<String> folders = new ArrayList<>();
        File directory = new File(path);

        if (directory.exists() && directory.isDirectory()) {
            File[] files = directory.listFiles();

            if (files != null) {
                for (File file : files) {
                    if (file.isDirectory()) {
                        folders.add(file.getName());
                    }
                }
            }
        } else {
            logger.severe(ErrorMsgsEnum.UNKNOWN_FOLDER.getValue("Path: " + path));
        }

        return folders;
    }

    /**
     * Creates a folder at the specified path.
     *
     * @param path the path where the folder should be created
     * @return true if the folder was created successfully, false otherwise
     */
    public static boolean createFolder(String path) {
        File folder = new File(path);

        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (success) {
                logger.info("Folder created at: " + path);
            } else {
                logger.severe(ErrorMsgsEnum.FOLDER_CREATE_ERROR.getValue(path));
            }
        } else {
            logger.severe(ErrorMsgsEnum.FOLDER_EXISTS.getValue(path));
        }

        return folder.exists();
    }
}
