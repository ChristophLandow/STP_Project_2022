package de.uniks.pioneers.services;

import de.uniks.pioneers.model.MapTemplate;
import de.uniks.pioneers.rest.MapApiService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.ArrayList;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@Singleton
public class MapBrowserService {
    private final ObservableList<MapTemplate> maps = FXCollections.observableList(new ArrayList<>());
    private final ObservableList<String> mapNames = FXCollections.observableList(new ArrayList<>());
    private final CompositeDisposable disposable = new CompositeDisposable();
    private final EventListener eventListener;
    private final MapApiService mapApiService;

    @Inject MapBrowserService(EventListener eventListener, MapApiService mapApiService){
        this.eventListener = eventListener;
        this.mapApiService = mapApiService;

        initMapListener();
    }

    private void initMapListener(){
        disposable.add(mapApiService.getMaps().observeOn(FX_SCHEDULER).subscribe((mapTemplates) -> {
            maps.setAll(mapTemplates);
            for(MapTemplate map : maps){
                mapNames.add(map.name());
            }
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
                        maps.remove(mapTemplate);
                        mapNames.removeIf(mapName -> mapName.equals(mapTemplate.name()));

                        maps.add(mapTemplate);
                        mapNames.add(mapTemplate.name());
                    }
                }));
    }

    public ObservableList<MapTemplate> getMaps() {
        return maps;
    }

    public Observable<MapTemplate> getMap(String id) {
        return mapApiService.getMap(id);
    }

    public ObservableList<String> getMapNames() {
        return mapNames;
    }

    public void stop(){
        disposable.dispose();
    }
}
