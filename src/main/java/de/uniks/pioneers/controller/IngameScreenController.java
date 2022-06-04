package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.*;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.services.BoardGenerator;
import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Paint;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.INGAME_SCREEN_TITLE;
import static de.uniks.pioneers.GameConstants.*;

@Singleton
public class IngameScreenController implements Controller {
    @FXML
    public Pane turnPane;
    @FXML
    public SVGPath streetSVG;
    @FXML
    public SVGPath houseSVG;
    @FXML
    public SVGPath citySVG;
    @FXML
    public Button rulesButton;
    @FXML
    public Pane fieldPane;
    @FXML
    public Button giveUpButton;
    @FXML
    public Button settingsButton;
    @FXML
    public ScrollPane chatScrollPane;
    @FXML
    public VBox messageVBox;
    @FXML
    public TextField sendMessageField;
    @FXML
    public ScrollPane userScrollPane;
    @FXML
    public VBox userVBox;
    @FXML
    public Label streetCountLabel;
    @FXML
    public Label houseCountLabel;
    @FXML
    public Label cityCountLabel;
    @FXML
    public ImageView tradeImageView;
    @FXML
    public ImageView hourglassImageView;
    @FXML
    public ImageView nextTurnImageView;
    @FXML
    public Label timeLabel;
    @FXML
    public Pane situationPane;
    @FXML
    public Label situationLabel;
    @FXML
    public ImageView leftDiceImageView;
    @FXML
    public ImageView rightDiceImageView;
    @FXML
    public ImageView hammerImageView;

    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    private int gameSize;
    private List<User> users;
    private final App app;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;
    private final Provider<SettingsScreenController> settingsScreenControllerProvider;
    private final IngameService ingameService;
    private final UserService userService;
    private final ArrayList<HexTileController> tileControllers = new ArrayList<>();

    private final EventListener eventListener;
    private final ArrayList<StreetPointController> streetControllers = new ArrayList<>();
    private final ArrayList<BuildingPointController> buildingControllers = new ArrayList<>();
    private HashMap<String, BuildingPointController> buildingPointControllerHashMap = new HashMap<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final GameStorage gameStorage;
    @Inject
    Provider<GameChatController> gameChatControllerProvider;
    @Inject
    Provider<StreetPointController> streetPointControllerProvider;

    @Inject
    public IngameScreenController(App app,
                                  Provider<RulesScreenController> rulesScreenControllerProvider,
                                  Provider<SettingsScreenController> settingsScreenControllerProvider,
                                  IngameService ingameService, UserService userService,
                                  GameStorage gameStorage,
                                  EventListener eventListener) {
        this.app = app;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
        this.settingsScreenControllerProvider = settingsScreenControllerProvider;
        this.ingameService = ingameService;
        this.userService = userService;
        this.gameStorage = gameStorage;
        this.eventListener = eventListener;
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
        return view;
    }

