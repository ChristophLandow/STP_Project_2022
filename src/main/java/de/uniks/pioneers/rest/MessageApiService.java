package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.dto.UpdateMessageDto;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface MessageApiService {

    @POST("{namespace}/{parent}/messages")
    Observable<MessageDto> sendMessage(@Path("namespace") String namespace, @Path("parent") String parent, @Body CreateMessageDto dto);

    @GET("{namespace}/{parent}/messages")
    Observable<List<MessageDto>> getChatMessages(@Path("namespace") String namespace, @Path("parent") String parent);

    @PATCH("{namespace}/{parent}/messages/{id}")
    Observable<MessageDto> updateMessage(@Path("namespace") String namespace, @Path("parent") String parent, @Path("id") String id, @Body UpdateMessageDto dto);
}