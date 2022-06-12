package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.*;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.services.*;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.TextField;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.INGAME_SCREEN_TITLE;
import static de.uniks.pioneers.GameConstants.*;

@Singleton
public class IngameScreenController implements Controller {
    private final GameService gameService;
    @FXML public Pane turnPane;
    @FXML public SVGPath streetSVG;
    @FXML public SVGPath houseSVG;
    @FXML public SVGPath citySVG;
    @FXML public Button rulesButton;
    @FXML public Pane fieldPane;
    @FXML public Button giveUpButton;
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

    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    private int gameSize;
    private List<User> users;
    private final App app;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;
    private final Provider<SettingsScreenController> settingsScreenControllerProvider;
    private final IngameService ingameService;
    private final ArrayList<HexTileController> tileControllers = new ArrayList<>();

    private final UserService userService;
    private final EventListener eventListener;
    private final DiceSubcontroller diceSubcontroller;

    private final ArrayList<BuildingPointController> buildingControllers = new ArrayList<>();
    private final HashMap<String, BuildingPointController> buildingPointControllerHashMap = new HashMap<>();
    private final HashMap<String, StreetPointController> streetPointControllerHashMap = new HashMap<>();
    private final ArrayList<StreetPointController> streetPointControllers = new ArrayList<>();
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final GameStorage gameStorage;

    @Inject
    Provider<GameChatController> gameChatControllerProvider;
    @Inject
    Provider<StreetPointController> streetPointControllerProvider;
    @Inject
    Provider<IngamePlayerListElementController> elementProvider;


