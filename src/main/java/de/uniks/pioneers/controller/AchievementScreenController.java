package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Achievement;
import de.uniks.pioneers.services.AchievementService;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;

import javax.inject.Inject;
import javax.inject.Provider;
import java.io.File;
import java.io.IOException;

import static de.uniks.pioneers.GameConstants.*;

public class AchievementScreenController implements Controller {

    @FXML Label cityPlanerDateLabel;
    @FXML Label longestRoadDateLabel;
    @FXML Label seaBuilderDateLabel;
    @FXML Label wildWestDateLabel;
    @FXML Label chickenDinnerDateLabel;

    @FXML VBox cityPlanerBox;
    @FXML VBox longestRoadBox;
    @FXML VBox seaBuilderBox;
    @FXML VBox wildWestBox;
    @FXML VBox chickenDinnerBox;
    private final AchievementService achievemetService;
    private final Provider<LobbyScreenController> lobbyScreenControllerProvider;
    private final App app;
    private ObservableMap<String, Achievement> achievements;

    @Inject
    public AchievementScreenController(App app, AchievementService achievementService, Provider<LobbyScreenController> lobbyScreenControllerProvider){
        this.achievemetService = achievementService;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.app = app;
    }

    @Override
    public void init() {
        achievements = achievemetService.getAchievements();
        if(achievements.isEmpty()){
            setEmptyAchievements(cityPlanerDateLabel,cityPlanerBox);
            setEmptyAchievements(longestRoadDateLabel,longestRoadBox);
            //setEmptyAchievements(seaBuilderDateLabel,seaBuilderBox);
            setEmptyAchievements(wildWestDateLabel,wildWestBox);
            setEmptyAchievements(chickenDinnerDateLabel,chickenDinnerBox);
        } else {
            //set unlock dates and check-icon or unlock status
            setAchievement(cityPlanerDateLabel, cityPlanerBox, achievements.get(CITY_ACHIEVEMENT));
            setAchievement(longestRoadDateLabel, longestRoadBox, achievements.get(ROAD_ACHIEVEMENT));
            setAchievement(wildWestDateLabel, wildWestBox, achievements.get(SETTLEMENT_ACHIEVEMENT));
            setAchievement(chickenDinnerDateLabel, chickenDinnerBox, achievements.get(WINNER_ACHIEVEMENT));
            //...for Harbor-Achievement extra, cause of lower progress lvl
            if(achievements.get(HARBOR_ACHIEVEMENT).progress() == 50){
                seaBuilderDateLabel.setText(achievements.get(HARBOR_ACHIEVEMENT).unlockedAt());
                Image image = new Image("de/uniks/pioneers/checkmark.png");
                ImageView imageView = new ImageView();
                imageView.setFitHeight(40);
                imageView.setFitWidth(40);
                imageView.setImage(image);
                seaBuilderBox.getChildren().add(imageView);
            } else {
                Label statusLabel = new Label();
                statusLabel.setText(achievements.get(HARBOR_ACHIEVEMENT).progress() + "/ 50");
                statusLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 20));
                seaBuilderDateLabel.setText("");
                seaBuilderBox.getChildren().add(statusLabel);
            }
        }
    }

    public void setAchievement(Label dateLabel, VBox box, Achievement achievement){
        if(achievement.progress() == 100){
            dateLabel.setText(achievement.unlockedAt());
            Image image = new Image("de/uniks/pioneers/checkmark.png");
            ImageView imageView = new ImageView();
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
            imageView.setImage(image);
            seaBuilderBox.getChildren().add(imageView);
            box.getChildren().add(imageView);
        } else {
            Label statusLabel = new Label();
            statusLabel.setText(achievements.get(ROAD_ACHIEVEMENT).progress() + "/ 100");
            statusLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 20));
            dateLabel.setText("");
            box.getChildren().add(statusLabel);
        }
    }

    public void setEmptyAchievements(Label dateLabel, VBox box){
        Label statusLabel = new Label();
        statusLabel.setFont(Font.font(Font.getDefault().getFamily(), FontWeight.BOLD, 20));
        dateLabel.setText("");
        statusLabel.setText("0 / 100");
        box.getChildren().add(statusLabel);
    }

    @Override
    public void stop() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/AchievementsScreen.fxml"));
        loader.setControllerFactory(c->this);
        final Parent achievementsView;
        try {
            achievementsView =  loader.load();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
        return achievementsView;
    }

    public void toLobby() {
        LobbyScreenController lobbyScreenController = lobbyScreenControllerProvider.get();
        app.show(lobbyScreenController);
    }
}