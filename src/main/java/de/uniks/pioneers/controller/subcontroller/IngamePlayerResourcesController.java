package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.services.GameService;
import javafx.animation.*;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import javax.inject.Inject;
import java.io.IOException;
import java.util.*;

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
    private Map<String, ImageView> imageMap;
    private Map<String, Label> labelMap;
    private ChangeListener<Boolean> enoughResourcesListener;
    private MapChangeListener<String, Integer> mapChangeListener;
    private ResourceAnimationController resourceAnimationController;

    @Inject
    public IngamePlayerResourcesController(GameService gameService) {
        this.gameService = gameService;
    }

    public void stop() {
        //remove listeners
        gameService.myResources.removeListener(mapChangeListener);
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

        // add listener to model (myResources, enoughResources)
        enoughResourcesListener = (observable, oldValue, newValue) -> {
            if (newValue.equals(true)) {
                showMissingRessources();
                gameService.notEnoughRessources.set(false);
            }
        };

        mapChangeListener = new MapChangeListener<>() {
            @Override
            public void onChanged(Change<? extends String, ? extends Integer> change) {
                String type = change.getKey();
                if (change.wasAdded() && !change.wasRemoved()) {
                    invokeElement(type, change.getValueAdded());
                } else if (change.wasAdded() && change.wasRemoved()) {
                    if (change.getValueAdded() > 0 && change.getValueRemoved() == 0) {
                        invokeElement(type, change.getValueAdded());
                    } else if (change.getValueAdded() == 0 && change.getValueRemoved() > 0) {
                        revokeElement(type);
                    } else {
                        mutateElement(type, change.getValueAdded());
                    }
                }
            }
        };

        gameService.myResources.addListener(mapChangeListener);
        gameService.notEnoughRessources.addListener(enoughResourcesListener);
        this.resourceAnimationController = new ResourceAnimationController(root, gameService, this);
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

    private void invokeElement(String type, Integer valueAdded) {
        ImageView img = imageMap.get(type);
        Label lbl = labelMap.get(type);
        resourcesHBox.getChildren().add(img);
        resourcesHBox.getChildren().add(lbl);
        double x = img.getLayoutX();
        double y = img.getLayoutY();
        lbl.setLayoutX(x);
        lbl.setLayoutY(y);
        lbl.setText(String.valueOf(valueAdded));
    }

    private void revokeElement(String type) {
        ImageView img = imageMap.get(type);
        Label lbl = labelMap.get(type);
        resourcesHBox.getChildren().remove(img);
        resourcesHBox.getChildren().remove(lbl);
    }

    private void mutateElement(String type, Integer valueAdded) {
        Label lbl = labelMap.get(type);
        lbl.setText(String.valueOf(valueAdded));
    }

    private void showMissingRessources() {
        System.out.println("show missing resources");

        Map<String, Integer> missingResources = gameService.missingResources;
        ObservableMap<String, Integer> resources = gameService.myResources;
        missingResources.keySet().forEach(s -> {
            Integer delta = missingResources.get(s);
            Integer oldValue = resources.getOrDefault(s, 0);
            if (delta < 0) {
                ImageView node = imageMap.get(s);
                Label label = labelMap.get(s);
                Paint color = label.textFillProperty().get();
                label.setText(String.valueOf(missingResources.get(s)));
                label.setTextFill(Color.RED);
                if (resourcesHBox.getChildren().contains(node)) {
                    textFillAnimation(node, label, oldValue, color);
                } else {
                    Platform.runLater(() -> addFadingIn(node, label, resourcesHBox));
                    textFillAnimation(node, label, oldValue, color);
                }
            }
        });
    }

    public void addFadingIn(Node node, Label label, HBox parent) {
                FadeTransition transition = new FadeTransition(Duration.millis(1500), node);
                parent.getChildren().add(node);
                parent.getChildren().add(label);
                double x = node.getLayoutX();
                double y = node.getLayoutY();
                label.setLayoutX(x);
                label.setLayoutY(y);
                transition.setFromValue(0);
                transition.setToValue(1);
                transition.setInterpolator(Interpolator.EASE_IN);
                transition.setOnFinished(finish -> removeFadingOut(node));
    }

    public void removeFadingOut(Node node) {
                FadeTransition transition = new FadeTransition(Duration.millis(1500), node);
                transition.setFromValue(1);
                transition.setToValue(0);
                transition.setInterpolator(Interpolator.EASE_OUT);
                transition.play();
    }

    private void textFillAnimation(ImageView node, Label label, Integer oldValue, Paint color) {
        label.setTranslateX(-19);
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0)),
                new KeyFrame(Duration.seconds(1.5))
        );
        timeline.setAutoReverse(true);
        timeline.setCycleCount(1);
        timeline.setOnFinished(e -> {
            if (oldValue == 0) {
                resourcesHBox.getChildren().remove(node);
                resourcesHBox.getChildren().remove(label);
            }
            label.setTranslateX(-14);
            label.setText(String.valueOf(oldValue));
            label.setTextFill(color);
        });
        timeline.play();
    }
}


