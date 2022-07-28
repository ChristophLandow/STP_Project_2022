package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.model.DevelopmentCard;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.ResourceService;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.collections.MapChangeListener;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Paint;
import javafx.util.Duration;

import java.util.List;
import java.util.Objects;

public class ResourceAnimationController {
    private final GameService gameService;
    private final ResourceNewAnimationController resourceNewAnimationController;
    private final ResourceRemovedAnimationController resourceRemovedAnimationController;
    private final DevCardNewAnimationController devCardNewAnimationController;
    private final DevCardRemovedAnimationController devCardRemovedAnimationController;
    private ImageView carbonView, fishView, iceView, polarbearView, whaleView, knightView, roadView, plentyView, monopolyView, vpointView;
    private Player valueAdded;
    private Player valueRemoved;
    private boolean me;
    private int knightOld;
    private int roadOld;
    private int plentyOld;
    private int monopolyOld;
    private int vpointOld;
    private int knightNew;
    private int roadNew;
    private int plentyNew;
    private int monopolyNew;
    private int vpointNew;

    public ResourceAnimationController(Pane root, GameService gameService, ResourceService resourceService) {
        this.gameService = gameService;
        this.resourceNewAnimationController = new ResourceNewAnimationController(root, resourceService);
        this.resourceRemovedAnimationController = new ResourceRemovedAnimationController(root, resourceService, this);
        this.devCardNewAnimationController = new DevCardNewAnimationController(root, resourceService);
        this.devCardRemovedAnimationController = new DevCardRemovedAnimationController(root, resourceService);
        this.me = false;

        this.addPlayerListener();
        this.addMoveActionListener();
    }

    private void initCards() {
        carbonView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_carbon.png")).toString()));
        fishView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_fish.png")).toString()));
        iceView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_ice.png")).toString()));
        polarbearView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_polarbear.png")).toString()));
        whaleView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_whale.png")).toString()));

        knightView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_knight.png")).toString()));
        roadView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_road.png")).toString()));
        plentyView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_plenty.png")).toString()));
        monopolyView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_monopoly.png")).toString()));
        vpointView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_vpoint.png")).toString()));
    }

    private void addPlayerListener() {
        // add listener for observable players list
        gameService.players.addListener((MapChangeListener<? super String, ? super Player>) c -> {
            if(!gameService.wonGame) {
                String key = c.getKey();
                if (key.equals(gameService.me)) {
                    if (c.wasRemoved() && c.wasAdded()) {
                        this.valueAdded = c.getValueAdded();
                        this.valueRemoved = c.getValueRemoved();
                        this.me = true;
                    }
                }
            }
        });
    }

    private void addMoveActionListener() {
        gameService.moveAction.addListener((observable, oldValue, newValue) -> {
            if(me) {
                initCards();
                me = false;

                this.handleResources(gameService.moveAction.get());
                this.handleDevCards();
            }
        });
    }

    public void handleResources(String moveAction) {
        int counter = 0;

        Resources resources = valueAdded.resources();
        int brick = resources.brick();
        int grain = resources.grain();
        int ore = resources.ore();
        int lumber = resources.lumber();
        int wool = resources.wool();
        int unknown = resources.unknown();

        resourceNewAnimationController.setResourceCounts(ore, lumber, brick, wool, grain);
        resourceRemovedAnimationController.setResourceCounts(ore, lumber, brick, wool, grain);

        resources = valueRemoved.resources();
        int oldBrick = resources.brick();
        int oldGrain = resources.grain();
        int oldOre = resources.ore();
        int oldLumber = resources.lumber();
        int oldWool = resources.wool();
        int oldUnknown = resources.unknown();

        if (oldOre > ore) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimation(carbonView, counter, 1, moveAction);
        } else if (ore > oldOre) {
            counter += 1;
            resourceNewAnimationController.newResourceCardAnimation(carbonView, counter, 1, moveAction);
        }

