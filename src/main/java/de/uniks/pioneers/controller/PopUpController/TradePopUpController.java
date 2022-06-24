package de.uniks.pioneers.controller.PopUpController;


import de.uniks.pioneers.Main;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.image.ImageView;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class TradePopUpController {
    @FXML public AnchorPane root;
    @FXML public HBox subRoot;
    @FXML public VBox tradeBox;
    @FXML public HBox timerBox;
    @FXML public Label timer;
    @FXML public HBox offerBox;
    @FXML public Label offer;
    @FXML public HBox offerImages;
    @FXML public ImageView packeisResource;
    @FXML public ImageView fellResource;
    @FXML public ImageView fischResource;
    @FXML public ImageView kohleResource;
    @FXML public ImageView walknochenResource;
    @FXML public HBox spinnerBox;
    @FXML public Spinner packeisSpinner;
    @FXML public Spinner fellSpinner;
    @FXML public Spinner fischSpinner;
    @FXML public Spinner kohleSpinner;
    @FXML public Spinner walknochenSpinner;
    @FXML public HBox getBox;
    @FXML public Label get;
    @FXML public ImageView packeisResourceI;
    @FXML public ImageView fellResourceI;
    @FXML public ImageView fischResourceI;
    @FXML public ImageView kohleResourceI;
    @FXML public ImageView walknochenResourceI;
    @FXML public HBox spinnerBoxI;
    @FXML public Spinner packeisSpinnerI;
    @FXML public Spinner fellSpinnerI;
    @FXML public Spinner fischSpinnerI;
    @FXML public Spinner kohleSpinnerI;
    @FXML public Spinner walknochenSpinnerI;
    @FXML public HBox tradeButtons;
    @FXML public Button cancel;
    @FXML public Button offerToPlayers;
    @FXML public Button tradeWithBank;
    @FXML public VBox playerListBox;
    @FXML public ListView playerList;

    private SpinnerValueFactory<Integer> spinnerValueFactory;


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

    public void init(){
        root.getStylesheets().add("/de/uniks/pioneers/styles/SpinnerLowerArrowWidth.css");
        // setup spinners
        List<Spinner> spinners= new ArrayList<>();
        spinnerBox.getChildren().forEach(node -> spinners.add((Spinner) node));
        spinnerBoxI.getChildren().forEach(node -> spinners.add((Spinner) node));
        spinners.forEach(spinner -> {

            spinnerValueFactory= new SpinnerValueFactory<Integer>() {
                @Override
                public void decrement(int steps) {
                    int currentValue = getValue();
                    if (currentValue>0){
                        setValue(currentValue-1);
                    }
                }

                @Override
                public void increment(int steps) {
                    int currentValue = getValue();
                    setValue(currentValue+1);
                }
            };

            spinnerValueFactory.setValue(0);
            spinner.setValueFactory(spinnerValueFactory);
        });
    }










}
