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
        actualEventHandler = ke -> {
            if (keyComb.match(ke)) {
                switch (kind) {
                    case TRADE -> fireTradeHotkey();
                    case END -> fireEndHotkey();
                    case RULES -> fireRulesHotkey();
                    case SETTINGS -> fireSettingsHotkey();
                    case BUILDIGLOO -> fireBuildIgluHotkey();
                    case BUILDSTREET -> fireBuildStreetHotkey();
                    case UPGRADEIGLOO -> fireUpgradeIgluToStationHotkey();
                }
                ke.consume();
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
    }

    private void fireSettingsHotkey(){
        ingameController.toSettings();
    }

    private void fireRulesHotkey(){
        ingameController.toRules();
    }

    private void fireBuildIgluHotkey(){
        Event rightClick = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY,
               1, false, false, false, false, false, false, false, false, true, false, null);
        ingameController.settlementFrame.fireEvent(rightClick);
    }

    private void fireUpgradeIgluToStationHotkey(){
        Event rightClick = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY,
               1, false, false, false, false, false, false, false, false, true, false, null);
        ingameController.cityFrame.fireEvent(rightClick);
    }

    private void fireBuildStreetHotkey(){
        Event rightClick = new MouseEvent(MouseEvent.MOUSE_CLICKED, 0, 0, 0, 0, MouseButton.PRIMARY,
               1, false, false, false, false, false, false, false, false, true, false, null);
        ingameController.roadFrame.fireEvent(rightClick);
    }


}