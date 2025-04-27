package cz.cvut.copakond.pinkfluffyunicorn;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.FileUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.InitClasses;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.ProfileManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.AppViewManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.frames.MenuFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.frames.ProfileFrame;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class Launcher extends Application {
    private static final Logger logger = Logger.getLogger(Launcher.class.getName());

    @Override
    public void start(Stage primaryStage) {
        AppViewManager.init(primaryStage);

        // init all directories to all file managers
        String texturesPath = "resources/textures";
        String levelsPath = "resources/datasaves/levels";
        String profilesPath = "resources/datasaves/profiles";
        String soundsPath = "resources/sounds";
        new InitClasses(texturesPath, levelsPath, profilesPath, soundsPath);


        String currentProfile = FileUtils.readFile(profilesPath + "/_CURRENT.txt");
        if (!currentProfile.isBlank()) {
            ProfileManager.switchProfile(currentProfile);
            AppViewManager.initSettings(profilesPath);
        }

        if (currentProfile.isBlank()) {
            AppViewManager.get().switchTo(new ProfileFrame());
        } else {
            AppViewManager.get().switchTo(new MenuFrame());
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
