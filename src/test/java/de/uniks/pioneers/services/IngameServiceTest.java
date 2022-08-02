package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.dto.RobDto;
import de.uniks.pioneers.dto.UpdatePlayerDto;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class IngameServiceTest {

    @Mock
    PioneersApiService pioneersApiService;

    @Mock
    GameStorage gameStorage;

    @InjectMocks
    IngameService ingameService;

    @Test
    void getAllPlayers() {
        List<Player> playerList= new ArrayList<>();
        Player player = new Player("TestGame", "Me", "ffffff", true, 2, new Resources(1,1,1,1,1), new RemainingBuildings(3, 4, 13), 3, 0, new ArrayList<>());
        playerList.add(player);
        when(pioneersApiService.getAllPlayers(anyString())).thenReturn(Observable.just(playerList));
        Player result = ingameService.getAllPlayers("123").blockingFirst().get(0);
        assertEquals(player, result);
        verify(pioneersApiService).getAllPlayers("123");
    }

    @Test
    void getPlayer() {
        Player player = new Player("TestGame", "Me", "ffffff", true, 2, new Resources(1,1,1,1,1), new RemainingBuildings(3, 4, 13), 3, 0, new ArrayList<>());
        when(pioneersApiService.getPlayer(anyString(), anyString())).thenReturn(Observable.just(player));
        Player result = ingameService.getPlayer("123", "1234").blockingFirst();
        assertEquals(player, result);
        verify(pioneersApiService).getPlayer("123", "1234");
    }

    @Test
    void getAllBuildings() {
        List<Building> buildingList = new ArrayList<>();
        buildingList.add(new Building(1, 1, 1, "123", 6, "igloo", "1234", "Me"));
        when(pioneersApiService.getAllBuildings(anyString())).thenReturn(Observable.just(buildingList));
        Building result = ingameService.getAllBuildings("123").blockingFirst().get(0);
        assertEquals("igloo", result.type());
        verify(pioneersApiService).getAllBuildings("123");
    }

    @Test
    void getMap() {
        List<Tile> tileList = new ArrayList<>();
        List<Harbor> harborList = new ArrayList<>();
        Tile tile = new Tile(1,1,1, "grain", 1);
        Harbor harbor = new Harbor(2,1,1, "ore", 3);
        tileList.add(tile);
        harborList.add(harbor);
        Map map = new Map("123", tileList, harborList);
        when(pioneersApiService.getMap(anyString())).thenReturn(Observable.just(map));
        Map result = ingameService.getMap("123").blockingFirst();
        assertEquals(tileList, result.tiles());
        verify(pioneersApiService).getMap("123");
    }

    @Test
    void getCurrentState() {
        List<ExpectedMove> moveList = new ArrayList<>();
        List<String> playerList = new ArrayList<>();
        playerList.add("1234");
        ExpectedMove move = new ExpectedMove("test", playerList);
        moveList.add(move);
        when(pioneersApiService.getCurrentState(anyString())).thenReturn(Observable.just(new State("now", "123", moveList, new Point3D(1,1,1))));
        State result = ingameService.getCurrentState("123").blockingFirst();
        assertEquals(moveList, result.expectedMoves());
        verify(pioneersApiService).getCurrentState("123");
    }

    @Test
    void postMove() {
        Resources resources = new Resources(3,3,3,2,1);
        when(pioneersApiService.postMove(anyString(), any())).thenReturn(Observable.just(new Move("now", "12345", "123", "1234", BUILD, 3, "igloo", new RobDto(2,1,2,"test"), resources, "test", null)));
        CreateMoveDto move = new CreateMoveDto("test", resources, "test");
        Move result = ingameService.postMove("123", move).blockingFirst();
        assertEquals(resources, result.resources());
        verify(pioneersApiService).postMove("123", move);
    }

    @Test
    void updatePlayer() {
        Player player = new Player("TestGame", "Me", "ffffff", true, 2, new Resources(1,1,1,1,1), new RemainingBuildings(3, 4, 13), 3, 0, new ArrayList<>());
        when(pioneersApiService.updatePlayer(anyString(), anyString(), any())).thenReturn(Observable.just(player));
        Player result = ingameService.updatePlayer("123", "1234", true).blockingFirst();
        assertEquals(player.remainingBuildings(), result.remainingBuildings());
        verify(pioneersApiService).updatePlayer("123", "1234", new UpdatePlayerDto(true));
    }

    @Test
    void getOrCreateTrade() {
        ingameService.getOrCreateTrade("1", 1);
        ingameService.getOrCreateTrade("2", 1);
        ingameService.getOrCreateTrade("1", 2);
    }

    @Test
    void checkTradeOptions() {
        gameStorage.tradeOptions = new ArrayList<>();
        // completely wrong resource combination
        Resources resources = new Resources(2,3,1,2,1);
        boolean res = ingameService.checkTradeOptions(resources);
        assertFalse(res);
        // without trade option
        resources = new Resources(-2,0,0,0,1);
        res = ingameService.checkTradeOptions(resources);
        assertFalse(res);
        // with trade option grain
        gameStorage.tradeOptions.add("grain");
        res = ingameService.checkTradeOptions(resources);
        assertTrue(res);
        // with trade option ore
        gameStorage.tradeOptions.remove("grain");
        gameStorage.tradeOptions.add("ore");
        resources = new Resources(0,0,-2,0,1);
        res = ingameService.checkTradeOptions(resources);
        assertTrue(res);
        // with trade option lumber
        gameStorage.tradeOptions.remove("ore");
        gameStorage.tradeOptions.add("lumber");
        resources = new Resources(0,0,0,-2,1);
        res = ingameService.checkTradeOptions(resources);
        assertTrue(res);
        // with trade option wool
        gameStorage.tradeOptions.remove("lumber");
        gameStorage.tradeOptions.add("wool");
        resources = new Resources(0,0,1,0,-2);
        res = ingameService.checkTradeOptions(resources);
        assertTrue(res);
        // with trade option brick
        gameStorage.tradeOptions.remove("wool");
        gameStorage.tradeOptions.add("brick");
        resources = new Resources(0,-2,0,0,1);
        res = ingameService.checkTradeOptions(resources);
        assertTrue(res);
        // with trade option general
        gameStorage.tradeOptions.remove("brick");
        gameStorage.tradeOptions.add(null);
        resources = new Resources(0,0,-3,0,1);
        res = ingameService.checkTradeOptions(resources);
        assertTrue(res);
        // without any trade option but correct number of resources
        gameStorage.tradeOptions.remove(null);
        resources = new Resources(0,0,-4,0,1);
        res = ingameService.checkTradeOptions(resources);
        assertTrue(res);
        // with too many resources
        resources = new Resources(0,0,-5,0,1);
        res = ingameService.checkTradeOptions(resources);
        assertFalse(res);
    }

}