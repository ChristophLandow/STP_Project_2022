package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.ResourceService;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import static de.uniks.pioneers.GameConstants.*;

public class DevCardNewAnimationController {
    private final Pane root;
    private final ResourceService resourceService;
    private int knight, road, plenty, monopoly, vpoint;

    public DevCardNewAnimationController(Pane root, ResourceService resourceService) {
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

    public void newDevCardAnimationOne(ImageView card, int counter, int devCardNumber) {
        card.setLayoutX(225);
        card.setLayoutY(0);

        new Thread(() -> {
            try {
                Thread.sleep((counter * 1000L) - 1000);
                Platform.runLater(() -> root.getChildren().add(card));

                ScaleTransition st = new ScaleTransition(new Duration(250), card);
                st.setFromX(0f);
                st.setFromY(0f);
                st.setByX(0.5f);
                st.setByY(0.5f);
                st.setOnFinished(t -> newDevCardAnimationTwo(card, devCardNumber));
                st.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void newDevCardAnimationTwo(ImageView card, int devCardNumber) {
        new Thread(() -> {
            try {
                Thread.sleep(500);

                ScaleTransition st = new ScaleTransition(new Duration(250), card);
                st.setFromX(0.5f);
                st.setFromY(0.5f);
                st.setToX(0.08f);
                st.setToY(0.095f);
                st.play();

                TranslateTransition tt = new TranslateTransition(Duration.millis(500), card);
                tt.setToX(4);
                tt.setToY(435);
                tt.setOnFinished(t -> newAfterAnimation(card, devCardNumber));
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void newAfterAnimation(ImageView card, int devCardNumber) {
        Platform.runLater(() -> root.getChildren().remove(card));

        if (devCardNumber == 1) {
            resourceService.updateDevCards(DEV_KNIGHT, knight);
        } else if (devCardNumber == 2) {
            resourceService.updateDevCards(DEV_ROAD, road);
        } else if (devCardNumber == 3) {
            resourceService.updateDevCards(DEV_PLENTY, plenty);
        } else if (devCardNumber == 4) {
            resourceService.updateDevCards(DEV_MONOPOLY, monopoly);
        } else if (devCardNumber == 5) {
            resourceService.updateDevCards(DEV_VPOINT, vpoint);
        }
    }
}

