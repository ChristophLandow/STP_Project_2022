package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.model.*;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.ResourceService;
import de.uniks.pioneers.services.UserService;
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

import static de.uniks.pioneers.GameConstants.*;

public class ResourceAnimationController {
    private final GameService gameService;
    private final UserService userService;
    private final IngameStateController ingameStateController;
    private final ResourceNewAnimationController resourceNewAnimationController;
    private final ResourceRemovedAnimationController resourceRemovedAnimationController;
    private final DevCardNewAnimationController devCardNewAnimationController;
    private final DevCardPlayedAnimationController devCardPlayedAnimationController;
    private final LargestArmyAnimationController largestArmyAnimationController;
    private ImageView carbonView, fishView, iceView, polarbearView, whaleView, knightView, roadView, plentyView, monopolyView, vpointView, largestArmyView;
    private Player valueAddedMe, valueRemovedMe, valueAddedOther, valueRemovedOther;
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

    public ResourceAnimationController(Pane root, GameService gameService, ResourceService resourceService, UserService userService, IngameStateController ingameStateController) {
        this.gameService = gameService;
        this.userService = userService;
        this.ingameStateController = ingameStateController;
        this.resourceNewAnimationController = new ResourceNewAnimationController(root, resourceService);
        this.resourceRemovedAnimationController = new ResourceRemovedAnimationController(root, resourceService, this);
        this.devCardNewAnimationController = new DevCardNewAnimationController(root, resourceService);
        this.devCardPlayedAnimationController = new DevCardPlayedAnimationController(root, gameService);
        this.largestArmyAnimationController = new LargestArmyAnimationController(root);
        this.me = false;

        this.addPlayerListener();
        this.addMoveActionListener();
        this.addArmyListener();
    }

    private void initCards() {
        carbonView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_carbon.png")).toString()));
        fishView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_fish.png")).toString()));
        iceView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_ice.png")).toString()));
        polarbearView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_polarbear.png")).toString()));
        whaleView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_whale.png")).toString()));

        knightView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_knight.png")).toString()));
        roadView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_road-building.png")).toString()));
        plentyView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_year-of-plenty.png")).toString()));
        monopolyView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_monopoly.png")).toString()));
        vpointView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/card_victory-point.png")).toString()));

        largestArmyView = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/Largest_Hunter_Group.png")).toString()));
    }

    private void addPlayerListener() {
        // add listener for observable players list
        gameService.players.addListener((MapChangeListener<? super String, ? super Player>) c -> {
            if(!gameService.wonGame) {
                String key = c.getKey();
                if (key.equals(gameService.me)) {
                    if (c.wasRemoved() && c.wasAdded()) {
                        this.valueAddedMe = c.getValueAdded();
                        this.valueRemovedMe = c.getValueRemoved();
                        this.me = true;
                        initCards();
                    }
                } else {
                    if (c.wasRemoved() && c.wasAdded()) {
                        this.valueAddedOther = c.getValueAdded();
                        this.valueRemovedOther = c.getValueRemoved();
                        initCards();
                        this.handleDevCards(-1);
                    }
                }
            }
        });
    }

    private void addMoveActionListener() {
        gameService.moveAction.addListener((observable, oldValue, newValue) -> {
            if(me) {
                me = false;
                this.handleResources(gameService.moveAction.get());
            }
        });
    }

    private void addArmyListener() {
        gameService.largestArmy.addListener((MapChangeListener<? super String, ? super LargestArmy>) c -> {
            if (c.getValueAdded().id().equals(userService.getCurrentUser()._id())) {
                largestArmyAnimationController.largestArmyAnimationOne(largestArmyView);
            }
        });
    }

    public void handleResources(String moveAction) {
        int counter = 0;

        Resources resources = valueAddedMe.resources();
        int brick = resources.brick();
        int grain = resources.grain();
        int ore = resources.ore();
        int lumber = resources.lumber();
        int wool = resources.wool();
        int unknown = resources.unknown();

        resourceNewAnimationController.setResourceCounts(ore, lumber, brick, wool, grain);
        resourceRemovedAnimationController.setResourceCounts(ore, lumber, brick, wool, grain);

        resources = valueRemovedMe.resources();
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

        this.handleDevCards(counter);
    }

