package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.rest.GameApiService;
import de.uniks.pioneers.rest.MessageApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class GameService {

    private final GameApiService gameApiService;
    private final MessageApiService messageApiService;

    @Inject
    public GameService(GameApiService gameApiService, MessageApiService messageApiService) {
        this.gameApiService = gameApiService;
        this.messageApiService = messageApiService;
    }

    public Observable<Game> deleteGame(String gameId) {
        return gameApiService.delete(gameId);
    }

    public Observable<MessageDto> sendMessage(String id, CreateMessageDto dto) {
        return messageApiService.sendMessage("games", id, dto);
    }

    public Observable<List<MessageDto>> getMessages(String id){
        return messageApiService.getChatMessages("games", id);
    }

}
