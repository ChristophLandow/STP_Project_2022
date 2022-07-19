package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.services.StylesService;
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
    private final StylesService stylesService;
    private Stage stage;

    @Inject
    public RulesScreenController(App app, StylesService stylesService) {
        this.app = app;
        this.stylesService = stylesService;
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
            String localStyle = "/de/uniks/pioneers/styles/RulesScreen.css";
            String localStyleDark = "/de/uniks/pioneers/styles/DarkMode_RulesScreen.css";
            stylesService.setStyleSheets(this.stage.getScene().getStylesheets(), localStyle, localStyleDark);
            this.stage.show();
        } else {
            String localStyle = "/de/uniks/pioneers/styles/RulesScreen.css";
            String localStyleDark = "/de/uniks/pioneers/styles/DarkMode_RulesScreen.css";
            stylesService.setStyleSheets(this.stage.getScene().getStylesheets(), localStyle, localStyleDark);
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
