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
import javafx.beans.binding.Bindings;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
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
    private final UserService userService;
    private final GameService gameService;
    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    public SimpleStringProperty password = new SimpleStringProperty();
    private final ObservableList<Member> members = FXCollections.observableArrayList();
    public SimpleIntegerProperty memberCount = new SimpleIntegerProperty();
    private final Map<String, User> users = new HashMap<>();
    private User currentUser;
    private final Map<String, PlayerEntryController> playerEntries = new HashMap<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

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
        passwordLabel.setText(password.get());
        clientUserNameLabel.setText(currentUser.name());
        colorPickerController = new ColorPickerController(colorPicker, houseSVG);
        this.reactivateReadyButton();

        // set on close request ...


        // when member count less than three games cant not be started
        final BooleanBinding lessThanThree = Bindings.lessThan(memberCount, 0);
        startGameButton.disableProperty().bind(lessThanThree);

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
                .setUsers(this.users.values().stream().toList());
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
        users.remove(member.userId());

        if(member.userId().equals(game.get().owner()) && !userService.getCurrentUser()._id().equals(game.get().owner())){
            app.show(lobbyScreenControllerProvider.get());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, Constants.HOST_LEFT_GAME_ALERT);
            alert.showAndWait();
        }
    }

    private void renderUser(Member member) {
        if(!users.containsKey(member.userId())) {
            User user = userService.getUserById(member.userId()).blockingFirst();
            // when we make the application multi stage, we need a userlistener or if a user dies
            initUserListener(user);

            users.put(user._id(), user);

            Image userImage;
            try {
                userImage = new Image(user.avatar());
            } catch (IllegalArgumentException | NullPointerException e) {
                userImage = new Image(Constants.DEFAULT_AVATAR);
            }

            if(!currentUser._id().equals(member.userId())) {
                PlayerEntryController playerEntryController = new PlayerEntryController(userImage, user.name(), member.color(), user._id());
                playerEntryController.setReady(false);
                playerEntries.put(user._id(), playerEntryController);
                userBox.getChildren().add(playerEntryController.getPlayerEntry());
            }
        }
    }
    private void initUserListener(User user) {
        String patternToObserveGameUsers = String.format("users.%s.updated", user._id());
        disposable.add(eventListener.listen(patternToObserveGameUsers, User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(userEvent -> {
                    User userFromEvent = userEvent.data();
                    if (userFromEvent.status().equals("offline")) {
                        Member toRemove = members.stream().filter(member -> member.userId().equals(userFromEvent._id())).findAny().get();
                        deleteUser(toRemove);
                    }
                })
        );
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
                        members.replaceAll(m -> m.userId().equals(member.userId()) ? member : m);
                        setReadyColor(member.userId(), member.ready(), member.color());
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
                    game.set(gameEvent.data());
                    memberCount.set(game.get().members());
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
        currentUser = userService.getCurrentUser();
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/NewGameLobbyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent view;
        try {
            view = loader.load();

            // set start button invisible if currentUser is not gameOwner
            if (!currentUser._id().equals(game.get().owner())) {
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
        boolean difference = true;

        for(PlayerEntryController entry : playerEntries.values()) {
            if(entry.getReady() && !colorPickerController.checkColorDifference(entry.getPlayerColor())) {
                difference = false;
                break;
            }
        }

        if(difference) {
            clientReady = !clientReady;
            disposable.add(newGameLobbyService.patchMember(game.get()._id(), currentUser._id(), clientReady, colorPickerController.getColor())
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
            this.reactivateReadyButton();
        } else {
            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Selected color is too similar to another player's color!");
            alert.showAndWait();
        }
    }

    private void setReadyColor(String memberId, boolean ready, String hexColor) {
        if(playerEntries.containsKey(memberId)) {
            playerEntries.get(memberId).setReady(ready);
            playerEntries.get(memberId).setColor(hexColor);
        }
    }

    public void startGame() {
        // check if all users are ready
        if (allUsersReady()) {
            disposable.add(newGameLobbyService.updateGame(game.get(),password.get(),true)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(response -> this.toIngame(), Throwable::printStackTrace));
        }
    }

    private void toIngame() {
        IngameScreenController ingameScreenController = ingameScreenControllerProvider.get();
        ingameScreenController.game.set(this.game.get());
        ingameScreenController.loadMap();
        ingameScreenController.setUsers(this.users.values().stream().toList());
        app.show(ingameScreenController);
        ingameScreenController.setPlayerColor(colorPickerController.getColor());
    }

    private boolean allUsersReady() {
        boolean playersReady = true;

        for(PlayerEntryController entry : playerEntries.values()) {
            if (!entry.getReady()) {
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
        if (game.get().owner().equals(currentUser._id())) {
            disposable.add(gameService.deleteGame(game.get()._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> app.show(lobbyScreenControllerProvider.get()), Throwable::printStackTrace));
        } else {
            disposable.add(newGameLobbyService.deleteMember(game.get()._id(), currentUser._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> app.show(lobbyScreenControllerProvider.get()), Throwable::printStackTrace));
        }
    }

    public void onColorChange() {
        colorPickerController.setColor();
    }

    public void setPlayerColor(String hexColor) {
        colorPickerController.setColor(hexColor);

        if(game.get().owner().equals(currentUser._id())) {
            disposable.add(newGameLobbyService.patchMember(game.get()._id(), currentUser._id(), clientReady, colorPickerController.getColor())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(result -> {}, Throwable::printStackTrace));
        }
    }

    public ObservableList<Member> getMembers() {
        return members;
    }

    private void reactivateReadyButton() {
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
}