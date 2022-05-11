package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateGroupDto;
import de.uniks.pioneers.dto.GroupDto;
import de.uniks.pioneers.rest.GroupApiService;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

public class GroupService {

    private final GroupApiService groupApiService;
    private final UserService userService;

    @Inject
    public GroupService(GroupApiService groupApiService, UserService userService) {
        this.groupApiService = groupApiService;
        this.userService = userService;
    }

    public Observable<GroupDto> createNewGroupWithOtherUser(String otherUserId) {
        List<String> memberList = new ArrayList<>();
        memberList.add(userService.getCurrentUserId());
        memberList.add(otherUserId);
        System.out.println(memberList);
        return this.groupApiService.newGroup(new CreateGroupDto(memberList, otherUserId));
    }

    public Observable<List<GroupDto>> getGroupsWithUser(String otherUserId) {
        String users = userService.getCurrentUserId() + "," + otherUserId;
        return this.groupApiService.getGroupsWithUsers(users);
    }

}