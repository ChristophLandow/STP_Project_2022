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
    private final GameService gameService;
    private int ore, lumber, brick, wool, grain;

    public ResourceRemovedAnimationController(Pane root, GameService gameService) {
        this.root = root;
        this.gameService = gameService;
    }

    public void setResourceCounts(int ore, int lumber, int brick, int wool, int grain) {
        this.ore = ore;
        this.lumber = lumber;
        this.brick = brick;
        this.wool = wool;
        this.grain = grain;
    }

    public void removedResourceCardAnimation(ImageView card, int counter, int resNumber, String moveAction) {
        if(moveAction.equals("accept") || moveAction.equals("rob")) {
            removedTradeResourceCardAnimationOne(card, counter, resNumber);
        } else {
            removedResourceCardAnimationOne(card, counter, resNumber);
        }
    }

    public void removedResourceCardAnimationOne(ImageView card, int counter, int resNumber) {
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
                tt.setOnFinished(t -> removedResourceCardAnimationTwo(card, resNumber));
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void removedResourceCardAnimationTwo(ImageView card, int resNumber) {
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
                ftLineTwo.setOnFinished(t -> removeAfterAnimation(card, resNumber, lineOne, lineTwo));
                ftLineTwo.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void removedTradeResourceCardAnimationOne(ImageView card, int counter, int resNumber) {
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
                tt.setOnFinished(t -> removedTradeResourceCardAnimationTwo(card, resNumber));
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void removedTradeResourceCardAnimationTwo(ImageView card, int resNumber) {
        new Thread(() -> {
            try {
                Thread.sleep(400);

                ScaleTransition st = new ScaleTransition(new Duration(500), card);
                st.setFromX(0.2f);
                st.setFromY(0.2f);
                st.setToX(0.37f);
                st.setToY(0.37f);
                st.play();

                TranslateTransition tt = new TranslateTransition(Duration.millis(500), card);
                tt.setToX(565);
                tt.setToY(-335);
                tt.setOnFinished(t -> removedTradeResourceCardAnimationThree(card, resNumber));
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void removedTradeResourceCardAnimationThree(ImageView card, int resNumber) {
        new Thread(() -> {
            FadeTransition ft = new FadeTransition(Duration.millis(100), card);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.setOnFinished(t -> removeAfterAnimation(card, resNumber, null, null));
            ft.play();
        }).start();
    }

    private void removeAfterAnimation(ImageView card, int resNumber, Line lineOne, Line lineTwo) {
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