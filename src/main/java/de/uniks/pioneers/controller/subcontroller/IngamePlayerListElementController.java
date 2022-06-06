package de.uniks.pioneers.controller.subcontroller;


import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.services.GameStorage;
import javafx.collections.ListChangeListener;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.shape.Circle;
import javafx.scene.shape.SVGPath;

import javax.inject.Inject;
import java.io.IOException;

public class IngamePlayerListElementController {

    private final GameStorage gameStorage;
    @FXML public HBox playerBox;
    @FXML public Circle playerColor;
    @FXML public ImageView playerAvatar;
    @FXML public SVGPath resourcesCards;
    @FXML public Label resourceCardsCount;
    @FXML public SVGPath developmentCards;
    @FXML public Label devolpmentCardsCount;
    @FXML public SVGPath cityImg;
    @FXML public Label cityCount;
    @FXML public SVGPath settlmentImg;
    @FXML public Label settlementCount;


    @Inject
    public IngamePlayerListElementController(GameStorage gameStorage) {
        this.gameStorage = gameStorage;
    }

    public void render(ListView<Node> playerListView, String playerId) {
        HBox node;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/IngamePlayerListElement.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            node = loader.load();
            playerListView.getItems().add(node);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // add listener for observable buildings list
        gameStorage.players.addListener((MapChangeListener<? super String, ? super Player>) c -> {
            c.getKey()
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(this::renderBuilding);
            } else if (c.wasRemoved()) {
                c.getRemoved().forEach(this::deleteBuilding);
            }
        });


    }



}


