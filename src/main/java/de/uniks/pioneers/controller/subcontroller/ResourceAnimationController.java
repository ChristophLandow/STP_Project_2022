package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.services.GameService;
import javafx.collections.MapChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import java.util.Objects;

public class ResourceAnimationController {
    private final GameService gameService;
    private final ResourceNewAnimationController resourceNewAnimationController;
    private final ResourceRemovedAnimationController resourceRemovedAnimationController;
    private ImageView carbonView, fishView, iceView, polarbearView, whaleView;
    private Player valueAdded;
    private Player valueRemoved;
    private boolean me;

    public ResourceAnimationController(Pane root, GameService gameService, IngamePlayerResourcesController ingamePlayerResourcesController) {
        this.gameService = gameService;
        this.resourceNewAnimationController = new ResourceNewAnimationController(root, gameService);
        this.resourceRemovedAnimationController = new ResourceRemovedAnimationController(root, gameService);
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
                this.handleResources(gameService.moveAction.get());
            }
        });
    }

    public void handleResources(String moveAction) {
        int counter = 0;
        initCards();
        me = false;

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
}