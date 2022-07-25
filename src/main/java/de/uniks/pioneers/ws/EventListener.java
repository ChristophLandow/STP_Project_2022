package de.uniks.pioneers.ws;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.services.TokenStorage;
import io.reactivex.rxjava3.core.Observable;
import javafx.beans.property.SimpleStringProperty;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.function.Consumer;
import java.util.regex.Pattern;

import static de.uniks.pioneers.Constants.*;


@Singleton
public class EventListener {
    private final TokenStorage tokenStorage;
    private final ObjectMapper mapper;
    public ClientEndpoint endpoint;
    final SimpleStringProperty toUri = new SimpleStringProperty();

    @Inject
    public EventListener(TokenStorage tokenStorage, ObjectMapper mapper) {
        this.tokenStorage = tokenStorage;
        this.mapper = mapper;
        toUri.set(BASE_URL_WSS + WS_PREFIX + EVENTS_AUTH_TOKEN + tokenStorage.getAccessToken());
    }

    public EventListener() {
        this.tokenStorage=null;
        this.mapper=null;
        this.toUri.set("http://localhost/path");
    }

    private void ensureOpen() {
        if (endpoint != null) {
            return;
        }
        try {
            endpoint = new ClientEndpoint(
                    new URI(toUri.get()));
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    public <T> Observable<Event<T>> listen(String pattern, Class<T> type) {
        return Observable.create(emitter -> {
            this.ensureOpen();
            send(Map.of("event", "subscribe", "data", pattern));

            final Pattern regex = Pattern.compile(pattern
                    .replace(".", "\\.")
                    .replace("*", "[^.]*"));

        final Consumer<String> handler = eventStr -> {
            try {
                final JsonNode node = mapper.readTree(eventStr);
                final String event = node.get("event").asText();
                if (!regex.matcher(event).matches()) {
                    return;
                }
                final T data = mapper.treeToValue(node.get("data"),type);
                emitter.onNext(new Event<>(event,data));
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
        };
        endpoint.addMessageHandler(handler);
        emitter.setCancellable(()-> removeEventHandler(pattern,handler));
        });
    }

    private void removeEventHandler(String pattern, Consumer<String> handler) {
        if (endpoint==null){
            return;
        }
        send(Map.of("Event","unsubscribe","Data",pattern));
        endpoint.removeMessageHandler(handler);
        if (!endpoint.hasMessageHandlers()){
            //close();
        }
    }

    private void close() {
        if (endpoint !=null){
            try {
                //endpoint.stop();
                endpoint=null;
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
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
