package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.services.PrefService;
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
    private final App app;
    private Stage stage;

    @Inject
    PrefService prefService;

    @Inject
    public RulesScreenController(App app) {
        this.app = app;
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
            if(prefService.getDarkModeState()){
                this.stage.getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/RulesScreen.css")));
                this.stage.getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_RulesScreen.css");
            } else {
                this.stage.getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_RulesScreen.css")));
                this.stage.getScene().getStylesheets().add( "/de/uniks/pioneers/styles/RulesScreen.css");
            }
            this.stage.show();
        } else {
            if(prefService.getDarkModeState()){
                stage.getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_RulesScreen.css");
            } else {
                stage.getScene().getStylesheets().add( "/de/uniks/pioneers/styles/RulesScreen.css");
            }
            // bring to front if already open
            this.stage.show();
            this.stage.toFront();
        }
        app.setIcons(stage);
    }

    @Override
    public void stop() {
    }

    public App getApp() {
        return this.app;
    }
}
