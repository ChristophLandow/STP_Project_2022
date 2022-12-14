package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.Vote;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;


public interface VoteApiService {

    @POST("maps/{mapId}/votes")
    Observable<Vote> createVote(@Path("mapId") String id, @Body CreateVoteDto voteDto);

    @GET("maps/{mapId}/votes")
    Observable<Vote> getVotesOfMap(@Path("mapId") String id);

    @GET("maps/{mapId}/votes/{userId}")
    Observable<Vote> getVotesOfUser(@Path("mapId") String mapId,  @Path("userId") String userId);

    @DELETE("maps/{mapId}/votes/{userId}")
    Observable<Vote> deleteVotesOfUser(@Path("mapId") String mapId,  @Path("userId") String userId);
}
