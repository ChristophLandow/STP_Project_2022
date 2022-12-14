package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.PopUpController.AchievementPopUpController;
import de.uniks.pioneers.controller.PopUpController.TradeOfferPopUpController;
import de.uniks.pioneers.controller.PopUpController.TradePopUpController;
import de.uniks.pioneers.controller.subcontroller.*;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.services.*;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.canvas.Canvas;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.INGAME_SCREEN_TITLE;
import static de.uniks.pioneers.GameConstants.*;

public class IngameScreenController implements Controller {
    @FXML public Pane fieldPane, root, turnPane, roadFrame, settlementFrame, cityFrame, situationPane, hammerPane, leftPane, rightPane;
    @FXML public AnchorPane scrollAnchorPane;
    @FXML public ScrollPane fieldScrollPane, chatScrollPane, userScrollPane;
    @FXML public SVGPath streetSVG, houseSVG, citySVG;
    @FXML public Button rulesButton, leaveButton, settingsButton;
    @FXML public VBox messageVBox;
    @FXML public TextField sendMessageField;
    @FXML public Label streetCountLabel, houseCountLabel, cityCountLabel, timeLabel, situationLabel;
    @FXML public ImageView tradeImageView;
    @FXML public ImageView turnImageView;
    @FXML public ImageView nextTurnImageView;
    @FXML public ImageView leftDiceImageView;
    @FXML public ImageView rightDiceImageView;
    @FXML public ImageView hammerImageView;
    @FXML public ImageView leftView;
    @FXML public ImageView rightView;
    @FXML public ListView<Node> playerListView;
    @FXML public Rectangle downRectangle, upRectangle;
    @FXML public Canvas mapCanvas;

    @Inject GameChatController gameChatController;
    @Inject PrefService prefService;
    @Inject Provider<IngamePlayerListElementController> elementProvider;
    @Inject Provider<IngamePlayerListSpectatorController> spectatorProvider;
    @Inject Provider<IngamePlayerResourcesController> resourcesControllerProvider;
    @Inject Provider<StreetPointController> streetPointControllerProvider;
    @Inject Provider<ZoomableScrollPane> zoomableScrollPaneProvider;
    @Inject RobberController robberController;
    @Inject LeaveGameController leaveGameController;
    @Inject Provider<LobbyScreenController> lobbyScreenControllerProvider;
    @Inject Provider<RulesScreenController> rulesScreenControllerProvider;
    @Inject Provider<SettingsScreenController> settingsScreenControllerProvider;
    @Inject Provider<TradePopUpController> tradePopUpControllerProvider;
    @Inject Provider<TradeOfferPopUpController> tradeOfferPopUpControllerProvider;
    @Inject Provider<AchievementPopUpController> achievementsPopUpControllerProvider;
    @Inject EventListener eventListener;
    @Inject VictoryPointController victoryPointController;

    private AchievementPopUpController achievementPopUpController;
    public ZoomableScrollPane zoomableScrollPane;
    private final App app;
    private final GameService gameService;
    public final SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    private List<User> users;
    private final GameStorage gameStorage;
    private final ResourceService resourceService;
    private final MapRenderService mapRenderService;
    private final IngameService ingameService;
    private final UserService userService;
    private final TimerService timerService;
    private final SpeechService speechService;
    private final RobberService robberService;
    private final StylesService stylesService;
    private final AchievementService achievementService;
    private final BoardController boardController;
    private final DiceSubcontroller diceSubcontroller;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private IngameStateController ingameStateController;
    private IngamePlayerController ingamePlayerController;
    private final ChangeListener<Boolean> finishedMapRenderListener;
    private TradeOfferPopUpController tradeOfferPopUpController;
    public IngameDevelopmentCardController ingameDevelopmentCardController;
    public IngameSelectController ingameSelectController;
    private TradePopUpController tradePopUpController;

