package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.GameService;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;
import javafx.util.Duration;

import static de.uniks.pioneers.GameConstants.*;

public class ResourceRemovedAnimationController {
    private final Pane root;
    private final IngamePlayerResourcesController ingamePlayerResourcesController;
    private final GameService gameService;
    private int ore, lumber, brick, wool, grain;

    public ResourceRemovedAnimationController(Pane root, IngamePlayerResourcesController ingamePlayerResourcesController, GameService gameService) {
        this.root = root;
        this.ingamePlayerResourcesController = ingamePlayerResourcesController;
        this.gameService = gameService;
    }

    public void setResourceCounts(int ore, int lumber, int brick, int wool, int grain) {
        this.ore = ore;
        this.lumber = lumber;
        this.brick = brick;
        this.wool = wool;
        this.grain = grain;
    }

    public void removedResourceCardAnimationOne(ImageView card, int counter, int resNumber, boolean remove) {
        card.setLayoutX(10);
        card.setLayoutY(409);

        new Thread(() -> {
            try {
                Thread.sleep((counter * 1500L) - 1500);
                Platform.runLater(() -> root.getChildren().add(card));

                ScaleTransition st = new ScaleTransition(new Duration(500), card);
                st.setFromX(0.06f);
                st.setFromY(0.06f);
                st.setToX(0.2f);
                st.setToY(0.2f);
                st.play();

                TranslateTransition tt = new TranslateTransition(Duration.millis(500), card);
                tt.setToX(0);
                tt.setToY(-509);
                tt.setOnFinished(t -> removedResourceCardAnimationTwo(card, resNumber, remove));
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void removedResourceCardAnimationTwo(ImageView card, int resNumber, boolean remove) {
        new Thread(() -> {
            try {
                Line lineOne = new Line(234, 227, 350, 399);
                lineOne.setStroke(Color.RED);
                lineOne.setStrokeWidth(6);

                Line lineTwo = new Line(350, 227, 234, 399);
                lineTwo.setStroke(Color.RED);
                lineTwo.setStrokeWidth(6);

                Thread.sleep(200);
                Platform.runLater(() -> root.getChildren().add(lineOne));
                Thread.sleep(200);
                Platform.runLater(() -> root.getChildren().add(lineTwo));
                Thread.sleep(500);

                FadeTransition ftCard = new FadeTransition(Duration.millis(100), card);
                ftCard.setFromValue(1);
                ftCard.setToValue(0);
                ftCard.play();

                FadeTransition ftLineOne = new FadeTransition(Duration.millis(100), lineOne);
                ftLineOne.setFromValue(1);
                ftLineOne.setToValue(0);
                ftLineOne.play();

                FadeTransition ftLineTwo = new FadeTransition(Duration.millis(100), lineTwo);
                ftLineTwo.setFromValue(1);
                ftLineTwo.setToValue(0);
                ftLineTwo.setOnFinished(t -> removeAfterAnimation(card, resNumber, remove, lineOne, lineTwo));
                ftLineTwo.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void removeAfterAnimation(ImageView card, int resNumber, boolean remove, Line lineOne, Line lineTwo) {
        Platform.runLater(() -> {
            root.getChildren().remove(lineOne);
            root.getChildren().remove(lineTwo);
            root.getChildren().remove(card);
        });

        if (resNumber == 1) {
            gameService.updateResources(ORE, ore);
        } else if (resNumber == 2) {
            gameService.updateResources(LUMBER, lumber);
        } else if (resNumber == 3) {
            gameService.updateResources(BRICK, brick);
        } else if (resNumber == 4) {
            gameService.updateResources(WOOL, wool);
        } else if (resNumber == 5) {
            gameService.updateResources(GRAIN, grain);
        }

    }
}