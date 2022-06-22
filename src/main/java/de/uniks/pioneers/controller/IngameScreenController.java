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
import javafx.scene.layout.AnchorPane;
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
    @FXML public Pane fieldPane, root, turnPane;
    @FXML
    public AnchorPane scrollAnchorPane;
    @FXML public Pane roadFrame, settlementFrame, cityFrame, situationPane;
    @FXML public ScrollPane fieldScrollPane, chatScrollPane, userScrollPane;
    @FXML public SVGPath streetSVG, houseSVG, citySVG;
    @FXML public Button rulesButton, leaveButton, settingsButton;
    @FXML public VBox messageVBox;
    @FXML public TextField sendMessageField;
    @FXML public Label streetCountLabel, houseCountLabel, cityCountLabel;
    @FXML public Label timeLabel, situationLabel;
    @FXML public ImageView tradeImageView, hourglassImageView, nextTurnImageView;
    @FXML public ImageView leftDiceImageView, rightDiceImageView, hammerImageView;
    @FXML public ListView<Node> playerListView;
    @FXML public Rectangle downRectangle, upRectangle;

    @Inject GameChatController gameChatController;

    @Inject PrefService prefService;
    @Inject Provider<IngamePlayerListElementController> elementProvider;
    @Inject Provider<IngamePlayerListSpectatorController> spectatorProvider;
    @Inject Provider<IngamePlayerResourcesController> resourcesControllerProvider;
    @Inject Provider<StreetPointController> streetPointControllerProvider;

    @Inject Provider<ZoomableScrollpane> zoomableScrollpaneProvider;

    private final GameService gameService;
    private final LeaveGameController leaveGameController;
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;

    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
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
        int gameSize = 2;
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

        //this.loadSpectators();
        gameService.members.addListener((ListChangeListener<? super Member>) c -> {
            c.next();
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(this::renderSpectator);
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(this::deleteSpectator);
            }
        });

        // buildings change listener
        gameService.buildings.addListener((ListChangeListener<? super Building>) c -> {
            c.next();
            if (c.wasAdded() || c.wasReplaced()) {
                c.getAddedSubList().forEach(this::renderBuilding);
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(this::deleteBuilding);
            }
        });
        // remaining building count change listener
        gameStorage.remainingBuildings.addListener((MapChangeListener<? super String, ? super Integer>) c -> {
            if(c.getKey().equals(ROAD)){this.streetCountLabel.setText(c.getValueAdded().toString());}
            if(c.getKey().equals(SETTLEMENT)){this.houseCountLabel.setText(c.getValueAdded().toString());}
            if(c.getKey().equals(CITY)){this.cityCountLabel.setText(c.getValueAdded().toString());}
        });

        if(prefService.getDarkModeState()){
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/IngameScreen.css")));
            this.app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_IngameScreen.css");
        } else {
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_IngameScreen.css")));
            this.app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/IngameScreen.css");
        }

        ZoomableScrollpane zoomableScrollpane = zoomableScrollpaneProvider.get();
        zoomableScrollpane.init(fieldScrollPane, fieldPane, scrollAnchorPane);
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

    public void deleteSpectator(Member member) {
        Node removal = playerListView.getItems().stream().filter(node -> node.getId().equals(member.userId())).findAny().orElse(null);
        playerListView.getItems().remove(removal);
    }

    public void renderSpectator(Member member) {
        if(member.spectator()) {
            if(userService.getCurrentUser()._id().equals(member.userId())) {
                hammerImageView.setVisible(false);
                streetCountLabel.setVisible(false);
                houseCountLabel.setVisible(false);
                cityCountLabel.setVisible(false);
                streetSVG.setVisible(false);
                citySVG.setVisible(false);
                houseSVG.setVisible(false);
                tradeImageView.setVisible(false);
                hourglassImageView.setVisible(false);
                nextTurnImageView.setVisible(false);
            }

            IngamePlayerListSpectatorController spectatorListElement = spectatorProvider.get();
            spectatorListElement.setNodeListView(playerListView);
            spectatorListElement.init(game.get()._id(), member.userId());
            spectatorListElement.render(game.get().owner());
        }
    }

    private void deleteBuilding(Building building) {}

    private void handleGameState(State currentState) {
        // enable corresponding user to perform their action
        ExpectedMove move = currentState.expectedMoves().get(0);
        if (move.players().get(0).equals(userService.getCurrentUser()._id())) {
            // enable posting move
            switch (move.action()) {
                case FOUNDING_ROLL, ROLL -> this.enableRoll(move.action());
                case FOUNDING_SETTLEMENT_1, FOUNDING_SETTLEMENT_2 -> this.enableBuildingPoints(move.action());
                case FOUNDING_ROAD_1, FOUNDING_ROAD_2 -> this.enableStreetPoints(move.action());
                case BUILD -> {
                    // set builder timer, in progress...
                    this.timerService.setBuildTimer(new Timer());
                    this.enableEndTurn();
                    this.enableBuildingPoints(move.action());
                    this.enableStreetPoints(move.action());
                }
            }
        }
        this.setSituationLabel(move.players().get(0), move.action());
    }

    private void enableStreetPoints(String action) { this.boardController.enableStreetPoints(action); }

    private void enableBuildingPoints(String action) { this.boardController.enableBuildingPoints(action); }

    private void enableEndTurn() {
        this.turnPane.setOnMouseClicked(this::endTurn);
    }

    private void endTurn(MouseEvent mouseEvent) {
        final CreateMoveDto moveDto = new CreateMoveDto(BUILD, null);
        disposable.add(ingameService.postMove(game.get()._id(), moveDto)
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    this.turnPane.setOnMouseClicked(null);
                    this.timerService.reset();
                })
        );
    }
    private void setSituationLabel(String playerId, String action) {
        // set game state label
        String playerName;
        String actionString = "";
        switch (action) {
            case ROLL, FOUNDING_ROLL -> actionString = "roll the dice";
            case FOUNDING_ROAD_1, FOUNDING_ROAD_2 -> actionString = "place road";
            case FOUNDING_SETTLEMENT_1, FOUNDING_SETTLEMENT_2 -> actionString = "place settlement";
            case BUILD -> actionString = BUILD;
        }

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
        LobbyScreenController newLobbyController = lobbyScreenControllerProvider.get();
        if(game.get().owner().equals(userService.getCurrentUser()._id())) {
            gameChatController.sendMessage("Host left the Game!", game.get());
            disposable.add(gameService.deleteGame(game.get()._id())
                    .observeOn(FX_SCHEDULER)
                    .subscribe(res -> {
                        this.stop();
                        disposable.dispose();
                        if(!onClose) {
                            app.show(newLobbyController);
                        }
                    }, Throwable::printStackTrace));
        } else {
            leaveGameController.saveLeavedGame(this.game.get()._id(), users, myColor);
            this.stop();
            timerService.reset();
            disposable.dispose();
            if(!onClose) {
                app.show(newLobbyController);
            }
        }
    }

    public void toRules() {
        RulesScreenController rulesController = rulesScreenControllerProvider.get();
        rulesController.init();
    }

    public void toSettings() {
        SettingsScreenController settingsController = settingsScreenControllerProvider.get();
        settingsController.init();
    }

    @Override
    public void stop() {
        gameChatController.stop();
        settingsScreenControllerProvider.get().stop();
        timerService.reset();
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void loadMap() {
        this.ingameService.getMap(this.game.get()._id())
                .observeOn(FX_SCHEDULER)
                .doOnComplete(this::buildBoardUI)
                .subscribe();
    }
    private void buildBoardUI() {
        this.boardController.buildBoardUI();
    }

    public void selectStreet() {
        this.gameStorage.selectedBuilding = ROAD;
        this.roadFrame.setBackground(Background.fill(Color.rgb(0,100,0)));
        this.settlementFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
        this.cityFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
    }
    public void selectSettlement() {
        this.gameStorage.selectedBuilding = SETTLEMENT;
        this.settlementFrame.setBackground(Background.fill(Color.rgb(0,100,0)));
        this.roadFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
        this.cityFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
    }
    public void selectCity() {
        this.gameStorage.selectedBuilding = CITY;
        this.cityFrame.setBackground(Background.fill(Color.rgb(0,100,0)));
        this.settlementFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
        this.roadFrame.setBackground(Background.fill(Color.rgb(250,250,250)));
    }


}
