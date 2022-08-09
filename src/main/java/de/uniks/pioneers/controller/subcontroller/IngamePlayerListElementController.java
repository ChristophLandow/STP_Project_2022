package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.services.GameService;
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
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;

public class IngamePlayerListElementController {
    @FXML public HBox playerBox;
    @FXML public Circle playerColor;
    @FXML public ImageView playerAvatar, resourceCards, developmentCards, settlement, knight, knightGold;
    @FXML public Label resourceCardsCount, knightCount, developmentCardsCount, settlementCount, playerName, victoryPointsLabel;
    @FXML private Player toRender;
    @FXML public ListView<Node> nodeListView;

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
                    if(!user.avatar().equals("")){
                    playerAvatar.setImage(new Image(user.avatar()));}
                    playerName.setText(user.name());
                    addUserListener(user._id());
                })
        );
        settlementCount.setVisible(false);
        setDataToElement(toRender);
        addPlayerListener();
        addArmyListener();
    }

    // have to fix style
    private void addUserListener(String id) {
        String patternToObserveUser = String.format("users.%s.updated", id);
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
                if (c.wasRemoved() && !c.wasAdded()) {
                    nodeListView.getItems().remove(playerBox);
                } else if (c.wasAdded() && c.wasRemoved()){
                    setDataToElement(c.getValueAdded());
                }
            }
            if (!toRender.userId().equals(gameService.largestArmy.get(LARGEST_ARMY).id())) {
                knight.setVisible(true);
                knightGold.setVisible(false);
            }
        });
    }

    private void addArmyListener() {
        gameService.largestArmy.addListener((MapChangeListener<? super String, ? super LargestArmy>) c -> {
            if (c.getValueAdded().id().equals(toRender.userId())) {
                knight.setVisible(false);
                knightGold.setVisible(true);
            } else {
                knight.setVisible(true);
                knightGold.setVisible(false);
            }
        });
    }

    private void setDataToElement(Player valueAdded) {
        Resources resources = valueAdded.resources();
        int resourceCount;

        if(valueAdded.userId().equals(userService.getCurrentUser()._id())) {
            resourceCount = resources.brick() + resources.grain() + resources.ore() + resources.lumber() + resources.wool();
        } else {
            resourceCount = resources.unknown();
        }

        knightCount.setText(String.valueOf(countArmy(valueAdded)));

        if (valueAdded.remainingBuildings().settlement()==0){
            settlementCount.setTextFill(Color.RED);
        }

        if (resourceCount>7){
            resourceCardsCount.setTextFill(Color.RED);
        } else {
            resourceCardsCount.setTextFill(Color.WHITE);
        }

        if (valueAdded.longestRoad() > 0) {
            settlementCount.setVisible(true);
            settlementCount.setText(String.valueOf(valueAdded.longestRoad()));
        }

        victoryPointsLabel.setText("" + valueAdded.victoryPoints());

        resourceCardsCount.setText(String.valueOf(resourceCount));
        developmentCardsCount.setText(String.valueOf(valueAdded.developmentCards().size()));
    }

    private int countArmy(Player valueAdded) {
        List<DevelopmentCard> devCards = valueAdded.developmentCards();
        int army = 0;

        for(DevelopmentCard devCard : devCards) {
            if(devCard.type().equals(DEV_KNIGHT) && devCard.revealed()) {
                army += 1;
            }
        }

        if(army >= 3 && army > gameService.largestArmy.get(LARGEST_ARMY).size()) {
            String oldId = gameService.largestArmy.get(LARGEST_ARMY).id();
            gameService.largestArmy.put(LARGEST_ARMY, new LargestArmy(valueAdded.userId(), oldId, army));
        }

        return army;
    }
}


