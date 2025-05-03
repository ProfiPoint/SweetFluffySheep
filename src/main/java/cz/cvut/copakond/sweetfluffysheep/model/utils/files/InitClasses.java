package cz.cvut.copakond.sweetfluffysheep.model.utils.files;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.TextureListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.ProfileManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.levels.LevelStatusUtils;
import cz.cvut.copakond.sweetfluffysheep.model.world.Level;
import cz.cvut.copakond.sweetfluffysheep.view.frames.LevelEditorFrame;
import cz.cvut.copakond.sweetfluffysheep.view.utils.AppViewManager;

/**
 * This class initializes the paths for various resources used in the application.
 * It sets the paths for textures, levels, profiles, and sounds.
 * Basically, it does connect Model and View classes by giving Model classes the paths to the resources
 */
public class InitClasses {
    public InitClasses(String texturesPath, String levelsPath, String profilesPath, String soundsPath) {
        LevelEditorFrame.setTexturePath(texturesPath);
        TextureListEnum.setLevelsPath(texturesPath);
        TextureManager.setTexturesPath(texturesPath);
        AppViewManager.setTexturesPath(texturesPath);
        SoundListEnum.setSoundPath(soundsPath);
        FolderUtils.setLevelsPath(levelsPath);
        LevelStatusUtils.setLevelPath(levelsPath);
        Level.setLevelPath(levelsPath);
        ProfileManager.setProfileFolderPath(profilesPath);
        AppViewManager.setProfilesPath(profilesPath);
        FolderUtils.setProfilesPath(profilesPath);
        LevelStatusUtils.setProfilesPath(profilesPath);
        Level.setProfilesPath(profilesPath);
    }
}
