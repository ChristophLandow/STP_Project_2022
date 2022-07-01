package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateBuildingDto;
import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.dto.UpdatePlayerDto;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;


import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.BUILD;
import static de.uniks.pioneers.GameConstants.OFFER;

@Singleton
public class IngameService {
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final PioneersApiService pioneersApiService;
    private final GameStorage gameStorage;
    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();

    private java.util.Map<String, Integer> trade = new HashMap<>();

    public SimpleObjectProperty<Move> tradeOffer = new SimpleObjectProperty<>();
    public SimpleBooleanProperty tradeIsOffered = new SimpleBooleanProperty(false);

    private java.util.Map<String, Integer> offer = new HashMap<>();
    private java.util.Map<String, Integer> accept = new HashMap<>();

    @Inject
    public IngameService(PioneersApiService pioneersApiService, GameStorage gameStorage) {
        this.pioneersApiService = pioneersApiService;
        this.gameStorage = gameStorage;
    }

    public Observable<List<Player>> getAllPlayers(String gameId) {
        return pioneersApiService.getAllPlayers(gameId);
    }

    public Observable<Player> getPlayer(String gameId, String playerId) {
        return pioneersApiService.getPlayer(gameId, playerId);
    }

    public Observable<List<Building>> getAllBuildings(String gameId) {
        return pioneersApiService.getAllBuildings(gameId);
    }

    public Observable<Map> getMap(String gameId) {
        return pioneersApiService.getMap(gameId)
                .doOnNext(result -> gameStorage.setMap(result.tiles()));
    }

    public Observable<State> getCurrentState(String gameId) {
        return pioneersApiService.getCurrentState(gameId);
    }

    public Observable<Move> postMove(String gameId, CreateMoveDto dto) {
        return pioneersApiService.postMove(gameId, dto);
    }

    public Observable<Player> updatePlayer(String gameId, String userId, boolean active) {
        return pioneersApiService.updatePlayer(gameId, userId, new UpdatePlayerDto(active));
    }

    public void getOrCreateTrade(String value, int i) {
        if (trade.containsKey(value)) {
            int oldValue = trade.get(value);
            trade.replace(value, oldValue + i);
        } else {
            trade.put(value, i);
        }
    }

    public void tradeWithBank() {
        Resources offer = new Resources(trade.get("walknochen"), trade.get("packeis"),
                trade.get("kohle"), trade.get("fisch"), trade.get("fell"));

        System.out.println(offer);

        String bank = "684072366f72202b72406465";

        disposable.add(postMove(game.get()._id(), new CreateMoveDto(BUILD, offer, bank))
                .observeOn(FX_SCHEDULER)
                .doOnError(Throwable::printStackTrace)
                .subscribe(move -> {
                    trade = new HashMap<>();
                })
        );
    }

    public void tradeWithPlayers() {
        Resources offer = new Resources(trade.get("walknochen"), trade.get("packeis"),
                trade.get("kohle"), trade.get("fisch"), trade.get("fell"));

        System.out.println(offer);

        disposable.add(postMove(game.get()._id(), new CreateMoveDto(BUILD, offer))
                .observeOn(FX_SCHEDULER)
                .doOnError(Throwable::printStackTrace)
                .subscribe(move -> {
                    trade = new HashMap<>();
                })
        );

    }


    public void acceptOffer() {
        Resources offer = tradeOffer.get().resources();

        /*
            lumber = fisch
            brick = packeis
            wool = fell
            grain = walknochen
            ore = kohle
         */

        int lumber = offer.lumber() == null ? 0 : offer.lumber() * -1;
        int brick = offer.brick() == null ? 0 : offer.brick() * -1;
        int wool = offer.wool() == null ? 0 : offer.wool() * -1;
        int grain = offer.grain() == null ? 0 : offer.grain() * -1;
        int ore = offer.ore() == null ? 0 : offer.ore() * -1;

        Resources accept = new Resources(grain, brick, ore, lumber, wool);

        disposable.add(postMove(game.get()._id(), new CreateMoveDto(OFFER, accept, tradeOffer.get().userId()))
                .observeOn(FX_SCHEDULER)
                .doOnError(Throwable::printStackTrace)
                .subscribe()
        );
    }
}
