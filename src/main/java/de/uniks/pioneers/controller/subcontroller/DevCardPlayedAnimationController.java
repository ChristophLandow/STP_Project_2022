package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.services.GameService;
import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.util.Duration;

public class DevCardPlayedAnimationController {
    private final Pane root;
    private final GameService gameService;
    private ImageView card;
    private Rectangle box;
    private final ChangeListener<String> stateListener;

    public DevCardPlayedAnimationController(Pane root, GameService gameService) {
        this.root = root;
        this.gameService = gameService;

        stateListener = (observable, oldValue, newValue) -> {
            if(!newValue.equals("build") && !newValue.equals("build_again")) {
                playedDevCardAnimationTwo(card);
            }
        };
    }

    public void playedDevCardAnimationOne(ImageView card, int devCardNumber) {
        this.card = card;
        addMoveActionListener();

        if(devCardNumber == 1) {
            card.setLayoutX(551);
            card.setLayoutY(-145);
            box = new Rectangle(190,229);
            box.setX(691);
            box.setY(29);
        } else {
            card.setLayoutX(552);
            card.setLayoutY(-146);
            box = new Rectangle(184, 229);
            box.setX(695);
            box.setY(31);
        }
        box.setFill(null);
        box.setStroke(Color.LIGHTGREEN);
        box.setStrokeWidth(6);
        box.setArcWidth(20);
        box.setArcHeight(20);

        new Thread(() -> {
            Platform.runLater(() -> root.getChildren().add(card));

            ScaleTransition st = new ScaleTransition(new Duration(250), card);
            st.setFromX(0f);
            st.setFromY(0f);
            st.setByX(0.4f);
            st.setByY(0.4f);
            st.setOnFinished(t -> {
                if(!root.getChildren().contains(box)) {
                    Platform.runLater(() -> root.getChildren().add(box));
                }
            });
            st.play();
        }).start();
    }

    private void addMoveActionListener() {
        gameService.moveAction.addListener(stateListener);
    }

    private void removeMoveActionListener() {
        gameService.moveAction.removeListener(stateListener);
    }

    public void playedDevCardAnimationTwo(ImageView card) {
        new Thread(() -> {
            removeMoveActionListener();

            FadeTransition ftCard = new FadeTransition(Duration.millis(200), card);
            ftCard.setFromValue(1);
            ftCard.setToValue(0);
            ftCard.play();

            FadeTransition ftBox = new FadeTransition(Duration.millis(200), box);
            ftBox.setFromValue(1);
            ftBox.setToValue(0);
            ftBox.setOnFinished(t -> removeAfterAnimation(card, box));
            ftBox.play();
        }).start();
    }

    private void removeAfterAnimation(ImageView card, Rectangle box) {
        Platform.runLater(() -> {
            root.getChildren().remove(box);
            root.getChildren().remove(card);
        });
    }
}

