package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Achievement;
import de.uniks.pioneers.services.AchievementService;
import de.uniks.pioneers.services.IngameService;
import javafx.collections.ObservableMap;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import static de.uniks.pioneers.GameConstants.*;

@Singleton
public class AchievementScreenController implements Controller {

    private final IngameService ingameService;
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
    public AchievementScreenController(App app, IngameService ingameService, AchievementService achievementService, Provider<LobbyScreenController> lobbyScreenControllerProvider){
        this.achievemetService = achievementService;
        this.ingameService = ingameService;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.app = app;
    }

    @Override
    public void init() {
        achievements = achievemetService.getAchievements();
        //set unlock dates and check-icon or unlock status
        setAchievement(cityPlanerDateLabel, cityPlanerBox, achievements.get(CITY_ACHIEVEMENT));
        setAchievement(longestRoadDateLabel, longestRoadBox, achievements.get(ROAD_ACHIEVEMENT));
        setAchievement(wildWestDateLabel, wildWestBox, achievements.get(SETTLEMENT_ACHIEVEMENT));
        setAchievement(chickenDinnerDateLabel, chickenDinnerBox, achievements.get(WINNER_ACHIEVEMENT));
        //...for Harbor-Achievement extra, cause of lower progress lvl
        if(achievements.get(HARBOR_ACHIEVEMENT).progress() == 50){
            seaBuilderDateLabel.setText(achievements.get(HARBOR_ACHIEVEMENT).unlockedAt());
            seaBuilderBox.getChildren().add(new ImageView());
        } else {
            Label statusLabel = new Label();
            statusLabel.setText(String.valueOf(achievements.get(HARBOR_ACHIEVEMENT).progress()) + "/ 50");
            cityPlanerDateLabel.setText("");
            cityPlanerBox.getChildren().add(statusLabel);
        }

    }

    public void setAchievement(Label dateLabel, VBox box, Achievement achievement){
        if(achievement.progress() == 100){
            dateLabel.setText(achievement.unlockedAt());
            box.getChildren().add(new ImageView());
        } else {
            Label statusLabel = new Label();
            statusLabel.setText(achievements.get(ROAD_ACHIEVEMENT).progress() + "/ 100");
            dateLabel.setText("");
            box.getChildren().add(statusLabel);
        }
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

    public void toLobby(ActionEvent actionEvent) {
        LobbyScreenController lobbyScreenController = lobbyScreenControllerProvider.get();
        app.show(lobbyScreenController);
    }
}
