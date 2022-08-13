package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.rest.MapApiService;
import de.uniks.pioneers.rest.VoteApiService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;
import java.util.HashMap;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@Singleton
public class MapBrowserService {
    private final ObservableList<MapTemplate> maps = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<MapTemplate> updateMaps = FXCollections.observableList(new ArrayList<>());
    private final HashMap<String, MapTemplate> templateHashMap = new HashMap<>();
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final EventListener eventListener;
    private final MapApiService mapApiService;
    private final VoteApiService voteApiService;

    @Inject MapBrowserService(EventListener eventListener, MapApiService mapApiService, VoteApiService voteApiService){
        this.eventListener = eventListener;
        this.mapApiService = mapApiService;
        this.voteApiService = voteApiService;

        initMapListener();
    }

    private void initMapListener(){
        disposable.add(mapApiService.getMaps().observeOn(FX_SCHEDULER).subscribe(mapTemplates -> {
            maps.setAll(mapTemplates);
            for (MapTemplate map : mapTemplates) {
                templateHashMap.put(map._id(), map);
            }
        }));

        disposable.add(eventListener.listen("maps.*.*", MapTemplate.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final MapTemplate mapTemplate = event.data();

                    if(event.event().endsWith(".created")){
                        maps.add(mapTemplate);
                        templateHashMap.put(mapTemplate._id(), mapTemplate);
                    }
                    else if(event.event().endsWith(".deleted")){
                        maps.remove(mapTemplate);
                        templateHashMap.remove(mapTemplate._id());
                    }
                    else if(event.event().endsWith(".updated")){
                        updateMaps.add(mapTemplate);
                        templateHashMap.replace(mapTemplate._id(), mapTemplate);
                    }
                }));
    }

    public void addOwnMap(MapTemplate mapTemplate){
        maps.add(mapTemplate);
        templateHashMap.put(mapTemplate._id(), mapTemplate);
    }

    public void updateOwnMap(MapTemplate mapTemplate){
        updateMaps.add(mapTemplate);
        templateHashMap.replace(mapTemplate._id(), mapTemplate);
    }

    public ObservableList<MapTemplate> getMaps() {
        return maps;
    }

    public MapTemplate getMap(String id) {
        return templateHashMap.get(id);
    }

    public Observable<MapTemplate> deleteMap(String id) {
        return mapApiService.deleteMap(id);
    }

    public ObservableList<MapTemplate> getUpdateMaps() {
        return updateMaps;
    }

    public void stop(){
        disposable.dispose();
    }

    public void vote(String id, CreateVoteDto voteMove){
        disposable.add(voteApiService.createVote(id,voteMove)
                .observeOn(FX_SCHEDULER)
                .subscribe()
        );
    }

    public void deleteVote(String mapId, String userId){
        disposable.add(
                voteApiService.deleteVotesOfUser(mapId,userId)
                        .observeOn(FX_SCHEDULER)
                        .subscribe()
        );
    }
}
