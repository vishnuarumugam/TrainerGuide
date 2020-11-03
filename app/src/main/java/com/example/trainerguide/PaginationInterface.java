package com.example.trainerguide;

import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;

public interface PaginationInterface {
    @GET("/Trainer.json")
    Call<String> STRING_CALL(
            @Query("page") int page,
            @Query("limit") int limit
    );


}
