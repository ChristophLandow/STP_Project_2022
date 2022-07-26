package de.uniks.pioneers.services;

import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import javax.inject.Inject;
import javax.inject.Singleton;

@Singleton
public class EventHandlerService {

    @Inject
    public EventHandlerService() {}
    public void setEnterEventHandler(Node root, Button button) {
        root.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.ENTER)) {
                button.fire();
                event.consume();
            }
        });
    }

    public void setSpaceEventHandler(Node root, Button button) {
        root.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.SPACE)) {
                button.fire();
                event.consume();
            }
        });
    }
}
