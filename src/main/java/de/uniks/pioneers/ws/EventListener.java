package de.uniks.pioneers.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.services.TokenStorage;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import javax.websocket.ClientEndpoint;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.regex.Pattern;

import static de.uniks.pioneers.Constants.*;

@Singleton
public class EventListener {

    private final TokenStorage tokenStorage;
    private final ObjectMapper mapper;
    private WebsocketClient endpoint;

    @Inject
    public EventListener(TokenStorage tokenStorage, ObjectMapper mapper) {
        this.tokenStorage = tokenStorage;
        this.mapper = mapper;
    }

    private void ensureOpen(){
        if (endpoint != null){
            return;
        }
        try {
            endpoint= new WebsocketClient(
                    new URI(BASE_URL + WS_V1_PREFIX + EVENTS_AUTH_TOKEN + tokenStorage.getToken()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public <T>Observable<Event<T>> listen (String pattern, Class<T> type){
        return Observable.create(emitter -> {
            this.ensureOpen();
            send(Map.of("event","subsripe", "data", pattern));

            final Pattern rexex = Pattern.compile(pattern.replace())
        });
    }

    private void send(Object message) {
            final String msg;
        try {
            msg = mapper.writeValueAsString(message);
            endpoint.sendMessage(msg);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
        }

    }


}
