package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.User;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;

import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;
import javafx.util.Duration;

public class GameListElementController {
    @FXML
    public HBox root;
    @FXML
    public TextField creationTime;
    @FXML
    public TextField title;
    @FXML
    public TextField memberCount;

    private Game game;
    private User creator;

    public Game getGame() {
        return game;
    }

    public void getOrCreateGame(Game game, ObservableList<User> users) {
        initGameAndUser(game,users);
        // might be better with rex ex, i gona update this
        String createdAt = game.createdAt();
        int start = createdAt.indexOf("T");
        int end = createdAt.indexOf(".");
        String time = game.createdAt().substring(start + 1, end) + " :";
        creationTime.setText(time);
        // set title to textfield
        title.setText(game.name());
        // set member count to textfield
        String actualMemberCount = String.format("%d/4", game.members());
        memberCount.setText(actualMemberCount);
    }


    private void initGameAndUser(Game game, ObservableList<User> users) {
        if (this.game==null){
            this.game=game;
            try {
                User creator = users.stream().filter(user -> user._id().equals(game.owner())).findAny().get();
                this.creator=creator;
            } catch (Exception e) {
                this.creator=null;
            }
        }else {
            return;
        }
    }


    public void onMouseClicked(MouseEvent mouseEvent) {
    }

    public void showGameInfo(MouseEvent mouseEvent) {
        Tooltip tt = new Tooltip();
        if (this.creator!=null && this.creator.name()!=null){
            String toolTipTxt = String.format("Created by: %s  id: %s", creator.name(),game._id());
            tt.setText(toolTipTxt);
        } else {
            String toolTipTxt = String.format("Created by: %s  id: %s", "unknown",game._id());
            tt.setText(toolTipTxt);
        }

        tt.setStyle("-fx-font: normal bold 4 Langdon; "
                + "-fx-base: #AE3522; "
                + "-fx-text-fill: orange;"
                + "-fx-font-size: 14");
        tt.setShowDelay(Duration.seconds(0.3));
        title.setTooltip(tt);
    }

    public void disableGameInfo(MouseEvent mouseEvent) {
        title.setTooltip(null);
    }
}
