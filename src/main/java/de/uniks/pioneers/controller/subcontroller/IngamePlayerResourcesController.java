package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.ResourceService;
import de.uniks.pioneers.services.UserService;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
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

import static de.uniks.pioneers.GameConstants.*;

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
    private final ResourceService resourceService;
    private final UserService userService;
    private Map<String, Label> resourceLabelMap;
    private Map<String, Pane> resourcePaneMap;
    private Map<String, Label> devLabelMap;
    private Map<String, Pane> devPaneMap;
    private ChangeListener<Boolean> enoughResourcesListener;
    private MapChangeListener<String, Integer> resourceMapChangeListener;
    private MapChangeListener<String, Integer> devCardMapChangeListener;
    private ResourceAnimationController resourceAnimationController;

    @Inject
    public IngamePlayerResourcesController(GameService gameService, ResourceService resourceService, UserService userService) {
        this.gameService = gameService;
        this.resourceService = resourceService;
        this.userService = userService;
    }

    public void stop() {
        //remove listeners
        resourceService.myResources.removeListener(resourceMapChangeListener);
        resourceService.myDevCards.removeListener(devCardMapChangeListener);
        resourceService.notEnoughResources.removeListener(enoughResourcesListener);
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

    public void init(IngameStateController ingameStateController) {
        // set values to gui and setup listeners
        setImages();

        // add listener to model (myResources, enoughResources)
        enoughResourcesListener = (observable, oldValue, newValue) -> {
            if (newValue.equals(true)) {
                showMissingResources();
                resourceService.notEnoughResources.set(false);
            }
        };

        resourceMapChangeListener = change -> {
            String type = change.getKey();
            if (change.wasAdded() && change.wasRemoved()) {
                if (change.getValueAdded() > 0 && change.getValueRemoved() == 0) {
                    invokeResourceElement(type, change.getValueAdded());
                } else if (change.getValueAdded() == 0 && change.getValueRemoved() > 0) {
                    revokeResourceElement(type);
                } else {
                    mutateResourceElement(type, change.getValueAdded());
                }
            }
        };

        devCardMapChangeListener = change -> {
            String type = change.getKey();
            if (change.wasAdded() && change.wasRemoved()) {
                if (change.getValueAdded() > 0 && change.getValueRemoved() == 0) {
                    invokeDevCardElement(type, change.getValueAdded());
                } else if (change.getValueAdded() == 0 && change.getValueRemoved() > 0) {
                    revokeDevCardElement(type);
                } else {
                    mutateDevCardElement(type, change.getValueAdded());
                }
            }
        };

        resourceService.myResources.addListener(resourceMapChangeListener);
        resourceService.myDevCards.addListener(devCardMapChangeListener);
        resourceService.notEnoughResources.addListener(enoughResourcesListener);
        this.resourceAnimationController = new ResourceAnimationController(root, gameService, resourceService, userService, ingameStateController);
    }

    private void setImages() {
        //iterate over subStrings from URL to setup imageViews
        //iterate over resourceStrings to create a map with resourceName -> resourceImage
        List<String> subStrings = List.of("fish", "ice", "polarbear", "carbon", "whale");
        Iterator<String> iter = subStrings.iterator();
        List<String> resStrings = List.of("lumber", "brick", "wool", "ore", "grain");
        Iterator<String> resIter = resStrings.iterator();
        Iterator<String> resIterLabels = resStrings.iterator();

        List<String> devStrings = List.of(DEV_KNIGHT, DEV_ROAD, DEV_PLENTY, DEV_MONOPOLY, DEV_VPOINT);
        Iterator<String> devIter = devStrings.iterator();
        Iterator<String> devCountIter = devStrings.iterator();

        resourceLabelMap = new HashMap<>();
        resourcePaneMap = new HashMap<>();
        devLabelMap = new HashMap<>();
        devPaneMap = new HashMap<>();

        resourcesHBox.getChildren().forEach(paneNode -> {
            Pane pane = (Pane) paneNode;
            pane.getChildren().forEach(node -> {
                String id = node.getId();
                if (id.endsWith("Resource")) {
                    String res = resIter.next();
                    String url = String.format("images/card_%s.png", iter.next());
                    Image img = new Image(Objects.requireNonNull(getClass().getResource(url)).toString());
                    ImageView view = (ImageView) node;
                    view.setImage(img);
                    resourcePaneMap.put(res, pane);
                } else if (id.endsWith("Count")) {
                    Label resouceCount = (Label) node;
                    resourceLabelMap.put(resIterLabels.next(), resouceCount);
                } else if (id.endsWith("Card")) {
                    String devCard = devIter.next();
                    String url = String.format("images/card_%s.png", devCard);
                    Image img = new Image(Objects.requireNonNull(getClass().getResource(url)).toString());
                    ImageView view = (ImageView) node;
                    view.setImage(img);
                    view.setScaleY(1.25);
                    devPaneMap.put(devCard, pane);
                } else if (id.endsWith("Label")) {
                    Label devCount = (Label) node;
                    devLabelMap.put(devCountIter.next(), devCount);
                }
            });
        });
        resourcesHBox.getChildren().clear();
    }

    private void invokeResourceElement(String type, Integer valueAdded) {
        Label lbl = resourceLabelMap.get(type);
        resourcesHBox.getChildren().add(resourcePaneMap.get(type));
        lbl.setText(String.valueOf(valueAdded));
    }

    private void revokeResourceElement(String type) {
        resourcesHBox.getChildren().remove(resourcePaneMap.get(type));
    }

    private void mutateResourceElement(String type, Integer valueAdded) {
        Label lbl = resourceLabelMap.get(type);
        lbl.setText(String.valueOf(valueAdded));
    }

    private void invokeDevCardElement(String type, Integer valueAdded) {
        Label lbl = devLabelMap.get(type);
        resourcesHBox.getChildren().add(devPaneMap.get(type));
        lbl.setText(String.valueOf(valueAdded));
    }

    private void revokeDevCardElement(String type) {
        resourcesHBox.getChildren().remove(devPaneMap.get(type));
    }

    private void mutateDevCardElement(String type, Integer valueAdded) {
        Label lbl = devLabelMap.get(type);
        lbl.setText(String.valueOf(valueAdded));
    }

    private void showMissingResources() {
        Map<String, Integer> missingResources = resourceService.missingResources;
        ObservableMap<String, Integer> resources = resourceService.myResources;
        missingResources.keySet().forEach(s -> {
            Integer delta = missingResources.get(s);
            Integer oldValue = resources.getOrDefault(s, 0);
            if (delta < 0) {
                Pane pane = resourcePaneMap.get(s);
                Label label = resourceLabelMap.get(s);
                Paint color = label.textFillProperty().get();
                label.setText(String.valueOf(missingResources.get(s)));
                label.setTextFill(Color.RED);
                if (resourcesHBox.getChildren().contains(pane)) {
                    resourceAnimationController.textFillAnimation(pane, label, oldValue, color, resourcesHBox);
                } else {
                    Platform.runLater(() -> resourceAnimationController.addFadingIn(pane, resourcesHBox));
                    resourceAnimationController.textFillAnimation(pane, label, oldValue, color, resourcesHBox);
                }
            }
        });
    }

    public void initDataToElement(Player me) {
        resourcesHBox.getChildren().clear();
        Resources resources = me.resources();

        int brick = resources.brick() == null ? 0 : resources.brick();
        int grain = resources.grain() == null ? 0 : resources.grain();
        int ore = resources.ore() == null ? 0 : resources.ore();
        int lumber = resources.lumber() == null ? 0 : resources.lumber();
        int wool = resources.wool() == null ? 0 : resources.wool();

        if (lumber > 0) {
            resourcesHBox.getChildren().add(resourcePaneMap.get("lumber"));
        }
        if (grain > 0) {
            resourcesHBox.getChildren().add(resourcePaneMap.get("grain"));
        }
        if (wool > 0) {
            resourcesHBox.getChildren().add(resourcePaneMap.get("wool"));
        }
        if (brick > 0) {
            resourcesHBox.getChildren().add(resourcePaneMap.get("brick"));
        }
        if (ore > 0) {
            resourcesHBox.getChildren().add(resourcePaneMap.get("ore"));
        }

        fischCount.setText(String.valueOf(lumber));
        packeisCount.setText(String.valueOf(brick));
        fellCount.setText(String.valueOf(wool));
        kohleCount.setText(String.valueOf(ore));
        walknochenCount.setText(String.valueOf(grain));

        HashMap<String, Integer> devCardMap = resourceService.getDevCardMap(me.developmentCards());
        if(devCardMap.get(DEV_KNIGHT) > 0) {
            invokeDevCardElement(DEV_KNIGHT, devCardMap.get(DEV_KNIGHT));
        }
        if(devCardMap.get(DEV_ROAD) > 0) {
            invokeDevCardElement(DEV_ROAD, devCardMap.get(DEV_ROAD));
        }
        if(devCardMap.get(DEV_PLENTY) > 0) {
            invokeDevCardElement(DEV_PLENTY, devCardMap.get(DEV_PLENTY));
        }
        if(devCardMap.get(DEV_MONOPOLY) > 0) {
            invokeDevCardElement(DEV_MONOPOLY, devCardMap.get(DEV_MONOPOLY));
        }
        if(devCardMap.get(DEV_VPOINT) > 0) {
            invokeDevCardElement(DEV_VPOINT, devCardMap.get(DEV_VPOINT));
        }
    }
}


