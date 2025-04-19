package cz.cvut.copakond.pinkfluffyunicorn.model.utils.files;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.TextureListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.ProfileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelStatusUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;
import cz.cvut.copakond.pinkfluffyunicorn.view.frames.LevelEditorFrame;

public class InitClasses {
    public InitClasses(String texturesPath, String levelsPath, String profilesPath, String soundsPath) {
        LevelEditorFrame.setTexturePath(texturesPath);
        TextureListEnum.setLevelsPath(texturesPath);
        TextureManager.setTexturesPath(texturesPath);
        SoundListEnum.setSoundPath(soundsPath);
        ProfileManager.setProfileFolderPath(profilesPath);
        FolderUtils.setLevelsPath(levelsPath);
        FolderUtils.setProfilesPath(profilesPath);
        LevelStatusUtils.setLevelPath(levelsPath);
        LevelStatusUtils.setProfilesPath(profilesPath);
        Level.setLevelPath(levelsPath);
        Level.setProfilesPath(profilesPath);
    }
}
