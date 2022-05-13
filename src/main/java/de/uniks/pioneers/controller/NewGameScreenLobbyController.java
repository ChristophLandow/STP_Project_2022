package de.uniks.pioneers.controller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.ws.EventListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class NewGameScreenLobbyController implements Controller {
    private final EventListener eventListener;
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    @FXML
    public Pane root;
    @FXML
    public VBox vBoxRoot;
    @FXML
    public HBox topLevel;
    @FXML
    public VBox leftBox;
    @FXML
    public Label gameNameLabel;
    @FXML
    public Label passwordLabel;
    @FXML
    public VBox userBox;
    @FXML
    public VBox rightBox;
    @FXML
    public VBox messageBox;
    @FXML
    public ScrollBar scrollbar;
    @FXML
    public HBox messageHbox;
    @FXML
    public TextField messageText;
    @FXML
    public Button sendButton;
    @FXML
    public HBox buttonBox;
    @FXML
    public Button readyButton;
    @FXML
    public Button startGameButton;
    @FXML
    public Button leaveButton;

    private Game game;
    private List <Member> members;
    private List <MessageDto> messaages;


    @Inject
    public NewGameScreenLobbyController(EventListener eventListener,Provider<LobbyScreenController> lobbyScreenControllerProvider) {
        this.eventListener = eventListener;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.members= new ArrayList<>();
        this.messaages= new ArrayList<>();

    }

    @Override
    public void init() {
        LobbyScreenController lobbyScreenController = lobbyScreenControllerProvider.get();
        game = lobbyScreenController.gameForNewLobby.get();

        //set game name label and password text label
        gameNameLabel.setText(game.name());
        passwordLabel.setText("kappa");

        initMemberListener();
        initMessageListener();
        

        
    }

    private void initMessageListener() {
        String patternToObserveChatMessages = String.format("games.%s.messages.*.*",game._id());
        eventListener.listen(patternToObserveChatMessages, MessageDto.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(messageEvent -> {
                    if (messageEvent.event().endsWith(".created")) {
                        messaages.add(messageEvent.data());
                    } else if (messageEvent.event().endsWith(".deleted")) {
                        messaages.remove(messageEvent.data());
                    }
                });
    }

    private void initMemberListener() {
        String patternToObserveGameMembers = String.format("games.%s.members.*",game._id());
        eventListener.listen(patternToObserveGameMembers, Member.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(memberEvent -> {
                    if (memberEvent.event().endsWith(".created")) {
                        members.add(memberEvent.data());
                    } else if (memberEvent.event().endsWith(".deleted")) {
                        members.remove(memberEvent.data());
                    } else {
                        updateMember(memberEvent.data());
                    }
                });
    }


    private void updateMember(Member data) {
    }

    @Override
    public void stop() {

    }

    @Override
    public Parent render() {
        Parent parent;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/NewGameLobbyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            parent = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return parent;
    }

}