package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.controller.LobbyScreenController;
import de.uniks.pioneers.model.Game;
import javafx.fxml.FXML;

import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.HBox;

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


    public void getOrCreateGame(Game game) {
        this.game = game;
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


    public void onMouseClicked(MouseEvent mouseEvent) {
    }

    public void showGameInfo(MouseEvent mouseEvent) {
    }

    public void disableGameInfo(MouseEvent mouseEvent) {

    }
}
