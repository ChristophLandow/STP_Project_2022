package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.NewGameScreenLobbyController;
import de.uniks.pioneers.dto.CreateMemberDto;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.rest.GameMemberApiService;
import io.reactivex.rxjava3.core.Observable;
import javafx.beans.property.SimpleStringProperty;

import javax.inject.Inject;
import java.util.List;

public class NewGameLobbyService {

    private final GameMemberApiService gameMemberApiService;
    private final NewGameScreenLobbyController newGameScreenLobbyController;
    private SimpleStringProperty gameId = new SimpleStringProperty();

    @Inject
    public NewGameLobbyService(GameMemberApiService gameMemberApiService, NewGameScreenLobbyController newGameScreenLobbyController) {
        this.gameMemberApiService = gameMemberApiService;
        this.newGameScreenLobbyController = newGameScreenLobbyController;
    }

    public Observable<List<Member>> getAll(String id){
        return gameMemberApiService.getMember(id);
    }

    public Observable<Member> createMember(String id, new CreateMemberDto()){
        return gameMemberApiService.createMember()
    }
}
