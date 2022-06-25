package de.uniks.pioneers.controller.PopUpController;


import de.uniks.pioneers.Main;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.util.Pair;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class TradePopUpController {

    private final IngameService ingameService;
    @FXML
    public AnchorPane root;
    @FXML
    public HBox subRoot;
    @FXML
    public VBox tradeBox;
    @FXML
    public HBox timerBox;
    @FXML
    public Label timer;
    @FXML
    public HBox offerBox;
    @FXML
    public Label offer;
    @FXML
    public HBox offerImages;
    @FXML
    public ImageView packeisResource;
    @FXML
    public ImageView fellResource;
    @FXML
    public ImageView fischResource;
    @FXML
    public ImageView kohleResource;
    @FXML
    public ImageView walknochenResource;


    @FXML
    public HBox getBox;
    @FXML
    public Label get;
    @FXML
    public ImageView packeisResourceI;
    @FXML
    public ImageView fellResourceI;
    @FXML
    public ImageView fischResourceI;
    @FXML
    public ImageView kohleResourceI;
    @FXML
    public ImageView walknochenResourceI;


    @FXML
    public HBox tradeButtons;
    @FXML
    public Button cancel;
    @FXML
    public Button offerToPlayers;
    @FXML
    public Button tradeWithBank;
    @FXML
    public VBox playerListBox;
    @FXML
    public ListView playerList;

    @FXML
    public HBox spinnerBoxOffer;
    @FXML
    public HBox spinnerBoxGet;


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
            }
        }

        @Override
        public void increment(int steps) {

            setValue(getValue() + 1);
        }
    }


    @Inject
    public TradePopUpController(IngameService ingameService) {
        this.ingameService = ingameService;
    }


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

    public void init() {
        root.getStylesheets().add("/de/uniks/pioneers/styles/SpinnerLowerArrowWidth.css");

        // setup spinners
        spinnerBoxOffer.getChildren().forEach(node -> setupSpinner((Spinner) node));
        spinnerBoxGet.getChildren().forEach(node -> setupSpinner((Spinner) node));

        // create EventHandler for trade with bank
        EventHandler<MouseEvent> eventHandler = event -> {

        };

        tradeWithBank.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);


    }

    private void setupSpinner(Spinner spinner) {
        String id = spinner.getId();
        System.out.println(id);
        Pattern pattern = Pattern.compile("");
        Matcher matcher = pattern.matcher(id);
        System.out.println(matcher.matches());
        String type = id.substring(0,matcher.start());
        String offerXorGet = id.substring(matcher.end());
        Pair<String, String> spinnerTyp = new Pair<>(type,offerXorGet);
        System.out.println(spinnerTyp);
        TradeSpinnerFactory factory = new TradeSpinnerFactory(spinnerTyp, ingameService);
        factory.setValue(0);
        spinner.setValueFactory(factory);
    }


}
