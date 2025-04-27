package cz.cvut.copakond.pinkfluffyunicorn.model.utils.files;

import cz.cvut.copakond.pinkfluffyunicorn.Launcher;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.TextureListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.ProfileManager;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.levels.LevelStatusUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.world.Level;
import cz.cvut.copakond.pinkfluffyunicorn.view.frames.LevelEditorFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.frames.MenuFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.AppViewManager;

import java.util.logging.Logger;

public class InitClasses {
    private static final Logger logger = Logger.getLogger(InitClasses.class.getName());
    
    public InitClasses(String texturesPath, String levelsPath, String profilesPath, String soundsPath) {
        LevelEditorFrame.setTexturePath(texturesPath);
        TextureListEnum.setLevelsPath(texturesPath);
        TextureManager.setTexturesPath(texturesPath);
        SoundListEnum.setSoundPath(soundsPath);
        FolderUtils.setLevelsPath(levelsPath);
        ProfileManager.setProfileFolderPath(profilesPath);
        LevelStatusUtils.setLevelPath(levelsPath);
        Level.setLevelPath(levelsPath);
        AppViewManager.setProfilesPath(profilesPath);
        FolderUtils.setProfilesPath(profilesPath);
        LevelStatusUtils.setProfilesPath(profilesPath);
        Level.setProfilesPath(profilesPath);
    }
}
