package de.uniks.pioneers.controller.subcontroller;


import de.uniks.pioneers.App;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.services.GameService;
import de.uniks.pioneers.services.IngameService;
import de.uniks.pioneers.services.PrefService;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.scene.input.MouseEvent;

import javax.inject.Inject;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

public class RobberController {

    @Inject
    IngameService ingameService;
    @Inject
    GameService gameService;

    @Inject
    PrefService prefService;
    private final HexTile tile;

    private final App app;
    private String action;
    private final CompositeDisposable disposable = new CompositeDisposable();

    public RobberController(App app, HexTile tile, String action){
        this.tile = tile;
        this.action = action;
        this.app = app;
    }

    public void init(){
        discard();
        itsRobbingTime();
        if(prefService.getDarkModeState()){
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DiscardResourcesPopup.css")));
            this.app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DarkMode_DiscardResourcesPopup.css");
        } else {
            this.app.getStage().getScene().getStylesheets().removeIf((style -> style.equals("/de/uniks/pioneers/styles/DarkMode_DiscardResourcesPopup.css")));
            this.app.getStage().getScene().getStylesheets().add( "/de/uniks/pioneers/styles/DiscardResourcesPopup.css");
        }
    }

    private void discard() {
    }

    public HexTile getTile(){
        return this.tile;
    }

    private void itsRobbingTime() {
        CreateMoveDto robMove = new CreateMoveDto(this.action, null, null);
        disposable.add(ingameService.postMove(gameService.game.get()._id(), robMove)
                .observeOn(FX_SCHEDULER)
                .subscribe(move -> {
                    setRobber();
                }));
    }

    private void setRobber() {
        rob();
    }

    private void rob(){
    }






}