    @Override
    public void init() {
        app.getStage().setTitle(INGAME_SCREEN_TITLE);

        // init game chat controller
        GameChatController gameChatController = gameChatControllerProvider.get()
                .setChatScrollPane(this.chatScrollPane)
                .setMessageText(this.sendMessageField)
                .setMessageBox(this.messageVBox)
                .setGame(this.game.get())
                .setUsers(this.users);
        gameChatController.render();
        gameChatController.init();


        // get current game state
        disposable.add(ingameService.getCurrentState(game.get()._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(state -> {
                    System.out.println(state);
                    handleGameState(state);
                }));

        // REST - get list of all players from server and set current player
        disposable.add(ingameService.getAllPlayers(game.get()._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(list -> {
                            gameStorage.players.setAll(list);
                            gameStorage.findMe();
                        }
                        , Throwable::printStackTrace));

        // Rest - get list of all buildings from server
        disposable.add(ingameService.getAllBuildings(game.get()._id())
                .observeOn(FX_SCHEDULER)
                .subscribe(list -> {
                            gameStorage.buildings.setAll(list);
                            gameStorage.findMe();
                        }
                        , Throwable::printStackTrace));

        // add listener for member observable
        gameStorage.buildings.addListener((ListChangeListener<? super Building>) c -> {
            c.next();
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(this::renderBuilding);
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(this::deleteBuilding);
            }
        });

        // init game listener
        initGameListener();
        initBuildingListener();
    }



    private void initGameListener() {
        // add game state listener
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
    }

    private void initBuildingListener() {
        // add buildingListener
        String patternToObserveBuildings = String.format("games.%s.buildings.*.*", game.get()._id());
        disposable.add(eventListener.listen(patternToObserveBuildings, Building.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(buildingEvent -> {
                    if (buildingEvent.event().endsWith(".created")) {
                        // render new building
                        System.out.println("new Building created: " + buildingEvent.data());
                        final Building building = buildingEvent.data();
                        gameStorage.buildings.add(building);
                    }
                }));
    }


    private void renderBuilding(Building building) {
        // find corresponding buildingPointController
        String coords = building.x() + " " + building.y() + " " + building.z();
        BuildingPointController controller = buildingPointControllerHashMap.get(coords);

        // set building on controller view
        controller.showBuilding(building);
    }

    private void deleteBuilding(Building building) {
    }

    private void handleGameState(State currentState) {
        // enable corresponding user to perform their action
        ExpectedMove move = currentState.expectedMoves().get(0);
        // update gameState in gameStorage
        gameStorage.currentState = currentState;


        if (move.players().get(0).equals(userService.getCurrentUser()._id())) {
            // enable posting move
            System.out.println("It's your turn now!");
            switch (move.action()) {
                case FOUNDING_ROLL:
                    this.enableFoundingRoll();
                    break;
                case FOUNDING_SETTLEMENT_1:
                case FOUNDING_SETTLEMENT_2:
                    // enable building points
                    for (BuildingPointController controller : buildingPointControllerHashMap.values()) {
                        controller.init();
                        controller.setAction(move.action());
                    }
                    break;
                case FOUNDING_ROAD_1:
                case FOUNDING_ROAD_2:
                    // enable road points
                    for (StreetPointController controller : streetControllers) {
                        controller.init();
                    }
                    break;
            }

        }
    }

    private void enableFoundingRoll() {
        // temporary solution!
        this.leftDiceImageView.setOnMouseClicked(this::foundingRoll);
    }

    private void foundingRoll(MouseEvent mouseEvent) {
        disposable.add(ingameService.postMove(game.get()._id(), new CreateMoveDto(FOUNDING_ROLL, null))
                .observeOn(FX_SCHEDULER)
                .subscribe(result -> {
                    // disable another roll
                    this.leftDiceImageView.setOnMouseClicked(null);
                }));
    }

    public App getApp() {
        return this.app;
    }


    private void swapTurnSymbol() {
        turnPane.getChildren().get(0).setVisible(!turnPane.getChildren().get(0).isVisible());
        turnPane.getChildren().get(1).setVisible(!turnPane.getChildren().get(1).isVisible());
    }

    public void setPlayerColor(String hexColor) {
        streetSVG.setFill(Paint.valueOf(hexColor));
        houseSVG.setFill(Paint.valueOf(hexColor));
        citySVG.setFill(Paint.valueOf(hexColor));
    }

    public void giveUp(ActionEvent actionEvent) {
    }

    public void toRules(ActionEvent actionEvent) {
        RulesScreenController rulesController = rulesScreenControllerProvider.get();
        rulesController.init();
    }

    public void toSettings(ActionEvent actionEvent) {
        SettingsScreenController settingsController = settingsScreenControllerProvider.get();
        settingsController.init();
    }

    public void sendMessage(KeyEvent keyEvent) {
    }

    public void onHammerPressed(MouseEvent mouseEvent) {
    }

    public void onStreetPressed(MouseEvent mouseEvent) {
    }

    public void onHousePressed(MouseEvent mouseEvent) {
    }

    public void onCityPressed(MouseEvent mouseEvent) {
    }

    public void onTradePressed(MouseEvent mouseEvent) {
    }

    public void onTurnPressed(MouseEvent mouseEvent) {
        // only for testing
        swapTurnSymbol();
    }

    @Override
    public void stop() {
    }

    public void setUsers(List<User> users) {
        this.users = users;
    }

    public void loadMap() {

        if (this.game.get().members() > 4) {
            this.gameSize = 3;
        } else {
            this.gameSize = 2;
        }

        this.ingameService.getMap(this.game.get()._id())
                .observeOn(FX_SCHEDULER)
                .doOnComplete(this::buildBoardUI)
                .subscribe();
    }

    private void buildBoardUI() {

        BoardGenerator generator = new BoardGenerator();
        List<HexTile> tiles = generator.generateTiles(this.gameStorage.getMap());
        List<HexTile> edges = generator.generateEdges(2 * this.gameSize + 1);
        List<HexTile> corners = generator.generateCorners(2 * this.gameSize + 1);

        for (HexTile hexTile : tiles) {

            Polygon hex = new Polygon();
            hex.getPoints().addAll(
                    0.0, 1.0,
                    Math.sqrt(3) / 2, 0.5,
                    Math.sqrt(3) / 2, -0.5,
                    0.0, -1.0,
                    -Math.sqrt(3) / 2, -0.5,
                    -Math.sqrt(3) / 2, 0.5);
            hex.setScaleX(scale);
            hex.setScaleY(scale);
            Image image = new Image(getClass().getResource("ingame/" + hexTile.type + ".png").toString());
            hex.setFill(new ImagePattern(image));
            hex.setLayoutX(hexTile.x + this.fieldPane.getPrefWidth() / 2);
            hex.setLayoutY(hexTile.y + this.fieldPane.getPrefHeight() / 2);
            this.fieldPane.getChildren().add(hex);

            if (!hexTile.type.equals("desert")) {
                String numberURL = "ingame/tile_" + hexTile.number + ".png";
                ImageView numberImage = new ImageView(getClass().getResource(numberURL).toString());
                numberImage.setLayoutX(hexTile.x + this.fieldPane.getPrefWidth() / 2 - 15);
                numberImage.setLayoutY(hexTile.y + this.fieldPane.getPrefHeight() / 2 - 15);
                numberImage.setFitHeight(30);
                numberImage.setFitWidth(30);
                this.fieldPane.getChildren().add(numberImage);
            }
            this.tileControllers.add(new HexTileController(hexTile, hex));
        }

        for (HexTile edge : edges) {

            Circle circ = new Circle(2);
            circ.setFill(RED);

            circ.setLayoutX(edge.x + this.fieldPane.getPrefWidth() / 2);
            circ.setLayoutY(edge.y + this.fieldPane.getPrefHeight() / 2);
            this.fieldPane.getChildren().add(circ);
            StreetPointController streetPointController = streetPointControllerProvider.get();
            streetPointController.post(edge,circ);
            this.streetControllers.add(streetPointController);
        }

        for (HexTile corner : corners) {

            Circle circ = new Circle(5);
            circ.setFill(RED);

            circ.setLayoutX(corner.x + this.fieldPane.getPrefWidth() / 2);
            circ.setLayoutY(corner.y + this.fieldPane.getPrefHeight() / 2);
            this.fieldPane.getChildren().add(circ);
            this.buildingControllers.add(new BuildingPointController(corner, circ, ingameService, game.get()._id(), this.fieldPane));

            // put buildingPointControllers in Hashmap to access with coordinates
            this.buildingPointControllerHashMap.put(
                    corner.generateKeyString(),
                    new BuildingPointController(corner, circ, ingameService, game.get()._id(), this.fieldPane)
            );
        }

        for (HexTileController tile : tileControllers) {

            tile.findEdges(this.streetControllers);
            tile.findCorners(this.buildingControllers);
            tile.link();
        }
    }
}
