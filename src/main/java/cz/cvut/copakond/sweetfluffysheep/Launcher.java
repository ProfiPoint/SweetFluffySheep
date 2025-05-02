package cz.cvut.copakond.sweetfluffysheep;

import cz.cvut.copakond.sweetfluffysheep.model.utils.files.FileUtils;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.InitClasses;
import cz.cvut.copakond.sweetfluffysheep.model.utils.game.ProfileManager;
import cz.cvut.copakond.sweetfluffysheep.model.utils.logging.LoggerConfig;
import cz.cvut.copakond.sweetfluffysheep.view.utils.AppViewManager;
import cz.cvut.copakond.sweetfluffysheep.view.frames.MenuFrame;
import cz.cvut.copakond.sweetfluffysheep.view.frames.ProfileFrame;
import javafx.application.Application;
import javafx.stage.Stage;

import java.util.logging.Logger;

public class Launcher extends Application {
    private static final Logger logger = Logger.getLogger(Launcher.class.getName());

    @Override
    public void start(Stage primaryStage) {
        // init all directories to all file managers
        String texturesPath = "resources/textures";
        String levelsPath = "resources/datasaves/levels";
        String profilesPath = "resources/datasaves/profiles";
        String soundsPath = "resources/sounds";
        new InitClasses(texturesPath, levelsPath, profilesPath, soundsPath);

        AppViewManager.init(primaryStage);

        String currentProfile = FileUtils.readFile(profilesPath + "/_CURRENT.txt");

        if (currentProfile == null) {
            currentProfile = "";
        }

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
        boolean loggerEnabled = false;

        for (String arg : args) {
            if (arg.equals("-logger") || arg.equals("--logger") || arg.equals("logger=true")) {
                loggerEnabled = true;
                logger.info("Logger enabled");
                break;
            }
        }

        LoggerConfig.configureLoggers(loggerEnabled);
        launch(args);
    }
}
