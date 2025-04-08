package cz.cvut.copakond.pinkfluffyunicorn.model.data;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.ErrorMsgsEnum;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FolderUtils {
    static String levelsPath;
    static String profilesPath;

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
            ErrorMsgsEnum.UNKNOWN_FOLDER.getValue("Path: " + path);
        }

        return folders;
    }

    public static boolean createFolder(String path) {
        File folder = new File(path);

        if (!folder.exists()) {
            boolean success = folder.mkdirs();
            if (success) {
                System.out.println("Folder created at: " + path);
            } else {
                System.out.println("Failed to create folder at: " + path);
            }
        } else {
            System.out.println("Folder already exists at: " + path);
        }

        return folder.exists();
    }
}
