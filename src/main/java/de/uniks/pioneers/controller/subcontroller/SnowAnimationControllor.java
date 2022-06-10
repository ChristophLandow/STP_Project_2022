package de.uniks.pioneers.controller.subcontroller;

import java.util.Objects;
import java.util.Random;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

public class SnowAnimationControllor {
    private final Random rand = new Random();
    private final Pane fieldPane;

    public SnowAnimationControllor(Pane fieldPane) {
        this.fieldPane = fieldPane;

        new Thread(() -> {
            try {
                Platform.runLater(this::initSnow);
                Thread.sleep(2000);
                Platform.runLater(this::initSnow);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    private void initSnow() {
        ImageView[] snowFlakes = new ImageView[15];

        for (int i = 0; i < snowFlakes.length; i++) {
            snowFlakes[i] = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/snow" + rand.nextInt(1, 4) + ".png")).toString()));
            fieldPane.getChildren().add(snowFlakes[i]);
            snowFallingAnimation(snowFlakes[i], false);
        }
    }

    public void snowFallingAnimation(ImageView snow, boolean visible) {
        double imageSize = snow.getImage().getWidth();
        double paneWidth = fieldPane.getWidth();
        double paneHeight = fieldPane.getHeight();
        double paneWidthMinusImageSize = fieldPane.getWidth() - imageSize;
        double randomX = rand.nextDouble(paneWidthMinusImageSize);
        int time = 2000 + rand.nextInt(2000);
        snow.setVisible(visible);

        TranslateTransition tt = new TranslateTransition(Duration.millis(time), snow);
        tt.setFromX(randomX);
        tt.setFromY(-100.0);
        tt.setToX(rand.nextDouble(Math.abs(randomX - paneWidth/8), Math.min((randomX + paneWidth/8), paneWidthMinusImageSize)));
        tt.setToY(paneHeight - 7);
        tt.setOnFinished(t -> snowFallingAnimation(snow, true));
        tt.play();
    }
}