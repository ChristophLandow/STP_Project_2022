package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.IngameScreenController;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.*;

import javax.inject.Provider;

import static de.uniks.pioneers.Constants.*;

public class HotkeyEventController {

    private final Scene scene;
    private final Provider<IngameScreenController> ingameScreenControllerProvider;
    private EventHandler<KeyEvent> actualEventHandler;

    public HotkeyEventController(Scene scene, Provider<IngameScreenController> ingameScreenControllerProvider) {
        this.scene = scene;
        this.ingameScreenControllerProvider = ingameScreenControllerProvider;
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
                        case TRADE -> ingameScreenControllerProvider.get().openTradePopUp();
                        case END -> System.out.println("toDo");
                        case RULES -> ingameScreenControllerProvider.get().toRules();
                        case SETTINGS -> ingameScreenControllerProvider.get().toSettings();
                    }
                    ke.consume();
                }
            }
        };
        scene.addEventFilter(KeyEvent.KEY_PRESSED, actualEventHandler);
    }
}