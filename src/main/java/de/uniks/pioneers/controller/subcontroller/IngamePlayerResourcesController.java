package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.services.GameService;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javax.inject.Inject;
import java.io.IOException;
import java.util.Objects;

public class IngamePlayerResourcesController {
    @FXML public HBox resourcesHBox;
    @FXML public ImageView fischResource;
    @FXML public Label fischCount;
    @FXML public ImageView packeisResource;
    @FXML public Label packeisCount;
    @FXML public ImageView fellResource;
    @FXML public Label fellCount;
    @FXML public ImageView kohleResource;
    @FXML public Label kohleCount;
    @FXML public ImageView walknochenResource;
    @FXML public Label walknochenCount;
    @FXML public Pane root;

    private final GameService gameService;

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

    public void init(Player me) {
        // set values to gui and setup listeners
        setImages();
        if(me != null) {
            initDataToElement(me);
        }

        this.fellCount.setTextFill(Color.BLACK);
        this.kohleCount.setTextFill(Color.BLACK);
        new ResourceAnimationController(root, gameService, this);
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

        resourcesHBox.getChildren().clear();
    }

    public void initDataToElement(Player me) {
        resourcesHBox.getChildren().clear();
        Resources resources = me.resources();

        int brick = resources.brick() == null ? 0 : resources.brick();
        int grain = resources.grain() == null ? 0 : resources.grain();
        int ore = resources.ore() == null ? 0 : resources.ore();
        int lumber = resources.lumber() == null ? 0 : resources.lumber();
        int wool = resources.wool() == null ? 0 : resources.wool();
        int unknown = resources.unknown() == null ? 0 : resources.unknown();

        if (lumber > 0) {
            resourcesHBox.getChildren().add(fischResource);
            resourcesHBox.getChildren().add(fischCount);
            fischCount.setLayoutX(fellResource.getLayoutX());
            fischCount.setLayoutY(fellResource.getLayoutY());
        }

        if (grain > 0) {
            resourcesHBox.getChildren().add(walknochenResource);
            resourcesHBox.getChildren().add(walknochenCount);
            walknochenCount.setLayoutX(walknochenResource.getLayoutX());
            walknochenCount.setLayoutY(walknochenResource.getLayoutY());
        }

        if (wool > 0) {
            resourcesHBox.getChildren().add(fellResource);
            resourcesHBox.getChildren().add(fellCount);
            fellCount.setLayoutX(fellResource.getLayoutX());
            fellCount.setLayoutY(fellResource.getLayoutY());
        }

        if (brick > 0) {
            resourcesHBox.getChildren().add(packeisResource);
            resourcesHBox.getChildren().add(packeisCount);
            packeisCount.setLayoutX(packeisCount.getLayoutX());
            packeisCount.setLayoutY(packeisCount.getLayoutY());
        }

        if (ore > 0) {
            resourcesHBox.getChildren().add(kohleResource);
            resourcesHBox.getChildren().add(kohleCount);
            kohleCount.setLayoutX(kohleResource.getLayoutX());
            kohleCount.setLayoutY(kohleResource.getLayoutY());
        }

        fischCount.setText(String.valueOf(lumber));
        packeisCount.setText(String.valueOf(brick));
        fellCount.setText(String.valueOf(wool));
        kohleCount.setText(String.valueOf(ore));
        walknochenCount.setText(String.valueOf(grain));
    }

    public void setBrickToElement() {
        resourcesHBox.getChildren().add(packeisResource);
        resourcesHBox.getChildren().add(packeisCount);
        packeisCount.setLayoutX(packeisCount.getLayoutX());
        packeisCount.setLayoutY(packeisCount.getLayoutY());
    }

    public void setBrickCount(int resCount) {
        packeisCount.setText(String.valueOf(resCount));
    }

    public void setGrainToElement() {
        resourcesHBox.getChildren().add(walknochenResource);
        resourcesHBox.getChildren().add(walknochenCount);
        walknochenCount.setLayoutX(walknochenResource.getLayoutX());
        walknochenCount.setLayoutY(walknochenResource.getLayoutY());
    }

    public void setGrainCount(int resCount) {
        walknochenCount.setText(String.valueOf(resCount));
    }

    public void setOreToElement() {
        resourcesHBox.getChildren().add(kohleResource);
        resourcesHBox.getChildren().add(kohleCount);
        kohleCount.setLayoutX(kohleResource.getLayoutX());
        kohleCount.setLayoutY(kohleResource.getLayoutY());
    }

    public void setOreCount(int resCount) {
        kohleCount.setText(String.valueOf(resCount));
    }

    public void setLumberToElement() {
        resourcesHBox.getChildren().add(fischResource);
        resourcesHBox.getChildren().add(fischCount);
        fischCount.setLayoutX(fellResource.getLayoutX());
        fischCount.setLayoutY(fellResource.getLayoutY());
    }

    public void setLumberCount(int resCount) {
        fischCount.setText(String.valueOf(resCount));
    }

    public void setWoolToElement() {
        resourcesHBox.getChildren().add(fellResource);
        resourcesHBox.getChildren().add(fellCount);
        fellCount.setLayoutX(fellResource.getLayoutX());
        fellCount.setLayoutY(fellResource.getLayoutY());
    }

    public void setWoolCount(int resCount) {
        fellCount.setText(String.valueOf(resCount));
    }
}


