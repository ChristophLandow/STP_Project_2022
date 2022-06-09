package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;

import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class DiceSubcontroller {
    private ImageView leftDiceView;
    private ImageView rightDiceView;
    private String action;
    
    private final IngameService ingameService;
    private final GameService gameService;
    private final CompositeDisposable disposable = new CompositeDisposable();
    
    @Inject
    public DiceSubcontroller(IngameService ingameService, GameService gameService) {
        this.ingameService = ingameService;
        this.gameService = gameService;
    }
    
    public void init() {
        this.leftDiceView.setOnMouseClicked(this::roll);
        this.rightDiceView.setOnMouseClicked(this::roll);
    }

    private void roll(MouseEvent mouseEvent) {
        CreateMoveDto rollMove = new CreateMoveDto(this.action, null);
        disposable.add(ingameService.postMove(gameService.game.get()._id(), rollMove)
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    this.showRolledNumber(move.roll());
                    this.reset();
                }));
    }

    private void reset() {
        this.leftDiceView.setOnMouseClicked(null);
        this.rightDiceView.setOnMouseClicked(null);
    }

    private void showRolledNumber(int roll) {
        int leftDice, rightDice;
        if ((roll % 2) == 0) {
            leftDice = roll / 2;
            rightDice = leftDice;
        } else {
            leftDice = roll / 2;
            rightDice = leftDice + 1;
        }

        Image leftDiceImage = new Image(Objects.requireNonNull(getClass().getResource("../ingame/" + leftDice + ".png")).toString());
        this.leftDiceView.setImage(leftDiceImage);

        Image rightDiceImage = new Image(Objects.requireNonNull(getClass().getResource("../ingame/" + rightDice + ".png")).toString());
        this.rightDiceView.setImage(rightDiceImage);
    }

    public void setAction(String action) {
        this.action = action;
    }

    public DiceSubcontroller setLeftDiceView(ImageView leftDiceView) {
        this.leftDiceView = leftDiceView;
        return this;
    }

    public DiceSubcontroller setRightDiceView(ImageView rightDiceView) {
        this.rightDiceView = rightDiceView;
        return this;
    }
}
