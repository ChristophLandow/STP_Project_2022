package de.uniks.pioneers.controller.subcontroller;


import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.model.User;
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
import javafx.scene.shape.SVGPath;

import javax.inject.Inject;
import java.io.IOException;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class IngamePlayerListElementController {

    @FXML
    public HBox playerBox;
    @FXML
    public Circle playerColor;
    @FXML
    public ImageView playerAvatar;
    @FXML
    public SVGPath resourcesCards;
    @FXML
    public Label resourceCardsCount;
    @FXML
    public SVGPath developmentCards;
    @FXML
    public Label devolpmentCardsCount;
    @FXML
    public SVGPath cityImg;
    @FXML
    public Label cityCount;
    @FXML
    public SVGPath settlmentImg;
    @FXML
    public Label settlementCount;


    private final CompositeDisposable disposable = new CompositeDisposable();
    private final GameStorage gameStorage;
    private final UserService userService;
    private final EventListener eventListener;
    private Player toRender;
    public ListView<Node> nodeListView;
    boolean online;


    @Inject
    public IngamePlayerListElementController(GameStorage gameStorage, UserService userService, EventListener eventListener) {
        this.gameStorage = gameStorage;
        this.userService = userService;
        this.eventListener = eventListener;
    }

    public void stop(){
        disposable.dispose();
    }

    public void render(String playerId) {
        playerBox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
        toRender = gameStorage.players.get(playerId);
        Parent node;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/IngamePlayerListElement.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            node = loader.load();
            nodeListView.getItems().add(node);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // set values to gui elements
        playerColor.setFill(Paint.valueOf(toRender.color()));
        disposable.add(userService.getUserById(toRender.userId())
                .observeOn(FX_SCHEDULER)
                .subscribe(user -> {
                    playerAvatar = new ImageView(new Image(user.avatar()));
                    addUserListener(user._id());
                })
        );

        addPlayerListener();
    }

    private void addUserListener(String id) {
            String patternToObserveUser = String.format("users.%s.updated", id);
            disposable.add(eventListener.listen(patternToObserveUser, User.class)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(userEvent -> {
                        User userFromEvent = userEvent.data();
                        if (userFromEvent.status().equals("offline")) {
                            // should be changed
                            playerBox.setBackground(new Background(new BackgroundFill(Color.TOMATO, null, null)));
                            playerBox.setOpacity(40);
                            online=false;
                        }else if (userFromEvent.status().equals("online") && !online){
                            playerBox.setBackground(new Background(new BackgroundFill(Color.WHITE, null, null)));
                            playerBox.setOpacity(100);
                            online=true;
                        }
                    })
            );
        }
    }

    private void addPlayerListener() {
        // add listener for observable players list
        gameStorage.players.addListener((MapChangeListener<? super String, ? super Player>) c -> {
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
        int resoureceCount = resources.brick()+ resources.grain()+ resources.ore()+ resources.lumber()+ resources.wool();
        resourceCardsCount.setText(String.valueOf(resoureceCount));
        devolpmentCardsCount.setText(String.valueOf(resources.unknown()));



    }


}


