package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Constants;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.GameChatController;
import de.uniks.pioneers.controller.subcontroller.LobbyGameListController;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.NewGameLobbyService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
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
import java.util.Objects;

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

    private final EventListener eventListener;
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final Provider<GameChatController> gameChatControllerProvider;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;
    private final NewGameLobbyService newGameLobbyService;
    private final App app;
    private Parent view;
    private final UserService userService;
    private final GameService gameService;
    private final Provider<LobbyGameListController> lobbyGameListControllerProvider;

    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    public SimpleObjectProperty<User> owner = new SimpleObjectProperty<>();

    private final ObservableList<Member> members = FXCollections.observableArrayList();
    private final List<User> users = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    //private final ObservableList<MessageDto> messages = FXCollections.observableArrayList();
    private String password;

    @Inject
    public NewGameScreenLobbyController(EventListener eventListener, Provider<LobbyScreenController> lobbyScreenControllerProvider,
                                        Provider<GameChatController> gameChatControllerProvider,
                                        Provider<RulesScreenController> rulesScreenControllerProvider,
                                        NewGameLobbyService newGameLobbyService, App app, UserService userService, GameService gameService,
                                        Provider<LobbyGameListController> lobbyGameListControllerProvider) {
        this.eventListener = eventListener;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.gameChatControllerProvider = gameChatControllerProvider;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
        this.newGameLobbyService = newGameLobbyService;
        this.app = app;
        this.userService = userService;
        this.gameService = gameService;
        this.lobbyGameListControllerProvider = lobbyGameListControllerProvider;
    }

    public void postNewMember(Game game, User user, String password) {

        this.password = password;
        this.game.set(game);
        if (user._id().equals(game.owner())){
            this.owner.set(user);
        }
        app.show(this);
        // post new member to game
        /*
        newGameLobbyService.postMember(game._id(), true, this.password)
                .observeOn(FX_SCHEDULER)
                .doOnError(e -> System.out.println(" why this doenst work ? "))
                .subscribe(member -> members.add(member));
         */
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
        //initMessageListener();

        // add listener for member observable
        members.addListener((ListChangeListener<? super Member>) c -> {
            c.next();
            if (c.wasAdded()) {
                /*c.getAddedSubList().stream().forEach(member -> {
                    System.out.println(member);
                    renderUser(member);
                });*/
                c.getAddedSubList().forEach(this::renderUser);
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
        gameChatController.users = this.users;
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
        users.removeIf(user -> user._id().equals(member.userId()));

        if(member.userId().equals(game.get().owner()) && !userService.getCurrentUser()._id().equals(game.get().owner())){
            app.show(lobbyScreenControllerProvider.get());
            Alert alert = new Alert(Alert.AlertType.INFORMATION, Constants.HOST_LEFT_GAME_ALERT);
            alert.showAndWait();
        }
    }

    private void renderUser(Member member) {
        //here i gona create a hbox with an image view
        //User userToRender = lobbyScreenControllerProvider.get().returnUserById(member.UserId());
        //Label userName = new Label(userToRender.name());
        User user = userService.getUserById(member.userId()).blockingFirst();
        users.add(user);

        // put new member information into HBox
        HBox memberBox = new HBox();
        memberBox.setId(user._id());
        Label memberId = new Label(user.name());
        memberBox.getChildren().add(memberId);

        userBox.getChildren().add(memberBox);
    }

    /*private void initMessageListener() {
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
    }*/

    private void initMemberListener() {
        System.out.println(game.get()._id());
        String patternToObserveGameMembers = String.format("games.%s.members.*.*", game.get()._id());
        eventListener.listen(patternToObserveGameMembers, Member.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(memberEvent -> {
                    final Member member = memberEvent.data();
                    System.out.println(member);
                    if (memberEvent.event().endsWith(".created")) {
                        members.add(member);
                        initUserListener(member.userId(), member);
                    } else if (memberEvent.event().endsWith(".updated")) {
                        if (member.ready()) {
                            showReadyCheckMark(member.userId());
                        }
                    } else if (memberEvent.event().endsWith(".deleted")) {
                        members.remove(member);
                    } else {
                        updateMember(member);
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
        disposable.dispose();
    }

    @Override
    public Parent render() {
        // Parent parent;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/NewGameLobbyScreen.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            view = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return view;
    }

    public void setReadyTrue(MouseEvent mouseEvent) {
        // set member "ready" true in API
        disposable.add(newGameLobbyService.setReady(game.get()._id(), newGameLobbyService.getCurrentMemberId())
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    System.out.println("set ready result: " + result.toString());
                }, Throwable::printStackTrace));
    }

    private void showReadyCheckMark(String memberId) {
        // set green checkmark next to current member
        ImageView checkMarkImage = new ImageView(new Image(Objects.requireNonNull(App.class.getResource("checkmark.png")).toString()));
        checkMarkImage.setFitWidth(20);
        checkMarkImage.setFitHeight(20);

        HBox currentMemberBox = (HBox) this.view.lookup("#" + memberId);
        currentMemberBox.getChildren().add(checkMarkImage);
    }

    public void startGame(MouseEvent mouseEvent) {
        // check if all users are ready
        if (allUsersReady()) {
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "START GAME!");
            alert.showAndWait();
        }
    }

    private boolean allUsersReady() {
        for (Node node: userBox.getChildren()) {
            HBox memberBox = (HBox) node;

            // check if there is a checkmark
            if (memberBox.getChildren().size() < 2) {
                System.out.println("Not all users ready!");
                // show popup with unready user(s)
                Label memberLabel = (Label) memberBox.getChildren().get(0);
                Alert alert = new Alert(Alert.AlertType.INFORMATION, memberLabel.getText() + " is not ready yet!");
                alert.showAndWait();
                return false;
            }
        }

        System.out.println("All users ready!");
        return true;
    }
    public void leaveLobby(MouseEvent mouseEvent) {
        if (game.get().owner().equals(userService.getCurrentUser()._id())) {
            disposable.add(gameService.deleteGame(game.get()._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> {
                        System.out.println(res.toString());
                        /*@yannik, ich habe hier eingefügt, dass das game aus der lobby games liste gelöscht wird und nicht mehr
                        gerendert wird
                        */
                        lobbyGameListControllerProvider.get().getGames().remove(game);
                        app.show(lobbyScreenControllerProvider.get());
                    }, Throwable::printStackTrace));
        } else {
            disposable.add(newGameLobbyService.deleteMember(game.get()._id(), userService.getCurrentUser()._id())
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