package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.Move;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.TimerService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.animation.Interpolator;
import javafx.animation.RotateTransition;
import javafx.collections.ListChangeListener;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.util.Duration;
import static de.uniks.pioneers.GameConstants.*;

import javax.inject.Inject;

import java.util.Objects;
import java.util.Timer;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.FOUNDING_ROLL;
import static de.uniks.pioneers.GameConstants.ROLL;

public class DiceSubcontroller {
    private ImageView leftDiceView;
    private ImageView rightDiceView;
    private String action;
    
    private final IngameService ingameService;
    private final GameService gameService;
    private final TimerService timerService;
    private final CompositeDisposable disposable = new CompositeDisposable();
    
    @Inject
    public DiceSubcontroller(IngameService ingameService, GameService gameService,
                             TimerService timerService) {
        this.ingameService = ingameService;
        this.gameService = gameService;
        this.timerService = timerService;
    }
    
    public void init() {
        // moves change listener
        gameService.moves.addListener((ListChangeListener<? super Move>) c -> {
            c.next();
            if (c.wasAdded()) {
                c.getAddedSubList().forEach(move -> {
                    if (move.action().equals(FOUNDING_ROLL) || move.action().equals(ROLL)) {
                        showRolledNumber(move.action(), move.roll());
                        if(move.roll() == ROBBER_NUMBER){

                        }
                    }
                });
            }
        });
    }

    public void activate() {
        this.leftDiceView.setOnMouseClicked(this::roll);
        this.rightDiceView.setOnMouseClicked(this::roll);
        this.timerService.setRollTimer(this.action, new Timer());
    }

    private void roll(MouseEvent mouseEvent) {
        CreateMoveDto rollMove = new CreateMoveDto(this.action, null, null);
        disposable.add(ingameService.postMove(gameService.game.get()._id(), rollMove)
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    timerService.reset();
                    this.reset();
                }));
    }

    private void reset() {
        this.leftDiceView.setOnMouseClicked(null);
        this.rightDiceView.setOnMouseClicked(null);
    }

    private void showRolledNumber(String action, int roll) {
        int leftDice, rightDice;

        Image leftDiceImage, rightDiceImage;
        // case distinction for founding roll
        if (action.equals(FOUNDING_ROLL)) {
            leftDice = roll;
            this.rightDiceView.setImage(null);
        } else {
            if ((roll % 2) == 0) {
                leftDice = roll / 2;
                rightDice = leftDice;
            } else {
                leftDice = roll / 2;
                rightDice = leftDice + 1;
            }
            rightDiceImage = new Image(Objects.requireNonNull(getClass().getResource(rightDice + ".png")).toString());
            this.rightDiceView.setImage(rightDiceImage);
        }
        animateDice();
        leftDiceImage = new Image(Objects.requireNonNull(getClass().getResource(leftDice + ".png")).toString());
        this.leftDiceView.setImage(leftDiceImage);

    }

    private void animateDice() {
        // animate dice rolling
        RotateTransition rotateLeftDice = new RotateTransition();
        rotateLeftDice.setNode(this.leftDiceView);
        rotateLeftDice.setDuration(Duration.millis(300));
        rotateLeftDice.setCycleCount(2);
        rotateLeftDice.setInterpolator(Interpolator.LINEAR);
        rotateLeftDice.setByAngle(360);
        rotateLeftDice.play();

        RotateTransition rotateRightDice = new RotateTransition();
        rotateRightDice.setNode(this.rightDiceView);
        rotateRightDice.setDuration(Duration.millis(300));
        rotateRightDice.setCycleCount(2);
        rotateRightDice.setInterpolator(Interpolator.LINEAR);
        rotateRightDice.setByAngle(360);
        rotateRightDice.play();
    }

    public void setAction(String action) {
        this.action = action;
    }

    public DiceSubcontroller setLeftDiceView(ImageView leftDiceView) {
        this.leftDiceView = leftDiceView;
        return this;
    }

    public void setRightDiceView(ImageView rightDiceView) {
        this.rightDiceView = rightDiceView;
    }
}
