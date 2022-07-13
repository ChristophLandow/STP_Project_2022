package de.uniks.pioneers.services;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.controller.subcontroller.HexTileController;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.dto.RobDto;
import de.uniks.pioneers.model.Move;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.beans.property.SimpleIntegerProperty;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

@Singleton
public class RobberService {
    @Inject PioneersApiService pioneersApiService;
    @Inject GameService gameService;
    @Inject MapRenderService mapRenderService;
    private HexTileController robberTile;
    private ArrayList<User> robbingCandidates = new ArrayList<>();
    SimpleIntegerProperty robberState = new SimpleIntegerProperty(-1);

    @Inject
    public RobberService(PioneersApiService pioneersApiService, GameService gameService) {
        this.pioneersApiService = pioneersApiService;
        this.gameService = gameService;
    }

    public void setup(){
        for(HexTileController hexTileController : mapRenderService.getTileControllers()) {
            hexTileController.setRobberService(this);
        }
    }

    public SimpleIntegerProperty getRobberState() {
        return robberState;
    }

    public void updateRobbingCandidates(){
        this.robbingCandidates = new ArrayList<>(robberTile.getPlayersFromTile());
        this.robbingCandidates.removeIf(user -> user._id().equals(gameService.me));
    }

    public ArrayList<User> getRobbingCandidates() {
        return robbingCandidates;
    }

    public HexTileController getRobberTile() {
        return robberTile;
    }

    public void moveRobber(HexTileController newPosition){
        mapRenderService.getTileControllers().forEach(hexTileController -> hexTileController.setRobber(hexTileController == newPosition));
        this.robberTile = newPosition;
    }

    public Observable<Move> robPlayer(String target){
        CreateMoveDto robMove = new CreateMoveDto(
                GameConstants.ROB,
                new RobDto(robberTile.tile.q,robberTile.tile.s,robberTile.tile.r, target),
                null,
                null,
                null
        );

        return pioneersApiService.postMove(gameService.getGame()._id(), robMove);
    }

    public Observable<Move> dropResources(Resources drop){
        CreateMoveDto dropMove = new CreateMoveDto(
                GameConstants.DROP,
                null,
                drop,
                null,
                null
        );

        return pioneersApiService.postMove(gameService.getGame()._id(), dropMove);
    }
}
