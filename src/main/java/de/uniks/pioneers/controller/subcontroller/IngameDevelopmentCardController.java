package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.ResourceService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class IngameDevelopmentCardController {
    public final Pane hammerPane;
    public final Pane leftPane;
    public final Pane rightPane;
    public final ImageView hammerImageView;
    public final ImageView leftView;
    public final ImageView rightView;
    public final IngameService ingameService;
    public final ResourceService resourceService;
    public final CompositeDisposable disposable = new CompositeDisposable();

    public IngameDevelopmentCardController(Pane hammerPane, Pane leftPane, Pane rightPane, ImageView hammerImageView, ImageView leftView, ImageView rightView, IngameService ingameService, ResourceService resourceService) {
        this.hammerPane = hammerPane;
        this.leftPane = leftPane;
        this.rightPane = rightPane;
        this.hammerImageView = hammerImageView;
        this.leftView = leftView;
        this.rightView = rightView;
        this.ingameService = ingameService;
        this.resourceService = resourceService;

        hammerPane.setOnMouseClicked(this::onHammerClicked);
        rightPane.setOnMouseClicked(this::onRightPaneClicked);
    }

    private void onHammerClicked(MouseEvent mouseEvent) {
        changeVisibility();
        changeHammerBorder();
    }

    private void onRightPaneClicked(MouseEvent mouseEvent) {
        System.out.println(ingameService.game.get()._id());
        if(resourceService.checkDevCard()) {
            disposable.add(ingameService.postMove(ingameService.game.get()._id(), new CreateMoveDto())
                    .observeOn(FX_SCHEDULER)
                    .doOnError(Throwable::printStackTrace)
                    .subscribe()
            );
        }
        changeVisibility();
        changeHammerBorder();
    }

    public void changeVisibility() {
        leftPane.setVisible(!leftPane.isVisible());
        rightPane.setVisible(!rightPane.isVisible());
    }

    public void changeHammerBorder() {
        if(rightPane.isVisible()) {
            hammerPane.setStyle("-fx-border-width: 3; -fx-border-color: lightgreen");
        } else {
            hammerPane.setStyle("-fx-border-width: 1; -fx-border-color: black");
        }
    }
}
