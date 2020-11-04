package com.example.trainerguide;

import com.example.trainerguide.models.Trainer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PaginationInterface {
    @GET("/Trainer.json")
    Call<String> STRING_CALL(
            @Query("orderBy") String orderBy,
            @Query("startAt") String startAt,
            @Query("limitToFirst") int limitToFirst
    );


}
