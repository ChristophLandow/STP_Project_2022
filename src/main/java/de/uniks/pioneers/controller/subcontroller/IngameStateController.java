package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.controller.BoardController;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.ExpectedMove;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Point3D;
import de.uniks.pioneers.model.State;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.MapRenderService;
import de.uniks.pioneers.services.TimerService;
import de.uniks.pioneers.services.UserService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import java.util.Objects;
import java.util.Timer;
import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;
import static de.uniks.pioneers.GameConstants.BUILD;

public class IngameStateController {
    private final UserService userService;
    private final IngameService ingameService;
    private final TimerService timerService;
    private final MapRenderService mapRenderService;
    private final BoardController boardController;
    private final Pane turnPane;
    private final ImageView hourglassImageView;
    private final Label situationLabel;
    private final DiceSubcontroller diceSubcontroller;
    private final Game game;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public IngameStateController(UserService userService, IngameService ingameService, TimerService timerService, BoardController boardController, Pane turnPane,
                                 ImageView hourglassImageView, Label situationLabel, DiceSubcontroller diceSubcontroller, Game game, MapRenderService mapRenderService) {
        this.userService = userService;
        this.ingameService = ingameService;
        this.timerService = timerService;
        this.mapRenderService = mapRenderService;
        this.boardController = boardController;
        this.turnPane = turnPane;
        this.hourglassImageView = hourglassImageView;
        this.situationLabel = situationLabel;
        this.diceSubcontroller = diceSubcontroller;
        this.game = game;
    }

    public void handleGameState(State currentState) {
        // enable corresponding user to perform their action
        ExpectedMove move = currentState.expectedMoves().get(0);
        if (move.players().get(0).equals(userService.getCurrentUser()._id())) {
            // enable posting move
            switch (move.action()) {
                case FOUNDING_ROLL, ROLL -> this.enableRoll(move.action());
                case FOUNDING_SETTLEMENT_1, FOUNDING_SETTLEMENT_2 -> this.enableBuildingPoints(move.action());
                case FOUNDING_ROAD_1, FOUNDING_ROAD_2 -> this.enableStreetPoints(move.action());
                case BUILD -> {
                    // set builder timer, in progress...
                    this.timerService.setBuildTimer(new Timer());
                    this.enableEndTurn();
                    this.enableBuildingPoints(move.action());
                    this.enableStreetPoints(move.action());
                }
                case ROB -> this.enableHexagonPoints();
            }
        }

        this.setSituationLabel(move.players().get(0), move.action());
        this.placeRobber(currentState.robber());
    }

    private void enableHexagonPoints(){
        this.boardController.enableHexagonPoints();
    }

    private void enableStreetPoints(String action) {
        this.boardController.enableStreetPoints(action);
    }

    private void enableBuildingPoints(String action) {
        this.boardController.enableBuildingPoints(action);
    }

    private void enableEndTurn() {
        this.turnPane.setOnMouseClicked(this::endTurn);
    }

    private void endTurn(MouseEvent mouseEvent) {
        final CreateMoveDto moveDto = new CreateMoveDto(BUILD, null, null, null, null);
        disposable.add(ingameService.postMove(game._id(), moveDto)
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    this.turnPane.setOnMouseClicked(null);
                    this.timerService.reset();
                })
        );
    }
    private void setSituationLabel(String playerId, String action) {
        // set game state label
        String playerName;
        String actionString = "";
        switch (action) {
            case ROLL, FOUNDING_ROLL -> actionString = "roll the dice";
            case FOUNDING_ROAD_1, FOUNDING_ROAD_2 -> actionString = "place road";
            case FOUNDING_SETTLEMENT_1, FOUNDING_SETTLEMENT_2 -> actionString = "place settlement";
            case BUILD -> actionString = BUILD;
            case ROB -> actionString = "place robber";
        }

        if (playerId.equals(userService.getCurrentUser()._id())) {
            playerName = "ME";
            this.hourglassImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("images/next.png")).toString()));
        } else {
            playerName = userService.getUserById(playerId).blockingFirst().name();
            this.hourglassImageView.setImage(new Image(Objects.requireNonNull(getClass().getResource("images/sanduhr.png")).toString()));
        }
        this.situationLabel.setText(playerName + ":\n" + actionString);
    }

    private void enableRoll(String action) {
        // init dice subcontroller
        this.diceSubcontroller.setAction(action);
        this.diceSubcontroller.activate();
    }

    private void placeRobber(Point3D pos){
        if(pos != null) {
            for (HexTileController hexTileController : mapRenderService.getTileControllers()) {
                HexTile tile = hexTileController.tile;
                hexTileController.setRobber(pos.x() == tile.q && pos.y() == tile.s && pos.z() == tile.r);

                if(pos.x() == tile.q && pos.y() == tile.s && pos.z() == tile.r){
                    hexTileController.moveRobber();
                }
            }
        }
    }
}
