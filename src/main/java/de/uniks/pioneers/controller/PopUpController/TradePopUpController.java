package de.uniks.pioneers.controller.PopUpController;


import de.uniks.pioneers.Main;
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

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;


public class TradePopUpController {
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

        @Override
        public void decrement(int steps) {

            if (getValue() > 0) {
                setValue(getValue() - 1);
            }
        }

        @Override
        public void increment(int steps) {

            setValue(getValue() + 1);
        }
    }


    @Inject
    public TradePopUpController() {

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
        Map<String, Spinner> spinners = new HashMap<>();
        spinnerBoxOffer.getChildren().forEach(node -> spinners.put(node.getId(), (Spinner) node));
        spinnerBoxGet.getChildren().forEach(node -> spinners.put(node.getId(), (Spinner) node));

        spinners.values().forEach(spinner -> {
            spinner.getId().f
            TradeSpinnerFactory factory = new TradeSpinnerFactory();
            factory.setValue(0);
            spinner.setValueFactory(factory);
        });


        EventHandler<MouseEvent> eventHandler = event -> {

        };

        tradeWithBank.addEventHandler(MouseEvent.MOUSE_CLICKED, eventHandler);


    }


}
