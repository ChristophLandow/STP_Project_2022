package de.uniks.pioneers.services;

import de.uniks.pioneers.controller.NewGameScreenLobbyController;
import de.uniks.pioneers.dto.*;
import de.uniks.pioneers.model.Game;
import de.uniks.pioneers.model.LogoutResult;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.model.User;
import de.uniks.pioneers.rest.AuthApiService;
import de.uniks.pioneers.rest.GameApiService;
import de.uniks.pioneers.rest.GameMemberApiService;
import de.uniks.pioneers.rest.MessageApiService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@Singleton
public class NewGameLobbyService {
    private ObservableList<Member> members = FXCollections.observableArrayList();
    private Map<String, User> users = new HashMap<>();
    private final GameApiService gameApiService;
    private final GameMemberApiService gameMemberApiService;
    private final MessageApiService messageApiService;
    private final AuthApiService authApiService;
    private final EventListener eventListener;
    private final UserService userService;
    private final CompositeDisposable disposable = new CompositeDisposable();
    private NewGameScreenLobbyController newGameScreenLobbyController;

    @Inject
    public NewGameLobbyService(GameApiService gameApiService, GameMemberApiService gameMemberApiService,
                               MessageApiService messageApiService, AuthApiService authApiService, EventListener eventListener, UserService userService) {
        this.gameApiService = gameApiService;
        this.gameMemberApiService = gameMemberApiService;
        this.messageApiService = messageApiService;
        this.authApiService = authApiService;
        this.eventListener = eventListener;
        this.userService = userService;
    }

    public void setNewGameScreenLobbyController(NewGameScreenLobbyController newGameScreenLobbyController) {
        this.newGameScreenLobbyController = newGameScreenLobbyController;
        members = FXCollections.observableArrayList();
        users = new HashMap<>();
        initMemberListener();
        initGameListener();
    }

    public void leaveLobby() {
        disposable.dispose();
    }

    public void initUserListener(User user) {
        String patternToObserveGameUsers = String.format("users.%s.updated", user._id());
        disposable.add(eventListener.listen(patternToObserveGameUsers, User.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(userEvent -> {
                    User userFromEvent = userEvent.data();
                    if(userFromEvent.status().equals("offline") && newGameScreenLobbyController.getGame().owner().equals(userService.getCurrentUser())) {
                        disposable.add(this.deleteMember(newGameScreenLobbyController.getGame()._id(), userFromEvent._id())
                                .observeOn(FX_SCHEDULER)
                                .subscribe(member -> newGameScreenLobbyController.deleteUser(member), Throwable::printStackTrace));
                    }
                })
        );
    }

    private void initMemberListener() {
        String patternToObserveGameMembers = String.format("games.%s.members.*.*", newGameScreenLobbyController.getGame()._id());
        disposable.add(eventListener.listen(patternToObserveGameMembers, Member.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(memberEvent -> {
                    final Member member = memberEvent.data();
                    if (memberEvent.event().endsWith(".created")) {
                        members.add(member);
                    } else if (memberEvent.event().endsWith(".updated")) {
                        members.replaceAll(m -> m.userId().equals(member.userId()) ? member : m);
                        newGameScreenLobbyController.setReadyColor(member.userId(), member.ready(), member.color(), member.spectator());
                    } else if (memberEvent.event().endsWith(".deleted")) {
                        members.remove(member);
                    }
                }));
    }

    private void initGameListener() {
        if(newGameScreenLobbyController != null) {
            String patternToObserveGame = String.format("games.%s.*", newGameScreenLobbyController.getGame()._id());
            disposable.add(eventListener.listen(patternToObserveGame, Game.class)
                    .observeOn(FX_SCHEDULER)
                    .subscribe(gameEvent -> {
                        newGameScreenLobbyController.setGame(gameEvent.data());
                        newGameScreenLobbyController.setMemberCount(newGameScreenLobbyController.getGame().members());
                        if (gameEvent.event().endsWith(".updated") && gameEvent.data().started()) {
                            newGameScreenLobbyController.toIngame(newGameScreenLobbyController.getGame(), this.users.values().stream().toList(), newGameScreenLobbyController.getColorPickerController().getColor());
                        } else if (gameEvent.event().endsWith(".deleted")) {
                            newGameScreenLobbyController.getApp().show(newGameScreenLobbyController.getLobbyScreenController());
                        }
                    })
            );
        }
    }

    public Map<String, User> getUsers() {
        return users;
    }

    public ObservableList<Member> getMembers() {
        return members;
    }

    public Observable<LogoutResult> logout() {
        return authApiService.logout();
    }

    public Observable<List<Member>> getAll(String id){
        return gameMemberApiService.getAll(id);
    }

    public Observable<Member> postMember(String id, boolean ready, String color, String password) {
        return gameMemberApiService.createMember(id, new CreateMemberDto(ready, color, password));
    }

    public Observable<Member> deleteMember(String id, String userId) {
        return gameMemberApiService.deleteMember(id,userId);
    }

    public Observable<MessageDto> sendMessage(String id, CreateMessageDto dto) {
        return messageApiService.sendMessage("games", id, dto);
    }

    public Observable<List<MessageDto>> getMessages(String id) {
        return messageApiService.getChatMessages("games", id);
    }

    public Observable<Member> patchMember(String groupId, String userId, boolean ready, String color, boolean spectator) {
        return gameMemberApiService.patchMember(groupId, userId, new UpdateMemberDto(ready, color, spectator));
    }

    public Observable<Game> updateGame(Game game, String password, boolean started) {
        return gameApiService.update(game._id(), new UpdateGameDto(game.name(), game.owner(), started, password));
    }
}
