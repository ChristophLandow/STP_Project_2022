package de.uniks.pioneers.controller.subcontroller;

import de.uniks.pioneers.Main;
import de.uniks.pioneers.controller.Controller;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.model.DevelopmentCard;
import de.uniks.pioneers.services.*;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;

public class IngameDevelopmentCardController implements Controller {
    @FXML ImageView plentyView, knightView, monopolyView, roadView, plentyViewMono, knightViewMono, monopolyViewMono, roadViewMono;
    @FXML Rectangle plentyRect, knightRect, monopolyRect, roadRect;
    @FXML Button playButton;
    public final Pane hammerPane;
    public final Pane leftPane;
    public final Pane rightPane;
    public final ImageView hammerImageView;
    public final ImageView leftView;
    public final ImageView rightView;
    private final TimerService timerService;
    public final IngameService ingameService;
    public final ResourceService resourceService;
    public final GameService gameService;
    public final UserService userService;
    public final RobberController robberController;

    public final CompositeDisposable disposable = new CompositeDisposable();
    public final Stage ingameStage, devCardPlayStage;
    public String selectedCard = null;

    public IngameDevelopmentCardController(Stage ingameStage, Pane hammerPane, Pane leftPane, Pane rightPane, ImageView hammerImageView, ImageView leftView, ImageView rightView, TimerService timerService,
                                           IngameService ingameService, ResourceService resourceService, GameService gameService, UserService userService, RobberController robberController) {
        this.ingameStage = ingameStage;
        this.hammerPane = hammerPane;
        this.leftPane = leftPane;
        this.rightPane = rightPane;
        this.hammerImageView = hammerImageView;
        this.leftView = leftView;
        this.rightView = rightView;
        this.timerService = timerService;
        this.ingameService = ingameService;
        this.resourceService = resourceService;
        this.gameService = gameService;
        this.userService = userService;
        this.robberController = robberController;
        this.devCardPlayStage = new Stage();

        hammerPane.setOnMouseClicked(mouseEvent -> onHammerClicked());
        leftPane.setOnMouseClicked(mouseEvent -> onLeftPaneClicked());
        rightPane.setOnMouseClicked(mouseEvent -> onRightPaneClicked());
    }

    private void onHammerClicked() {
        if(ingameService.getExpectedMove().action().equals(BUILD)) {
            changeVisibility();
            changeHammerBorder();
        }
    }


    private void onLeftPaneClicked() {
        changeVisibility();
        show();
    }


    private void onRightPaneClicked() {
        if(ingameService.getExpectedMove().action().equals(BUILD) && resourceService.checkDevCard()) {
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

    public void resetHammerSelection() {
        hammerPane.setStyle("-fx-border-width: 1; -fx-border-color: black");
        leftPane.setVisible(false);
        rightPane.setVisible(false);
    }

    @Override
    public void init() {
        HashMap<String, Integer> myUnlockedDevCardsMap = getUnlockedDevCardMap(gameService.players.get(userService.getCurrentUser()._id()).developmentCards());

        if(myUnlockedDevCardsMap.get(DEV_PLENTY) > 0) {
            plentyViewMono.setVisible(false);
            plentyView.setVisible(true);
        }
        if(myUnlockedDevCardsMap.get(DEV_KNIGHT) > 0) {
            knightViewMono.setVisible(false);
            knightView.setVisible(true);
        }
        if(myUnlockedDevCardsMap.get(DEV_MONOPOLY) > 0) {
            monopolyViewMono.setVisible(false);
            monopolyView.setVisible(true);
        }
        if(myUnlockedDevCardsMap.get(DEV_ROAD) > 0) {
            roadViewMono.setVisible(false);
            roadView.setVisible(true);
        }
    }

    @Override
    public void stop() {

    }

    @Override
    public Parent render() {
        final FXMLLoader loader = new FXMLLoader(Main.class.getResource("views/DevCardPlayPopUp.fxml"));
        loader.setControllerFactory(c -> this);
        Parent node;
        try {
            node = loader.load();
        } catch (IOException e) {
            node = null;
        }
        return node;
    }

    public void show() {
        Parent view = render();
        Scene devCardPlayScene = new Scene(view);
        init();

        double xPos = ingameStage.getX();
        double yPos = ingameStage.getY();

        devCardPlayStage.setOnCloseRequest(event -> resetHammerSelection());
        devCardPlayStage.setX(xPos + 24);
        devCardPlayStage.setY(yPos + 200);
        devCardPlayStage.setScene(devCardPlayScene);
        devCardPlayStage.show();
        devCardPlayStage.toFront();
    }

    public void onPlentyClicked() {
        resetCardSelection();
        plentyRect.setVisible(true);
        playButton.setDisable(false);
        selectedCard = DEV_PLENTY;
    }

    public void onKnightClicked() {
        resetCardSelection();
        knightRect.setVisible(true);
        playButton.setDisable(false);
        selectedCard = DEV_KNIGHT;
    }

    public void onMonopolyClicked() {
        resetCardSelection();
        monopolyRect.setVisible(true);
        playButton.setDisable(false);
        selectedCard = DEV_MONOPOLY;
    }

    public void onRoadClicked() {
        resetCardSelection();
        roadRect.setVisible(true);
        playButton.setDisable(false);
        selectedCard = DEV_ROAD;
    }

    public void onPlayClicked() {
        if(selectedCard != null) {
            disposable.add(ingameService.postMove(ingameService.game.get()._id(), new CreateMoveDto(selectedCard, true))
                    .observeOn(FX_SCHEDULER)
                    .doOnError(Throwable::printStackTrace)
                    .subscribe(m -> {
                        timerService.interruptBuildTimer();

                        if(selectedCard.equals(DEV_KNIGHT)) {
                            this.robberController.stop();
                            this.robberController.init();
                        }

                        changeHammerBorder();
                        closeDevCardPlayStage();
                    })
            );
        }
    }

    public void resetCardSelection() {
        plentyRect.setVisible(false);
        knightRect.setVisible(false);
        monopolyRect.setVisible(false);
        roadRect.setVisible(false);
        playButton.setDisable(true);
        selectedCard = null;
    }

    public void closeDevCardPlayStage() {
        devCardPlayStage.close();
    }

    public HashMap<String, Integer> getUnlockedDevCardMap(List<DevelopmentCard> devCards) {
        HashMap<String, Integer> devCardMap = new HashMap<>();
        int knight = 0;
        int road = 0;
        int plenty = 0;
        int monopoly = 0;

        for(DevelopmentCard devCard : devCards) {
            switch (devCard.type()) {
                case DEV_KNIGHT -> {if(!devCard.locked() && !devCard.revealed()){knight += 1;}}
                case DEV_ROAD -> {if(!devCard.locked() && !devCard.revealed()){road += 1;}}
                case DEV_PLENTY -> {if(!devCard.locked() && !devCard.revealed()){plenty += 1;}}
                case DEV_MONOPOLY -> {if(!devCard.locked() && !devCard.revealed()){monopoly += 1;}}
            }
        }

        devCardMap.put(DEV_KNIGHT, knight);
        devCardMap.put(DEV_ROAD, road);
        devCardMap.put(DEV_PLENTY, plenty);
        devCardMap.put(DEV_MONOPOLY, monopoly);

        return devCardMap;
    }
}
