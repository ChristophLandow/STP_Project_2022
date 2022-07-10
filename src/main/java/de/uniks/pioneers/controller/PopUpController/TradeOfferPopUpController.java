package de.uniks.pioneers.controller.PopUpController;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.Property;
import javafx.beans.value.ChangeListener;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;


public class TradeOfferPopUpController implements Controller {

    @FXML
    public AnchorPane root;
    @FXML
    public HBox tradeBox;
    @FXML
    public VBox playerMe;
    @FXML
    public Label meLabel;
    @FXML
    public ImageView playerAvatar;
    @FXML
    public ImageView offerArrow;
    @FXML
    public HBox resourcesHBoxOffer;
    @FXML
    public HBox resourcesHBoxGet;
    @FXML
    public ImageView getArrow;
    @FXML
    public VBox playerOther;
    @FXML
    public Label otherLabel;
    @FXML
    public ImageView otherAvatar;
    @FXML
    public VBox buttonBox;
    @FXML
    public Button accept;
    @FXML
    public Button decline;

    private final IngameService ingameService;
    private final GameService gameService;
    private final UserService userService;
    private final CompositeDisposable disposable = new CompositeDisposable();

    private final App app;
    private Stage primaryStage;
    private Stage popUpStage;
    private Map<String, ImageView> imageMap;
    private EventHandler<MouseEvent> acceptHandler;
    private EventHandler<MouseEvent> declineHandler;
    private ChangeListener<Boolean> tradeOfferListener;
    private EventHandler<WindowEvent> closeStageHandler;


    @Inject
    public TradeOfferPopUpController(IngameService ingameService, GameService gameService, UserService userService, App app) {
        this.ingameService = ingameService;
        this.gameService = gameService;
        this.userService = userService;
        this.app = app;
    }

    @Override
    public void init() {
        // init stages
        primaryStage = app.getStage();
        popUpStage = new Stage();
        popUpStage.setTitle("trade offer");

        // create listener for trade offer
        tradeOfferListener = ((observable, oldValue, newValue) -> {
            if (oldValue.equals(false) && newValue.equals(true)) {
                show();
            } else if (oldValue.equals(true) && newValue.equals(false)) {
                accept.removeEventHandler(MouseEvent.MOUSE_CLICKED, acceptHandler);
                decline.removeEventHandler(MouseEvent.MOUSE_CLICKED, declineHandler);
                popUpStage.removeEventHandler(WindowEvent.ANY, closeStageHandler);
                popUpStage.close();
            }
        });

        // add listeners for trade is offered
        ingameService.tradeIsOffered.addListener(tradeOfferListener);
    }

    private void show() {
        Scene scene = app.getStage().getScene();
        Node node = scene.lookup("#situationPane");
        double x = primaryStage.getX() + node.getLayoutX() - 50;
        double y = primaryStage.getY() + node.getLayoutY() - 20;
        Parent view = render();
        build();
        Scene tradeOfferScene = new Scene(view);
        popUpStage.setX(x);
        popUpStage.setY(y);
        popUpStage.setScene(tradeOfferScene);
        popUpStage.show();
        popUpStage.toFront();
    }

    public void build() {
        // init me player view elements
        disposable.add(userService.getUserById(gameService.me)
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    if (!user.avatar().equals("")) {
                        playerAvatar.setImage(new Image(user.avatar()));
                    }
                })
        );

        // init other player view elements
        disposable.add(userService.getUserById(ingameService.tradeOffer.get().userId())
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    if (!user.avatar().equals("")) {
                        otherAvatar.setImage(new Image(user.avatar()));
                    }
                    otherLabel.setText(user.name());
                })
        );

        // create imageViews for different resource types
        List<String> subStrings = List.of("fish", "ice", "polarbear", "carbon", "whale");
        List<String> resStrings = List.of("lumber", "brick", "wool", "ore", "grain");
        Iterator<String> resIter = resStrings.iterator();
        imageMap = new HashMap<>();
        subStrings.forEach(s -> {
            String resourceURL = String.format("/de/uniks/pioneers/controller/subcontroller/images/card_%s.png", s);
            Image img = new Image(Objects.requireNonNull(getClass().getResource(resourceURL)).toString());
            ImageView imageView = new ImageView();
            imageView.setFitWidth(30);
            imageView.setFitHeight(45);
            imageView.setImage(img);
            imageMap.put(resIter.next(), imageView);
        });

        // add image view to offerBox xOr getBox according to resources from offer
        Resources trade = ingameService.tradeOffer.get().resources().normalize();
        System.out.println("trade offer: " + trade);
        Map<String, Integer> resources = trade.createMap();
        System.out.println("trade offer: " + resources);

        // label x-14, y-18 font 14px bold color white xor black
        resources.keySet().forEach(s -> {
            if (resources.get(s) > 0) {
                resourcesHBoxOffer.getChildren().add(imageMap.get(s));
            } else if (resources.get(s) < 0) {
                resourcesHBoxGet.getChildren().add(imageMap.get(s));
            }
        });

        // invoke event handlers for accept and decline trade offer
        acceptHandler = e -> ingameService.acceptOffer();
        declineHandler = e -> ingameService.tradeIsOffered.set(false);
        closeStageHandler = e -> ingameService.tradeIsOffered.set(false);


        // set handlers to buttons and stage
        accept.addEventHandler(MouseEvent.MOUSE_CLICKED, acceptHandler);
        decline.addEventHandler(MouseEvent.MOUSE_CLICKED, declineHandler);
        popUpStage.setOnCloseRequest(closeStageHandler);
    }

    @Override
    public void stop() {
        if (popUpStage.isShowing()) {
            popUpStage.close();
        }

        try {
            disposable.dispose();
            accept.removeEventHandler(MouseEvent.MOUSE_CLICKED, acceptHandler);
            decline.removeEventHandler(MouseEvent.MOUSE_CLICKED, declineHandler);
            popUpStage.removeEventHandler(WindowEvent.ANY, closeStageHandler);
            ingameService.tradeIsOffered.removeListener(tradeOfferListener);
        }catch (NullPointerException ignored){

        }
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/PopUps/TradeOfferPopUp.fxml"));
        loader.setControllerFactory(c -> this);
        Parent node;
        try {
            node = loader.load();
        } catch (IOException e) {
            node = null;
        }
        return node;
    }
}
