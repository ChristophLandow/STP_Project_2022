package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.MessageDto;
import io.reactivex.rxjava3.core.Observable;
import javafx.beans.property.SimpleStringProperty;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface MessageApiService {

    @POST("{namespace}/{parent}/messages")
    Observable<MessageDto> sendMessage(@Path("namespace") String namespace, @Path("parent") String parent, @Body CreateMessageDto dto);

}