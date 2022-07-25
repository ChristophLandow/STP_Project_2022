package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateGroupDto;
import de.uniks.pioneers.dto.GroupDto;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;

import java.util.List;

public interface GroupApiService {

    @POST("groups")
    Observable<GroupDto> newGroup(@Body CreateGroupDto dto);

    @GET("groups/")
    Observable<List<GroupDto>> getGroupsWithUsers(@Query("members") String users);

}