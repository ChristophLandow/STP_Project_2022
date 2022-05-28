package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;

import javax.inject.Inject;

import java.io.IOException;

import static de.uniks.pioneers.Constants.INGAME_SCREEN_TITLE;
import static de.uniks.pioneers.Constants.SETTINGS_SCREEN_TITLE;


public class SettingsScreenController implements Controller{

    @FXML public Button leaveButton;

    @FXML public RadioButton lightMode_RadioButton;

    @FXML public RadioButton darkMode_RadioButton;

    @FXML public ChoiceBox musicChoiceBox;

    private final App app;

    @Inject
    public SettingsScreenController(App app){
        this.app = app;
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
}
