package cz.cvut.copakond.pinkfluffyunicorn.model.utils.files;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.TextureListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.ProfileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelStatusUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.Level;

public class InitClasses {
    public InitClasses(String texturesPath, String levelsPath, String profilesPath) {
        TextureListEnum.setLevelsPath(texturesPath);
        TextureManager.setTexturesPath(texturesPath);
        ProfileManager.setProfileFolderPath(profilesPath);
        FolderUtils.setLevelsPath(levelsPath);
        FolderUtils.setProfilesPath(profilesPath);
        LevelStatusUtils.setLevelPath(levelsPath);
        LevelStatusUtils.setProfilesPath(profilesPath);
        Level.setLevelPath(levelsPath);
        Level.setProfilesPath(profilesPath);
    }
}
