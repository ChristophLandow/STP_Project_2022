package de.uniks.pioneers.rest;


import de.uniks.pioneers.dto.CreateMemberDto;
import de.uniks.pioneers.dto.CreateUserDto;
import de.uniks.pioneers.model.Member;
import io.reactivex.rxjava3.core.Observable;
import javafx.collections.ObservableList;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

import java.util.List;

public interface GameMemberApiService {
    @GET("games/{gameId}/members")
    Observable<List<Member>> getMember(@Path("gameId") String gameId);

    @POST("games/{gameId}/members")
    Observable<Member> createMember(@Path("gameId") String gameId, @Body CreateMemberDto dto);
}
