package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
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
import javafx.scene.Scene;
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
import javafx.stage.Stage;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.INGAME_SCREEN_TITLE;
import static de.uniks.pioneers.GameConstants.*;

public class IngameScreenController implements Controller {
    @FXML public Pane fieldPane, root, turnPane, roadFrame, settlementFrame, cityFrame, situationPane;
    @FXML public AnchorPane scrollAnchorPane;
    @FXML public ScrollPane fieldScrollPane, chatScrollPane, userScrollPane;
    @FXML public SVGPath streetSVG, houseSVG, citySVG;
    @FXML public Button rulesButton, leaveButton, settingsButton;
    @FXML public VBox messageVBox;
    @FXML public TextField sendMessageField;
    @FXML public Label streetCountLabel, houseCountLabel, cityCountLabel, timeLabel, situationLabel;
    @FXML public ImageView tradeImageView, hourglassImageView, nextTurnImageView, leftDiceImageView, rightDiceImageView, hammerImageView;
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
    @Inject Provider<RobberController> robberControllerProvider;
    @Inject LeaveGameController leaveGameController;
    @Inject Provider<LobbyScreenController> lobbyScreenControllerProvider;
    @Inject Provider<RulesScreenController> rulesScreenControllerProvider;
    @Inject Provider<SettingsScreenController> settingsScreenControllerProvider;
    @Inject Provider<TradePopUpController> tradePopUpControllerProvider;
    @Inject Provider<TradeOfferPopUpController> tradeOfferPopUpControllerProvider;
    @Inject EventListener eventListener;
    @Inject VictoryPointController victoryPointController;

    public ZoomableScrollPane zoomableScrollPane;
    private final App app;
    private Stage popUpStage;
    private final GameService gameService;
    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    private List<User> users;
    private final GameStorage gameStorage;
    private final MapRenderService mapRenderService;
    private final IngameService ingameService;
    private final UserService userService;
    private final TimerService timerService;
    private final SpeechService speechService;
    private final RobberService robberService;
    private final BoardController boardController;
    private final DiceSubcontroller diceSubcontroller;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private IngameStateController ingameStateController;
    private IngamePlayerController ingamePlayerController;
    private ChangeListener<Boolean> tradeOfferListener;
    private final ChangeListener<Boolean> finishedMapRenderListener;

