package de.uniks.pioneers.controller.subcontroller;


import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.UserService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;

import javax.inject.Inject;
import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class IngamePlayerResourcesController {

    @FXML public HBox resourcesHBox;
    private final GameService gameService;
    private String me;


    @Inject
    public IngamePlayerResourcesController(GameService gameService, UserService userService, EventListener eventListener) {
        this.gameService = gameService;
    }

    public void stop() {
        //remove listeners
    }

    public void init() {
        // set values to gui and setup listeners
        me = gameService.me.userId();
        Player toRender = gameService.players.get(me);
        setImages();
        setDataToElement(toRender);
        addPlayerListener();
    }

    private void setImages() {
        // 30 w
        // 45 h

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


    private void addPlayerListener() {
        // add listener for observable players list
        gameService.players.addListener((MapChangeListener<? super String, ? super Player>) c -> {
            String key = c.getKey();
            System.out.println("player map got updated");
            if (key.equals(me)) {
                if (c.wasRemoved() && c.wasAdded()) {
                    setDataToElement(c.getValueAdded());
                }
            }
        });
    }

    private void setDataToElement(Player valueAdded) {
        Resources resources = valueAdded.resources();
        int brick = resources.brick() == null ? 0 : resources.brick();
        int grain = resources.grain() == null ? 0 : resources.grain();
        int ore   = resources.ore()   == null ? 0 : resources.ore();
        int lumber = resources.lumber() == null ? 0 : resources.lumber();
        int wool = resources.wool()   == null ? 0 : resources.wool();
        int unknown = resources.unknown() == null ? 0 : resources.unknown();

        int resourceCount = brick + grain + ore + lumber + wool;

        resourceCardsCount.setText(String.valueOf(resourceCount));
        developmentCardsCount.setText(String.valueOf(unknown));
        cityCount.setText(String.valueOf(4 - valueAdded.remainingBuildings().city()));
        settlementCount.setText(String.valueOf(5 - valueAdded.remainingBuildings().settlement()));
    }
}


