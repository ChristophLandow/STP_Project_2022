package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateGroupDto;
import de.uniks.pioneers.dto.GroupDto;
import de.uniks.pioneers.rest.GroupApiService;
import io.reactivex.rxjava3.core.Observable;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GroupServiceTest {

    @Mock
    GroupApiService groupApiService;

    @Mock
    UserService userService;

    @InjectMocks
    GroupService groupService;

    @Test
    void createNewGroupWithOtherUser() {
        ArrayList<String> members = new ArrayList<>();
        members.add("12345678");
        members.add("87654321");
        when(userService.getCurrentUserId()).thenReturn("12345678");
        when(groupApiService.newGroup(any())).thenReturn(Observable.just(new GroupDto("right now", "right now", "12345678", members, "87654321")));

        final String result = groupService.createNewGroupWithOtherUser("87654321").blockingFirst().name();
        assertEquals("87654321", result);

        verify(groupApiService).newGroup(new CreateGroupDto(members, "87654321"));
        verify(userService).getCurrentUserId();
    }

    @Test
    void getGroupsWithOtherUser() {
        ArrayList<String> members = new ArrayList<>();
        members.add("12345678");
        members.add("87654321");
        when(userService.getCurrentUserId()).thenReturn("12345678");
        List<GroupDto> testGroupList = new ArrayList<>();
        testGroupList.add(new GroupDto("right now", "right now", "12345678", members, "87654321"));
        when(groupApiService.getGroupsWithUsers(anyString())).thenReturn(Observable.just(testGroupList));

        final GroupDto result = groupService.getGroupsWithUser("87654321").blockingFirst().get(0);
        assertEquals(result.name(), "87654321");

        verify(groupApiService).getGroupsWithUsers("12345678,87654321");
        verify(userService).getCurrentUserId();

    }

}