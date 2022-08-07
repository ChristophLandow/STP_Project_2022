package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.CreateAchievementDto;
import de.uniks.pioneers.dto.UpdateAchievementDto;
import de.uniks.pioneers.model.Achievement;
import de.uniks.pioneers.rest.AchievementsApiService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;
import static de.uniks.pioneers.GameConstants.*;

@Singleton
public class AchievementService {
    private final EventListener eventListener;
    private final UserService userService;
    private final AchievementsApiService achievementsApiService;

    private final ObservableMap<String, Achievement> achievements = FXCollections.observableHashMap();
    private final CompositeDisposable disposable = new CompositeDisposable();

    @Inject
    public AchievementService(EventListener eventListener, UserService userService, AchievementsApiService achievementsApiService) {
        this.eventListener = eventListener;
        this.userService = userService;
        this.achievementsApiService = achievementsApiService;

        initAchievementListener();
    }

    private void initAchievementListener(){
        disposable.add(achievementsApiService.getUserAchievements(userService.getCurrentUser()._id()).observeOn(FX_SCHEDULER)
                .subscribe(serverAchievements-> {
                    serverAchievements.forEach(achievement -> this.achievements.put(achievement.id(), achievement));
                    addAchievements();
                }));

        disposable.add(eventListener.listen("users." + userService.getCurrentUser()._id() + ".achievements.*.*", Achievement.class)
                .observeOn(FX_SCHEDULER)
                .subscribe(event -> {
                    final Achievement achievement = event.data();
                    if(event.event().endsWith(".created") || event.event().endsWith(".updated")){
                        this.achievements.put(achievement.id(), achievement);
                    }
                }));
    }

    public ObservableMap<String, Achievement> getAchievements(){
        return this.achievements;
    }

    private void addAchievements(){
        addAchievement(WINNER_ACHIEVEMENT);
        addAchievement(CITY_ACHIEVEMENT);
        addAchievement(HARBOR_ACHIEVEMENT);
        addAchievement(ROAD_ACHIEVEMENT);
        addAchievement(SETTLEMENT_ACHIEVEMENT);
    }

    private void addAchievement(String id){
        if(achievements.get(id) == null) {
            disposable.add(this.achievementsApiService.addAchievement(userService.getCurrentUser()._id(), id, new CreateAchievementDto(null, 0))
                    .observeOn(FX_SCHEDULER).subscribe());
        }
    }

    private void updateAchievement(String id, String unlockedAt, int progress){
        disposable.add(this.achievementsApiService.updateAchievement(userService.getCurrentUser()._id(), id, new UpdateAchievementDto(unlockedAt, progress))
                .observeOn(FX_SCHEDULER).subscribe());
    }

    public void incrementProgress(String id){
        updateAchievement(id, this.achievements.get(id).unlockedAt(), this.achievements.get(id).progress() + 1);
    }

    public void unlockAchievement(String id){
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
        formatter.setTimeZone(TimeZone.getTimeZone("UTC"));
        String dateString = formatter.format(new Date());

        updateAchievement(id, dateString, this.achievements.get(id).progress());
    }

    public void stop(){
        disposable.dispose();
    }
}
