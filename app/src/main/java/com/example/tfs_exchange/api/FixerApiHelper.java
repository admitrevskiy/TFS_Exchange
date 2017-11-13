package com.example.tfs_exchange.api;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by pusya on 06.11.17.
 * Помощник для работы с api - создаем клиент OkHttp, Retrofit и создаем FixerApi
 */

public class FixerApiHelper {

    public FixerApi api;
    public ApiResponse rate ;

    //Регитрируем в GSON свой десериализатор
    private Gson gson = new GsonBuilder().registerTypeAdapter(ApiResponse.class, new ApiResponseDeserializer()).create();

    public FixerApi createApi() {

        //Клиент подключения
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(30, TimeUnit.SECONDS)
                .writeTimeout(30, TimeUnit.SECONDS)
                .connectTimeout(30, TimeUnit.SECONDS)
                .build();

        //Retrofit с подключенным клиентом, передается baseUrl, в качестве конвертера добавляем gson с десериализатором, адаптер RxJava
        Retrofit retrofit = new Retrofit.Builder()
                .client(client)
                .baseUrl("http://api.fixer.io/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .build();

        //Возвращаем FixerApi
        return api = retrofit.create(FixerApi.class);
    }
}
