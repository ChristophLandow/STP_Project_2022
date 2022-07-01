package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.services.GameService;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.collections.MapChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;
import java.util.Objects;

public class ResourceAnimationController {
    private final Pane root;
    private final GameService gameService;
    private ImageView carbonView, fishView, iceView, polarbearView, whaleView;
    private int ore, lumber, brick, wool, grain;
    private Player me;
    private final IngamePlayerResourcesController ingamePlayerResourcesController;
    private Player valueAdded;
    private Player valueRemoved;

    public ResourceAnimationController(Pane root, GameService gameService, IngamePlayerResourcesController ingamePlayerResourcesController) {
        this.gameService = gameService;
        this.me = this.gameService.players.get(gameService.me);
        this.ingamePlayerResourcesController = ingamePlayerResourcesController;
        this.root = root;

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
                    System.out.println(" your ressources: "+ c.getValueAdded().resources());
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
        ore = resources.ore() == null ? 0 : resources.ore();
        lumber = resources.lumber() == null ? 0 : resources.lumber();
        brick = resources.brick() == null ? 0 : resources.brick();
        wool = resources.wool() == null ? 0 : resources.wool();
        grain = resources.grain() == null ? 0 : resources.grain();
        int unknown = resources.unknown() == null ? 0 : resources.unknown();

        resources = valueRemoved.resources();
        int oldOre = resources.ore() == null ? 0 : resources.ore();
        int oldLumber = resources.lumber() == null ? 0 : resources.lumber();
        int oldBrick = resources.brick() == null ? 0 : resources.brick();
        int oldWool = resources.wool() == null ? 0 : resources.wool();
        int oldGrain = resources.grain() == null ? 0 : resources.grain();
        int oldUnknown = resources.unknown() == null ? 0 : resources.unknown();

        if(ore==0 && oldOre>0) {
        } else if(ore>0 && oldOre==0) {
            System.out.println("ore");
            counter += 1;
            firstResourceCardAnimation(carbonView, counter, 1, true);
        } else if(ore > oldOre) {
            counter += 1;
            firstResourceCardAnimation(carbonView, counter, 1, false);
        }

        if(lumber==0 && oldLumber>0) {
        } else if(lumber>0 && oldLumber==0) {
            System.out.println("lumber");
            counter += 1;
            firstResourceCardAnimation(fishView, counter, 2, true);
        } else if(lumber > oldLumber) {
            counter += 1;
            firstResourceCardAnimation(fishView, counter, 2, false);
        }

        if(brick==0 && oldBrick>0) {
        } else if(brick>0 && oldBrick==0) {
            System.out.println("brick");
            counter += 1;
            firstResourceCardAnimation(iceView, counter, 3, true);
        } else if(brick > oldBrick) {
            counter += 1;
            firstResourceCardAnimation(iceView, counter, 3, false);
        }

        if(wool==0 && oldWool>0) {
        } else if(wool>0 && oldWool==0) {
            System.out.println("wool");
            counter += 1;
            firstResourceCardAnimation(polarbearView, counter, 4, true);
        } else if(wool > oldWool) {
            counter += 1;
            firstResourceCardAnimation(polarbearView, counter, 4, false);
        }

        if(grain==0 && oldGrain>0) {
        } else if(grain>0 && oldGrain==0) {
            System.out.println("grain");
            counter += 1;
            firstResourceCardAnimation(whaleView, counter, 5, true);
        } else if(grain > oldGrain) {
            counter += 1;
            firstResourceCardAnimation(whaleView, counter, 5, false);
        }

        System.out.println(valueAdded.resources());
        System.out.println(valueRemoved.resources());
    }

    public void firstResourceCardAnimation(ImageView card, int counter, int resNumber, boolean firstTime) {
        card.setLayoutX(10);
        card.setLayoutY(-100);

        new Thread(() -> {
            try {
                Thread.sleep((counter*1500)-1500);
                Platform.runLater(() -> root.getChildren().add(card));
                ScaleTransition st = new ScaleTransition(new Duration(500), card);
                st.setFromX(0f);
                st.setFromY(0f);
                st.setByX(0.2f);
                st.setByY(0.2f);
                st.setOnFinished(t -> secondResourceCardAnimation(card, resNumber, firstTime));
                st.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void secondResourceCardAnimation(ImageView card, int resNumber, boolean firstTime) {
        new Thread(() -> {
            try {
                Thread.sleep(500);
                ScaleTransition st = new ScaleTransition(new Duration(500), card);
                st.setFromX(0.2f);
                st.setFromY(0.2f);
                st.setToX(0.06f);
                st.setToY(0.06f);
                st.play();

                TranslateTransition tt = new TranslateTransition(Duration.millis(500), card);
                tt.setToX(0);
                tt.setToY(409);
                tt.setOnFinished(t -> {
                    afterAnimation(card, resNumber, firstTime);
                });
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void afterAnimation(ImageView card, int resNumber, boolean firstTime) {
        root.getChildren().remove(card);
        if(firstTime) {
            if(resNumber == 1) {
                ingamePlayerResourcesController.setOreToElement();
            } else if(resNumber == 2) {
                ingamePlayerResourcesController.setLumberToElement();
            } else if(resNumber == 3) {
                ingamePlayerResourcesController.setBrickToElement();
            } else if(resNumber == 4) {
                ingamePlayerResourcesController.setWoolToElement();
            } else if(resNumber == 5) {
                ingamePlayerResourcesController.setGrainToElement();
            }
        }
        ingamePlayerResourcesController.setOreCount(ore);
        ingamePlayerResourcesController.setLumberCount(lumber);
        ingamePlayerResourcesController.setBrickCount(brick);
        ingamePlayerResourcesController.setWoolCount(wool);
        ingamePlayerResourcesController.setGrainCount(grain);
    }
}
