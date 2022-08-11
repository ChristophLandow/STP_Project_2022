package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.controller.BoardController;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.ExpectedMove;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Point3D;
import de.uniks.pioneers.model.State;
import de.uniks.pioneers.services.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.Objects;
import java.util.Timer;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;

public class IngameStateController {
    private final UserService userService;
    public final IngameService ingameService;
    private final TimerService timerService;
    private final RobberController robberController;
    private final IngameSelectController ingameSelectController;
    private final MapRenderService mapRenderService;
    private final BoardController boardController;
    private final Pane turnPane;
    private final ImageView turnImageView;
    private final Image hourGlassImage, nextImage, nextDisabledImage;
    private final Label situationLabel;
    private final DiceSubcontroller diceSubcontroller;
    private final Game game;
    private final IngameDevelopmentCardController ingameDevelopmentCardController;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final RobberService robberService;
    private final SpeechService speechService;
    private final SimpleBooleanProperty nextDisabled;
    private boolean founding;
    private long time;

    public IngameStateController(UserService userService, IngameService ingameService, TimerService timerService, BoardController boardController, Pane turnPane, RobberController robberController,
                                 ImageView turnImageView, Label situationLabel, DiceSubcontroller diceSubcontroller, Game game, IngameSelectController ingameSelectController, MapRenderService mapRenderService,
                                 RobberService robberService, SpeechService speechService, IngameDevelopmentCardController ingameDevelopmentCardController, ResourceService resourceService) {
        this.userService = userService;
        this.ingameService = ingameService;
        this.timerService = timerService;
        this.robberController = robberController;
        this.ingameSelectController = ingameSelectController;
        this.mapRenderService = mapRenderService;
        this.robberService = robberService;
        this.speechService = speechService;
        this.boardController = boardController;
        this.turnPane = turnPane;
        this.turnImageView = turnImageView;
        this.situationLabel = situationLabel;
        this.diceSubcontroller = diceSubcontroller;
        this.game = game;
        this.ingameDevelopmentCardController = ingameDevelopmentCardController;
        this.nextDisabled = new SimpleBooleanProperty();
        this.addDisableListener();

        this.nextImage = new Image(Objects.requireNonNull(getClass().getResource("images/next.png")).toString());
        this.nextDisabledImage = new Image(Objects.requireNonNull(getClass().getResource("images/next_disabled.png")).toString());
        this.hourGlassImage = new Image(Objects.requireNonNull(getClass().getResource("images/sanduhr.png")).toString());
    }

    public void handleGameState(State currentState) {
        // enable corresponding user to perform their action
        if(currentState.expectedMoves().size() > 0) {
            ExpectedMove move = currentState.expectedMoves().get(0);
            ingameService.setExpectedMove(move);

            assert move.players().get(0)!=null;
            if (move.players().get(0).equals(userService.getCurrentUser()._id())) {
                // enable posting move
                switch (move.action()) {
                    case FOUNDING_ROLL, ROLL -> {
                        this.enableRoll(move.action());
                        this.setDisableEndTurn(true);
                        speechService.play(SPEECH_ROLL_DICE);
                    }
                    case FOUNDING_SETTLEMENT_1, FOUNDING_SETTLEMENT_2 -> {
                        this.founding = true;
                        this.enableBuildingPoints(move.action());
                        this.setDisableEndTurn(true);
                        speechService.play(SPEECH_PLACE_IGLOO);
                    }
                    case FOUNDING_ROAD_1, FOUNDING_ROAD_2, ROAD_MOVE -> {
                        this.founding = true;
                        this.enableStreetPoints(move.action());
                        this.setDisableEndTurn(true);
                        speechService.play(SPEECH_PLACE_STREET);
                    }
                    case BUILD -> {
                        // set builder timer, in progress...
                        this.founding = false;
                        robberService.getRobberState().set(ROBBER_FINISHED);
                        this.timerService.setBuildTimer(new Timer());
                        if(time > 0) {
                            new Thread(() -> {
                                try {
                                    Thread.sleep(time+100);
                                    this.setDisableEndTurn(false);
                                } catch (InterruptedException e) {
                                    throw new RuntimeException(e);
                                }
                            }).start();
                        } else {
                            this.setDisableEndTurn(false);
                        }
                        this.enableEndTurn();
                        this.enableBuildingPoints(move.action());
                        this.enableStreetPoints(move.action());
                        speechService.play(SPEECH_BUILD);
                    }
                    case DROP -> {
                        this.setDisableEndTurn(true);
                        robberService.getRobberState().set(ROBBER_DISCARD);
                        speechService.play(SPEECH_DROP_RESOURCES);
                    }
                    case ROB -> {
                        this.setDisableEndTurn(true);
                        this.enableHexagonPoints();

                        if(robberService.getRobberState().get() != ROBBER_STEAL){
                            speechService.play(GameConstants.SPEECH_MOVE_ROBBER);
                            robberService.getRobberState().set(ROBBER_MOVE);
                        }
                    }
                    case OFFER -> {
                        this.setDisableEndTurn(true);
                        ingameService.tradeIsOffered.set(true);
                        speechService.play(SPEECH_TRADEOFFER);
                    }
                    case ACCEPT -> this.setDisableEndTurn(true);
                    case MONOPOLY_MOVE -> {
                        this.setDisableEndTurn(true);
                        robberController.discardOrChoose(MONOPOLY_NUMBER);
                    }
                    case PLENTY_MOVE -> {
                        this.setDisableEndTurn(true);
                        robberController.discardOrChoose(PLENTY_NUMBER);
                    }
                }
            }

            this.setTime(-1L);
            this.setSituationLabel(move.players().get(0), move.action());
            this.placeRobber(currentState.robber(), move.action());
        }
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
        this.turnPane.setOnMouseClicked(mouseEvent -> endTurn());
    }

