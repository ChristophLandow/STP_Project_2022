package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.services.GameService;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

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
    private Map<String, ImageView> imageMap;
    private Map<String, Label> labelMap;
    private ChangeListener<Boolean> enoughResourcesListener;
    private ResourceAnimationController resourceAnimationController;

    @Inject
    public IngamePlayerResourcesController(GameService gameService) {
        this.gameService = gameService;
    }

    public void stop() {
        //remove listeners
        gameService.notEnoughRessources.removeListener(enoughResourcesListener);
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
        if (me != null) {
            initDataToElement(me);
        }

        // add listener to ingame enough resources
        enoughResourcesListener = (observable, oldValue, newValue) -> {
            if (newValue.equals(true)) {
                showMissingRessources();
                gameService.notEnoughRessources.set(false);
            }
        };

        gameService.notEnoughRessources.addListener(enoughResourcesListener);

        this.fellCount.setTextFill(Color.BLACK);
        this.kohleCount.setTextFill(Color.BLACK);
        this.resourceAnimationController = new ResourceAnimationController(root, gameService, this);
    }

    private void showMissingRessources() {
        Map<String, Integer> missingResources = gameService.missingResources;
        missingResources.keySet().forEach(s -> {
            Integer delta = missingResources.get(s);
            if (delta < 0) {
                ImageView node = imageMap.get(s);
                Label label = labelMap.get(s);
                String oldValue = label.getText();
                ObjectProperty<Paint> color = label.textFillProperty();
                label.setTextFill(Color.RED);
                label.setLayoutX(node.getLayoutX());
                label.setLayoutY(node.getLayoutY());
                label.setText(String.valueOf(missingResources.get(s)));
                if (!resourcesHBox.getChildren().contains(node)) {
                    label.setTranslateX(-20);
                    resourceAnimationController.addFadingIn(node, resourcesHBox);
                    resourcesHBox.getChildren().add(label);
                    resourceAnimationController.textFillAnimation(label, resourcesHBox, color.get());
                } else {
                    label.setTranslateX(-5);
                    resourceAnimationController.textFillAnimation(label, oldValue,color.get());
                }
            }
        });
    }

    private void setImages() {
        //iterate over subString from URL to setup imageViews
        //iterate over resourceStrings to create a map with resourceName -> resourceImage
        List<String> subStrings = List.of("fish", "ice", "polarbear", "carbon", "whale");
        Iterator<String> iter = subStrings.iterator();
        List<String> resStrings = List.of("lumber", "brick", "wool", "ore", "grain");
        Iterator<String> resIter = resStrings.iterator();
        Iterator<String> resIterLabels = resStrings.iterator();
        imageMap = new HashMap<>();
        labelMap = new HashMap<>();

        resourcesHBox.getChildren().forEach(node -> {
            String id = node.getId();
            if (id.endsWith("Ressource")) {
                String url = String.format("images/card_%s.png", iter.next());
                Image img = new Image(Objects.requireNonNull(getClass().getResource(url)).toString());
                ImageView view = (ImageView) node;
                imageMap.put(resIter.next(), view);
                view.setImage(img);
            } else {
                Label resouceCount = (Label) node;
                labelMap.put(resIterLabels.next(), resouceCount);
            }
        });

        resourcesHBox.getChildren().clear();
    }

    public void initDataToElement(Player me) {
        resourcesHBox.getChildren().clear();
        Resources resources = me.resources();

        int brick = resources.brick();
        int grain = resources.grain();
        int ore = resources.ore();
        int lumber = resources.lumber();
        int wool = resources.wool();
        int unknown = resources.unknown();

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

    public void setBrickToElement(boolean add) {
        if(add) {
            resourcesHBox.getChildren().add(packeisResource);
            resourcesHBox.getChildren().add(packeisCount);
            packeisCount.setLayoutX(packeisCount.getLayoutX());
            packeisCount.setLayoutY(packeisCount.getLayoutY());
        } else {
            resourcesHBox.getChildren().remove(packeisResource);
            resourcesHBox.getChildren().remove(packeisCount);
        }
    }

    public void setBrickCount(int resCount) {
        packeisCount.setText(String.valueOf(resCount));
    }

    public void setGrainToElement(boolean add) {
        if(add) {
            resourcesHBox.getChildren().add(walknochenResource);
            resourcesHBox.getChildren().add(walknochenCount);
            walknochenCount.setLayoutX(walknochenResource.getLayoutX());
            walknochenCount.setLayoutY(walknochenResource.getLayoutY());
        } else {
            resourcesHBox.getChildren().remove(walknochenResource);
            resourcesHBox.getChildren().remove(walknochenCount);
        }
    }

    public void setGrainCount(int resCount) {
        walknochenCount.setText(String.valueOf(resCount));
    }

    public void setOreToElement(boolean add) {
        if(add) {
            resourcesHBox.getChildren().add(kohleResource);
            resourcesHBox.getChildren().add(kohleCount);
            kohleCount.setLayoutX(kohleResource.getLayoutX());
            kohleCount.setLayoutY(kohleResource.getLayoutY());
        } else {
            resourcesHBox.getChildren().remove(kohleResource);
            resourcesHBox.getChildren().remove(kohleCount);
        }
    }

    public void setOreCount(int resCount) {
        kohleCount.setText(String.valueOf(resCount));
    }

    public void setLumberToElement(boolean add) {
        if(add) {
            resourcesHBox.getChildren().add(fischResource);
            resourcesHBox.getChildren().add(fischCount);
            fischCount.setLayoutX(fellResource.getLayoutX());
            fischCount.setLayoutY(fellResource.getLayoutY());
        } else {
            resourcesHBox.getChildren().remove(fischResource);
            resourcesHBox.getChildren().remove(fischCount);
        }
    }

    public void setLumberCount(int resCount) {
        fischCount.setText(String.valueOf(resCount));
    }

    public void setWoolToElement(boolean add) {
        if(add) {
            resourcesHBox.getChildren().add(fellResource);
            resourcesHBox.getChildren().add(fellCount);
            fellCount.setLayoutX(fellResource.getLayoutX());
            fellCount.setLayoutY(fellResource.getLayoutY());
        } else {
            resourcesHBox.getChildren().remove(fellResource);
            resourcesHBox.getChildren().remove(fellCount);
        }
    }

    public void setWoolCount(int resCount) {
        fellCount.setText(String.valueOf(resCount));
    }
}


