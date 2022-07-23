package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;

public class IngameDevelopmentCardController {
    private final Pane hammerPane;
    private final Pane leftPane;
    private final Pane rightPane;
    private final ImageView hammerImageView;
    private final ImageView leftView;
    private final ImageView rightView;
    private final IngameService ingameService;
    private final GameService gameService;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public IngameDevelopmentCardController(Pane hammerPane, Pane leftPane, Pane rightPane, ImageView hammerImageView, ImageView leftView, ImageView rightView, IngameService ingameService, GameService gameService) {
        this.hammerPane = hammerPane;
        this.leftPane = leftPane;
        this.rightPane = rightPane;
        this.hammerImageView = hammerImageView;
        this.leftView = leftView;
        this.rightView = rightView;
        this.ingameService = ingameService;
        this.gameService = gameService;

        hammerPane.setOnMouseClicked(this::onHammerClicked);
        rightPane.setOnMouseClicked(this::onRightPaneClicked);
    }

    private void onHammerClicked(MouseEvent mouseEvent) {
        changeVisibility();
        if(rightPane.isVisible()) {
            hammerPane.setStyle("-fx-border-width: 3; -fx-border-color: lightgreen");
        } else {
            hammerPane.setStyle("-fx-border-width: 1; -fx-border-color: black");
        }
    }

    private void onRightPaneClicked(MouseEvent mouseEvent) {
        System.out.println(ingameService.game.get()._id());
        if(gameService.checkDevCard()) {
            disposable.add(ingameService.postMove(ingameService.game.get()._id(), new CreateMoveDto())
                    .observeOn(FX_SCHEDULER)
                    .doOnError(Throwable::printStackTrace)
                    .subscribe()
            );
        }
        changeVisibility();
    }

    public void changeVisibility() {
        leftPane.setVisible(!leftPane.isVisible());
        rightPane.setVisible(!rightPane.isVisible());
    }
}