    public void setTime(long time) {
        this.time = time;
    }

    private void addDisableListener() {
        this.nextDisabled.addListener((observable, oldValue, newValue) -> {
            if(newValue) {
                this.turnImageView.setImage(nextDisabledImage);
            } else {
                this.turnImageView.setImage(nextImage);
            }

            this.turnPane.setDisable(nextDisabled.get());
        });
    }

    public void setDisableEndTurn(boolean nextDisabled) {
        if(!this.nextDisabled.get() == nextDisabled) {
            this.nextDisabled.set(nextDisabled);
        }
    }

    private void endTurn() {
        boardController.disableBuild();
        ingameSelectController.resetSelect();
        ingameDevelopmentCardController.resetHammerSelection();
        ingameDevelopmentCardController.closeDevCardPlayStage();
        final CreateMoveDto moveDto = new CreateMoveDto(BUILD, null, null, null, null, null);
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
            case ROLL, FOUNDING_ROLL -> actionString = ROLL_DICE;
            case FOUNDING_ROAD_1, FOUNDING_ROAD_2, ROAD_MOVE -> actionString = PLACE_ROAD;
            case FOUNDING_SETTLEMENT_1, FOUNDING_SETTLEMENT_2 -> actionString = PLACE_SETTLEMENT;
            case BUILD -> actionString = BUILD;
            case ROB -> actionString = PLACE_ROBBER;
            case OFFER -> actionString = OFFER;
            case ACCEPT -> actionString = ACCEPT;
            case DROP -> actionString = DROP_CARDS;
            case MONOPOLY_MOVE -> actionString = CHOOSE_MONOPOLY;
            case PLENTY_MOVE -> actionString = CHOOSE_PLENTY;
        }

        if (playerId.equals(userService.getCurrentUser()._id())) {
            playerName = "ME";
            if(!nextDisabled.get() && !founding) {
                this.turnImageView.setImage(nextImage);
            } else {
                this.turnImageView.setImage(nextDisabledImage);
            }
        } else {
            playerName = userService.getUserById(playerId).blockingFirst().name();
            this.turnImageView.setImage(hourGlassImage);
        }
        this.situationLabel.setText(playerName + ":\n" + actionString);
    }

    private void enableRoll(String action) {
        // init dice subcontroller
        this.diceSubcontroller.setAction(action);
        this.diceSubcontroller.activate();
    }

    private void placeRobber(Point3D pos, String action){
        if(pos != null && !action.equals(ROB)) {
            for (HexTileController hexTileController : mapRenderService.getTileControllers()) {
                HexTile tile = hexTileController.tile;
                hexTileController.setRobber(pos.x() == tile.q && pos.y() == tile.s && pos.z() == tile.r);

                if(pos.x() == tile.q && pos.y() == tile.s && pos.z() == tile.r){
                    this.robberService.moveRobber(hexTileController);
                }
            }
        }
    }
}
