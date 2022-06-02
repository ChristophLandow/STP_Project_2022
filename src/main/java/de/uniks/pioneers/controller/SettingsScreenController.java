package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.RadioButton;
import javafx.stage.Stage;

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

    @FXML public ChoiceBox<String> musicChoiceBox;

    private final App app;

    private Stage stage;

    private final String[] songList = {"Hardbass", "Ambient"};

    private final Provider<IngameScreenController> ingameScreenControllerProvider;

    @Inject
    public SettingsScreenController(App app, Provider<IngameScreenController> ingameScreenControllerProvider){
        this.app = app;
        this.ingameScreenControllerProvider = ingameScreenControllerProvider;
    }

    @Override
    public void init() {
        // check if rules screen is not open yet
        if (this.stage == null) {
            this.stage = new Stage();
            this.stage.setScene(new Scene(render()));
            this.stage.setTitle(SETTINGS_SCREEN_TITLE);
            this.stage.show();
        } else {
            // bring to front if already open
            this.stage.show();
            this.stage.toFront();
        }
        app.setIcons(stage);
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
        musicChoiceBox.getItems().addAll(songList);
        musicChoiceBox.setOnAction(this::setMusic);
    }

    private void setMusic(ActionEvent actionEvent) {
    }

    public void setApperenceMode(){
        if (lightMode_RadioButton.isSelected()){
            IngameScreenController controller = ingameScreenControllerProvider.get();
            controller.getApp().getStage().getScene().getStylesheets().clear();
            stage.getScene().getStylesheets().clear();
        }
        if(darkMode_RadioButton.isSelected()){
            IngameScreenController controller = ingameScreenControllerProvider.get();
            controller.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
            stage.getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
        }

    }

    public void leave(){
        stage.close();
    }
}
