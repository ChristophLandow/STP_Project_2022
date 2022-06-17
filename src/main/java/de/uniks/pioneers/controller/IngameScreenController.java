package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.*;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.services.*;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.*;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.INGAME_SCREEN_TITLE;
import static de.uniks.pioneers.GameConstants.*;

public class IngameScreenController implements Controller {


    @FXML public Pane fieldPane;

    @FXML public Pane root;
    @FXML public Pane turnPane;
    @FXML public SVGPath streetSVG;
    @FXML public SVGPath houseSVG;
    @FXML public SVGPath citySVG;
    @FXML public Button rulesButton;

    @FXML public Button leaveButton;
    @FXML public Button settingsButton;
    @FXML public ScrollPane chatScrollPane;
    @FXML public VBox messageVBox;
    @FXML public TextField sendMessageField;
    @FXML public ScrollPane userScrollPane;
    @FXML public Label streetCountLabel;
    @FXML public Label houseCountLabel;
    @FXML public Label cityCountLabel;
    @FXML public ImageView tradeImageView;
    @FXML public ImageView hourglassImageView;
    @FXML public ImageView nextTurnImageView;
    @FXML public Label timeLabel;
    @FXML public Pane situationPane;
    @FXML public Label situationLabel;
    @FXML public ImageView leftDiceImageView;
    @FXML public ImageView rightDiceImageView;
    @FXML public ImageView hammerImageView;
    @FXML public ListView<Node> playerListView;
    @FXML public Rectangle downRectangle;
    @FXML public Rectangle upRectangle;
    @FXML public Pane roadFrame;
    @FXML public Pane settlementFrame;
    @FXML public Pane cityFrame;

    @Inject GameChatController gameChatController;
    @Inject Provider<IngamePlayerListElementController> elementProvider;
    @Inject Provider<IngamePlayerResourcesController> resourcesControllerProvider;

    @Inject Provider<StreetPointController> streetPointControllerProvider;

    private final GameService gameService;
    private final LeaveGameController leaveGameController;
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;

    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    private final int gameSize;
    private List<User> users;
    private final App app;
    private final GameStorage gameStorage;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;
    private final Provider<SettingsScreenController> settingsScreenControllerProvider;
    private final IngameService ingameService;
    private final UserService userService;
    private final TimerService timerService;
    private final EventListener eventListener;
    private final BoardController boardController;
    private final DiceSubcontroller diceSubcontroller;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private String myColor;
    private boolean darkMode = false;
    private boolean onClose = false;

