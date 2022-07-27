package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.ResourceService;
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
    private final ResourceService resourceService;
    private final ResourceAnimationController resourceAnimationController;
    private int ore, lumber, brick, wool, grain;

    public ResourceRemovedAnimationController(Pane root, ResourceService resourceService, ResourceAnimationController resourceAnimationController) {
        this.root = root;
        this.resourceService = resourceService;
        this.resourceAnimationController = resourceAnimationController;
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
        card.setLayoutX(180);
        card.setLayoutY(409);

        new Thread(() -> {
            try {
                Thread.sleep((counter * 1000L) - 1000);
                Platform.runLater(() -> root.getChildren().add(card));

                ScaleTransition st = new ScaleTransition(new Duration(400), card);
                st.setFromX(0.06f);
                st.setFromY(0.06f);
                st.setToX(0.2f);
                st.setToY(0.2f);
                st.play();

                TranslateTransition tt = new TranslateTransition(Duration.millis(400), card);
                tt.setToX(0);
                tt.setToY(-509);
                tt.setOnFinished(t -> removedResourceCardAnimationTwo(card, counter, resNumber));
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void removedResourceCardAnimationTwo(ImageView card, int counter, int resNumber) {
        new Thread(() -> {
            try {
                Line lineOne = new Line(407, 227, 523, 399);
                lineOne.setStroke(Color.RED);
                lineOne.setStrokeWidth(6);

                Line lineTwo = new Line(523, 227, 407, 399);
                lineTwo.setStroke(Color.RED);
                lineTwo.setStrokeWidth(6);

                Thread.sleep(100);
                Platform.runLater(() -> root.getChildren().add(lineOne));
                Thread.sleep(100);
                Platform.runLater(() -> root.getChildren().add(lineTwo));
                Thread.sleep(300);

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
                ftLineTwo.setOnFinished(t -> removeAfterAnimation(card, counter, resNumber, lineOne, lineTwo));
                ftLineTwo.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void removedTradeResourceCardAnimationOne(ImageView card, int counter, int resNumber) {
        card.setLayoutX(180);
        card.setLayoutY(409);

        new Thread(() -> {
            try {
                Thread.sleep((counter * 1000L) - 1000);
                Platform.runLater(() -> root.getChildren().add(card));

                ScaleTransition st = new ScaleTransition(new Duration(300), card);
                st.setFromX(0.06f);
                st.setFromY(0.06f);
                st.setToX(0.2f);
                st.setToY(0.2f);
                st.play();

                TranslateTransition tt = new TranslateTransition(Duration.millis(300), card);
                tt.setToX(0);
                tt.setToY(-509);
                tt.setOnFinished(t -> removedTradeResourceCardAnimationTwo(card, counter, resNumber));
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void removedTradeResourceCardAnimationTwo(ImageView card, int counter, int resNumber) {
        new Thread(() -> {
            try {
                Thread.sleep(250);

                ScaleTransition st = new ScaleTransition(new Duration(350), card);
                st.setFromX(0.2f);
                st.setFromY(0.2f);
                st.setToX(0.37f);
                st.setToY(0.37f);
                st.play();

                TranslateTransition tt = new TranslateTransition(Duration.millis(350), card);
                tt.setToX(557);
                tt.setToY(-335);
                tt.setOnFinished(t -> removedTradeResourceCardAnimationThree(card, counter, resNumber));
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void removedTradeResourceCardAnimationThree(ImageView card, int counter, int resNumber) {
        new Thread(() -> {
            FadeTransition ft = new FadeTransition(Duration.millis(100), card);
            ft.setFromValue(1);
            ft.setToValue(0);
            ft.setOnFinished(t -> removeAfterAnimation(card, counter, resNumber, null, null));
            ft.play();
        }).start();
    }

    private void removeAfterAnimation(ImageView card, int counter, int resNumber, Line lineOne, Line lineTwo) {
        Platform.runLater(() -> {
            root.getChildren().remove(lineOne);
            root.getChildren().remove(lineTwo);
            root.getChildren().remove(card);
        });

        if (resNumber == 1) {
            resourceService.updateResources(ORE, ore);
        } else if (resNumber == 2) {
            resourceService.updateResources(LUMBER, lumber);
        } else if (resNumber == 3) {
            resourceService.updateResources(BRICK, brick);
        } else if (resNumber == 4) {
            resourceService.updateResources(WOOL, wool);
        } else if (resNumber == 5) {
            resourceService.updateResources(GRAIN, grain);
        }

        if(counter == 1) {
            new Thread(() -> {
                try {
                    Thread.sleep(2000);
                    resourceAnimationController.handleNewDevCards();
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }
            }).start();
        }
    }
}