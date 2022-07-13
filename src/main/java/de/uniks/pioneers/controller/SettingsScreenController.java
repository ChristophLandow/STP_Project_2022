package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.HotkeyController;
import de.uniks.pioneers.controller.subcontroller.LobbyGameListController;
import de.uniks.pioneers.controller.subcontroller.LobbyUserlistController;
import de.uniks.pioneers.controller.subcontroller.SpeechSettingsController;
import de.uniks.pioneers.services.PrefService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
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

import static de.uniks.pioneers.Constants.*;

@Singleton
public class SettingsScreenController implements Controller, Initializable {

    @FXML public Button leaveButton;
    @FXML public RadioButton lightMode_RadioButton;
    @FXML public RadioButton darkMode_RadioButton;
    @FXML public ChoiceBox<String> musicChoiceBox, genderChoiceBox;
    @FXML public Slider volumeSlider;
    @FXML public CheckBox voiceOutputCheckBox;
    @FXML public HBox hotkeyHBox;

    @Inject
    PrefService prefService;

    private final App app;
    private Stage stage;
    private final String[] songNameList = {"no music", "Hardbass", "Ambient"};
    private final Provider<IngameScreenController> ingameScreenControllerProvider;
    private final Provider<NewGameScreenLobbyController> newGameLobbyControllerProvider;
    private final Provider<EditProfileController> editProfileControllerProvider;
    private final Provider<ChatController> chatControllerProvider;
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final Provider<LoginScreenController> loginScreenControllerProvider;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;
    private final Provider<LobbyUserlistController> lobbyUserlistControllerProvider;

    private final Provider<LobbyGameListController> lobbyGameListControllerProvider;

    @Inject Provider<SpeechSettingsController> speechSettingsControllerProvider;
    private ArrayList<File> songs;
    private MediaPlayer mediaPlayer;
    private HotkeyController hotkeyController;
    private SpeechSettingsController speechSettingsController;

    @Inject
    public SettingsScreenController(App app, Provider<IngameScreenController> ingameScreenControllerProvider,
                                    Provider<LobbyGameListController> lobbyGameListControllerProvider,
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
        this.lobbyGameListControllerProvider = lobbyGameListControllerProvider;
    }

    @Override
    public void init() {
        // check if settings screen is not open yet
        if (this.stage == null) {
            this.stage = new Stage();
            this.stage.setScene(new Scene(render()));
            this.stage.setTitle(SETTINGS_SCREEN_TITLE);
            if(prefService.getDarkModeState()) {
                this.stage.getScene().getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_SettingsScreen.css");
            } else {
                this.stage.getScene().getStylesheets().add("/de/uniks/pioneers/styles/SettingsScreen.css");
            }
            this.stage.show();
        } else {
            if(prefService.getDarkModeState()) {
                this.stage.getScene().getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_SettingsScreen.css");
            } else {
                this.stage.getScene().getStylesheets().add("/de/uniks/pioneers/styles/SettingsScreen.css");
            }
            // bring to front if already open
            this.stage.show();
            this.stage.toFront();
        }
        app.setIcons(stage);
        stage.setOnCloseRequest(event -> leave());
        musicChoiceBox.setTooltip((new Tooltip("Choose your background music")));
        if(prefService.getDarkModeState()){
            darkMode_RadioButton.setSelected(true);
        } else {
            lightMode_RadioButton.setSelected(true);
        }
        volumeSlider.setMin(0);
        volumeSlider.setMax(1);
        volumeSlider.setValue(0.3);
        //list all songs
        songs = new ArrayList<>();
        File songDirectory = new File("src/main/resources/de/uniks/pioneers/music");
        File[] songFiles = songDirectory.listFiles();
        if(songFiles != null) {
            songs.addAll(Arrays.asList(songFiles));
        }
        setEventHandler(lightMode_RadioButton);
        setEventHandler(darkMode_RadioButton);
    }

    @Override
    public void stop() {
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }

