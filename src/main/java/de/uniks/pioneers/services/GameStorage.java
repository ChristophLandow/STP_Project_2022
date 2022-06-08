package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.subcontroller.IngamePlayerListElementController;
import de.uniks.pioneers.model.*;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.ObservableMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@Singleton
public class GameStorage {

    private List<Tile> map;

    @Inject
    EventListener eventListener;

    @Inject
    public GameStorage() {
    }

//    public void setCurrentPlayers(List<String> playerIdS) {
//        currentPlayers = players.values().stream().filter(player -> playerIdS.contains(player.userId())).toList();
//    }

    public List<Tile> getMap() {
        return map;
    }

    public void setMap(List<Tile> map) {
        this.map = map;
    }

}
