package cz.cvut.copakond.pinkfluffyunicorn.model.utils.files;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.ProfileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelStatusUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;

public class InitClasses {
    public InitClasses(String levelsPath, String profilesPath) {
        ProfileManager.setProfileFolderPath(profilesPath);
        FolderUtils.setLevelsPath(levelsPath);
        FolderUtils.setProfilesPath(profilesPath);
        LevelStatusUtils.setLevelPath(levelsPath);
        LevelStatusUtils.setProfilesPath(profilesPath);
        Level.setLevelPath(levelsPath);
        Level.setProfilesPath(profilesPath);
    }
}
