package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.GameService;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import static de.uniks.pioneers.GameConstants.*;

public class ResourceNewAnimationController {
    private final Pane root;
    private final GameService gameService;
    private int ore, lumber, brick, wool, grain;

    public ResourceNewAnimationController(Pane root, GameService gameService) {
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

    public void newResourceCardAnimation(ImageView card, int counter, int resNumber, String moveAction) {
        if(moveAction.equals("accept") || moveAction.equals("rob")) {
            newTradeResourceCardAnimationOne(card, counter, resNumber);
        } else {
            newResourceCardAnimationOne(card, counter, resNumber);
        }
    }

    public void newResourceCardAnimationOne(ImageView card, int counter, int resNumber) {
        card.setLayoutX(180);
        card.setLayoutY(-100);

        new Thread(() -> {
            try {
                Thread.sleep((counter * 1000L) - 1000);
                Platform.runLater(() -> root.getChildren().add(card));

                ScaleTransition st = new ScaleTransition(new Duration(250), card);
                st.setFromX(0f);
                st.setFromY(0f);
                st.setByX(0.2f);
                st.setByY(0.2f);
                st.setOnFinished(t -> newResourceCardAnimationTwo(card, resNumber));
                st.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void newResourceCardAnimationTwo(ImageView card, int resNumber) {
        new Thread(() -> {
            try {
                Thread.sleep(250);

                ScaleTransition st = new ScaleTransition(new Duration(500), card);
                st.setFromX(0.2f);
                st.setFromY(0.2f);
                st.setToX(0.06f);
                st.setToY(0.06f);
                st.play();

                TranslateTransition tt = new TranslateTransition(Duration.millis(500), card);
                tt.setToX(0);
                tt.setToY(409);
                tt.setOnFinished(t -> newAfterAnimation(card, resNumber));
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void newTradeResourceCardAnimationOne(ImageView card, int counter, int resNumber) {
        card.setLayoutX(738);
        card.setLayoutY(74);
        card.setScaleX(0.37f);
        card.setScaleY(0.37f);

        new Thread(() -> {
            try {
                Thread.sleep((counter * 1000L) - 1000);
                Platform.runLater(() -> root.getChildren().add(card));

                FadeTransition ft = new FadeTransition(Duration.millis(100), card);
                ft.setFromValue(0);
                ft.setToValue(1);
                ft.setOnFinished(t -> newTradeResourceCardAnimationTwo(card, resNumber));
                ft.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void newTradeResourceCardAnimationTwo(ImageView card, int resNumber) {
        new Thread(() -> {
            ScaleTransition st = new ScaleTransition(new Duration(350), card);
            st.setFromX(0.37f);
            st.setFromY(0.37f);
            st.setToX(0.2f);
            st.setToY(0.2f);
            st.play();

            TranslateTransition tt = new TranslateTransition(Duration.millis(350), card);
            tt.setToX(-557);
            tt.setToY(-174);
            tt.setOnFinished(t -> newTradeResourceCardAnimationThree(card, resNumber));
            tt.play();
        }).start();
    }

    public void newTradeResourceCardAnimationThree(ImageView card, int resNumber) {
        new Thread(() -> {
            try {
                Thread.sleep(250);

                ScaleTransition st = new ScaleTransition(new Duration(300), card);
                st.setFromX(0.2f);
                st.setFromY(0.2f);
                st.setToX(0.06f);
                st.setToY(0.06f);
                st.play();

                TranslateTransition tt = new TranslateTransition(Duration.millis(300), card);
                tt.setToX(-557);
                tt.setToY(235);
                tt.setOnFinished(t -> newAfterAnimation(card, resNumber));
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void newAfterAnimation(ImageView card, int resNumber) {
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
