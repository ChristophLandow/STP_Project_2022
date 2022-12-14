package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Achievement;
import de.uniks.pioneers.services.AchievementService;
import de.uniks.pioneers.services.StylesService;
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
import java.io.IOException;

import static de.uniks.pioneers.Constants.ACHIEVEMENTS_SCREEN_TITLE;
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
    private final StylesService stylesService;
    private final App app;
    private ObservableMap<String, Achievement> achievements;

    @Inject
    public AchievementScreenController(App app, StylesService stylesService, AchievementService achievementService, Provider<LobbyScreenController> lobbyScreenControllerProvider){
        this.achievemetService = achievementService;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.stylesService = stylesService;
        this.app = app;
    }

    @Override
    public void init() {
        app.getStage().setTitle(ACHIEVEMENTS_SCREEN_TITLE);
        stylesService.setStyleSheets(this.app.getStage().getScene().getStylesheets());
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
        achievements = achievemetService.getAchievements();
        if(achievements.isEmpty()){
            if(achievemetService.getChecker().equals(true)){
                setEmptyAchievements(cityPlanerDateLabel, cityPlanerBox);
                setEmptyAchievements(longestRoadDateLabel, longestRoadBox);
                setEmptyAchievements(seaBuilderDateLabel, seaBuilderBox);
                setEmptyAchievements(wildWestDateLabel, wildWestBox);
                setEmptyAchievements(chickenDinnerDateLabel, chickenDinnerBox);
                return achievementsView;
            }
        }
        if(achievemetService.getChecker().equals(false)){
            achievemetService.getMapLoadedChecker().addListener((mapLoadedListener, oldValue, newValue) -> {
                achievements = achievemetService.getAchievements();
                loadAchievements();
            });
        } else {
            achievements = achievemetService.getAchievements();
            loadAchievements();
        }
        return achievementsView;
    }

    public void loadAchievements(){
        //set unlock dates and check-icon or unlock status
        setAchievement(cityPlanerDateLabel, cityPlanerBox, achievements.get(CITY_ACHIEVEMENT), CITY_ACHIEVEMENT);
        setAchievement(longestRoadDateLabel, longestRoadBox, achievements.get(ROAD_ACHIEVEMENT), ROAD_ACHIEVEMENT);
        setAchievement(wildWestDateLabel, wildWestBox, achievements.get(SETTLEMENT_ACHIEVEMENT), SETTLEMENT_ACHIEVEMENT);
        setAchievement(chickenDinnerDateLabel, chickenDinnerBox, achievements.get(WINNER_ACHIEVEMENT), WINNER_ACHIEVEMENT);
        setAchievement(seaBuilderDateLabel, seaBuilderBox, achievements.get(HARBOR_ACHIEVEMENT), HARBOR_ACHIEVEMENT);
    }

    public void setAchievement(Label dateLabel, VBox box, Achievement achievement, String kind){
        if(achievement.progress() >= 100){
            dateLabel.setText(achievement.unlockedAt().substring(0,10));
            Image image = new Image("de/uniks/pioneers/checkmark.png");
            ImageView imageView = new ImageView();
            imageView.setFitHeight(40);
            imageView.setFitWidth(40);
            imageView.setImage(image);
            seaBuilderBox.getChildren().add(imageView);
            box.getChildren().add(imageView);
        } else {
            Label statusLabel = new Label();
            statusLabel.setText(achievements.get(kind).progress() + "/ 100");
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

    public void toLobby() {
        LobbyScreenController lobbyScreenController = lobbyScreenControllerProvider.get();
        app.show(lobbyScreenController);
    }
}