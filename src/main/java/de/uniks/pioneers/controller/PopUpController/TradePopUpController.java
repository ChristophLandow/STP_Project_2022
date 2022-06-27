package de.uniks.pioneers.controller.PopUpController;


import de.uniks.pioneers.Main;
import de.uniks.pioneers.services.IngameService;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
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
import java.io.IOException;
import java.util.*;
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
    @FXML
    public HBox getImages;


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
                updateTrade();
            }
        }

        @Override
        public void increment(int steps) {
            setValue(getValue() + 1);
            updateTrade();
        }

        private void updateTrade() {
            if (types.getKey().equals("Offer")){
                System.out.println(types.getValue());
                ingameService.getOrCreateTrade(types.getValue(),-1);
            }else {
                ingameService.getOrCreateTrade(types.getValue(),1);
            }
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

        // create EventHandler for trade with bank
        EventHandler<MouseEvent> bankHandler = event -> {
            ingameService.tradeWithBank();
        };

        tradeWithBank.addEventHandler(MouseEvent.MOUSE_CLICKED, bankHandler);
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
        String offerXorGet = id.substring(end-1,id.length());
        Pair<String, String> spinnerTyp = new Pair<>(offerXorGet, resourceTyp);
        TradeSpinnerFactory factory = new TradeSpinnerFactory(spinnerTyp, ingameService);
        factory.setValue(0);
        spinner.setValueFactory(factory);
    }


}
