package cz.cvut.copakond.pinkfluffyunicorn.model.profile;

import cz.cvut.copakond.pinkfluffyunicorn.model.data.FileUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.data.FolderUtils;

import java.io.File;

public class ProfileManager {
    static String currentProfile = "";
    static String profileFolderPath = "";

    public static void setProfileFolderPath(String path) {
        profileFolderPath = path;
    }

    public static String getProfileFolderPath() {
        return profileFolderPath;
    }

    public static String getCurrentProfile() {
        return currentProfile;
    }

    public static boolean addNewProfile(String profileName) {
        boolean result = FolderUtils.createFolder(profileFolderPath + "/" + profileName);
        if (!result) {return false;}
        result = FileUtils.copyFile(profileFolderPath + "/_TEMPLATE.json", profileFolderPath + "/" + profileName +
                "/" + "_DATA.json");
        if (!result) {return false;}
        currentProfile = profileName;
        return true;
    }

    public static void switchProfile(String profileName) {
        currentProfile = profileName;
        FileUtils.writeFile(profileFolderPath + "/_CURRENT.txt", profileName);
    }
}
