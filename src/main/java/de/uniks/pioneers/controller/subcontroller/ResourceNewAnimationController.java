package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.GameService;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import static de.uniks.pioneers.GameConstants.*;

public class ResourceNewAnimationController {
    private final Pane root;
    private final IngamePlayerResourcesController ingamePlayerResourcesController;
    private final GameService gameService;
    private int ore, lumber, brick, wool, grain;


    public ResourceNewAnimationController(Pane root, IngamePlayerResourcesController ingamePlayerResourcesController, GameService gameService) {
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