    @Inject
    public IngameScreenController(App app, DiceSubcontroller diceSubcontroller, IngameService ingameService, GameStorage gameStorage, UserService userService, ResourceService resourceService,
                                  GameService gameService, TimerService timerService, MapRenderService mapRenderService, RobberService robberService, SpeechService speechService, StylesService stylesService,
                                  AchievementService achievementService) {
        this.app = app;
        this.ingameService = ingameService;
        this.gameStorage = gameStorage;
        this.resourceService = resourceService;
        this.mapRenderService = mapRenderService;
        this.userService = userService;
        this.gameService = gameService;
        this.timerService = timerService;
        this.robberService = robberService;
        this.speechService = speechService;
        this.stylesService = stylesService;
        this.achievementService = achievementService;
        this.diceSubcontroller = diceSubcontroller;
        this.ingameSelectController = new IngameSelectController();
        this.boardController = new BoardController(ingameService, userService, ingameSelectController, gameStorage, gameService, resourceService, mapRenderService, robberService);
        this.boardController.game = game;

        finishedMapRenderListener = (observable, oldValue, newValue) -> {
            if (mapRenderService.isFinishedLoading().get()) Platform.runLater(this::initWhenMapFinishedRendering);
        };
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

        mapRenderService.setFinishedLoading(false);
        this.boardController.fieldPane = this.fieldPane;
        this.boardController.streetPointControllerProvider = this.streetPointControllerProvider;

        this.zoomableScrollPane = zoomableScrollPaneProvider.get();
        this.zoomableScrollPane.init(false, fieldScrollPane, scrollAnchorPane, fieldPane, mapCanvas);
        Platform.runLater(this.zoomableScrollPane::render);

        this.achievementPopUpController = achievementsPopUpControllerProvider.get();

        Node achievementPopUp = this.achievementPopUpController.render();
        if(achievementPopUp != null) {
            this.root.getChildren().add(achievementPopUp);
        }
        return view;
    }

    @Override
    public void init() {
        this.app.getStage().setOnCloseRequest(event -> {
            leaveGameController.setOnClose(true);
            leave();
            userService.editProfile(null, null, null, "offline").subscribe();
            Platform.exit();
            System.exit(0);
        });

        // set variables
        app.getStage().setTitle(INGAME_SCREEN_TITLE);
        gameService.game.set(game.get());

        // init game chat controller
        gameChatController.setChatScrollPane(this.chatScrollPane);
        gameChatController.setMessageText(this.sendMessageField);
        gameChatController.setMessageBox(this.messageVBox);
        gameChatController.setGame(this.game.get());
        gameChatController.setUsers(this.users);
        gameChatController.setIngameScreenController(this);
        gameChatController.render();
        gameChatController.init();

        ingameSelectController.init(gameStorage, ingameService, prefService, roadFrame, settlementFrame, cityFrame);
        leaveGameController.init(this, gameChatController);

        this.tradePopUpController = tradePopUpControllerProvider.get();

        this.mapRenderService.isFinishedLoading().addListener(finishedMapRenderListener);

        // set timeLabel of timer
        this.timerService.setTimeLabel(this.timeLabel);

        String localStyle = "/de/uniks/pioneers/styles/IngameScreen.css";
        String localStyleDark = "/de/uniks/pioneers/styles/DarkMode_IngameScreen.css";
        stylesService.setStyleSheets(this.app.getStage().getScene().getStylesheets(), localStyle, localStyleDark);
        ingameService.setActualIngameController(this);
    }

