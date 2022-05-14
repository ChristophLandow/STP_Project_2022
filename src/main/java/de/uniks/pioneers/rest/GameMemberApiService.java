package de.uniks.pioneers.rest;


import de.uniks.pioneers.dto.CreateMemberDto;
import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.model.Member;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.ObservableList;
import retrofit2.http.*;

import java.util.List;

public interface GameMemberApiService {
    @GET("games/{gameId}/members")
    Observable<List<Member>> getAll(@Path("gameId") String gameId);

    @POST("games/{gameId}/members")
    Observable<Member> createMember(@Path("gameId") String gameId, @Body CreateMemberDto dto);

    @DELETE("/api/v1/games/{gameId}/members/{userId}")
    Observable<Member> deleteMemmber(@Path("gameId") String gameId, @Path("userId") String userId);
}
