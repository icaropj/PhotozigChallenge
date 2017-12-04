package icaro.com.br.photozigchallenge.retrofit;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import icaro.com.br.photozigchallenge.base.BaseService;
import icaro.com.br.photozigchallenge.util.Constants;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by icaro on 02/12/2017.
 */

public class RetrofitSingleton {

    private static RetrofitSingleton instance = null;
    private Retrofit retrofit;
    private BaseService baseService;

    private RetrofitSingleton() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient client = new OkHttpClient.Builder()
                .addInterceptor(loggingInterceptor)
                .build();

        Gson gson = new GsonBuilder().create();

        retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl(Constants.getBaseUrl())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        baseService = retrofit.create(BaseService.class);
    }

    public static RetrofitSingleton getInstance() {
        if(instance == null) {
            instance = new RetrofitSingleton();
        }
        return instance;
    }

    public Retrofit getRetrofit() {
        return retrofit;
    }

    public BaseService getBaseService() {
        return baseService;
    }


}
