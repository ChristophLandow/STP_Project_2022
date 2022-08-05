package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.services.AchievementService;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import javax.inject.Inject;
import javax.inject.Provider;
import javax.inject.Singleton;
import java.io.IOException;

@Singleton
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

    @Inject
    public AchievementScreenController(App app, AchievementService achievementService, Provider<LobbyScreenController> lobbyScreenControllerProvider){
        this.achievemetService = achievementService;
        this.lobbyScreenControllerProvider = lobbyScreenControllerProvider;
        this.app = app;
    }

    @Override
    public void init() {

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
