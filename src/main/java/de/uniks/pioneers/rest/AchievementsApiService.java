package de.uniks.pioneers.rest;

import de.uniks.pioneers.Constants;
import de.uniks.pioneers.dto.CreateAchievementDto;
import de.uniks.pioneers.dto.UpdateAchievementDto;
import de.uniks.pioneers.model.Achievement;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.http.*;

import java.util.List;

public interface AchievementsApiService {
    @GET(Constants.API_PREFIX + "/users/{userId}/achievements")
    Observable<List<Achievement>> getUserAchievements(@Path("userId") String userId);

    @PUT(Constants.API_PREFIX + "/users/{userId}/achievements/{id}")
    Observable<Achievement> addAchievement(@Path("userId") String userId, @Path("id") String id, @Body CreateAchievementDto dto);

    @PATCH(Constants.API_PREFIX + "/users/{userId}/achievements/{id}")
    Observable<Achievement> updateAchievement(@Path("userId") String userId, @Path("id") String id, @Body UpdateAchievementDto dto);
}
