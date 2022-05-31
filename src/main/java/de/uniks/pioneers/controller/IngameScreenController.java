package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.subcontroller.*;
import de.uniks.pioneers.services.BoardGenerator;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.IngameService;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
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
import javafx.scene.paint.Color;
import javafx.scene.paint.ImagePattern;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.layout.VBox;
import javafx.scene.shape.SVGPath;
import javafx.scene.paint.Paint;
import javafx.scene.text.Font;
import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.text.FontWeight;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.Constants.INGAME_SCREEN_TITLE;
import static de.uniks.pioneers.GameConstants.scale;

@Singleton
public class IngameScreenController implements Controller {
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
    @FXML public VBox userVBox;
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

    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    private int gameSize;
    private List<User> users;
    private final App app;
    private final Provider<RulesScreenController> rulesScreenControllerProvider;
    private final Provider<SettingsScreenController> settingsScreenControllerProvider;
    private final IngameService ingameService;
    private final ArrayList<HexTileController> tileControllers = new ArrayList<>();

    private final ArrayList<StreetPointController> streetControllers = new ArrayList<>();
    private final ArrayList<BuildingPointController> buildingControllers = new ArrayList<>();

    private final GameStorage gameStorage;
    @Inject
    Provider<GameChatController> gameChatControllerProvider;

    @Inject
    public IngameScreenController(App app, Provider<RulesScreenController> rulesScreenControllerProvider, Provider<SettingsScreenController> settingsScreenControllerProvider, IngameService ingameService, GameStorage gameStorage) {
        this.app = app;
        this.rulesScreenControllerProvider = rulesScreenControllerProvider;
        this.settingsScreenControllerProvider = settingsScreenControllerProvider;
        this.ingameService = ingameService;
        this.gameStorage = gameStorage;
    }
    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/IngameScreen.fxml"));
        loader.setControllerFactory(c->this);
        final Parent view;
        try {
            view =  loader.load();
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
    }

    private void swapTurnSymbol() {
        turnPane.getChildren().get(0).setVisible(!turnPane.getChildren().get(0).isVisible());
        turnPane.getChildren().get(1).setVisible(!turnPane.getChildren().get(1).isVisible());
    }

    public void setPlayerColor(String hexColor)
    {
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
        this.app.show(settingsScreenControllerProvider.get());
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

    public void loadMap(){

        if(this.game.get().members() > 4){this.gameSize = 3;}
        else{this.gameSize = 2;}

        this.ingameService.getMap(this.game.get()._id())
                .observeOn(FX_SCHEDULER)
                .doOnComplete(this::buildBoardUI)
                .subscribe();
    }
    private void buildBoardUI(){

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

            Label numberLabel = new Label();
            numberLabel.setText(Integer.toString(hexTile.number));
            numberLabel.setFont(Font.font("Arial", FontWeight.EXTRA_BOLD , 30));
            numberLabel.setTextFill(Color.rgb(255,0,0));
            numberLabel.setAlignment(Pos.CENTER);
            numberLabel.setLayoutX(hexTile.x + this.fieldPane.getPrefWidth() / 2);
            numberLabel.setLayoutY(hexTile.y + this.fieldPane.getPrefHeight() / 2);

            this.fieldPane.getChildren().add(hex);
            this.fieldPane.getChildren().add(numberLabel);
            this.tileControllers.add(new HexTileController(hexTile, hex));
        }
        for (HexTile edge : edges) {

            Circle circ = new Circle(2);
            circ.setFill(Color.rgb(255, 0, 0));

            circ.setLayoutX(edge.x + this.fieldPane.getPrefWidth() / 2);
            circ.setLayoutY(edge.y + this.fieldPane.getPrefHeight() / 2);
            this.fieldPane.getChildren().add(circ);
            this.streetControllers.add(new StreetPointController(edge, circ));
        }
        for (HexTile corner : corners) {

            Circle circ = new Circle(5);
            circ.setFill(Color.rgb(255, 0, 0));

            circ.setLayoutX(corner.x + this.fieldPane.getPrefWidth() / 2);
            circ.setLayoutY(corner.y + this.fieldPane.getPrefHeight() / 2);
            this.fieldPane.getChildren().add(circ);
            this.buildingControllers.add(new BuildingPointController(corner, circ));
        }
        for(HexTileController tile : tileControllers){

            tile.findEdges(this.streetControllers);
            tile.findCorners(this.buildingControllers);
            tile.link();
        }
    }
}
