package de.uniks.pioneers.services;

import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.GameApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.GameConstants.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GameAndResourceServiceTest {

    @Mock
    GameApiService gameApiService;

    @Spy
    ResourceService resourceService = new ResourceService();

    @InjectMocks
    GameService gameService;

    @Test
    void deleteGame() {
        Game testGame = new Game("yesterday", "now", "123", "TestGame", "Me", 1, false, new GameSettings(2, 10, null, true, 0));
        when(gameApiService.delete("123")).thenReturn(Observable.just(testGame));
        String result = gameService.deleteGame("123").blockingFirst()._id();
        assertEquals("123", result);
        verify(gameApiService).delete("123");
    }

    @Test
    void checkRoadSpot() {
        boolean result = gameService.checkRoadSpot(1,1,1,3);
        assertFalse(result);
        gameService.me = "Me";
        gameService.buildings.add(new Building(1, 1, 1, "123", 3, "road", "1234", "Me"));
        result = gameService.checkRoadSpot(1,1,1,3);
        assertTrue(result);
    }

    @Test
    void isValidFromThree() {
        int[] uploadCoords = new int[3];
        uploadCoords[0] = 1;
        uploadCoords[1] = 2;
        boolean result = gameService.isValidFromThree(uploadCoords);
        assertFalse(result);
        gameService.me = "Me";
        gameService.buildings.add(new Building(1, 1, 1, "123", 11, "road", "1234", "Me"));
        result = gameService.isValidFromThree(uploadCoords);
        assertTrue(result);
    }

    @Test
    void isValidFromSeven() {
        int[] uploadCoords = new int[3];
        uploadCoords[0] = 2;
        uploadCoords[1] = 1;
        boolean result = gameService.isValidFromSeven(uploadCoords);
        assertFalse(result);
        gameService.me = "Me";
        gameService.buildings.add(new Building(1, 1, 1, "123", 3, "road", "1234", "Me"));
        result = gameService.isValidFromSeven(uploadCoords);
        assertTrue(result);
    }

    @Test
    void isValidFromEleven() {
        int[] uploadCoords = new int[3];
        uploadCoords[0] = 1;
        uploadCoords[2] = 2;
        boolean result = gameService.isValidFromEleven(uploadCoords);
        assertFalse(result);
        gameService.me = "Me";
        gameService.buildings.add(new Building(1, 1, 1, "123", 7, "road", "1234", "Me"));
        result = gameService.isValidFromEleven(uploadCoords);
        assertTrue(result);
    }

    @Test
    void checkBuildingSpot() {
        boolean result = gameService.checkBuildingSpot(1,1,1,3);
        assertFalse(result);
        gameService.me = "Me";
        gameService.buildings.add(new Building(1, 1, 1, "123", 3, "igloo", "1234", "Me"));
        result = gameService.checkBuildingSpot(1,1,1,3);
        assertTrue(result);
    }

    @Test
    void checkBuildingsFromThree() {
        int[] uploadCoords = new int[3];
        uploadCoords[0] = 1;
        uploadCoords[1] = 2;
        boolean result = gameService.checkBuildingsFromThree(uploadCoords);
        assertFalse(result);
        gameService.me = "Me";
        gameService.buildings.add(new Building(1, 1, 1, "123", 0, "igloo", "1234", "Me"));
        result = gameService.checkBuildingsFromThree(uploadCoords);
        assertTrue(result);
    }

    @Test
    void checkBuildingsFromSeven() {
        int[] uploadCoords = new int[3];
        uploadCoords[0] = 1;
        uploadCoords[1] = 1;
        uploadCoords[2] = 1;
        boolean result = gameService.checkBuildingsFromSeven(uploadCoords);
        assertFalse(result);
        gameService.me = "Me";
        gameService.buildings.add(new Building(1, 1, 1, "123", 6, "igloo", "1234", "Me"));
        result = gameService.checkBuildingsFromSeven(uploadCoords);
        assertTrue(result);
    }

    @Test
    void checkBuildingsFromEleven() {
        int[] uploadCoords = new int[3];
        uploadCoords[0] = 1;
        uploadCoords[1] = 1;
        uploadCoords[2] = 1;
        boolean result = gameService.checkBuildingsFromEleven(uploadCoords);
        assertFalse(result);
        gameService.me = "Me";
        gameService.buildings.add(new Building(1, 1, 1, "123", 0, "igloo", "1234", "Me"));
        result = gameService.checkBuildingsFromEleven(uploadCoords);
        assertTrue(result);
    }

    @Test
    void checkRoad() {
        Player player = new Player("TestGame", "Me", "ffffff", true, 2, new Resources(1,1,1,1,1), new RemainingBuildings(3, 4, 13), 3, 0, new ArrayList<>());
        gameService.players.put(player.userId(), player);
        gameService.me = player.userId();
        resourceService.myResources.put("grain", 1);
        resourceService.myResources.put("brick", 1);
        resourceService.myResources.put("ore", 1);
        resourceService.myResources.put("lumber", 1);
        resourceService.myResources.put("wool", 1);
        boolean result = resourceService.checkRoad();
        assertTrue(result);
        assertFalse(resourceService.notEnoughResources.get());
        player = new Player("TestGame", "Me", "ffffff", true, 2, new Resources(1,1,1,0,1), new RemainingBuildings(3, 4, 13), 3, 0, new ArrayList<>());
        gameService.players.replace(player.userId(), player);
        resourceService.myResources.put("grain", 1);
        resourceService.myResources.put("brick", 1);
        resourceService.myResources.put("ore", 1);
        resourceService.myResources.put("lumber", 0);
        resourceService.myResources.put("wool", 1);
        result = resourceService.checkRoad();
        assertFalse(result);
        assertTrue(resourceService.notEnoughResources.get());
    }

    @Test
    void checkResourcesSettlement() {
        Player player = new Player("TestGame", "Me", "ffffff", true, 2, new Resources(1,1,1,1,1), new RemainingBuildings(3, 4, 13), 3, 0, new ArrayList<>());
        gameService.players.put(player.userId(), player);
        gameService.me = player.userId();
        resourceService.myResources.put("grain", 1);
        resourceService.myResources.put("brick", 1);
        resourceService.myResources.put("ore", 1);
        resourceService.myResources.put("lumber", 1);
        resourceService.myResources.put("wool", 1);
        boolean result = resourceService.checkResourcesSettlement();
        assertTrue(result);
        assertFalse(resourceService.notEnoughResources.get());
        player = new Player("TestGame", "Me", "ffffff", true, 2, new Resources(1,1,1,0,1), new RemainingBuildings(3, 4, 13), 3, 0, new ArrayList<>());
        gameService.players.replace(player.userId(), player);
        resourceService.myResources.put("grain", 1);
        resourceService.myResources.put("brick", 1);
        resourceService.myResources.put("ore", 1);
        resourceService.myResources.put("lumber", 0);
        resourceService.myResources.put("wool", 1);
        result = resourceService.checkResourcesSettlement();
        assertFalse(result);
        assertTrue(resourceService.notEnoughResources.get());
    }

    @Test
    void checkCity() {
        Player player = new Player("TestGame", "Me", "ffffff", true, 2, new Resources(2,1,3,1,1), new RemainingBuildings(3, 4, 13), 3, 0, new ArrayList<>());
        gameService.players.put(player.userId(), player);
        gameService.me = player.userId();
        resourceService.myResources.put("grain", 2);
        resourceService.myResources.put("brick", 1);
        resourceService.myResources.put("ore", 3);
        resourceService.myResources.put("lumber", 1);
        resourceService.myResources.put("wool", 1);
        boolean result = resourceService.checkCity();
        assertTrue(result);
        assertFalse(resourceService.notEnoughResources.get());
        player = new Player("TestGame", "Me", "ffffff", true, 2, new Resources(1,1,1,0,1), new RemainingBuildings(3, 4, 13), 3, 0, new ArrayList<>());
        gameService.players.replace(player.userId(), player);
        resourceService.myResources.put("grain", 1);
        resourceService.myResources.put("brick", 1);
        resourceService.myResources.put("ore", 1);
        resourceService.myResources.put("lumber", 0);
        resourceService.myResources.put("wool", 1);
        result = resourceService.checkCity();
        assertFalse(result);
        assertTrue(resourceService.notEnoughResources.get());
    }

    @Test
    void checkDevCard() {
        Player player = new Player("TestGame", "Me", "ffffff", true, 2, new Resources(1,1,1,1,1), new RemainingBuildings(3, 4, 13), 3, 0, new ArrayList<>());
        gameService.players.put(player.userId(), player);
        gameService.me = player.userId();
        resourceService.myResources.put("grain", 1);
        resourceService.myResources.put("brick", 1);
        resourceService.myResources.put("ore", 1);
        resourceService.myResources.put("lumber", 1);
        resourceService.myResources.put("wool", 1);
        boolean result = resourceService.checkDevCard();
        assertTrue(result);
        assertFalse(resourceService.notEnoughResources.get());
        player = new Player("TestGame", "Me", "ffffff", true, 2, new Resources(1,1,1,0,1), new RemainingBuildings(3, 4, 13), 3, 0, new ArrayList<>());
        gameService.players.replace(player.userId(), player);
        resourceService.myResources.put("grain", 0);
        resourceService.myResources.put("brick", 1);
        resourceService.myResources.put("ore", 0);
        resourceService.myResources.put("lumber", 0);
        resourceService.myResources.put("wool", 1);
        result = resourceService.checkCity();
        assertFalse(result);
        assertTrue(resourceService.notEnoughResources.get());
    }

    @Test
    void getResourcesSize() {
        Player player = new Player("TestGame", "Me", "ffffff", true, 2, new Resources(2,1,3,1,1), new RemainingBuildings(3, 4, 13), 3, 0, new ArrayList<>());
        gameService.players.put(player.userId(), player);
        gameService.me = player.userId();
        
        Resources ingameResources = gameService.players.get(gameService.me).resources();
        int result = 0;

        if (ingameResources.grain() != null) {
            result += ingameResources.grain();
        }
        if (ingameResources.brick() != null) {
            result += ingameResources.brick();
        }
        if (ingameResources.ore() != null) {
            result += ingameResources.ore();
        }
        if (ingameResources.lumber() != null) {
            result += ingameResources.lumber();
        }
        if (ingameResources.wool() != null) {
            result += ingameResources.wool();
        }

        assertEquals(8, result);
    }

    @Test
    void getDevCardMap() {
        HashMap<String, Integer> devCardMap = resourceService.getDevCardMap(List.of(new DevelopmentCard(DEV_KNIGHT, false, false), new DevelopmentCard(DEV_KNIGHT, false, false), new DevelopmentCard(DEV_KNIGHT, false, false), new DevelopmentCard(DEV_PLENTY, false, false)));
        assertEquals(devCardMap.size(), 5);
        assertEquals(devCardMap.get(DEV_KNIGHT), 3);
        assertEquals(devCardMap.get(DEV_PLENTY), 1);
    }

    @Test
    void getUsers() {
        ArrayList<User> userList = gameService.getUsers();
        assertEquals(0, userList.size());
    }

    @Test
    void getGame() {
        Game game = gameService.getGame();
        assertNull(game);
    }
}