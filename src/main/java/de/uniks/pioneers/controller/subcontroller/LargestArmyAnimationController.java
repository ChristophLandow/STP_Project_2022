package de.uniks.pioneers.controller.subcontroller;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.application.Platform;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class LargestArmyAnimationController {
    private final Pane root;
    private ImageView card;

    public LargestArmyAnimationController(Pane root) {
        this.root = root;
    }

    public void largestArmyAnimationOne(ImageView card) {
        card.setLayoutX(200);
        card.setLayoutY(0);
        this.card = card;

        new Thread(() -> {
            Platform.runLater(() -> root.getChildren().add(card));
            ScaleTransition st = new ScaleTransition(new Duration(400), card);

            st.setFromX(0f);
            st.setFromY(0f);
            st.setByX(0.5f);
            st.setByY(0.5f);
            st.setOnFinished(t -> card.setOnMouseClicked(this::onCardClickedAnimation));
            st.play();
        }).start();
    }

    private void onCardClickedAnimation(MouseEvent mouseEvent) {
        new Thread(() -> {
            FadeTransition ftCard = new FadeTransition(Duration.millis(250), card);
            ftCard.setFromValue(1);
            ftCard.setToValue(0);
            ftCard.play();
        }).start();

        Platform.runLater(() -> root.getChildren().remove(card));
    }
}