    @Inject
    public IngameScreenController(App app,
                                  Provider<RulesScreenController> rulesScreenControllerProvider,
                                  Provider<SettingsScreenController> settingsScreenControllerProvider,
                                  IngameService ingameService, GameStorage gameStorage,
                                  UserService userService, GameService gameService,
                                  EventListener eventListener) {
        this.app = app;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
        this.settingsScreenControllerProvider = settingsScreenControllerProvider;
        this.ingameService = ingameService;
        this.gameStorage = gameStorage;
        this.userService = userService;
        this.eventListener = eventListener;
        this.gameService = gameService;
        this.diceSubcontroller = new DiceSubcontroller(ingameService, gameService);
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
        // set variables
        app.getStage().setTitle(INGAME_SCREEN_TITLE);
        gameService.game.set(game.get());

        // init game chat controller
        GameChatController gameChatController = gameChatControllerProvider.get()
                .setChatScrollPane(this.chatScrollPane)
                .setMessageText(this.sendMessageField)
                .setMessageBox(this.messageVBox)
                .setGame(this.game.get())
                .setUsers(this.users);
        gameChatController.render();
        gameChatController.init();

        // set dice subcontroller
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

        // add change listeners
        // players change listener
        gameService.players.addListener((MapChangeListener<? super String, ? super Player>) c -> {
            if (c.wasAdded()) {
                System.out.println("Player was added!");
                this.renderPlayer(c.getValueAdded());
            } else if (c.wasRemoved()) {
                this.deletePlayer(c.getValueRemoved());
            }
        });

        // buildings change listener
        gameService.buildings.addListener((ListChangeListener<? super Building>) c -> {
            c.next();
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(this::renderBuilding);
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(this::deleteBuilding);
            }
        });
    }

    private void deletePlayer(Player player) {
        // TODO: remove player from list
    }

    private void renderPlayer(Player player) {
        /*System.out.println("added player "+ player.userId());
        System.out.println("belongs to game:" + game.get()._id());*/
        IngamePlayerListElementController playerListElement = elementProvider.get();
        playerListElement.nodeListView = playerListView;
        playerListElement.render(player.userId());
    }

    private void renderBuilding(Building building) {
        System.out.println("building type: " + building.type());
        String coords = building.x() + " " + building.y() + " " + building.z() + " " + building.side();
        if (Objects.equals(building.type(), "settlement") || Objects.equals(building.type(), "city")) {
            // find corresponding buildingPointController
            BuildingPointController controller = buildingPointControllerHashMap.get(coords);
            controller.placeBuilding(building);
        } else {
            // find corresponding streetPointController
            StreetPointController controller = streetPointControllerHashMap.get(coords);
            controller.renderRoad(building);
        }
    }

    private void deleteBuilding(Building building) {
    }

    private void handleGameState(State currentState) {
        // enable corresponding user to perform their action
        ExpectedMove move = currentState.expectedMoves().get(0);

        this.setSituationLabel(move);

        /*System.out.println(userService.getCurrentUser()._id());
        System.out.println(gameService.me.userId());*/

        if (move.players().get(0).equals(userService.getCurrentUser()._id())) {
            disposable.add(ingameService.updatePlayer(game.get()._id(), userService.getCurrentUser()._id(),true)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(player -> System.out.println(player.resources())));


            // enable posting move
            System.out.println("It's your turn now!");
            switch (move.action()) {
                case FOUNDING_ROLL, ROLL -> this.enableRoll(move.action());
                case FOUNDING_SETTLEMENT_1, FOUNDING_SETTLEMENT_2 -> this.enableBuildingPoints(move.action());
                case FOUNDING_ROAD_1, FOUNDING_ROAD_2 -> this.enableStreetPoints(move.action());
                case BUILD -> this.enableEndTurn();
            }
        }
    }

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

    private void enableStreetPoints(String action) {
        for (StreetPointController controller : streetPointControllerHashMap.values()) {
            controller.init();
            controller.setAction(action);
        }
    }

    private void enableBuildingPoints(String action) {
        for (BuildingPointController controller : buildingPointControllerHashMap.values()) {
            controller.init();
            controller.setAction(action);
        }
    }

    private void setSituationLabel(ExpectedMove move) {
        // set game state label
        String playerName;
        // if this user is current player
        if (move.players().get(0).equals(userService.getCurrentUser()._id())) {
            playerName = "ME";
            this.hourglassImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("ingame/next.png")).toString()));
        } else {
            playerName = userService.getUserById(move.players().get(0)).blockingFirst().name();
            this.hourglassImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("ingame/sanduhr.png")).toString()));
        }
        this.situationLabel.setText(playerName + ":\n" + move.action());
    }

    private void enableRoll(String action) {
        // init dice subcontroller
        this.diceSubcontroller.setAction(action);
        this.diceSubcontroller.init();
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

    public void giveUp() {
    }

    public void toRules() {
        RulesScreenController rulesController = rulesScreenControllerProvider.get();
        rulesController.init();
    }

    public void toSettings() {
        SettingsScreenController settingsController = settingsScreenControllerProvider.get();
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
            Image image = new Image(Objects.requireNonNull(getClass().getResource("ingame/" + hexTile.type + ".png")).toString());
            hex.setFill(new ImagePattern(image));
            hex.setLayoutX(hexTile.x + this.fieldPane.getPrefWidth() / 2);
            hex.setLayoutY(-hexTile.y + this.fieldPane.getPrefHeight() / 2);
            this.fieldPane.getChildren().add(hex);

            if (!hexTile.type.equals("desert")) {
                String numberURL = "ingame/tile_" + hexTile.number + ".png";
                ImageView numberImage = new ImageView(Objects.requireNonNull(getClass().getResource(numberURL)).toString());
                numberImage.setLayoutX(hexTile.x + this.fieldPane.getPrefWidth() / 2 - 15);
                numberImage.setLayoutY(-hexTile.y + this.fieldPane.getPrefHeight() / 2 - 15);
                numberImage.setFitHeight(30);
                numberImage.setFitWidth(30);
                this.fieldPane.getChildren().add(numberImage);
            }
            this.tileControllers.add(new HexTileController(hexTile, hex));
        }

        for (HexTile edge : edges) {

            Circle circ = new Circle(3);
            circ.setFill(RED);

            circ.setLayoutX(edge.x + this.fieldPane.getPrefWidth() / 2);
            circ.setLayoutY(-edge.y + this.fieldPane.getPrefHeight() / 2);
            this.fieldPane.getChildren().add(circ);
            StreetPointController streetPointController = streetPointControllerProvider.get();
            streetPointController.post(edge, circ, this.fieldPane);
            streetPointControllers.add(streetPointController);
        }

        for (HexTile corner : corners) {

            Circle circ = new Circle(6);
            circ.setFill(RED);

            circ.setLayoutX(corner.x + this.fieldPane.getPrefWidth() / 2);
            circ.setLayoutY(-corner.y + this.fieldPane.getPrefHeight() / 2);
            this.fieldPane.getChildren().add(circ);
            this.buildingControllers.add(new BuildingPointController(corner, circ, ingameService, game.get()._id(), this.fieldPane));

        }
        for (HexTileController tile : tileControllers) {

            tile.findEdges(this.streetPointControllers);
            tile.findCorners(this.buildingControllers);
            tile.link();
        }
        for (BuildingPointController buildingPoint : this.buildingControllers) {
            // put buildingPointControllers in Hashmap to access with coordinates
            this.buildingPointControllerHashMap.put(
                    buildingPoint.generateKeyString(),
                    buildingPoint);
        }

        for (StreetPointController streetPoint : this.streetPointControllers) {
            // put buildingPointControllers in Hashmap to access with coordinates
            this.streetPointControllerHashMap.put(
                    streetPoint.generateKeyString(),
                    streetPoint);
        }

        loadSnowAnimation();
    }

    private void loadSnowAnimation() {
        new SnowAnimationControllor(fieldPane, buildingControllers, streetPointControllers);
    }
}
