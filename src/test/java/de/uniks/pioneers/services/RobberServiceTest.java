package de.uniks.pioneers.services;

import de.uniks.pioneers.GameConstants;
import de.uniks.pioneers.controller.subcontroller.HexTile;
import de.uniks.pioneers.controller.subcontroller.HexTileController;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.dto.RobDto;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.scene.shape.Circle;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
        ObservableMap<String, Player> players = FXCollections.observableHashMap();
        players.put("me", new Player("","me","",true,
                0,new Resources(10,0,0,0,0,0),null,0,0, new ArrayList<>()));

        players.put("id1", new Player("","id1","",true,
                0,new Resources(3,0,0,0,0,0),null,0,0, new ArrayList<>()));

        players.put("id2", new Player("","id2","",true,
                0,new Resources(0,0,0,0,0,0),null,0,0, new ArrayList<>()));

        gameService.me = "me";
        gameService.players = players;
        this.robberService.mapRenderService = mapRenderService;
        when(mapRenderService.getTileControllers()).thenReturn(new ArrayList<>());

        robberService.moveRobber(hexTileController);

        ArrayList<User> users = new ArrayList<>();
        users.add(new User("me","me","",""));
        users.add(new User("id1","user1","",""));
        users.add(new User("id2","user2","",""));

        when(hexTileController.getPlayersFromTile()).thenReturn(users);

        robberService.updateRobbingCandidates();

        assertEquals(robberService.getRobbingCandidates().size(), 1);
        assertFalse(robberService.getRobbingCandidates().contains(users.get(0)));
        assertFalse(robberService.getRobbingCandidates().contains(users.get(2)));
        assertEquals(robberService.getRobbingCandidates().get(0), users.get(1));
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

        RobDto robInfo = new RobDto(0,0,0,"target");

        CreateMoveDto robMove = new CreateMoveDto(
                GameConstants.ROB,
                robInfo,
                null,
                null,
                null,
                null
        );

        when(gameService.getGame()).thenReturn(
                new Game("","","1","testgame","u",1, true,null));

        when(pioneersApiService.postMove("1", robMove)).thenReturn(
                Observable.just(new Move("", "","1", "u","rob",0, null, robInfo, null, "", null)));

        final Move robResult = robberService.robPlayer("target").blockingFirst();

        assertEquals(robResult.gameId(), "1");
        assertEquals(robResult.userId(), "u");
        assertEquals(robResult.action(), "rob");
        assertEquals(robResult.roll(), 0);
        assertEquals(robResult.rob(), robInfo);

        verify(pioneersApiService).postMove("1", robMove);
    }

    @Test
    void dropResources() {
        Resources resources = new Resources(4,3,2,1,0);

        CreateMoveDto dropMove = new CreateMoveDto(
                GameConstants.DROP,
                null,
                resources,
                null,
                null,
                null
        );

        when(gameService.getGame()).thenReturn(
                new Game("","","1","testgame","u",1, true,null));

        when(pioneersApiService.postMove("1", dropMove)).thenReturn(
                Observable.just(new Move("", "","1", "u","drop",0, null, null, resources, "", null)));

        final Move dropResult = robberService.dropResources(resources).blockingFirst();

        assertEquals(dropResult.gameId(), "1");
        assertEquals(dropResult.userId(), "u");
        assertEquals(dropResult.action(), "drop");
        assertEquals(dropResult.roll(), 0);
        assertEquals(dropResult.resources(), resources);

        verify(pioneersApiService).postMove("1", dropMove);
    }
}