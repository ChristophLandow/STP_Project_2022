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
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final EventListener eventListener;
    private final MapApiService mapApiService;
    private final VoteApiService voteApiService;

    @Inject MapBrowserService(EventListener eventListener, VoteApiService voteApiService, MapApiService mapApiService){
        this.eventListener = eventListener;
        this.mapApiService = mapApiService;
        this.voteApiService = voteApiService;
        initMapListener();
    }

    private void initMapListener(){
        disposable.add(mapApiService.getMaps().observeOn(FX_SCHEDULER).subscribe(maps::setAll));

        disposable.add(eventListener.listen("maps.*.*", MapTemplate.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final MapTemplate mapTemplate = event.data();

                    if(event.event().endsWith(".created")){
                        maps.add(mapTemplate);
                    }
                    else if(event.event().endsWith(".deleted")){
                        maps.remove(mapTemplate);
                    }
                    else if(event.event().endsWith(".updated")){
                        maps.forEach(m -> m = (m._id().equals(mapTemplate._id()) ? mapTemplate : m));
                    }
                }));
    }

    public ObservableList<MapTemplate> getMaps() {
        return maps;
    }

    public void stop(){
        disposable.dispose();
    }
    
    public void vote(String id, CreateVoteDto voteMove){
        voteApiService.createVote(id,voteMove).subscribe(System.out::println, this::handleHttpError);
    }

    public Observable<Vote> getVoteFromMap(String id){
        return voteApiService.getVotesOfMap(id);
    }

    public void deleteVote(String mapId, String userId){
        voteApiService.deleteVotesOfUser(mapId,userId).subscribe(System.out::println, this::handleHttpError);
    }

    public Observable<Vote> getVoteFromUSer(String mapId, String userId){
         return voteApiService.deleteVotesOfUser(mapId,userId);
    }

    private  void handleHttpError(Throwable exception) throws IOException {
        if (exception instanceof HttpException httpException) {
            return;
        }
    }

}
