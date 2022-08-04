package de.uniks.pioneers.controller.PopUpController;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.model.Achievement;
import de.uniks.pioneers.services.AchievementService;
import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Pane;
import javafx.util.Duration;

import javax.inject.Inject;
import java.io.IOException;

import static de.uniks.pioneers.GameConstants.*;

public class AchievementPopUpController implements Controller {

    @FXML public AnchorPane achievementPane;
    @FXML public Label titleLabel, descriptionLabel;
    private final AchievementService achievementService;

    @Inject
    public AchievementPopUpController(AchievementService achievementService){
        this.achievementService = achievementService;
    }

    @Override
    public void init() {
        achievementService.getAchievements().addListener((MapChangeListener<? super String, ? super Achievement>) c -> {
            Achievement achievement = c.getValueAdded();
            if(achievement.progress() == 100 && achievement.unlockedAt() == null){
                switch(achievement.id()){
                    case WINNER_ACHIEVEMENT -> showPopUp(WINNER_ACHIEVEMENT_TITLE, WINNER_ACHIEVEMENT_DESCRIPTION);
                    case CITY_ACHIEVEMENT -> showPopUp(CITY_ACHIEVEMENT_TITLE, CITY_ACHIEVEMENT_DESCRIPTION);
                    case HARBOR_ACHIEVEMENT -> showPopUp(HARBOR_ACHIEVEMENT_TITLE, HARBOR_ACHIEVEMENT_DESCRIPTION);
                    case ROAD_ACHIEVEMENT -> showPopUp(ROAD_ACHIEVEMENT_TITLE, ROAD_ACHIEVEMENT_DESCRIPTION);
                    case SETTLEMENT_ACHIEVEMENT -> showPopUp(SETTLEMENT_ACHIEVEMENT_TITLE, SETTLEMENT_ACHIEVEMENT_DESCRIPTION);
                }

                achievementService.unlockAchievement(achievement.id());
            }
        });
    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/PopUps/AchievementPopUp.fxml"));
        loader.setControllerFactory(c -> this);
        final Parent view;
        try {
            view = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }

        achievementPane.setOpacity(0);

        return view;
    }



    public void showPopUp(String title, String description){
        Pane root = (Pane) achievementPane.getParent();

        achievementPane.toFront();
        titleLabel.setText(title);
        descriptionLabel.setText(description);

        new Thread(() -> {
            try {
                Thread.sleep(100);

                //Animation for showing pop up
                FadeTransition ft = new FadeTransition(Duration.millis(300), achievementPane);
                ft.setDelay(Duration.millis(50));
                ft.setFromValue(0.0f);
                ft.setToValue(1.0f);
                ft.play();

                TranslateTransition tt = new TranslateTransition(Duration.millis(300), achievementPane);
                tt.setFromX((root.getWidth() - achievementPane.getWidth())/2);
                tt.setFromY(-50);
                tt.setToY(20);
                tt.play();

                Thread.sleep(3500);

                //Animation for disappearing of pop up
                ft.setDelay(Duration.millis(0));
                ft.setFromValue(1.0f);
                ft.setToValue(0.0f);
                ft.play();

                tt.setFromX((root.getWidth() - achievementPane.getWidth())/2);
                tt.setFromY(20);
                tt.setToY(-50);
                tt.setOnFinished(e -> achievementPane.setOpacity(0));
                tt.play();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }).start();
    }

    @Override
    public void stop() {

    }
}
