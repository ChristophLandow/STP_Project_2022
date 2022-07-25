package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.Main;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.SpeechService;
import javafx.collections.MapChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;

import javax.inject.Inject;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class VictoryPointController {
    @FXML VBox placeVBox;
    @FXML Label firstLabel, secondLabel, thirdLabel, firstPlaceLabel, secondPlaceLabel, thirdPlaceLabel, firstPointsLabel, secondPointsLabel, thirdPointsLabel;
    @FXML Pane secondPlacePane, thirdPlacePane;
    @FXML ImageView firstImageView, secondImageView, thirdImageView;
    @FXML Circle firstColorCircle, secondColorCircle, thirdColorCircle;
    private final GameService gameService;
    private int victoryPoints, winnerPoints, secondPoints, thirdPoints;
    private List<User> users;
    private String winnerID, secondID, thirdID;
    private Stage stage;
    private Pane root;
    private LeaveGameController leaveGameController;

    @Inject SpeechService speechService;

    @Inject
    public VictoryPointController(GameService gameService) {
        this.gameService = gameService;
    }

    public void showVictoryPopUp(String winner) {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/viewElements/VictoryPopUp.fxml"));
        loader.setControllerFactory(c -> this);
        Parent view = null;
        try {
            view = loader.load();
        } catch (IOException e) {
            e.printStackTrace();
        }

        stage = new Stage();
        stage.setOnCloseRequest(c -> onContinueClicked());
        stage.setTitle(winner + " wins!");
        assert view != null;
        Scene scene = new Scene(view);
        stage.setScene(scene);

        stage.show();
    }

    public void init(List<User> users, Pane root, LeaveGameController leaveGameController) {
        this.leaveGameController = leaveGameController;
        this.root = root;
        this.users = users;
        if(gameService.victoryPoints > 0) {
            this.victoryPoints = gameService.victoryPoints;
        } else {
            this.victoryPoints = gameService.getGame().settings().victoryPoints();
        }

        addPlayerListener();
    }

    private void addPlayerListener() {
        // add listener for observable players list
        gameService.players.addListener((MapChangeListener<? super String, ? super Player>) c -> {
            if(!gameService.wonGame) {
                if(c.getValueAdded() != null && c.getValueAdded().victoryPoints() == victoryPoints) {
                    speechService.play(GameConstants.SPEECH_WINNER);
                    gameService.wonGame = true;
                    winnerID = c.getKey();
                    winnerPoints = c.getValueAdded().victoryPoints();
                    showVictoryPopUp(users.stream().filter(p -> p._id().equals(winnerID)).findFirst().orElseThrow().name());
                    checkSecondThird();
                }
            }
        });
    }

    public void checkSecondThird() {
        HashMap<String, Integer> pointMap = new HashMap<>();
        root.setDisable(true);

        for(var entry : gameService.players.entrySet()) {
            pointMap.put(entry.getKey(), entry.getValue().victoryPoints());
        }

        List<Map.Entry<String, Integer>> rankingList = pointMap.entrySet()
                .stream()
                .sorted(Map.Entry.comparingByValue())
                .toList();

        if(rankingList.size() >= 3) {
            secondID = rankingList.get(rankingList.size()-2).getKey();
            secondPoints = rankingList.get(rankingList.size()-2).getValue();
            thirdID = rankingList.get(rankingList.size()-3).getKey();
            thirdPoints = rankingList.get(rankingList.size() - 3).getValue();

            if(!(secondPoints > thirdPoints)) {
                thirdPlaceLabel.setText(secondPlaceLabel.getText());
            }

            renderSecond();
            renderThird();

        } else if(rankingList.size() == 2) {
            placeVBox.getChildren().remove(thirdPlacePane);
            secondID = rankingList.get(0).getKey();
            secondPoints = rankingList.get(0).getValue();

            renderSecond();
        } else {
            placeVBox.getChildren().remove(secondPlacePane);
            placeVBox.getChildren().remove(thirdPlacePane);
        }

        renderWinner();
    }

    private void renderWinner() {
        User winner = users.stream().filter(p -> p._id().equals(winnerID)).findFirst().orElseThrow();
        firstLabel.setText(winner.name());
        if(!winner.avatar().equals("")) {
            firstImageView.setImage(new Image(winner.avatar()));
        }
        firstColorCircle.setFill(Paint.valueOf(gameService.players.get(winnerID).color()));
        firstPointsLabel.setText("" + winnerPoints);
    }

    private void renderSecond() {
        User second = users.stream().filter(p -> p._id().equals(secondID)).findFirst().orElseThrow();
        secondLabel.setText(second.name());
        if(!second.avatar().equals("")) {
            secondImageView.setImage(new Image(second.avatar()));
        }
        secondColorCircle.setFill(Paint.valueOf(gameService.players.get(secondID).color()));
        secondPointsLabel.setText("" + secondPoints);
    }

    private void renderThird() {
        User third = users.stream().filter(p -> p._id().equals(thirdID)).findFirst().orElseThrow();
        thirdLabel.setText(third.name());
        if(!third.avatar().equals("")) {
            thirdImageView.setImage(new Image(third.avatar()));
        }
        thirdColorCircle.setFill(Paint.valueOf(gameService.players.get(thirdID).color()));
        thirdPointsLabel.setText("" + thirdPoints);
    }

    public void onContinueClicked() {
        leaveGameController.leaveAfterVictory();
        stage.close();
    }
}
