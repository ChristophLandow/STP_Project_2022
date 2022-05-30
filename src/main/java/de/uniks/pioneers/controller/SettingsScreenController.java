package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import javax.inject.Provider;

import java.io.IOException;

import static de.uniks.pioneers.Constants.SETTINGS_SCREEN_TITLE;


public class SettingsScreenController implements Controller{

    @FXML
    public Button leaveButton;

    @FXML
    public RadioButton lightMode_RadioButton;

    @FXML
    public RadioButton darkMode_RadioButton;

    @FXML
    public ChoiceBox musicChoiceBox;

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

        //System.out.println(radioButtongroup.getSelectedToggle().toString());
        return settingsView;
    }

    public void setApperenceMode(ActionEvent event){
        if(darkMode_RadioButton.isSelected()){
            System.out.println("Hello");
            darkMode();
        }
    }

    public void darkMode(){
        app.getStage().getScene().setFill(Color.rgb(35, 39, 42));
    }

    public void leave(){
        app.show(ingameScreenControllerProvider.get());
    }
}
