package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateMemberDto;
import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.rest.GameMemberApiService;
import de.uniks.pioneers.rest.MessageApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class NewGameLobbyService {

    private final GameMemberApiService gameMemberApiService;
    private final MessageApiService messageApiService;

    @Inject
    public NewGameLobbyService(GameMemberApiService gameMemberApiService, MessageApiService messageApiService) {
        this.gameMemberApiService = gameMemberApiService;
        this.messageApiService = messageApiService;
    }

    public Observable<List<Member>> getAll(String id){
        return gameMemberApiService.getAll(id);
    }

    public Observable<Member> postMember(String id, boolean ready, String password){
        return gameMemberApiService.createMember(id, new CreateMemberDto(ready, password));
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
}
