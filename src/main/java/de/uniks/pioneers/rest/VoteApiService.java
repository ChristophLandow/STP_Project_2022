package de.uniks.pioneers.rest;

import de.uniks.pioneers.dto.CreateVoteDto;
import de.uniks.pioneers.model.Vote;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

public interface VoteApiService {

    @POST("maps/{mapId}/votes")
    Observable<Vote> createVote(@Body CreateVoteDto dto);

    @GET("maps/{mapId}/votes")
    Observable<Vote> getVotesOfMap(@Path("mapId") String id);

    @GET("maps/{mapId}/votes/{userId}")
    Observable<Vote> getVotes(@Path("mapId") String mapId,  @Path("userId") String userId);

    @DELETE("maps/{mapId}/votes/{userId}")
    Observable<Vote> deleteVotes(@Path("mapId") String mapId,  @Path("userId") String userId);
}
