package de.uniks.pioneers.controller.subcontroller;


import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.GameStorage;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class IngamePlayerListElementController {

    @FXML
    public HBox playerBox;
    @FXML
    public Circle playerColor;
    @FXML
    public ImageView playerAvatar;
    @FXML
    public Label resourceCardsCount;
    @FXML
    public ImageView resourceCards;
    @FXML
    public Label developmentCardsCount;
    @FXML
    public ImageView developmentCards;
    @FXML
    public Label settlementCount;
    @FXML
    public ImageView settlement;
    @FXML
    public Label cityCount;
    @FXML
    public ImageView city;
    @FXML
    public Label playerName;
    @FXML
    private Player toRender;
    @FXML
    public ListView<Node> nodeListView;

    private final CompositeDisposable disposable = new CompositeDisposable();
    private final GameService gameService;
    private final UserService userService;
    private final EventListener eventListener;
    private boolean online;

    @Inject
    public IngamePlayerListElementController(GameService gameService, UserService userService, EventListener eventListener) {
        this.gameService = gameService;
        this.userService = userService;
        this.eventListener = eventListener;
    }

    public void stop() {
        disposable.dispose();
    }

    public void render(String playerId) {
        Parent node;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/IngamePlayerListElement.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            node = loader.load();
            nodeListView.getItems().add(node);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set values to gui and setup listeners
        toRender = gameService.players.get(playerId);
        playerColor.setFill(Paint.valueOf(toRender.color()));
        disposable.add(userService.getUserById(toRender.userId())
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    playerAvatar.setImage(new Image(user.avatar()));
                    playerName.setText(user.name());
                    addUserListener(user._id());
                })
        );

        setDataToElement(toRender);
        setImages();
        addPlayerListener();
    }

    private void setImages() {
        // set values to gui elements
        Image resourceImage = new Image(Objects.requireNonNull(getClass().getResource("images/card_question_mark.png")).toString());
        resourceCards.setImage(resourceImage);
        Image developmentImage = new Image(Objects.requireNonNull(getClass().getResource("images/card_hammer.png")).toString());
        developmentCards.setImage(developmentImage);
        Image cityImage = new Image(Objects.requireNonNull(getClass().getResource("images/steine_3.png")).toString());
        city.setImage(cityImage);
        Image settlementImage = new Image(Objects.requireNonNull(getClass().getResource("images/ruinsCorner.png")).toString());
        settlement.setImage(settlementImage);
    }

    // have to fix style
    private void addUserListener(String id) {
        String patternToObserveUser = String.format("users.%s.updated", id);
        System.out.println(patternToObserveUser);
        disposable.add(eventListener.listen(patternToObserveUser, User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(userEvent -> {
                    User userFromEvent = userEvent.data();
                    Background background = new Background(new BackgroundFill(Color.WHITE, null, null));
                    if (userFromEvent.status().equals("offline")) {
                        background = new Background(new BackgroundFill(Color.TOMATO, null, null));
                        playerBox.setBackground(background);
                        playerBox.setOpacity(40);
                        online = false;
                    } else if (userFromEvent.status().equals("online") && !online) {
                        playerBox.setBackground(background);
                        playerBox.setOpacity(100);
                        online = true;
                    }
                })
        );
    }


    private void addPlayerListener() {
        // add listener for observable players list
        gameService.players.addListener((MapChangeListener<? super String, ? super Player>) c -> {
            String key = c.getKey();
            if (key.equals(toRender.userId())) {
                if (c.wasRemoved()) {
                    nodeListView.getItems().remove(playerBox);
                } else {
                    setDataToElement(c.getValueAdded());
                }
            }
        });
    }

    private void setDataToElement(Player valueAdded) {
        Resources resources = valueAdded.resources();
        int resourceCount;
        try {
            resourceCount = resources.brick() + resources.grain() + resources.ore() + resources.lumber() + resources.wool();
        } catch (NullPointerException e) {
            resourceCount = 0;
        }
        resourceCardsCount.setText(String.valueOf(resourceCount));
        developmentCardsCount.setText(String.valueOf(resources.unknown()));
        cityCount.setText(String.valueOf(4 - valueAdded.remainingBuildings().city()));
        settlementCount.setText(String.valueOf(5 - valueAdded.remainingBuildings().settlement()));
    }
}


