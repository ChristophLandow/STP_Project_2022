package de.uniks.pioneers.controller.subcontroller;

import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class ResourceNewAnimationController {
    private final Pane root;
    private final IngamePlayerResourcesController ingamePlayerResourcesController;
    private int ore, lumber, brick, wool, grain;

    public ResourceNewAnimationController(Pane root, IngamePlayerResourcesController ingamePlayerResourcesController) {
        this.root = root;
        this.ingamePlayerResourcesController = ingamePlayerResourcesController;
    }

    public void setResourceCounts(int ore, int lumber, int brick, int wool, int grain) {
        this.ore = ore;
        this.lumber = lumber;
        this.brick = brick;
        this.wool = wool;
        this.grain = grain;
    }

    public void newResourceCardAnimationOne(ImageView card, int counter, int resNumber, boolean firstTime) {
        card.setLayoutX(10);
        card.setLayoutY(-100);

        new Thread(() -> {
            try {
                Thread.sleep((counter * 1500L) - 1500);
                Platform.runLater(() -> root.getChildren().add(card));

                ScaleTransition st = new ScaleTransition(new Duration(500), card);
                st.setFromX(0f);
                st.setFromY(0f);
                st.setByX(0.2f);
                st.setByY(0.2f);
                st.setOnFinished(t -> newResourceCardAnimationTwo(card, resNumber, firstTime));
                st.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void newResourceCardAnimationTwo(ImageView card, int resNumber, boolean firstTime) {
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
                tt.setOnFinished(t -> newAfterAnimation(card, resNumber, firstTime));
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void newAfterAnimation(ImageView card, int resNumber, boolean firstTime) {
        Platform.runLater(() -> root.getChildren().remove(card));

        if(firstTime) {
            if(resNumber == 1) {
                ingamePlayerResourcesController.setOreToElement(true);
                ingamePlayerResourcesController.setOreCount(ore);
            } else if(resNumber == 2) {
                ingamePlayerResourcesController.setLumberToElement(true);
                ingamePlayerResourcesController.setLumberCount(lumber);
            } else if(resNumber == 3) {
                ingamePlayerResourcesController.setBrickToElement(true);
                ingamePlayerResourcesController.setBrickCount(brick);
            } else if(resNumber == 4) {
                ingamePlayerResourcesController.setWoolToElement(true);
                ingamePlayerResourcesController.setWoolCount(wool);
            } else if(resNumber == 5) {
                ingamePlayerResourcesController.setGrainToElement(true);
                ingamePlayerResourcesController.setGrainCount(grain);
            }
        } else {
            if(resNumber == 1) {
                ingamePlayerResourcesController.setOreCount(ore);
            } else if(resNumber == 2) {
                ingamePlayerResourcesController.setLumberCount(lumber);
            } else if(resNumber == 3) {
                ingamePlayerResourcesController.setBrickCount(brick);
            } else if(resNumber == 4) {
                ingamePlayerResourcesController.setWoolCount(wool);
            } else if(resNumber == 5) {
                ingamePlayerResourcesController.setGrainCount(grain);
            }
        }
    }
}
