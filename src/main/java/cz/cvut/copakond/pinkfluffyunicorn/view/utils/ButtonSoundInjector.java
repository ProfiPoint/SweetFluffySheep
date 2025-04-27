package cz.cvut.copakond.pinkfluffyunicorn.view.utils;

import cz.cvut.copakond.pinkfluffyunicorn.Launcher;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.pinkfluffyunicorn.model.utils.files.SoundManager;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;

import java.util.logging.Logger;

public class ButtonSoundInjector {
    private static final Logger logger = Logger.getLogger(ButtonSoundInjector.class.getName());

    public static void injectToAllButtons(Parent root) {
        for (Node node : root.lookupAll(".button")) {
            if (node instanceof Button button) {
                attachSound(button);
            }
        }
    }

    private static void attachSound(Button button) {
        // handle it only if the button is not already injected, avoid multiple handlers
        button.addEventHandler(javafx.event.ActionEvent.ACTION, e -> {
            SoundManager.playSound(SoundListEnum.MOUSE_CLICK);
        });
    }
}
