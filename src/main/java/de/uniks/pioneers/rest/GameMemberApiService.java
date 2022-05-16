package de.uniks.pioneers.rest;


import de.uniks.pioneers.dto.CreateMemberDto;
import de.uniks.pioneers.model.Member;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface GameMemberApiService {
    @GET("games/{gameId}/members")
    Observable<List<Member>> getAll(@Path("gameId") String gameId);

    @POST("games/{gameId}/members")
    Observable<Member> createMember(@Path("gameId") String gameId, @Body CreateMemberDto dto);

    @DELETE("/api/v1/games/{gameId}/members/{userId}")
    Observable<Member> deleteMember(@Path("gameId") String gameId, @Path("userId") String userId);
}
