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

    public ResourceAnimationController(Pane root, GameService gameService, IngamePlayerResourcesController ingamePlayerResourcesController) {
        this.gameService = gameService;
        this.resourceNewAnimationController = new ResourceNewAnimationController(root, ingamePlayerResourcesController);
        this.resourceRemovedAnimationController = new ResourceRemovedAnimationController(root, ingamePlayerResourcesController);

        this.addPlayerListener();
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
            String key = c.getKey();
            if (key.equals(gameService.me)) {
                if (c.wasRemoved() && c.wasAdded()) {
                    this.valueAdded = c.getValueAdded();
                    this.valueRemoved = c.getValueRemoved();
                    this.handleResources();
                }
            }
        });
    }

    public void handleResources() {
        int counter = 0;
        initCards();

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

        if(ore==0 && oldOre>0) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimationOne(carbonView, counter, 1, true);
        } else if(ore>0 && oldOre==0) {
            counter += 1;
            resourceNewAnimationController.newResourceCardAnimationOne(carbonView, counter, 1, true);
        } else if(ore > oldOre) {
            counter += 1;
            resourceNewAnimationController.newResourceCardAnimationOne(carbonView, counter, 1, false);
        } else if(oldOre > ore) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimationOne(carbonView, counter, 1, false);
        }

        if(lumber==0 && oldLumber>0) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimationOne(fishView, counter, 2, true);
        } else if(lumber>0 && oldLumber==0) {
            counter += 1;
             resourceNewAnimationController.newResourceCardAnimationOne(fishView, counter, 2, true);
        } else if(lumber > oldLumber) {
            counter += 1;
             resourceNewAnimationController.newResourceCardAnimationOne(fishView, counter, 2, false);
        } else if(oldLumber > lumber) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimationOne(fishView, counter, 2, false);
        }

        if(brick==0 && oldBrick>0) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimationOne(iceView, counter, 3, true);
        } else if(brick>0 && oldBrick==0) {
            counter += 1;
             resourceNewAnimationController.newResourceCardAnimationOne(iceView, counter, 3, true);
        } else if(brick > oldBrick) {
            counter += 1;
             resourceNewAnimationController.newResourceCardAnimationOne(iceView, counter, 3, false);
        } else if(oldBrick > brick) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimationOne(iceView, counter, 3, false);
        }

        if(wool==0 && oldWool>0) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimationOne(polarbearView, counter, 4, true);
        } else if(wool>0 && oldWool==0) {
            counter += 1;
             resourceNewAnimationController.newResourceCardAnimationOne(polarbearView, counter, 4, true);
        } else if(wool > oldWool) {
            counter += 1;
             resourceNewAnimationController.newResourceCardAnimationOne(polarbearView, counter, 4, false);
        } else if(oldWool > wool) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimationOne(polarbearView, counter, 4, false);
        }

        if(grain==0 && oldGrain>0) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimationOne(whaleView, counter, 5, true);
        } else if(grain>0 && oldGrain==0) {
            counter += 1;
             resourceNewAnimationController.newResourceCardAnimationOne(whaleView, counter, 5, true);
        } else if(grain > oldGrain) {
            counter += 1;
             resourceNewAnimationController.newResourceCardAnimationOne(whaleView, counter, 5, false);
        } else if(oldGrain > grain) {
            counter += 1;
            resourceRemovedAnimationController.removedResourceCardAnimationOne(whaleView, counter, 5, false);
        }
    }
}
