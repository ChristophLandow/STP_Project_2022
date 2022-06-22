package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateMemberDto;
import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.dto.UpdateMemberDto;
import de.uniks.pioneers.model.Member;
import de.uniks.pioneers.rest.GameApiService;
import de.uniks.pioneers.rest.GameMemberApiService;
import de.uniks.pioneers.rest.MessageApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class NewGameLobbyServiceTest {
    @Mock
    GameMemberApiService gameMemberApiService;

    @Mock
    GameApiService gameApiService;

    @Mock
    MessageApiService messageApiService;

    @InjectMocks
    NewGameLobbyService newGameLobbyService;

    @Test
    void getAll() {
        List<Member> gameMembers = new ArrayList<>();
        gameMembers.add(new Member("now", "now", "1", "u1", true, "#ff0000",false));
        gameMembers.add(new Member("now", "now", "1", "u2", false, "#ff0000",false));
        when(gameMemberApiService.getAll(anyString())).thenReturn(Observable.just(gameMembers));

        final String result = newGameLobbyService.getAll("1").blockingFirst().get(1).userId();
        assertEquals("u2", result);
        verify(gameMemberApiService).getAll("1");
    }

    @Test
    void postMember() {
        Member member = new Member("now", "now", "1", "u1", true, "#ff0000",false);
        when(gameMemberApiService.createMember(anyString(), any())).thenReturn(Observable.just(member));

        final String result = newGameLobbyService.postMember("1", true, "#ff0000", "password").blockingFirst().userId();
        assertEquals("u1", result);
        verify(gameMemberApiService).createMember("1", new CreateMemberDto(true, "#ff0000","password"));
    }

    @Test
    void sendMessage() {
        when(messageApiService.sendMessage(anyString(), anyString(), any())).thenReturn(Observable.just(new MessageDto("now", "now", "1", "me", "Hello Group!")));
        final String result = newGameLobbyService.sendMessage("1", new CreateMessageDto("Hello Group!")).blockingFirst()._id();
        assertEquals("1", result);
        verify(messageApiService).sendMessage("games", "1", new CreateMessageDto("Hello Group!"));
    }

    @Test
    void getMessages() {
        List<MessageDto> messageList = new ArrayList<>();
        messageList.add(new MessageDto("now", "now", "1", "me", "Hello"));
        messageList.add(new MessageDto("yesterday", "now", "2", "you", "World!"));
        when(messageApiService.getChatMessages(anyString(), anyString())).thenReturn(Observable.just(messageList));
        final String result = newGameLobbyService.getMessages("1").blockingFirst().get(1).body();
        assertEquals("World!", result);
        verify(messageApiService).getChatMessages("games", "1");
    }

    @Test
    void setReady() {
        when(gameMemberApiService.patchMember(anyString(), anyString(), any())).thenReturn(Observable.just(new Member("now", "now", "1", "u1", true, "#ff0000",false)));

        final boolean result = newGameLobbyService.patchMember("1", "u1", true, "#ff0000",false).blockingFirst().ready();
        assertTrue(result);
        verify(gameMemberApiService).patchMember("1", "u1", new UpdateMemberDto(true, "#ff0000",false));
    }
}