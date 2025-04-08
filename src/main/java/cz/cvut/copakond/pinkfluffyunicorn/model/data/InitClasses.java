package cz.cvut.copakond.pinkfluffyunicorn.model.data;

import cz.cvut.copakond.pinkfluffyunicorn.model.profile.ProfileManager;
import javafx.stage.Stage;

public class InitClasses {
    public InitClasses(String levelsPath, String profilesPath) {
        ProfileManager.setProfileFolderPath(profilesPath);
        FolderUtils.setLevelsPath(levelsPath);
        FolderUtils.setProfilesPath(profilesPath);
    }
}
