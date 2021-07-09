package com.example.trainerguide;

import com.example.trainerguide.models.Trainer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface PaginationInterface {
    @GET("/Trainer.json")
    Call<String> STRING_CALL(
            @Query("orderBy") String orderBy,
            @Query("startAt") String startAt,
            @Query("limitToFirst") int limitToFirst
    );

    @GET("/Trainer.json")
    Call<String> STRING_CALL_SearchTrainer(
            @Query("orderBy") String orderBy,
            @Query("startAt") String startAt,
            @Query("endAt") String endAt,
            @Query("limitToFirst") int limitToFirst
    );

    @GET(".json")
    Call<String> STRING_CALL_Trainees(
            @Query("orderBy") String orderBy,
            @Query("startAt") String startAt,
            @Query("limitToFirst") int limitToFirst
    );
}