        if(speechSettingsController != null){
            speechSettingsController.stop();
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
        hotkeyController = new HotkeyController(ingameScreenControllerProvider.get().getApp().getStage().getScene(), ingameScreenControllerProvider);
        hotkeyHBox.getChildren().add(hotkeyController.render());
        hotkeyController.init();

        speechSettingsController = speechSettingsControllerProvider.get();
        speechSettingsController.setVoiceOutputCheckBox(this.voiceOutputCheckBox);
        speechSettingsController.setGenderChoiceBox(this.genderChoiceBox);
        speechSettingsController.init();
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
        if(mediaPlayer != null) {
            mediaPlayer.stop();
        }
        //find song and play it
        int index = musicChoiceBox.getSelectionModel().getSelectedIndex();
        if (index == 0) {
            mediaPlayer.stop();
        } else {
            Media media = new Media(songs.get(index-1).toURI().toString());
            mediaPlayer = new MediaPlayer(media);
            mediaPlayer.play();
        }

    }

    //The Darkmode should work if the SettingsScreen is open nearby AND without it. Here we handle only the nearby
    // part. Without the SettingsScreen it works via the setDarkMode Method.
    public void setApperenceMode() {
        //get all the controllers
        IngameScreenController ingameScreenController = ingameScreenControllerProvider.get();
        NewGameScreenLobbyController newGameController = newGameLobbyControllerProvider.get();
        LobbyScreenController lobbyController = lobbyScreenControllerProvider.get();
        ChatController chatController = chatControllerProvider.get();
        EditProfileController editController = editProfileControllerProvider.get();
        LoginScreenController loginController = loginScreenControllerProvider.get();
        RulesScreenController rulesController = rulesScreenControllerProvider.get();
        LobbyUserlistController userListController = lobbyUserlistControllerProvider.get();
        LobbyGameListController gameListController = lobbyGameListControllerProvider.get();

        //handle the options
        if (lightMode_RadioButton.isSelected()){
            prefService.saveDarkModeState(DARKMODE_FALSE);
            ingameScreenController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_IngameScreen.css")));
            ingameScreenController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/IngameScreen.css");
            newGameController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_NewGameScreen.css")));
            newGameController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/NewGameScreen.css");
            lobbyController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_LobbyScreen.css")));
            lobbyController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/LobbyScreen.css");
            chatController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_ChatScreen.css")));
            chatController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/ChatScreen.css");
            editController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_EditProfileScreen.css")));
            editController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/EditProfileScreen.css");
            loginController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_LoginScreenScreen.css")));
            loginController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/LoginScreen.css");
            rulesController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_RulesScreen.css")));
            rulesController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/RulesScreen.css");
            userListController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_UserListView.css")));
            userListController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/UserListView.css");
            gameListController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_LobbyGameList.css")));
            gameListController.getApp().getStage().getScene().getStylesheets().add("/de/uniks/pioneers/styles/LobbyGameList.css");
            stage.getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_SettingsScreen.css")));
            stage.getScene().getStylesheets().add( "/de/uniks/pioneers/styles/SettingsScreen.css");
        }
        if(darkMode_RadioButton.isSelected()){
            prefService.saveDarkModeState(DARKMODE_TRUE);
            ingameScreenController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/IngameScreen.css")));
            ingameScreenController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_IngameScreen.css");
            newGameController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/NewGameScreen.css")));
            newGameController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_NewGameScreen.css");
            lobbyController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_LobbyScreen.css");
            chatController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/ChatScreen.css")));
            chatController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_ChatScreen.css");
            editController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/EditProfileScreen.css")));
            editController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_EditProfileScreen.css");
            loginController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/LoginScreen.css")));
            loginController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_LoginScreen.css");
            rulesController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/RulesScreen.css")));
            rulesController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_RulesScreen.css");
            userListController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/UserListView.css")));
            userListController.getApp().getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_UserListView.css");
            gameListController.getApp().getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/LobbyGameList.css")));
            gameListController.getApp().getStage().getScene().getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_LobbyGameList.css");
            stage.getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/SettingsScreen.css")));
            stage.getScene().getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_SettingsScreen.css");
        }
    }

    private void setEventHandler(Node node) {
        node.addEventHandler(KeyEvent.KEY_PRESSED, event -> {
            if (event.getCode().equals(KeyCode.SPACE)) {
                setApperenceMode();
                event.consume();
            }
        });
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

    public void safe() {
        hotkeyController.safeHotkeys();
        speechSettingsController.saveSettings();
    }
}