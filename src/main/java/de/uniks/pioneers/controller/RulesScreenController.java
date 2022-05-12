package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;

import static de.uniks.pioneers.Constants.RULES_SCREEN_TITLE;

@Singleton
public class RulesScreenController implements Controller {

    Stage stage;

    @Inject
    public RulesScreenController() {
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
        // check if rules screen is not open yet
        if (this.stage == null) {
            this.stage = new Stage();
            this.stage.setScene(new Scene(render()));
            this.stage.setTitle(RULES_SCREEN_TITLE);
            this.stage.setX(100);
            this.stage.show();
        } else {
            // bring to front if already open
            this.stage.show();
            this.stage.toFront();
        }
    }

    @Override
    public void stop() {
    }
}