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
import javax.inject.Singleton;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import static de.uniks.pioneers.Constants.SETTINGS_SCREEN_TITLE;

@Singleton
public class SettingsScreenController implements Controller, Initializable {

    @FXML public Button leaveButton;

    @FXML public RadioButton lightMode_RadioButton;

    @FXML public RadioButton darkMode_RadioButton;

    @FXML public ChoiceBox<String> musicChoiceBox;

    private final App app;

    private Stage stage;

    private final String[] songList = {"Hardbass", "Ambient"};

    private final Provider<IngameScreenController> ingameScreenControllerProvider;

    private final Provider<NewGameScreenLobbyController> newGameLobbyControllerProvider;

    private final Provider<EditProfileController> editProfileControllerProvider;

    private final Provider<ChatController> chatControllerProvider;

    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;

    private final Provider<LoginScreenController> loginScreenControllerProvider;

    private final Provider<RulesScreenController> rulesScreenControllerProvider;

    private boolean darkMode = false;

    @Inject
    public SettingsScreenController(App app, Provider<IngameScreenController> ingameScreenControllerProvider,
                                    Provider<NewGameScreenLobbyController> newGameLobbyControllerProvider,
                                    Provider<EditProfileController> editProfileControllerProvider,
                                    Provider<ChatController> chatControllerProvider,
                                    Provider<LobbyScreenController> lobbyScreenControllerProvider,
                                    Provider<LoginScreenController> loginScreenControllerProvider,
                                    Provider<RulesScreenController> rulesScreenControllerProvider){
        this.app = app;
        this.ingameScreenControllerProvider = ingameScreenControllerProvider;
        this.newGameLobbyControllerProvider = newGameLobbyControllerProvider;
        this.editProfileControllerProvider = editProfileControllerProvider;
        this.chatControllerProvider = chatControllerProvider;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.loginScreenControllerProvider = loginScreenControllerProvider;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
    }

    @Override
    public void init() {
        // check if settings screen is not open yet
        if (this.stage == null) {
            this.stage = new Stage();
            this.stage.setScene(new Scene(render()));
            this.stage.setTitle(SETTINGS_SCREEN_TITLE);
            if(darkMode){
                this.stage.getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
            }
            this.stage.show();
        } else {
            if(darkMode){
                this.stage.getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
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

    //The Darkmode should work if the SettingsScreen is open nearby AND without it. Here we handle only the nearby
    // part. Without the SettingsScreen it works via the setDarkMode Method.
    public void setApperenceMode(){
        //get all the controllers
        IngameScreenController ingameController = ingameScreenControllerProvider.get();
        NewGameScreenLobbyController newGameController = newGameLobbyControllerProvider.get();
        LobbyScreenController lobbyController = lobbyScreenControllerProvider.get();
        ChatController chatController = chatControllerProvider.get();
        EditProfileController editController = editProfileControllerProvider.get();
        LoginScreenController loginController = loginScreenControllerProvider.get();
        RulesScreenController rulesController = rulesScreenControllerProvider.get();
        //handle the options
        if (lightMode_RadioButton.isSelected()){
            ingameController.getApp().getStage().getScene().getStylesheets().clear();
            newGameController.getApp().getStage().getScene().getStylesheets().clear();
            lobbyController.getApp().getStage().getScene().getStylesheets().clear();
            chatController.getApp().getStage().getScene().getStylesheets().clear();
            editController.getApp().getStage().getScene().getStylesheets().clear();
            loginController.getApp().getStage().getScene().getStylesheets().clear();
            rulesController.getApp().getStage().getScene().getStylesheets().clear();
            stage.getScene().getStylesheets().clear();
        }
        if(darkMode_RadioButton.isSelected()){
            ingameController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
            newGameController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
            lobbyController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
            chatController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
            editController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
            loginController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
            rulesController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
            stage.getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
        }

    }

    public void setDarkMode(){
        darkMode = true;
    }

    public void leave(){
        stage.close();
    }

    public App getApp() {
        return this.app;
    }
}
