package de.uniks.pioneers;

import dagger.Module;
import dagger.Provides;
import de.uniks.pioneers.dto.Event;
import de.uniks.pioneers.dto.LoginDto;
import de.uniks.pioneers.dto.RefreshDto;
import de.uniks.pioneers.model.LoginResult;
import de.uniks.pioneers.model.LogoutResult;
import de.uniks.pioneers.rest.AuthApiService;
import de.uniks.pioneers.ws.EventListener;
import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;

@Module
public class TestModule {

    @Provides
    static EventListener eventListener(){

        return new EventListener(null,null){

            @Override
            public <T> Observable<Event<T>> listen(String pattern, Class<T> type) {

                return Observable.empty();
            }

            private void send(Object message){}
        };
    }

    @Provides
    static AuthApiService authApiService(){

        return new AuthApiService() {
            @Override
            public Observable<LoginResult> login(LoginDto dto) {
                return null;
            }

            @Override
            public Observable<LoginResult> refresh(RefreshDto dto) {
                return null;
            }

            @Override
            public Call<LoginResult> checkPassword(LoginDto dto) {
                return null;
            }

            @Override
            public Observable<LogoutResult> logout() {
                return null;
            }
        };
    }
}
