package cz.cvut.copakond.pinkfluffyunicorn;

import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.FileUtils;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.InitClasses;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.game.ProfileManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.utils.AppViewManager;
import cz.cvut.copakond.pinkfluffyunicorn.view.frames.MenuFrame;
import cz.cvut.copakond.pinkfluffyunicorn.view.frames.ProfileFrame;
import javafx.application.Application;
import javafx.stage.Stage;

public class Launcher extends Application {
    @Override
    public void start(Stage primaryStage) {
        AppViewManager.init(primaryStage);

        String texturesPath = "resources/textures";
        String levelsPath = "resources/datasaves/levels";
        String profilesPath = "resources/datasaves/profiles";

        // init all directories to all file managers
        InitClasses initClasses = new InitClasses(texturesPath, levelsPath, profilesPath);

        String currentProfile = FileUtils.readFile(profilesPath + "/_CURRENT.txt");
        if (!currentProfile.isBlank()) {
            ProfileManager.switchProfile(currentProfile);
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