        if (oldLumber > lumber) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimation(fishView, counter, 2, moveAction);
        } else if (lumber > oldLumber) {
            counter += 1;
            resourceNewAnimationController.newResourceCardAnimation(fishView, counter, 2, moveAction);
        }

        if (oldBrick > brick) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimation(iceView, counter, 3, moveAction);
        } else if (brick > oldBrick) {
            counter += 1;
            resourceNewAnimationController.newResourceCardAnimation(iceView, counter, 3, moveAction);
        }

        if (oldWool > wool) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimation(polarbearView, counter, 4, moveAction);
        } else if (wool > oldWool) {
            counter += 1;
            resourceNewAnimationController.newResourceCardAnimation(polarbearView, counter, 4, moveAction);
        }

        if (oldGrain > grain) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimation(whaleView, counter, 5, moveAction);
        } else if (grain > oldGrain) {
            counter += 1;
            resourceNewAnimationController.newResourceCardAnimation(whaleView, counter, 5, moveAction);
        }
    }

    public void handleDevCards() {
        List<DevelopmentCard> devCardsNew = valueAdded.developmentCards();
        knightNew = 0;
        roadNew = 0;
        plentyNew = 0;
        monopolyNew = 0;
        vpointNew = 0;
        int unknownNew = 0;

        for(DevelopmentCard devCard : devCardsNew) {
            switch (devCard.type()) {
                case "knight" -> knightNew += 1;
                case "road-building" -> roadNew += 1;
                case "year-of-plenty" -> plentyNew += 1;
                case "monopoly" -> monopolyNew += 1;
                case "victory-point" -> vpointNew += 1;
                case "unknown" -> unknownNew += 1;
            }
        }

        devCardNewAnimationController.setCardCounts(knightNew, roadNew, plentyNew, monopolyNew, vpointNew);
        devCardRemovedAnimationController.setCardCounts(knightNew, roadNew, plentyNew, monopolyNew, vpointNew);

        List<DevelopmentCard> devCardsOld = valueRemoved.developmentCards();
        knightOld = 0;
        roadOld = 0;
        plentyOld = 0;
        monopolyOld = 0;
        vpointOld = 0;
        int unknownOld = 0;

        for(DevelopmentCard devCard : devCardsOld) {
            switch (devCard.type()) {
                case "knight" -> knightOld += 1;
                case "road-building" -> roadOld += 1;
                case "year-of-plenty" -> plentyOld += 1;
                case "monopoly" -> monopolyOld += 1;
                case "victory-point" -> vpointOld += 1;
                case "unknown" -> unknownOld += 1;
            }
        }
    }

    public void handleNewDevCards() {
        if (knightNew > knightOld) {
            devCardNewAnimationController.newDevCardAnimationOne(knightView, 1, 1);
        } else if (roadNew > roadOld) {
            devCardNewAnimationController.newDevCardAnimationOne(roadView, 1, 2);
        } else if (plentyNew > plentyOld) {
            devCardNewAnimationController.newDevCardAnimationOne(plentyView, 1, 3);
        } else if (monopolyNew > monopolyOld) {
            devCardNewAnimationController.newDevCardAnimationOne(monopolyView, 1, 4);
        } else if (vpointNew > vpointOld) {
            devCardNewAnimationController.newDevCardAnimationOne(vpointView, 1, 5);
        }
    }

    public void handleRemovedDevCards() {
        if (knightOld > knightNew) {
            devCardRemovedAnimationController.removedDevCardAnimationOne(knightView, 1, 1);
        } else if (roadOld > roadNew) {
            devCardRemovedAnimationController.removedDevCardAnimationOne(roadView, 1, 2);
        } else if (plentyOld > plentyNew) {
            devCardRemovedAnimationController.removedDevCardAnimationOne(plentyView, 1, 3);
        } else if (monopolyOld > monopolyNew) {
            devCardRemovedAnimationController.removedDevCardAnimationOne(monopolyView, 1, 4);
        } else if (vpointOld > vpointNew) {
            devCardRemovedAnimationController.removedDevCardAnimationOne(vpointView, 1, 5);
        }
    }

    public void addFadingIn(Node node, HBox parent) {
        FadeTransition transition = new FadeTransition(Duration.millis(1500), node);
        parent.getChildren().add(node);
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

    public void textFillAnimation(Pane pane, Label label, Integer oldValue, Paint color, HBox resourcesHBox) {
        Timeline timeline = new Timeline(
                new KeyFrame(Duration.seconds(0)),
                new KeyFrame(Duration.seconds(1.5))
        );
        timeline.setAutoReverse(true);
        timeline.setCycleCount(1);
        timeline.setOnFinished(e -> {
            if (oldValue == 0) {
                resourcesHBox.getChildren().remove(pane);
            }
            label.setText(String.valueOf(oldValue));
            label.setTextFill(color);
        });
        timeline.play();
    }
}
