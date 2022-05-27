package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Constants;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.ColorPickerController;
import de.uniks.pioneers.controller.subcontroller.GameChatController;
import de.uniks.pioneers.controller.subcontroller.PlayerEntryController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.SVGPath;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.*;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class NewGameScreenLobbyController implements Controller {
    @FXML public Pane root;
    @FXML public VBox vBoxRoot;
    @FXML public HBox topLevel;
    @FXML public VBox leftBox;
    @FXML public Label gameNameLabel;
    @FXML public Label passwordLabel;
    @FXML public VBox userBox;
    @FXML public VBox rightBox;
    @FXML public VBox messageBox;
    @FXML public ScrollPane chatScrollPane;
    @FXML public HBox messageHbox;
    @FXML public TextField messageText;
    @FXML public Button sendButton;
    @FXML public HBox buttonBox;
    @FXML public Button readyButton;
    @FXML public Button startGameButton;
    @FXML public Button leaveButton;
    @FXML public ImageView RulesButton;
    @FXML public HBox clientReadyBox;
    @FXML public Label clientReadyLabel;
    @FXML public ColorPicker colorPicker;
    @FXML public SVGPath houseSVG;
    @FXML public ImageView clientAvatar;
    @FXML public Label clientUserNameLabel;

    private final EventListener eventListener;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;
    private final NewGameLobbyService newGameLobbyService;
    private final App app;
    private Parent view;
    private final UserService userService;
    private final GameService gameService;
    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    private final ObservableList<Member> members = FXCollections.observableArrayList();
    private final List<User> users = new ArrayList<>();
    private final Map<String, PlayerEntryController> playerEntries = new HashMap<String, PlayerEntryController>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private String password;
    private GameChatController gameChatController;
    private ColorPickerController colorPickerController;
    private boolean clientReady = false;

    @Inject
    Provider<LobbyScreenController> lobbyScreenControllerProvider;
    @Inject
    Provider<GameChatController> gameChatControllerProvider;
    @Inject
    Provider<IngameScreenController> ingameScreenControllerProvider;

    @Inject
    public NewGameScreenLobbyController(EventListener eventListener, Provider<RulesScreenController> rulesScreenControllerProvider,
                                        NewGameLobbyService newGameLobbyService, App app, UserService userService, GameService gameService) {
        this.eventListener = eventListener;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
        this.newGameLobbyService = newGameLobbyService;
        this.app = app;
        this.userService = userService;
        this.gameService = gameService;
    }

    @Override
    public void init() {
        //set game name label and password text label
        gameNameLabel.setText(game.get().name());
        passwordLabel.setText(this.getPassword());
        clientUserNameLabel.setText(userService.getCurrentUser().name());
        colorPickerController = new ColorPickerController(colorPicker, houseSVG);

        try {
            clientAvatar.setImage(new Image(userService.getCurrentUser().avatar()));
        } catch (IllegalArgumentException | NullPointerException e) {
            clientAvatar.setImage(new Image(Constants.DEFAULT_AVATAR));
        }

        // add mouse event for rules button
        this.RulesButton.setOnMouseClicked(this::openRules);

        // init event listeners
        initMemberListener();
        initGameListener();

        // add listener for member observable
        members.addListener((ListChangeListener<? super Member>) c -> {
            c.next();
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(this::renderUser);
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(this::deleteUser);
            }
        });

        disposable.add(newGameLobbyService.getAll(game.get()._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(this.members::setAll
                        , Throwable::printStackTrace));

        // init game chat controller
        gameChatController = gameChatControllerProvider.get()
                .setChatScrollPane(this.chatScrollPane)
                .setMessageText(this.messageText)
                .setMessageBox(this.messageBox)
                .setSendButton(this.sendButton)
                .setGame(this.game.get())
                .setUsers(this.users);
        gameChatController.render();
        gameChatController.init();
    }

    private void openRules(MouseEvent mouseEvent) {
        RulesScreenController controller = rulesScreenControllerProvider.get();
        controller.init();
    }

    private void deleteUser(Member member) {
        Node removal = userBox.getChildren().stream().filter(node -> node.getId().equals(member.userId())).findAny().get();
        userBox.getChildren().remove(removal);
        playerEntries.remove(member.userId());
        users.removeIf(user -> user._id().equals(member.userId()));

        if(member.userId().equals(game.get().owner()) && !userService.getCurrentUser()._id().equals(game.get().owner())){
            app.show(lobbyScreenControllerProvider.get());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, Constants.HOST_LEFT_GAME_ALERT);
            alert.showAndWait();
        }
    }

    private void renderUser(Member member) {
        User user = userService.getUserById(member.userId()).blockingFirst();
        users.add(user);

        if(!userService.getCurrentUser()._id().equals(member.userId())) {
            Image userImage;
            try {
                userImage = new Image(user.avatar());
            } catch (IllegalArgumentException | NullPointerException e) {
                userImage = new Image(Constants.DEFAULT_AVATAR);
            }

            PlayerEntryController playerEntryController = new PlayerEntryController(userImage, user.name(), member.color(), user._id());
            playerEntryController.setReady(false);
            playerEntries.put(user._id(), playerEntryController);
            userBox.getChildren().add(playerEntryController.getPlayerEntry());

            if (member.ready()) {
                setReady(member.userId(), true);
            }
        }
    }

    private void initMemberListener() {
        String patternToObserveGameMembers = String.format("games.%s.members.*.*", game.get()._id());
        disposable.add(eventListener.listen(patternToObserveGameMembers, Member.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(memberEvent -> {
                    final Member member = memberEvent.data();
                    if(memberEvent.event().endsWith(".created")) {
                        members.add(member);
                    } else if(memberEvent.event().endsWith(".updated")) {
                        if(member.ready()) {
                            setReady(member.userId(), true);
                        } else if(!member.ready()) {
                            setReady(member.userId(), false);
                        }
                    } else if (memberEvent.event().endsWith(".deleted")) {
                        members.remove(member);
                    }
                }));
    }

    private void initGameListener(){
        String patternToObserveGame= String.format("games.%s.*", game.get()._id());
        disposable.add(eventListener.listen(patternToObserveGame, Game.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(gameEvent -> {
                     if (gameEvent.event().endsWith(".updated") && gameEvent.data().started()) {
                         this.toIngame();
                    }
                })
        );
    }

    @Override
    public void stop() {
        gameChatController.stop();
        disposable.dispose();
    }

    @Override
    public Parent render() {
        // Parent parent;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/NewGameLobbyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            view = loader.load();

            // set start button invisible if currentUser is not gameOwner
            if (!userService.getCurrentUser()._id().equals(game.get().owner())) {
                startGameButton.setVisible(false);
            }
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return view;
    }

    public void onSetReadyButton() {
        // set member "ready" true in API
        clientReady = !clientReady;
        disposable.add(newGameLobbyService.patchMember(game.get()._id(), newGameLobbyService.getCurrentMemberId(), clientReady, colorPickerController.getColor())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    if(clientReady) {
                        clientReadyLabel.setText("Ready");
                        clientReadyBox.setBackground(Background.fill(Color.GREEN));
                        colorPickerController.setDisable(true);
                    } else {
                        clientReadyLabel.setText("Not Ready");
                        clientReadyBox.setBackground(Background.fill(Color.RED));
                        colorPickerController.setDisable(false);
                    }}, Throwable::printStackTrace));
    }

    private void setReady(String memberId, boolean ready) {
        if(playerEntries.containsKey(memberId)) {
            playerEntries.get(memberId).setReady(ready);
        }
    }

    public void startGame() {
        // check if all users are ready
        if (allUsersReady()) {
            disposable.add(newGameLobbyService.updateGame(game.get(),password,true)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(response -> {
                        this.toIngame();
                    }, Throwable::printStackTrace));
        }
    }

    private void toIngame() {
        IngameScreenController ingameScreenController = ingameScreenControllerProvider.get();
        ingameScreenController.game.set(this.game.get());
        ingameScreenController.setUsers(this.users);
        app.show(ingameScreenController);
        ingameScreenController.setPlayerColor(colorPickerController.getColor());
    }

    private boolean allUsersReady() {
        boolean playersReady = true;
        Iterator<HashMap.Entry<String, PlayerEntryController>> it = playerEntries.entrySet().iterator();

        while(it.hasNext()) {
            HashMap.Entry<String, PlayerEntryController> entry = it.next();
            if(!entry.getValue().getReady()) {
                playersReady = false;
                break;
            }
        }

        // check if there is a checkmark
        if(clientReady && playersReady) {
            return true;
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Not all players are ready yet!");
            alert.showAndWait();
            return false;
        }
    }

    public void leaveLobby() {
        if (game.get().owner().equals(userService.getCurrentUser()._id())) {
            disposable.add(gameService.deleteGame(game.get()._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> {
                        app.show(lobbyScreenControllerProvider.get());
                    }, Throwable::printStackTrace));
        } else {
            disposable.add(newGameLobbyService.deleteMember(game.get()._id(), userService.getCurrentUser()._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> app.show(lobbyScreenControllerProvider.get()), Throwable::printStackTrace));
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void onColorChange() {
        colorPickerController.setColor();
    }

    public void setPlayerColor(String hexColor) {
        colorPickerController.setColor(hexColor);

        if(game.get().owner().equals(userService.getCurrentUser()._id())) {
            disposable.add(newGameLobbyService.patchMember(game.get()._id(), newGameLobbyService.getCurrentMemberId(), clientReady, colorPickerController.getColor())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(result -> {

                    }, Throwable::printStackTrace));
        }
    }

    public ObservableList<Member> getMembers() {
        return members;
    }
}