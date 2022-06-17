package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.LobbyUserlistController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.ResourceBundle;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

import static de.uniks.pioneers.Constants.SETTINGS_SCREEN_TITLE;

@Singleton
public class SettingsScreenController implements Controller, Initializable {

    @FXML public Button leaveButton;
    @FXML public RadioButton lightMode_RadioButton;
    @FXML public RadioButton darkMode_RadioButton;
    @FXML public ChoiceBox<String> musicChoiceBox;
    @FXML public Slider volumeSlider;

    private final App app;
    private Stage stage;
    private final String[] songNameList = {"Hardbass", "Ambient"};
    private final Provider<IngameScreenController> ingameScreenControllerProvider;
    private final Provider<NewGameScreenLobbyController> newGameLobbyControllerProvider;
    private final Provider<EditProfileController> editProfileControllerProvider;
    private final Provider<ChatController> chatControllerProvider;
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final Provider<LoginScreenController> loginScreenControllerProvider;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;
    private final Provider<LobbyUserlistController> lobbyUserlistControllerProvider;
    private boolean darkMode = false;
    private ArrayList<File> songs;
    private MediaPlayer mediaPlayer;

    @Inject
    public SettingsScreenController(App app, Provider<IngameScreenController> ingameScreenControllerProvider,
                                    Provider<NewGameScreenLobbyController> newGameLobbyControllerProvider,
                                    Provider<EditProfileController> editProfileControllerProvider,
                                    Provider<ChatController> chatControllerProvider,
                                    Provider<LobbyScreenController> lobbyScreenControllerProvider,
                                    Provider<LoginScreenController> loginScreenControllerProvider,
                                    Provider<RulesScreenController> rulesScreenControllerProvider,
                                    Provider<LobbyUserlistController> lobbyUserlistControllerProvider) {
        this.app = app;
        this.ingameScreenControllerProvider = ingameScreenControllerProvider;
        this.newGameLobbyControllerProvider = newGameLobbyControllerProvider;
        this.editProfileControllerProvider = editProfileControllerProvider;
        this.chatControllerProvider = chatControllerProvider;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.loginScreenControllerProvider = loginScreenControllerProvider;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
        this.lobbyUserlistControllerProvider = lobbyUserlistControllerProvider;
    }

    @Override
    public void init() {
        // check if settings screen is not open yet
        if (this.stage == null) {
            this.stage = new Stage();
            this.stage.setScene(new Scene(render()));
            this.stage.setTitle(SETTINGS_SCREEN_TITLE);
            if(darkMode) {
                this.stage.getScene().getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_SettingsScreen.css");
            }
            this.stage.show();
        } else {
            if(darkMode) {
                this.stage.getScene().getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_SettingsScreen.css");
            }
            // bring to front if already open
            this.stage.show();
            this.stage.toFront();
        }
        app.setIcons(stage);
        stage.setOnCloseRequest(event -> leave());
        musicChoiceBox.setTooltip((new Tooltip("Chooose your backgound music")));
        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(0.3);
        //list all songs
        songs = new ArrayList<>();
        File songDirectory = new File("src/main/resources/de/uniks/pioneers/music");
        File[] songFiles = songDirectory.listFiles();
        if(songFiles != null){
            songs.addAll(Arrays.asList(songFiles));
        }
    }

    @Override
    public void stop() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
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
        musicChoiceBox.getItems().addAll(songNameList);
        musicChoiceBox.setOnAction(this::setMusic);
        volumeSlider.valueProperty().addListener((observable, oldValue, newValue) -> {
            double musicVolume = volumeSlider.getValue();
            if(mediaPlayer != null) {
                mediaPlayer.setVolume(musicVolume);
            }
        });
    }

    private void setMusic(ActionEvent actionEvent) {
        //if a song is played actualy..
        if(mediaPlayer != null){
            mediaPlayer.stop();
        }
        //find song and play it
        int index = musicChoiceBox.getSelectionModel().getSelectedIndex();
        Media media = new Media(songs.get(index).toURI().toString());
        mediaPlayer = new MediaPlayer(media);
        mediaPlayer.play();
    }

    //The Darkmode should work if the SettingsScreen is open nearby AND without it. Here we handle only the nearby
    // part. Without the SettingsScreen it works via the setDarkMode Method.
    public void setApperenceMode() {
        //get all the controllers
        IngameScreenController ingameController = ingameScreenControllerProvider.get();
        NewGameScreenLobbyController newGameController = newGameLobbyControllerProvider.get();
        LobbyScreenController lobbyController = lobbyScreenControllerProvider.get();
        ChatController chatController = chatControllerProvider.get();
        EditProfileController editController = editProfileControllerProvider.get();
        LoginScreenController loginController = loginScreenControllerProvider.get();
        RulesScreenController rulesController = rulesScreenControllerProvider.get();
        LobbyUserlistController userListController = lobbyUserlistControllerProvider.get();
        //handle the options
        if (lightMode_RadioButton.isSelected()){
            ingameController.getApp().getStage().getScene().getStylesheets().clear();
            ingameController.setBrightMode();
            newGameController.getApp().getStage().getScene().getStylesheets().clear();
            newGameController.setBrightMode();
            lobbyController.getApp().getStage().getScene().getStylesheets().clear();
            lobbyController.setBrightMode();
            chatController.getApp().getStage().getScene().getStylesheets().clear();
            chatController.setBrightMode();
            editController.getApp().getStage().getScene().getStylesheets().clear();
            editController.setBrightMode();
            loginController.getApp().getStage().getScene().getStylesheets().clear();
            loginController.setBrightMode();
            rulesController.getApp().getStage().getScene().getStylesheets().clear();
            rulesController.setBrightMode();
            userListController.getApp().getStage().getScene().getStylesheets().clear();
            userListController.setBrightMode();
            stage.getScene().getStylesheets().clear();
        }
        if(darkMode_RadioButton.isSelected()){
            ingameController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_IngameScreen.css");
            ingameController.setDarkmode();
            newGameController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_NewGameScreen.css");
            newGameController.setDarkMode();
            lobbyController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_LobbyScreen.css");
            lobbyController.setDarkMode();
            chatController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_ChatScreen.css");
            chatController.setDarkMode();
            editController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_EditProfileScreen.css");
            editController.setDarkMode();
            loginController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_LoginScreen.css");
            loginController.setDarkMode();
            rulesController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_RulesScreen.css");
            rulesController.setDarkMode();
            userListController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_UserListView.css");
            userListController.setDarkMode();
            stage.getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_SettingsScreen.css");
        }
    }

    public void setDarkMode(){
        darkMode = true;
    }

    public void leave() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
        stage.close();
    }
    public App getApp() {
        return this.app;
    }
}