package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateMemberDto;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.rest.GameMemberApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.List;

public class NewGameLobbyService {

    private final GameMemberApiService gameMemberApiService;

    @Inject
    public NewGameLobbyService(GameMemberApiService gameMemberApiService) {
        this.gameMemberApiService = gameMemberApiService;
    }

    public Observable<List<Member>> getAll(String id){
        return gameMemberApiService.getAll(id);
    }

    public Observable<Member> postMember(String id, String name, boolean ready){
        return gameMemberApiService.createMember(id, new CreateMemberDto(ready, name));
    }

    public Observable<Member> deleteMember(String id, String userId){
        return gameMemberApiService.deleteMember(id,userId);
    }
}
