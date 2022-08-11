package de.uniks.pioneers.services;

import de.uniks.pioneers.dto.RefreshDto;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.rest.AuthApiService;
import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.Observable;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.util.Timer;
import java.util.TimerTask;

import static de.uniks.pioneers.Constants.FX_SCHEDULER;

@Singleton
public class RefreshService {

    private final AuthApiService authApiService;
    private final TokenStorage tokenStorage;
    private final Timer timer = new Timer();

    @Inject
    public RefreshService(TokenStorage tokenStorage, AuthApiService authApiService) {
        this.tokenStorage = tokenStorage;
        this.authApiService = authApiService;
    }

    public void startRefreshCycle() {
        TimerTask myTask = new TimerTask() {
            @Override
            public void run() {

                sendRefresh()
                        .observeOn(FX_SCHEDULER)
                        .doOnError(e -> System.out.println("An error has occurred during session validation."))
                        .subscribe();

            }
        };


        this.timer.schedule(myTask, 0, 28 * 60 * 1000);
    }

    public void stopRefreshCycle(){
        this.timer.cancel();
    }

    public Observable<LoginResult> sendRefresh(){
        return authApiService.refresh(new RefreshDto(tokenStorage.getRefreshToken()))
                .doOnNext(result -> {
                    tokenStorage.setAccessToken(result.accessToken());
                    tokenStorage.setRefreshToken(result.refreshToken());
                });
    }



}