    public void handleDevCards(int counter) {
        List<DevelopmentCard> devCardsNew;
        List<DevelopmentCard> devCardsOld;

        if(counter < 0) {
            devCardsNew = valueAddedOther.developmentCards();
            devCardsOld = valueRemovedOther.developmentCards();
        } else {
            devCardsNew = valueAddedMe.developmentCards();
            devCardsOld = valueRemovedMe.developmentCards();
        }

        knightNew = 0;
        roadNew = 0;
        plentyNew = 0;
        monopolyNew = 0;
        vpointNew = 0;
        int unknownNew = 0;

        for(DevelopmentCard devCard : devCardsNew) {
            switch (devCard.type()) {
                case DEV_KNIGHT -> knightNew += 1;
                case DEV_ROAD -> roadNew += 1;
                case DEV_PLENTY -> plentyNew += 1;
                case DEV_MONOPOLY -> monopolyNew += 1;
                case DEV_VPOINT -> vpointNew += 1;
                case DEV_UNKNOWN -> unknownNew += 1;
            }
        }

        devCardNewAnimationController.setCardCounts(knightNew, roadNew, plentyNew, monopolyNew, vpointNew);

        knightOld = 0;
        roadOld = 0;
        plentyOld = 0;
        monopolyOld = 0;
        vpointOld = 0;
        int unknownOld = 0;

        for(DevelopmentCard devCard : devCardsOld) {
            switch (devCard.type()) {
                case DEV_KNIGHT -> knightOld += 1;
                case DEV_ROAD -> roadOld += 1;
                case DEV_PLENTY -> plentyOld += 1;
                case DEV_MONOPOLY -> monopolyOld += 1;
                case DEV_VPOINT -> vpointOld += 1;
                case DEV_UNKNOWN -> unknownOld += 1;
            }
        }

        if(knightNew > knightOld || roadNew > roadOld || plentyNew > plentyOld || monopolyNew > monopolyOld || vpointNew > vpointOld) {
            if(counter > 0) {
                disableEndTurnButton((counter * 1000L) + 1250);
            } else if (!(vpointNew > vpointOld)) {
                handlePlayedDevCards();
            }
        } else if (counter > 0) {
            disableEndTurnButton(counter * 1000L);
        }
    }

    public void handleNewDevCards() {
        if (knightNew > knightOld) {
            devCardNewAnimationController.newDevCardAnimationOne(knightView, 1);
        } else if (roadNew > roadOld) {
            devCardNewAnimationController.newDevCardAnimationOne(roadView, 2);
        } else if (plentyNew > plentyOld) {
            devCardNewAnimationController.newDevCardAnimationOne(plentyView, 3);
        } else if (monopolyNew > monopolyOld) {
            devCardNewAnimationController.newDevCardAnimationOne(monopolyView, 4);
        } else if (vpointNew > vpointOld) {
            devCardNewAnimationController.newDevCardAnimationOne(vpointView, 5);
        }
    }

    public void handlePlayedDevCards() {
        if (knightNew > knightOld) {
            devCardPlayedAnimationController.playedDevCardAnimationOne(knightView, 1);
        } else if (roadNew > roadOld) {
            devCardPlayedAnimationController.playedDevCardAnimationOne(roadView, 2);
        } else if (plentyNew > plentyOld) {
            devCardPlayedAnimationController.playedDevCardAnimationOne(plentyView, 3);
        } else if (monopolyNew > monopolyOld) {
            devCardPlayedAnimationController.playedDevCardAnimationOne(monopolyView, 4);
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

    public void disableEndTurnButton(Long time) {
        ExpectedMove move = ingameStateController.ingameService.getExpectedMove();
        if(move.action().equals(BUILD) && move.players().get(0).equals(userService.getCurrentUser()._id())) {
            new Thread(() -> {
                try {
                    ingameStateController.setDisableEndTurn(true);
                    Thread.sleep(time+100);
                    ingameStateController.setDisableEndTurn(false);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        } else if (move.players().get(0).equals(userService.getCurrentUser()._id())) {
            ingameStateController.setTime(time);
            ingameStateController.setDisableEndTurn(true);
        }
    }
}

