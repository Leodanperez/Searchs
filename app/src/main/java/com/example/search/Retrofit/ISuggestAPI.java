package com.example.search.Retrofit;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface ISuggestAPI {
    @GET("complete/search")
    Observable<String> getSuggestFromYoutube(@Query("q") String query,
                                             @Query("client") String client,
                                             @Query("h1") String laguage,
                                             @Query("ds") String restrict);

    @GET("complete/search")
    Observable<String> getSuggestFromGoogle(@Query("q") String query,
                                             @Query("client") String client,
                                             @Query("h1") String laguage);
}
