package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.NewGameScreenLobbyController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.services.MapBrowserService;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.event.ActionEvent;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;

import javax.inject.Inject;
import java.util.Map;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class NewGameLobbyReadyController {
    private NewGameScreenLobbyController screenController;
    private CheckBox spectatorCheckBox;
    private Map<String, PlayerEntryController> playerEntries;
    private ColorPickerController colorPickerController;
    private Label clientReadyLabel;
    private HBox clientReadyBox;
    private NewGameLobbyService newGameLobbyService;
    private UserService userService;
    private Game game;
    private Button readyButton;
    private ImageView spectatorImageView;
    private NewGameLobbyGameSettingsController spinnerController;
    private boolean clientReady = false;
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject MapBrowserService mapBrowserService;

    public void init(NewGameScreenLobbyController newGameScreenLobbyController, CheckBox spectatorCheckBox, Map<String, PlayerEntryController> playerEntries,
                     ColorPickerController colorPickerController, Label clientReadyLabel, HBox clientReadyBox, Button readyButton, Button startButton,
                     ImageView spectatorImageView, NewGameLobbyGameSettingsController spinnerController, NewGameLobbyService newGameLobbyService, UserService userService) {
        this.newGameLobbyService = newGameLobbyService;
        this.userService = userService;
        this.screenController = newGameScreenLobbyController;
        this.spectatorCheckBox = spectatorCheckBox;
        this.playerEntries = playerEntries;
        this.colorPickerController = colorPickerController;
        this.clientReadyLabel = clientReadyLabel;
        this.clientReadyBox = clientReadyBox;
        this.game = newGameScreenLobbyController.getGame();
        this.readyButton = readyButton;
        this.spectatorImageView = spectatorImageView;
        this.spinnerController = spinnerController;

        readyButton.setOnAction(this::onSetReadyButton);
        startButton.setOnAction(this::startGame);
    }

    public boolean onSetReadyButton(ActionEvent actionEvent) {
        // set member "ready" true in API
        boolean difference = true;

        if(!spectatorCheckBox.isSelected()) {
            for(PlayerEntryController entry : playerEntries.values()) {
                if(entry.getReady() && !colorPickerController.checkColorDifference(entry.getPlayerColor()) && !entry.getSpectator()) {
                    difference = false;
                    break;
                }
            }
        }

        if (difference) {
            clientReady = !clientReady;
            spectatorCheckBox.setDisable(clientReady);
            disposable.add(newGameLobbyService.patchMember(game._id(), userService.getCurrentUser()._id(), clientReady, colorPickerController.getColor(), spectatorCheckBox.isSelected())
                    .observeOn(FX_SCHEDULER)
                    .doOnError(Throwable::printStackTrace)
                    .subscribe(result -> {
                        if (clientReady) {
                            clientReadyLabel.setText("Ready");
                            clientReadyBox.setBackground(Background.fill(Color.GREEN));
                            if(!spectatorImageView.isVisible()) {
                                colorPickerController.setDisable(true);
                            }
                        } else {
                            clientReadyLabel.setText("Not Ready");
                            clientReadyBox.setBackground(Background.fill(Color.RED));
                            colorPickerController.setDisable(spectatorImageView.isVisible());
                        }
                    }, Throwable::printStackTrace));
            this.reactivateReadyButton();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Selected color is too similar to another player's color!");
            alert.showAndWait();
        }

        return difference;
    }


    public boolean allUsersReady() {
        boolean playersReady = true;

        for (PlayerEntryController entry : playerEntries.values()) {
            if (!entry.getReady()) {
                playersReady = false;
                break;
            }
        }

        // check if there is a checkmark
        if (clientReady && playersReady) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Not all players are ready yet!");
            alert.showAndWait();
            return false;
        }
    }

    private void startGame(ActionEvent actionEvent) {
        // check if all users are ready
        if (allUsersReady()) {
            disposable.add(newGameLobbyService.updateGame(game, screenController.getPassword(), true, spinnerController.getMapSize(),
                            spinnerController.getVictoryPoints(), spinnerController.getMapTemplateID(), true, 0)
                    .observeOn(FX_SCHEDULER)
                    .doOnError(Throwable::printStackTrace)
                    .subscribe(response -> {
                        screenController.setGame(response);
                        screenController.toIngame(this.game, newGameLobbyService.getUsers().values().stream().toList(), colorPickerController.getColor(),
                                false, spinnerController.getMapSize(), spinnerController.getMapTemplateID() != null);
                    }, Throwable::printStackTrace));
        }
    }

    public void reactivateReadyButton() {
        this.readyButton.setDisable(true);
        new Thread(() -> {
            try {
                Thread.sleep(2000);
                readyButton.setDisable(false);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public boolean getReady() {
        return clientReady;
    }

    public void stop() {
        disposable.dispose();
    }
}
