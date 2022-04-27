package de.uniks.pioneers;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import dagger.Module;
import dagger.Provides;
import de.uniks.pioneers.rest.UserApiService;
import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.converter.jackson.JacksonConverterFactory;

import javax.inject.Singleton;

import static de.uniks.pioneers.Constants.API_V1_PREFIX;
import static de.uniks.pioneers.Constants.BASE_URL;

@Module
public class MainModule {

    @Provides
    @Singleton
    static ObjectMapper mapper(){
        return new ObjectMapper()
                .enable(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
                .disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES)
                .setSerializationInclusion(JsonInclude.Include.NON_ABSENT);
    }

    @Provides
    @Singleton
    static Retrofit retrofit (ObjectMapper mapper){
        return new Retrofit.Builder()
                .baseUrl(BASE_URL+API_V1_PREFIX+"/")
                .addConverterFactory(JacksonConverterFactory.create(mapper))
                //addCallAdapterFactory(RxJava3CallAdapterFactory.creaeAsync())
                .build();

    }

    @Provides
    static UserApiService userApiService(Retrofit retrofit){
        return retrofit.create(UserApiService.class);
    }




}
