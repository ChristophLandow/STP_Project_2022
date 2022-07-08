package de.uniks.pioneers.controller.PopUpController;


import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.controller.PopUpController.ElementController.TradePopUpPlayerListElementController;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.IOException;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static de.uniks.pioneers.GameConstants.OFFER;


public class TradePopUpController implements Controller {

    @FXML public AnchorPane root;
    @FXML public HBox subRoot;
    @FXML public VBox tradeBox;
    @FXML public HBox timerBox;
    @FXML public Label timer;
    @FXML public HBox offerBox;
    @FXML public Label offer;
    @FXML public HBox offerImages;
    @FXML public HBox getBox;
    @FXML public Label get;
    @FXML public HBox tradeButtons;
    @FXML public Button cancel;
    @FXML public Button offerToPlayers;
    @FXML public Button tradeWithBank;
    @FXML public VBox playerListBox;
    @FXML public ListView<Node> playerList;
    @FXML public HBox spinnerBoxOffer;
    @FXML public HBox spinnerBoxGet;
    @FXML public HBox getImages;

    private final IngameService ingameService;
    private final GameService gameService;

    @Inject
    Provider <TradePopUpPlayerListElementController> elementControllerProvider;
    private EventHandler<MouseEvent> bankHandler;
    private EventHandler<MouseEvent> playerHandler;
    private EventHandler<MouseEvent> cancelHandler;

    private class TradeSpinnerFactory extends SpinnerValueFactory<Integer> {

        private final IngameService ingameService;
        private final Pair<String, String> types;

        public TradeSpinnerFactory(Pair<String, String> types, IngameService ingameService) {
            this.types = types;
            this.ingameService = ingameService;
        }

        @Override
        public void decrement(int steps) {
            if (getValue() > 0 ) {
                setValue(getValue() - 1);
                updateTrade(-1);
            }
        }

        @Override
        public void increment(int steps) {
            setValue(getValue() + 1);
            updateTrade(1);
        }

        private void updateTrade(int increment) {
            if (types.getKey().equals(OFFER)){
                ingameService.getOrCreateTrade(types.getValue(),-increment);
            }else {
                ingameService.getOrCreateTrade(types.getValue(),increment);
            }
        }
    }

    @Inject
    public TradePopUpController(IngameService ingameService, GameService gameService) {
        this.ingameService = ingameService;
        this.gameService = gameService;
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/PopUps/TradePopUp.fxml"));
        loader.setControllerFactory(c -> this);
        Parent node;
        try {
            node = loader.load();
        } catch (IOException e) {
            node = null;
        }
        return node;
    }

    @Override
    public void init() {
        // init model


        // set stylesheet for spinners
        root.getStylesheets().add("/de/uniks/pioneers/styles/SpinnerLowerArrowWidth.css");

        // setup player list
        gameService.players.values().forEach(player -> {
            TradePopUpPlayerListElementController elementController = elementControllerProvider.get();
            Parent node = elementController.render();
            elementController.init(player.userId());
            playerList.getItems().add(node);
        });

        // setup ImageViews
        List<String> subStrings = List.of("ice", "polarbear", "fish","carbon", "whale");
        Iterator<String> first = subStrings.iterator();
        Iterator<String> second = subStrings.iterator();

        offerImages.getChildren().forEach(node -> {
            ImageView imageView = (ImageView) node;
            String resourceURL = String.format("/de/uniks/pioneers/controller/subcontroller/images/card_%s.png",first.next());
            Image img = new Image(Objects.requireNonNull(getClass().getResource(resourceURL)).toString());
            imageView.setImage(img);
        });

        getImages.getChildren().forEach(node -> {
            ImageView imageView = (ImageView) node;
            String resourceURL = String.format("/de/uniks/pioneers/controller/subcontroller/images/card_%s.png",second.next());
            Image img = new Image(Objects.requireNonNull(getClass().getResource(resourceURL)).toString());
            imageView.setImage(img);
        });

        // setup spinners
        spinnerBoxOffer.getChildren().forEach(node -> setupSpinner((Spinner) node));
        spinnerBoxGet.getChildren().forEach(node -> setupSpinner((Spinner) node));

        // setup EventHandler for trade with bank
        bankHandler = event -> ingameService.tradeWithBank();

        // setup EventHandler for trade with player
        playerHandler = event -> ingameService.tradeWithPlayers();

        // setup EventHandler for cancel and stage closed
        cancelHandler = event -> {

        };

        // add event handler to buttons
        tradeWithBank.addEventHandler(MouseEvent.MOUSE_CLICKED, bankHandler);
        offerToPlayers.addEventHandler(MouseEvent.MOUSE_CLICKED, playerHandler);
        cancel.addEventHandler(MouseEvent.MOUSE_CLICKED, cancelHandler);

    }

    @Override
    public void stop() {
        tradeWithBank.removeEventHandler(MouseEvent.MOUSE_CLICKED,bankHandler);
        offerToPlayers.removeEventHandler(MouseEvent.MOUSE_CLICKED,playerHandler);
        cancel.removeEventHandler(MouseEvent.MOUSE_CLICKED,cancelHandler);
    }

    private void setupSpinner(Spinner spinner) {
        String id = spinner.getId();
        Pattern pattern = Pattern.compile("[A-Z]");
        Matcher matcher = pattern.matcher(id);

        int start=0;
        int end=0;
        if(matcher.find())
        {
            start = matcher.start();
            end = matcher.end();
        }

        String resourceTyp = id.substring(0,start);
        String offerXorGet = id.substring(end-1);
        Pair<String, String> spinnerTyp = new Pair<>(offerXorGet, resourceTyp);
        TradeSpinnerFactory factory = new TradeSpinnerFactory(spinnerTyp, ingameService);
        factory.setValue(0);
        spinner.setValueFactory(factory);
    }

}
