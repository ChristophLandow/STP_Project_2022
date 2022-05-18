package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateMessageDto;
import de.uniks.pioneers.dto.MessageDto;
import de.uniks.pioneers.dto.UpdateMessageDto;
import de.uniks.pioneers.rest.MessageApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MessageServiceTest {

    @Mock
    MessageApiService messageApiService;
    @InjectMocks
    MessageService messageService;

    @Test
    void sendMessageToGroup() {
        when(messageApiService.sendMessage(anyString(), anyString(), any())).thenReturn(Observable.just(new MessageDto("123", "1234", "testGroupId", "me", "This is a test message!")));
        final String result = messageService.sendMessageToGroup("testGroup", new CreateMessageDto("This is a test message!")).blockingFirst()._id();
        assertEquals("testGroupId", result);
        verify(messageApiService).sendMessage("groups", "testGroup", new CreateMessageDto("This is a test message!"));
    }

    @Test
    void getChatMessages() {
        List<MessageDto> messageList = new ArrayList<>();
        messageList.add(new MessageDto("yesterday", "now", "1", "me", "First message"));
        messageList.add(new MessageDto("yesterday", "now", "2", "you", "Second message"));
        when(messageApiService.getChatMessages(anyString(), anyString())).thenReturn(Observable.just(messageList));
        final String result = messageService.getChatMessages("test").blockingFirst().get(1).body();
        assertEquals("Second message", result);
        verify(messageApiService).getChatMessages("groups", "test");
    }

    @Test
    void updateMessage() {
        UpdateMessageDto msgUpdate = new UpdateMessageDto("Message updated");
        when(messageApiService.updateMessage(anyString(), anyString(), anyString(), any())).thenReturn(Observable.just(new MessageDto("yesterday", "now", "1", "me", "Message updated")));
        final String result = messageService.updateMessage("groups", "testGroupId", "1", msgUpdate).blockingFirst().body();
        assertEquals("Message updated", result);
        verify(messageApiService).updateMessage("groups", "testGroupId", "1", msgUpdate);
    }

}