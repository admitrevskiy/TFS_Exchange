package com.example.tfs_exchange.api;

import com.example.tfs_exchange.api.ApiResponse;

import io.reactivex.Single;
import retrofit2.http.GET;
import retrofit2.http.Query;

/**
 * Created by pusya on 06.11.17.
 */

public interface FixerApi {

    //Запрос с latest; Single от RxJava
    @GET("latest")
    Single<ApiResponse> latest(
            @Query("base") String from,
            @Query("symbols") String to
    );
}