    private void initWhenMapFinishedRendering() {
        // set dice subcontroller
        this.diceSubcontroller.init();
        this.diceSubcontroller.setLeftDiceView(this.leftDiceImageView).setRightDiceView(this.rightDiceImageView);
        this.ingameDevelopmentCardController = new IngameDevelopmentCardController(app.getStage(), hammerPane, leftPane, rightPane, hammerImageView, leftView, rightView, timerService, ingameService, resourceService, gameService, userService, robberController, false);
        this.ingameStateController = new IngameStateController(userService, ingameService, timerService, boardController, turnPane, robberController, turnImageView, situationLabel, diceSubcontroller, game.get(), ingameSelectController, mapRenderService, robberService, speechService, ingameDevelopmentCardController, resourceService, tradePopUpController);
        this.timerService.init(ingameSelectController, ingameDevelopmentCardController);
        // init game attributes and event listeners
        gameService.initGame();

        // REST - get game state from server
        disposable.add(ingameService.getCurrentState(game.get()._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(ingameStateController::handleGameState));

        // init game state listener
        String patternToObserveGameState = String.format("games.%s.state.*", game.get()._id());
        disposable.add(eventListener.listen(patternToObserveGameState, State.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(gameEvent -> {
                    if (gameEvent.event().endsWith(".updated")) {
                        ingameStateController.handleGameState(gameEvent.data());
                    }
                })
        );

        ingamePlayerController = new IngamePlayerController(userService, leaveGameController, elementProvider, playerListView, spectatorProvider, game.get(), hammerImageView, streetCountLabel,
                houseCountLabel, cityCountLabel, streetSVG, citySVG, houseSVG, tradeImageView, turnImageView, nextTurnImageView);

        // add change listeners
        // players change listener
        gameService.loadPlayers(game.get());
        gameService.players.addListener((MapChangeListener<? super String, ? super Player>) c -> {
            if (c.wasAdded() && !c.wasRemoved()) {
                ingamePlayerController.renderPlayer(c.getValueAdded());
            }
        });

        victoryPointController.init(users, root, leaveGameController);

        gameService.members.addListener((ListChangeListener<? super Member>) c -> {
            c.next();
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(ingamePlayerController::renderSpectator);
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(ingamePlayerController::deleteSpectator);
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
            if (c.getKey().equals(ROAD)) {
                this.streetCountLabel.setText(c.getValueAdded().toString());
                this.achievementService.incrementProgress(ROAD_ACHIEVEMENT);
            }
            if (c.getKey().equals(SETTLEMENT)) {
                this.houseCountLabel.setText(c.getValueAdded().toString());
                if(c.getValueRemoved() > c.getValueAdded()) {
                    this.achievementService.incrementProgress(SETTLEMENT_ACHIEVEMENT);
                }
            }
            if (c.getKey().equals(CITY)) {
                this.cityCountLabel.setText(c.getValueAdded().toString());
                this.achievementService.incrementProgress(CITY_ACHIEVEMENT);
            }
        });

        // init controller for player resources box
        IngamePlayerResourcesController ingamePlayerResourcesController = resourcesControllerProvider.get();
        ingamePlayerResourcesController.root = this.root;
        ingamePlayerResourcesController.render();
        ingamePlayerResourcesController.init(ingameStateController);
        gameService.setResourceController(ingamePlayerResourcesController);

        // setup controller for trade offer controller
        tradeOfferPopUpController = tradeOfferPopUpControllerProvider.get();
        tradeOfferPopUpController.init();

        this.achievementPopUpController.init();
    }

    private void renderBuilding(Building building) {
        this.boardController.renderBuilding(building);
    }

    private void deleteBuilding(Building building) {
    }

    public App getApp() {
        return this.app;
    }

    public void setPlayerColor(String hexColor) {
        streetSVG.setFill(Paint.valueOf(hexColor));
        houseSVG.setFill(Color.WHITE);
        houseSVG.setStroke(Paint.valueOf(hexColor));
        houseSVG.setStrokeWidth(1.5);
        citySVG.setFill(Color.WHITE);
        citySVG.setStroke(Paint.valueOf(hexColor));
        citySVG.setStrokeWidth(2.0);
    }

    public void leave() {
        leaveGameController.leave();
        this.stop();
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
        this.mapRenderService.isFinishedLoading().removeListener(finishedMapRenderListener);
        gameChatController.stop();
        tradeOfferPopUpController.stop();
        settingsScreenControllerProvider.get().stop();
        this.fieldPane.getChildren().clear();
        this.mapRenderService.stop();
        this.boardController.stop();
        this.diceSubcontroller.stop();
        timerService.reset();
        mapRenderService.stop();
        boardController.stop();
        achievementPopUpController.stop();
    }

    public void setUsers(List<User> users) {
        this.users = users;
        this.gameService.setUsers(new ArrayList<>(users));
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

    public void openTradePopUp() {
        ExpectedMove expectedMove = ingameService.getExpectedMove();
        if (expectedMove.action().equals(BUILD) && Objects.requireNonNull(expectedMove.players().get(0)).equals(gameService.me)) {
            speechService.play(GameConstants.SPEECH_TRADE);
            tradePopUpController.show();
        }
    }
}