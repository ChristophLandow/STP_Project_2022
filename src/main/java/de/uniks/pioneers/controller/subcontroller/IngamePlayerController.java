package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.services.UserService;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.image.ImageView;
import javafx.scene.shape.SVGPath;
import javax.inject.Provider;

public class IngamePlayerController {
    private final UserService userService;
    private final LeaveGameController leaveGameController;
    private final Provider<IngamePlayerListElementController> elementProvider;
    private final ListView<Node> playerListView;
    private final Provider<IngamePlayerListSpectatorController> spectatorProvider;
    private final Game game;
    private final ImageView hammerImageView;
    private final Label streetCountLabel;
    private final Label houseCountLabel;
    private final Label cityCountLabel;
    private final SVGPath streetSVG;
    private final SVGPath citySVG;
    private final SVGPath houseSVG;
    private final ImageView tradeImageView;
    private final ImageView hourglassImageView;
    private final ImageView nextTurnImageView;

    public IngamePlayerController(UserService userService, LeaveGameController leaveGameController, Provider<IngamePlayerListElementController> elementProvider, ListView<Node> playerListView,
                                   Provider<IngamePlayerListSpectatorController> spectatorProvider, Game game, ImageView hammerImageView, Label streetCountLabel, Label houseCountLabel,
                                   Label cityCountLabel, SVGPath streetSVG, SVGPath citySVG, SVGPath houseSVG, ImageView tradeImageView, ImageView hourglassImageView, ImageView nextTurnImageView) {
        this.userService = userService;
        this.leaveGameController = leaveGameController;
        this.elementProvider = elementProvider;
        this.playerListView = playerListView;
        this.spectatorProvider = spectatorProvider;
        this.game = game;
        this.hammerImageView = hammerImageView;
        this.streetCountLabel = streetCountLabel;
        this.houseCountLabel = houseCountLabel;
        this.cityCountLabel = cityCountLabel;
        this.streetSVG = streetSVG;
        this.citySVG = citySVG;
        this.houseSVG = houseSVG;
        this.tradeImageView = tradeImageView;
        this.hourglassImageView = hourglassImageView;
        this.nextTurnImageView = nextTurnImageView;
    }

    public void renderPlayer(Player player) {
        IngamePlayerListElementController playerListElement = elementProvider.get();
        playerListElement.nodeListView = playerListView;
        playerListElement.render(player.userId());
    }

    public void deleteSpectator(Member member) {
        Node removal = playerListView.getItems().stream().filter(node -> node.getId().equals(member.userId())).findAny().orElse(null);
        playerListView.getItems().remove(removal);

        if(member.userId().equals(userService.getCurrentUser()._id()) && userService.isSpectator()) {
            leaveGameController.setKicked(true);
            leaveGameController.leave();
        }
    }

    public void renderSpectator(Member member) {
        if(member.spectator()) {
            if(userService.getCurrentUser()._id().equals(member.userId())) {
                hammerImageView.setVisible(false);
                streetCountLabel.setVisible(false);
                houseCountLabel.setVisible(false);
                cityCountLabel.setVisible(false);
                streetSVG.setVisible(false);
                citySVG.setVisible(false);
                houseSVG.setVisible(false);
                tradeImageView.setVisible(false);
                hourglassImageView.setVisible(false);
                nextTurnImageView.setVisible(false);
            }

            IngamePlayerListSpectatorController spectatorListElement = spectatorProvider.get();
            spectatorListElement.setNodeListView(playerListView);
            spectatorListElement.init(game._id(), member.userId());
            spectatorListElement.render(game.owner());
        }
    }
}
