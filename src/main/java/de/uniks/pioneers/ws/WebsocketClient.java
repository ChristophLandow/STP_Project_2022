package de.uniks.pioneers.ws;

import javax.websocket.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;
import java.util.function.Consumer;

import static de.uniks.pioneers.Constants.NOOP;

@javax.websocket.ClientEndpoint
public class WebsocketClient {
    private final Timer noopTimer;
    private Session session;
    private final List<Consumer<String>> messageHandlers = Collections.synchronizedList(new ArrayList<>());

    public WebsocketClient(URI endpointURI) {
        //Create a new timer and save the callback
        noopTimer = new Timer();
        //Create and connect the websocket client
        try {
            WebSocketContainer webSocketContainer = ContainerProvider.getWebSocketContainer();
            webSocketContainer.connectToServer(this, endpointURI);
        } catch (DeploymentException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @OnOpen
    public void onOpen(Session session) {
        // Save the session
        this.session = session;
        // Start the timer to send noop messages every 30 seconds
        noopTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                sendMessage(NOOP);
            }
        }, 0, 1000 * 30);
    }

    @OnClose
    public void onClose(Session session, CloseReason reason) {
        // Set the session to null
        this.session = null;
        // Print the close reason for debug purpose
        System.out.println("Websocket closed with reason: " + reason.getReasonPhrase());
        // Cancel the timer
        noopTimer.cancel();
    }

    @OnMessage
    public void onMessage(String message) {
        for (final Consumer<String> handler: this.messageHandlers)
        {
            handler.accept(message);
        }
    }

    public void addMessageHandler(Consumer<String> msgHandler)
    {
        this.messageHandlers.add(msgHandler);
    }

    public void removeMessageHandler(Consumer<String> msgHandler)
    {
        this.messageHandlers.remove(msgHandler);
    }

    public void sendMessage(String message) {
        // If the current session is not null and open, send the message
        if (session != null && session.isOpen()) {
            try {
                this.session.getAsyncRemote().sendText(message);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public void stop() throws Exception {
        // Cancel the timer
        noopTimer.cancel();
        // If the current session is not null and open, close it with the reason "Fine exit"
        if (session!=null && session.isOpen()){
            session.close(new CloseReason(CloseReason.CloseCodes.NORMAL_CLOSURE,"Fine exit"));
        }
    }

    public boolean hasMessageHandlers() {
        return messageHandlers.isEmpty();
    }
}