package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import javax.inject.Provider;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static de.uniks.pioneers.Constants.SETTINGS_SCREEN_TITLE;


public class SettingsScreenController implements Controller, Initializable {

    @FXML public Button leaveButton;

    @FXML public RadioButton lightMode_RadioButton;

    @FXML public RadioButton darkMode_RadioButton;

    @FXML public ChoiceBox musicChoiceBox;

    private final App app;

    private final Provider<IngameScreenController> ingameScreenControllerProvider;

    @Inject
    public SettingsScreenController(App app, Provider<IngameScreenController> ingameScreenControllerProvider){
        this.app = app;
        this.ingameScreenControllerProvider = ingameScreenControllerProvider;
    }

    @Override
    public void init() {
        app.getStage().setTitle(SETTINGS_SCREEN_TITLE);
    }

    @Override
    public void stop() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/SettingsScreen.fxml"));
        loader.setControllerFactory(c->this);
        final Parent settingsView;
        try {
            settingsView =  loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return settingsView;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (lightMode_RadioButton.isSelected()){
            app.getStage().getScene().getStylesheets().clear();
        }
        if(darkMode_RadioButton.isSelected()){
            app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
        }
    }

    public void setApperenceMode(){

    }

    public void leave(){
        this.app.show(ingameScreenControllerProvider.get());
    }
}
