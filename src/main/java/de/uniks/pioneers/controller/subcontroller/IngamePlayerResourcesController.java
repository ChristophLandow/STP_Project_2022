package de.uniks.pioneers.controller.subcontroller;


import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.services.GameService;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;

import javax.inject.Inject;
import java.io.IOException;
import java.util.Objects;

public class IngamePlayerResourcesController {
    @FXML
    public HBox resourcesHBox;
    @FXML
    public ImageView fischResource;
    @FXML
    public Label fischCount;
    @FXML
    public ImageView packeisResource;
    @FXML
    public Label packeisCount;
    @FXML
    public ImageView fellResource;
    @FXML
    public Label fellCount;
    @FXML
    public ImageView kohleResource;
    @FXML
    public Label kohleCount;
    @FXML
    public ImageView walknochenResource;
    @FXML
    public Label walknochenCount;
    @FXML
    public Pane root;

    private final GameService gameService;
    private String me;

    @Inject
    public IngamePlayerResourcesController(GameService gameService) {
        this.gameService = gameService;
    }

    public void stop() {
        //remove listeners
    }

    public void render() {
        Parent node;
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/PlayerResourcesBox.fxml"));
        loader.setControllerFactory(c -> this);
        try {
            node = loader.load();
            root.getChildren().add(node);
            node.setLayoutX(20);
            node.setLayoutY(693);

        } catch (IOException e) {
            e.printStackTrace();
        }
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
        // set pngs to imageViews
        Image fischImg = new Image(Objects.requireNonNull(getClass().getResource("images/card_fish.png")).toString());
        fischResource.setImage(fischImg);
        Image packeisImg = new Image(Objects.requireNonNull(getClass().getResource("images/card_ice.png")).toString());
        packeisResource.setImage(packeisImg);
        Image fellImg = new Image(Objects.requireNonNull(getClass().getResource("images/card_polarbear.png")).toString());
        fellResource.setImage(fellImg);
        Image walknochenImg = new Image(Objects.requireNonNull(getClass().getResource("images/card_whale.png")).toString());
        walknochenResource.setImage(walknochenImg);
        Image kohleImg = new Image(Objects.requireNonNull(getClass().getResource("images/card_carbon.png")).toString());
        kohleResource.setImage(kohleImg);
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
        int ore = resources.ore() == null ? 0 : resources.ore();
        int lumber = resources.lumber() == null ? 0 : resources.lumber();
        int wool = resources.wool() == null ? 0 : resources.wool();
        int unknown = resources.unknown() == null ? 0 : resources.unknown();


        fischCount.setText(String.valueOf(brick));
        packeisCount.setText(String.valueOf(grain));
        fellCount.setText(String.valueOf(ore));
        kohleCount.setText(String.valueOf(lumber));
        walknochenCount.setText(String.valueOf(wool));
    }
}


