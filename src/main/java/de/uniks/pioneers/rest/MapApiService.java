package de.uniks.pioneers.rest;

import de.uniks.pioneers.model.MapTemplate;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.GET;
import retrofit2.http.Path;

import java.util.List;

public interface MapApiService {
    @GET("maps")
    Observable<List<MapTemplate>> getMaps();

    @GET("maps/{id}")
    Observable<MapTemplate> getMap(@Path("id") String id);
}
