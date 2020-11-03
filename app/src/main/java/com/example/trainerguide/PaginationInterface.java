package com.example.trainerguide;

import com.example.trainerguide.models.Trainer;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PaginationInterface {
    @GET("/Trainer.json")
    Call<Trainer> STRING_CALL(
            @Query("page") int page,
            @Query("limit") int limit
    );


}
