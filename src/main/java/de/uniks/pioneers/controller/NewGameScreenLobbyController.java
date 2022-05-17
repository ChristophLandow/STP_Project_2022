package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.GameChatController;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
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
    public ScrollPane chatScrollPane;
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
    @FXML
    public ImageView RulesButton;

    private final EventListener eventListener;
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final Provider<GameChatController> gameChatControllerProvider;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;
    private final NewGameLobbyService newGameLobbyService;
    private final App app;
    private final UserService userService;
    private final GameService gameService;

    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    public SimpleObjectProperty<User> owner = new SimpleObjectProperty<>();

    private final ObservableList<Member> members = FXCollections.observableArrayList();
    private final ObservableList<MessageDto> messages = FXCollections.observableArrayList();

    @Inject
    public NewGameScreenLobbyController(EventListener eventListener, Provider<LobbyScreenController> lobbyScreenControllerProvider,
                                        Provider<GameChatController> gameChatControllerProvider,
                                        Provider<RulesScreenController> rulesScreenControllerProvider,
                                        NewGameLobbyService newGameLobbyService, App app, UserService userService, GameService gameService) {
        this.eventListener = eventListener;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.gameChatControllerProvider = gameChatControllerProvider;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
        this.newGameLobbyService = newGameLobbyService;
        this.app = app;
        this.userService = userService;
        this.gameService = gameService;
    }

    public void postNewMember(Game game, User user) {
            System.out.println(game._id());
            this.game.set(game);
            if (user._id().equals(game.owner())){
                this.owner.set(user);
            }
            app.show(this);
            // post new Member to game
            newGameLobbyService.postMember(game._id(), user.name(), true)
                    .observeOn(FX_SCHEDULER)
                    .doOnError(e -> System.out.println(" why this doenst work ? "))
                    .subscribe(member -> members.add(member));
            // rest
            newGameLobbyService.getAll(game._id()).observeOn(FX_SCHEDULER)
               .subscribe(this.members::setAll);

        }


    @Override
    public void init() {
        LobbyScreenController lobbyScreenController = lobbyScreenControllerProvider.get();

        //set game name label and password text label
        gameNameLabel.setText(game.get().name());
        passwordLabel.setText(this.getPassword());

        // init event listeners
        initMemberListener();
        initMessageListener();

        // add listener for member observable
        members.addListener((ListChangeListener<? super Member>) c -> {
            c.next();
            if (c.wasAdded()) {
                c.getAddedSubList().stream().forEach(member -> {
                    System.out.println(member);
                    renderUser(member);
                });
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(this::deleteUser);
            }
        });



        GameChatController gameChatController = gameChatControllerProvider.get();
        gameChatController.chatScrollPane = this.chatScrollPane;
        gameChatController.messageBox = this.messageBox;
        gameChatController.messageText = this.messageText;
        gameChatController.sendButton = this.sendButton;
        gameChatController.game = this.game.get();
        gameChatController.render();
        gameChatController.init();
    }

    private void openRules(MouseEvent mouseEvent) {
        RulesScreenController controller = rulesScreenControllerProvider.get();
        controller.init();
    }

    private void deleteUser(Member member) {
        Node removal = userBox.getChildren().stream().filter(node -> node.getId().equals(member.UserId())).findAny().get();
        userBox.getChildren().remove(removal);
    }

    private void renderUser(Member member) {
        //here i gona create a hbox with an image view
        //User userToRender = lobbyScreenControllerProvider.get().returnUserById(member.UserId());
        //Label userName = new Label(userToRender.name());
        Label memberId = new Label(member.UserId() + " weird null user cannot be kicked");
        //userName.setId(member.UserId());
        memberId.setId(member.UserId());
        //userBox.getChildren().add(userName);
        userBox.getChildren().add(memberId);
    }

    private void initMessageListener() {
        String patternToObserveChatMessages = String.format("games.%s.messages.*.*", game.get()._id());
        eventListener.listen(patternToObserveChatMessages, MessageDto.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(messageEvent -> {
                    if (messageEvent.event().endsWith(".created")) {
                        messages.add(messageEvent.data());
                    } else if (messageEvent.event().endsWith(".deleted")) {
                        messages.remove(messageEvent.data());
                    }
                });
    }

    private void initMemberListener() {
        String patternToObserveGameMembers = String.format("games.%s.members.*", game.get()._id());
        eventListener.listen(patternToObserveGameMembers, Member.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(memberEvent -> {
                    final Member member = memberEvent.data();
                    if (memberEvent.event().endsWith(".created")) {
                        members.add(memberEvent.data());
                        initUserListener(memberEvent.data().UserId(), member);
                    } else if (memberEvent.event().endsWith(".deleted")) {
                        members.remove(memberEvent.data());
                    } else {
                        updateMember(memberEvent.data());
                    }
                });
    }

    private void initUserListener(String userId, Member member) {
        String patternToObserveGameUsers = String.format("users.%s.*", userId);
        eventListener.listen(patternToObserveGameUsers, User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(userEvent -> {
                    final User user = userEvent.data();
                    if (userEvent.event().endsWith(".updated")) {
                        if (user.status().equals("online")) {
                            updateMember(member);
                        } else {
                            deleteMember(member);
                        }
                    }
                });

    }

    private void deleteMember(Member member) {
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

    public void setReadyTrue(MouseEvent mouseEvent) {
    }

    public void startGame(MouseEvent mouseEvent) {
    }

    public void leaveLobby(MouseEvent mouseEvent) {
        if (game.get().owner().equals(userService.getCurrentUserId())) {
            disposable.add(gameService.deleteGame(game.get()._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> {
                        System.out.println(res.toString());
                        app.show(lobbyScreenControllerProvider.get());
                    }, Throwable::printStackTrace));
        } else {
            disposable.add(newGameLobbyService.deleteMember(game.get()._id(), userService.getCurrentUserId())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> {
                        System.out.println(res.toString());
                        app.show(lobbyScreenControllerProvider.get());
                    }, Throwable::printStackTrace));
        }
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}