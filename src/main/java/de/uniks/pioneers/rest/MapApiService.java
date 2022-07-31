package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateMapTemplateDto;
import de.uniks.pioneers.dto.UpdateMapTemplateDto;
import de.uniks.pioneers.model.MapTemplate;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface MapApiService {
    @GET("maps")
    Observable<List<MapTemplate>> getMaps();

    @GET("maps/{id}")
    Observable<MapTemplate> getMap(@Path("id") String id);

    @POST("maps")
    Observable<MapTemplate> createMap(@Body CreateMapTemplateDto createMapTemplateDto);

    @PATCH("maps/{id}")
    Observable<MapTemplate> updateMap(@Path("id") String id, @Body UpdateMapTemplateDto updateMapTemplateDto);


    @DELETE("maps/{id}")
    Observable<MapTemplate> deleteMap(@Path("id") String id);
}
