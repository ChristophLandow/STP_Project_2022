package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.MoveDto;
import de.uniks.pioneers.model.Building;
import de.uniks.pioneers.model.Player;
import de.uniks.pioneers.model.State;
import de.uniks.pioneers.model.Tile;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

import static de.uniks.pioneers.Constants.API_V2_PIONEERS_PREFIX;

public interface PioneersApiService {
    @GET(API_V2_PIONEERS_PREFIX+"/map")
    Observable<List<Tile>> getAllTiles (@Path("gameId") String gameId);

    @GET(API_V2_PIONEERS_PREFIX+"/players")
    Observable<List<Player>> getAllPlayers (@Path("gameId") String gameId);

    @GET(API_V2_PIONEERS_PREFIX+"/players/{userId}")
    Observable<Player> getPlayer (@Path("gameId") String gameId, @Path("userId") String userId);

    @GET(API_V2_PIONEERS_PREFIX+"/state")
    Observable<State> getCurrentState(@Path("gameId") String gameId);

    @GET(API_V2_PIONEERS_PREFIX+"/buildings")
    Observable<List<Building>> getAllBuildings (@Path("gameId") String gameId);

    @GET(API_V2_PIONEERS_PREFIX+"/buildings/{buildingId}")
    Observable<Building> getBuilding (@Path("gameId") String gameId, @Path("buildingId") String buildingId);

    @POST(API_V2_PIONEERS_PREFIX+"/moves")
    Observable<Move> postMove (@Path("gameId") String gameId, @Body MoveDto dto);










}
