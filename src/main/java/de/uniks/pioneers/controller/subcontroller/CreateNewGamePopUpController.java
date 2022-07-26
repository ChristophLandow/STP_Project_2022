package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.services.EventHandlerService;
import de.uniks.pioneers.services.LobbyService;
import de.uniks.pioneers.services.PrefService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.binding.IntegerBinding;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.Window;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.Random;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class CreateNewGamePopUpController implements Controller {
    @FXML public VBox popUpBox;
    @FXML public HBox gameNameBox;
    @FXML public Label gameNameLabel;
    @FXML public TextField gameNameTextField;
    @FXML public HBox passwordBox;
    @FXML public Label passWordLabel;
    @FXML public PasswordField passwordTextField;
    @FXML public HBox buttonBox;
    @FXML public Button cancelButton;
    @FXML public Button createGameButton;
    @FXML public Label nameLen;
    @FXML public Label passwordLen;

    @Inject
    PrefService prefService;
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final Provider<LobbyService> lobbyServiceProvider;
    final CompositeDisposable disposable = new CompositeDisposable();
    private Stage stage;
    private LobbyScreenController lobbyScreenController;
    private final EventHandlerService eventHandlerService;

    @Inject
    public CreateNewGamePopUpController(Provider<LobbyScreenController> lobbyScreenControllerProvider, Provider<LobbyService> lobbyServiceProvider, EventHandlerService eventHandlerService) {
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.lobbyServiceProvider = lobbyServiceProvider;
        this.eventHandlerService = eventHandlerService;
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/CreateNewGamePopUp.fxml"));
        loader.setControllerFactory(c -> this);
        Parent node;
        try {
            node = loader.load();
        } catch (IOException e) {
            node = null;
        }
        return node;
    }

    @Override
    public void init() {
        // get singleton from dagger
        lobbyScreenController = lobbyScreenControllerProvider.get();
        // create binding for name/password input
        IntegerBinding gameNameLength = Bindings.length(gameNameTextField.textProperty());
        IntegerBinding passwordLength = Bindings.length(passwordTextField.textProperty());
        BooleanBinding invalid = Bindings.equal(passwordLen.textProperty(), nameLen.textProperty()).not();
        createGameButton.disableProperty().bind(invalid);
        Node passwordFieldNode = this.passwordTextField;
        eventHandlerService.setEnterEventHandler(passwordFieldNode, this.createGameButton);

        passwordLen.textProperty().bind(Bindings
                .when(passwordLength.greaterThan(0)).then("")
                .otherwise("password must be at least one characters long"));

        nameLen.textProperty().bind(Bindings
                .when(gameNameLength.greaterThan(0)).then("")
                .otherwise("game name must be at least one characters long"));

        // create stage and set window on close request
        stage = (Stage) popUpBox.getScene().getWindow();
        Window window = stage;
        window.setOnCloseRequest(event -> lobbyScreenController.isCreatingGame.set(false));
        if(prefService.getDarkModeState()){
            stage.getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/NewGamePopup.css")));
            stage.getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_NewGamePopup.css");
        } else {
            stage.getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_NewGamePopup.css")));
            stage.getScene().getStylesheets().add( "/de/uniks/pioneers/styles/NewGamePopup.css");
        }
    }

    @Override
    public void stop() {
    }

    public void createGame() {
        String name = gameNameTextField.getText();
        String password = passwordTextField.getText();
        disposable.add(lobbyServiceProvider.get().createGame(name,false, password)
                .observeOn(FX_SCHEDULER)
                .subscribe(game -> lobbyScreenController.showNewGameLobby(game, password, getRandomColor()))
        );
    }

    public void closePopUp() {
        lobbyScreenController.isCreatingGame.set(false);
        stage.close();
    }

    public String getRandomColor() {
        Random obj = new Random();
        int rand_num = obj.nextInt(0xffffff + 1);
        return String.format("#%06x", rand_num);
    }
}
