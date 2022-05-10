package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;

import static de.uniks.pioneers.Constants.RULES_SCREEN_TITLE;

public class RulesScreenController implements Controller {

    Stage stage;

    @Inject
    public RulesScreenController(Stage stage) {
        this.stage = stage;
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/RulesScreen.fxml"));
        loader.setControllerFactory(c->this);
        final Parent parent;
        try {
            parent =  loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return parent;
    }

    @Override
    public void init() {
        this.stage.setTitle(RULES_SCREEN_TITLE);
        this.stage.setScene(new Scene(render()));
        stage.show();
    }

    @Override
    public void stop() {
    }
}
