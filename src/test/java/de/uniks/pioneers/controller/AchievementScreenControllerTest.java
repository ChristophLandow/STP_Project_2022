package de.uniks.pioneers.controller;

import de.uniks.pioneers.App;
import de.uniks.pioneers.model.Achievement;
import de.uniks.pioneers.services.AchievementService;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.testfx.framework.junit5.ApplicationTest;
import static de.uniks.pioneers.GameConstants.*;

import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class AchievementScreenControllerTest extends ApplicationTest {

    @Spy
    App app = new App(null);

    @Mock
    AchievementService achievementService;

    @InjectMocks
    AchievementScreenController achievementScreenController;

    @Override
    public void start(Stage stage) {
        app.start(stage);
    }

    @Test
    void renderTest_EmptyAchievements(){
        ObservableMap<String, Achievement> achievements = FXCollections.observableHashMap();
        when(achievementService.getAchievements()).thenReturn(achievements);
        when(achievementService.getChecker()).thenReturn(true);
        achievementScreenController.render();
    }

    @Test
    void renderTest_NotEmptyAchievements(){
        achievementScreenController.cityPlanerDateLabel = new Label();
        achievementScreenController.seaBuilderDateLabel = new Label();
        achievementScreenController.chickenDinnerDateLabel = new Label();
        achievementScreenController.wildWestDateLabel = new Label();
        achievementScreenController.longestRoadDateLabel = new Label();
        achievementScreenController.cityPlanerBox = new VBox();
        achievementScreenController.seaBuilderBox = new VBox();
        achievementScreenController.chickenDinnerBox = new VBox();
        achievementScreenController.wildWestBox = new VBox();
        achievementScreenController.longestRoadBox = new VBox();
        ObservableMap<String, Achievement> achievements = FXCollections.observableHashMap();
        achievements.put(CITY_ACHIEVEMENT,new Achievement("test", "test","test","test","ALongTimeAgoInAGalaxyFarFarAway.",100));
        achievements.put(HARBOR_ACHIEVEMENT,new Achievement("test", "test","test","test","test",33));
        achievements.put(WINNER_ACHIEVEMENT,new Achievement("test", "test","test","test","test",33));
        achievements.put(SETTLEMENT_ACHIEVEMENT,new Achievement("test", "test","test","test","test",33));
        achievements.put(ROAD_ACHIEVEMENT,new Achievement("test", "test","test","test","test",33));
        when(achievementService.getAchievements()).thenReturn(achievements);
        when(achievementService.getChecker()).thenReturn(true);
        achievementScreenController.render();
    }

}
