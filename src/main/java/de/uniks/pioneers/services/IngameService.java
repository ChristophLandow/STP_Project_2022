package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateMoveDto;
import de.uniks.pioneers.dto.UpdatePlayerDto;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.rest.PioneersApiService;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.util.Callback;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;

@Singleton
public class IngameService {
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final PioneersApiService pioneersApiService;
    private final GameStorage gameStorage;
    public SimpleObjectProperty<Game> game = new SimpleObjectProperty<>();
    public SimpleObjectProperty<ExpectedMove> currentExpectedMove = new SimpleObjectProperty<>();

    private java.util.Map<String, Integer> trade = new HashMap<>();

    public SimpleBooleanProperty tradeIsOffered = new SimpleBooleanProperty(false);
    public SimpleObjectProperty<Move> tradeOffer = new SimpleObjectProperty<>();
    public ObservableList<Move> tradeAccepted = FXCollections.observableArrayList();

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
                .doOnNext(result -> {
                    gameStorage.setMap(result.tiles());
                    gameStorage.setHarbors(result.harbors());
                });
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

        offer = offer.normalize();
        if (checkTradeOptions(offer)) {
            disposable.add(postMove(game.get()._id(), new CreateMoveDto(BUILD, offer, BANK_ID))
                    .observeOn(FX_SCHEDULER)
                    .doOnError(e -> {
                        Alert alert = new Alert(Alert.AlertType.ERROR, "Something went wrong!");
                        alert.showAndWait();
                    })
                    .subscribe());
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Something went wrong, please check the resource types and amounts!");
            alert.showAndWait();
        }
    }
    public void tradeWithPlayers() {
        Resources offer = new Resources(trade.get("walknochen"), trade.get("packeis"),
                trade.get("kohle"), trade.get("fisch"), trade.get("fell"));

        disposable.add(postMove(game.get()._id(), new CreateMoveDto(BUILD, offer))
                .observeOn(FX_SCHEDULER)
                .doOnError(Throwable::printStackTrace)
                .subscribe()
        );
    }

    public void acceptOffer() {
        Resources offer = tradeOffer.get().resources();

        int lumber = offer.lumber() == null ? 0 : offer.lumber() * -1;
        int brick = offer.brick() == null ? 0 : offer.brick() * -1;
        int wool = offer.wool() == null ? 0 : offer.wool() * -1;
        int grain = offer.grain() == null ? 0 : offer.grain() * -1;
        int ore = offer.ore() == null ? 0 : offer.ore() * -1;

        Resources accept = new Resources(grain, brick, ore, lumber, wool);

        disposable.add(postMove(game.get()._id(), new CreateMoveDto(OFFER, accept, tradeOffer.get().userId()))
                .observeOn(FX_SCHEDULER)
                .doOnError(Throwable::printStackTrace)
                .subscribe(move -> tradeIsOffered.set(false))
        );
    }

    public void initTrade() {
        trade = new HashMap<>();
    }

    public void confirmTrade(String playerId) {
        disposable.add(postMove(game.get()._id(), new CreateMoveDto(ACCEPT, playerId))
                .observeOn(FX_SCHEDULER)
                .doOnError(Throwable::printStackTrace)
                .subscribe(move -> tradeAccepted = FXCollections.emptyObservableList())
        );
    }

    protected boolean checkTradeOptions(Resources resources) {
        ArrayList<Integer> res = new ArrayList<>();
        res.add(resources.brick());
        res.add(resources.grain());
        res.add(resources.lumber());
        res.add(resources.ore());
        res.add(resources.wool());
        List<String> tradeOptions = gameStorage.tradeOptions;
        if (!onlyOneResourceTypeSet(res)) {
            return false;
        }
        int numberOfTradeResources = checkNumberOfTradeResources(resources);
        if (numberOfTradeResources == -1) {
            return false;
        } else if (numberOfTradeResources == 2) {
            int index = res.indexOf(-2);
            return switch (index) {
                case 0 -> tradeOptions.contains("brick");
                case 1 -> tradeOptions.contains("grain");
                case 2 -> tradeOptions.contains("lumber");
                case 3 -> tradeOptions.contains("ore");
                case 4 -> tradeOptions.contains("wool");
                default -> false;
            };
        } else if (numberOfTradeResources == 3) {
            return tradeOptions.contains(null);
        } else return numberOfTradeResources == 4;
    }

    private boolean onlyOneResourceTypeSet(ArrayList<Integer> res) {
        // checks that only one resource type is positive, one negative and the rest null
        int nullCounter = 0;
        int positiveCounter = 0;
        int negativeCounter = 0;
        for (Integer i : res) {
            if (i == 0) {
                nullCounter += 1;
            } else if (i > 0) {
                positiveCounter += 1;
            } else {
                negativeCounter += 1;
            }
        }
        return nullCounter == 3 && positiveCounter == 1 && negativeCounter == 1;
    }

    private int checkNumberOfTradeResources(Resources resources) {
        // returns the number of resources the player wants to trade away
        if (resources.brick() == -4 || resources.grain() == -4 || resources.lumber() == -4 || resources.ore() == -4 || resources.wool() == -4) {
            return 4;
        } else if (resources.brick() == -3 || resources.grain() == -3 || resources.lumber() == -3 || resources.ore() == -3 || resources.wool() == -3) {
            return 3;
        } else if (resources.brick() == -2 || resources.grain() == -2 || resources.lumber() == -2 || resources.ore() == -2 || resources.wool() == -2) {
            return 2;
        } else {
            return -1;
        }
    }

}