    @Inject
    public IngameScreenController(App app,Provider<LobbyScreenController> lobbyScreenControllerProvider,
                                  Provider<RulesScreenController> rulesScreenControllerProvider,
                                  Provider<SettingsScreenController> settingsScreenControllerProvider,
                                  IngameService ingameService, GameStorage gameStorage,
                                  UserService userService, GameService gameService,
                                  EventListener eventListener, LeaveGameController leaveGameController,
                                  TimerService timerService) {
        this.app = app;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
        this.settingsScreenControllerProvider = settingsScreenControllerProvider;
        this.ingameService = ingameService;
        this.gameStorage = gameStorage;
        this.userService = userService;
        this.eventListener = eventListener;
        this.gameService = gameService;
        this.timerService = timerService;
        this.diceSubcontroller = new DiceSubcontroller(ingameService, gameService, timerService);
        this.leaveGameController = leaveGameController;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.gameSize = 2;
        this.boardController = new BoardController(ingameService, userService, timerService, game, gameSize, this.gameStorage);
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/IngameScreen.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent view;
        try {
            view = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        this.boardController.fieldPane = this.fieldPane;
        this.boardController.streetPointControllerProvider = this.streetPointControllerProvider;
        return view;
    }

    @Override
    public void init() {
        this.app.getStage().setOnCloseRequest(event -> {
            onClose = true;
            leave();
            userService.editProfile(null, null, null, "offline").subscribe();
            Platform.exit();
            System.exit(0);
        });

        // set variables
        app.getStage().setTitle(INGAME_SCREEN_TITLE);
        if(darkMode){
            app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_stylesheet.css");
        }
        gameService.game.set(game.get());

        // init game chat controller
        gameChatController
                .setChatScrollPane(this.chatScrollPane)
                .setMessageText(this.sendMessageField)
                .setMessageBox(this.messageVBox)
                .setGame(this.game.get())
                .setUsers(this.users)
                .setIngameScreenController(this);
        gameChatController.render();
        gameChatController.init();

        // set timeLabel of timer
        this.timerService.setTimeLabel(this.timeLabel);

        // set dice subcontroller
        this.diceSubcontroller.init();
        this.diceSubcontroller.setLeftDiceView(this.leftDiceImageView)
                .setRightDiceView(this.rightDiceImageView);

        // init game attributes and event listeners
        gameService.initGame();

        // REST - get game state from server
        disposable.add(ingameService.getCurrentState(game.get()._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(this::handleGameState));

        // init game state listener
        String patternToObserveGameState = String.format("games.%s.state.*", game.get()._id());
        disposable.add(eventListener.listen(patternToObserveGameState, State.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(gameEvent -> {
                    if (gameEvent.event().endsWith(".updated")) {
                        System.out.println("new game state: " + gameEvent.data());
                        this.handleGameState(gameEvent.data());
                    }
                })
        );

        Platform.runLater(() -> {
            // init controller for player resources box
            IngamePlayerResourcesController ingamePlayerResourcesController = resourcesControllerProvider.get();
            ingamePlayerResourcesController.root = this.root;
            ingamePlayerResourcesController.render();
            ingamePlayerResourcesController.init(gameService.players.get(gameService.me));
        });

        gameService.loadPlayers(game.get());

        // add change listeners
        // players change listener
        gameService.players.addListener((MapChangeListener<? super String, ? super Player>) c -> {
            if (c.wasAdded() && !c.wasRemoved()) {
                this.renderPlayer(c.getValueAdded());
            } else if (c.wasRemoved() && !c.wasAdded()) {
                this.deletePlayer(c.getValueRemoved());
            }
        });

        // buildings change listener
        gameService.buildings.addListener((ListChangeListener<? super Building>) c -> {
            System.out.println(gameService.buildings);
            c.next();
            System.out.println(c.wasAdded());
            System.out.println(c.wasUpdated());
            System.out.println(c.wasReplaced());
            if (c.wasAdded() || c.wasReplaced()) {
                c.getAddedSubList().forEach(this::renderBuilding);
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(this::deleteBuilding);
            }
        });
    }
    private void renderBuilding(Building building) {this.boardController.renderBuilding(building);}

    private void deletePlayer(Player player) {
        // TODO: remove player from list
    }

    public void renderPlayer(Player player) {
        IngamePlayerListElementController playerListElement = elementProvider.get();
        playerListElement.nodeListView = playerListView;
        playerListElement.render(player.userId());
    }

    private void deleteBuilding(Building building) {}

    private void handleGameState(State currentState) {
        // enable corresponding user to perform their action
        ExpectedMove move = currentState.expectedMoves().get(0);

        String actionString = "";
        if (move.players().get(0).equals(userService.getCurrentUser()._id())) {
            // enable posting move
            System.out.println("It's your turn now!");
            switch (move.action()) {
                case FOUNDING_ROLL, ROLL -> { this.enableRoll(move.action()); actionString = ROLL_DICE; }
                case FOUNDING_SETTLEMENT_1, FOUNDING_SETTLEMENT_2 -> { this.enableBuildingPoints(move.action()); actionString = PLACE_SETTLEMENT; }
                case FOUNDING_ROAD_1, FOUNDING_ROAD_2 -> { this.enableStreetPoints(move.action()); actionString = PLACE_ROAD; }
                case BUILD -> {
                    // set builder timer, in progress...
                    actionString = BUILD;
                    this.timerService.setBuildTimer(new Timer(), this.timeLabel);
                    this.enableEndTurn();
                    this.enableBuildingPoints(move.action());
                    this.enableStreetPoints(move.action());
                }
            }
        }
        this.setSituationLabel(move.players().get(0), actionString);
    }

    private void enableStreetPoints(String action) {this.boardController.enableStreetPoints(action);}

    private void enableBuildingPoints(String action) {this.boardController.enableBuildingPoints(action);}

    private void enableEndTurn() {
        this.turnPane.setOnMouseClicked(this::endTurn);
    }

    private void endTurn(MouseEvent mouseEvent) {
        final CreateMoveDto moveDto = new CreateMoveDto(BUILD, null);
        disposable.add(ingameService.postMove(game.get()._id(), moveDto)
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> this.turnPane.setOnMouseClicked(null))
        );
    }
    private void setSituationLabel(String playerId, String actionString) {
        // set game state label
        String playerName;

        if (playerId.equals(userService.getCurrentUser()._id())) {
            playerName = "ME";
            this.hourglassImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("ingame/next.png")).toString()));
        } else {
            playerName = userService.getUserById(playerId).blockingFirst().name();
            this.hourglassImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("ingame/sanduhr.png")).toString()));
        }
        this.situationLabel.setText(playerName + ":\n" + actionString);
    }

    private void enableRoll(String action) {
        // init dice subcontroller
        this.diceSubcontroller.setAction(action);
        this.diceSubcontroller.activate();
    }

    public App getApp() {
        return this.app;
    }

    public void setPlayerColor(String hexColor) {
        this.myColor = hexColor;
        streetSVG.setFill(Paint.valueOf(hexColor));
        houseSVG.setFill(Color.WHITE);
        houseSVG.setStroke(Paint.valueOf(hexColor));
        houseSVG.setStrokeWidth(1.5);
        citySVG.setFill(Color.WHITE);
        citySVG.setStroke(Paint.valueOf(hexColor));
        citySVG.setStrokeWidth(2.0);
    }

    public void leave() {
        LobbyScreenController lobbyController = lobbyScreenControllerProvider.get();
        if(!app.getStage().getScene().getStylesheets().isEmpty()){
             lobbyController.setDarkMode();
        }
        SettingsScreenController settingsController = settingsScreenControllerProvider.get();
        settingsController.stop();

        if(game.get().owner().equals(userService.getCurrentUser()._id())) {
            gameChatController.sendMessage("Host left the Game!", game.get());
            disposable.add(gameService.deleteGame(game.get()._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> {
                        this.stop();
                        disposable.dispose();
                        if(!onClose) {
                            app.show(lobbyController);
                        }
                    }, Throwable::printStackTrace));
        } else {
            leaveGameController.saveLeavedGame(this.game.get()._id(), users, myColor);
            this.stop();
            disposable.dispose();
            if(!onClose) {
                app.show(lobbyController);
            }
        }
    }

    public void toRules() {
        RulesScreenController rulesController = rulesScreenControllerProvider.get();
        if(darkMode){
            rulesController.setDarkMode();
        }
        rulesController.init();
    }

    public void toSettings() {
        SettingsScreenController settingsController = settingsScreenControllerProvider.get();
        if(darkMode){
            settingsController.setDarkMode();
        }
        settingsController.init();
    }

    public void onHammerPressed() {
    }

    public void onStreetPressed() {
    }

    public void onHousePressed() {
    }

    public void onCityPressed() {
    }

    public void onTradePressed() {
    }

    @Override
    public void stop() {gameChatController.stop();}
    public void setUsers(List<User> users) {this.users = users;}
    public void loadMap() {

        this.ingameService.getMap(this.game.get()._id())
                .observeOn(FX_SCHEDULER)
                .doOnComplete(this::buildBoardUI)
                .subscribe();
    }
    private void buildBoardUI(){this.boardController.buildBoardUI();}
    public void setDarkmode(){darkMode = true;}

    public void setBrightMode(){
        darkMode = false;
    }
    public void selectStreet() {
        this.gameStorage.selectedBuilding = ROAD;
        this.roadFrame.setBackground(Background.fill(Color.rgb(0,100,0)));
        this.settlementFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
        this.cityFrame.setBackground(Background.fill(Color.rgb(250,250,250)));}
    public void selectSettlement() {
        this.gameStorage.selectedBuilding = SETTLEMENT;
        this.settlementFrame.setBackground(Background.fill(Color.rgb(0,100,0)));
        this.roadFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
        this.cityFrame.setBackground(Background.fill(Color.rgb(250,250,250)));}
    public void selectCity() {
        this.gameStorage.selectedBuilding = CITY;
        this.cityFrame.setBackground(Background.fill(Color.rgb(0,100,0)));
        this.settlementFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
        this.roadFrame.setBackground(Background.fill(Color.rgb(250,250,250)));}
}
