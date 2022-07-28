package de.uniks.pioneers.controller.subcontroller;

import javafx.animation.FadeTransition;
import javafx.animation.RotateTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.Objects;
import java.util.Random;

import static de.uniks.pioneers.GameConstants.MAP_WIDTH;

public class SnowAnimationControllor {
    private final Random rand;
    private final ArrayList<BuildingPointController> buildingControllers;
    private final ArrayList<StreetPointController> streetPointControllers;
    private final Pane fieldPane;
    private double paneWidth;
    private double paneHeight;

    public SnowAnimationControllor(Pane fieldPane, ArrayList<BuildingPointController> buildingControllers, ArrayList<StreetPointController> streetPointControllers) {
        this.rand = new Random();
        this.buildingControllers = buildingControllers;
        this.streetPointControllers = streetPointControllers;
        this.fieldPane = fieldPane;
        this.paneWidth = fieldPane.getWidth();
        this.paneHeight = fieldPane.getHeight();

        new Thread(() -> {
            try {
                Platform.runLater(this::initSnow);
                Thread.sleep(3000);
                Platform.runLater(this::initSnow);
                Platform.runLater(this::initEventAreas);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    public void refreshPaneBounding() {
        this.paneWidth = fieldPane.getWidth();
        this.paneHeight = fieldPane.getHeight();
    }

    private void initSnow() {
        ImageView[] snowFlakes = new ImageView[20];

        for (int i = 0; i < snowFlakes.length; i++) {
            snowFlakes[i] = new ImageView(new Image(Objects.requireNonNull(getClass().getResource("images/snow" + rand.nextInt(1, 4) + ".png")).toString()));
            fieldPane.getChildren().add(snowFlakes[i]);
            snowFallingAnimation(snowFlakes[i], false);
        }
    }

    public void snowFallingAnimation(ImageView snow, boolean visible) {
        double imageSize = snow.getImage().getWidth();
        double paneWidthMinusImageSize = MAP_WIDTH*2 - imageSize;
        double randomX = rand.nextDouble(-200, paneWidthMinusImageSize);
        int time = 3000 + rand.nextInt(3000);
        snow.setVisible(visible);

        TranslateTransition tt = new TranslateTransition(Duration.millis(time), snow);
        tt.setFromX(randomX);
        tt.setFromY(-100.0);
        if(randomX < 0) {
            tt.setToX(rand.nextDouble(randomX, randomX + 100));
        } else if(randomX > 0 && randomX < 200) {
            tt.setToX(rand.nextDouble(randomX - 100, randomX));
        } else {
            tt.setToX(rand.nextDouble(Math.abs(randomX - paneWidth / 8), Math.min((randomX + paneWidth / 8), paneWidthMinusImageSize)));
        }
        tt.setToY(paneHeight - 7);
        tt.setOnFinished(t -> snowFallingAnimation(snow, true));
        tt.play();

        RotateTransition rt = new RotateTransition(Duration.millis(time), snow);
        rt.setFromAngle(0);
        rt.setToAngle(rand.nextDouble(360));
        rt.play();

        FadeTransition ft = new FadeTransition(Duration.millis(time), snow);
        ft.setFromValue(1.2);
        ft.setToValue(0.35);
        ft.play();
    }

    private void initEventAreas() {
        for(BuildingPointController buildingController : buildingControllers) {
            buildingController.addEventArea();
        }

        for(StreetPointController streetPointController : streetPointControllers) {
            streetPointController.addEventArea();
        }
    }
}