    @Inject
    public IngameScreenController(App app, Provider<RobberController> robberControllerProvider, IngameService ingameService, GameStorage gameStorage, UserService userService,
                                  GameService gameService, TimerService timerService, MapRenderService mapRenderService, RobberService robberService, SpeechService speechService) {
        this.app = app;
        this.ingameService = ingameService;
        this.gameStorage = gameStorage;
        this.mapRenderService = mapRenderService;
        this.userService = userService;
        this.gameService = gameService;
        this.timerService = timerService;
        this.robberService = robberService;
        this.speechService = speechService;
        this.diceSubcontroller = new DiceSubcontroller(robberControllerProvider, ingameService, gameService, prefService, timerService, robberService);
        this.boardController = new BoardController(ingameService, userService, game, gameStorage, gameService, mapRenderService);

        finishedMapRenderListener = (observable, oldValue, newValue) -> {
            if (mapRenderService.isFinishedLoading().get()) initWhenMapFinishedRendering();
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
        this.zoomableScrollPane.init(fieldScrollPane, scrollAnchorPane, fieldPane, mapCanvas);

        Platform.runLater(this.zoomableScrollPane::render);

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
        gameChatController
                .setChatScrollPane(this.chatScrollPane)
                .setMessageText(this.sendMessageField)
                .setMessageBox(this.messageVBox)
                .setGame(this.game.get())
                .setUsers(this.users)
                .setIngameScreenController(this);
        gameChatController.render();
        gameChatController.init();

        new IngameSelectController(gameStorage, roadFrame, settlementFrame, cityFrame, streetSVG, houseSVG, citySVG);
        leaveGameController.init(this, gameChatController);

        this.mapRenderService.isFinishedLoading().addListener(finishedMapRenderListener);

        // set timeLabel of timer
        this.timerService.setTimeLabel(this.timeLabel);

        if (prefService.getDarkModeState()) {
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/IngameScreen.css")));
            this.app.getStage().getScene().getStylesheets().add("/de/uniks/pioneers/styles/DarkMode_IngameScreen.css");
        } else {
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_IngameScreen.css")));
            this.app.getStage().getScene().getStylesheets().add("/de/uniks/pioneers/styles/IngameScreen.css");
        }
    }

    private void initWhenMapFinishedRendering() {
        // set dice subcontroller
        this.diceSubcontroller.init();
        this.diceSubcontroller.setLeftDiceView(this.leftDiceImageView).setRightDiceView(this.rightDiceImageView);
        this.ingameStateController = new IngameStateController(userService, ingameService, timerService, boardController, turnPane, hourglassImageView, situationLabel, diceSubcontroller, game.get(), mapRenderService, robberService, speechService);

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

        Platform.runLater(() -> {
            // init controller for player resources box
            IngamePlayerResourcesController ingamePlayerResourcesController = resourcesControllerProvider.get();
            ingamePlayerResourcesController.root = this.root;
            ingamePlayerResourcesController.render();
            ingamePlayerResourcesController.init(gameService.players.get(gameService.me));
        });

        ingamePlayerController = new IngamePlayerController(userService, leaveGameController, elementProvider, playerListView, spectatorProvider, game.get(), hammerImageView, streetCountLabel,
                houseCountLabel, cityCountLabel, streetSVG, citySVG, houseSVG, tradeImageView, hourglassImageView, nextTurnImageView);

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
            }
            if (c.getKey().equals(SETTLEMENT)) {
                this.houseCountLabel.setText(c.getValueAdded().toString());
            }
            if (c.getKey().equals(CITY)) {
                this.cityCountLabel.setText(c.getValueAdded().toString());
            }
        });

        // init listener for incoming trade offer
        tradeOfferListener = ((observable, oldValue, newValue) -> {
            if (oldValue.equals(false) && newValue.equals(true)) {
                openTradeOfferPopUp();
            } else if (oldValue.equals(true) && newValue.equals(false)) {
                closePopUpStage();
            }
        });

        ingameService.tradeIsOffered.addListener(tradeOfferListener);
    }

    private void renderBuilding(Building building) {
        this.boardController.renderBuilding(building);
    }

    private void deleteBuilding(Building building) {
    }

    public App getApp() {
        return this.app;
    }

    private void closePopUpStage() {
        popUpController.stop();
        popUpStage.close();
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
        if (this.popUpStage != null) {
            this.popUpStage.close();
        }
        settingsScreenControllerProvider.get().stop();
        this.fieldPane.getChildren().clear();
        this.mapRenderService.stop();
        this.boardController.stop();
        this.diceSubcontroller.stop();
        timerService.reset();
        mapRenderService.stop();
        ingameService.tradeIsOffered.removeListener(tradeOfferListener);
        boardController.stop();
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

    Controller popUpController;

    private void openTradeOfferPopUp() {
        popUpStage = new Stage();
        popUpStage.setTitle("trade offer");
        popUpController = tradeOfferPopUpControllerProvider.get();
        showPopUpStage();
    }

    private void showPopUpStage() {
        Parent root = popUpController.render();
        popUpController.init();
        Scene scene = new Scene(root);
        popUpStage.setScene(scene);
        popUpStage.show();
    }

    public void openTradePopUp() {
        if (this.popUpStage == null) {
            popUpStage = new Stage();
            popUpStage.setTitle("Pioneers - trade");
            popUpController = tradePopUpControllerProvider.get();
            showPopUpStage();
        } else {
            this.popUpStage.show();
            this.popUpStage.toFront();
        }
    }
}