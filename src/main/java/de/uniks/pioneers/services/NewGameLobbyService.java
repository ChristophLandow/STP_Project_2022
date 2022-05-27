package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.*;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.rest.GameApiService;
import de.uniks.pioneers.rest.GameMemberApiService;
import de.uniks.pioneers.rest.MessageApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.List;

@Singleton
public class NewGameLobbyService {

    private final GameApiService gameApiService;
    private final GameMemberApiService gameMemberApiService;
    private final MessageApiService messageApiService;
    private String currentMemberId;

    @Inject
    public NewGameLobbyService(GameApiService gameApiService, GameMemberApiService gameMemberApiService,
                               MessageApiService messageApiService) {
        this.gameApiService = gameApiService;
        this.gameMemberApiService = gameMemberApiService;
        this.messageApiService = messageApiService;
    }

    public Observable<List<Member>> getAll(String id){
        return gameMemberApiService.getAll(id);
    }

    public Observable<Member> postMember(String id, boolean ready, String color, String password){
        return gameMemberApiService.createMember(id, new CreateMemberDto(ready, color, password));
    }

    public Observable<Member> deleteMember(String id, String userId){
        return gameMemberApiService.deleteMember(id,userId);
    }

    public Observable<MessageDto> sendMessage(String id, CreateMessageDto dto) {
        return messageApiService.sendMessage("games", id, dto);
    }

    public Observable<List<MessageDto>> getMessages(String id){
        return messageApiService.getChatMessages("games", id);
    }

    public Observable<Member> patchMember(String groupId, String userId, boolean ready, String color) {
        return gameMemberApiService.patchMember(groupId, userId, new UpdateMemberDto(ready, color));
    }

    public void setCurrentMemberId(String id) {
        currentMemberId = id;
    }

    public String getCurrentMemberId() {
        return currentMemberId;
    }

    public Observable<Game> updateGame(Game game, String password, Boolean started) {
        return gameApiService.update(game._id(), new UpdateGameDto(game.name(), game.owner(), started, password));
    }
}
