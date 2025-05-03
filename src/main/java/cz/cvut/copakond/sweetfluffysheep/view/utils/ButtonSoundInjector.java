package cz.cvut.copakond.sweetfluffysheep.view.utils;

import cz.cvut.copakond.sweetfluffysheep.model.utils.enums.SoundListEnum;
import cz.cvut.copakond.sweetfluffysheep.model.utils.files.SoundManager;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;

/**
 * This class is responsible for injecting sound effects into all buttons in a given JavaFX Parent node.
 * It looks for all nodes with the CSS class "button" and attaches a sound effect to their action events.
 */
public class ButtonSoundInjector {

    /**
     * Injects sound effects into all buttons within the provided Parent node.
     *
     * @param root The Parent node containing buttons to inject sound effects into.
     */
    public static void injectToAllButtons(Parent root) {
        for (Node node : root.lookupAll(".button")) {
            if (node instanceof Button button) {
                attachSound(button);
            }
        }
    }

    /**
     * Attaches a sound effect to the action event of the provided button.
     *
     * @param button The button to attach the sound effect to.
     */
    private static void attachSound(Button button) {
        // avoid multiple handlers if already injected
        button.addEventHandler(javafx.event.ActionEvent.ACTION, e -> {
            SoundManager.playSound(SoundListEnum.MOUSE_CLICK);
        });
    }
}