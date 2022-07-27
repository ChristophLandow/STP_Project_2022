package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.model.Vote;
import de.uniks.pioneers.rest.MapApiService;
import de.uniks.pioneers.rest.VoteApiService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import retrofit2.HttpException;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Objects;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@Singleton
public class MapBrowserService {
    private final ObservableList<MapTemplate> maps = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<MapTemplate> updateMaps = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<String> mapNames = FXCollections.observableList(new ArrayList<>());
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
        disposable.add(mapApiService.getMaps().subscribe((mapTemplates) -> {
            maps.setAll(mapTemplates);
//            for(MapTemplate map : maps){
//                mapNames.add(map.name());
//            }
        }));

        disposable.add(eventListener.listen("maps.*.*", MapTemplate.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final MapTemplate mapTemplate = event.data();

                    if(event.event().endsWith(".created")){
                        maps.add(mapTemplate);
                        mapNames.add(mapTemplate.name());
                    }
                    else if(event.event().endsWith(".deleted")){
                        maps.remove(mapTemplate);
                        mapNames.removeIf(mapName -> mapName.equals(mapTemplate.name()));
                    }
                    else if(event.event().endsWith(".updated")){
                        updateMaps.add(mapTemplate);
                    }
                }));
    }

    public ObservableList<MapTemplate> getMaps() {
        return maps;
    }

    public Observable<MapTemplate> getMap(String id) {
        return mapApiService.getMap(id);
    }

    public Observable<MapTemplate> deleteMap(String id) {
        return mapApiService.deleteMap(id);
    }

    public ObservableList<MapTemplate> getUpdateMaps() {
        return updateMaps;
    }

    public ObservableList<String> getMapNames() {
        return mapNames;
    }

    public void stop(){
        disposable.dispose();
    }

    public void vote(String id, CreateVoteDto voteMove){
        disposable.add(voteApiService.createVote(id,voteMove)
                .observeOn(FX_SCHEDULER)
                .doOnError(this::handleHttpError)
                .subscribe()
        );
    }

    public Observable<Vote> getVoteFromMap(String id){
        return voteApiService.getVotesOfMap(id);
    }

    public void deleteVote(String mapId, String userId){
        disposable.add(
                voteApiService.deleteVotesOfUser(mapId,userId)
                        .observeOn(FX_SCHEDULER)
                        .doOnError(this::handleHttpError)
                        .subscribe()
        );
    }

    public Observable<Vote> getVoteFromUSer(String mapId, String userId){
         return voteApiService.deleteVotesOfUser(mapId,userId);
    }

    private  void handleHttpError(Throwable exception) throws IOException {
        String errorBody;
        if (exception instanceof HttpException httpException) {
            errorBody = Objects.requireNonNull(Objects.requireNonNull(httpException.response()).errorBody()).string();
        } else {
            return;
        }
        System.out.println("!!!An Http Error appeared!!!\n" + errorBody);
    }
}
