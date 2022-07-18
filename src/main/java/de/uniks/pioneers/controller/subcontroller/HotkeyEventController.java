package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.IngameScreenController;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.*;

import static de.uniks.pioneers.Constants.*;

public class HotkeyEventController {

    private final Scene scene;
    private final IngameScreenController ingameController;
    private EventHandler<KeyEvent> actualEventHandler;

    public HotkeyEventController(Scene scene, IngameScreenController ingameController) {
        this.scene = scene;
        this.ingameController = ingameController;
    }

    public void stop(){
        scene.removeEventFilter(KeyEvent.KEY_PRESSED, actualEventHandler);
    }

    public void setHotkey(KeyCode letter, String controllOrAlt, String kind ) {
        KeyCombination keyComb;
        if(controllOrAlt.equals(STRG)){
            keyComb = new KeyCodeCombination(letter, KeyCombination.CONTROL_DOWN);
        } else {
            keyComb = new KeyCodeCombination(letter, KeyCombination.ALT_DOWN);
        }
        actualEventHandler = new EventHandler<>() {
            public void handle(KeyEvent ke) {
                if (keyComb.match(ke)) {
                    switch (kind) {
                        case TRADE -> fireTradeHotkey();
                        case END -> fireEndHotkey();
                        case RULES -> fireRulesHotkey();
                        case SETTINGS -> fireSettingsHotkey();
                    }
                    ke.consume();
                }
            }
        };
        scene.addEventFilter(KeyEvent.KEY_PRESSED, actualEventHandler);
    }

    private void fireTradeHotkey(){
        ingameController.openTradePopUp();
    }

    private void fireEndHotkey(){
        Event rightClick = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY,
               1, false, false, false, false, false, false, false, false, true, false, null);
        ingameController.turnPane.fireEvent(rightClick);
        System.out.println("toDo");
    }

    private void fireSettingsHotkey(){
        ingameController.toSettings();
    }

    private void fireRulesHotkey(){
        ingameController.toRules();
    }
}