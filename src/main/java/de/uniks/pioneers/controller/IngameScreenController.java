package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.PopUpController.TradePopUpController;
import de.uniks.pioneers.controller.subcontroller.*;
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
import java.util.*;
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

    public ZoomableScrollPane zoomableScrollPane;
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
    @Inject EventListener eventListener;


    @Inject Provider<TradePopUpController> tradePopUpControllerProvider;
    private Stage popUpStage;

    private final GameService gameService;
    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    private List<User> users;
    private final App app;
    private final GameStorage gameStorage;
    private final MapRenderService mapRenderService;
    private final IngameService ingameService;
    private final UserService userService;
    private final TimerService timerService;
    private final BoardController boardController;
    private final DiceSubcontroller diceSubcontroller;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private IngameStateController ingameStateController;
    private IngamePlayerController ingamePlayerController;

    @Inject
    public IngameScreenController(App app, Provider<RobberController> robberControllerProvider, IngameService ingameService, GameStorage gameStorage, UserService userService,
                                  GameService gameService, TimerService timerService, MapRenderService mapRenderService) {
        this.app = app;
        this.ingameService = ingameService;
        this.gameStorage = gameStorage;
        this.mapRenderService = mapRenderService;
        this.userService = userService;
        this.gameService = gameService;
        this.timerService = timerService;
        this.diceSubcontroller = new DiceSubcontroller(robberControllerProvider, ingameService, gameService, prefService,timerService);
        this.boardController = new BoardController(ingameService, userService, game, gameStorage, mapRenderService);
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

        Thread waitForMapLoadedThread = new Thread(() -> {
            try{
                while(!this.mapRenderService.isFinishedLoading()){
                    Thread.sleep(300);
                }

                initWhenMapFinishedRendering();
            }
            catch (Exception e){
                e.printStackTrace();
            }
        });

        waitForMapLoadedThread.setDaemon(true);
        waitForMapLoadedThread.start();

        // set timeLabel of timer
        this.timerService.setTimeLabel(this.timeLabel);

        if(prefService.getDarkModeState()){
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/IngameScreen.css")));
            this.app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_IngameScreen.css");
        } else {
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_IngameScreen.css")));
            this.app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/IngameScreen.css");
        }
    }

    private void initWhenMapFinishedRendering(){
        // set dice subcontroller
        this.diceSubcontroller.init();
        this.diceSubcontroller.setLeftDiceView(this.leftDiceImageView).setRightDiceView(this.rightDiceImageView);

        this.ingameStateController = new IngameStateController(userService, ingameService, timerService, boardController, turnPane, hourglassImageView, situationLabel, diceSubcontroller, game.get());

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
            }});

        //this.loadSpectators();
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
            if(c.getKey().equals(ROAD)){this.streetCountLabel.setText(c.getValueAdded().toString());}
            if(c.getKey().equals(SETTLEMENT)){this.houseCountLabel.setText(c.getValueAdded().toString());}
            if(c.getKey().equals(CITY)){this.cityCountLabel.setText(c.getValueAdded().toString());}
        });
    }

    private void renderBuilding(Building building) {
        this.boardController.renderBuilding(building);
    }

    private void deleteBuilding(Building building) {}

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
        gameChatController.stop();
        if (this.popUpStage != null) {
            this.popUpStage.close();
        }
        settingsScreenControllerProvider.get().stop();
        this.fieldPane.getChildren().clear();
        this.mapRenderService.stop();
        this.boardController.stop();
        timerService.reset();
        mapRenderService.stop();
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

    public void openTradePopUp() {
        if (this.popUpStage == null) {
            popUpStage = new Stage();
            TradePopUpController tradePopUpController = tradePopUpControllerProvider.get();
            Parent root = tradePopUpController.render();
            tradePopUpController.init();
            Scene scene = new Scene(root);
            popUpStage.setScene(scene);
            popUpStage.show();
        } else {
            this.popUpStage.show();
            this.popUpStage.toFront();
        }

    }
}
