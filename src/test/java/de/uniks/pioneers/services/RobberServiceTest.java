package de.uniks.pioneers.services;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.controller.subcontroller.HexTileController;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.dto.RobDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Move;
import de.uniks.pioneers.model.Resources;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.scene.shape.Circle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RobberServiceTest {
    @Mock
    GameService gameService;
    @Mock
    MapRenderService mapRenderService;
    @InjectMocks
    RobberService robberService;

    @Mock
    HexTileController hexTileController;

    @Mock
    PioneersApiService pioneersApiService;

    @Test
    void updateRobbingCandidates() {
        gameService.me = "me";
        this.robberService.mapRenderService = mapRenderService;

        when(mapRenderService.getTileControllers()).thenReturn(new ArrayList<>());

        robberService.moveRobber(hexTileController);

        ArrayList<User> users = new ArrayList<>();
        users.add(new User("me","me","",""));
        users.add(new User("id1","user1","",""));
        users.add(new User("id2","user2","",""));

        when(hexTileController.getPlayersFromTile()).thenReturn(users);

        robberService.updateRobbingCandidates();

        assertEquals(robberService.getRobbingCandidates().size(), 2);
        assertFalse(robberService.getRobbingCandidates().contains(users.get(0)));
        assertEquals(robberService.getRobbingCandidates().get(0), users.get(1));
        assertEquals(robberService.getRobbingCandidates().get(1), users.get(2));
    }

    @Test
    void moveRobber() {
        this.robberService.mapRenderService = mapRenderService;

        HexTileController newRobberTile = new HexTileController(null,
                new HexTile(0,0,0,0,false),
                null , new Circle());

        when(mapRenderService.getTileControllers()).thenReturn(new ArrayList<>());

        robberService.moveRobber(newRobberTile);

        assertEquals(robberService.getRobberTile(), newRobberTile);
    }

    @Test
    void robPlayer() {
        this.robberService.mapRenderService = mapRenderService;
        robberService.moveRobber(new HexTileController(null, new HexTile(0,0,0,0,false), null , new Circle()));

        RobDto robMove = new RobDto(0,0,0,"target");

        CreateMoveDto ropMove = new CreateMoveDto(
                GameConstants.ROB,
                robMove,
                null,
                null,
                null
        );

        when(gameService.getGame()).thenReturn(
                new Game("","","1","testgame","u",1, true,null));

        when(pioneersApiService.postMove("1", ropMove)).thenReturn(
                Observable.just(new Move("", "","1", "u","rob",0, null, robMove, null, "")));

        final Move robResult = robberService.robPlayer("target").blockingFirst();

        assertEquals(robResult.gameId(), "1");
        assertEquals(robResult.userId(), "u");
        assertEquals(robResult.action(), "rob");
        assertEquals(robResult.roll(), 0);
        assertEquals(robResult.rob(), robMove);

        verify(pioneersApiService).postMove("1", ropMove);
    }

    @Test
    void dropResources() {
        Resources resources = new Resources(4,3,2,1,0);

        CreateMoveDto dropMove = new CreateMoveDto(
                GameConstants.DROP,
                null,
                resources,
                null,
                null
        );

        when(gameService.getGame()).thenReturn(
                new Game("","","1","testgame","u",1, true,null));

        when(pioneersApiService.postMove("1", dropMove)).thenReturn(
                Observable.just(new Move("", "","1", "u","drop",0, null, null, resources, "")));

        final Move dropResult = robberService.dropResources(resources).blockingFirst();

        assertEquals(dropResult.gameId(), "1");
        assertEquals(dropResult.userId(), "u");
        assertEquals(dropResult.action(), "drop");
        assertEquals(dropResult.roll(), 0);
        assertEquals(dropResult.resources(), resources);

        verify(pioneersApiService).postMove("1", dropMove);
    }
}