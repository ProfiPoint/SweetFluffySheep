package cz.cvut.copakond.sweetfluffysheep.model.utils.game;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.ErrorMsgsEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.FileUtils;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.FolderUtils;

import java.util.logging.Logger;

public class ProfileManager {
    private static final Logger logger = Logger.getLogger(ProfileManager.class.getName());

    protected static String currentProfile = "";
    protected static String profileFolderPath = "";

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
        if (!result) {
            return false;
        }

        result = FileUtils.copyFile(profileFolderPath + "/_TEMPLATE.json", profileFolderPath + "/" + profileName +  "/" + "_DATA.json");
        if (!result) {
            return false;
        }

        result = FileUtils.copyFile(profileFolderPath + "/_TEMPLATE2.json", profileFolderPath + "/" + profileName + "/" + "_SETTINGS.json");
        if (!result) {
            return false;
        }

        currentProfile = profileName;
        return true;
    }


    public static void switchProfile(String profileName) {
        currentProfile = profileName;
        if (!FileUtils.writeFile(profileFolderPath + "/_CURRENT.txt", profileName)) {
            logger.severe(ErrorMsgsEnum.SAVE_FILE.getValue(profileFolderPath + "/_CURRENT.txt"));
        }
    }
}