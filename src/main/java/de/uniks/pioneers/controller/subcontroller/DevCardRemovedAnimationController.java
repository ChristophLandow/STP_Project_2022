package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.GameService;
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

public class DevCardRemovedAnimationController {
    private final Pane root;
    private final ResourceService resourceService;
    private int knight, road, plenty, monopoly, vpoint;

    public DevCardRemovedAnimationController(Pane root, ResourceService resourceService) {
        this.root = root;
        this.resourceService = resourceService;
    }

    public void setCardCounts(int knight, int road, int plenty, int monopoly, int vpoint) {
        this.knight = knight;
        this.road = road;
        this.plenty = plenty;
        this.monopoly = monopoly;
        this.vpoint = vpoint;
    }

    public void removedDevCardAnimationOne(ImageView card, int counter, int devCardNumber) {
        card.setLayoutX(225);
        card.setLayoutY(437);

        new Thread(() -> {
            try {
                Thread.sleep((counter * 1500L) - 1500);
                Platform.runLater(() -> root.getChildren().add(card));

                ScaleTransition st = new ScaleTransition(new Duration(500), card);
                st.setFromX(0.08f);
                st.setFromY(0.095f);
                st.setToX(0.5f);
                st.setToY(0.5f);
                st.play();

                TranslateTransition tt = new TranslateTransition(Duration.millis(500), card);
                tt.setToX(0);
                tt.setToY(-437);
                tt.setOnFinished(t -> removedDevCardAnimationTwo(card, devCardNumber));
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void removedDevCardAnimationTwo(ImageView card, int devCardNumber) {
        new Thread(() -> {
            try {
                Line lineOne = new Line(407, 227, 523, 399);
                lineOne.setStroke(Color.RED);
                lineOne.setStrokeWidth(6);

                Line lineTwo = new Line(523, 227, 407, 399);
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
                ftLineTwo.setOnFinished(t -> removeAfterAnimation(card, devCardNumber, lineOne, lineTwo));
                ftLineTwo.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void removeAfterAnimation(ImageView card, int devCardNumber, Line lineOne, Line lineTwo) {
        Platform.runLater(() -> {
            root.getChildren().remove(lineOne);
            root.getChildren().remove(lineTwo);
            root.getChildren().remove(card);
        });

        if (devCardNumber == 1) {
            resourceService.updateDevCards("knight", knight);
        } else if (devCardNumber == 2) {
            resourceService.updateDevCards("road", road);
        } else if (devCardNumber == 3) {
            resourceService.updateDevCards("plenty", plenty);
        } else if (devCardNumber == 4) {
            resourceService.updateDevCards("monopoly", monopoly);
        } else if (devCardNumber == 5) {
            resourceService.updateDevCards("vpoint", vpoint);
        }
    }
}

